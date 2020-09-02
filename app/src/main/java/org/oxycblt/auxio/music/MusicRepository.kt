package org.oxycblt.auxio.music

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.music.models.Artist
import org.oxycblt.auxio.music.models.Genre
import org.oxycblt.auxio.music.models.Song
import org.oxycblt.auxio.music.processing.MusicLoader
import org.oxycblt.auxio.music.processing.MusicLoaderResponse
import org.oxycblt.auxio.music.processing.MusicSorter

// Storage for music data.
class MusicRepository {

    lateinit var genres: List<Genre>
    lateinit var artists: List<Artist>
    lateinit var albums: List<Album>
    lateinit var songs: List<Song>

    fun init(app: Application): MusicLoaderResponse {
        if (!checkPerms(app)) {
            Log.i(this::class.simpleName, "No permissions, aborting...")

            return MusicLoaderResponse.NO_PERMS
        }

        Log.i(this::class.simpleName, "Starting initial music load...")

        val start = System.currentTimeMillis()

        val loader = MusicLoader(app.contentResolver)

        if (loader.response == MusicLoaderResponse.DONE) {
            // If the loading succeeds, then process the songs and set them.
            val sorter = MusicSorter(
                loader.genres,
                loader.artists,
                loader.albums,
                loader.songs,

                app.applicationContext.getString(R.string.label_unknown_genre),
                app.applicationContext.getString(R.string.label_unknown_artist),
                app.applicationContext.getString(R.string.label_unknown_album)
            )

            songs = sorter.songs.toList()
            albums = sorter.albums.toList()
            artists = sorter.artists.toList()
            genres = sorter.genres.toList()

            val elapsed = System.currentTimeMillis() - start

            Log.i(
                this::class.simpleName,
                "Music load completed successfully in ${elapsed}ms."
            )
        }

        return loader.response
    }

    private fun checkPerms(app: Application): Boolean {
        return ContextCompat.checkSelfPermission(
            app.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
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
