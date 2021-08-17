/*
 * Copyright (c) 2021 Auxio Project
 * PlaybackService.kt is part of Auxio.
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

import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.getSystemServiceSafe
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.Parent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toURI
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.widgets.WidgetController
import org.oxycblt.auxio.widgets.WidgetProvider

/**
 * A service that manages the system-side aspects of playback, such as:
 * - The single [SimpleExoPlayer] instance.
 * - The Media Notification
 * - Headset management
 * - Widgets
 *
 * This service relies on [PlaybackStateManager.Callback] and [SettingsManager.Callback],
 * so therefore there's no need to bind to it to deliver commands.
 * @author OxygenCobalt
 */
class PlaybackService : Service(), Player.Listener, PlaybackStateManager.Callback, SettingsManager.Callback {

    // Player components
    private lateinit var player: SimpleExoPlayer
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var connector: PlaybackSessionConnector

    // Notification components
    private lateinit var notification: PlaybackNotification
    private lateinit var notificationManager: NotificationManager

    // System backend components
    private lateinit var audioReactor: AudioReactor
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var widgets: WidgetController
    private val systemReceiver = SystemEventReceiver()

    // Managers
    private val playbackManager = PlaybackStateManager.getInstance()
    private val settingsManager = SettingsManager.getInstance()

    // State
    private var isForeground = false

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(serviceJob + Dispatchers.Main)

    // --- SERVICE OVERRIDES ---

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logD("Service is active.")

