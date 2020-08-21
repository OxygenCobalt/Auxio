package org.oxycblt.auxio.music

import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Genres
import android.provider.MediaStore.Audio.AudioColumns
import android.util.Log
import org.oxycblt.auxio.music.models.Song

enum class MusicLoaderResponse {
    DONE, FAILURE, NO_MUSIC
}

// Class that loads music from the FileSystem.
// FIXME: This thing probably has some memory leaks *somewhere*
class MusicLoader(private val app: Application) {

    var songs = mutableListOf<Song>()
    var genres = mutableListOf<Pair<Long, String>>()

    private var musicCursor: Cursor? = null
    private var genreCursor: Cursor? = null

    val response: MusicLoaderResponse

    init {
        response = findMusic()
    }

    private fun findMusic(): MusicLoaderResponse {
        genreCursor = getGenreCursor(
            app.contentResolver
        )

        useGenreCursor()

        indexMusic()

        try {
        } catch (error: Exception) {
            Log.e(this::class.simpleName, "Something went horribly wrong.")
            error.printStackTrace()

            musicCursor?.close()

            return MusicLoaderResponse.FAILURE
        }

        // If the main loading completed without a failure, return DONE or
        // NO_MUSIC depending on if any music was found.
        return if (songs.size > 0) {
            Log.d(
                this::class.simpleName,
                "Successfully found " + songs.size.toString() + " Songs."
            )

            MusicLoaderResponse.DONE
        } else {
            Log.d(
                this::class.simpleName,
                "No music was found."
            )

            MusicLoaderResponse.NO_MUSIC
        }
    }

    private fun getGenreCursor(resolver: ContentResolver): Cursor? {
        Log.i(this::class.simpleName, "Getting genre cursor.")

        // Get every Genre indexed by the android system, for some reason
        // you cant directly get this from the plain music cursor.
        return resolver.query(
            Genres.EXTERNAL_CONTENT_URI,
            arrayOf(
                Genres._ID, // 0
                Genres.NAME // 1
            ),
            null, null,
            Genres.DEFAULT_SORT_ORDER
        )
    }

    private fun getMusicCursor(resolver: ContentResolver, genreId: Long): Cursor? {
        Log.i(this::class.simpleName, "Getting music cursor.")

        // Get all the values that can be retrieved by the cursor.
        // The rest are retrieved using MediaMetadataExtractor and
        // stored into a database.
        return resolver.query(
            Genres.Members.getContentUri("external", genreId),
            arrayOf(
                AudioColumns._ID, // 0
                AudioColumns.DISPLAY_NAME, // 1
                AudioColumns.TITLE, // 2
                AudioColumns.ARTIST, // 3
                AudioColumns.ALBUM, // 4
                AudioColumns.YEAR, // 5
                AudioColumns.TRACK, // 6
                AudioColumns.DURATION // 7
            ),
            AudioColumns.IS_MUSIC + "=1", null,
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        )
    }

    // Use the genre cursor to index all the genres the android system has indexed.
    private fun useGenreCursor() {
        // TODO: Move genre to its own model, even if its just two values

        genreCursor?.use { cursor ->

            // Don't even bother running if there's nothing to process.
            if (cursor.count == 0) {
                return
            }

            val idIndex = cursor.getColumnIndexOrThrow(Genres._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(Genres.NAME)

            while (cursor.moveToNext()) {
                genres.add(
                    Pair<Long, String>(
                        cursor.getLong(idIndex),
                        cursor.getString(nameIndex)
                    )
                )

                Log.i(this::class.simpleName, cursor.getString(nameIndex))
            }

            cursor.close()
        }

        Log.i(this::class.simpleName, "Retrieved " + genres.size.toString() + " Genres.")
    }

    // Use the cursor index music files from the shared storage.
    private fun indexMusic() {
        Log.i(this::class.simpleName, "Starting music search...")

        // So, android has a brain aneurysm if you try to get an audio genre through
        // AudioColumns. As a result, I just index every genre's name & ID, create a cursor
        // of music for that genres ID, and then load and iterate through them normally,
        // creating using the genres name as that songs Genre.

        // God why do I have to do this
        for (genre in genres) {
            val musicCursor = getMusicCursor(app.contentResolver, genre.first)

            musicCursor?.use { cursor ->

                // Don't run the more expensive file loading operations if there
                // is no music to index.
                if (cursor.count == 0) {
                    return
                }

                val idIndex = cursor.getColumnIndexOrThrow(AudioColumns._ID)
                val displayIndex = cursor.getColumnIndexOrThrow(AudioColumns.DISPLAY_NAME)
                val titleIndex = cursor.getColumnIndexOrThrow(AudioColumns.TITLE)
                val artistIndex = cursor.getColumnIndexOrThrow(AudioColumns.ARTIST)
                val albumIndex = cursor.getColumnIndexOrThrow(AudioColumns.ALBUM)
                val yearIndex = cursor.getColumnIndexOrThrow(AudioColumns.YEAR)
                val trackIndex = cursor.getColumnIndexOrThrow(AudioColumns.TRACK)
                val durationIndex = cursor.getColumnIndexOrThrow(AudioColumns.DURATION)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    val display = cursor.getString(displayIndex)

                    // Get the basic metadata from the cursor
                    val title = cursor.getString(titleIndex) ?: display
                    val artist = cursor.getString(artistIndex)
                    val album = cursor.getString(albumIndex)
                    val year = cursor.getInt(yearIndex)
                    val track = cursor.getInt(trackIndex)
                    val duration = cursor.getLong(durationIndex)

                    // TODO: Add album art [But its loaded separately, as that will take a bit]
                    // TODO: Add genres whenever android hasn't borked it
                    songs.add(
                        Song(
                            id, title, artist, album,
                            genre.second, year, track, duration
                        )
                    )
                }

                cursor.close()
            }
        }
    }
}
