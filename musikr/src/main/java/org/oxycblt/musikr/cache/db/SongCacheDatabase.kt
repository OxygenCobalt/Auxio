/*
 * Copyright (c) 2023 Auxio Project
 * SongCacheDatabase.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package org.oxycblt.musikr.cache.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import org.oxycblt.musikr.tag.Date
import org.oxycblt.musikr.util.correctWhitespace
import org.oxycblt.musikr.util.splitEscaped

@Database(entities = [CachedSongData::class], version = 57, exportSchema = false)
internal abstract class CacheDatabase : RoomDatabase() {
    abstract fun readDao(): CacheReadDao

    abstract fun writeDao(): CacheWriteDao

    companion object {
        fun from(context: Context) =
            Room.databaseBuilder(
                    context.applicationContext, CacheDatabase::class.java, "music_cache.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}

@Dao
internal interface CacheReadDao {
    @Query("SELECT * FROM CachedSongData WHERE uri = :uri")
    suspend fun selectSong(uri: String): CachedSongData?
}

@Dao
internal interface CacheWriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSong(cachedSong: CachedSongData)

    /** Delete every CachedSong whose URI is not in the uris list */
    @Transaction
    suspend fun deleteExcludingUris(uris: List<String>) {
        // SQLite has a limit of 999 variables in a query
        val chunks = uris.chunked(999)
        for (chunk in chunks) {
            deleteExcludingUriChunk(chunk)
        }
    }

    @Query("DELETE FROM CachedSongData WHERE uri NOT IN (:uris)")
    suspend fun deleteExcludingUriChunk(uris: List<String>)
}

@Entity
@TypeConverters(CachedSongData.Converters::class)
internal data class CachedSongData(
    @PrimaryKey val uri: String,
    val modifiedMs: Long,
    val addedMs: Long,
    val mimeType: String,
    val durationMs: Long,
    val bitrateHz: Int,
    val sampleRateHz: Int,
    val musicBrainzId: String?,
    val name: String,
    val sortName: String?,
    val track: Int?,
    val disc: Int?,
    val subtitle: String?,
    val date: Date?,
    val albumMusicBrainzId: String?,
    val albumName: String?,
    val albumSortName: String?,
    val releaseTypes: List<String>,
    val artistMusicBrainzIds: List<String>,
    val artistNames: List<String>,
    val artistSortNames: List<String>,
    val albumArtistMusicBrainzIds: List<String>,
    val albumArtistNames: List<String>,
    val albumArtistSortNames: List<String>,
    val genreNames: List<String>,
    val replayGainTrackAdjustment: Float?,
    val replayGainAlbumAdjustment: Float?,
    val coverId: String?,
) {
    object Converters {
        @TypeConverter
        fun fromMultiValue(values: List<String>) =
            values.joinToString(";") { it.replace(";", "\\;") }

        @TypeConverter
        fun toMultiValue(string: String) = string.splitEscaped { it == ';' }.correctWhitespace()

        @TypeConverter fun fromDate(date: Date?) = date?.toString()

        @TypeConverter fun toDate(string: String?) = string?.let(Date::from)
    }
}
