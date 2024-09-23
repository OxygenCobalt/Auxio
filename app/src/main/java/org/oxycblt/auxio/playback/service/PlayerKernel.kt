package org.oxycblt.auxio.playback.service

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.RawQueue
import org.oxycblt.auxio.playback.state.RepeatMode

interface PlayerKernel {
    // REPLICAS
    val isPlaying: Boolean
    var playWhenReady: Boolean
    val currentPosition: Long
    @get:Player.RepeatMode var repeatMode: Int
    val audioSessionId: Int
    val currentMediaItem: MediaItem?
    val currentMediaItemIndex: Int
    val shuffleModeEnabled: Boolean

    fun addListener(player: Player.Listener)
    fun removeListener(player: Player.Listener)
    fun release()

    fun play()
    fun pause()
    fun seekTo(positionMs: Long)
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
}

