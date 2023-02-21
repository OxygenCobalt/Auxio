/*
 * Copyright (c) 2023 Auxio Project
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
 
package org.oxycblt.auxio.music.cache

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import org.oxycblt.auxio.music.metadata.Date
import org.oxycblt.auxio.music.metadata.correctWhitespace
import org.oxycblt.auxio.music.metadata.splitEscaped
import org.oxycblt.auxio.music.model.RawSong

@Database(entities = [CachedSong::class], version = 27, exportSchema = false)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun cachedSongsDao(): CachedSongsDao
}

@Dao
interface CachedSongsDao {
    @Query("SELECT * FROM ${CachedSong.TABLE_NAME}") suspend fun readSongs(): List<CachedSong>
    @Query("DELETE FROM ${CachedSong.TABLE_NAME}") suspend fun nukeSongs()
    @Insert suspend fun insertSongs(songs: List<CachedSong>)
}

@Entity(tableName = CachedSong.TABLE_NAME)
@TypeConverters(CachedSong.Converters::class)
data class CachedSong(
    /**
     * The ID of the [RawSong]'s audio file, obtained from MediaStore. Note that this ID is highly
     * unstable and should only be used for accessing the audio file.
     */
    @PrimaryKey var mediaStoreId: Long,
    /** @see RawSong.dateAdded */
    var dateAdded: Long,
    /** The latest date the [RawSong]'s audio file was modified, as a unix epoch timestamp. */
    var dateModified: Long,
    /** @see RawSong.size */
    var size: Long? = null,
    /** @see RawSong */
    var durationMs: Long,
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
    fun copyToRaw(rawSong: RawSong): CachedSong {
        rawSong.musicBrainzId = musicBrainzId
        rawSong.name = name
        rawSong.sortName = sortName

        rawSong.size = size
        rawSong.durationMs = durationMs

        rawSong.track = track
        rawSong.disc = disc
        rawSong.subtitle = subtitle
        rawSong.date = date

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
        return this
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
        const val TABLE_NAME = "cached_songs"

        fun fromRaw(rawSong: RawSong) =
            CachedSong(
                mediaStoreId =
                    requireNotNull(rawSong.mediaStoreId) { "Invalid raw: No MediaStore ID" },
                dateAdded = requireNotNull(rawSong.dateAdded) { "Invalid raw: No date added" },
                dateModified =
                    requireNotNull(rawSong.dateModified) { "Invalid raw: No date modified" },
                musicBrainzId = rawSong.musicBrainzId,
                name = requireNotNull(rawSong.name) { "Invalid raw: No name" },
                sortName = rawSong.sortName,
                size = rawSong.size,
                durationMs = requireNotNull(rawSong.durationMs) { "Invalid raw: No duration" },
                track = rawSong.track,
                disc = rawSong.disc,
                subtitle = rawSong.subtitle,
                date = rawSong.date,
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
