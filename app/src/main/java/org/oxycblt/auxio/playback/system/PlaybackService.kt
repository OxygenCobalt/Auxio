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

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.audiofx.AudioEffect
import android.os.IBinder
import androidx.core.content.ContextCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.decoder.ffmpeg.FfmpegAudioRenderer
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.audio.AudioCapabilities
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.MediaSource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.list.ListSettings
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.playback.persist.PersistenceRepository
import org.oxycblt.auxio.playback.replaygain.ReplayGainAudioProcessor
import org.oxycblt.auxio.playback.state.DeferredPlayback
import org.oxycblt.auxio.playback.state.PlaybackStateHolder
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.Progression
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.playback.state.StateEvent
import org.oxycblt.auxio.service.ForegroundManager
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE
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
 * @author Alexander Capehart (OxygenCobalt)
 *
 * TODO: Refactor lifecycle to run completely headless (i.e no activity needed)
 * TODO: Android Auto
 */
@AndroidEntryPoint
class PlaybackService :
    Service(),
    Player.Listener,
    PlaybackStateHolder,
    MediaSessionComponent.Listener,
    MusicRepository.UpdateListener {
    // Player components
    private lateinit var player: ExoPlayer
    @Inject lateinit var mediaSourceFactory: MediaSource.Factory
    @Inject lateinit var replayGainProcessor: ReplayGainAudioProcessor

    // System backend components
    @Inject lateinit var mediaSessionComponent: MediaSessionComponent
    @Inject lateinit var widgetComponent: WidgetComponent
    private val systemReceiver = PlaybackReceiver()

    // Shared components
    @Inject lateinit var playbackManager: PlaybackStateManager
    @Inject lateinit var playbackSettings: PlaybackSettings
    @Inject lateinit var persistenceRepository: PersistenceRepository
    @Inject lateinit var listSettings: ListSettings
    @Inject lateinit var musicRepository: MusicRepository

    // State
    private lateinit var foregroundManager: ForegroundManager
    private var hasPlayed = false
    private var openAudioEffectSession = false

    // Coroutines
    private val serviceJob = Job()
    private val restoreScope = CoroutineScope(serviceJob + Dispatchers.IO)
    private val saveScope = CoroutineScope(serviceJob + Dispatchers.IO)

    // --- SERVICE OVERRIDES ---

    override fun onCreate() {
        super.onCreate()

        // Since Auxio is a music player, only specify an audio renderer to save
        // battery/apk size/cache size
        val audioRenderer = RenderersFactory { handler, _, audioListener, _, _ ->
            arrayOf(
                FfmpegAudioRenderer(handler, audioListener, replayGainProcessor),
                MediaCodecAudioRenderer(
                    this,
                    MediaCodecSelector.DEFAULT,
                    handler,
                    audioListener,
                    AudioCapabilities.DEFAULT_AUDIO_CAPABILITIES,
                    replayGainProcessor))
        }

        player =
            ExoPlayer.Builder(this, audioRenderer)
                .setMediaSourceFactory(mediaSourceFactory)
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
        foregroundManager = ForegroundManager(this)
        // Initialize any listener-dependent components last as we wouldn't want a listener race
        // condition to cause us to load music before we were fully initialize.
        playbackManager.registerStateHolder(this)
        musicRepository.addUpdateListener(this)
        mediaSessionComponent.registerListener(this)

        val intentFilter =
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
            }

        ContextCompat.registerReceiver(
            this, systemReceiver, intentFilter, ContextCompat.RECEIVER_EXPORTED)

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
        playbackManager.playing(false)
        playbackManager.unregisterStateHolder(this)
        musicRepository.removeUpdateListener(this)

        unregisterReceiver(systemReceiver)
        serviceJob.cancel()

        widgetComponent.release()
        mediaSessionComponent.release()

        replayGainProcessor.release()
        player.release()
        if (openAudioEffectSession) {
            // Make sure to close the audio session when we release the player.
            broadcastAudioEffectAction(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION)
            openAudioEffectSession = false
        }

        logD("Service destroyed")
    }

    // --- PLAYBACKSTATEHOLDER OVERRIDES ---

    override val repeatMode
        get() = player.repeat

    override val progression: Progression
        get() =
            player.song?.let {
                Progression.from(
                    player.playWhenReady,
                    player.isPlaying,
                    // The position value can be below zero or past the expected duration, make
                    // sure we handle that.
                    player.currentPosition.coerceAtLeast(0).coerceAtMost(it.durationMs))
            }
                ?: Progression.nil()

    override var parent: MusicParent? = null

    override val isShuffled
        get() = player.shuffleModeEnabled

    override fun resolveIndex() = player.resolveIndex()

    override fun resolveQueue() = player.resolveQueue()

    override val audioSessionId: Int
        get() = player.audioSessionId

    override fun newPlayback(
        queue: List<Song>,
        start: Song?,
        parent: MusicParent?,
        shuffled: Boolean,
        play: Boolean
    ) {
        this.parent = parent
        if (shuffled) {
            player.shuffledQueue(queue, start)
        } else {
            player.orderedQueue(queue, start)
        }
        player.prepare()
        player.playWhenReady = play
        playbackManager.dispatchEvent(this, StateEvent.NewPlayback)
    }

    override fun playing(playing: Boolean) {
        player.playWhenReady = playing
        // Dispatched later once all of the changes have been accumulated
    }

    override fun repeatMode(repeatMode: RepeatMode) {
        player.repeatMode =
            when (repeatMode) {
                RepeatMode.NONE -> Player.REPEAT_MODE_OFF
                RepeatMode.ALL -> Player.REPEAT_MODE_ALL
                RepeatMode.TRACK -> Player.REPEAT_MODE_ONE
            }
        playbackManager.dispatchEvent(this, StateEvent.RepeatModeChanged)
    }

    override fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }

    override fun next() {
        player.seekToNext()
        playbackManager.dispatchEvent(this, StateEvent.IndexMoved)
        player.play()
    }

    override fun prev() {
        player.seekToPrevious()
        playbackManager.dispatchEvent(this, StateEvent.IndexMoved)
        player.play()
    }

    override fun goto(index: Int) {
        player.goto(index)
        playbackManager.dispatchEvent(this, StateEvent.IndexMoved)
        player.play()
    }

    override fun reorder(shuffled: Boolean) {
        player.reorder(shuffled)
        playbackManager.dispatchEvent(this, StateEvent.QueueReordered)
    }

    override fun addToQueue(songs: List<Song>) {
        val insertAt = playbackManager.index + 1
        player.addToQueue(songs)
        playbackManager.dispatchEvent(
            this, StateEvent.QueueChanged(UpdateInstructions.Add(insertAt, songs.size), false))
    }

    override fun playNext(songs: List<Song>) {
        val insertAt = playbackManager.index + 1
        player.playNext(songs)
        playbackManager.dispatchEvent(
            this, StateEvent.QueueChanged(UpdateInstructions.Add(insertAt, songs.size), false))
    }

    override fun move(from: Int, to: Int) {
        player.move(from, to)
        playbackManager.dispatchEvent(
            this, StateEvent.QueueChanged(UpdateInstructions.Move(from, to), false))
    }

    override fun remove(at: Int) {
        val oldIndex = player.currentMediaItemIndex
        player.remove(at)
        val newIndex = player.currentMediaItemIndex
        playbackManager.dispatchEvent(
            this, StateEvent.QueueChanged(UpdateInstructions.Remove(at, 1), oldIndex != newIndex))
    }

    // --- PLAYER OVERRIDES ---

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)

        if (player.playWhenReady) {
            // Mark that we have started playing so that the notification can now be posted.
            hasPlayed = true
            logD("Player has started playing")
            if (!openAudioEffectSession) {
                // Convention to start an audioeffect session on play/pause rather than
                // start/stop
                logD("Opening audio effect session")
                broadcastAudioEffectAction(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION)
                openAudioEffectSession = true
            }
        } else if (openAudioEffectSession) {
            // Make sure to close the audio session when we stop playback.
            logD("Closing audio effect session")
            broadcastAudioEffectAction(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION)
            openAudioEffectSession = false
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)

        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO ||
            reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK) {
            playbackManager.dispatchEvent(this, StateEvent.IndexMoved)
        }
    }

    override fun onEvents(player: Player, events: Player.Events) {
        super.onEvents(player, events)

        if (events.containsAny(
            Player.EVENT_PLAY_WHEN_READY_CHANGED,
            Player.EVENT_IS_PLAYING_CHANGED,
            Player.EVENT_POSITION_DISCONTINUITY)) {
            logD("Player state changed, must synchronize state")
            playbackManager.dispatchEvent(this, StateEvent.ProgressionChanged)
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        // TODO: Replace with no skipping and a notification instead
        // If there's any issue, just go to the next song.
        logE("Player error occured")
        logE(error.stackTraceToString())
        playbackManager.next()
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        if (changes.deviceLibrary && musicRepository.deviceLibrary != null) {
            // We now have a library, see if we have anything we need to do.
            logD("Library obtained, requesting action")
            playbackManager.requestAction(this)
        }
    }

    // --- OTHER FUNCTIONS ---

    private fun broadcastAudioEffectAction(event: String) {
        logD("Broadcasting AudioEffect event: $event")
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

    override fun handleDeferred(action: DeferredPlayback): Boolean {
        val deviceLibrary =
            musicRepository.deviceLibrary
            // No library, cannot do anything.
            ?: return false

        when (action) {
            // Restore state -> Start a new restoreState job
            is DeferredPlayback.RestoreState -> {
                logD("Restoring playback state")
                restoreScope.launch {
                    persistenceRepository.readState()?.let {
                        // Apply the saved state on the main thread to prevent code expecting
                        // state updates on the main thread from crashing.
                        withContext(Dispatchers.Main) { playbackManager.applySavedState(it, false) }
                    }
                }
            }
            // Shuffle all -> Start new playback from all songs
            is DeferredPlayback.ShuffleAll -> {
                logD("Shuffling all tracks")
                playbackManager.play(
                    null, null, listSettings.songSort.songs(deviceLibrary.songs), true)
            }
            // Open -> Try to find the Song for the given file and then play it from all songs
            is DeferredPlayback.Open -> {
                logD("Opening specified file")
                deviceLibrary.findSongForUri(application, action.uri)?.let { song ->
                    playbackManager.play(
                        song,
                        null,
                        listSettings.songSort.songs(deviceLibrary.songs),
                        player.shuffleModeEnabled && playbackSettings.keepShuffle)
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
            logD("Played before, starting foreground state")
            if (!foregroundManager.tryStartForeground(notification)) {
                logD("Notification changed, re-posting")
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
                    logD("Received headset plug event")
                    when (intent.getIntExtra("state", -1)) {
                        0 -> pauseFromHeadsetPlug()
                        1 -> playFromHeadsetPlug()
                    }

                    initialHeadsetPlugEventHandled = true
                }
                AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                    logD("Received Headset noise event")
                    pauseFromHeadsetPlug()
                }

                // --- AUXIO EVENTS ---
                ACTION_PLAY_PAUSE -> {
                    logD("Received play event")
                    playbackManager.playing(!playbackManager.progression.isPlaying)
                }
                ACTION_INC_REPEAT_MODE -> {
                    logD("Received repeat mode event")
                    playbackManager.repeatMode(playbackManager.repeatMode.increment())
                }
                ACTION_INVERT_SHUFFLE -> {
                    logD("Received shuffle event")
                    playbackManager.shuffled(!playbackManager.isShuffled)
                }
                ACTION_SKIP_PREV -> {
                    logD("Received skip previous event")
                    playbackManager.prev()
                }
                ACTION_SKIP_NEXT -> {
                    logD("Received skip next event")
                    playbackManager.next()
                }
                ACTION_EXIT -> {
                    logD("Received exit event")
                    playbackManager.playing(false)
                    stopAndSave()
                }
                WidgetProvider.ACTION_WIDGET_UPDATE -> {
                    logD("Received widget update event")
                    widgetComponent.update()
                }
            }
        }

        private fun playFromHeadsetPlug() {
            // ACTION_HEADSET_PLUG will fire when this BroadcastReciever is initially attached,
            // which would result in unexpected playback. Work around it by dropping the first
            // call to this function, which should come from that Intent.
            if (playbackSettings.headsetAutoplay &&
                playbackManager.currentSong != null &&
                initialHeadsetPlugEventHandled) {
                logD("Device connected, resuming")
                playbackManager.playing(true)
            }
        }

        private fun pauseFromHeadsetPlug() {
            if (playbackManager.currentSong != null) {
                logD("Device disconnected, pausing")
                playbackManager.playing(false)
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
