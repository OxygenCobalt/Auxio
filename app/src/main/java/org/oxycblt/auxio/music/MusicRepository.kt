package org.oxycblt.auxio.music

import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns
import android.util.Log

// Storage for music data. Design largely adapted from Music Player GO:
// https://github.com/enricocid/Music-Player-GO
class MusicRepository {

    private lateinit var mArtists: List<Artist>
    private lateinit var mAlbums: List<Album>
    private lateinit var mSongs: List<Song>

    // Not sure if backings are necessary but they're vars so better safe than sorry
    val artists: List<Artist> get() = mArtists
    val albums: List<Album> get() = mAlbums
    val songs: List<Song> get() = mSongs

    fun init(app: Application): Boolean {
        findMusic(app)?.let { ss ->
            if (ss.size > 0) {
                processSongs(ss)

                return true
            }
        }

        // Return false if the load as failed for any reason, either
        // through there being no music or an Exception.
        return false
    }

    private fun findMusic(app: Application): MutableList<Song>? {

        val songList = mutableListOf<Song>()

        try {
            val musicCursor = getCursor(
                app.contentResolver
            )

            // Index music files from shared storage
            musicCursor?.use { cursor ->

                val nameIndex = cursor.getColumnIndexOrThrow(AudioColumns.TITLE)
                val artistIndex = cursor.getColumnIndexOrThrow(AudioColumns.ARTIST)
                val albumIndex = cursor.getColumnIndexOrThrow(AudioColumns.ALBUM)
                val yearIndex = cursor.getColumnIndexOrThrow(AudioColumns.YEAR)
                val trackIndex = cursor.getColumnIndexOrThrow(AudioColumns.TRACK)
                val durationIndex = cursor.getColumnIndexOrThrow(AudioColumns.DURATION)
                val idIndex = cursor.getColumnIndexOrThrow(AudioColumns._ID)

                while (cursor.moveToNext()) {
                    songList.add(
                        Song(
                            cursor.getString(nameIndex),
                            cursor.getString(artistIndex),
                            cursor.getString(albumIndex),
                            cursor.getInt(yearIndex),
                            cursor.getInt(trackIndex),
                            cursor.getLong(durationIndex),
                            cursor.getLong(idIndex)
                        )
                    )
                }
            }

            Log.d(
                this::class.simpleName,
                "Music search finished with " + songList.size.toString() + " Songs found."
            )

            return songList
        } catch (error: Exception) {
            // TODO: Add better error handling

            Log.e(this::class.simpleName, "Something went horribly wrong.")
            error.printStackTrace()

            return null
        }
    }

    private fun getCursor(resolver: ContentResolver): Cursor? {
        Log.i(this::class.simpleName, "Getting music cursor.")

        return resolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                AudioColumns.TITLE,
                AudioColumns.ARTIST,
                AudioColumns.ALBUM,
                AudioColumns.YEAR,
                AudioColumns.TRACK,
                AudioColumns.DURATION,
                AudioColumns._ID
            ),
            AudioColumns.IS_MUSIC + "=1", null,
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        )

        // TODO: Art Loading, since android cant do it on its own
        // TODO: Genre Loading?
    }

    // Sort the list of Song objects into an abstracted lis
    private fun processSongs(songs: MutableList<Song>) {
        // Eliminate all duplicates from the list
        // excluding the ID, as that's guaranteed to be unique [I think]
        val distinctSongs = songs.distinctBy {
            it.name to it.artist to it.album to it.year to it.track to it.duration
        }.toMutableList()

        // Sort the music by artists/albums
        val songsByAlbum = distinctSongs.groupBy { it.album }
        val albumList = mutableListOf<Album>()

        songsByAlbum.keys.iterator().forEach { album ->
            val albumSongs = songsByAlbum[album]

            // Add an album abstraction for each album item in the list of songs.
            albumSongs?.let {
                albumList.add(
                    Album(albumSongs)
                )
            }
        }

        // Then abstract the remaining albums into artist objects
        // TODO: If enabled
        val albumsByArtist = albumList.groupBy { it.artist }
        val artistList = mutableListOf<Artist>()

        albumsByArtist.keys.iterator().forEach { artist ->
            val artistAlbums = albumsByArtist[artist]

            artistAlbums?.let {
                artistList.add(
                    Artist(artistAlbums)
                )
            }
        }

        Log.i(this::class.simpleName,
            "Successfully sorted songs into "
                    + artistList.size.toString()
                    + " Artists and "
                    + albumList.size.toString()
                    + " Albums."
        )

        mArtists = artistList
        mAlbums = albumList
        mSongs = distinctSongs

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
