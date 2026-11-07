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
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
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
import org.ostrichbat2k.usbdac.streaming.RoutingAudioSink
import org.ostrichbat2k.usbdac.UsbAudioManager
import org.ostrichbat2k.usbdac.UsbDacPreferences
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
    private val usbAudioManager: UsbAudioManager,
    private val usbSettings: UsbDacPreferences,
) :
    PlaybackStateHolder,
    Player.Listener,
    MusicRepository.UpdateListener,
    PlaybackSettings.Listener,
    ImageSettings.Listener,
    UsbDacPreferences.Listener {
    private val saveJob = Job()
    private val saveScope = CoroutineScope(Dispatchers.IO + saveJob)
    private val restoreScope = CoroutineScope(Dispatchers.IO + saveJob)
    private val positionSyncScope = CoroutineScope(Dispatchers.Main + saveJob)
    private var currentSaveJob: Job? = null
    private var pendingPlayJob: Job? = null
    private var positionSyncJob: Job? = null
    // The user's (or any other playing() caller's) latest actual intent, independent of whatever
    // player.playWhenReady happens to be mid-reconfigure -- see forceReconfigureOnDacStateChange's
    // own doc comment on why its final step reads this instead of a stale snapshot.
    @Volatile private var desiredPlaying = false
    private var openAudioEffectSession = false

    var sessionOngoing = false
        private set

    fun attach() {
        playbackManager.registerStateHolder(this)
        musicRepository.addUpdateListener(this)
        player.addListener(this)
        replayGainProcessor.attach()
        playbackSettings.registerListener(this)
        imageSettings.registerListener(this)
        usbSettings.registerListener(this)
        restoreScope.launch { forceReconfigureOnDacStateChange() }
    }

    /**
     * Forces the currently-playing track to re-resolve its audio route the moment the USB DAC
     * finishes activating mid-stream, rather than silently waiting for the next track boundary --
     * see [RoutingAudioSink.configure]'s own doc comment on why that's otherwise the only time a
     * track's route gets re-checked at all. (The opposite direction -- the DAC going away
     * mid-stream -- doesn't need forcing from here any more: confirmed, reproduced, it used to
     * leave [RoutingAudioSink] driving a dead session until the next track, but
     * [RoutingAudioSink.current]'s own liveness check now self-heals that on the very next buffer,
     * without needing the player nudged from outside at all.)
     *
     * Confirmed, reproduced: a track that started playing before [UsbAudioManager.controlState]
     * went non-null stays on [UsbAudioManager.PlaybackRoute.Fallback] for its entire remaining
     * duration even once the DAC is available, with nothing -- not even the Now Playing screen's
     * own status row -- ever prompting the user that anything changed.
     *
     * A plain [Player.seekTo] to the current position is *not* enough to force this, despite being
     * the usual trick for flushing a renderer -- confirmed, reproduced: it repositions the
     * timeline (a real BUFFERING/READY blip shows up) but [RoutingAudioSink.configure] never gets
     * called again, because Media3 only re-invokes it around a genuine stream/period change or
     * decoder output format change, neither of which an intra-period seek to an unchanged format
     * causes. What's actually required -- and confirmed working -- is a full [Player.stop] +
     * [Player.prepare], which fully disables and re-enables the renderer for this item, forcing a
     * fresh [RoutingAudioSink.configure] call same as a cold start of the track would. This is a
     * bigger interruption than a quick flush (a brief rebuffer/reload of the current item, not
     * just a short discontinuity) -- the user explicitly chose this over leaving the upgrade for
     * the next track, aware of that tradeoff.
     */
    private suspend fun forceReconfigureOnDacStateChange() {
        usbAudioManager.controlState
            .map { it != null }
            .distinctUntilChanged()
            .drop(1) // Skip the flow's own current value -- only react to a real transition.
            .filter { ready -> ready } // Only the DAC-becomes-ready direction needs forcing here.
            .collect {
                val currentRoute = usbAudioManager.playbackRoute.value
                val shouldForce =
                    currentRoute is UsbAudioManager.PlaybackRoute.Fallback &&
                        usbSettings.enabled &&
                        usbSettings.bitPerfectMode
                if (!shouldForce) return@collect
                withContext(Dispatchers.Main) {
                    val position = player.currentPosition
                    // Silence immediately -- confirmed, reproduced: setting playWhenReady back
                    // right after prepare() still let HAL audibly keep playing for a bit while
                    // the reload/reconfigure was still in flight underneath, instead of cutting
                    // over cleanly once the USB route was actually ready.
                    player.pause()
                    // Force the progress bar to freeze right now instead of waiting for Media3's
                    // own event dispatch -- confirmed, reproduced: pause()/stop()/seekTo()/
                    // prepare() called back-to-back like this get batched into a single deferred
                    // onEvents callback, so the UI kept extrapolating the position forward off the
                    // *previous*, still-playing Progression for the entirety of the reload, then
                    // visibly snapped back down once that one deferred callback finally landed.
                    playbackManager.ack(this@ExoPlaybackStateHolder, StateAck.ProgressionChanged)
                    player.stop()
                    player.seekTo(position)
                    player.prepare()
                    awaitReady()
                    // Not a `player.isPlaying` snapshot taken before this block started -- confirmed,
                    // reproduced: a replug-then-play landing while this very reconfigure is what's
                    // completing the DAC activation that press is waiting on (see playing()'s own
                    // doc comment) snapshotted false here *before* that press's own coroutine had
                    // set playWhenReady, then stomped its `true` back to `false` moments later,
                    // right after real streaming had already started -- looked like the first press
                    // did nothing. desiredPlaying is the one place that always holds the user's
                    // latest actual intent regardless of which of this function's or playing()'s own
                    // coroutine happens to finish last.
                    player.playWhenReady = desiredPlaying
                }
            }
    }

    /** Suspends until [player] leaves [Player.STATE_BUFFERING], e.g. after a [Player.prepare]. */
    private suspend fun awaitReady() {
        if (player.playbackState != Player.STATE_BUFFERING) return
        suspendCancellableCoroutine { cont ->
            val listener =
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState != Player.STATE_BUFFERING) {
                            player.removeListener(this)
                            cont.resumeWith(Result.success(Unit))
                        }
                    }
                }
            player.addListener(listener)
            cont.invokeOnCancellation { player.removeListener(listener) }
        }
    }

    fun release() {
        saveJob.cancel()
        playbackManager.unregisterStateHolder(this)
        musicRepository.removeUpdateListener(this)
        player.removeListener(this)
        replayGainProcessor.release()
        imageSettings.unregisterListener(this)
        playbackSettings.unregisterListener(this)
        usbSettings.unregisterListener(this)
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
        if (musicRepository.library == null) {
            // No library, cannot do anything.
            return RawQueue(emptyList(), emptyList(), 0)
        }
        val heap = (0 until player.mediaItemCount).map { player.getMediaItemAt(it) }
        val shuffledMapping =
            if (player.shuffleModeEnabled) {
                player.unscrambleQueueIndices()
            } else {
                emptyList()
            }
        return RawQueue(heap.mapNotNull { it.song }, shuffledMapping, player.currentMediaItemIndex)
    }

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
                    // Applying the restored state below makes the player prepare the restored
                    // item, which calls RoutingAudioSink.configure() to buffer ahead -- *whether
                    // or not action.play is true*. Confirmed, reproduced: the fallback AudioTrack
                    // got configured during prepare, ten seconds before the user's own manual
                    // play tap, with the DAC session not ready for another ~50ms after that.
                    // configure() only re-checks on the *next* call (next track), so losing this
                    // race during prepare commits the restored track to fallback regardless of
                    // when (or whether, this session) the user actually presses play. Give a DAC
                    // that's still finishing activation (e.g. right after a cold-launch permission
                    // grant racing this same restore) a bounded chance to be ready first -- see
                    // UsbAudioManager.awaitActivation's doc comment.
                    usbAudioManager.awaitActivation()
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

    /**
     * Confirmed, reproduced: letting this set [Player.playWhenReady] immediately, as it used to,
     * meant a replug-then-play racing a still-in-flight [UsbAudioManager] activation hit
     * [RoutingAudioSink.current]'s own liveness re-check -- called on every single
     * [RoutingAudioSink.getCurrentPositionUs]/[RoutingAudioSink.handleBuffer] from the render
     * thread, many times a second -- which falls through to [UsbAudioManager.resolveActiveSink]'s
     * `runBlocking { awaitActivation() }` fallback on every one of those calls for as long as
     * activation is still running. Repeatedly stalling the render thread like that for the full
     * ~1-2s a DAC reactivation takes let [player]'s own position estimate run ahead of real time
     * during the stall (nothing from the blocked renderer was there to correct it) -- by
     * measured, reproduced numbers, the next [Player.getCurrentPosition] read after a stall like
     * that came back ~6s ahead of only ~1.4s of real elapsed time. [forceReconfigureOnDacStateChange]
     * then captured and [Player.seekTo]'d that already-corrupted value, permanently skipping
     * however much of the track the stall's error happened to add.
     *
     * Awaiting activation *before* ever touching [Player.playWhenReady] -- off the render thread
     * entirely, same [UsbAudioManager.triggerActivation]/[UsbAudioManager.awaitActivation] pair
     * the cold-launch restore path already uses -- means [UsbAudioManager.activeSink] is already
     * resolved by the time the renderer asks, so [RoutingAudioSink.current]'s liveness check
     * resolves instantly instead of blocking. [UsbAudioManager.blockPlayback] (already wired to
     * the play/pause button's disabled/spinner state) covers the user-visible side of the wait for
     * free -- nothing new needed there. [resolveActiveSink]'s own blocking fallback is left in
     * place, not removed: it's still the only thing protecting a *different*, already-fixed race
     * (a cold-launch state restore's [RoutingAudioSink.configure] call landing before that
     * restore's own upfront [UsbAudioManager.awaitActivation] call here would have run).
     *
     * [pendingPlayJob] exists so a rapid play/pause/play doesn't let a stale await from the first
     * tap land [Player.playWhenReady] back to `true` after a later tap explicitly paused.
     */
    override fun playing(playing: Boolean) {
        desiredPlaying = playing
        pendingPlayJob?.cancel()
        if (!playing) {
            player.playWhenReady = false
            return
        }
        pendingPlayJob =
            restoreScope.launch {
                usbAudioManager.triggerActivation()
                usbAudioManager.awaitActivation()
                withContext(Dispatchers.Main) { player.playWhenReady = true }
            }
    }

    /**
     * [UsbDacPreferences.Listener] callback: flipping "Enable USB DAC output" or "Bit-perfect
     * mode" mid-track has no path to re-resolve the current track's route at all -- unlike the
     * DAC appearing/disappearing (see [forceReconfigureOnDacStateChange] and
     * [RoutingAudioSink.current]'s self-heal), nothing here ever changes [UsbAudioManager]'s own
     * state, so there's no signal to hook a live reroute onto. Rather than live-reroute the
     * current track (already tried twice on the library side and reverted both times -- see
     * libusbdac's `work-pending-fixes-and-abx` history -- including once because it left a stale,
     * unflushed sink producing silence while the UI said "playing"), just pause: the user's next
     * tap re-enters [playing] fresh, which already does a clean activation-await before resolving
     * the route.
     */
    override fun onUsbEnabledChanged() {
        if (desiredPlaying) playbackManager.playing(false)
    }

    override fun onBitPerfectModeChanged() {
        if (desiredPlaying) playbackManager.playing(false)
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
        player.setMediaItems(command.queue.map { it.buildMediaItem() })
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
                BetterShuffleOrder(player.mediaItemCount, player.currentMediaItemIndex)
            )
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
                player.currentTimeline.getFirstWindowIndex(player.shuffleModeEnabled),
                C.TIME_UNSET,
            )
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
                    player.currentMediaItemIndex,
                    Player.REPEAT_MODE_OFF,
                    player.shuffleModeEnabled,
                )
            }

        if (nextIndex == C.INDEX_UNSET) {
            player.addMediaItems(songs.map { it.buildMediaItem() })
        } else {
            player.addMediaItems(nextIndex, songs.map { it.buildMediaItem() })
        }
        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun addToQueue(songs: List<Song>, ack: StateAck.AddToQueue) {
        player.addMediaItems(songs.map { it.buildMediaItem() })
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
        ack: StateAck.NewPlayback?,
    ) {
        var sendNewPlaybackEvent = false
        var shouldSeek = false
        if (this.parent != parent) {
            this.parent = parent
            sendNewPlaybackEvent = true
        }
        if (rawQueue != resolveQueue()) {
            // Preserve whatever playWhenReady already was rather than force-pausing -- this path
            // also runs live, mid-playback, whenever the library changes under a still-ongoing
            // session (see IndexingHolder.onMusicChanges' destructive applySavedState call,
            // reached on every re-index, including the one a song deletion triggers). Confirmed,
            // reproduced: deleting any song that was part of the current queue -- not necessarily
            // the one actually playing -- rebuilt the player's media items here and then
            // unconditionally paused, even though nothing about the song actually playing had
            // changed. A real first-ever restore from disk (the only other caller, see
            // DeferredPlayback.RestoreState) still ends up paused here regardless, since nothing
            // has played yet at that point and playWhenReady is reliably false.
            val wasPlaying = player.playWhenReady
            player.setMediaItems(rawQueue.heap.map { it.buildMediaItem() })
            if (rawQueue.isShuffled) {
                player.shuffleModeEnabled = true
                player.setShuffleOrder(BetterShuffleOrder(rawQueue.shuffledMapping.toIntArray()))
            } else {
                player.shuffleModeEnabled = false
            }
            player.seekTo(rawQueue.heapIndex, C.TIME_UNSET)
            player.prepare()
            player.playWhenReady = wasPlaying
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
        player.setMediaItems(listOf())
        playbackManager.ack(this, ack)
        deferSave()
    }

    // --- PLAYER OVERRIDES ---

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)
        L.i(
            "USB DEBUG: onPlayWhenReadyChanged($playWhenReady, reason=$reason) " +
                "[USER_REQUEST=${Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST} " +
                "AUDIO_FOCUS_LOSS=${Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS} " +
                "AUDIO_BECOMING_NOISY=${Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY} " +
                "REMOTE=${Player.PLAY_WHEN_READY_CHANGE_REASON_REMOTE} " +
                "END_OF_MEDIA_ITEM=${Player.PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM}]")

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

        if (!playWhenReady && reason == Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS) {
            // UsbAudioOutput.pause() -- Media3's AudioSink.pause() takes no reason, so it always
            // applies its own grace period (see its doc comment) -- has no way to know this pause
            // wasn't a quick same-app pause/resume but another app taking over outright, where a
            // resume within RELEASE_GRACE_MS is not a realistic expectation. Releasing immediately
            // here as well cancels that pending grace-period release (UsbAudioManager.releaseClaim
            // cancels whichever release is already pending before scheduling or running a new
            // one), or, if this runs first, simply leaves the later grace-period release a no-op
            // once it finds the claim already gone.
            usbAudioManager.releaseClaim(delayMs = 0)
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        L.i(
            "USB DEBUG: onPlaybackStateChanged($playbackState) " +
                "[IDLE=${Player.STATE_IDLE} BUFFERING=${Player.STATE_BUFFERING} " +
                "READY=${Player.STATE_READY} ENDED=${Player.STATE_ENDED}] " +
                "repeatMode=${player.repeatMode} currentPosition=${player.currentPosition} " +
                "duration=${player.duration}")

        if (playbackState == Player.STATE_ENDED && player.repeatMode == Player.REPEAT_MODE_OFF) {
            goto(0)
            player.pause()
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)

        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
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

        if (events.containsAny(Player.EVENT_IS_PLAYING_CHANGED)) {
            if (player.isPlaying) {
                beginPositionSyncUntilAdvancing()
            } else {
                positionSyncJob?.cancel()
            }
        }
    }

    /**
     * Since the USB DAC path's `UsbAudioOutput.play()` stopped blocking the caller for its own
     * clock/volume relock (libusbdac#30), [player].isPlaying flips `true` essentially the instant
     * [Player.play] is called, well before the DAC's real output position starts moving --
     * confirmed on real hardware via logcat: `UsbAudioOutput: play()` to `UsbAudioOutput:
     * streaming thread starting` is a multi-second gap during a genuine relock.
     * [player].currentPosition itself stays correctly frozen for that whole window, but
     * [progression]'s wall-clock extrapolation (anchored off whatever [player].currentPosition
     * was the last time [StateAck.ProgressionChanged] fired) has no way to know that, and runs
     * open-loop until the next discrete player event naturally corrects it -- visible as the seek
     * bar jumping forward for seconds, then snapping back down once real streaming starts.
     *
     * Forces a fresh [StateAck.ProgressionChanged] every 150ms -- short enough that the worst-case
     * fake-advancement window this causes is one tick, not the whole relock -- until
     * [player].currentPosition has genuinely moved away from where it was when this started,
     * confirming real streaming is now underway, then stops itself. Before #30, this never
     * surfaced because [Player.play]'s own blocking call froze ExoPlayer's internal playback
     * thread for the same duration, so `isPlaying=true` was never even observable until real
     * audio had already started.
     */
    private fun beginPositionSyncUntilAdvancing() {
        positionSyncJob?.cancel()
        val anchorPositionMs = player.currentPosition
        positionSyncJob =
            positionSyncScope.launch {
                while (player.currentPosition == anchorPositionMs) {
                    delay(150.milliseconds)
                    playbackManager.ack(this@ExoPlaybackStateHolder, StateAck.ProgressionChanged)
                }
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
            player.repeatMode == Player.REPEAT_MODE_ONE && playbackSettings.pauseOnRepeat
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
            delay(SAVE_BUFFER.milliseconds)
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

    private fun Player.unscrambleQueueIndices(): List<Int> {
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
                        lastMediaItemIndex,
                        Player.REPEAT_MODE_OFF,
                        shuffleModeEnabled,
                    )
                if (lastMediaItemIndex != C.INDEX_UNSET) {
                    queue.add(lastMediaItemIndex)
                }
            }
            if (firstMediaItemIndex != C.INDEX_UNSET) {
                firstMediaItemIndex =
                    timeline.getPreviousWindowIndex(
                        firstMediaItemIndex,
                        Player.REPEAT_MODE_OFF,
                        shuffleModeEnabled,
                    )
                if (firstMediaItemIndex != C.INDEX_UNSET) {
                    queue.add(0, firstMediaItemIndex)
                }
            }
        }

        return queue
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
        private val usbAudioManager: UsbAudioManager,
        private val usbSettings: UsbDacPreferences,
    ) {
        fun create(): ExoPlaybackStateHolder {
            // Since Auxio is a music player, only specify an audio renderer to save
            // battery/apk size/cache size]
            val audioRenderer = RenderersFactory { handler, _, audioListener, _, _ ->
                // A ready UAC3 DAC session takes over output entirely, bypassing AudioTrack/the
                // mixer/ReplayGain processing (no AudioProcessor chain runs on this path yet).
                // RoutingAudioSink defers the USB-vs-default choice to the first configure()
                // call rather than making it here: UsbAudioManager activates its DAC session
                // asynchronously and the player is typically built before that finishes, so
                // checking this early loses the race more often than not. Each renderer gets its
                // own wrapper; only one is ever active for a given track, so only one actually
                // opens the USB stream.
                // Tried listing MediaCodecAudioRenderer first (ahead of FfmpegAudioRenderer) to
                // get real higher-bit-depth PCM for high-res FLAC, since Media3's own FFmpeg
                // decoder extension has a confirmed, current (checked against the live upstream
                // androidx/media repo, not just this project's vendored copy) hard limitation:
                // FfmpegAudioDecoder only ever outputs 16-bit integer or 32-bit float PCM, never
                // a source's real higher bit depth. Reverted: this made ExoPlayer's
                // MediaCodecRenderer decide every FLAC track could skip decoding entirely via its
                // "bypass" path (treating the still-compressed container format as something our
                // sink could accept directly, which it never legitimately can for FLAC) --
                // reproduced, crashed every single track with a NegativeArraySizeException in
                // PcmRingBuffer's size calculation, compounding into the pre-existing
                // onPlayerError unconditional-retry-next-track loop (unplayable library). Getting
                // real 24-bit decode via the platform codec needs a more careful, narrower
                // investigation of why MediaCodecRenderer chose bypass here at all, not just a
                // priority swap -- left as a known follow-up, not attempted again without that.
                arrayOf<BaseRenderer>(
                    FfmpegAudioRenderer(
                        handler,
                        audioListener,
                        RoutingAudioSink(
                            usbAudioManager,
                            DefaultAudioSink.Builder(context)
                                .setAudioProcessors(arrayOf(replayGainProcessor))
                                // Off by default in DefaultAudioSink -- without this,
                                // getFormatSupport() silently rewrites every float-PCM query
                                // back to 16-bit before UsbAudioOutputProvider ever sees it,
                                // which is exactly what made FfmpegAudioRenderer
                                // .shouldOutputFloat() (and MediaCodecAudioRenderer's own
                                // equivalent check just below) always choose 16-bit regardless
                                // of the DAC's real capability -- confirmed, reproduced: a
                                // genuinely 24-bit/96kHz FLAC (ffprobe:
                                // bits_per_raw_sample=24) still got decoded to 16-bit PCM with
                                // this flag left at its default. See DefaultAudioSink
                                // .getFormatSupport()'s own shouldUseFloatOutput() check.
                                .setEnableFloatOutput(true)
                                .build()),
                    ),
                    MediaCodecAudioRenderer(
                        context,
                        MediaCodecSelector.DEFAULT,
                        handler,
                        audioListener,
                        RoutingAudioSink(
                            usbAudioManager,
                            DefaultAudioSink.Builder(context)
                                .setAudioProcessors(arrayOf(replayGainProcessor))
                                // See the matching FfmpegAudioRenderer sink above for why.
                                .setEnableFloatOutput(true)
                                .build()),
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
                usbAudioManager,
                usbSettings,
            )
        }
    }

    private companion object {
        const val SAVE_BUFFER = 5000L
    }
}
