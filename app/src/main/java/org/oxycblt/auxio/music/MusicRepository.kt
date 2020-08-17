package org.oxycblt.auxio.music

import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns
import android.util.Log

// Storage for music data. Design largely adapted from Music Player GO:
// https://github.com/enricocid/Music-Player-GO
class MusicRepository() {

    var rawMusicList = mutableListOf<RawMusic>()

    fun getMusic(app: Application): MutableList<RawMusic> {
        findMusic(app)?.let { rm ->

            // TODO: Sort the raw music
            rawMusicList = rm
        }

        return rawMusicList
    }

    private fun findMusic(app: Application): MutableList<RawMusic>? {

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
                    rawMusicList.add(
                        RawMusic(
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
                "Music search ended with " + rawMusicList.size.toString() + " Songs found."
            )

            return rawMusicList
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
    }

    companion object {
        @Volatile
        private var INSTANCE: MusicRepository? = null

        fun getInstance(): MusicRepository {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val newInstance = MusicRepository()
                INSTANCE = newInstance

                Log.d(
                    MusicRepository::class.simpleName,
                    "Created an instance of MusicRepository."
                )

                return newInstance
            }
        }
    }
}
