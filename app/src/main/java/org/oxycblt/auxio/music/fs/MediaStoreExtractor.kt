/*
 * Copyright (c) 2022 Auxio Project
 * MediaStoreExtractor.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.fs

import android.content.Context
import android.database.Cursor
import android.os.Build
import android.os.storage.StorageManager
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import java.io.File
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.yield
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.music.cache.Cache
import org.oxycblt.auxio.music.device.RawSong
import org.oxycblt.auxio.music.metadata.Date
import org.oxycblt.auxio.music.metadata.parseId3v2PositionField
import org.oxycblt.auxio.music.metadata.transformPositionField
import org.oxycblt.auxio.util.getSystemServiceCompat
import org.oxycblt.auxio.util.logD

/**
 * The layer that loads music from the [MediaStore] database. This is an intermediate step in the
 * music extraction process and primarily intended for redundancy for files not natively supported
 * by other extractors. Solely relying on this is not recommended, as it often produces bad
 * metadata.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface MediaStoreExtractor {
    /**
     * Query the media database.
     *
     * @return A new [Query] returned from the media database.
     */
    suspend fun query(): Query

    /**
     * Consume the [Cursor] loaded after [query].
     *
     * @param query The [Query] to consume.
     * @param cache A [Cache] used to avoid extracting metadata for cached songs, or null if no
     *   [Cache] was available.
     * @param incompleteSongs A channel where songs that could not be retrieved from the [Cache]
     *   should be sent to.
     * @param completeSongs A channel where completed songs should be sent to.
     */
    suspend fun consume(
        query: Query,
        cache: Cache?,
        incompleteSongs: Channel<RawSong>,
        completeSongs: Channel<RawSong>
    )

    /** A black-box interface representing a query from the media database. */
    interface Query {
        val projectedTotal: Int
        fun moveToNext(): Boolean
        fun close()
        fun populateFileInfo(rawSong: RawSong)
        fun populateTags(rawSong: RawSong)
    }

    companion object {
        /**
         * Create a framework-backed instance.
         *
         * @param context [Context] required.
         * @param musicSettings [MusicSettings] required.
         * @return A new [MediaStoreExtractor] that will work best on the device's API level.
         */
        fun from(context: Context, musicSettings: MusicSettings): MediaStoreExtractor =
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ->
                    Api30MediaStoreExtractor(context, musicSettings)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                    Api29MediaStoreExtractor(context, musicSettings)
                else -> Api21MediaStoreExtractor(context, musicSettings)
            }
    }
}

