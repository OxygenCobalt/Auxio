package org.oxycblt.auxio.music

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.music.models.Artist
import org.oxycblt.auxio.music.models.Song

// Storage for music data.
class MusicRepository {

    private val mArtists = MutableLiveData<List<Artist>>()
    private var mAlbums = MutableLiveData<List<Album>>()
    private var mSongs = MutableLiveData<List<Song>>()

    val artists: LiveData<List<Artist>> get() = mArtists
    val albums: LiveData<List<Album>> get() = mAlbums
    val songs: LiveData<List<Song>> get() = mSongs

    suspend fun init(app: Application): MusicLoaderResponse {
        Log.i(this::class.simpleName, "Starting initial music load")

        val loader = MusicLoader(app)

        if (loader.response == MusicLoaderResponse.DONE) {
            // If the loading succeeds, then process the songs into lists of
            // songs, albums, and artists on the main thread.
            withContext(Dispatchers.Main) {
                val sorter = MusicSorter(
                    loader.artists,
                    loader.albums,
                    loader.songs
                )

                mSongs.value = sorter.songs
                mAlbums.value = sorter.albums
                mArtists.value = sorter.artists

                Log.i(this::class.simpleName, "Finished initial music load.")
            }
        }

        return loader.response
    }

    companion object {
        @Volatile
        private var INSTANCE: MusicRepository? = null

        fun getInstance(): MusicRepository {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                Log.d(
                    this::class.simpleName,
                    "Passed an existing instance of MusicRepository."
                )

                return tempInstance
            }

            synchronized(this) {
                val newInstance = MusicRepository()
                INSTANCE = newInstance

                Log.d(
                    this::class.simpleName,
                    "Created an instance of MusicRepository."
                )

                return newInstance
            }
        }
    }
}
