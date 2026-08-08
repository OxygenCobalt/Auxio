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
import android.provider.OpenableColumns
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.decoder.ffmpeg.FfmpegAudioRenderer
import androidx.media3.exoplayer.BaseRenderer
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.MediaSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.abs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.oxycblt.auxio.image.ImageSettings
import org.oxycblt.auxio.music.MusicRepository
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
import org.oxycblt.musikr.MusicParent
import org.oxycblt.musikr.Song
import timber.log.Timber as L

@OptIn(UnstableApi::class)
class ExoPlaybackStateHolder(
    private val context: Context,
    private val player: ExoPlayer,
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
    PlaybackSettings.Listener,
    ImageSettings.Listener {
    private val saveJob = Job()
    private val saveScope = CoroutineScope(Dispatchers.IO + saveJob)
    private val restoreScope = CoroutineScope(Dispatchers.IO + saveJob)
    private var currentSaveJob: Job? = null
    private var openAudioEffectSession = false

    // The queue is owned here rather than by ExoPlayer, since a MediaItem per queue entry does
    // not scale to library-sized queues. The representation mirrors RawQueue.
    private val heap = mutableListOf<Song>()
    private val mapping = mutableListOf<Int>()
    private var heapIndex = -1
    private var repeatModeState = RepeatMode.NONE

    // The player's playlist as resolved queue positions, a small contiguous run around the
    // current song. Wraps circularly under RepeatMode.ALL.
    private val window = ArrayDeque<Int>()

    var sessionOngoing = false
        private set

    fun attach() {
        playbackManager.registerStateHolder(this)
        musicRepository.addUpdateListener(this)
        player.addListener(this)
        replayGainProcessor.attach()
        playbackSettings.registerListener(this)
        imageSettings.registerListener(this)
    }

    fun release() {
        saveJob.cancel()
        playbackManager.unregisterStateHolder(this)
        musicRepository.removeUpdateListener(this)
        player.removeListener(this)
        replayGainProcessor.release()
        imageSettings.unregisterListener(this)
        playbackSettings.unregisterListener(this)
        player.release()
    }

    override var parent: MusicParent? = null
        private set

    override val progression: Progression
        get() {
            val mediaItem = player.currentMediaItem ?: return Progression.nil()
            val duration = mediaItem.mediaMetadata.extras?.getLong("durationMs") ?: Long.MAX_VALUE
            val clampedPosition = player.currentPosition.coerceAtLeast(0).coerceAtMost(duration)
            return Progression.from(player.playWhenReady, player.isPlaying, clampedPosition)
        }

    override val repeatMode
        get() = repeatModeState

    override val audioSessionId: Int
        get() = player.audioSessionId

    override fun resolveQueue() = RawQueue(heap.toList(), mapping.toList(), heapIndex)

    override fun handleDeferred(action: DeferredPlayback): Boolean {
        val library =
            musicRepository.library?.takeIf { !it.empty() }
                // No library, cannot do anything.
                ?: return false

        when (action) {
            // Restore state -> Start a new restoreState job
            is DeferredPlayback.RestoreState -> {
                L.d("Restoring playback state")
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
                L.d("Shuffling all tracks")
                playbackManager.play(
                    requireNotNull(commandFactory.all(ShuffleMode.ON)) {
                        "Invalid playback parameters"
                    }
                )
            }
            // Open -> Try to find the Song for the given file and then play it from all songs
            is DeferredPlayback.Open -> {
                L.d("Opening specified file")
                context.applicationContext.contentResolver
                    .query(
                        action.uri,
                        arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
                        null,
                        null,
                        null,
                    )
                    ?.use { cursor ->
                        val displayNameIndex =
                            cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                        val sizeIndex = cursor.getColumnIndexOrThrow(OpenableColumns.SIZE)
                        if (cursor.moveToFirst()) {
                            val displayName = cursor.getString(displayNameIndex)
                            val size = cursor.getLong(sizeIndex)
                            val song =
                                library.songs.find {
                                    it.path.name == displayName && it.size == size
                                }
                            if (song != null) {
                                val command =
                                    requireNotNull(
                                        commandFactory.songFromAll(song, ShuffleMode.IMPLICIT)
                                    ) {
                                        "Invalid playback command"
                                    }
                                playbackManager.play(command)
                            }
                        }
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
        repeatModeState = repeatMode
        syncPlayerRepeatMode()
        updatePauseOnRepeat()
        // The repeat mode decides whether the window wraps, so it may need to be reshaped.
        slideWindow()
        playbackManager.ack(this, StateAck.RepeatModeChanged)
        deferSave()
    }

    override fun newPlayback(command: PlaybackCommand) {
        parent = command.parent
        heap.clear()
        heap.addAll(command.queue)
        mapping.clear()
        val startHeapIndex =
            command.song?.let { song ->
                command.queue.indexOf(song).also { check(it != -1) { "Start song not in queue" } }
            }
        if (command.shuffled && heap.isNotEmpty()) {
            mapping.addAll(shuffledMapping(anchor = startHeapIndex))
        }
        heapIndex =
            when {
                heap.isEmpty() -> -1
                startHeapIndex != null -> startHeapIndex
                isShuffled -> mapping[0]
                else -> 0
            }
        syncPlayerRepeatMode()
        hardResetWindow()
        player.prepare()
        player.play()
        playbackManager.ack(this, StateAck.NewPlayback)
        deferSave()
    }

    override fun shuffled(shuffled: Boolean) {
        if (heap.isEmpty()) {
            return
        }
        mapping.clear()
        if (shuffled) {
            mapping.addAll(shuffledMapping(anchor = heapIndex))
        }
        syncPlayerRepeatMode()
        refreshWindow()
        playbackManager.ack(this, StateAck.QueueReordered)
        deferSave()
    }

    override fun next() {
        // Replicate the old pseudo-circular queue behavior when no repeat option is implemented.
        // Basically, you can't skip back and wrap around the queue, but you can skip forward and
        // wrap around the queue, albeit playback will be paused.
        if (repeatModeState == RepeatMode.ALL || player.hasNextMediaItem()) {
            player.seekToNext()
            syncIndexFromPlayer()
            if (!playbackSettings.rememberPause) {
                player.play()
            }
        } else {
            gotoImpl(0)
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
        syncIndexFromPlayer()
        if (!playbackSettings.rememberPause) {
            player.play()
        }
        playbackManager.ack(this, StateAck.IndexMoved)
        deferSave()
    }

    override fun goto(index: Int) {
        if (heap.isEmpty()) {
            return
        }
        gotoImpl(index)
        if (!playbackSettings.rememberPause) {
            player.play()
        }
        playbackManager.ack(this, StateAck.IndexMoved)
        deferSave()
    }

    override fun playNext(songs: List<Song>, ack: StateAck.PlayNext) {
        if (heap.isEmpty()) {
            return
        }
        val insertAt = heapIndex + 1
        heap.addAll(insertAt, songs)
        if (isShuffled) {
            for (i in mapping.indices) {
                if (mapping[i] >= insertAt) {
                    mapping[i] += songs.size
                }
            }
            mapping.addAll(resolvedIndex() + 1, List(songs.size) { insertAt + it })
        }
        refreshWindow()
        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun addToQueue(songs: List<Song>, ack: StateAck.AddToQueue) {
        if (heap.isEmpty()) {
            return
        }
        val base = heap.size
        heap.addAll(songs)
        if (isShuffled) {
            mapping.addAll(List(songs.size) { base + it })
        }
        refreshWindow()
        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun move(from: Int, to: Int, ack: StateAck.Move) {
        if (heap.isEmpty()) {
            return
        }
        if (isShuffled) {
            mapping.add(to, mapping.removeAt(from))
        } else {
            heap.add(to, heap.removeAt(from))
            heapIndex =
                when {
                    heapIndex == from -> to
                    from < heapIndex && to >= heapIndex -> heapIndex - 1
                    from > heapIndex && to <= heapIndex -> heapIndex + 1
                    else -> heapIndex
                }
        }
        refreshWindow()
        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun remove(at: Int, ack: StateAck.Remove) {
        if (heap.isEmpty()) {
            return
        }
        val removedHeapIndex = heapIndexAt(at)
        val songWillChange = removedHeapIndex == heapIndex
        heap.removeAt(removedHeapIndex)
        if (isShuffled) {
            mapping.removeAt(at)
            for (i in mapping.indices) {
                if (mapping[i] > removedHeapIndex) {
                    mapping[i] -= 1
                }
            }
        }
        if (removedHeapIndex < heapIndex) {
            heapIndex -= 1
        }
        when {
            heap.isEmpty() -> {
                heapIndex = -1
                window.clear()
                player.clearMediaItems()
            }
            songWillChange -> {
                // Playback moves to the song now occupying the removed song's position.
                heapIndex = heapIndexAt(at.coerceAtMost(heap.size - 1))
                hardResetWindow()
            }
            else -> refreshWindow()
        }
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
        ack: StateAck.NewPlayback?,
    ) {
        var sendNewPlaybackEvent = false
        var shouldSeek = false
        if (this.parent != parent) {
            this.parent = parent
            sendNewPlaybackEvent = true
        }
        if (rawQueue != resolveQueue()) {
            heap.clear()
            heap.addAll(rawQueue.heap)
            mapping.clear()
            mapping.addAll(rawQueue.shuffledMapping)
            heapIndex = rawQueue.heapIndex
            repeatModeState = repeatMode
            syncPlayerRepeatMode()
            hardResetWindow()
            player.prepare()
            player.pause()
            sendNewPlaybackEvent = true
            shouldSeek = true
        }

        repeatMode(repeatMode)
        // See if we differ by more than a second. This allows us to avoid a meaningless seek
        // in the case of a "tight restore" (i.e music was reloaded).
        // In the case that this is a false positive, it's not very percievable (at least compared
        // to skipping when updating the library).
        // TODO: Introduce a better state management system rather than do something finicky like
        // this.
        if (shouldSeek || abs(player.currentPosition - positionMs) > 1000L) {
            player.seekTo(positionMs)
        }

        if (sendNewPlaybackEvent) {
            ack?.let { playbackManager.ack(this, it) }
        }
    }

    override fun endSession() {
        // This session has ended, so we need to reset this flag for when the next
        // session starts.
        playbackManager.playing(false)
        save {
            // User could feasibly start playing again if they were fast enough, so
            // we need to avoid stopping the foreground state if that's the case.
            if (!playbackManager.progression.isPlaying) {
                sessionOngoing = false
                playbackManager.ack(this, StateAck.SessionEnded)
            }
        }
    }

    override fun reset(ack: StateAck.NewPlayback) {
        heap.clear()
        mapping.clear()
        heapIndex = -1
        window.clear()
        player.setMediaItems(listOf())
        playbackManager.ack(this, ack)
        deferSave()
    }

    // --- QUEUE WINDOW MANAGEMENT ---

    private val isShuffled
        get() = mapping.isNotEmpty()

    private fun resolvedIndex(): Int = if (isShuffled) mapping.indexOf(heapIndex) else heapIndex

    private fun heapIndexAt(resolved: Int): Int = if (isShuffled) mapping[resolved] else resolved

    private fun songAt(resolved: Int): Song = heap[heapIndexAt(resolved)]

    /** Create a new random play order over the heap, with [anchor] first if given. */
    private fun shuffledMapping(anchor: Int?): List<Int> {
        val indices = MutableList(heap.size) { it }
        indices.shuffle()
        if (anchor != null) {
            val at = indices.indexOf(anchor)
            indices[at] = indices[0]
            indices[0] = anchor
        }
        return indices
    }

    /**
     * The resolved positions the player should currently hold: the whole queue when it's small,
     * otherwise a fixed-size run centered on [center]. Under [RepeatMode.ALL] the run wraps around
     * the queue edges so the player can always advance (and skip back) across them.
     */
    private fun computeWindowPositions(center: Int): List<Int> {
        val size = heap.size
        if (size == 0 || center < 0) {
            return emptyList()
        }
        if (size <= WINDOW_MAX_SIZE) {
            return (0 until size).toList()
        }
        return if (repeatModeState == RepeatMode.ALL) {
            (center - WINDOW_RADIUS..center + WINDOW_RADIUS).map { ((it % size) + size) % size }
        } else {
            val start = (center - WINDOW_RADIUS).coerceAtLeast(0)
            val end = (center + WINDOW_RADIUS).coerceAtMost(size - 1)
            (start..end).toList()
        }
    }

    /**
     * When the whole queue fits in the window the player behaves exactly as it did before windowing
     * and can use its native repeat handling. Otherwise repeat-all is emulated by wrapping the
     * window, so the player itself must not repeat.
     */
    private fun syncPlayerRepeatMode() {
        player.repeatMode =
            when (repeatModeState) {
                RepeatMode.TRACK -> Player.REPEAT_MODE_ONE
                RepeatMode.ALL ->
                    if (heap.size <= WINDOW_MAX_SIZE) {
                        Player.REPEAT_MODE_ALL
                    } else {
                        Player.REPEAT_MODE_OFF
                    }
                RepeatMode.NONE -> Player.REPEAT_MODE_OFF
            }
    }

    /** Replace the playlist outright, (re)starting the current song. */
    private fun hardResetWindow() {
        val desired = computeWindowPositions(resolvedIndex())
        window.clear()
        window.addAll(desired)
        if (desired.isEmpty()) {
            player.clearMediaItems()
            return
        }
        player.setMediaItems(desired.map { songAt(it).buildMediaItem() })
        player.seekTo(desired.indexOf(resolvedIndex()), C.TIME_UNSET)
    }

    /** Rebuild the playlist around the currently-playing item without interrupting it. */
    private fun refreshWindow() {
        val current = resolvedIndex()
        val desired = computeWindowPositions(current)
        if (desired.isEmpty()) {
            window.clear()
            player.clearMediaItems()
            return
        }
        val playing = player.currentMediaItem?.song
        if (playing == null || playing != heap.getOrNull(heapIndex)) {
            hardResetWindow()
            return
        }
        val cur = player.currentMediaItemIndex
        if (cur + 1 < player.mediaItemCount) {
            player.removeMediaItems(cur + 1, player.mediaItemCount)
        }
        if (cur > 0) {
            player.removeMediaItems(0, cur)
        }
        val split = desired.indexOf(current)
        if (split > 0) {
            player.addMediaItems(0, desired.subList(0, split).map { songAt(it).buildMediaItem() })
        }
        player.addMediaItems(
            desired.subList(split + 1, desired.size).map { songAt(it).buildMediaItem() }
        )
        window.clear()
        window.addAll(desired)
    }

    /**
     * Slide the window towards the current position after an index move, keeping untouched items in
     * place so the player's preload of the next song survives. Falls back to a rebuild if the
     * window somehow diverged.
     */
    private fun slideWindow() {
        val current = resolvedIndex()
        val desired = computeWindowPositions(current)
        if (desired == window) {
            return
        }
        if (desired.isEmpty()) {
            window.clear()
            player.clearMediaItems()
            return
        }
        val desiredSet = desired.toHashSet()
        // Never removes the current item, since the window is always computed around it.
        while (window.isNotEmpty() && window.first() !in desiredSet) {
            player.removeMediaItem(0)
            window.removeFirst()
        }
        while (window.isNotEmpty() && window.last() !in desiredSet) {
            player.removeMediaItem(window.size - 1)
            window.removeLast()
        }
        if (window.isEmpty()) {
            hardResetWindow()
            return
        }
        val i0 = desired.indexOf(window.first())
        val i1 = desired.indexOf(window.last())
        if (i0 == -1 || i1 == -1) {
            hardResetWindow()
            return
        }
        if (i0 > 0) {
            val pre = desired.subList(0, i0)
            player.addMediaItems(0, pre.map { songAt(it).buildMediaItem() })
            pre.asReversed().forEach { window.addFirst(it) }
        }
        if (i1 < desired.size - 1) {
            val post = desired.subList(i1 + 1, desired.size)
            player.addMediaItems(post.map { songAt(it).buildMediaItem() })
            window.addAll(post)
        }
    }

    /** Adopt the player's current item after the player moved on its own. */
    private fun syncIndexFromPlayer() {
        val resolved = window.getOrNull(player.currentMediaItemIndex) ?: return
        heapIndex = heapIndexAt(resolved)
        slideWindow()
    }

    private fun gotoImpl(resolved: Int) {
        heapIndex = heapIndexAt(resolved)
        val at = window.indexOf(resolved)
        if (at != -1) {
            player.seekTo(at, C.TIME_UNSET)
            slideWindow()
        } else {
            hardResetWindow()
        }
    }

    // --- PLAYER OVERRIDES ---

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)

        if (player.playWhenReady) {
            // Mark that we have started playing so that the notification can now be posted.
            L.d("Player has started playing")
            sessionOngoing = true
            if (!openAudioEffectSession) {
                // Convention to start an audioeffect session on play/pause rather than
                // start/stop
                L.d("Opening audio effect session")
                broadcastAudioEffectAction(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION)
                openAudioEffectSession = true
            }
        } else if (openAudioEffectSession) {
            // Make sure to close the audio session when we stop playback.
            L.d("Closing audio effect session")
            broadcastAudioEffectAction(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION)
            openAudioEffectSession = false
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

        if (playbackState == Player.STATE_ENDED && repeatModeState == RepeatMode.NONE) {
            goto(0)
            player.pause()
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)

        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
            syncIndexFromPlayer()
            playbackManager.ack(this, StateAck.IndexMoved)
            deferSave()
        }
    }

    override fun onEvents(player: Player, events: Player.Events) {
        super.onEvents(player, events)

        // So many actions trigger progression changes that it becomes easier just to handle it
        // in an ExoPlayer callback anyway. This doesn't really cause issues anywhere.
        if (
            events.containsAny(
                Player.EVENT_PLAY_WHEN_READY_CHANGED,
                Player.EVENT_IS_PLAYING_CHANGED,
                Player.EVENT_POSITION_DISCONTINUITY,
            )
        ) {
            L.d("Player state changed, must synchronize state")
            playbackManager.ack(this, StateAck.ProgressionChanged)
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        // TODO: Replace with no skipping and a notification instead
        // If there's any issue, just go to the next song.
        L.e("Player error occurred")
        L.e(error.stackTraceToString())
        player.prepare()
        playbackManager.next()
    }

    private fun broadcastAudioEffectAction(event: String) {
        L.d("Broadcasting AudioEffect event: $event")
        context.sendBroadcast(
            Intent(event)
                .putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
                .putExtra(AudioEffect.EXTRA_AUDIO_SESSION, audioSessionId)
                .putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
        )
    }

    // --- MUSICREPOSITORY METHODS ---

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        if (changes.deviceLibrary && musicRepository.library?.takeIf { !it.empty() } != null) {
            // We now have a library, see if we have anything we need to do.
            L.d("Library obtained, requesting action")
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
            repeatModeState == RepeatMode.TRACK && playbackSettings.pauseOnRepeat
    }

    private fun save(cb: () -> Unit) {
        saveJob {
            if (sessionOngoing) {
                persistenceRepository.saveState(playbackManager.toSavedState())
            }
            withContext(Dispatchers.Main) { cb() }
        }
    }

    private fun deferSave() {
        saveJob {
            L.d("Waiting for save buffer")
            delay(SAVE_BUFFER)
            yield()
            L.d("Committing saved state")
            if (sessionOngoing) {
                persistenceRepository.saveState(playbackManager.toSavedState())
            }
        }
    }

    private fun saveJob(block: suspend () -> Unit) {
        currentSaveJob?.let {
            L.d("Discarding prior save job")
            it.cancel()
        }
        currentSaveJob = saveScope.launch { block() }
    }

    private fun Song.buildMediaItem() = MediaItem.Builder().setUri(uri).setTag(this).build()

    private val MediaItem.song: Song?
        get() = this.localConfiguration?.tag as? Song?

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
            // battery/apk size/cache size]
            val audioRenderer = RenderersFactory { handler, _, audioListener, _, _ ->
                arrayOf<BaseRenderer>(
                    FfmpegAudioRenderer(handler, audioListener, replayGainProcessor),
                    MediaCodecAudioRenderer(
                        context,
                        MediaCodecSelector.DEFAULT,
                        handler,
                        audioListener,
                        DefaultAudioSink.Builder(context)
                            .setAudioProcessors(arrayOf(replayGainProcessor))
                            .build(),
                    ),
                )
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
                        true,
                    )
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
                imageSettings,
            )
        }
    }

    private companion object {
        const val SAVE_BUFFER = 5000L

        /**
         * How many songs to keep in the player on either side of the current one. Only needs to be
         * big enough that a burst of rapid skips can't outrun the window before it slides.
         */
        const val WINDOW_RADIUS = 25

        /** Queues at or below this size skip windowing entirely and behave exactly as before. */
        const val WINDOW_MAX_SIZE = WINDOW_RADIUS * 2 + 1
    }
}
