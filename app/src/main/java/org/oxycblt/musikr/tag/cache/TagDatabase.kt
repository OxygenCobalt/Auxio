/*
 * Copyright (c) 2023 Auxio Project
 * TagDatabase.kt is part of Auxio.
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
 
package org.oxycblt.musikr.tag.cache

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import org.oxycblt.musikr.fs.query.DeviceFile
import org.oxycblt.musikr.tag.Date
import org.oxycblt.musikr.tag.parse.ParsedTags
import org.oxycblt.musikr.tag.util.correctWhitespace
import org.oxycblt.musikr.tag.util.splitEscaped

@Database(entities = [CachedTags::class], version = 50, exportSchema = false)
abstract class TagDatabase : RoomDatabase() {
    abstract fun cachedSongsDao(): TagDao
}

@Dao
interface TagDao {
    @Query("SELECT * FROM CachedTags WHERE uri = :uri AND dateModified = :dateModified")
    suspend fun selectTags(uri: String, dateModified: Long): CachedTags?

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun updateTags(cachedTags: CachedTags)
}

@Entity
@TypeConverters(CachedTags.Converters::class)
data class CachedTags(
    /**
     * The Uri of the [AudioFile]'s audio file, obtained from SAF. This should ideally be a black
     * box only used for comparison.
     */
    @PrimaryKey val uri: String,
    /** The latest date the [AudioFile]'s audio file was modified, as a unix epoch timestamp. */
    val dateModified: Long,
    /** @see AudioFile */
    val durationMs: Long,
    /** @see AudioFile.replayGainTrackAdjustment */
    val replayGainTrackAdjustment: Float?,
    /** @see AudioFile.replayGainAlbumAdjustment */
    val replayGainAlbumAdjustment: Float?,
    /** @see AudioFile.musicBrainzId */
    val musicBrainzId: String?,
    /** @see AudioFile.name */
    val name: String,
    /** @see AudioFile.sortName */
    val sortName: String?,
    /** @see AudioFile.track */
    val track: Int?,
    /** @see AudioFile.name */
    val disc: Int?,
    /** @See AudioFile.subtitle */
    val subtitle: String?,
    /** @see AudioFile.date */
    val date: Date?,
    /** @see AudioFile.albumMusicBrainzId */
    val albumMusicBrainzId: String?,
    /** @see AudioFile.albumName */
    val albumName: String?,
    /** @see AudioFile.albumSortName */
    val albumSortName: String?,
    /** @see AudioFile.releaseTypes */
    val releaseTypes: List<String> = listOf(),
    /** @see AudioFile.artistMusicBrainzIds */
    val artistMusicBrainzIds: List<String> = listOf(),
    /** @see AudioFile.artistNames */
    val artistNames: List<String> = listOf(),
    /** @see AudioFile.artistSortNames */
    val artistSortNames: List<String> = listOf(),
    /** @see AudioFile.albumArtistMusicBrainzIds */
    val albumArtistMusicBrainzIds: List<String> = listOf(),
    /** @see AudioFile.albumArtistNames */
    val albumArtistNames: List<String> = listOf(),
    /** @see AudioFile.albumArtistSortNames */
    val albumArtistSortNames: List<String> = listOf(),
    /** @see AudioFile.genreNames */
    val genreNames: List<String> = listOf()
) {
    fun intoParsedTags() =
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
            genreNames = genreNames)

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
        fun fromParsedTags(deviceFile: DeviceFile, parsedTags: ParsedTags) =
            CachedTags(
                uri = deviceFile.uri.toString(),
                dateModified = deviceFile.lastModified,
                musicBrainzId = parsedTags.musicBrainzId,
                name = parsedTags.name,
                sortName = parsedTags.sortName,
                durationMs = parsedTags.durationMs,
                replayGainTrackAdjustment = parsedTags.replayGainTrackAdjustment,
                replayGainAlbumAdjustment = parsedTags.replayGainAlbumAdjustment,
                track = parsedTags.track,
                disc = parsedTags.disc,
                subtitle = parsedTags.subtitle,
                date = parsedTags.date,
                albumMusicBrainzId = parsedTags.albumMusicBrainzId,
                albumName = parsedTags.albumName,
                albumSortName = parsedTags.albumSortName,
                releaseTypes = parsedTags.releaseTypes,
                artistMusicBrainzIds = parsedTags.artistMusicBrainzIds,
                artistNames = parsedTags.artistNames,
                artistSortNames = parsedTags.artistSortNames,
                albumArtistMusicBrainzIds = parsedTags.albumArtistMusicBrainzIds,
                albumArtistNames = parsedTags.albumArtistNames,
                albumArtistSortNames = parsedTags.albumArtistSortNames,
                genreNames = parsedTags.genreNames)
    }
}
