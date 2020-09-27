package org.oxycblt.auxio.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.recycler.SortMode

class DetailViewModel : ViewModel() {
    var isAlreadyNavigating = false

    private val mGenreSortMode = MutableLiveData(SortMode.ALPHA_DOWN)
    val genreSortMode: LiveData<SortMode> get() = mGenreSortMode

    private val mArtistSortMode = MutableLiveData(SortMode.NUMERIC_DOWN)
    val artistSortMode: LiveData<SortMode> get() = mArtistSortMode

    private val mAlbumSortMode = MutableLiveData(SortMode.NUMERIC_DOWN)
    val albumSortMode: LiveData<SortMode> get() = mAlbumSortMode

    // Current music models being shown
    // These have placeholder values initially so that they don't have to be checked if they're null
    private val mCurrentGenre = MutableLiveData(
        Genre(id = Long.MIN_VALUE, name = "")
    )
    val currentGenre: LiveData<Genre> get() = mCurrentGenre

    private val mCurrentArtist = MutableLiveData(
        Artist(id = Long.MIN_VALUE, name = "")
    )
    val currentArtist: LiveData<Artist> get() = mCurrentArtist

    private val mCurrentAlbum = MutableLiveData(
        Album(id = Long.MIN_VALUE, name = "", artistName = "")
    )
    val currentAlbum: LiveData<Album> get() = mCurrentAlbum

    private val mNavToParent = MutableLiveData<Boolean>()
    val navToParent: LiveData<Boolean> get() = mNavToParent

    fun updateGenre(genre: Genre) {
        mCurrentGenre.value = genre
    }

    fun updateArtist(artist: Artist) {
        mCurrentArtist.value = artist
    }

    fun updateAlbum(album: Album) {
        mCurrentAlbum.value = album
    }

    fun doNavToParent() {
        mNavToParent.value = true
    }

    fun doneWithNavToParent() {
        mNavToParent.value = false
    }

    fun incrementGenreSortMode() {
        mGenreSortMode.value = when (mGenreSortMode.value) {
            SortMode.ALPHA_DOWN -> SortMode.ALPHA_UP
            SortMode.ALPHA_UP -> SortMode.ALPHA_DOWN

            else -> SortMode.ALPHA_DOWN
        }
    }

    fun incrementArtistSortMode() {
        mArtistSortMode.value = when (mArtistSortMode.value) {
            SortMode.NUMERIC_DOWN -> SortMode.NUMERIC_UP
            SortMode.NUMERIC_UP -> SortMode.ALPHA_DOWN
            SortMode.ALPHA_DOWN -> SortMode.ALPHA_UP
            SortMode.ALPHA_UP -> SortMode.NUMERIC_DOWN

            else -> SortMode.NUMERIC_DOWN
        }
    }

    fun incrementAlbumSortMode() {
        mAlbumSortMode.value = when (mAlbumSortMode.value) {
            SortMode.NUMERIC_DOWN -> SortMode.NUMERIC_UP
            SortMode.NUMERIC_UP -> SortMode.NUMERIC_DOWN

            else -> SortMode.NUMERIC_DOWN
        }
    }
}
