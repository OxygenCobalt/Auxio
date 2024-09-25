package org.oxycblt.auxio.playback.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.RepeatMode
import androidx.media3.exoplayer.ExoPlayer

interface Queuer {
    val currentMediaItem: MediaItem?
    val currentMediaItemIndex: Int
    val shuffleModeEnabled: Boolean

    @get:RepeatMode var repeatMode: Int

    fun attach()
    fun release()

    fun goto(mediaItemIndex: Int)

    fun seekToNext()
    fun hasNextMediaItem(): Boolean
    fun seekToPrevious()
    fun seekToPreviousMediaItem()
    fun hasPreviousMediaItem(): Boolean

    fun moveMediaItem(fromIndex: Int, toIndex: Int)
    fun removeMediaItem(index: Int)

    // EXTENSIONS
    fun computeHeap(): List<MediaItem>
    fun computeMapping(): List<Int>
    fun computeFirstMediaItemIndex(): Int

    fun prepareNew(mediaItems: List<MediaItem>, startIndex: Int?, shuffled: Boolean)
    fun prepareSaved(mediaItems: List<MediaItem>, mapping: List<Int>, index: Int, shuffled: Boolean)
    fun discard()

    fun addTopMediaItems(mediaItems: List<MediaItem>)
    fun addBottomMediaItems(mediaItems: List<MediaItem>)
    fun shuffled(shuffled: Boolean)

    interface Listener {
        fun onAutoTransition()
    }

    interface Factory {
        fun create(exoPlayer: ExoPlayer, listener: Listener): Queuer
    }
}