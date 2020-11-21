package org.oxycblt.auxio.library

import android.content.Context
import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.recycler.ShowMode
import org.oxycblt.auxio.recycler.SortMode

class LibraryViewModel : ViewModel() {
    private var mIsNavigating = false
    val isNavigating: Boolean get() = mIsNavigating

    private var mSearchHasFocus = false
    val searchHasFocus: Boolean get() = mSearchHasFocus

    // TODO: Move these to prefs when they're added
    private val mShowMode = MutableLiveData(ShowMode.SHOW_ARTISTS)
    val showMode: LiveData<ShowMode> get() = mShowMode

    private val mSortMode = MutableLiveData(SortMode.ALPHA_DOWN)
    val sortMode: LiveData<SortMode> get() = mSortMode

    private val mSearchResults = MutableLiveData(listOf<BaseModel>())
    val searchResults: LiveData<List<BaseModel>> get() = mSearchResults

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

    fun updateSearchQuery(query: String, context: Context) {
        // Don't bother if the query is blank.
        if (query == "") {
            resetQuery()

            return
        }

        // Search MusicStore for all the items [Artists, Albums, Songs] that contain
        // the query, and update the LiveData with those items. This is done on a separate
        // thread as it can be a very long operation for large music libraries.
        viewModelScope.launch {
            val musicStore = MusicStore.getInstance()
            val combined = mutableListOf<BaseModel>()
            val children = showMode.value!!.getChildren()

            // If the Library ShowMode supports it, include artists / genres in the search.
            if (children.contains(ShowMode.SHOW_GENRES)) {
                val genres = musicStore.genres.filter { it.name.contains(query, true) }

                if (genres.isNotEmpty()) {
                    combined.add(Header(name = context.getString(R.string.label_genres)))
                    combined.addAll(genres)
                }
            }

            if (children.contains(ShowMode.SHOW_ARTISTS)) {
                val artists = musicStore.artists.filter { it.name.contains(query, true) }

                if (artists.isNotEmpty()) {
                    combined.add(Header(name = context.getString(R.string.label_artists)))
                    combined.addAll(artists)
                }
            }

            // Albums & Songs are always included.
            val albums = musicStore.albums.filter { it.name.contains(query, true) }

            if (albums.isNotEmpty()) {
                combined.add(Header(name = context.getString(R.string.label_albums)))
                combined.addAll(albums)
            }

            val songs = musicStore.songs.filter { it.name.contains(query, true) }

            if (songs.isNotEmpty()) {
                combined.add(Header(name = context.getString(R.string.label_songs)))
                combined.addAll(songs)
            }

            mSearchResults.value = combined
        }
    }

    fun resetQuery() {
        mSearchResults.value = listOf()
    }

    fun updateNavigationStatus(value: Boolean) {
        mIsNavigating = value
    }

    fun updateSearchFocusStatus(value: Boolean) {
        mSearchHasFocus = value
    }
}
