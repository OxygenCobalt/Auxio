/*
 * Copyright (c) 2023 Auxio Project
 * CacheDatabase.kt is part of Auxio.
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
 
package org.oxycblt.musikr.cache

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
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
import androidx.room.Update
import org.oxycblt.musikr.cover.StoredCovers
import org.oxycblt.musikr.fs.DeviceFile
import org.oxycblt.musikr.metadata.Properties
import org.oxycblt.musikr.pipeline.RawSong
import org.oxycblt.musikr.tag.Date
import org.oxycblt.musikr.tag.parse.ParsedTags
import org.oxycblt.musikr.util.correctWhitespace
import org.oxycblt.musikr.util.splitEscaped

@Database(entities = [CachedSong::class], version = 50, exportSchema = false)
internal abstract class CacheDatabase : RoomDatabase() {
    abstract fun visibleDao(): VisibleCacheDao

    abstract fun invisibleDao(): InvisibleCacheDao

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
internal interface VisibleCacheDao {
    @Query("SELECT * FROM CachedSong WHERE uri = :uri")
    suspend fun selectSong(uri: String): CachedSong?

    @Query("SELECT addedMs FROM CachedSong WHERE uri = :uri")
    suspend fun selectAddedMs(uri: String): Long?

    @Transaction
    suspend fun touch(uri: String) = updateTouchedNs(uri, System.nanoTime())

    @Query("UPDATE cachedsong SET touchedNs = :nowNs WHERE uri = :uri")
    suspend fun updateTouchedNs(uri: String, nowNs: Long)
}

@Dao
internal interface InvisibleCacheDao {
    @Query("SELECT addedMs FROM CachedSong WHERE uri = :uri")
    suspend fun selectAddedMs(uri: String): Long?
}

@Dao
internal interface CacheWriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun updateSong(cachedSong: CachedSong)

    @Query("DELETE FROM CachedSong WHERE touchedNs < :now")
    suspend fun pruneOlderThan(now: Long)
}

@Entity
@TypeConverters(CachedSong.Converters::class)
internal data class CachedSong(
    @PrimaryKey val uri: String,
    val modifiedMs: Long,
    val addedMs: Long,
    val touchedNs: Long,
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
    suspend fun intoRawSong(file: DeviceFile, storedCovers: StoredCovers) =
        RawSong(
            file,
            Properties(mimeType, durationMs, bitrateHz, sampleRateHz),
            ParsedTags(
                musicBrainzId = musicBrainzId,
                name = name,
                sortName = sortName,
                durationMs = durationMs,
                track = track,
                disc = disc,
                subtitle = subtitle,
                date = date,
                albumMusicBrainzId = albumMusicBrainzId,
                albumName = albumName,
                albumSortName = albumSortName,
                releaseTypes = releaseTypes,
                artistMusicBrainzIds = artistMusicBrainzIds,
                artistNames = artistNames,
                artistSortNames = artistSortNames,
                albumArtistMusicBrainzIds = albumArtistMusicBrainzIds,
                albumArtistNames = albumArtistNames,
                albumArtistSortNames = albumArtistSortNames,
                genreNames = genreNames,
                replayGainTrackAdjustment = replayGainTrackAdjustment,
                replayGainAlbumAdjustment = replayGainAlbumAdjustment),
            coverId?.let { storedCovers.obtain(it) },
            addedMs = addedMs)

    object Converters {
        @TypeConverter
        fun fromMultiValue(values: List<String>) =
            values.joinToString(";") { it.replace(";", "\\;") }

        @TypeConverter
        fun toMultiValue(string: String) = string.splitEscaped { it == ';' }.correctWhitespace()

        @TypeConverter fun fromDate(date: Date?) = date?.toString()

        @TypeConverter fun toDate(string: String?) = string?.let(Date::from)
    }

    companion object {
        fun fromRawSong(rawSong: RawSong) =
            CachedSong(
                uri = rawSong.file.uri.toString(),
                modifiedMs = rawSong.file.lastModified,
                addedMs = rawSong.addedMs,
                // Should be strictly monotonic so we don't prune this
                // by accident later.
                touchedNs = System.nanoTime(),
                musicBrainzId = rawSong.tags.musicBrainzId,
                name = rawSong.tags.name,
                sortName = rawSong.tags.sortName,
                durationMs = rawSong.tags.durationMs,
                track = rawSong.tags.track,
                disc = rawSong.tags.disc,
                subtitle = rawSong.tags.subtitle,
                date = rawSong.tags.date,
                albumMusicBrainzId = rawSong.tags.albumMusicBrainzId,
                albumName = rawSong.tags.albumName,
                albumSortName = rawSong.tags.albumSortName,
                releaseTypes = rawSong.tags.releaseTypes,
                artistMusicBrainzIds = rawSong.tags.artistMusicBrainzIds,
                artistNames = rawSong.tags.artistNames,
                artistSortNames = rawSong.tags.artistSortNames,
                albumArtistMusicBrainzIds = rawSong.tags.albumArtistMusicBrainzIds,
                albumArtistNames = rawSong.tags.albumArtistNames,
                albumArtistSortNames = rawSong.tags.albumArtistSortNames,
                genreNames = rawSong.tags.genreNames,
                replayGainTrackAdjustment = rawSong.tags.replayGainTrackAdjustment,
                replayGainAlbumAdjustment = rawSong.tags.replayGainAlbumAdjustment,
                coverId = rawSong.cover?.id,
                mimeType = rawSong.properties.mimeType,
                bitrateHz = rawSong.properties.bitrateKbps,
                sampleRateHz = rawSong.properties.sampleRateHz)
    }
}
