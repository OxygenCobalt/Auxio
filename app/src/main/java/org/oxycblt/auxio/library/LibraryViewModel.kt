package org.oxycblt.auxio.library

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Parent
import org.oxycblt.auxio.recycler.DisplayMode
import org.oxycblt.auxio.recycler.SortMode
import org.oxycblt.auxio.settings.SettingsManager

/**
 * A [ViewModel] that manages what [LibraryFragment] is currently showing, and also the search
 * functionality.
 * @author OxygenCobalt
 */
class LibraryViewModel : ViewModel(), SettingsManager.Callback {
    private val mLibraryData = MutableLiveData(listOf<Parent>())
    val libraryData: LiveData<List<Parent>> get() = mLibraryData

    private var mSortMode = SortMode.ALPHA_DOWN
    val sortMode: SortMode get() = mSortMode

    private var mDisplayMode = DisplayMode.SHOW_ARTISTS

    private var mIsNavigating = false
    val isNavigating: Boolean get() = mIsNavigating

    private val settingsManager = SettingsManager.getInstance()
    private val musicStore = MusicStore.getInstance()

    init {
        settingsManager.addCallback(this)

        // Set up the display/sort modes
        mDisplayMode = settingsManager.libraryDisplayMode
        mSortMode = settingsManager.librarySortMode

        updateLibraryData()
    }

    /**
     * Update the current [SortMode] using an menu [itemId].
     */
    fun updateSortMode(@IdRes itemId: Int) {
        val mode = when (itemId) {
            R.id.option_sort_none -> SortMode.NONE
            R.id.option_sort_alpha_down -> SortMode.ALPHA_DOWN
            R.id.option_sort_alpha_up -> SortMode.ALPHA_UP

            else -> SortMode.NONE
        }

        if (mode != mSortMode) {
            mSortMode = mode
            settingsManager.librarySortMode = mode

            updateLibraryData()
        }
    }

    /**
     * Update the current navigation status
     */
    fun updateNavigationStatus(isNavigating: Boolean) {
        mIsNavigating = isNavigating
    }

    /**
     * Shortcut function for updating the library data with the current [SortMode]/[DisplayMode]
     */
    private fun updateLibraryData() {
        mLibraryData.value = when (mDisplayMode) {
            DisplayMode.SHOW_GENRES -> mSortMode.getSortedGenreList(musicStore.genres)

            DisplayMode.SHOW_ARTISTS -> mSortMode.getSortedArtistList(musicStore.artists)

            DisplayMode.SHOW_ALBUMS -> mSortMode.getSortedAlbumList(musicStore.albums)

            else -> error("DisplayMode $mDisplayMode is unsupported.")
        }
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
}
