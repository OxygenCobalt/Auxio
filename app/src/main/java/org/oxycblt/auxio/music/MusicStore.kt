package org.oxycblt.auxio.music

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.processing.MusicLoader
import org.oxycblt.auxio.music.processing.MusicSorter

/**
 * The main storage for music items. Use [MusicStore.getInstance] to get the single instance of it.
 * @author OxygenCobalt
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

    /** All parent models (ex Albums, Artists) loaded by Auxio */
    val parents: MutableList<Parent> by lazy {
        mutableListOf<Parent>().apply {
            addAll(mGenres)
            addAll(mArtists)
            addAll(mAlbums)
        }
    }

    var loaded = false
        private set

    /**
     * Load/Sort the entire music library.
     * ***THIS SHOULD ONLY BE RAN FROM AN IO THREAD.***
     * @param app [Application] required to load the music.
     */
    suspend fun load(app: Application): Response {
        return withContext(Dispatchers.IO) {
            this@MusicStore.logD("Starting initial music load...")

            val start = System.currentTimeMillis()

            val loader = MusicLoader(app)
            val response = loader.loadMusic()

            if (response == Response.SUCCESS) {
                // If the loading succeeds, then sort the songs and update the value
                val sorter = MusicSorter(loader.songs, loader.albums)

                sorter.sort()

                mSongs = sorter.songs.toList()
                mAlbums = sorter.albums.toList()
                mArtists = sorter.artists.toList()
                mGenres = loader.genres.toList()

                val elapsed = System.currentTimeMillis() - start

                this@MusicStore.logD("Music load completed successfully in ${elapsed}ms.")

                loaded = true
            }

            response
        }
    }

    enum class Response {
        NO_MUSIC, NO_PERMS, FAILED, SUCCESS
    }

    companion object {
        @Volatile
        private var INSTANCE: MusicStore? = null

        /**
         * Get/Instantiate the single instance of [MusicStore].
         */
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