        return START_NOT_STICKY
    }

    // No binding, service is headless
    // Communicate using PlaybackStateManager, SettingsManager, or Broadcasts instead.
    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        // --- PLAYER SETUP ---

        player = newPlayer()
        player.addListener(this@PlaybackService)
        player.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build(),
            false
        )

        // --- SYSTEM SETUP ---

        audioReactor = AudioReactor(this, player)
        wakeLock = getSystemServiceSafe(PowerManager::class).newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK, this::class.simpleName
        )

        widgets = WidgetController(this)

        // Set up the media button callbacks
        mediaSession = MediaSessionCompat(this, packageName).apply {
            isActive = true
        }

        connector = PlaybackSessionConnector(this, player, mediaSession)

        // Then the notif/headset callbacks
        IntentFilter().apply {
            addAction(ACTION_LOOP)
            addAction(ACTION_SHUFFLE)
            addAction(ACTION_SKIP_PREV)
            addAction(ACTION_PLAY_PAUSE)
            addAction(ACTION_SKIP_NEXT)
            addAction(ACTION_EXIT)

            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            addAction(Intent.ACTION_HEADSET_PLUG)

            addAction(WidgetProvider.ACTION_WIDGET_UPDATE)

            registerReceiver(systemReceiver, this)
        }

        // --- NOTIFICATION SETUP ---

        notificationManager = getSystemServiceSafe(NotificationManager::class)
        notification = PlaybackNotification.from(this, notificationManager, mediaSession)

        // --- PLAYBACKSTATEMANAGER SETUP ---

        playbackManager.setHasPlayed(playbackManager.isPlaying)
        playbackManager.addCallback(this)

        if (playbackManager.song != null || playbackManager.isRestored) {
            restore()
        }

        // --- SETTINGSMANAGER SETUP ---

        settingsManager.addCallback(this)

        logD("Service created.")
    }

    override fun onDestroy() {
        super.onDestroy()

        stopForegroundAndNotification()
        unregisterReceiver(systemReceiver)

        player.release()
        connector.release()
        mediaSession.release()
        audioReactor.release()
        widgets.release()
        releaseWakelock()

        playbackManager.removeCallback(this)
        settingsManager.removeCallback(this)

        // Pause just in case this destruction was unexpected.
        playbackManager.setPlaying(false)

        // The service coroutines last job is to save the state to the DB, before terminating itself
        serviceScope.launch {
            playbackManager.saveStateToDatabase(this@PlaybackService)
            serviceJob.cancel()
        }

        logD("Service destroyed.")
    }

    // --- PLAYER EVENT LISTENER OVERRIDES ---

    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_READY -> {
                startPollingPosition()
                releaseWakelock()
            }

            Player.STATE_ENDED -> {
                if (playbackManager.loopMode == LoopMode.TRACK) {
                    playbackManager.rewind()

                    if (settingsManager.pauseOnLoop) {
                        playbackManager.setPlaying(false)
                    }
                } else {
                    playbackManager.next()
                }
            }

            else -> {}
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        // We use the wakelock to ensure that the CPU is active while music is being loaded
        acquireWakeLock()
    }

    override fun onPlayerError(error: PlaybackException) {
        // If there's any issue, just go to the next song.
        playbackManager.next()
    }

    override fun onPositionDiscontinuity(reason: Int) {
        if (reason == Player.DISCONTINUITY_REASON_SEEK) {
            playbackManager.setPosition(player.currentPosition)
        }
    }

    // --- PLAYBACK STATE CALLBACK OVERRIDES ---

    override fun onSongUpdate(song: Song?) {
        if (song != null) {
            player.setMediaItem(MediaItem.fromUri(song.id.toURI()))
            player.prepare()

            notification.setMetadata(song, ::startForegroundOrNotify)

            return
        }

        // Clear if there's nothing to play.
        player.stop()
        stopForegroundAndNotification()
    }

    override fun onParentUpdate(parent: Parent?) {
        notification.setParent(parent)

        startForegroundOrNotify()
    }

    override fun onPlayingUpdate(isPlaying: Boolean) {
        if (isPlaying && !player.isPlaying) {
            player.play()
            audioReactor.requestFocus()
            startPollingPosition()
        } else {
            player.pause()
        }

        notification.setPlaying(isPlaying)
        startForegroundOrNotify()
    }

    override fun onLoopUpdate(loopMode: LoopMode) {
        if (!settingsManager.useAltNotifAction) {
            notification.setLoop(loopMode)

            startForegroundOrNotify()
        }
    }

    override fun onShuffleUpdate(isShuffling: Boolean) {
        if (settingsManager.useAltNotifAction) {
            notification.setShuffle(isShuffling)

            startForegroundOrNotify()
        }
    }

    override fun onSeek(position: Long) {
        player.seekTo(position)
    }

    // --- SETTINGSMANAGER OVERRIDES ---

    override fun onColorizeNotifUpdate(doColorize: Boolean) {
        playbackManager.song?.let { song ->
            notification.setMetadata(song, ::startForegroundOrNotify)
        }
    }

    override fun onNotifActionUpdate(useAltAction: Boolean) {
        if (useAltAction) {
            notification.setShuffle(playbackManager.isShuffling)
        } else {
            notification.setLoop(playbackManager.loopMode)
        }

        startForegroundOrNotify()
    }

    override fun onShowCoverUpdate(showCovers: Boolean) {
        playbackManager.song?.let { song ->
            connector.onSongUpdate(song)

            notification.setMetadata(song, ::startForegroundOrNotify)
        }
    }

    override fun onQualityCoverUpdate(doQualityCovers: Boolean) {
        playbackManager.song?.let { song ->
            notification.setMetadata(song, ::startForegroundOrNotify)
        }
    }

    // --- OTHER FUNCTIONS ---

    /**
     * Create the [SimpleExoPlayer] instance.
     */
    private fun newPlayer(): SimpleExoPlayer {
        // Since Auxio is a music player, only specify an audio renderer to save
        // battery/apk size/cache size
        val audioRenderer = RenderersFactory { handler, _, audioListener, _, _ ->
            arrayOf(
                MediaCodecAudioRenderer(this, MediaCodecSelector.DEFAULT, handler, audioListener)
            )
        }

        // Enable constant bitrate seeking so that certain MP3s/AACs are seekable
        val extractorsFactory = DefaultExtractorsFactory().setConstantBitrateSeekingEnabled(true)

        return SimpleExoPlayer.Builder(this, audioRenderer)
            .setMediaSourceFactory(DefaultMediaSourceFactory(this, extractorsFactory))
            .build()
    }

    /**
     * Fully restore the notification and playback state
     */
    private fun restore() {
        logD("Restoring the service state")

        // Re-call existing callbacks with the current values to restore everything
        onParentUpdate(playbackManager.parent)
        onPlayingUpdate(playbackManager.isPlaying)
        onShuffleUpdate(playbackManager.isShuffling)
        onLoopUpdate(playbackManager.loopMode)
        onSongUpdate(playbackManager.song)
        onSeek(playbackManager.position)

        // Notify other classes that rely on this service to also update.
        widgets.update()
    }

    /**
     * Start polling the position on a coroutine.
     */
    private fun startPollingPosition() {
        val pollFlow = flow {
            while (true) {
                emit(player.currentPosition)
                delay(POS_POLL_INTERVAL)
            }
        }.conflate()

        serviceScope.launch {
            pollFlow.takeWhile { player.isPlaying }.collect { pos ->
                playbackManager.setPosition(pos)
            }
        }
    }

    /**
     * Bring the service into the foreground and show the notification, or refresh the notification.
     */
    private fun startForegroundOrNotify() {
        if (playbackManager.hasPlayed && playbackManager.song != null) {
            logD("Starting foreground/notifying")

            if (!isForeground) {
                // Specify that this is a media service, if supported.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(
                        PlaybackNotification.NOTIFICATION_ID, notification.build(),
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                    )
                } else {
                    startForeground(PlaybackNotification.NOTIFICATION_ID, notification.build())
                }
            } else {
                // If we are already in foreground just update the notification
                notificationManager.notify(
                    PlaybackNotification.NOTIFICATION_ID, notification.build()
                )
            }
        }
    }

    /**
     * Stop the foreground state and hide the notification
     */
    private fun stopForegroundAndNotification() {
        stopForeground(true)
        notificationManager.cancel(PlaybackNotification.NOTIFICATION_ID)

        isForeground = false
    }

    /**
     * Hold the wakelock for the default amount of time [25 Seconds]
     */
    private fun acquireWakeLock() {
        wakeLock.acquire(WAKELOCK_TIME)

        logD("Wakelock is held.")
    }

    /**
     * Release the wakelock if its currently being held.
     */
    private fun releaseWakelock() {
        if (wakeLock.isHeld) {
            wakeLock.release()

            logD("Wakelock is released.")
        }
    }

    /**
     * A [BroadcastReceiver] for receiving system events from notifications, widgets, or
     * headset plug events.
     */
    private inner class SystemEventReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {

                // --- NOTIFICATION CASES ---

                ACTION_PLAY_PAUSE -> playbackManager.setPlaying(
                    !playbackManager.isPlaying
                )

                ACTION_LOOP -> playbackManager.setLoopMode(
                    playbackManager.loopMode.increment()
                )

                ACTION_SHUFFLE -> playbackManager.setShuffling(
                    !playbackManager.isShuffling, keepSong = true
                )

                ACTION_SKIP_PREV -> playbackManager.prev()
                ACTION_SKIP_NEXT -> playbackManager.next()

                ACTION_EXIT -> {
                    playbackManager.setPlaying(false)
                    stopForegroundAndNotification()
                }

                // --- HEADSET CASES ---

                BluetoothDevice.ACTION_ACL_CONNECTED -> resumeFromPlug()
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> pauseFromPlug()

                AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED -> {
                    when (intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1)) {
                        AudioManager.SCO_AUDIO_STATE_CONNECTED -> resumeFromPlug()
                        AudioManager.SCO_AUDIO_STATE_DISCONNECTED -> pauseFromPlug()
                    }
                }

                AudioManager.ACTION_AUDIO_BECOMING_NOISY -> pauseFromPlug()

                Intent.ACTION_HEADSET_PLUG -> {
                    when (intent.getIntExtra("state", -1)) {
                        CONNECTED -> resumeFromPlug()
                        DISCONNECTED -> pauseFromPlug()
                    }
                }

                WidgetProvider.ACTION_WIDGET_UPDATE -> widgets.update()
            }
        }

        /**
         * Resume from a headset plug event, as long as its allowed.
         */
        private fun resumeFromPlug() {
            if (playbackManager.song != null && settingsManager.doPlugMgt) {
                logD("Device connected, resuming...")

                playbackManager.setPlaying(true)
            }
        }

        /**
         * Pause from a headset plug, as long as its allowed.
         */
        private fun pauseFromPlug() {
            if (playbackManager.song != null && settingsManager.doPlugMgt) {
                logD("Device disconnected, pausing...")

                playbackManager.setPlaying(false)
            }
        }
    }

    companion object {
        private const val DISCONNECTED = 0
        private const val CONNECTED = 1
        private const val WAKELOCK_TIME = 25000L
        private const val POS_POLL_INTERVAL = 500L

        const val ACTION_LOOP = BuildConfig.APPLICATION_ID + ".action.LOOP"
        const val ACTION_SHUFFLE = BuildConfig.APPLICATION_ID + ".action.SHUFFLE"
        const val ACTION_SKIP_PREV = BuildConfig.APPLICATION_ID + ".action.PREV"
        const val ACTION_PLAY_PAUSE = BuildConfig.APPLICATION_ID + ".action.PLAY_PAUSE"
        const val ACTION_SKIP_NEXT = BuildConfig.APPLICATION_ID + ".action.NEXT"
        const val ACTION_EXIT = BuildConfig.APPLICATION_ID + ".action.EXIT"
    }
}
