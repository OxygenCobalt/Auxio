package org.oxycblt.auxio.recycler

import androidx.annotation.DrawableRes
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Song

/**
 * An enum for the current sorting mode. Contains helper functions to sort lists based
 * off the given sorting mode.
 * @property iconRes The icon for this [SortMode]
 * @author OxygenCobalt
 */
enum class SortMode(@DrawableRes val iconRes: Int) {
    // Icons for each mode are assigned to the enums themselves
    NONE(R.drawable.ic_sort_none),
    ALPHA_UP(R.drawable.ic_sort_alpha_up),
    ALPHA_DOWN(R.drawable.ic_sort_alpha_down),
    NUMERIC_UP(R.drawable.ic_sort_numeric_up),
    NUMERIC_DOWN(R.drawable.ic_sort_numeric_down);

    fun getSortedArtistList(list: List<Artist>): List<Artist> {
        return when (this) {
            ALPHA_UP -> list.sortedWith(
                compareByDescending(String.CASE_INSENSITIVE_ORDER) { it.name }
            )

            ALPHA_DOWN -> list.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
            )

            else -> list
        }
    }

    fun getSortedAlbumList(list: List<Album>): List<Album> {
        return when (this) {
            ALPHA_UP -> list.sortedWith(
                compareByDescending(String.CASE_INSENSITIVE_ORDER) { it.name }
            )

            ALPHA_DOWN -> list.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
            )

            NUMERIC_UP -> list.sortedBy { it.year }
            NUMERIC_DOWN -> list.sortedByDescending { it.year }

            else -> list
        }
    }

    fun getSortedSongList(list: List<Song>): List<Song> {
        return when (this) {
            ALPHA_UP -> list.sortedWith(
                compareByDescending(String.CASE_INSENSITIVE_ORDER) { it.name }
            )

            ALPHA_DOWN -> list.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
            )

            NUMERIC_UP -> list.sortedWith(compareByDescending { it.track })
            NUMERIC_DOWN -> list.sortedWith(compareBy { it.track })

            else -> list
        }
    }

    fun getSortedBaseModelList(list: List<BaseModel>): List<BaseModel> {
        return when (this) {
            ALPHA_UP -> list.sortedWith(
                compareByDescending(String.CASE_INSENSITIVE_ORDER) { it.name }
            )

            ALPHA_DOWN -> list.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
            )

            NUMERIC_UP -> list.sortedWith(compareByDescending { it.id })
            NUMERIC_DOWN -> list.sortedWith(compareBy { it.id })

            else -> list
        }
    }

    fun toMenuId(): Int {
        return when (this) {
            NONE -> R.id.option_sort_none
            ALPHA_UP -> R.id.option_sort_alpha_up
            ALPHA_DOWN -> R.id.option_sort_alpha_down

            else -> R.id.option_sort_none
        }
    }

    fun toInt(): Int {
        return when (this) {
            NONE -> CONSTANT_NONE
            ALPHA_UP -> CONSTANT_ALPHA_UP
            ALPHA_DOWN -> CONSTANT_ALPHA_DOWN
            NUMERIC_UP -> CONSTANT_NUMERIC_UP
            NUMERIC_DOWN -> CONSTANT_NUMERIC_DOWN
        }
    }

    companion object {
        const val CONSTANT_NONE = 0xA060
        const val CONSTANT_ALPHA_UP = 0xA061
        const val CONSTANT_ALPHA_DOWN = 0xA062
        const val CONSTANT_NUMERIC_UP = 0xA063
        const val CONSTANT_NUMERIC_DOWN = 0xA065

        fun fromInt(value: Int): SortMode? {
            return when (value) {
                CONSTANT_NONE -> NONE
                CONSTANT_ALPHA_UP -> ALPHA_UP
                CONSTANT_ALPHA_DOWN -> ALPHA_DOWN
                CONSTANT_NUMERIC_UP -> NUMERIC_UP
                CONSTANT_NUMERIC_DOWN -> NUMERIC_DOWN

                else -> null
            }
        }
    }
}
