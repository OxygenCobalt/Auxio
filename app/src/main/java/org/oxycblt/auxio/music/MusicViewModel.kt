package org.oxycblt.auxio.music

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.processing.MusicLoader
import org.oxycblt.auxio.music.processing.MusicLoaderResponse
import org.oxycblt.auxio.music.processing.MusicSorter

// ViewModel for music storage.
// TODO: Move genre usage to songs [If there's a way to find songs without a genre]
class MusicViewModel(private val app: Application) : ViewModel() {

    // Coroutine
    private val loadingJob = Job()
    private val ioScope = CoroutineScope(
        loadingJob + Dispatchers.IO
    )

    // Values
    private val mGenres = MutableLiveData<List<Genre>>()
    val genres: LiveData<List<Genre>> get() = mGenres

    private val mArtists = MutableLiveData<List<Artist>>()
    val artists: LiveData<List<Artist>> get() = mArtists

    private val mAlbums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> get() = mAlbums

    private val mSongs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> get() = mSongs

    private val mResponse = MutableLiveData<MusicLoaderResponse>()
    val response: LiveData<MusicLoaderResponse> get() = mResponse

    // UI control
    private val mRedo = MutableLiveData<Boolean>()
    val doReload: LiveData<Boolean> get() = mRedo

    private val mDoGrant = MutableLiveData<Boolean>()
    val doGrant: LiveData<Boolean> get() = mDoGrant

    private var started = false

    // Start the music loading sequence.
    // This should only be ran once, use reload() for all other loads.
    fun go() {
        if (!started) {
            started = true
            doLoad()
        }
    }

    private fun doLoad() {
        Log.i(this::class.simpleName, "Starting initial music load...")

        ioScope.launch {
            val start = System.currentTimeMillis()

            // Get the placeholder strings, which are used by MusicLoader & MusicSorter for
            // any music that doesn't have metadata.
            val genrePlaceholder = app.getString(R.string.placeholder_genre)
            val artistPlaceholder = app.getString(R.string.placeholder_artist)
            val albumPlaceholder = app.getString(R.string.placeholder_album)

            val loader = MusicLoader(
                app.contentResolver,

                genrePlaceholder,
                artistPlaceholder,
                albumPlaceholder
            )

            withContext(Dispatchers.Main) {
                if (loader.response == MusicLoaderResponse.DONE) {
                    // If the loading succeeds, then sort the songs and update the value
                    val sorter = MusicSorter(
                        loader.genres,
                        loader.artists,
                        loader.albums,
                        loader.songs,

                        genrePlaceholder,
                        artistPlaceholder,
                        albumPlaceholder
                    )

                    mSongs.value = sorter.songs.toList()
                    mAlbums.value = sorter.albums.toList()
                    mArtists.value = sorter.artists.toList()
                    mGenres.value = sorter.genres.toList()
                }

                mResponse.value = loader.response

                val elapsed = System.currentTimeMillis() - start

                Log.i(
                    this::class.simpleName,
                    "Music load completed successfully in ${elapsed}ms."
                )
            }
        }
    }

    // UI communication functions
    // LoadingFragment uses these so that button presses can update the ViewModel.
    // all doneWithX functions are to reset the value so that LoadingFragment doesn't
    // repeat commands if the view is recreated.
    fun reload() {
        mRedo.value = true

        doLoad()
    }

    fun doneWithReload() {
        mRedo.value = false
    }

    fun grant() {
        mDoGrant.value = true
    }

    fun doneWithGrant() {
        mDoGrant.value = false
    }

    override fun onCleared() {
        super.onCleared()

        // Cancel the current loading job if the app has been stopped
        loadingJob.cancel()
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MusicViewModel::class.java)) {
                return MusicViewModel(application) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class.")
        }
    }
}
