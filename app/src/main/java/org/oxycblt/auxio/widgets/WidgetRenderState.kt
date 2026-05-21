package org.oxycblt.auxio.widgets

import androidx.annotation.DrawableRes
import org.oxycblt.auxio.R

sealed class WidgetRenderState {
    data object NoSession : WidgetRenderState()

    data class Active(
        val title: String,
        val artist: String,
        val album: String?,
        val isPlaying: Boolean,
        val hasArtwork: Boolean,
    ) : WidgetRenderState()

    companion object {
        fun fromPlayback(
            title: String?,
            artist: String?,
            album: String?,
            isPlaying: Boolean,
            hasArtwork: Boolean,
        ): WidgetRenderState {
            if (title.isNullOrBlank()) return NoSession
            return Active(
                title = title,
                artist = artist.orEmpty(),
                album = album,
                isPlaying = isPlaying,
                hasArtwork = hasArtwork,
            )
        }

        @DrawableRes
        fun playPauseIcon(isPlaying: Boolean): Int = if (isPlaying) R.drawable.ic_pause_24 else R.drawable.ic_play_24
    }
}
