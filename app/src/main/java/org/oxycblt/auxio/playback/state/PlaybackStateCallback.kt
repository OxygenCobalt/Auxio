package org.oxycblt.auxio.playback.state

import org.oxycblt.auxio.music.Song

interface PlaybackStateCallback {
    fun onSongUpdate(song: Song?) {}
    fun onPositionUpdate(position: Long) {}
    fun onQueueUpdate(queue: MutableList<Song>) {}
    fun onModeUpdate(mode: PlaybackMode) {}
    fun onIndexUpdate(index: Int) {}
    fun onPlayingUpdate(isPlaying: Boolean) {}
    fun onShuffleUpdate(isShuffling: Boolean) {}
    fun onLoopUpdate(mode: LoopMode) {}

    // Service callbacks
    fun onSeekConfirm(position: Long) {}
}
