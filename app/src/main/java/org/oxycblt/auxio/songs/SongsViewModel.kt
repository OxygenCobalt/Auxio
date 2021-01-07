package org.oxycblt.auxio.songs

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

class SongsViewModel : ViewModel() {
    private val mSearchResults = MutableLiveData(listOf<BaseModel>())
    val searchResults: LiveData<List<BaseModel>> get() = mSearchResults

    private val musicStore = MusicStore.getInstance()

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
            val songs = mutableListOf<BaseModel>().also { list ->
                list.addAll(
                    musicStore.songs.filter {
                        it.name.contains(query, true)
                    }.toMutableList()
                )
            }

            if (songs.isNotEmpty()) {
                songs.add(0, Header(id = 0, name = context.getString(R.string.label_songs)))
            }

            mSearchResults.value = songs
        }
    }

    fun resetQuery() {
        mSearchResults.value = listOf()
    }
}