private abstract class BaseMediaStoreExtractor(
    protected val context: Context,
    private val musicSettings: MusicSettings
) : MediaStoreExtractor {
    final override suspend fun query(): MediaStoreExtractor.Query {
        val start = System.currentTimeMillis()

        val args = mutableListOf<String>()
        var selector = BASE_SELECTOR

        // Filter out audio that is not music, if enabled.
        if (musicSettings.excludeNonMusic) {
            logD("Excluding non-music")
            selector += " AND ${MediaStore.Audio.AudioColumns.IS_MUSIC}=1"
        }

        // Set up the projection to follow the music directory configuration.
        val dirs = musicSettings.musicDirs
        if (dirs.dirs.isNotEmpty()) {
            selector += " AND "
            if (!dirs.shouldInclude) {
                // Without a NOT, the query will be restricted to the specified paths, resulting
                // in the "Include" mode. With a NOT, the specified paths will not be included,
                // resulting in the "Exclude" mode.
                selector += "NOT "
            }
            selector += " ("

            // Specifying the paths to filter is version-specific, delegate to the concrete
            // implementations.
            for (i in dirs.dirs.indices) {
                if (addDirToSelector(dirs.dirs[i], args)) {
                    selector +=
                        if (i < dirs.dirs.lastIndex) {
                            "$dirSelectorTemplate OR "
                        } else {
                            dirSelectorTemplate
                        }
                }
            }

            selector += ')'
        }

        // Now we can actually query MediaStore.
        logD("Starting song query [proj: ${projection.toList()}, selector: $selector, args: $args]")
        val cursor =
            context.contentResolverSafe.safeQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selector,
                args.toTypedArray())
        logD("Song query succeeded [Projected total: ${cursor.count}]")

        val genreNamesMap = mutableMapOf<Long, String>()

        // Since we can't obtain the genre tag from a song query, we must construct our own
        // equivalent from genre database queries. Theoretically, this isn't needed since
        // MetadataLayer will fill this in for us, but I'd imagine there are some obscure
        // formats where genre support is only really covered by this, so we are forced to
        // bite the O(n^2) complexity here.
        context.contentResolverSafe.useQuery(
            MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Audio.Genres._ID, MediaStore.Audio.Genres.NAME)) { genreCursor ->
                val idIndex = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID)
                val nameIndex = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME)

                while (genreCursor.moveToNext()) {
                    val id = genreCursor.getLong(idIndex)
                    val name = genreCursor.getStringOrNull(nameIndex) ?: continue

                    context.contentResolverSafe.useQuery(
                        MediaStore.Audio.Genres.Members.getContentUri(VOLUME_EXTERNAL, id),
                        arrayOf(MediaStore.Audio.Genres.Members._ID)) { cursor ->
                            val songIdIndex =
                                cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members._ID)

                            while (cursor.moveToNext()) {
                                // Assume that a song can't inhabit multiple genre entries, as I
                                // doubt MediaStore is actually aware that songs can have multiple
                                // genres.
                                genreNamesMap[cursor.getLong(songIdIndex)] = name
                            }
                        }
                }
            }

        logD("Finished initialization in ${System.currentTimeMillis() - start}ms")
        return wrapQuery(cursor, genreNamesMap)
    }

    final override suspend fun consume(
        query: MediaStoreExtractor.Query,
        cache: Cache?,
        incompleteSongs: Channel<RawSong>,
        completeSongs: Channel<RawSong>
    ) {
        while (query.moveToNext()) {
            val rawSong = RawSong()
            query.populateFileInfo(rawSong)
            if (cache?.populate(rawSong) == true) {
                completeSongs.send(rawSong)
            } else {
                query.populateTags(rawSong)
                incompleteSongs.send(rawSong)
            }
            yield()
        }
        // Free the cursor and signal that no more incomplete songs will be produced by
        // this extractor.
        query.close()
        incompleteSongs.close()
    }

    /**
     * The database columns available to all android versions supported by Auxio. Concrete
     * implementations can extend this projection to add version-specific columns.
     */
    protected open val projection: Array<String>
        get() =
            arrayOf(
                // These columns are guaranteed to work on all versions of android
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.DATE_ADDED,
                MediaStore.Audio.AudioColumns.DATE_MODIFIED,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.SIZE,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.MIME_TYPE,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.YEAR,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.ARTIST,
                AUDIO_COLUMN_ALBUM_ARTIST)

    /**
     * The companion template to add to the projection's selector whenever arguments are added by
     * [addDirToSelector].
     *
     * @see addDirToSelector
     */
    protected abstract val dirSelectorTemplate: String

    /**
     * Add a [Directory] to the given list of projection selector arguments.
     *
     * @param dir The [Directory] to add.
     * @param args The destination list to append selector arguments to that are analogous to the
     *   given [Directory].
     * @return true if the [Directory] was added, false otherwise.
     * @see dirSelectorTemplate
     */
    protected abstract fun addDirToSelector(dir: Directory, args: MutableList<String>): Boolean

    protected abstract fun wrapQuery(
        cursor: Cursor,
        genreNamesMap: Map<Long, String>
    ): MediaStoreExtractor.Query

    abstract class Query(
        protected val cursor: Cursor,
        private val genreNamesMap: Map<Long, String>
    ) : MediaStoreExtractor.Query {
        private val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
        private val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
        private val displayNameIndex =
            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
        private val mimeTypeIndex =
            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.MIME_TYPE)
        private val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.SIZE)
        private val dateAddedIndex =
            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_ADDED)
        private val dateModifiedIndex =
            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_MODIFIED)
        private val durationIndex =
            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
        private val yearIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.YEAR)
        private val albumIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
        private val albumIdIndex =
            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)
        private val artistIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
        private val albumArtistIndex = cursor.getColumnIndexOrThrow(AUDIO_COLUMN_ALBUM_ARTIST)

        final override val projectedTotal = cursor.count
        final override fun moveToNext() = cursor.moveToNext()
        final override fun close() = cursor.close()

        override fun populateFileInfo(rawSong: RawSong) {
            rawSong.mediaStoreId = cursor.getLong(idIndex)
            rawSong.dateAdded = cursor.getLong(dateAddedIndex)
            rawSong.dateModified = cursor.getLong(dateModifiedIndex)
            // Try to use the DISPLAY_NAME column to obtain a (probably sane) file name
            // from the android system.
            rawSong.fileName = cursor.getStringOrNull(displayNameIndex)
            rawSong.extensionMimeType = cursor.getString(mimeTypeIndex)
            rawSong.albumMediaStoreId = cursor.getLong(albumIdIndex)
        }

        override fun populateTags(rawSong: RawSong) {
            // Song title
            rawSong.name = cursor.getString(titleIndex)
            // Size (in bytes)
            rawSong.size = cursor.getLong(sizeIndex)
            // Duration (in milliseconds)
            rawSong.durationMs = cursor.getLong(durationIndex)
            // MediaStore only exposes the year value of a file. This is actually worse than it
            // seems, as it means that it will not read ID3v2 TDRC tags or Vorbis DATE comments.
            // This is one of the major weaknesses of using MediaStore, hence the redundancy layers.
            rawSong.date = cursor.getStringOrNull(yearIndex)?.let(Date::from)
            // A non-existent album name should theoretically be the name of the folder it contained
            // in, but in practice it is more often "0" (as in /storage/emulated/0), even when it
            // the file is not actually in the root internal storage directory. We can't do
            // anything to fix this, really.
            rawSong.albumName = cursor.getString(albumIndex)
            // Android does not make a non-existent artist tag null, it instead fills it in
            // as <unknown>, which makes absolutely no sense given how other columns default
            // to null if they are not present. If this column is such, null it so that
            // it's easier to handle later.
            val artist = cursor.getString(artistIndex)
            if (artist != MediaStore.UNKNOWN_STRING) {
                rawSong.artistNames = listOf(artist)
            }
            // The album artist column is nullable and never has placeholder values.
            cursor.getStringOrNull(albumArtistIndex)?.let { rawSong.albumArtistNames = listOf(it) }
            // Get the genre value we had to query for in initialization
            genreNamesMap[rawSong.mediaStoreId]?.let { rawSong.genreNames = listOf(it) }
        }
    }

    companion object {
        /**
         * The base selector that works across all versions of android. Does not exclude
         * directories.
         */
        private const val BASE_SELECTOR = "NOT ${MediaStore.Audio.Media.SIZE}=0"

        /**
         * The album artist of a song. This column has existed since at least API 21, but until API
         * 30 it was an undocumented extension for Google Play Music. This column will work on all
         * versions that Auxio supports.
         */
        @Suppress("InlinedApi")
        private const val AUDIO_COLUMN_ALBUM_ARTIST = MediaStore.Audio.AudioColumns.ALBUM_ARTIST

        /**
         * The external volume. This naming has existed since API 21, but no constant existed for it
         * until API 29. This will work on all versions that Auxio supports.
         */
        @Suppress("InlinedApi") private const val VOLUME_EXTERNAL = MediaStore.VOLUME_EXTERNAL
    }
}

