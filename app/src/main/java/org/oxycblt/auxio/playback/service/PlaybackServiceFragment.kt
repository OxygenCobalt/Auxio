/*
 * Copyright (c) 2021 Auxio Project
 * PlaybackServiceFragment.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.audiofx.AudioEffect
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
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.list.ListSettings
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
import org.oxycblt.auxio.playback.state.RawQueue
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.playback.state.StateAck
import org.oxycblt.auxio.service.ServiceFragment
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
class PlaybackServiceFragment
@Inject
constructor(
    val mediaSourceFactory: MediaSource.Factory,
    val replayGainProcessor: ReplayGainAudioProcessor,
    val mediaSessionComponent: MediaSessionComponent,
    val widgetComponent: WidgetComponent,
    val playbackManager: PlaybackStateManager,
    val playbackSettings: PlaybackSettings,
    val persistenceRepository: PersistenceRepository,
    val listSettings: ListSettings,
    val musicRepository: MusicRepository
) :
    ServiceFragment(),
    Player.Listener,
    PlaybackStateHolder,
    PlaybackSettings.Listener,
    MediaSessionComponent.Listener,
    MusicRepository.UpdateListener {
    // Player components
    private lateinit var player: ExoPlayer

    // System backend components
    private val systemReceiver = PlaybackReceiver()

    // Stat
    private var hasPlayed = false
    private var openAudioEffectSession = false

    // Coroutines
    private val serviceJob = Job()
    private val restoreScope = CoroutineScope(serviceJob + Dispatchers.IO)
    private val saveScope = CoroutineScope(serviceJob + Dispatchers.IO)
    private var currentSaveJob: Job? = null

    // --- SERVICE OVERRIDES ---

    override fun onCreate(context: Context) {
        // Since Auxio is a music player, only specify an audio renderer to save
        // battery/apk size/cache size
        val audioRenderer = RenderersFactory { handler, _, audioListener, _, _ ->
            arrayOf(
                FfmpegAudioRenderer(handler, audioListener, replayGainProcessor),
                MediaCodecAudioRenderer(
                    context,
                    MediaCodecSelector.DEFAULT,
                    handler,
                    audioListener,
                    AudioCapabilities.DEFAULT_AUDIO_CAPABILITIES,
                    replayGainProcessor))
        }

        player =
            ExoPlayer.Builder(context, audioRenderer)
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
        // Initialize any listener-dependent components last as we wouldn't want a listener race
        // condition to cause us to load music before we were fully initialize.
        playbackManager.registerStateHolder(this)
        musicRepository.addUpdateListener(this)
        mediaSessionComponent.registerListener(this)
        playbackSettings.registerListener(this)

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
            context, systemReceiver, intentFilter, ContextCompat.RECEIVER_EXPORTED)

        logD("Service created")
    }

    override fun onStartCommand(intent: Intent) {
        // Forward system media button sent by MediaButtonReceiver to MediaSessionComponent
        if (intent.action == Intent.ACTION_MEDIA_BUTTON) {
            mediaSessionComponent.handleMediaButtonIntent(intent)
        }
    }

    override fun onTaskRemoved() {
        if (!playbackManager.progression.isPlaying) {
            playbackManager.playing(false)
            endSession()
        }
    }

    override fun onDestroy() {
        // Pause just in case this destruction was unexpected.
        playbackManager.playing(false)
        playbackManager.unregisterStateHolder(this)
        musicRepository.removeUpdateListener(this)
        playbackSettings.unregisterListener(this)

        context.unregisterReceiver(systemReceiver)
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

    override val progression: Progression
        get() =
            player.currentMediaItem?.let {
                Progression.from(
                    player.playWhenReady,
                    player.isPlaying,
                    // The position value can be below zero or past the expected duration, make
                    // sure we handle that.
                    player.currentPosition.coerceAtLeast(0).coerceAtMost(it.song.durationMs))
            }
                ?: Progression.nil()

    override val repeatMode
        get() =
            when (val repeatMode = player.repeatMode) {
                Player.REPEAT_MODE_OFF -> RepeatMode.NONE
                Player.REPEAT_MODE_ONE -> RepeatMode.TRACK
                Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                else -> throw IllegalStateException("Unknown repeat mode: $repeatMode")
            }

    override var parent: MusicParent? = null

    override fun resolveQueue(): RawQueue {
        val heap = (0 until player.mediaItemCount).map { player.getMediaItemAt(it).song }
        val shuffledMapping =
            if (player.shuffleModeEnabled) {
                player.unscrambleQueueIndices()
            } else {
                emptyList()
            }
        return RawQueue(heap, shuffledMapping, player.currentMediaItemIndex)
    }

    override val audioSessionId: Int
        get() = player.audioSessionId

    override fun newPlayback(
        queue: List<Song>,
        start: Song?,
        parent: MusicParent?,
        shuffled: Boolean
    ) {
        this.parent = parent
        player.shuffleModeEnabled = shuffled
        player.setMediaItems(queue.map { it.toMediaItem() })
        val startIndex =
            start
                ?.let { queue.indexOf(start) }
                .also { check(it != -1) { "Start song not in queue" } }
        if (shuffled) {
            player.setShuffleOrder(BetterShuffleOrder(queue.size, startIndex ?: -1))
        }
        val target =
            startIndex ?: player.currentTimeline.getFirstWindowIndex(player.shuffleModeEnabled)
        player.seekTo(target, C.TIME_UNSET)
        player.prepare()
        player.play()
        playbackManager.ack(this, StateAck.NewPlayback)
        deferSave()
    }

    override fun playing(playing: Boolean) {
        player.playWhenReady = playing
        // Dispatched later once all of the changes have been accumulated
        // Playing state is not persisted, do not need to save
    }

    override fun repeatMode(repeatMode: RepeatMode) {
        player.repeatMode =
            when (repeatMode) {
                RepeatMode.NONE -> Player.REPEAT_MODE_OFF
                RepeatMode.ALL -> Player.REPEAT_MODE_ALL
                RepeatMode.TRACK -> Player.REPEAT_MODE_ONE
            }
        playbackManager.ack(this, StateAck.RepeatModeChanged)
        updatePauseOnRepeat()
        deferSave()
    }

    override fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        // Dispatched later once all of the changes have been accumulated
        // Deferred save is handled on position discontinuity
    }

    override fun next() {
        // Replicate the old pseudo-circular queue behavior when no repeat option is implemented.
        // Basically, you can't skip back and wrap around the queue, but you can skip forward and
        // wrap around the queue, albeit playback will be paused.
        if (player.repeatMode != Player.REPEAT_MODE_OFF || player.hasNextMediaItem()) {
            player.seekToNext()
            if (!playbackSettings.rememberPause) {
                player.play()
            }
        } else {
            goto(0)
            // TODO: Dislike the UX implications of this, I feel should I bite the bullet
            //  and switch to dynamic skip enable/disable?
            if (!playbackSettings.rememberPause) {
                player.pause()
            }
        }
        playbackManager.ack(this, StateAck.IndexMoved)
        // Deferred save is handled on position discontinuity
    }

    override fun prev() {
        if (playbackSettings.rewindWithPrev) {
            player.seekToPrevious()
        } else {
            player.seekToPreviousMediaItem()
        }
        if (!playbackSettings.rememberPause) {
            player.play()
        }
        playbackManager.ack(this, StateAck.IndexMoved)
        // Deferred save is handled on position discontinuity
    }

    override fun goto(index: Int) {
        val indices = player.unscrambleQueueIndices()
        if (indices.isEmpty()) {
            return
        }

        val trueIndex = indices[index]
        player.seekTo(trueIndex, C.TIME_UNSET)
        if (!playbackSettings.rememberPause) {
            player.play()
        }
        playbackManager.ack(this, StateAck.IndexMoved)
        // Deferred save is handled on position discontinuity
    }

    override fun shuffled(shuffled: Boolean) {
        logD("Reordering queue to $shuffled")
        player.shuffleModeEnabled = shuffled
        if (shuffled) {
            // Have to manually refresh the shuffle seed and anchor it to the new current songs
            player.setShuffleOrder(
                BetterShuffleOrder(player.mediaItemCount, player.currentMediaItemIndex))
        }
        playbackManager.ack(this, StateAck.QueueReordered)
        deferSave()
    }

    override fun playNext(songs: List<Song>, ack: StateAck.PlayNext) {
        val currTimeline = player.currentTimeline
        val nextIndex =
            if (currTimeline.isEmpty) {
                C.INDEX_UNSET
            } else {
                currTimeline.getNextWindowIndex(
                    player.currentMediaItemIndex,
                    Player.REPEAT_MODE_OFF,
                    player.shuffleModeEnabled
                )
            }

        if (nextIndex == C.INDEX_UNSET) {
            player.addMediaItems(songs.map { it.toMediaItem() })
        } else {
            player.addMediaItems(nextIndex, songs.map { it.toMediaItem() })
        }

        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun addToQueue(songs: List<Song>, ack: StateAck.AddToQueue) {
        player.addMediaItems(songs.map { it.toMediaItem() })
        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun move(from: Int, to: Int, ack: StateAck.Move) {
        val indices = player.unscrambleQueueIndices()
        if (indices.isEmpty()) {
            return
        }

        val trueFrom = indices[from]
        val trueTo = indices[to]

        when {
            trueFrom > trueTo -> {
                player.moveMediaItem(trueFrom, trueTo)
                player.moveMediaItem(trueTo + 1, trueFrom)
            }
            trueTo > trueFrom -> {
                player.moveMediaItem(trueFrom, trueTo)
                player.moveMediaItem(trueTo - 1, trueFrom)
            }
        }
        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun remove(at: Int, ack: StateAck.Remove) {
        val indices = player.unscrambleQueueIndices()
        if (indices.isEmpty()) {
            return
        }

        val trueIndex = indices[at]
        val songWillChange = player.currentMediaItemIndex == trueIndex
        player.removeMediaItem(trueIndex)
        if (songWillChange && !playbackSettings.rememberPause) {
            player.play()
        }
        playbackManager.ack(this, ack)
        deferSave()
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
                deviceLibrary.findSongForUri(context.applicationContext, action.uri)?.let { song ->
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

    override fun applySavedState(
        parent: MusicParent?,
        rawQueue: RawQueue,
        ack: StateAck.NewPlayback?
    ) {
        this.parent = parent
        player.setMediaItems(rawQueue.heap.map { it.toMediaItem() })
        if (rawQueue.isShuffled) {
            player.shuffleModeEnabled = true
            player.setShuffleOrder(BetterShuffleOrder(rawQueue.shuffledMapping.toIntArray()))
        } else {
            player.shuffleModeEnabled = false
        }
        player.seekTo(rawQueue.heapIndex, C.TIME_UNSET)
        player.prepare()
        ack?.let { playbackManager.ack(this, it) }
    }

    override fun reset(ack: StateAck.NewPlayback) {
        player.setMediaItems(emptyList())
        playbackManager.ack(this, ack)
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
            playbackManager.ack(this, StateAck.IndexMoved)
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

        if (playbackState == Player.STATE_ENDED && player.repeatMode == Player.REPEAT_MODE_OFF) {
            goto(0)
            player.pause()
        }
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
        if (reason == Player.DISCONTINUITY_REASON_SEEK) {
            // TODO: Once position also naturally drifts by some threshold, save
            deferSave()
        }
    }

    override fun onEvents(player: Player, events: Player.Events) {
        super.onEvents(player, events)

        if (events.containsAny(
            Player.EVENT_PLAY_WHEN_READY_CHANGED,
            Player.EVENT_IS_PLAYING_CHANGED,
            Player.EVENT_POSITION_DISCONTINUITY)) {
            logD("Player state changed, must synchronize state")
            playbackManager.ack(this, StateAck.ProgressionChanged)
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        // TODO: Replace with no skipping and a notification instead
        // If there's any issue, just go to the next song.
        logE("Player error occured")
        logE(error.stackTraceToString())
        playbackManager.next()
    }

    // --- OTHER OVERRIDES ---

    override fun onPauseOnRepeatChanged() {
        updatePauseOnRepeat()
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        if (changes.deviceLibrary && musicRepository.deviceLibrary != null) {
            // We now have a library, see if we have anything we need to do.
            logD("Library obtained, requesting action")
            playbackManager.requestAction(this)
        }
    }

    override fun onPostNotification(notification: NotificationComponent) {
        // Do not post the notification if playback hasn't started yet. This prevents errors
        // where changing a setting would cause the notification to appear in an unfriendly
        // manner.
        if (hasPlayed) {
            logD("Played before, starting foreground state")
            startForeground(notification)
        }
    }

    // --- PLAYER MANAGEMENT ---

    private fun updatePauseOnRepeat() {
        player.pauseAtEndOfMediaItems =
            playbackManager.repeatMode == RepeatMode.TRACK && playbackSettings.pauseOnRepeat
    }

    private fun ExoPlayer.unscrambleQueueIndices(): List<Int> {
        val timeline = currentTimeline
        if (timeline.isEmpty) {
            return emptyList()
        }
        val queue = mutableListOf<Int>()

        // Add the active queue item.
        val currentMediaItemIndex = currentMediaItemIndex
        queue.add(currentMediaItemIndex)

        // Fill queue alternating with next and/or previous queue items.
        var firstMediaItemIndex = currentMediaItemIndex
        var lastMediaItemIndex = currentMediaItemIndex
        val shuffleModeEnabled = shuffleModeEnabled
        while ((firstMediaItemIndex != C.INDEX_UNSET || lastMediaItemIndex != C.INDEX_UNSET)) {
            // Begin with next to have a longer tail than head if an even sized queue needs to be
            // trimmed.
            if (lastMediaItemIndex != C.INDEX_UNSET) {
                lastMediaItemIndex =
                    timeline.getNextWindowIndex(
                        lastMediaItemIndex, Player.REPEAT_MODE_OFF, shuffleModeEnabled)
                if (lastMediaItemIndex != C.INDEX_UNSET) {
                    queue.add(lastMediaItemIndex)
                }
            }
            if (firstMediaItemIndex != C.INDEX_UNSET) {
                firstMediaItemIndex =
                    timeline.getPreviousWindowIndex(
                        firstMediaItemIndex, Player.REPEAT_MODE_OFF, shuffleModeEnabled)
                if (firstMediaItemIndex != C.INDEX_UNSET) {
                    queue.add(0, firstMediaItemIndex)
                }
            }
        }

        return queue
    }

    private fun Song.toMediaItem() = MediaItem.Builder().setUri(uri).setTag(this).build()

    private val MediaItem.song: Song
        get() = requireNotNull(localConfiguration).tag as Song

    // --- OTHER FUNCTIONS ---

    private fun deferSave() {
        saveJob {
            logD("Waiting for save buffer")
            delay(SAVE_BUFFER)
            yield()
            logD("Committing saved state")
            persistenceRepository.saveState(playbackManager.toSavedState())
        }
    }

    private fun saveJob(block: suspend () -> Unit) {
        currentSaveJob?.let {
            logD("Discarding prior save job")
            it.cancel()
        }
        currentSaveJob = saveScope.launch { block() }
    }

    private fun broadcastAudioEffectAction(event: String) {
        logD("Broadcasting AudioEffect event: $event")
        context.sendBroadcast(
            Intent(event)
                .putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
                .putExtra(AudioEffect.EXTRA_AUDIO_SESSION, audioSessionId)
                .putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC))
    }

    private fun endSession() {
        // This session has ended, so we need to reset this flag for when the next
        // session starts.
        saveJob {
            logD("Committing saved state")
            persistenceRepository.saveState(playbackManager.toSavedState())
            withContext(Dispatchers.Main) {
                // User could feasibly start playing again if they were fast enough, so
                // we need to avoid stopping the foreground state if that's the case.
                if (!player.isPlaying) {
                    hasPlayed = false
                    playbackManager.playing(false)
                    stopForeground()
                }
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
                    endSession()
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
        const val SAVE_BUFFER = 5000L
        const val ACTION_INC_REPEAT_MODE = BuildConfig.APPLICATION_ID + ".action.LOOP"
        const val ACTION_INVERT_SHUFFLE = BuildConfig.APPLICATION_ID + ".action.SHUFFLE"
        const val ACTION_SKIP_PREV = BuildConfig.APPLICATION_ID + ".action.PREV"
        const val ACTION_PLAY_PAUSE = BuildConfig.APPLICATION_ID + ".action.PLAY_PAUSE"
        const val ACTION_SKIP_NEXT = BuildConfig.APPLICATION_ID + ".action.NEXT"
        const val ACTION_EXIT = BuildConfig.APPLICATION_ID + ".action.EXIT"
    }
}
