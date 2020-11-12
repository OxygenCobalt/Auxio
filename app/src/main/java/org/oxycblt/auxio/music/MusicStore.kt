package org.oxycblt.auxio.music

import android.app.Application
import android.util.Log
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.processing.MusicLoader
import org.oxycblt.auxio.music.processing.MusicLoaderResponse
import org.oxycblt.auxio.music.processing.MusicSorter
import org.oxycblt.auxio.recycler.ShowMode

/**
 * The main storage for music items. Use [MusicStore.from()] to get the instance.
 */
class MusicStore private constructor() {
    private var mGenres = listOf<Genre>()
    val genres: List<Genre> get() = mGenres

    private var mArtists = listOf<Artist>()
    val artists: List<Artist> get() = mArtists

    private var mAlbums = listOf<Album>()
    val albums: List<Album> get() = mAlbums

    private var mSongs = listOf<Song>()
    val songs: List<Song> get() = mSongs

    var loaded = false
        private set

    // Load/Sort the entire library.
    // ONLY CALL THIS FROM AN IO THREAD.
    fun load(app: Application): MusicLoaderResponse {
        Log.i(this::class.simpleName, "Starting initial music load...")

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

            mSongs = sorter.songs.toList()
            mAlbums = sorter.albums.toList()
            mArtists = sorter.artists.toList()
            mGenres = sorter.genres.toList()

            val elapsed = System.currentTimeMillis() - start

            Log.i(
                this::class.simpleName,
                "Music load completed successfully in ${elapsed}ms."
            )

            loaded = true
        }

        return loader.response
    }

    fun getListForShowMode(showMode: ShowMode): List<BaseModel> {
        return when (showMode) {
            ShowMode.SHOW_GENRES -> mGenres
            ShowMode.SHOW_ARTISTS -> mArtists
            ShowMode.SHOW_ALBUMS -> mAlbums
            ShowMode.SHOW_SONGS -> mSongs
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MusicStore? = null

        fun getInstance(): MusicStore {
            val currentInstance = INSTANCE

            if (currentInstance != null) {
                return currentInstance
            }

            synchronized(this) {
                val newInstance = MusicStore()
                INSTANCE = newInstance
                return newInstance
            }
        }
    }
}