// Note: The separation between version-specific backends may not be the cleanest. To preserve
// speed, we only want to add redundancy on known issues, not with possible issues.

private class Api21MediaStoreExtractor(context: Context, musicSettings: MusicSettings) :
    BaseMediaStoreExtractor(context, musicSettings) {
    override val projection: Array<String>
        get() =
            super.projection +
                arrayOf(
                    MediaStore.Audio.AudioColumns.TRACK,
                    // Below API 29, we are restricted to the absolute path (Called DATA by
                    // MedaStore) when working with audio files.
                    MediaStore.Audio.AudioColumns.DATA)

    // The selector should be configured to convert the given directories instances to their
    // absolute paths and then compare them to DATA.

    override val dirSelectorTemplate: String
        get() = "${MediaStore.Audio.Media.DATA} LIKE ?"

    override fun addDirToSelector(dir: Directory, args: MutableList<String>): Boolean {
        // "%" signifies to accept any DATA value that begins with the Directory's path,
        // thus recursively filtering all files in the directory.
        args.add("${dir.volume.directoryCompat ?: return false}/${dir.relativePath}%")
        return true
    }

    override fun wrapQuery(
        cursor: Cursor,
        genreNamesMap: Map<Long, String>,
    ): MediaStoreExtractor.Query =
        Query(cursor, genreNamesMap, context.getSystemServiceCompat(StorageManager::class))

    private class Query(
        cursor: Cursor,
        genreNamesMap: Map<Long, String>,
        storageManager: StorageManager
    ) : BaseMediaStoreExtractor.Query(cursor, genreNamesMap) {
        // Set up cursor indices for later use.
        private val trackIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TRACK)
        private val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
        private val volumes = storageManager.storageVolumesCompat

        override fun populateFileInfo(rawSong: RawSong) {
            super.populateFileInfo(rawSong)

            val data = cursor.getString(dataIndex)
            // On some OEM devices below API 29, DISPLAY_NAME may not be present. I assume
            // that this only applies to below API 29, as beyond API 29, this column not being
            // present would completely break the scoped storage system. Fill it in with DATA
            // if it's not available.
            if (rawSong.fileName == null) {
                rawSong.fileName = data.substringAfterLast(File.separatorChar, "").ifEmpty { null }
            }

            // Find the volume that transforms the DATA column into a relative path. This is
            // the Directory we will use.
            val rawPath = data.substringBeforeLast(File.separatorChar)
            for (volume in volumes) {
                val volumePath = volume.directoryCompat ?: continue
                val strippedPath = rawPath.removePrefix(volumePath)
                if (strippedPath != rawPath) {
                    rawSong.directory = Directory.from(volume, strippedPath)
                    break
                }
            }
        }

        override fun populateTags(rawSong: RawSong) {
            super.populateTags(rawSong)
            // See unpackTrackNo/unpackDiscNo for an explanation
            // of how this column is set up.
            val rawTrack = cursor.getIntOrNull(trackIndex)
            if (rawTrack != null) {
                rawTrack.unpackTrackNo()?.let { rawSong.track = it }
                rawTrack.unpackDiscNo()?.let { rawSong.disc = it }
            }
        }
    }
}

