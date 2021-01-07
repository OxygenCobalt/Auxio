package org.oxycblt.auxio.recycler

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import org.oxycblt.auxio.R

/**
 * An enum for determining what items to show in a given list.
 * @author OxygenCobalt
 */
enum class DisplayMode(@DrawableRes val iconRes: Int) {
    SHOW_ALL(R.drawable.ic_sort_none),
    SHOW_GENRES(R.drawable.ic_genre),
    SHOW_ARTISTS(R.drawable.ic_artist),
    SHOW_ALBUMS(R.drawable.ic_album);

    /**
     * Get a menu action for this show mode. Corresponds to filter actions.
     */
    @IdRes
    fun toMenuId(): Int {
        return when (this) {
            SHOW_ALL -> (R.id.option_filter_all)
            SHOW_ALBUMS -> (R.id.option_filter_albums)
            SHOW_ARTISTS -> (R.id.option_filter_artists)
            SHOW_GENRES -> (R.id.option_filter_genres)
        }
    }

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
