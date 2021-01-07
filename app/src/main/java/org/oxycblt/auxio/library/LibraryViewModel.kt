package org.oxycblt.auxio.library

import android.content.Context
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Header
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
    private val mLibraryData = MutableLiveData(listOf<BaseModel>())
    private val mSearchResults = MutableLiveData(listOf<BaseModel>())
    private var mDisplayMode = DisplayMode.SHOW_ARTISTS
    private var mIsNavigating = false

    val sortMode: LiveData<SortMode> get() = mSortMode
    val libraryData: LiveData<List<BaseModel>> get() = mLibraryData
    val searchResults: LiveData<List<BaseModel>> get() = mSearchResults
    val isNavigating: Boolean get() = mIsNavigating

    private val settingsManager = SettingsManager.getInstance()
    private val musicStore = MusicStore.getInstance()

    init {
        settingsManager.addCallback(this)

        // Set up the display/sort modes
        mDisplayMode = settingsManager.libraryDisplayMode
        mSortMode.value = settingsManager.librarySortMode

        updateLibraryData()
    }

    // --- SEARCH FUNCTIONS ---

    /**
     * Perform a search of the music library, given a query.
     * Results are pushed to [searchResults].
     * @param query The query for this search
     * @param context The context needed to create the header text
     */
    fun doSearch(query: String, context: Context) {
        // Don't bother if the query is blank.
        if (query == "") {
            resetQuery()

            return
        }

        viewModelScope.launch {
            val combined = mutableListOf<BaseModel>()

            // Searching is done in a different order depending on which items are being shown
            // E.G If albums are being shown, then they will be the first items on the list.
            when (mDisplayMode) {
                DisplayMode.SHOW_GENRES -> {
                    searchForGenres(combined, query, context)
                    searchForArtists(combined, query, context)
                    searchForAlbums(combined, query, context)
                }

                DisplayMode.SHOW_ARTISTS -> {
                    searchForArtists(combined, query, context) -
                        searchForAlbums(combined, query, context)
                    searchForGenres(combined, query, context)
                }

                DisplayMode.SHOW_ALBUMS -> {
                    searchForAlbums(combined, query, context)
                    searchForArtists(combined, query, context)
                    searchForGenres(combined, query, context)
                }
            }

            mSearchResults.value = combined
        }
    }

    private fun searchForGenres(
        data: MutableList<BaseModel>,
        query: String,
        context: Context
    ): MutableList<BaseModel> {
        val genres = musicStore.genres.filter { it.name.contains(query, true) }

        if (genres.isNotEmpty()) {
            data.add(Header(id = 0, name = context.getString(R.string.label_genres)))
            data.addAll(genres)
        }

        return data
    }

    private fun searchForArtists(
        data: MutableList<BaseModel>,
        query: String,
        context: Context
    ): MutableList<BaseModel> {
        val artists = musicStore.artists.filter { it.name.contains(query, true) }

        if (artists.isNotEmpty()) {
            data.add(Header(id = 1, name = context.getString(R.string.label_artists)))
            data.addAll(artists)
        }

        return data
    }

    private fun searchForAlbums(
        data: MutableList<BaseModel>,
        query: String,
        context: Context
    ): MutableList<BaseModel> {
        val albums = musicStore.albums.filter { it.name.contains(query, true) }

        if (albums.isNotEmpty()) {
            data.add(Header(id = 2, name = context.getString(R.string.label_albums)))
            data.addAll(albums)
        }

        return data
    }

    fun resetQuery() {
        mSearchResults.value = listOf()
    }

    // --- LIBRARY FUNCTIONS ---

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
    @Suppress("UNCHECKED_CAST")
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
        }
    }
}
