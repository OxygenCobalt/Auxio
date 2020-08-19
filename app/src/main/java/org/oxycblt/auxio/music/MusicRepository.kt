package org.oxycblt.auxio.music

import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns
import android.util.Log
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.music.models.Artist
import org.oxycblt.auxio.music.models.Song

enum class MusicLoadResponse {
    DONE, FAILURE, NO_MUSIC
}

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

    fun init(app: Application): MusicLoadResponse {

        findMusic(app)?.let { ss ->
            return if (ss.size > 0) {
                processSongs(ss)

                MusicLoadResponse.DONE
            } else {
                MusicLoadResponse.NO_MUSIC
            }
        }

        // If the let function isn't run, then the loading has failed due to some Exception
        // and FAILURE must be returned
        return MusicLoadResponse.FAILURE
    }

    private fun findMusic(app: Application): MutableList<Song>? {

        val songList = mutableListOf<Song>()
        val retriever = MediaMetadataRetriever()

        try {

            val musicCursor = getCursor(
                app.contentResolver
            )

            Log.i(this::class.simpleName, "Starting music search...")

            // Index music files from shared storage
            musicCursor?.use { cursor ->

                // Don't run the more expensive file loading operations if there is no music
                // to index.
                if (cursor.count == 0) {
                    return songList
                }

                val idIndex = cursor.getColumnIndexOrThrow(AudioColumns._ID)
                val displayIndex = cursor.getColumnIndexOrThrow(AudioColumns.DISPLAY_NAME)

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

                // Close the retriever/cursor so that it gets garbage collected
                retriever.close()
                cursor.close()
            }

            Log.d(
                this::class.simpleName,
                "Successfully found " + songList.size.toString() + " Songs."
            )

            return songList
        } catch (error: Exception) {
            // TODO: Add better error handling

            Log.e(this::class.simpleName, "Something went horribly wrong.")
            error.printStackTrace()

            retriever.close()

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

        // Add an album abstraction for each group of songs
        val songsByAlbum = distinctSongs.groupBy { it.album }
        val albumList = mutableListOf<Album>()

        songsByAlbum.keys.iterator().forEach { album ->
            val albumSongs = songsByAlbum[album]
            albumSongs?.let {
                albumList.add(
                    Album(albumSongs)
                )
            }
        }

        // Then abstract the remaining albums into artist objects
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
