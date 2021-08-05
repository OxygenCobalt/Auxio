package org.oxycblt.auxio.widgets

import android.graphics.Bitmap
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.LoopMode

/*
 * A condensed variant of the current playback state, used so that PlaybackStateManager does not
 * need to be queried directly.
 */
data class WidgetState(
    val song: Song,
    val albumArt: Bitmap?,
    val isPlaying: Boolean,
    val isShuffled: Boolean,
    val loopMode: LoopMode,
)
