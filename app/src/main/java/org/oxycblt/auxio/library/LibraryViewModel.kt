package org.oxycblt.auxio.library

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.recycler.DisplayMode
import org.oxycblt.auxio.recycler.SortMode
import org.oxycblt.auxio.settings.SettingsManager

/**
 * A [ViewModel] that manages what [LibraryFragment] is currently showing, and also the search
 * functionality.
 * @author OxygenCobalt
 */
class LibraryViewModel : ViewModel(), SettingsManager.Callback {
    private val mSortMode = MutableLiveData(SortMode.ALPHA_DOWN)
    val sortMode: LiveData<SortMode> get() = mSortMode

    private val mLibraryData = MutableLiveData(listOf<BaseModel>())
    val libraryData: LiveData<List<BaseModel>> get() = mLibraryData

    private var mIsNavigating = false
    val isNavigating: Boolean get() = mIsNavigating

    private var mDisplayMode = DisplayMode.SHOW_ARTISTS

    private val settingsManager = SettingsManager.getInstance()
    private val musicStore = MusicStore.getInstance()

    init {
        settingsManager.addCallback(this)

        // Set up the display/sort modes
        mDisplayMode = settingsManager.libraryDisplayMode
        mSortMode.value = settingsManager.librarySortMode

        updateLibraryData()
    }

    /**
     * Update the current [SortMode] with a menu id.
     * @param itemId The id of the menu item selected.
     */
    fun updateSortMode(@IdRes itemId: Int) {
        val mode = when (itemId) {
            R.id.option_sort_none -> SortMode.NONE
            R.id.option_sort_alpha_down -> SortMode.ALPHA_DOWN
            R.id.option_sort_alpha_up -> SortMode.ALPHA_UP

            else -> SortMode.NONE
        }

        if (mode != mSortMode.value) {
            mSortMode.value = mode
            settingsManager.librarySortMode = mode

            updateLibraryData()
        }
    }

    /**
     * Update the current navigation status
     * @param value Whether LibraryFragment is navigating or not
     */
    fun updateNavigationStatus(value: Boolean) {
        mIsNavigating = value
    }

    // --- OVERRIDES ---

    override fun onCleared() {
        super.onCleared()

        settingsManager.removeCallback(this)
    }

    override fun onLibDisplayModeUpdate(displayMode: DisplayMode) {
        mDisplayMode = displayMode

        updateLibraryData()
    }

    // --- UTILS ---

    /**
     * Shortcut function for updating the library data with the current [SortMode]/[DisplayMode]
     */
    private fun updateLibraryData() {
        mLibraryData.value = when (mDisplayMode) {
            DisplayMode.SHOW_GENRES -> {
                mSortMode.value!!.getSortedGenreList(musicStore.genres)
            }

            DisplayMode.SHOW_ARTISTS -> {
                mSortMode.value!!.getSortedBaseModelList(musicStore.artists)
            }

            DisplayMode.SHOW_ALBUMS -> {
                mSortMode.value!!.getSortedAlbumList(musicStore.albums)
            }

            else -> error("DisplayMode $mDisplayMode is unsupported.")
        }
    }
}
