package org.oxycblt.auxio.search

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.MusicStore

class SearchViewModel : ViewModel() {
    private val mSearchResults = MutableLiveData(listOf<BaseModel>())
    val searchResults: LiveData<List<BaseModel>> get() = mSearchResults

    private var mIsNavigating = false
    val isNavigating: Boolean get() = mIsNavigating

    private val musicStore = MusicStore.getInstance()

    fun doSearch(query: String, context: Context) {
        if (query.isEmpty()) {
            mSearchResults.value = listOf()

            return
        }

        viewModelScope.launch {
            val results = mutableListOf<BaseModel>()

            musicStore.artists.filterByOrNull(query)?.let {
                results.add(Header(id = -1, name = context.getString(R.string.label_artists)))
                results.addAll(it)
            }

            musicStore.albums.filterByOrNull(query)?.let {
                results.add(Header(id = -2, name = context.getString(R.string.label_albums)))
                results.addAll(it)
            }

            musicStore.genres.filterByOrNull(query)?.let {
                results.add(Header(id = -3, name = context.getString(R.string.label_genres)))
                results.addAll(it)
            }

            musicStore.songs.filterByOrNull(query)?.let {
                results.add(Header(id = -4, name = context.getString(R.string.label_songs)))
                results.addAll(it)
            }

            mSearchResults.value = results
        }
    }

    private fun List<BaseModel>.filterByOrNull(value: String): List<BaseModel>? {
        val filtered = filter { it.name.contains(value, ignoreCase = true) }

        return if (filtered.isNotEmpty()) filtered else null
    }

    /**
     * Update the current navigation status
     * @param value Whether LibraryFragment is navigating or not
     */
    fun updateNavigationStatus(value: Boolean) {
        mIsNavigating = value
    }
}
