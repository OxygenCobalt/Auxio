package org.oxycblt.auxio.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.recycler.SortMode

/**
 * ViewModel that stores data for the [DetailFragment]s, such as what they're showing & what
 * [SortMode] they are currently on.
 */
class DetailViewModel : ViewModel() {
    private var mIsNavigating = false
    val isNavigating: Boolean get() = mIsNavigating

    private val mGenreSortMode = MutableLiveData(SortMode.ALPHA_DOWN)
    val genreSortMode: LiveData<SortMode> get() = mGenreSortMode

    private val mArtistSortMode = MutableLiveData(SortMode.NUMERIC_DOWN)
    val artistSortMode: LiveData<SortMode> get() = mArtistSortMode

    private val mAlbumSortMode = MutableLiveData(SortMode.NUMERIC_DOWN)
    val albumSortMode: LiveData<SortMode> get() = mAlbumSortMode

    // Current music models being shown
    private val mCurrentGenre = MutableLiveData<Genre?>()
    val currentGenre: LiveData<Genre?> get() = mCurrentGenre

    private val mCurrentArtist = MutableLiveData<Artist?>()
    val currentArtist: LiveData<Artist?> get() = mCurrentArtist

    private val mCurrentAlbum = MutableLiveData<Album?>()
    val currentAlbum: LiveData<Album?> get() = mCurrentAlbum

    // Navigation flags
    private val mNavToItem = MutableLiveData<BaseModel?>()
    val navToItem: LiveData<BaseModel?> get() = mNavToItem

    private val mNavToParent = MutableLiveData<Boolean>()
    val navToParent: LiveData<Boolean> get() = mNavToParent

    private val mNavToChild = MutableLiveData<BaseModel?>()
    val navToChild: LiveData<BaseModel?> get() = mNavToChild

    /**
     * Update the current navigation status
     * @param value Whether the current [DetailFragment] is navigating or not.
     */
    fun updateNavigationStatus(value: Boolean) {
        mIsNavigating = value
    }

    /**
     * Increment the sort mode of the genre artists
     */
    fun incrementGenreSortMode() {
        mGenreSortMode.value = when (mGenreSortMode.value) {
            SortMode.ALPHA_DOWN -> SortMode.ALPHA_UP
            SortMode.ALPHA_UP -> SortMode.ALPHA_DOWN

            else -> SortMode.ALPHA_DOWN
        }
    }

    /**
     * Increment the sort mode of the artist albums
     */
    fun incrementArtistSortMode() {
        mArtistSortMode.value = when (mArtistSortMode.value) {
            SortMode.NUMERIC_DOWN -> SortMode.NUMERIC_UP
            SortMode.NUMERIC_UP -> SortMode.ALPHA_DOWN
            SortMode.ALPHA_DOWN -> SortMode.ALPHA_UP
            SortMode.ALPHA_UP -> SortMode.NUMERIC_DOWN

            else -> SortMode.NUMERIC_DOWN
        }
    }

    /**
     * Increment the sort mode of the album songs
     */
    fun incrementAlbumSortMode() {
        mAlbumSortMode.value = when (mAlbumSortMode.value) {
            SortMode.NUMERIC_DOWN -> SortMode.NUMERIC_UP
            SortMode.NUMERIC_UP -> SortMode.NUMERIC_DOWN

            else -> SortMode.NUMERIC_DOWN
        }
    }

    fun updateGenre(genre: Genre) {
        mCurrentGenre.value = genre
    }

    fun updateArtist(artist: Artist) {
        mCurrentArtist.value = artist
    }

    fun updateAlbum(album: Album) {
        mCurrentAlbum.value = album
    }

    /** Navigate to an item, whether a song/album/artist */
    fun navToItem(item: BaseModel) {
        mNavToItem.value = item
    }

    /** Mark that the navigation process is done. */
    fun doneWithNavToItem() {
        mNavToItem.value = null
    }

    /** Mark that parent navigation should occur */
    fun navToParent() {
        mNavToParent.value = true
    }

    /** Mark that the UI is done with the parent navigation */
    fun doneWithNavToParent() {
        mNavToParent.value = false
    }

    /** Navigate to some child item (Primarily used by GenreDetailFragment) */
    fun navToChild(child: BaseModel) {
        mNavToChild.value = child
    }

    fun doneWithNavToChild() {
        mNavToChild.value = null
    }
}
