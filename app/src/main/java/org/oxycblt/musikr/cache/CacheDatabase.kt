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
import org.oxycblt.musikr.fs.query.DeviceFile
import org.oxycblt.musikr.tag.Date
import org.oxycblt.musikr.tag.parse.ParsedTags
import org.oxycblt.musikr.tag.util.correctWhitespace
import org.oxycblt.musikr.tag.util.splitEscaped

@Database(entities = [CachedInfo::class], version = 50, exportSchema = false)
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
    @Query("SELECT * FROM CachedInfo WHERE uri = :uri AND dateModified = :dateModified")
    suspend fun selectInfo(uri: String, dateModified: Long): CachedInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun updateInfo(cachedInfo: CachedInfo)
}

@Entity
@TypeConverters(CachedInfo.Converters::class)
internal data class CachedInfo(
    /**
     * The Uri of the [AudioFile]'s audio file, obtained from SAF. This should ideally be a black
     * box only used for comparison.
     */
    @PrimaryKey val uri: String,
    val dateModified: Long,
    val durationMs: Long,
    val replayGainTrackAdjustment: Float?,
    val replayGainAlbumAdjustment: Float?,
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
    val cover: Cover? = null
) {
    fun intoCachedSong() =
        CachedSong(
            ParsedTags(
                musicBrainzId = musicBrainzId,
                name = name,
                sortName = sortName,
                durationMs = durationMs,
                replayGainTrackAdjustment = replayGainTrackAdjustment,
                replayGainAlbumAdjustment = replayGainAlbumAdjustment,
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
                genreNames = genreNames),
            cover)

    object Converters {
        @TypeConverter
        fun fromMultiValue(values: List<String>) =
            values.joinToString(";") { it.replace(";", "\\;") }

        @TypeConverter
        fun toMultiValue(string: String) = string.splitEscaped { it == ';' }.correctWhitespace()

        @TypeConverter fun fromDate(date: Date?) = date?.toString()

        @TypeConverter fun toDate(string: String?) = string?.let(Date::from)

        @TypeConverter fun fromCover(cover: Cover?) = cover?.key

        @TypeConverter fun toCover(key: String?) = key?.let { Cover.Single(it) }
    }

    companion object {
        fun fromCachedSong(deviceFile: DeviceFile, cachedSong: CachedSong) =
            CachedInfo(
                uri = deviceFile.uri.toString(),
                dateModified = deviceFile.lastModified,
                musicBrainzId = cachedSong.parsedTags.musicBrainzId,
                name = cachedSong.parsedTags.name,
                sortName = cachedSong.parsedTags.sortName,
                durationMs = cachedSong.parsedTags.durationMs,
                replayGainTrackAdjustment = cachedSong.parsedTags.replayGainTrackAdjustment,
                replayGainAlbumAdjustment = cachedSong.parsedTags.replayGainAlbumAdjustment,
                track = cachedSong.parsedTags.track,
                disc = cachedSong.parsedTags.disc,
                subtitle = cachedSong.parsedTags.subtitle,
                date = cachedSong.parsedTags.date,
                albumMusicBrainzId = cachedSong.parsedTags.albumMusicBrainzId,
                albumName = cachedSong.parsedTags.albumName,
                albumSortName = cachedSong.parsedTags.albumSortName,
                releaseTypes = cachedSong.parsedTags.releaseTypes,
                artistMusicBrainzIds = cachedSong.parsedTags.artistMusicBrainzIds,
                artistNames = cachedSong.parsedTags.artistNames,
                artistSortNames = cachedSong.parsedTags.artistSortNames,
                albumArtistMusicBrainzIds = cachedSong.parsedTags.albumArtistMusicBrainzIds,
                albumArtistNames = cachedSong.parsedTags.albumArtistNames,
                albumArtistSortNames = cachedSong.parsedTags.albumArtistSortNames,
                genreNames = cachedSong.parsedTags.genreNames,
                cover = cachedSong.cover)
    }
}
