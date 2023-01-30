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
 * Defines an Extractor that can load cached music. This is the first step in the music extraction
 * process and is an optimization to avoid the slow [MediaStoreExtractor] and [MetadataExtractor]
 * extraction process.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface CacheExtractor {
    /** Initialize the Extractor by reading the cache data into memory. */
    suspend fun init()

    /**
     * Finalize the Extractor by writing the newly-loaded [RealSong.Raw]s back into the cache,
     * alongside freeing up memory.
     * @param rawSongs The songs to write into the cache.
     */
    suspend fun finalize(rawSongs: List<RealSong.Raw>)

    /**
     * Use the cache to populate the given [RealSong.Raw].
     * @param rawSong The [RealSong.Raw] to attempt to populate. Note that this [RealSong.Raw] will
     * only contain the bare minimum information required to load a cache entry.
     * @return An [ExtractionResult] representing the result of the operation.
     * [ExtractionResult.PARSED] is not returned.
     */
    fun populate(rawSong: RealSong.Raw): ExtractionResult

    companion object {
        /**
         * Create an instance with optional read-capacity.
         * @param context [Context] required.
         * @param readable Whether the new [CacheExtractor] should be able to read cached entries.
         * @return A new [CacheExtractor] with the specified configuration.
         */
        fun from(context: Context, readable: Boolean): CacheExtractor =
            if (readable) {
                ReadWriteCacheExtractor(context)
            } else {
                WriteOnlyCacheExtractor(context)
            }
    }
}

/**
 * A [CacheExtractor] only capable of writing to the cache. This can be used to load music with
 * without the cache if the user desires.
 * @param context [Context] required to read the cache database.
 * @see CacheExtractor
 * @author Alexander Capehart (OxygenCobalt)
 */
private open class WriteOnlyCacheExtractor(private val context: Context) : CacheExtractor {
    protected val cacheDao: CacheDao by lazy { CacheDatabase.getInstance(context).cacheDao() }

    override suspend fun init() {
        // Nothing to do.
    }

    override suspend fun finalize(rawSongs: List<RealSong.Raw>) {
        try {
            // Still write out whatever data was extracted.
            cacheDao.nukeCache()
            cacheDao.insertCache(rawSongs.map(CachedSong::fromRaw))
        } catch (e: Exception) {
            logE("Unable to save cache database.")
            logE(e.stackTraceToString())
        }
    }

    override fun populate(rawSong: RealSong.Raw) =
        // Nothing to do.
        ExtractionResult.NONE
}

/**
 * A [CacheExtractor] that supports reading from and writing to the cache.
 * @param context [Context] required to load
 * @see CacheExtractor
 * @author Alexander Capehart
 */
private class ReadWriteCacheExtractor(private val context: Context) :
    WriteOnlyCacheExtractor(context) {
    private var cacheMap: Map<Long, CachedSong>? = null
    private var invalidate = false

    override suspend fun init() {
        try {
            // Faster to load the whole database into memory than do a query on each
            // populate call.
            val cache = cacheDao.readCache()
            cacheMap = buildMap {
                for (cachedSong in cache) {
                    put(cachedSong.mediaStoreId, cachedSong)
                }
            }
        } catch (e: Exception) {
            logE("Unable to load cache database.")
            logE(e.stackTraceToString())
        }
    }

    override suspend fun finalize(rawSongs: List<RealSong.Raw>) {
        cacheMap = null
        // Same some time by not re-writing the cache if we were able to create the entire
        // library from it. If there is even just one song we could not populate from the
        // cache, then we will re-write it.
        if (invalidate) {
            logD("Cache was invalidated during loading, rewriting")
            super.finalize(rawSongs)
        }
    }

    override fun populate(rawSong: RealSong.Raw): ExtractionResult {
        val map = cacheMap ?: return ExtractionResult.NONE

        // For a cached raw song to be used, it must exist within the cache and have matching
        // addition and modification timestamps. Technically the addition timestamp doesn't
        // exist, but to safeguard against possible OEM-specific timestamp incoherence, we
        // check for it anyway.
        val cachedSong = map[rawSong.mediaStoreId]
        if (cachedSong != null &&
            cachedSong.dateAdded == rawSong.dateAdded &&
            cachedSong.dateModified == rawSong.dateModified) {
            cachedSong.copyToRaw(rawSong)
            return ExtractionResult.CACHED
        }

        // We could not populate this song. This means our cache is stale and should be
        // re-written with newly-loaded music.
        invalidate = true
        return ExtractionResult.NONE
    }
}

@Database(entities = [CachedSong::class], version = 27, exportSchema = false)
private abstract class CacheDatabase : RoomDatabase() {
    abstract fun cacheDao(): CacheDao

    companion object {
        @Volatile private var INSTANCE: CacheDatabase? = null

        /**
         * Get/create the shared instance of this database.
         * @param context [Context] required.
         */
        fun getInstance(context: Context): CacheDatabase {
            val instance = INSTANCE
            if (instance != null) {
                return instance
            }

            synchronized(this) {
                val newInstance =
                    Room.databaseBuilder(
                            context.applicationContext,
                            CacheDatabase::class.java,
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
private interface CacheDao {
    @Query("SELECT * FROM ${CachedSong.TABLE_NAME}") suspend fun readCache(): List<CachedSong>
    @Query("DELETE FROM ${CachedSong.TABLE_NAME}") suspend fun nukeCache()
    @Insert suspend fun insertCache(songs: List<CachedSong>)
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