/**
 * A [BaseMediaStoreExtractor] that implements common behavior supported from API 29 onwards.
 *
 * @param context [Context] required to query the media database.
 * @author Alexander Capehart (OxygenCobalt)
 */
@RequiresApi(Build.VERSION_CODES.Q)
private abstract class BaseApi29MediaStoreExtractor(
    context: Context,
    musicSettings: MusicSettings
) : BaseMediaStoreExtractor(context, musicSettings) {
    override val projection: Array<String>
        get() =
            super.projection +
                arrayOf(
                    // After API 29, we now have access to the volume name and relative
                    // path, which simplifies working with Paths significantly.
                    MediaStore.Audio.AudioColumns.VOLUME_NAME,
                    MediaStore.Audio.AudioColumns.RELATIVE_PATH)

    // The selector should be configured to compare both the volume name and relative path
    // of the given directories, albeit with some conversion to the analogous MediaStore
    // column values.

    override val dirSelectorTemplate: String
        get() =
            "(${MediaStore.Audio.AudioColumns.VOLUME_NAME} LIKE ? " +
                "AND ${MediaStore.Audio.AudioColumns.RELATIVE_PATH} LIKE ?)"

    override fun addDirToSelector(dir: Directory, args: MutableList<String>): Boolean {
        // MediaStore uses a different naming scheme for it's volume column convert this
        // directory's volume to it.
        args.add(dir.volume.mediaStoreVolumeNameCompat ?: return false)
        // "%" signifies to accept any DATA value that begins with the Directory's path,
        // thus recursively filtering all files in the directory.
        args.add("${dir.relativePath}%")
        return true
    }

    abstract class Query(
        cursor: Cursor,
        genreNamesMap: Map<Long, String>,
        storageManager: StorageManager
    ) : BaseMediaStoreExtractor.Query(cursor, genreNamesMap) {
        private val volumeIndex =
            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.VOLUME_NAME)
        private val relativePathIndex =
            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.RELATIVE_PATH)
        private val volumes = storageManager.storageVolumesCompat

        final override fun populateFileInfo(rawSong: RawSong) {
            super.populateFileInfo(rawSong)
            // Find the StorageVolume whose MediaStore name corresponds to this song.
            // This is combined with the plain relative path column to create the directory.
            val volumeName = cursor.getString(volumeIndex)
            val relativePath = cursor.getString(relativePathIndex)
            val volume = volumes.find { it.mediaStoreVolumeNameCompat == volumeName }
            if (volume != null) {
                rawSong.directory = Directory.from(volume, relativePath)
            }
        }
    }
}

