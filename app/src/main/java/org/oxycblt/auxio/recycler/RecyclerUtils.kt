package org.oxycblt.auxio.recycler

import androidx.recyclerview.widget.DiffUtil
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.models.Album

// RecyclerView click listener
class ClickListener<T>(val onClick: (T) -> Unit)

// Diff callback
class DiffCallback : DiffUtil.ItemCallback<Album>() {
    override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
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
    }
}
