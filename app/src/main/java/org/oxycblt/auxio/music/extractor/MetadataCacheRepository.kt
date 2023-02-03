/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.music.extractor

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.library.RealSong
import org.oxycblt.auxio.music.metadata.Date
import org.oxycblt.auxio.music.parsing.correctWhitespace
import org.oxycblt.auxio.music.parsing.splitEscaped
import org.oxycblt.auxio.util.*

/**
 * A cache of music metadata obtained in prior music loading operations. Obtain an instance with
 * [MetadataCacheRepository].
 * @author Alexander Capehart (OxygenCobalt)
 */
interface MetadataCache {
    /** Whether this cache has encountered a [RealSong.Raw] that did not have a cache entry. */
    val invalidated: Boolean

    /**
     * Populate a [RealSong.Raw] from a cache entry, if it exists.
     * @param rawSong The [RealSong.Raw] to populate.
     * @return true if a cache entry could be applied to [rawSong], false otherwise.
     */
    fun populate(rawSong: RealSong.Raw): Boolean
}

private class RealMetadataCache(cachedSongs: List<CachedSong>) : MetadataCache {
    private val cacheMap = buildMap {
        for (cachedSong in cachedSongs) {
            put(cachedSong.mediaStoreId, cachedSong)
        }
    }

    override var invalidated = false
    override fun populate(rawSong: RealSong.Raw): Boolean {

        // For a cached raw song to be used, it must exist within the cache and have matching
        // addition and modification timestamps. Technically the addition timestamp doesn't
        // exist, but to safeguard against possible OEM-specific timestamp incoherence, we
        // check for it anyway.
        val cachedSong = cacheMap[rawSong.mediaStoreId]
        if (cachedSong != null &&
            cachedSong.dateAdded == rawSong.dateAdded &&
            cachedSong.dateModified == rawSong.dateModified) {
            cachedSong.copyToRaw(rawSong)
            return true
        }

        // We could not populate this song. This means our cache is stale and should be
        // re-written with newly-loaded music.
        invalidated = true
        return false
    }
}

/**
 * A repository allowing access to cached metadata obtained in prior music loading operations.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface MetadataCacheRepository {
    /**
     * Read the current [MetadataCache], if it exists.
     * @return The stored [MetadataCache], or null if it could not be obtained.
     */
    suspend fun readCache(): MetadataCache?

    /**
     * Write the list of newly-loaded [RealSong.Raw]s to the cache, replacing the prior data.
     * @param rawSongs The [rawSongs] to write to the cache.
     */
    suspend fun writeCache(rawSongs: List<RealSong.Raw>)

    companion object {
        /**
         * Create a framework-backed instance.
         * @param context [Context] required.
         * @return A new instance.
         */
        fun from(context: Context): MetadataCacheRepository = RealMetadataCacheRepository(context)
    }
}

private class RealMetadataCacheRepository(private val context: Context) : MetadataCacheRepository {
    private val cachedSongsDao: CachedSongsDao by lazy {
        MetadataCacheDatabase.getInstance(context).cachedSongsDao()
    }

    override suspend fun readCache() =
        try {
            // Faster to load the whole database into memory than do a query on each
            // populate call.
            RealMetadataCache(cachedSongsDao.readSongs())
        } catch (e: Exception) {
            logE("Unable to load cache database.")
            logE(e.stackTraceToString())
            null
        }

    override suspend fun writeCache(rawSongs: List<RealSong.Raw>) {
        try {
            // Still write out whatever data was extracted.
            cachedSongsDao.nukeSongs()
            cachedSongsDao.insertSongs(rawSongs.map(CachedSong::fromRaw))
        } catch (e: Exception) {
            logE("Unable to save cache database.")
            logE(e.stackTraceToString())
        }
    }
}

@Database(entities = [CachedSong::class], version = 27, exportSchema = false)
private abstract class MetadataCacheDatabase : RoomDatabase() {
    abstract fun cachedSongsDao(): CachedSongsDao

