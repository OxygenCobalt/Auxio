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
 
package org.oxycblt.auxio.music.stack.cache

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
import org.oxycblt.auxio.music.device.RawSong
import org.oxycblt.auxio.music.info.Date
import org.oxycblt.auxio.music.stack.extractor.correctWhitespace
import org.oxycblt.auxio.music.stack.extractor.splitEscaped

@Database(entities = [Tags::class], version = 50, exportSchema = false)
abstract class TagDatabase : RoomDatabase() {
    abstract fun cachedSongsDao(): TagDao
}

@Dao
interface TagDao {
    @Query("SELECT * FROM Tags WHERE uri = :uri AND dateModified = :dateModified")
    suspend fun selectTags(uri: String, dateModified: Long): Tags?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTags(tags: Tags)
}

@Entity
@TypeConverters(Tags.Converters::class)
data class Tags(
    /**
     * The Uri of the [RawSong]'s audio file, obtained from SAF.
     * This should ideally be a black box only used for comparison.
     */
    @PrimaryKey val uri: String,
    /** The latest date the [RawSong]'s audio file was modified, as a unix epoch timestamp. */
    val dateModified: Long,
    /** @see RawSong */
    val durationMs: Long,
    /** @see RawSong.replayGainTrackAdjustment */
    val replayGainTrackAdjustment: Float? = null,
    /** @see RawSong.replayGainAlbumAdjustment */
    val replayGainAlbumAdjustment: Float? = null,
    /** @see RawSong.musicBrainzId */
    var musicBrainzId: String? = null,
    /** @see RawSong.name */
    var name: String,
    /** @see RawSong.sortName */
    var sortName: String? = null,
    /** @see RawSong.track */
    var track: Int? = null,
    /** @see RawSong.name */
    var disc: Int? = null,
    /** @See RawSong.subtitle */
    var subtitle: String? = null,
    /** @see RawSong.date */
    var date: Date? = null,
    /** @see RawSong.coverPerceptualHash */
    var coverPerceptualHash: String? = null,
    /** @see RawSong.albumMusicBrainzId */
    var albumMusicBrainzId: String? = null,
    /** @see RawSong.albumName */
    var albumName: String,
    /** @see RawSong.albumSortName */
    var albumSortName: String? = null,
    /** @see RawSong.releaseTypes */
    var releaseTypes: List<String> = listOf(),
    /** @see RawSong.artistMusicBrainzIds */
    var artistMusicBrainzIds: List<String> = listOf(),
    /** @see RawSong.artistNames */
    var artistNames: List<String> = listOf(),
    /** @see RawSong.artistSortNames */
    var artistSortNames: List<String> = listOf(),
    /** @see RawSong.albumArtistMusicBrainzIds */
    var albumArtistMusicBrainzIds: List<String> = listOf(),
    /** @see RawSong.albumArtistNames */
    var albumArtistNames: List<String> = listOf(),
    /** @see RawSong.albumArtistSortNames */
    var albumArtistSortNames: List<String> = listOf(),
    /** @see RawSong.genreNames */
    var genreNames: List<String> = listOf()
) {
    fun copyToRaw(rawSong: RawSong) {
        rawSong.musicBrainzId = musicBrainzId
        rawSong.name = name
        rawSong.sortName = sortName

        rawSong.durationMs = durationMs

        rawSong.replayGainTrackAdjustment = replayGainTrackAdjustment
        rawSong.replayGainAlbumAdjustment = replayGainAlbumAdjustment

        rawSong.track = track
        rawSong.disc = disc
        rawSong.subtitle = subtitle
        rawSong.date = date

        rawSong.coverPerceptualHash = coverPerceptualHash

        rawSong.albumMusicBrainzId = albumMusicBrainzId
        rawSong.albumName = albumName
        rawSong.albumSortName = albumSortName
        rawSong.releaseTypes = releaseTypes

        rawSong.artistMusicBrainzIds = artistMusicBrainzIds
        rawSong.artistNames = artistNames
        rawSong.artistSortNames = artistSortNames

        rawSong.albumArtistMusicBrainzIds = albumArtistMusicBrainzIds
        rawSong.albumArtistNames = albumArtistNames
        rawSong.albumArtistSortNames = albumArtistSortNames

        rawSong.genreNames = genreNames
    }

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
        fun fromRaw(rawSong: RawSong) =
            Tags(
                uri = rawSong.file.uri.toString(),
                dateModified = rawSong.file.lastModified,
                musicBrainzId = rawSong.musicBrainzId,
                name = requireNotNull(rawSong.name) { "Invalid raw: No name" },
                sortName = rawSong.sortName,
                durationMs = requireNotNull(rawSong.durationMs) { "Invalid raw: No duration" },
                replayGainTrackAdjustment = rawSong.replayGainTrackAdjustment,
                replayGainAlbumAdjustment = rawSong.replayGainAlbumAdjustment,
                track = rawSong.track,
                disc = rawSong.disc,
                subtitle = rawSong.subtitle,
                date = rawSong.date,
                coverPerceptualHash = rawSong.coverPerceptualHash,
                albumMusicBrainzId = rawSong.albumMusicBrainzId,
                albumName = requireNotNull(rawSong.albumName) { "Invalid raw: No album name" },
                albumSortName = rawSong.albumSortName,
                releaseTypes = rawSong.releaseTypes,
                artistMusicBrainzIds = rawSong.artistMusicBrainzIds,
                artistNames = rawSong.artistNames,
                artistSortNames = rawSong.artistSortNames,
                albumArtistMusicBrainzIds = rawSong.albumArtistMusicBrainzIds,
                albumArtistNames = rawSong.albumArtistNames,
                albumArtistSortNames = rawSong.albumArtistSortNames,
                genreNames = rawSong.genreNames)
    }
}
