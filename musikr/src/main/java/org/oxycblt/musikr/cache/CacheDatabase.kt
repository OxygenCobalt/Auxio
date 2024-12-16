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
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.fs.DeviceFile
import org.oxycblt.musikr.metadata.Properties
import org.oxycblt.musikr.pipeline.RawSong
import org.oxycblt.musikr.tag.Date
import org.oxycblt.musikr.tag.parse.ParsedTags
import org.oxycblt.musikr.util.correctWhitespace
import org.oxycblt.musikr.util.splitEscaped

@Database(entities = [CachedSong::class], version = 50, exportSchema = false)
abstract class CacheDatabase : RoomDatabase() {
    internal abstract fun cachedSongsDao(): CacheInfoDao

    companion object {
        fun from(context: Context) =
            Room.databaseBuilder(
                    context.applicationContext, CacheDatabase::class.java, "music_cache.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}

@Dao
internal interface CacheInfoDao {
    @Query("SELECT * FROM CachedSong WHERE uri = :uri AND dateModified = :dateModified")
    suspend fun selectSong(uri: String, dateModified: Long): CachedSong?

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun updateSong(cachedSong: CachedSong)
}

@Entity
@TypeConverters(CachedSong.Converters::class)
internal data class CachedSong(
    @PrimaryKey val uri: String,
    val dateModified: Long,
    val mimeType: String,
    val durationMs: Long,
    val bitrate: Int,
    val sampleRate: Int,
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
    val cover: Cover.Single?,
) {
    fun intoRawSong(file: DeviceFile) =
        RawSong(
            file,
            Properties(mimeType, durationMs, bitrate, sampleRate),
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
            cover)

    object Converters {
        @TypeConverter
        fun fromMultiValue(values: List<String>) =
            values.joinToString(";") { it.replace(";", "\\;") }

        @TypeConverter
        fun toMultiValue(string: String) = string.splitEscaped { it == ';' }.correctWhitespace()

        @TypeConverter fun fromDate(date: Date?) = date?.toString()

        @TypeConverter fun toDate(string: String?) = string?.let(Date::from)

        @TypeConverter fun fromCover(cover: Cover.Single?) = cover?.key

        @TypeConverter fun toCover(key: String?) = key?.let { Cover.Single(it) }
    }

    companion object {
        fun fromRawSong(rawSong: RawSong) =
            CachedSong(
                uri = rawSong.file.uri.toString(),
                dateModified = rawSong.file.lastModified,
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
                cover = rawSong.cover,
                mimeType = rawSong.properties.mimeType,
                bitrate = rawSong.properties.bitrate,
                sampleRate = rawSong.properties.sampleRate)
    }
}