    companion object {
        @Volatile private var INSTANCE: MetadataCacheDatabase? = null

        /**
         * Get/create the shared instance of this database.
         * @param context [Context] required.
         */
        fun getInstance(context: Context): MetadataCacheDatabase {
            val instance = INSTANCE
            if (instance != null) {
                return instance
            }

            synchronized(this) {
                val newInstance =
                    Room.databaseBuilder(
                            context.applicationContext,
                            MetadataCacheDatabase::class.java,
                            "auxio_metadata_cache.db")
                        .fallbackToDestructiveMigration()
                        .fallbackToDestructiveMigrationFrom(0)
                        .fallbackToDestructiveMigrationOnDowngrade()
                        .build()
                INSTANCE = newInstance
                return newInstance
            }
        }
    }
}

@Dao
private interface CachedSongsDao {
    @Query("SELECT * FROM ${CachedSong.TABLE_NAME}") suspend fun readSongs(): List<CachedSong>
    @Query("DELETE FROM ${CachedSong.TABLE_NAME}") suspend fun nukeSongs()
    @Insert suspend fun insertSongs(songs: List<CachedSong>)
}

@Entity(tableName = CachedSong.TABLE_NAME)
@TypeConverters(CachedSong.Converters::class)
private data class CachedSong(
    /**
     * The ID of the [Song]'s audio file, obtained from MediaStore. Note that this ID is highly
     * unstable and should only be used for accessing the audio file.
     */
    @PrimaryKey var mediaStoreId: Long,
    /** @see RealSong.Raw.dateAdded */
    var dateAdded: Long,
    /** The latest date the [Song]'s audio file was modified, as a unix epoch timestamp. */
    var dateModified: Long,
    /** @see RealSong.Raw.size */
    var size: Long? = null,
    /** @see RealSong.Raw */
    var durationMs: Long,
    /** @see RealSong.Raw.musicBrainzId */
    var musicBrainzId: String? = null,
    /** @see RealSong.Raw.name */
    var name: String,
    /** @see RealSong.Raw.sortName */
    var sortName: String? = null,
    /** @see RealSong.Raw.track */
    var track: Int? = null,
    /** @see RealSong.Raw.name */
    var disc: Int? = null,
    /** @See RealSong.Raw.subtitle */
    var subtitle: String? = null,
    /** @see RealSong.Raw.date */
    var date: Date? = null,
    /** @see RealSong.Raw.albumMusicBrainzId */
    var albumMusicBrainzId: String? = null,
    /** @see RealSong.Raw.albumName */
    var albumName: String,
    /** @see RealSong.Raw.albumSortName */
    var albumSortName: String? = null,
    /** @see RealSong.Raw.releaseTypes */
    var releaseTypes: List<String> = listOf(),
    /** @see RealSong.Raw.artistMusicBrainzIds */
    var artistMusicBrainzIds: List<String> = listOf(),
    /** @see RealSong.Raw.artistNames */
    var artistNames: List<String> = listOf(),
    /** @see RealSong.Raw.artistSortNames */
    var artistSortNames: List<String> = listOf(),
    /** @see RealSong.Raw.albumArtistMusicBrainzIds */
    var albumArtistMusicBrainzIds: List<String> = listOf(),
    /** @see RealSong.Raw.albumArtistNames */
    var albumArtistNames: List<String> = listOf(),
    /** @see RealSong.Raw.albumArtistSortNames */
    var albumArtistSortNames: List<String> = listOf(),
    /** @see RealSong.Raw.genreNames */
    var genreNames: List<String> = listOf()
) {
    fun copyToRaw(rawSong: RealSong.Raw): CachedSong {
        rawSong.musicBrainzId = musicBrainzId
        rawSong.name = name
        rawSong.sortName = sortName

        rawSong.size = size
        rawSong.durationMs = durationMs

        rawSong.track = track
        rawSong.disc = disc
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

        fun fromRaw(rawSong: RealSong.Raw) =
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
