package org.oxycblt.auxio.recycler

import androidx.recyclerview.widget.DiffUtil
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.music.models.Song

// RecyclerView click listener
class ClickListener<T>(val onClick: (T) -> Unit)

// Diff callback for albums
class AlbumDiffCallback : DiffUtil.ItemCallback<Album>() {
    override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
        return oldItem == newItem
    }
}

// Diff callback for songs
class SongDiffCallback : DiffUtil.ItemCallback<Song>() {
    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem == newItem
    }
}

// Sorting modes
enum class SortMode(val iconRes: Int) {
    // Icons for each mode are assigned to the enums themselves
    NONE(R.drawable.ic_sort_alpha_down),
    ALPHA_UP(R.drawable.ic_sort_alpha_up),
    ALPHA_DOWN(R.drawable.ic_sort_alpha_down),
    NUMERIC_UP(R.drawable.ic_sort_numeric_up),
    NUMERIC_DOWN(R.drawable.ic_sort_numeric_down);

    companion object {
        // Sort comparators are different for each music model, so they are
        // static maps instead.
        val albumSortComparators = mapOf<SortMode, Comparator<Album>>(
            NUMERIC_DOWN to compareByDescending { it.year },
            NUMERIC_UP to compareBy { it.year },

            // Alphabetic sorting needs to be case-insensitive
            ALPHA_DOWN to compareByDescending(
                String.CASE_INSENSITIVE_ORDER
            ) { it.name },
            ALPHA_UP to compareBy(
                String.CASE_INSENSITIVE_ORDER
            ) { it.name },
        )

        val songSortComparators = mapOf<SortMode, Comparator<Song>>(
            NUMERIC_DOWN to compareBy { it.track },
            NUMERIC_UP to compareByDescending { it.track }
        )
    }
}
