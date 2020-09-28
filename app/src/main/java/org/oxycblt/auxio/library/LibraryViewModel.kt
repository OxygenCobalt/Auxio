package org.oxycblt.auxio.library

import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.R
import org.oxycblt.auxio.recycler.SortMode
import org.oxycblt.auxio.theme.SHOW_ARTISTS

class LibraryViewModel : ViewModel() {
    var isAlreadyNavigating = false

    // TODO: Move these to pref values when they're added
    private val mShowMode = MutableLiveData(SHOW_ARTISTS)
    val showMode: LiveData<Int> get() = mShowMode

    private val mSortMode = MutableLiveData(SortMode.ALPHA_UP)
    val sortMode: LiveData<SortMode> get() = mSortMode

    fun updateSortMode(item: MenuItem) {
        val mode = when (item.itemId) {
            R.id.sort_none -> SortMode.NONE
            R.id.sort_alpha_down -> SortMode.ALPHA_DOWN
            R.id.sort_alpha_up -> SortMode.ALPHA_UP

            else -> SortMode.NONE
        }

        if (mode != mSortMode.value) {
            mSortMode.value = mode
        }
    }
}
