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
import org.oxycblt.auxio.music.stack.AudioFile
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

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun updateTags(tags: Tags)
}

@Entity
@TypeConverters(Tags.Converters::class)
data class Tags(
    /**
     * The Uri of the [AudioFile]'s audio file, obtained from SAF. This should ideally be a black box
     * only used for comparison.
     */
    @PrimaryKey val uri: String,
    /** The latest date the [AudioFile]'s audio file was modified, as a unix epoch timestamp. */
    val dateModified: Long,
    /** @see AudioFile */
    val durationMs: Long,
    /** @see AudioFile.replayGainTrackAdjustment */
    val replayGainTrackAdjustment: Float? = null,
    /** @see AudioFile.replayGainAlbumAdjustment */
    val replayGainAlbumAdjustment: Float? = null,
    /** @see AudioFile.musicBrainzId */
    var musicBrainzId: String? = null,
    /** @see AudioFile.name */
    var name: String,
    /** @see AudioFile.sortName */
    var sortName: String? = null,
    /** @see AudioFile.track */
    var track: Int? = null,
    /** @see AudioFile.name */
    var disc: Int? = null,
    /** @See AudioFile.subtitle */
    var subtitle: String? = null,
    /** @see AudioFile.date */
    var date: Date? = null,
    /** @see AudioFile.albumMusicBrainzId */
    var albumMusicBrainzId: String? = null,
    /** @see AudioFile.albumName */
    var albumName: String,
    /** @see AudioFile.albumSortName */
    var albumSortName: String? = null,
    /** @see AudioFile.releaseTypes */
    var releaseTypes: List<String> = listOf(),
    /** @see AudioFile.artistMusicBrainzIds */
    var artistMusicBrainzIds: List<String> = listOf(),
    /** @see AudioFile.artistNames */
    var artistNames: List<String> = listOf(),
    /** @see AudioFile.artistSortNames */
    var artistSortNames: List<String> = listOf(),
    /** @see AudioFile.albumArtistMusicBrainzIds */
    var albumArtistMusicBrainzIds: List<String> = listOf(),
    /** @see AudioFile.albumArtistNames */
    var albumArtistNames: List<String> = listOf(),
    /** @see AudioFile.albumArtistSortNames */
    var albumArtistSortNames: List<String> = listOf(),
    /** @see AudioFile.genreNames */
    var genreNames: List<String> = listOf()
) {
    fun copyToRaw(audioFile: AudioFile) {
        audioFile.musicBrainzId = musicBrainzId
        audioFile.name = name
        audioFile.sortName = sortName

        audioFile.durationMs = durationMs

        audioFile.replayGainTrackAdjustment = replayGainTrackAdjustment
        audioFile.replayGainAlbumAdjustment = replayGainAlbumAdjustment

        audioFile.track = track
        audioFile.disc = disc
        audioFile.subtitle = subtitle
        audioFile.date = date

        audioFile.albumMusicBrainzId = albumMusicBrainzId
        audioFile.albumName = albumName
        audioFile.albumSortName = albumSortName
        audioFile.releaseTypes = releaseTypes

        audioFile.artistMusicBrainzIds = artistMusicBrainzIds
        audioFile.artistNames = artistNames
        audioFile.artistSortNames = artistSortNames

        audioFile.albumArtistMusicBrainzIds = albumArtistMusicBrainzIds
        audioFile.albumArtistNames = albumArtistNames
        audioFile.albumArtistSortNames = albumArtistSortNames

        audioFile.genreNames = genreNames
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
        fun fromRaw(audioFile: AudioFile) =
            Tags(
                uri = audioFile.deviceFile.uri.toString(),
                dateModified = audioFile.deviceFile.lastModified,
                musicBrainzId = audioFile.musicBrainzId,
                name = requireNotNull(audioFile.name) { "Invalid raw: No name" },
                sortName = audioFile.sortName,
                durationMs = requireNotNull(audioFile.durationMs) { "Invalid raw: No duration" },
                replayGainTrackAdjustment = audioFile.replayGainTrackAdjustment,
                replayGainAlbumAdjustment = audioFile.replayGainAlbumAdjustment,
                track = audioFile.track,
                disc = audioFile.disc,
                subtitle = audioFile.subtitle,
                date = audioFile.date,
                albumMusicBrainzId = audioFile.albumMusicBrainzId,
                albumName = requireNotNull(audioFile.albumName) { "Invalid raw: No album name" },
                albumSortName = audioFile.albumSortName,
                releaseTypes = audioFile.releaseTypes,
                artistMusicBrainzIds = audioFile.artistMusicBrainzIds,
                artistNames = audioFile.artistNames,
                artistSortNames = audioFile.artistSortNames,
                albumArtistMusicBrainzIds = audioFile.albumArtistMusicBrainzIds,
                albumArtistNames = audioFile.albumArtistNames,
                albumArtistSortNames = audioFile.albumArtistSortNames,
                genreNames = audioFile.genreNames)
    }
}
