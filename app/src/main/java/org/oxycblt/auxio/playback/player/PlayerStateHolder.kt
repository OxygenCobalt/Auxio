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
 
package org.oxycblt.auxio.playback.player

import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
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

class PlayerStateHolder(
    private val context: Context,
    playerFactory: PlayerFactory,
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
    ImageSettings.Listener,
    PlaybackSettings.Listener {
    class Factory
    @Inject
    constructor(
        private val playbackManager: PlaybackStateManager,
        private val persistenceRepository: PersistenceRepository,
        private val playbackSettings: PlaybackSettings,
        private val playerFactory: PlayerFactory,
        private val commandFactory: PlaybackCommand.Factory,
        private val replayGainProcessor: ReplayGainAudioProcessor,
        private val musicRepository: MusicRepository,
        private val imageSettings: ImageSettings,
    ) {
        fun create(context: Context): PlayerStateHolder {
            return PlayerStateHolder(
                context,
                playerFactory,
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
    private val player = playerFactory.create(context)
    private val queuer = player.intoQueuer(GaplessQueuer.Factory)

    var sessionOngoing = false
        private set

    fun attach() {
        player.attach(this)
        playbackSettings.registerListener(this)
        imageSettings.registerListener(this)
        playbackManager.registerStateHolder(this)
        musicRepository.addUpdateListener(this)
    }

    fun release() {
        saveJob.cancel()
        player.release()
        playbackSettings.unregisterListener(this)
        playbackManager.unregisterStateHolder(this)
        musicRepository.removeUpdateListener(this)
        replayGainProcessor.release()
        imageSettings.unregisterListener(this)
        player.release()
    }

    override var parent: MusicParent? = null
        private set

    override val progression: Progression
        get() {
            val mediaItem = queuer.currentMediaItem ?: return Progression.nil()
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
        val heap = queuer.computeHeap()
        val shuffledMapping = if (queuer.shuffleModeEnabled) queuer.computeMapping() else emptyList()
        return RawQueue(heap.mapNotNull { it.song }, shuffledMapping, queuer.currentMediaItemIndex)
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
        val mediaItems = command.queue.map { it.buildMediaItem() }
        val startIndex =
            command.song
                ?.let { command.queue.indexOf(it) }
                .also { check(it != -1) { "Start song not in queue" } }
        queuer.prepareNew(mediaItems, startIndex, command.shuffled)
        player.play()
        playbackManager.ack(this, StateAck.NewPlayback)
        deferSave()
    }

    override fun shuffled(shuffled: Boolean) {
        queuer.shuffled(shuffled)
        playbackManager.ack(this, StateAck.QueueReordered)
        deferSave()
    }

    override fun next() {
        // Replicate the old pseudo-circular queue behavior when no repeat option is implemented.
        // Basically, you can't skip back and wrap around the queue, but you can skip forward and
        // wrap around the queue, albeit playback will be paused.
        if (player.repeatMode == Player.REPEAT_MODE_ALL || queuer.hasNextMediaItem()) {
            queuer.seekToNext()
            if (!playbackSettings.rememberPause) {
                player.play()
            }
        } else {
            queuer.goto(queuer.computeFirstMediaItemIndex())
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
            queuer.seekToPrevious()
        } else if (queuer.hasPreviousMediaItem()) {
            queuer.seekToPreviousMediaItem()
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
        val indices = queuer.computeMapping()
        if (indices.isEmpty()) {
            return
        }
        val trueIndex = indices[index]
        queuer.goto(trueIndex)
        if (!playbackSettings.rememberPause) {
            player.play()
        }
        playbackManager.ack(this, StateAck.IndexMoved)
        deferSave()
    }

    override fun playNext(songs: List<Song>, ack: StateAck.PlayNext) {
        queuer.addBottomMediaItems(songs.map { it.buildMediaItem() })
        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun addToQueue(songs: List<Song>, ack: StateAck.AddToQueue) {
        queuer.addTopMediaItems(songs.map { it.buildMediaItem() })
        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun move(from: Int, to: Int, ack: StateAck.Move) {
        val indices = queuer.computeMapping()
        if (indices.isEmpty()) {
            return
        }

        val trueFrom = indices[from]
        val trueTo = indices[to]

        queuer.moveMediaItem(trueFrom, trueTo)
        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun remove(at: Int, ack: StateAck.Remove) {
        val indices = queuer.computeMapping()
        if (indices.isEmpty()) {
            return
        }

        val trueIndex = indices[at]
        val songWillChange = queuer.currentMediaItemIndex == trueIndex
        queuer.removeMediaItem(trueIndex)
        if (songWillChange && !playbackSettings.rememberPause) {
            player.play()
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
            queuer.prepareSaved(rawQueue.heap.map { it.buildMediaItem() }, rawQueue.shuffledMapping, rawQueue.heapIndex, rawQueue.isShuffled)
            player.pause()
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
        queuer.discard()
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

    // --- PLAYBACK SETTINGS METHODS ---

    override fun onPauseOnRepeatChanged() {
        super.onPauseOnRepeatChanged()
        updatePauseOnRepeat()
    }

    private fun updatePauseOnRepeat() {
        player.pauseAtEndOfMediaItems =
            player.repeatMode == Player.REPEAT_MODE_ONE && playbackSettings.pauseOnRepeat
    }

    // --- OVERRIDES ---

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
