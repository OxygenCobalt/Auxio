/*
 * Copyright (c) 2021 Auxio Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package org.oxycblt.auxio.playback.system

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.IBinder
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.TracksInfo
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.audio.AudioCapabilities
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer
import com.google.android.exoplayer2.ext.flac.LibflacAudioRenderer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.widgets.WidgetController
import org.oxycblt.auxio.widgets.WidgetProvider

/**
 * A service that manages the system-side aspects of playback, such as:
 * - The single [ExoPlayer] instance.
 * - The Media Notification
 * - Headset management
 * - Widgets
 *
 * This service relies on [PlaybackStateManager.Callback] and [SettingsManager.Callback], so
 * therefore there's no need to bind to it to deliver commands.
 * @author OxygenCobalt
 *
 * TODO: Synchronize components in a less awful way (Fix issue where rapid-fire updates results
 * in a desynced notification)
 */
class PlaybackService :
    Service(), Player.Listener, PlaybackStateManager.Callback, SettingsManager.Callback {
    // Player components
    private lateinit var player: ExoPlayer
    private val replayGainProcessor = ReplayGainAudioProcessor()

    // System backend components
    private lateinit var notificationComponent: NotificationComponent
    private lateinit var mediaSessionComponent: MediaSessionComponent
    private lateinit var widgets: WidgetController
    private val systemReceiver = PlaybackReceiver()

    // Managers
    private val playbackManager = PlaybackStateManager.getInstance()
    private val settingsManager = SettingsManager.getInstance()

    // State
    private var isForeground = false
    private var hasPlayed = false

    // Coroutines
    private val serviceJob = Job()
    private val positionScope = CoroutineScope(serviceJob + Dispatchers.Main)
    private val saveScope = CoroutineScope(serviceJob + Dispatchers.Main)

    // --- SERVICE OVERRIDES ---

    override fun onCreate() {
        super.onCreate()

        // --- PLAYER SETUP ---

        player = newPlayer()
        player.addListener(this@PlaybackService)

        positionScope.launch {
            while (true) {
                playbackManager.synchronizePosition(player.currentPosition)
                delay(POS_POLL_INTERVAL)
            }
        }

        // --- SYSTEM SETUP ---

        widgets = WidgetController(this)
        mediaSessionComponent = MediaSessionComponent(this, player)
        notificationComponent = NotificationComponent(this, mediaSessionComponent.token)

        // Then the notification/headset callbacks
        IntentFilter().apply {
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            addAction(AudioManager.ACTION_HEADSET_PLUG)

            addAction(ACTION_INC_REPEAT_MODE)
            addAction(ACTION_INVERT_SHUFFLE)
            addAction(ACTION_SKIP_PREV)
            addAction(ACTION_PLAY_PAUSE)
            addAction(ACTION_SKIP_NEXT)
            addAction(ACTION_EXIT)
            addAction(WidgetProvider.ACTION_WIDGET_UPDATE)

            registerReceiver(systemReceiver, this)
        }

        // --- PLAYBACKSTATEMANAGER SETUP ---

        playbackManager.addCallback(this)
        if (playbackManager.isInitialized) {
            restore()
        }

        // --- SETTINGSMANAGER SETUP ---

        settingsManager.addCallback(this)

        logD("Service created")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == Intent.ACTION_MEDIA_BUTTON) {
            // Workaround to get GadgetBridge and other apps that blindly query for
            // ACTION_MEDIA_BUTTON working.
            mediaSessionComponent.handleMediaButtonIntent(intent)
        }

        return START_NOT_STICKY
    }

    // No binding, service is headless
    // Communicate using PlaybackStateManager, SettingsManager, or Broadcasts instead.
    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()

        stopForeground(true)
        isForeground = false

        unregisterReceiver(systemReceiver)

        serviceJob.cancel()
        mediaSessionComponent.release()
        widgets.release()
        player.release()

        playbackManager.removeCallback(this)
        settingsManager.removeCallback(this)

        // Pause just in case this destruction was unexpected.
        playbackManager.isPlaying = false

        logD("Service destroyed")
    }

    // --- PLAYER EVENT LISTENER OVERRIDES ---

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)

        if (playWhenReady) {
            hasPlayed = true
        }

        if (playbackManager.isPlaying != playWhenReady) {
            playbackManager.isPlaying = playWhenReady
        }
    }

    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_ENDED -> {
                if (playbackManager.repeatMode == RepeatMode.TRACK) {
                    playbackManager.repeat()
                } else {
                    playbackManager.next()
                }
            }
            else -> {}
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        // If there's any issue, just go to the next song.
        playbackManager.next()
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        playbackManager.synchronizePosition(player.currentPosition)
    }

    override fun onTracksInfoChanged(tracksInfo: TracksInfo) {
        super.onTracksInfoChanged(tracksInfo)

        for (info in tracksInfo.trackGroupInfos) {
            if (info.isSelected) {
                for (i in 0 until info.trackGroup.length) {
                    if (info.isTrackSelected(i)) {
                        replayGainProcessor.applyReplayGain(info.trackGroup.getFormat(i).metadata)
                        break
                    }
                }

                break
            }
        }
    }

    // --- PLAYBACK STATE CALLBACK OVERRIDES ---

    override fun onIndexMoved(index: Int) {
        onSongChanged(playbackManager.song)
    }

    override fun onNewPlayback(index: Int, queue: List<Song>, parent: MusicParent?) {
        onSongChanged(playbackManager.song)
    }

    private fun onSongChanged(song: Song?) {
        if (song == null) {
            // Clear if there's nothing to play.
            logD("Nothing playing, stopping playback")
            player.stop()
            stopAndSave()
            return
        }

        logD("Loading ${song.rawName}")
        player.setMediaItem(MediaItem.fromUri(song.uri))
        player.prepare()
        notificationComponent.updateMetadata(
            song, playbackManager.parent, ::startForegroundOrNotify)
    }

    override fun onPlayingChanged(isPlaying: Boolean) {
        player.playWhenReady = isPlaying
        notificationComponent.updatePlaying(isPlaying)
        startForegroundOrNotify()
    }

    override fun onRepeatChanged(repeatMode: RepeatMode) {
        if (!settingsManager.useAltNotifAction) {
            notificationComponent.updateRepeatMode(repeatMode)
            startForegroundOrNotify()
        }
    }

    override fun onShuffledChanged(isShuffled: Boolean) {
        if (settingsManager.useAltNotifAction) {
            notificationComponent.updateShuffled(isShuffled)
            startForegroundOrNotify()
        }
    }

    override fun onSeek(positionMs: Long) {
        player.seekTo(positionMs)
    }

    // --- SETTINGSMANAGER OVERRIDES ---

    override fun onNotifActionUpdate(useAltAction: Boolean) {
        if (useAltAction) {
            notificationComponent.updateShuffled(playbackManager.isShuffled)
        } else {
            notificationComponent.updateRepeatMode(playbackManager.repeatMode)
        }

        startForegroundOrNotify()
    }

    override fun onShowCoverUpdate(showCovers: Boolean) {
        playbackManager.song?.let { song ->
            notificationComponent.updateMetadata(
                song, playbackManager.parent, ::startForegroundOrNotify)
        }
    }

    override fun onQualityCoverUpdate(doQualityCovers: Boolean) {
        playbackManager.song?.let { song ->
            notificationComponent.updateMetadata(
                song, playbackManager.parent, ::startForegroundOrNotify)
        }
    }

    override fun onReplayGainUpdate(mode: ReplayGainMode) {
        onTracksInfoChanged(player.currentTracksInfo)
    }

    // --- OTHER FUNCTIONS ---

    /** Create the [ExoPlayer] instance. */
    private fun newPlayer(): ExoPlayer {
        // Since Auxio is a music player, only specify an audio renderer to save
        // battery/apk size/cache size
        val audioRenderer = RenderersFactory { handler, _, audioListener, _, _ ->
            arrayOf(
                MediaCodecAudioRenderer(
                    this,
                    MediaCodecSelector.DEFAULT,
                    handler,
                    audioListener,
                    AudioCapabilities.DEFAULT_AUDIO_CAPABILITIES,
                    replayGainProcessor),
                LibflacAudioRenderer(handler, audioListener, replayGainProcessor))
        }

        // Enable constant bitrate seeking so that certain MP3s/AACs are seekable
        val extractorsFactory = DefaultExtractorsFactory().setConstantBitrateSeekingEnabled(true)

        return ExoPlayer.Builder(this, audioRenderer)
            .setMediaSourceFactory(DefaultMediaSourceFactory(this, extractorsFactory))
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.CONTENT_TYPE_MUSIC)
                    .build(),
                true)
            .build()
    }

    /** Fully restore the notification and playback state */
    private fun restore() {
        logD("Restoring the service state")

        onSongChanged(playbackManager.song)
        onSeek(playbackManager.positionMs)
        onPlayingChanged(playbackManager.isPlaying)
        onShuffledChanged(playbackManager.isShuffled)
        onRepeatChanged(playbackManager.repeatMode)

        // Notify other classes that rely on this service to also update.
        widgets.update()
    }

    /**
     * Bring the service into the foreground and show the notification, or refresh the notification.
     */
    private fun startForegroundOrNotify() {
        if (hasPlayed && playbackManager.song != null) {
            logD("Starting foreground/notifying")

            if (!isForeground) {
                startForeground(IntegerTable.NOTIFICATION_CODE, notificationComponent.build())
                isForeground = true
            } else {
                // If we are already in foreground just update the notification
                notificationComponent.renotify()
            }
        }
    }

    /** Stop the foreground state and hide the notification */
    private fun stopAndSave() {
        stopForeground(true)
        isForeground = false
        saveScope.launch { playbackManager.saveState(this@PlaybackService) }
    }

    /** A [BroadcastReceiver] for receiving general playback events from the system. */
    private inner class PlaybackReceiver : BroadcastReceiver() {
        private var initialHeadsetPlugEventHandled = false

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                // --- SYSTEM EVENTS ---

                // Android has four different ways of handling audio plug events for some reason:
                // 1. ACTION_HEADSET_PLUG, which only works with wired headsets
                // 2. ACTION_SCO_AUDIO_STATE_UPDATED, which only works with pausing from a plug
                // event and I'm not even sure if it's needed
                // 3. ACTION_ACL_CONNECTED, which allows headset autoplay but also requires
                // granting the BLUETOOTH/BLUETOOTH_CONNECT permissions, which is more or less
                // a non-starter since both require me to display a permission prompt
                // 4. Some weird internal framework thing that also handles bluetooth headsets???
                //
                // They should have just stopped at ACTION_HEADSET_PLUG.
                AudioManager.ACTION_HEADSET_PLUG -> {
                    when (intent.getIntExtra("state", -1)) {
                        0 -> pauseFromPlug()
                        1 -> maybeResumeFromPlug()
                    }

                    initialHeadsetPlugEventHandled = true
                }
                AudioManager.ACTION_AUDIO_BECOMING_NOISY -> pauseFromPlug()

                // --- AUXIO EVENTS ---
                ACTION_PLAY_PAUSE -> playbackManager.isPlaying = !playbackManager.isPlaying
                ACTION_INC_REPEAT_MODE ->
                    playbackManager.repeatMode = playbackManager.repeatMode.increment()
                ACTION_INVERT_SHUFFLE -> playbackManager.reshuffle(!playbackManager.isShuffled)
                ACTION_SKIP_PREV -> playbackManager.prev()
                ACTION_SKIP_NEXT -> playbackManager.next()
                ACTION_EXIT -> {
                    playbackManager.isPlaying = false
                    stopAndSave()
                }
                WidgetProvider.ACTION_WIDGET_UPDATE -> widgets.update()
            }
        }

        /**
         * Resume from a headset plug event in the case that the quirk is enabled. This
         * functionality remains a quirk for two reasons:
         * 1. Automatically resuming more or less overrides all other audio streams, which is not
         * that friendly
         * 2. There is a bug where playback will always start when this service starts, mostly due
         * to AudioManager.ACTION_HEADSET_PLUG always firing on startup. This is fixed, but I fear
         * that it may not work on OEM skins that for whatever reason don't make this action fire.
         */
        private fun maybeResumeFromPlug() {
            if (playbackManager.song != null &&
                settingsManager.headsetAutoplay &&
                initialHeadsetPlugEventHandled) {
                logD("Device connected, resuming")
                playbackManager.isPlaying = true
            }
        }

        /** Pause from a headset plug. */
        private fun pauseFromPlug() {
            if (playbackManager.song != null) {
                logD("Device disconnected, pausing")
                playbackManager.isPlaying = false
            }
        }
    }

    companion object {
        private const val POS_POLL_INTERVAL = 1000L

        const val ACTION_INC_REPEAT_MODE = BuildConfig.APPLICATION_ID + ".action.LOOP"
        const val ACTION_INVERT_SHUFFLE = BuildConfig.APPLICATION_ID + ".action.SHUFFLE"
        const val ACTION_SKIP_PREV = BuildConfig.APPLICATION_ID + ".action.PREV"
        const val ACTION_PLAY_PAUSE = BuildConfig.APPLICATION_ID + ".action.PLAY_PAUSE"
        const val ACTION_SKIP_NEXT = BuildConfig.APPLICATION_ID + ".action.NEXT"
        const val ACTION_EXIT = BuildConfig.APPLICATION_ID + ".action.EXIT"
    }
}
