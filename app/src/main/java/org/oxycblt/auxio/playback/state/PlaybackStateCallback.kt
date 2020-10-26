package org.oxycblt.auxio.playback.state

import org.oxycblt.auxio.music.Song

interface PlaybackStateCallback {
    fun onSongUpdate(song: Song?) {}
    fun onPositionUpdate(position: Long) {}
    fun onQueueUpdate(queue: MutableList<Song>) {}
    fun onPlayingUpdate(isPlaying: Boolean) {}
    fun onShuffleUpdate(isShuffling: Boolean) {}
    fun onIndexUpdate(index: Int) {}
}
