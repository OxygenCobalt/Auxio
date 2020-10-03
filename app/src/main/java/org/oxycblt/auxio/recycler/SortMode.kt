package org.oxycblt.auxio.recycler

import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song

// Sorting modes
enum class SortMode(val iconRes: Int) {
    // Icons for each mode are assigned to the enums themselves
    NONE(R.drawable.ic_sort_none),
    ALPHA_UP(R.drawable.ic_sort_alpha_up),
    ALPHA_DOWN(R.drawable.ic_sort_alpha_down),
    NUMERIC_UP(R.drawable.ic_sort_numeric_up),
    NUMERIC_DOWN(R.drawable.ic_sort_numeric_down);

    fun getSortedGenreList(list: List<Genre>): List<Genre> {
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
}
