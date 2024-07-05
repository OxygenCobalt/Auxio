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
import org.oxycblt.auxio.music.service.toMediaItem
import org.oxycblt.auxio.music.service.toSong
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.playback.msToSecs
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
    private val player: ExoPlayer,
    private val playbackManager: PlaybackStateManager,
    private val persistenceRepository: PersistenceRepository,
    private val playbackSettings: PlaybackSettings,
    private val commandFactory: PlaybackCommand.Factory,
    private val replayGainProcessor: ReplayGainAudioProcessor,
    private val musicRepository: MusicRepository,
    private val imageSettings: ImageSettings
) :
    PlaybackStateHolder,
    Player.Listener,
    MusicRepository.UpdateListener,
    PlaybackSettings.Listener,
    ImageSettings.Listener {
    private val saveJob = Job()
    private val saveScope = CoroutineScope(Dispatchers.IO + saveJob)
    private val restoreScope = CoroutineScope(Dispatchers.IO + saveJob)
    private var currentSaveJob: Job? = null
    private var openAudioEffectSession = false

    var sessionOngoing = false
        private set

    fun attach() {
        imageSettings.registerListener(this)
        player.addListener(this)
        replayGainProcessor.attach()
        playbackManager.registerStateHolder(this)
        playbackSettings.registerListener(this)
        musicRepository.addUpdateListener(this)
    }

    fun release() {
        saveJob.cancel()
        player.removeListener(this)
        playbackManager.unregisterStateHolder(this)
        musicRepository.removeUpdateListener(this)
        replayGainProcessor.release()
        imageSettings.unregisterListener(this)
        player.release()
    }

    override var parent: MusicParent? = null
        private set

    val mediaSessionPlayer: Player
        get() =
            MediaSessionPlayer(context, player, playbackManager, commandFactory, musicRepository)

    override val progression: Progression
        get() {
            val mediaItem = player.currentMediaItem ?: return Progression.nil()
            val duration = mediaItem.mediaMetadata.extras?.getLong("durationMs") ?: Long.MAX_VALUE
            val clampedPosition = player.currentPosition.coerceAtLeast(0).coerceAtMost(duration)
            return Progression.from(player.playWhenReady, player.isPlaying, clampedPosition)
        }

    override val repeatMode
        get() =
            when (val repeatMode = player.repeatMode) {
                Player.REPEAT_MODE_OFF -> RepeatMode.NONE
                Player.REPEAT_MODE_ONE -> RepeatMode.TRACK
                Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                else -> throw IllegalStateException("Unknown repeat mode: $repeatMode")
            }

    override val audioSessionId: Int
        get() = player.audioSessionId

    override fun resolveQueue(): RawQueue {
        val deviceLibrary =
            musicRepository.deviceLibrary
            // No library, cannot do anything.
            ?: return RawQueue(emptyList(), emptyList(), 0)
        val heap = (0 until player.mediaItemCount).map { player.getMediaItemAt(it) }
        val shuffledMapping =
            if (player.shuffleModeEnabled) {
                player.unscrambleQueueIndices()
            } else {
                emptyList()
            }
        return RawQueue(
            heap.mapNotNull { it.toSong(deviceLibrary) },
            shuffledMapping,
            player.currentMediaItemIndex)
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
        player.playWhenReady = playing
    }

    override fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        deferSave()
        // Ack handled w/ExoPlayer events
    }

    override fun repeatMode(repeatMode: RepeatMode) {
        player.repeatMode =
            when (repeatMode) {
                RepeatMode.NONE -> Player.REPEAT_MODE_OFF
                RepeatMode.ALL -> Player.REPEAT_MODE_ALL
                RepeatMode.TRACK -> Player.REPEAT_MODE_ONE
            }
        updatePauseOnRepeat()
        playbackManager.ack(this, StateAck.RepeatModeChanged)
        deferSave()
    }

    override fun newPlayback(command: PlaybackCommand) {
        parent = command.parent
        player.shuffleModeEnabled = command.shuffled
        player.setMediaItems(command.queue.map { it.toMediaItem(context, null) })
        val startIndex =
            command.song
                ?.let { command.queue.indexOf(it) }
                .also { check(it != -1) { "Start song not in queue" } }
        if (command.shuffled) {
            player.setShuffleOrder(BetterShuffleOrder(command.queue.size, startIndex ?: -1))
        }
        val target = startIndex ?: player.currentTimeline.getFirstWindowIndex(command.shuffled)
        player.seekTo(target, C.TIME_UNSET)
        player.prepare()
        player.play()
        playbackManager.ack(this, StateAck.NewPlayback)
        deferSave()
    }

    override fun shuffled(shuffled: Boolean) {
        player.setShuffleModeEnabled(shuffled)
        if (player.shuffleModeEnabled) {
            // Have to manually refresh the shuffle seed and anchor it to the new current songs
            player.setShuffleOrder(
                BetterShuffleOrder(player.mediaItemCount, player.currentMediaItemIndex))
        }
        playbackManager.ack(this, StateAck.QueueReordered)
        deferSave()
    }

    override fun next() {
        // Replicate the old pseudo-circular queue behavior when no repeat option is implemented.
        // Basically, you can't skip back and wrap around the queue, but you can skip forward and
        // wrap around the queue, albeit playback will be paused.
        if (player.repeatMode == Player.REPEAT_MODE_ALL || player.hasNextMediaItem()) {
            player.seekToNext()
            if (!playbackSettings.rememberPause) {
                player.play()
            }
        } else {
            player.seekTo(
                player.currentTimeline.getFirstWindowIndex(player.shuffleModeEnabled), C.TIME_UNSET)
            // TODO: Dislike the UX implications of this, I feel should I bite the bullet
            //  and switch to dynamic skip enable/disable?
            if (!playbackSettings.rememberPause) {
                player.pause()
            }
        }
        playbackManager.ack(this, StateAck.IndexMoved)
        deferSave()
    }

    override fun prev() {
        if (playbackSettings.rewindWithPrev) {
            player.seekToPrevious()
        } else if (player.hasPreviousMediaItem()) {
            player.seekToPreviousMediaItem()
        } else {
            player.seekTo(0)
        }
        if (!playbackSettings.rememberPause) {
            player.play()
        }
        playbackManager.ack(this, StateAck.IndexMoved)
        deferSave()
    }

    override fun goto(index: Int) {
        val indices = player.unscrambleQueueIndices()
        if (indices.isEmpty()) {
            return
        }

        val trueIndex = indices[index]
        player.seekTo(trueIndex, C.TIME_UNSET) // Handles remaining custom logic
        if (!playbackSettings.rememberPause) {
            player.play()
        }
        playbackManager.ack(this, StateAck.IndexMoved)
        deferSave()
    }

    override fun playNext(songs: List<Song>, ack: StateAck.PlayNext) {
        val currTimeline = player.currentTimeline
        val nextIndex =
            if (currTimeline.isEmpty) {
                C.INDEX_UNSET
            } else {
                currTimeline.getNextWindowIndex(
                    player.currentMediaItemIndex, Player.REPEAT_MODE_OFF, player.shuffleModeEnabled)
            }

        if (nextIndex == C.INDEX_UNSET) {
            player.addMediaItems(songs.map { it.toMediaItem(context, null) })
        } else {
            player.addMediaItems(nextIndex, songs.map { it.toMediaItem(context, null) })
        }
        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun addToQueue(songs: List<Song>, ack: StateAck.AddToQueue) {
        player.addMediaItems(songs.map { it.toMediaItem(context, null) })
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
        // ExoPlayer does not actually update it's ShuffleOrder when moving items. Retain a
        // semblance of "normalcy" by doing a weird no-op swap that actually moves the item.
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

    override fun applySavedState(
        parent: MusicParent?,
        rawQueue: RawQueue,
        positionMs: Long,
        repeatMode: RepeatMode,
        ack: StateAck.NewPlayback?
    ) {
        val resolve = resolveQueue()
        logD("${rawQueue.heap == resolve.heap}")
        logD("${rawQueue.shuffledMapping == resolve.shuffledMapping}")
        logD("${rawQueue.heapIndex == resolve.heapIndex}")
        logD("${rawQueue.isShuffled == resolve.isShuffled}")
        logD("${rawQueue == resolve}")
        var sendNewPlaybackEvent = false
        var shouldSeek = false
        if (this.parent != parent) {
            this.parent = parent
            sendNewPlaybackEvent = true
        }
        if (rawQueue != resolveQueue()) {
            player.setMediaItems(rawQueue.heap.map { it.toMediaItem(context, null) })
            if (rawQueue.isShuffled) {
                player.shuffleModeEnabled = true
                player.setShuffleOrder(BetterShuffleOrder(rawQueue.shuffledMapping.toIntArray()))
            } else {
                player.shuffleModeEnabled = false
            }
            player.seekTo(rawQueue.heapIndex, C.TIME_UNSET)
            player.prepare()
            player.pause()
            sendNewPlaybackEvent = true
            shouldSeek = true
        }

        repeatMode(repeatMode)
        // Positions in milliseconds will drift during tight restores (i.e what the music loader
        // does to sanitize the state), compare by seconds instead.
        if (positionMs.msToSecs() != player.currentPosition.msToSecs() || shouldSeek) {
            player.seekTo(positionMs)
        }

        if (sendNewPlaybackEvent) {
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
        player.setMediaItems(listOf())
        playbackManager.ack(this, ack)
        deferSave()
    }

    // --- PLAYER OVERRIDES ---

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)

        if (player.playWhenReady) {
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

        if (playbackState == Player.STATE_ENDED && player.repeatMode == Player.REPEAT_MODE_OFF) {
            goto(0)
            player.pause()
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

    // --- PLAYBACKSETTINGS OVERRIDES ---

    override fun onPauseOnRepeatChanged() {
        super.onPauseOnRepeatChanged()
        updatePauseOnRepeat()
    }

    private fun updatePauseOnRepeat() {
        player.pauseAtEndOfMediaItems =
            player.repeatMode == Player.REPEAT_MODE_ONE && playbackSettings.pauseOnRepeat
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
                exoPlayer,
                playbackManager,
                persistenceRepository,
                playbackSettings,
                commandFactory,
                replayGainProcessor,
                musicRepository,
                imageSettings)
        }
    }

    private companion object {
        const val SAVE_BUFFER = 5000L
    }
}
