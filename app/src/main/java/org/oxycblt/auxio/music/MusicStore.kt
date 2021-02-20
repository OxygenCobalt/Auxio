package org.oxycblt.auxio.music

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.logE
import org.oxycblt.auxio.music.processing.MusicLinker
import org.oxycblt.auxio.music.processing.MusicLoader
import java.lang.Exception

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

            try {
                val loader = MusicLoader(app)
                loader.loadMusic()

                if (loader.songs.isEmpty()) {
                    return@withContext Response.NO_MUSIC
                }

                val linker = MusicLinker(app, loader.songs, loader.albums, loader.genres)
                linker.link()

                mSongs = linker.songs.toList()
                mAlbums = linker.albums.toList()
                mArtists = linker.artists.toList()
                mGenres = linker.genres.toList()

                loaded = true

                this@MusicStore.logD(
                    "Music load completed successfully in ${System.currentTimeMillis() - start}ms."
                )
            } catch (e: Exception) {
                logE("Something went horribly wrong.")
                logE(e.stackTraceToString())

                return@withContext Response.FAILED
            }

            return@withContext Response.SUCCESS
        }
    }

    /**
     * Get the song for a specific URI.
     */
    suspend fun getSongForUri(uri: Uri, resolver: ContentResolver): Song? {
        return withContext(Dispatchers.IO) {
            resolver.query(uri, null, null, null, null)?.use { cursor ->
                cursor.moveToFirst()
                val fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                return@withContext songs.find { it.fileName == fileName }
            }
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
