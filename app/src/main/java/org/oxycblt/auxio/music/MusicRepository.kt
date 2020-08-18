package org.oxycblt.auxio.music

import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.media.MediaMetadataRetriever
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

            Log.i(this::class.simpleName, "Starting music search...")

            // Index music files from shared storage
            musicCursor?.use { cursor ->

                val idIndex = cursor.getColumnIndexOrThrow(AudioColumns._ID)
                val displayIndex = cursor.getColumnIndexOrThrow(AudioColumns.DISPLAY_NAME)

                val retriever = MediaMetadataRetriever()

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)

                    // Read the current file from the ID
                    retriever.setDataSource(
                        app.applicationContext,
                        ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                    )

                    // Get the metadata attributes
                    val title = retriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_TITLE
                    ) ?: cursor.getString(displayIndex)

                    val artist = retriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_ARTIST
                    )

                    val album = retriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_ALBUM
                    )

                    val genre = retriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_GENRE
                    )

                    val year = (
                        retriever.extractMetadata(
                            MediaMetadataRetriever.METADATA_KEY_YEAR
                        ) ?: "0"
                        ).toInt()

                    // Track is formatted as X/0, so trim off the /0 part to parse
                    // the track number correctly.
                    val track = (
                        retriever.extractMetadata(
                            MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER
                        ) ?: "0/0"
                        ).split("/")[0].toInt()

                    // Something has gone horribly wrong if a file has no duration,
                    // so assert it as such.
                    val duration = retriever.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_DURATION
                    )!!.toLong()

                    // TODO: Add int-based genre compatibility
                    songList.add(
                        Song(
                            title,
                            artist,
                            album,
                            genre,
                            year,
                            track,
                            duration,

                            retriever.embeddedPicture,
                            id

                        )
                    )
                }

                // Close the retriever when done so that it gets garbage collected [I hope]
                retriever.close()
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
                AudioColumns._ID,
                AudioColumns.DISPLAY_NAME
            ),
            AudioColumns.IS_MUSIC + "=1", null,
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        )
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

        Log.i(
            this::class.simpleName,
            "Successfully sorted songs into " +
                artistList.size.toString() +
                " Artists and " +
                albumList.size.toString() +
                " Albums."
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