/**
 * A [BaseMediaStoreExtractor] that completes the music loading process in a way compatible with at
 * API 29.
 *
 * @param context [Context] required to query the media database.
 * @author Alexander Capehart (OxygenCobalt)
 */
@RequiresApi(Build.VERSION_CODES.Q)
private class Api29MediaStoreExtractor(context: Context, musicSettings: MusicSettings) :
    BaseApi29MediaStoreExtractor(context, musicSettings) {

    override val projection: Array<String>
        get() = super.projection + arrayOf(MediaStore.Audio.AudioColumns.TRACK)

    override fun wrapQuery(
        cursor: Cursor,
        genreNamesMap: Map<Long, String>
    ): MediaStoreExtractor.Query =
        Query(cursor, genreNamesMap, context.getSystemServiceCompat(StorageManager::class))

    private class Query(
        cursor: Cursor,
        genreNamesMap: Map<Long, String>,
        storageManager: StorageManager
    ) : BaseApi29MediaStoreExtractor.Query(cursor, genreNamesMap, storageManager) {
        private val trackIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TRACK)
        override fun populateTags(rawSong: RawSong) {
            super.populateTags(rawSong)
            // This extractor is volume-aware, but does not support the modern track columns.
            // Use the old column instead. See unpackTrackNo/unpackDiscNo for an explanation
            // of how this column is set up.
            val rawTrack = cursor.getIntOrNull(trackIndex)
            if (rawTrack != null) {
                rawTrack.unpackTrackNo()?.let { rawSong.track = it }
                rawTrack.unpackDiscNo()?.let { rawSong.disc = it }
            }
        }
    }
}

/**
 * A [BaseMediaStoreExtractor] that completes the music loading process in a way compatible from API
 * 30 onwards.
 *
 * @param context [Context] required to query the media database.
 * @author Alexander Capehart (OxygenCobalt)
 */
@RequiresApi(Build.VERSION_CODES.R)
private class Api30MediaStoreExtractor(context: Context, musicSettings: MusicSettings) :
    BaseApi29MediaStoreExtractor(context, musicSettings) {
    override val projection: Array<String>
        get() =
            super.projection +
                arrayOf(
                    // API 30 grant us access to the superior CD_TRACK_NUMBER and DISC_NUMBER
                    // fields, which take the place of TRACK.
                    MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER,
                    MediaStore.Audio.AudioColumns.DISC_NUMBER)

    override fun wrapQuery(
        cursor: Cursor,
        genreNamesMap: Map<Long, String>
    ): MediaStoreExtractor.Query =
        Query(cursor, genreNamesMap, context.getSystemServiceCompat(StorageManager::class))

    private class Query(
        cursor: Cursor,
        genreNamesMap: Map<Long, String>,
        storageManager: StorageManager
    ) : BaseApi29MediaStoreExtractor.Query(cursor, genreNamesMap, storageManager) {
        private val trackIndex =
            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER)
        private val discIndex =
            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISC_NUMBER)

        override fun populateTags(rawSong: RawSong) {
            super.populateTags(rawSong)
            // Both CD_TRACK_NUMBER and DISC_NUMBER tend to be formatted as they are in
            // the tag itself, which is to say that it is formatted as NN/TT tracks, where
            // N is the number and T is the total. Parse the number while ignoring the
            // total, as we have no use for it.
            cursor.getStringOrNull(trackIndex)?.parseId3v2PositionField()?.let {
                rawSong.track = it
            }
            cursor.getStringOrNull(discIndex)?.parseId3v2PositionField()?.let { rawSong.disc = it }
        }
    }
}

/**
 * Unpack the track number from a combined track + disc [Int] field. These fields appear within
 * MediaStore's TRACK column, and combine the track and disc value into a single field where the
 * disc number is the 4th+ digit.
 *
 * @return The track number extracted from the combined integer value, or null if the value was
 *   zero.
 */
private fun Int.unpackTrackNo() = transformPositionField(mod(1000), null)

/**
 * Unpack the disc number from a combined track + disc [Int] field. These fields appear within
 * MediaStore's TRACK column, and combine the track and disc value into a single field where the
 * disc number is the 4th+ digit.
 *
 * @return The disc number extracted from the combined integer field, or null if the value was zero.
 */
private fun Int.unpackDiscNo() = transformPositionField(div(1000), null)
