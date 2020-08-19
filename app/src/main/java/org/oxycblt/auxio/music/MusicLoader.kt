package org.oxycblt.auxio.music

import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns
import android.util.Log
import org.oxycblt.auxio.music.models.Song

enum class MusicLoaderResponse {
    DONE, FAILURE, NO_MUSIC
}

// Class that loads music from the FileSystem.
// This thing is probably full of memory leaks.
class MusicLoader(private val app: Application) {

    var songs = mutableListOf<Song>()

    private val retriever: MediaMetadataRetriever = MediaMetadataRetriever()
    private var musicCursor: Cursor? = null

    val response: MusicLoaderResponse

    init {
        response = findMusic()
    }

    private fun findMusic() : MusicLoaderResponse {
        try {
            musicCursor = getCursor(
                app.contentResolver
            )

            Log.i(this::class.simpleName, "Starting music search...")

            useCursor()

        } catch (error: Exception) {
            // TODO: Add better error handling

            Log.e(this::class.simpleName, "Something went horribly wrong.")
            error.printStackTrace()

            finalize()

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

    // Use the cursor index music files from the shared storage, returns true if any were found.
    private fun useCursor() {
        musicCursor?.use { cursor ->

            // Don't run the more expensive file loading operations if there is no music to index.
            if (cursor.count == 0) {
                return
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
                songs.add(
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
        }
    }

    // Free the metadata retriever & the musicCursor, and make the song list immutable.
    private fun finalize() {
        retriever.close()
        musicCursor?.use{
            it.close()
        }
    }
}