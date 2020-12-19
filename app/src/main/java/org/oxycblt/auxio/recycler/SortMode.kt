package org.oxycblt.auxio.recycler

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
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

    /**
     * Get a sorted list of artists for a SortMode. Only supports alphabetic sorting.
     * @param artists An unsorted list of artists.
     * @return The sorted list of artists.
     */
    fun getSortedArtistList(artists: List<Artist>): List<Artist> {
        return when (this) {
            ALPHA_UP -> artists.sortedWith(
                compareByDescending(String.CASE_INSENSITIVE_ORDER) { it.name }
            )

            ALPHA_DOWN -> artists.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
            )

            else -> artists
        }
    }

    /**
     * Get a sorted list of albums for a SortMode. Supports alpha + numeric sorting.
     * @param albums An unsorted list of albums.
     * @return The sorted list of albums.
     */
    fun getSortedAlbumList(albums: List<Album>): List<Album> {
        return when (this) {
            ALPHA_UP -> albums.sortedWith(
                compareByDescending(String.CASE_INSENSITIVE_ORDER) { it.name }
            )

            ALPHA_DOWN -> albums.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
            )

            NUMERIC_UP -> albums.sortedBy { it.year }
            NUMERIC_DOWN -> albums.sortedByDescending { it.year }

            else -> albums
        }
    }

    /**
     * Get a sorted list of songs for a SortMode. Supports alpha + numeric sorting.
     * @param songs An unsorted list of songs.
     * @return The sorted list of songs.
     */
    fun getSortedSongList(songs: List<Song>): List<Song> {
        return when (this) {
            ALPHA_UP -> songs.sortedWith(
                compareByDescending(String.CASE_INSENSITIVE_ORDER) { it.name }
            )

            ALPHA_DOWN -> songs.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
            )

            NUMERIC_UP -> songs.sortedWith(compareByDescending { it.track })
            NUMERIC_DOWN -> songs.sortedWith(compareBy { it.track })

            else -> songs
        }
    }

    /**
     * Get a sorted list of BaseModels. Supports alpha + numeric sorting.
     * @param baseModels An unsorted list of BaseModels.
     * @return The sorted list of BaseModels.
     */
    fun getSortedBaseModelList(baseModels: List<BaseModel>): List<BaseModel> {
        return when (this) {
            ALPHA_UP -> baseModels.sortedWith(
                compareByDescending(String.CASE_INSENSITIVE_ORDER) { it.name }
            )

            ALPHA_DOWN -> baseModels.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
            )

            NUMERIC_UP -> baseModels.sortedWith(compareByDescending { it.id })
            NUMERIC_DOWN -> baseModels.sortedWith(compareBy { it.id })

            else -> baseModels
        }
    }

    /**
     * Get a sorting menu ID for this mode. Alphabetic only.
     * @return The action id for this mode.
     */
    @IdRes
    fun toMenuId(): Int {
        return when (this) {
            NONE -> R.id.option_sort_none
            ALPHA_UP -> R.id.option_sort_alpha_up
            ALPHA_DOWN -> R.id.option_sort_alpha_down

            else -> R.id.option_sort_none
        }
    }

    /**
     * Get the constant for this mode. Used to write a compressed variant to SettingsManager
     * @return The int constant for this mode.
     */
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

        /**
         * Get an enum for an int constant
         * @return The [SortMode] if the constant is valid, null otherwise.
         */
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
