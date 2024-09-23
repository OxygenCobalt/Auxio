/*
 * Copyright (c) 2024 Auxio Project
 * ExoPlaybackStateHolder.kt is part of Auxio.
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

import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
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
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.oxycblt.auxio.image.ImageSettings
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.playback.persist.PersistenceRepository
import org.oxycblt.auxio.playback.replaygain.ReplayGainAudioProcessor
import org.oxycblt.auxio.playback.state.DeferredPlayback
import org.oxycblt.auxio.playback.state.PlaybackCommand
import org.oxycblt.auxio.playback.state.PlaybackStateHolder
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.Progression
import org.oxycblt.auxio.playback.state.RawQueue
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.playback.state.ShuffleMode
import org.oxycblt.auxio.playback.state.StateAck
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE

class ExoPlaybackStateHolder(
    private val context: Context,
    private val kernel: PlayerKernel,
    private val playbackManager: PlaybackStateManager,
    private val persistenceRepository: PersistenceRepository,
    private val playbackSettings: PlaybackSettings,
    private val commandFactory: PlaybackCommand.Factory,
    private val replayGainProcessor: ReplayGainAudioProcessor,
    private val musicRepository: MusicRepository,
    private val imageSettings: ImageSettings,
) :
    PlaybackStateHolder,
    Player.Listener,
    MusicRepository.UpdateListener,
    ImageSettings.Listener {
    class Factory
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val playbackManager: PlaybackStateManager,
        private val persistenceRepository: PersistenceRepository,
        private val playbackSettings: PlaybackSettings,
        private val commandFactory: PlaybackCommand.Factory,
        private val mediaSourceFactory: MediaSource.Factory,
        private val replayGainProcessor: ReplayGainAudioProcessor,
        private val musicRepository: MusicRepository,
        private val imageSettings: ImageSettings,
    ) {
        fun create(): ExoPlaybackStateHolder {
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

            val exoPlayer =
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

            return ExoPlaybackStateHolder(
                context,
                GaplessPlayerKernel(exoPlayer, playbackSettings),
                playbackManager,
                persistenceRepository,
                playbackSettings,
                commandFactory,
                replayGainProcessor,
                musicRepository,
                imageSettings)
        }
    }

    private val saveJob = Job()
    private val saveScope = CoroutineScope(Dispatchers.IO + saveJob)
    private val restoreScope = CoroutineScope(Dispatchers.IO + saveJob)
    private var currentSaveJob: Job? = null
    private var openAudioEffectSession = false

    var sessionOngoing = false
        private set

    fun attach() {
        imageSettings.registerListener(this)
        kernel.addListener(this)
        playbackManager.registerStateHolder(this)
        musicRepository.addUpdateListener(this)
    }

    fun release() {
        saveJob.cancel()
        kernel.removeListener(this)
        playbackManager.unregisterStateHolder(this)
        musicRepository.removeUpdateListener(this)
        replayGainProcessor.release()
        imageSettings.unregisterListener(this)
        kernel.release()
    }

    override var parent: MusicParent? = null
        private set

    override val progression: Progression
        get() {
            val mediaItem = kernel.currentMediaItem ?: return Progression.nil()
            val duration = mediaItem.mediaMetadata.extras?.getLong("durationMs") ?: Long.MAX_VALUE
            val clampedPosition = kernel.currentPosition.coerceAtLeast(0).coerceAtMost(duration)
            return Progression.from(kernel.playWhenReady, kernel.isPlaying, clampedPosition)
        }

    override val repeatMode
        get() =
            when (val repeatMode = kernel.repeatMode) {
                Player.REPEAT_MODE_OFF -> RepeatMode.NONE
                Player.REPEAT_MODE_ONE -> RepeatMode.TRACK
                Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                else -> throw IllegalStateException("Unknown repeat mode: $repeatMode")
            }

    override val audioSessionId: Int
        get() = kernel.audioSessionId

    override fun resolveQueue(): RawQueue {
        val heap = kernel.computeHeap()
        val shuffledMapping = if (kernel.shuffleModeEnabled) kernel.computeMapping() else emptyList()
        return RawQueue(heap.mapNotNull { it.song }, shuffledMapping, kernel.currentMediaItemIndex)
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
                    val state = persistenceRepository.readState()
                    withContext(Dispatchers.Main) {
                        if (state != null) {
                            // Apply the saved state on the main thread to prevent code expecting
                            // state updates on the main thread from crashing.
                            playbackManager.applySavedState(state, false)
                            if (action.play) {
                                playbackManager.playing(true)
                            }
                        } else if (action.fallback != null) {
                            playbackManager.playDeferred(action.fallback)
                        }
                    }
                }
            }
            // Shuffle all -> Start new playback from all songs
            is DeferredPlayback.ShuffleAll -> {
                logD("Shuffling all tracks")
                playbackManager.play(
                    requireNotNull(commandFactory.all(ShuffleMode.ON)) {
                        "Invalid playback parameters"
                    })
            }
            // Open -> Try to find the Song for the given file and then play it from all songs
            is DeferredPlayback.Open -> {
                logD("Opening specified file")
                deviceLibrary.findSongForUri(context, action.uri)?.let { song ->
                    playbackManager.play(
                        requireNotNull(commandFactory.song(song, ShuffleMode.IMPLICIT)) {
                            "Invalid playback parameters"
                        })
                }
            }
        }

        return true
    }

    override fun playing(playing: Boolean) {
        kernel.playWhenReady = playing
    }

    override fun seekTo(positionMs: Long) {
        kernel.seekTo(positionMs)
        deferSave()
        // Ack handled w/ExoPlayer events
    }

    override fun repeatMode(repeatMode: RepeatMode) {
        kernel.repeatMode =
            when (repeatMode) {
                RepeatMode.NONE -> Player.REPEAT_MODE_OFF
                RepeatMode.ALL -> Player.REPEAT_MODE_ALL
                RepeatMode.TRACK -> Player.REPEAT_MODE_ONE
            }
        playbackManager.ack(this, StateAck.RepeatModeChanged)
        deferSave()
    }

    override fun newPlayback(command: PlaybackCommand) {
        parent = command.parent
        val mediaItems = command.queue.map { it.buildMediaItem() }
        val startIndex =
            command.song
                ?.let { command.queue.indexOf(it) }
                .also { check(it != -1) { "Start song not in queue" } }
        kernel.prepareNew(mediaItems, startIndex, command.shuffled)
        kernel.play()
        playbackManager.ack(this, StateAck.NewPlayback)
        deferSave()
    }

    override fun shuffled(shuffled: Boolean) {
        kernel.shuffled(shuffled)
        playbackManager.ack(this, StateAck.QueueReordered)
        deferSave()
    }

    override fun next() {
        // Replicate the old pseudo-circular queue behavior when no repeat option is implemented.
        // Basically, you can't skip back and wrap around the queue, but you can skip forward and
        // wrap around the queue, albeit playback will be paused.
        if (kernel.repeatMode == Player.REPEAT_MODE_ALL || kernel.hasNextMediaItem()) {
            kernel.seekToNext()
            if (!playbackSettings.rememberPause) {
                kernel.play()
            }
        } else {
            kernel.goto(kernel.computeFirstMediaItemIndex())
            // TODO: Dislike the UX implications of this, I feel should I bite the bullet
            //  and switch to dynamic skip enable/disable?
            if (!playbackSettings.rememberPause) {
                kernel.pause()
            }
        }
        playbackManager.ack(this, StateAck.IndexMoved)
        deferSave()
    }

    override fun prev() {
        if (playbackSettings.rewindWithPrev) {
            kernel.seekToPrevious()
        } else if (kernel.hasPreviousMediaItem()) {
            kernel.seekToPreviousMediaItem()
        } else {
            kernel.seekTo(0)
        }
        if (!playbackSettings.rememberPause) {
            kernel.play()
        }
        playbackManager.ack(this, StateAck.IndexMoved)
        deferSave()
    }

    override fun goto(index: Int) {
        val indices = kernel.computeMapping()
        if (indices.isEmpty()) {
            return
        }
        val trueIndex = indices[index]
        kernel.goto(trueIndex)
        if (!playbackSettings.rememberPause) {
            kernel.play()
        }
        playbackManager.ack(this, StateAck.IndexMoved)
        deferSave()
    }

    override fun playNext(songs: List<Song>, ack: StateAck.PlayNext) {
        kernel.addBottomMediaItems(songs.map { it.buildMediaItem() })
        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun addToQueue(songs: List<Song>, ack: StateAck.AddToQueue) {
        kernel.addTopMediaItems(songs.map { it.buildMediaItem() })
        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun move(from: Int, to: Int, ack: StateAck.Move) {
        val indices = kernel.computeMapping()
        if (indices.isEmpty()) {
            return
        }

        val trueFrom = indices[from]
        val trueTo = indices[to]

        kernel.moveMediaItem(trueFrom, trueTo)
        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun remove(at: Int, ack: StateAck.Remove) {
        val indices = kernel.computeMapping()
        if (indices.isEmpty()) {
            return
        }

        val trueIndex = indices[at]
        val songWillChange = kernel.currentMediaItemIndex == trueIndex
        kernel.removeMediaItem(trueIndex)
        if (songWillChange && !playbackSettings.rememberPause) {
            kernel.play()
        }
        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun applySavedState(
        parent: MusicParent?,
        rawQueue: RawQueue,
        ack: StateAck.NewPlayback?
    ) {
        logD("Applying saved state")
        var sendEvent = false
        if (this.parent != parent) {
            this.parent = parent
            sendEvent = true
        }
        if (rawQueue != resolveQueue()) {
            kernel.prepareSaved(rawQueue.heap.map { it.buildMediaItem() }, rawQueue.shuffledMapping, rawQueue.heapIndex, rawQueue.isShuffled)
            kernel.pause()
            sendEvent = true
        }
        if (sendEvent) {
            ack?.let { playbackManager.ack(this, it) }
        }
    }

    override fun endSession() {
        // This session has ended, so we need to reset this flag for when the next
        // session starts.
        save {
            // User could feasibly start playing again if they were fast enough, so
            // we need to avoid stopping the foreground state if that's the case.
            if (playbackManager.progression.isPlaying) {
                playbackManager.playing(false)
            }
            sessionOngoing = false
            playbackManager.ack(this, StateAck.SessionEnded)
        }
    }

    override fun reset(ack: StateAck.NewPlayback) {
        kernel.discard()
        playbackManager.ack(this, ack)
        deferSave()
    }

    // --- PLAYER OVERRIDES ---

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)

        if (kernel.playWhenReady) {
            // Mark that we have started playing so that the notification can now be posted.
            logD("Player has started playing")
            sessionOngoing = true
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

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

        if (playbackState == Player.STATE_ENDED && kernel.repeatMode == Player.REPEAT_MODE_OFF) {
            goto(0)
            kernel.pause()
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)

        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
            playbackManager.ack(this, StateAck.IndexMoved)
        }
    }

    override fun onEvents(player: Player, events: Player.Events) {
        super.onEvents(player, events)

        // So many actions trigger progression changes that it becomes easier just to handle it
        // in an ExoPlayer callback anyway. This doesn't really cause issues anywhere.
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
        logE("Player error occurred")
        logE(error.stackTraceToString())
        playbackManager.next()
    }

    private fun broadcastAudioEffectAction(event: String) {
        logD("Broadcasting AudioEffect event: $event")
        context.sendBroadcast(
            Intent(event)
                .putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
                .putExtra(AudioEffect.EXTRA_AUDIO_SESSION, audioSessionId)
                .putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC))
    }

    // --- MUSICREPOSITORY METHODS ---

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        if (changes.deviceLibrary && musicRepository.deviceLibrary != null) {
            // We now have a library, see if we have anything we need to do.
            logD("Library obtained, requesting action")
            playbackManager.requestAction(this)
        }
    }

    private fun save(cb: () -> Unit) {
        saveJob {
            persistenceRepository.saveState(playbackManager.toSavedState())
            withContext(Dispatchers.Main) { cb() }
        }
    }

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

    private fun Song.buildMediaItem() = MediaItem.Builder().setUri(uri).setTag(this).build()

    private val MediaItem.song: Song?
        get() = this.localConfiguration?.tag as? Song?

    private companion object {
        const val SAVE_BUFFER = 5000L
    }
}
