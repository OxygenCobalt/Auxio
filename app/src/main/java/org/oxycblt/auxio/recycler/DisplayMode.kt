package org.oxycblt.auxio.recycler

import androidx.annotation.DrawableRes
import org.oxycblt.auxio.R

/**
 * An enum for determining what items to show in a given list.
 * @author OxygenCobalt
 */
enum class DisplayMode(@DrawableRes val iconRes: Int) {
    SHOW_ALL(R.drawable.ic_sort_none),
    SHOW_GENRES(R.drawable.ic_genre),
    SHOW_ARTISTS(R.drawable.ic_artist),
    SHOW_ALBUMS(R.drawable.ic_album),
    SHOW_SONGS(R.drawable.ic_song);

    companion object {
        /**
         * A valueOf wrapper that will return a default value if given a null/invalid string.
         */
        fun valueOfOrFallback(value: String?): DisplayMode {
            if (value == null) {
                return SHOW_ARTISTS
            }

            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                SHOW_ARTISTS
            }
        }
    }
}
