package org.oxycblt.auxio.playback.player

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.RepeatMode
import androidx.media3.exoplayer.ExoPlayer
import org.oxycblt.auxio.playback.PlaybackSettings
import javax.inject.Inject

/**
 *
 */
class GaplessQueuer private constructor(private val exoPlayer: ExoPlayer, private val listener: Queuer.Listener, private val playbackSettings: PlaybackSettings) : Queuer, PlaybackSettings.Listener, Player.Listener {
    class Factory @Inject constructor(private val playbackSettings: PlaybackSettings) : Queuer.Factory {
        override fun create(exoPlayer: ExoPlayer, listener: Queuer.Listener) = GaplessQueuer(exoPlayer, listener, playbackSettings)
    }

    override val currentMediaItem: MediaItem? = exoPlayer.currentMediaItem
    override val currentMediaItemIndex: Int = exoPlayer.currentMediaItemIndex
    override val shuffleModeEnabled: Boolean = exoPlayer.shuffleModeEnabled
    @get:RepeatMode
    override var repeatMode: Int = exoPlayer.repeatMode
        set(value) {
            field = value
            exoPlayer.repeatMode = value
            updatePauseOnRepeat()
        }

    override fun attach() {
        playbackSettings.registerListener(this)
        exoPlayer.addListener(this)
    }

    override fun release() {
        playbackSettings.unregisterListener(this)
        exoPlayer.removeListener(this)
    }

    override fun computeHeap(): List<MediaItem> {
        return (0 until exoPlayer.mediaItemCount).map { exoPlayer.getMediaItemAt(it) }
    }

    override fun computeMapping(): List<Int> {
        val timeline = exoPlayer.currentTimeline
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
        val shuffleModeEnabled = exoPlayer.shuffleModeEnabled
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

    override fun computeFirstMediaItemIndex() =
        exoPlayer.currentTimeline.getFirstWindowIndex(exoPlayer.shuffleModeEnabled)

    override fun goto(mediaItemIndex: Int) = exoPlayer.seekTo(mediaItemIndex, C.TIME_UNSET)

    override fun seekToNext() = exoPlayer.seekToNext()
    override fun hasNextMediaItem() = exoPlayer.hasNextMediaItem()
    override fun seekToPrevious() = exoPlayer.seekToPrevious()
    override fun seekToPreviousMediaItem() = exoPlayer.seekToPreviousMediaItem()
    override fun hasPreviousMediaItem() = exoPlayer.hasPreviousMediaItem()

    override fun prepareNew(mediaItems: List<MediaItem>, startIndex: Int?, shuffled: Boolean) {
        exoPlayer.shuffleModeEnabled = shuffled
        exoPlayer.setMediaItems(mediaItems)
        if (shuffled) {
            exoPlayer.setShuffleOrder(BetterShuffleOrder(mediaItems.size, startIndex ?: -1))
        }
        val target = startIndex ?: exoPlayer.currentTimeline.getFirstWindowIndex(shuffled)
        exoPlayer.seekTo(target, C.TIME_UNSET)
        exoPlayer.prepare()
    }

    override fun prepareSaved(mediaItems: List<MediaItem>, mapping: List<Int>, index: Int, shuffled: Boolean) {
        exoPlayer.setMediaItems(mediaItems)
        if (shuffled) {
            exoPlayer.shuffleModeEnabled = true
            exoPlayer.setShuffleOrder(BetterShuffleOrder(mapping.toIntArray()))
        } else {
            exoPlayer.shuffleModeEnabled = false
        }
        exoPlayer.seekTo(index, C.TIME_UNSET)
        exoPlayer.prepare()
    }

    override fun discard() {
        exoPlayer.setMediaItems(emptyList())
    }

    override fun addTopMediaItems(mediaItems: List<MediaItem>) {
        val currTimeline = exoPlayer.currentTimeline
        val nextIndex =
            if (currTimeline.isEmpty) {
                C.INDEX_UNSET
            } else {
                currTimeline.getNextWindowIndex(
                    exoPlayer.currentMediaItemIndex, Player.REPEAT_MODE_OFF, exoPlayer.shuffleModeEnabled)
            }

        if (nextIndex == C.INDEX_UNSET) {
            exoPlayer.addMediaItems(mediaItems)
        } else {
            exoPlayer.addMediaItems(nextIndex, mediaItems)
        }
    }

    override fun addBottomMediaItems(mediaItems: List<MediaItem>) {
        exoPlayer.addMediaItems(mediaItems)
    }

    override fun moveMediaItem(fromIndex: Int, toIndex: Int) {
        // ExoPlayer does not actually update it's ShuffleOrder when moving items. Retain a
        // semblance of "normalcy" by doing a weird no-op swap that actually moves the item.
        when {
            fromIndex > toIndex -> {
                exoPlayer.moveMediaItem(fromIndex, toIndex)
                exoPlayer.moveMediaItem(toIndex + 1, fromIndex)
            }
            toIndex > fromIndex -> {
                exoPlayer.moveMediaItem(fromIndex, toIndex)
                exoPlayer.moveMediaItem(toIndex - 1, fromIndex)
            }
        }
    }

    override fun removeMediaItem(index: Int) = exoPlayer.removeMediaItem(index)

    override fun shuffled(shuffled: Boolean) {
        exoPlayer.setShuffleModeEnabled(shuffled)
        if (exoPlayer.shuffleModeEnabled) {
            // Have to manually refresh the shuffle seed and anchor it to the new current songs
            exoPlayer.setShuffleOrder(
                BetterShuffleOrder(exoPlayer.mediaItemCount, exoPlayer.currentMediaItemIndex)
            )
        }
    }

    override fun onPauseOnRepeatChanged() {
        super.onPauseOnRepeatChanged()
        updatePauseOnRepeat()
    }

    private fun updatePauseOnRepeat() {
        exoPlayer.pauseAtEndOfMediaItems =
            exoPlayer.repeatMode == Player.REPEAT_MODE_ONE && playbackSettings.pauseOnRepeat
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

        if (playbackState == Player.STATE_ENDED && exoPlayer.repeatMode == Player.REPEAT_MODE_OFF) {
            goto(0)
            exoPlayer.pause()
        }
    }
}