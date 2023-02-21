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
import android.media.audiofx.AudioEffect
import android.os.IBinder
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.audio.AudioCapabilities
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer
import com.google.android.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.music.AudioOnlyExtractors
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.model.Library
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.playback.persist.PersistenceRepository
import org.oxycblt.auxio.playback.replaygain.ReplayGainAudioProcessor
import org.oxycblt.auxio.playback.state.InternalPlayer
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.service.ForegroundManager
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.widgets.WidgetComponent
import org.oxycblt.auxio.widgets.WidgetProvider

/**
 * A service that manages the system-side aspects of playback, such as:
 * - The single [ExoPlayer] instance.
 * - The Media Notification
 * - Headset management
 * - Widgets
 *
 * This service is headless and does not manage the playback state. Moreover, the player instance is
 * not the source of truth for the state, but rather the means to control system-side playback. Both
 * of those tasks are what [PlaybackStateManager] is for.
 *
 * TODO: Refactor lifecycle to run completely headless (i.e no activity needed)
 *
 * TODO: Android Auto
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class PlaybackService :
    Service(),
    Player.Listener,
    InternalPlayer,
    MediaSessionComponent.Listener,
    MusicRepository.Listener {
    // Player components
    private lateinit var player: ExoPlayer
    @Inject lateinit var replayGainProcessor: ReplayGainAudioProcessor

    // System backend components
    @Inject lateinit var mediaSessionComponent: MediaSessionComponent
    @Inject lateinit var widgetComponent: WidgetComponent
    private val systemReceiver = PlaybackReceiver()

    // Shared components
    @Inject lateinit var playbackManager: PlaybackStateManager
    @Inject lateinit var playbackSettings: PlaybackSettings
    @Inject lateinit var persistenceRepository: PersistenceRepository
    @Inject lateinit var musicRepository: MusicRepository
    @Inject lateinit var musicSettings: MusicSettings

    // State
    private lateinit var foregroundManager: ForegroundManager
    private var hasPlayed = false
    private var openAudioEffectSession = false

    // Coroutines
    private val serviceJob = Job()
    private val restoreScope = CoroutineScope(serviceJob + Dispatchers.Main)
    private val saveScope = CoroutineScope(serviceJob + Dispatchers.Main)

    // --- SERVICE OVERRIDES ---

    override fun onCreate() {
        super.onCreate()

        // Define our own extractors so we can exclude non-audio parsers.
        // Ordering is derived from the DefaultExtractorsFactory's optimized ordering:
        // https://docs.google.com/document/d/1w2mKaWMxfz2Ei8-LdxqbPs1VLe_oudB-eryXXw9OvQQ.
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
                FfmpegAudioRenderer(handler, audioListener, replayGainProcessor))
        }

        player =
            ExoPlayer.Builder(this, audioRenderer)
                .setMediaSourceFactory(DefaultMediaSourceFactory(this, AudioOnlyExtractors))
                // Enable automatic WakeLock support
                .setWakeMode(C.WAKE_MODE_LOCAL)
                .setAudioAttributes(
                    // Signal that we are a music player.
                    AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .build(),
                    true)
                .build()
                .also { it.addListener(this) }
        replayGainProcessor.addToListeners(player)
        foregroundManager = ForegroundManager(this)
        // Initialize any listener-dependent components last as we wouldn't want a listener race
        // condition to cause us to load music before we were fully initialize.
        playbackManager.registerInternalPlayer(this)
        musicRepository.addListener(this)
        mediaSessionComponent.registerListener(this)
        registerReceiver(
            systemReceiver,
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
            })

        logD("Service created")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Forward system media button sent by MediaButtonReceiver to MediaSessionComponent
        if (intent.action == Intent.ACTION_MEDIA_BUTTON) {
            mediaSessionComponent.handleMediaButtonIntent(intent)
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    // TODO: Implement task removal (Have to radically alter state saving to occur at runtime)

    override fun onDestroy() {
        super.onDestroy()

        foregroundManager.release()

        // Pause just in case this destruction was unexpected.
        playbackManager.setPlaying(false)
        playbackManager.unregisterInternalPlayer(this)
        musicRepository.removeListener(this)

        unregisterReceiver(systemReceiver)
        serviceJob.cancel()

        widgetComponent.release()
        mediaSessionComponent.release()

        replayGainProcessor.releaseFromListeners(player)
        player.release()
        if (openAudioEffectSession) {
            // Make sure to close the audio session when we release the player.
            broadcastAudioEffectAction(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION)
            openAudioEffectSession = false
        }

        logD("Service destroyed")
    }

    // --- CONTROLLER OVERRIDES ---

    override val audioSessionId: Int
        get() = player.audioSessionId

    override val shouldRewindWithPrev: Boolean
        get() = playbackSettings.rewindWithPrev && player.currentPosition > REWIND_THRESHOLD

    override fun getState(durationMs: Long) =
        InternalPlayer.State.from(
            player.playWhenReady,
            player.isPlaying,
            // The position value can be below zero or past the expected duration, make
            // sure we handle that.
            player.currentPosition.coerceAtLeast(0).coerceAtMost(durationMs))

    override fun loadSong(song: Song?, play: Boolean) {
        if (song == null) {
            // No song, stop playback and foreground state.
            logD("Nothing playing, stopping playback")
            player.stop()
            if (openAudioEffectSession) {
                // Make sure to close the audio session when we stop playback.
                broadcastAudioEffectAction(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION)
                openAudioEffectSession = false
            }
            stopAndSave()
            return
        }

        logD("Loading ${song.rawName}")
        player.setMediaItem(MediaItem.fromUri(song.uri))
        player.prepare()

        if (!openAudioEffectSession) {
            // Android does not like it if you start an audio effect session without having
            // something within your player buffer. Make sure we only start one when we load
            // a song.
            broadcastAudioEffectAction(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION)
            openAudioEffectSession = true
        }

        player.playWhenReady = play
    }

    override fun seekTo(positionMs: Long) {
        logD("Seeking to ${positionMs}ms")
        player.seekTo(positionMs)
    }

    override fun setPlaying(isPlaying: Boolean) {
        player.playWhenReady = isPlaying
    }

    // --- PLAYER OVERRIDES ---

    override fun onEvents(player: Player, events: Player.Events) {
        super.onEvents(player, events)
        if (events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED) && player.playWhenReady) {
            // Mark that we have started playing so that the notification can now be posted.
            hasPlayed = true
        }

        // Any change to the analogous isPlaying, isAdvancing, or positionMs values require
        // us to synchronize with a new state.
        if (events.containsAny(
            Player.EVENT_PLAY_WHEN_READY_CHANGED,
            Player.EVENT_IS_PLAYING_CHANGED,
            Player.EVENT_POSITION_DISCONTINUITY)) {
            playbackManager.synchronizeState(this)
        }
    }

    override fun onPlaybackStateChanged(state: Int) {
        if (state == Player.STATE_ENDED) {
            // Player ended, repeat the current track if we are configured to.
            if (playbackManager.repeatMode == RepeatMode.TRACK) {
                playbackManager.rewind()
                // May be configured to pause when we repeat a track.
                if (playbackSettings.pauseOnRepeat) {
                    playbackManager.setPlaying(false)
                }
            } else {
                playbackManager.next()
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        // TODO: Replace with no skipping and a notification instead
        // If there's any issue, just go to the next song.
        playbackManager.next()
    }

    // --- MUSICSTORE OVERRIDES ---

    override fun onLibraryChanged(library: Library?) {
        if (library != null) {
            // We now have a library, see if we have anything we need to do.
            playbackManager.requestAction(this)
        }
    }

    // --- OTHER FUNCTIONS ---

    private fun broadcastAudioEffectAction(event: String) {
        sendBroadcast(
            Intent(event)
                .putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName)
                .putExtra(AudioEffect.EXTRA_AUDIO_SESSION, audioSessionId)
                .putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC))
    }

    private fun stopAndSave() {
        // This session has ended, so we need to reset this flag for when the next session starts.
        hasPlayed = false
        if (foregroundManager.tryStopForeground()) {
            // Now that we have ended the foreground state (and thus music playback), we'll need
            // to save the current state as it's not long until this service (and likely the whole
            // app) is killed.
            logD("Saving playback state")
            saveScope.launch { persistenceRepository.saveState(playbackManager.toSavedState()) }
        }
    }

    override fun performAction(action: InternalPlayer.Action): Boolean {
        val library =
            musicRepository.library
            // No library, cannot do anything.
            ?: return false

        logD("Performing action: $action")

        when (action) {
            // Restore state -> Start a new restoreState job
            is InternalPlayer.Action.RestoreState -> {
                restoreScope.launch {
                    persistenceRepository.readState(library)?.let {
                        playbackManager.applySavedState(it, false)
                    }
                }
            }
            // Shuffle all -> Start new playback from all songs
            is InternalPlayer.Action.ShuffleAll -> {
                playbackManager.play(null, null, musicSettings.songSort.songs(library.songs), true)
            }
            // Open -> Try to find the Song for the given file and then play it from all songs
            is InternalPlayer.Action.Open -> {
                library.findSongForUri(application, action.uri)?.let { song ->
                    playbackManager.play(
                        song,
                        null,
                        musicSettings.songSort.songs(library.songs),
                        playbackManager.queue.isShuffled && playbackSettings.keepShuffle)
                }
            }
        }

        return true
    }

    // --- MEDIASESSIONCOMPONENT OVERRIDES ---

    override fun onPostNotification(notification: NotificationComponent) {
        // Do not post the notification if playback hasn't started yet. This prevents errors
        // where changing a setting would cause the notification to appear in an unfriendly
        // manner.
        if (hasPlayed) {
            logD("Updating notification")
            if (!foregroundManager.tryStartForeground(notification)) {
                notification.post()
            }
        }
    }

    /**
     * A [BroadcastReceiver] for receiving playback-specific [Intent]s from the system that require
     * an active [IntentFilter] to be registered.
     */
    private inner class PlaybackReceiver : BroadcastReceiver() {
        private var initialHeadsetPlugEventHandled = false

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                // --- SYSTEM EVENTS ---

                // Android has three different ways of handling audio plug events for some reason:
                // 1. ACTION_HEADSET_PLUG, which only works with wired headsets
                // 2. ACTION_ACL_CONNECTED, which allows headset autoplay but also requires
                // granting the BLUETOOTH/BLUETOOTH_CONNECT permissions, which is more or less
                // a non-starter since both require me to display a permission prompt
                // 3. Some internal framework thing that also handles bluetooth headsets
                // Just use ACTION_HEADSET_PLUG.
                AudioManager.ACTION_HEADSET_PLUG -> {
                    when (intent.getIntExtra("state", -1)) {
                        0 -> pauseFromHeadsetPlug()
                        1 -> playFromHeadsetPlug()
                    }

                    initialHeadsetPlugEventHandled = true
                }
                AudioManager.ACTION_AUDIO_BECOMING_NOISY -> pauseFromHeadsetPlug()

                // --- AUXIO EVENTS ---
                ACTION_PLAY_PAUSE ->
                    playbackManager.setPlaying(!playbackManager.playerState.isPlaying)
                ACTION_INC_REPEAT_MODE ->
                    playbackManager.repeatMode = playbackManager.repeatMode.increment()
                ACTION_INVERT_SHUFFLE -> playbackManager.reorder(!playbackManager.queue.isShuffled)
                ACTION_SKIP_PREV -> playbackManager.prev()
                ACTION_SKIP_NEXT -> playbackManager.next()
                ACTION_EXIT -> {
                    playbackManager.setPlaying(false)
                    stopAndSave()
                }
                WidgetProvider.ACTION_WIDGET_UPDATE -> widgetComponent.update()
            }
        }

        private fun playFromHeadsetPlug() {
            // ACTION_HEADSET_PLUG will fire when this BroadcastReciever is initially attached,
            // which would result in unexpected playback. Work around it by dropping the first
            // call to this function, which should come from that Intent.
            if (playbackSettings.headsetAutoplay &&
                playbackManager.queue.currentSong != null &&
                initialHeadsetPlugEventHandled) {
                logD("Device connected, resuming")
                playbackManager.setPlaying(true)
            }
        }

        private fun pauseFromHeadsetPlug() {
            if (playbackManager.queue.currentSong != null) {
                logD("Device disconnected, pausing")
                playbackManager.setPlaying(false)
            }
        }
    }

    companion object {
        const val ACTION_INC_REPEAT_MODE = BuildConfig.APPLICATION_ID + ".action.LOOP"
        const val ACTION_INVERT_SHUFFLE = BuildConfig.APPLICATION_ID + ".action.SHUFFLE"
        const val ACTION_SKIP_PREV = BuildConfig.APPLICATION_ID + ".action.PREV"
        const val ACTION_PLAY_PAUSE = BuildConfig.APPLICATION_ID + ".action.PLAY_PAUSE"
        const val ACTION_SKIP_NEXT = BuildConfig.APPLICATION_ID + ".action.NEXT"
        const val ACTION_EXIT = BuildConfig.APPLICATION_ID + ".action.EXIT"
        private const val REWIND_THRESHOLD = 3000L
    }
}
