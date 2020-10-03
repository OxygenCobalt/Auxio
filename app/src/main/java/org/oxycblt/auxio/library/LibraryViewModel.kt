package org.oxycblt.auxio.library

import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.R
import org.oxycblt.auxio.recycler.SortMode
import org.oxycblt.auxio.theme.SHOW_ARTISTS

class LibraryViewModel() : ViewModel() {
    // TODO: Move these to prefs when they're added
    private val mShowMode = MutableLiveData(SHOW_ARTISTS)
    val showMode: LiveData<Int> get() = mShowMode

    private val mSortMode = MutableLiveData(SortMode.ALPHA_DOWN)
    val sortMode: LiveData<SortMode> get() = mSortMode

    private val mSearchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> get() = mSearchQuery

    fun updateSortMode(item: MenuItem) {
        val mode = when (item.itemId) {
            R.id.option_sort_none -> SortMode.NONE
            R.id.option_sort_alpha_down -> SortMode.ALPHA_DOWN
            R.id.option_sort_alpha_up -> SortMode.ALPHA_UP

            else -> SortMode.NONE
        }

        if (mode != mSortMode.value) {
            mSortMode.value = mode
        }
    }

    fun updateSearchQuery(query: String) {
        mSearchQuery.value = query
    }
}
