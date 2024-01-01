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
import android.provider.MediaStore
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import java.io.File
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.yield
import org.oxycblt.auxio.music.cache.Cache
import org.oxycblt.auxio.music.device.RawSong
import org.oxycblt.auxio.music.dirs.MusicDirectories
import org.oxycblt.auxio.music.info.Date
import org.oxycblt.auxio.music.metadata.parseId3v2PositionField
import org.oxycblt.auxio.music.metadata.transformPositionField
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.sendWithTimeout

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
     * @param constraints Configuration parameter to restrict what music should be ignored when
     *   querying.
     * @return A new [Query] returned from the media database.
     */
    suspend fun query(constraints: Constraints): Query

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

    data class Constraints(val excludeNonMusic: Boolean, val musicDirs: MusicDirectories)

    companion object {
        /**
         * Create a framework-backed instance.
         *
         * @param context [Context] required.
         * @param volumeManager [VolumeManager] required.
         * @return A new [MediaStoreExtractor] that will work best on the device's API level.
         */
        fun from(context: Context, volumeManager: VolumeManager): MediaStoreExtractor {
            val pathInterpreter =
                when {
                    // Huawei violates the API docs and prevents you from accessing the new path
                    // fields without first granting access to them through SAF. Fall back to DATA
                    // instead.
                    Build.MANUFACTURER.equals("huawei", ignoreCase = true) ||
                        Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ->
                        DataPathInterpreter.Factory(volumeManager)
                    else -> VolumePathInterpreter.Factory(volumeManager)
                }

            val volumeInterpreter =
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Api30TagInterpreter.Factory()
                    else -> Api21TagInterpreter.Factory()
                }

            return MediaStoreExtractorImpl(context, pathInterpreter, volumeInterpreter)
        }
    }
}

private class MediaStoreExtractorImpl(
    private val context: Context,
    private val pathInterpreterFactory: PathInterpreterFactory,
    private val tagInterpreterFactory: TagInterpreterFactory
) : MediaStoreExtractor {
    override suspend fun query(
        constraints: MediaStoreExtractor.Constraints
    ): MediaStoreExtractor.Query {
        val start = System.currentTimeMillis()

        val projection =
            BASE_PROJECTION + pathInterpreterFactory.projection + tagInterpreterFactory.projection
        var uniSelector = BASE_SELECTOR
        var uniArgs = listOf<String>()

        // Filter out audio that is not music, if enabled.
        if (constraints.excludeNonMusic) {
            logD("Excluding non-music")
            uniSelector += " AND ${MediaStore.Audio.AudioColumns.IS_MUSIC}=1"
        }

        // Set up the projection to follow the music directory configuration.
        if (constraints.musicDirs.dirs.isNotEmpty()) {
            val pathSelector = pathInterpreterFactory.createSelector(constraints.musicDirs.dirs)
            if (pathSelector != null) {
                logD("Must select for directories")
                uniSelector += " AND "
                if (!constraints.musicDirs.shouldInclude) {
                    logD("Excluding directories in selector")
                    // Without a NOT, the query will be restricted to the specified paths, resulting
                    // in the "Include" mode. With a NOT, the specified paths will not be included,
                    // resulting in the "Exclude" mode.
                    uniSelector += "NOT "
                }
                uniSelector += " (${pathSelector.template})"
                uniArgs = pathSelector.args
            }
        }

        // Now we can actually query MediaStore.
        logD(
            "Starting song query [proj=${projection.toList()}, selector=$uniSelector, args=$uniArgs]")
        val cursor =
            context.contentResolverSafe.safeQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                uniSelector,
                uniArgs.toTypedArray())
        logD("Successfully queried for ${cursor.count} songs")

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

        logD("Read ${genreNamesMap.values.distinct().size} genres from MediaStore")
        logD("Finished initialization in ${System.currentTimeMillis() - start}ms")
        return QueryImpl(
            cursor,
            pathInterpreterFactory.wrap(cursor),
            tagInterpreterFactory.wrap(cursor),
            genreNamesMap)
    }

    override suspend fun consume(
        query: MediaStoreExtractor.Query,
        cache: Cache?,
        incompleteSongs: Channel<RawSong>,
        completeSongs: Channel<RawSong>
    ) {
        while (query.moveToNext()) {
            val rawSong = RawSong()
            query.populateFileInfo(rawSong)
            if (cache?.populate(rawSong) == true) {
                completeSongs.sendWithTimeout(rawSong)
            } else {
                query.populateTags(rawSong)
                incompleteSongs.sendWithTimeout(rawSong)
            }
            yield()
        }
        // Free the cursor and signal that no more incomplete songs will be produced by
        // this extractor.
        query.close()
    }

    class QueryImpl(
        private val cursor: Cursor,
        private val pathInterpreter: PathInterpreter,
        private val tagInterpreter: TagInterpreter,
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

        override val projectedTotal = cursor.count

        override fun moveToNext() = cursor.moveToNext()

        override fun close() = cursor.close()

        override fun populateFileInfo(rawSong: RawSong) {
            rawSong.mediaStoreId = cursor.getLong(idIndex)
            rawSong.dateAdded = cursor.getLong(dateAddedIndex)
            rawSong.dateModified = cursor.getLong(dateModifiedIndex)
            // Try to use the DISPLAY_NAME column to obtain a (probably sane) file name
            // from the android system.
            rawSong.fileName = cursor.getStringOrNull(displayNameIndex)
            rawSong.extensionMimeType = cursor.getString(mimeTypeIndex)
            rawSong.albumMediaStoreId = cursor.getLong(albumIdIndex)
            pathInterpreter.populate(rawSong)
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
            // Get version/device-specific tags
            tagInterpreter.populate(rawSong)
        }
    }

    companion object {
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

        /**
         * The base selector that works across all versions of android. Does not exclude
         * directories.
         */
        private const val BASE_SELECTOR = "NOT ${MediaStore.Audio.Media.SIZE}=0"

        /** The base projection that works across all versions of android. */
        private val BASE_PROJECTION =
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
    }
}

interface Interpreter {
    fun populate(rawSong: RawSong)
}

interface InterpreterFactory {
    val projection: Array<String>

    fun wrap(cursor: Cursor): Interpreter
}

interface PathInterpreterFactory : InterpreterFactory {
    override fun wrap(cursor: Cursor): PathInterpreter

    fun createSelector(paths: List<Path>): Selector?

    data class Selector(val template: String, val args: List<String>)
}

interface TagInterpreterFactory : InterpreterFactory {
    override fun wrap(cursor: Cursor): TagInterpreter
}

sealed interface PathInterpreter : Interpreter

class DataPathInterpreter(private val cursor: Cursor, private val volumeManager: VolumeManager) :
    PathInterpreter {
    private val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
    private val volumes = volumeManager.getVolumes()

    override fun populate(rawSong: RawSong) {
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
            val volumePath = (volume.components ?: continue).toString()
            val strippedPath = rawPath.removePrefix(volumePath)
            if (strippedPath != rawPath) {
                rawSong.directory = Path(volume, Components.parseUnix(strippedPath))
                break
            }
        }
    }

    class Factory(private val volumeManager: VolumeManager) : PathInterpreterFactory {
        override val projection: Array<String>
            get() = arrayOf(MediaStore.Audio.AudioColumns.DATA)

        override fun createSelector(paths: List<Path>): PathInterpreterFactory.Selector? {
            val args = mutableListOf<String>()
            var template = ""
            for (i in paths.indices) {
                val path = paths[i]
                val volume = path.volume.components ?: continue
                template +=
                    if (i == 0) {
                        "${MediaStore.Audio.AudioColumns.DATA} LIKE ?"
                    } else {
                        " OR ${MediaStore.Audio.AudioColumns.DATA} LIKE ?"
                    }
                args.add("${volume}${path.components}%")
            }

            if (template.isEmpty()) {
                return null
            }

            return PathInterpreterFactory.Selector(template, args)
        }

        override fun wrap(cursor: Cursor): PathInterpreter =
            DataPathInterpreter(cursor, volumeManager)
    }
}

class VolumePathInterpreter(private val cursor: Cursor, private val volumeManager: VolumeManager) :
    PathInterpreter {
    private val volumeIndex =
        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.VOLUME_NAME)
    private val relativePathIndex =
        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.RELATIVE_PATH)
    private val volumes = volumeManager.getVolumes()

    override fun populate(rawSong: RawSong) {
        // Find the StorageVolume whose MediaStore name corresponds to this song.
        // This is combined with the plain relative path column to create the directory.
        val volumeName = cursor.getString(volumeIndex)
        val relativePath = cursor.getString(relativePathIndex)
        val volume = volumes.find { it.mediaStoreName == volumeName }
        if (volume != null) {
            rawSong.directory = Path(volume, Components.parseUnix(relativePath))
        }
    }

    class Factory(private val volumeManager: VolumeManager) : PathInterpreterFactory {
        override val projection: Array<String>
            get() =
                arrayOf(
                    // After API 29, we now have access to the volume name and relative
                    // path, which simplifies working with Paths significantly.
                    MediaStore.Audio.AudioColumns.VOLUME_NAME,
                    MediaStore.Audio.AudioColumns.RELATIVE_PATH)

        // The selector should be configured to compare both the volume name and relative path
        // of the given directories, albeit with some conversion to the analogous MediaStore
        // column values.

        override fun createSelector(paths: List<Path>): PathInterpreterFactory.Selector? {
            val args = mutableListOf<String>()
            var template = ""
            for (i in paths.indices) {
                val path = paths[i]
                template =
                    if (i == 0) {
                        "(${MediaStore.Audio.AudioColumns.VOLUME_NAME} LIKE ? " +
                            "AND ${MediaStore.Audio.AudioColumns.RELATIVE_PATH} LIKE ?)"
                    } else {
                        " OR (${MediaStore.Audio.AudioColumns.VOLUME_NAME} LIKE ? " +
                            "AND ${MediaStore.Audio.AudioColumns.RELATIVE_PATH} LIKE ?)"
                    }
                // MediaStore uses a different naming scheme for it's volume column. Convert this
                // directory's volume to it.
                args.add(path.volume.mediaStoreName ?: return null)
                // "%" signifies to accept any DATA value that begins with the Directory's path,
                // thus recursively filtering all files in the directory.
                args.add("${path.components}%")
            }

            if (template.isEmpty()) {
                return null
            }

            return PathInterpreterFactory.Selector(template, args)
        }

        override fun wrap(cursor: Cursor): PathInterpreter =
            VolumePathInterpreter(cursor, volumeManager)
    }
}

sealed interface TagInterpreter : Interpreter

class Api21TagInterpreter(private val cursor: Cursor) : TagInterpreter {
    private val trackIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TRACK)

    override fun populate(rawSong: RawSong) {
        // See unpackTrackNo/unpackDiscNo for an explanation
        // of how this column is set up.
        val rawTrack = cursor.getIntOrNull(trackIndex)
        if (rawTrack != null) {
            rawTrack.unpackTrackNo()?.let { rawSong.track = it }
            rawTrack.unpackDiscNo()?.let { rawSong.disc = it }
        }
    }

    class Factory : TagInterpreterFactory {
        override val projection: Array<String>
            get() = arrayOf(MediaStore.Audio.AudioColumns.TRACK)

        override fun wrap(cursor: Cursor): TagInterpreter = Api21TagInterpreter(cursor)
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
}

class Api30TagInterpreter(private val cursor: Cursor) : TagInterpreter {
    private val trackIndex =
        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER)
    private val discIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISC_NUMBER)

    override fun populate(rawSong: RawSong) {
        // Both CD_TRACK_NUMBER and DISC_NUMBER tend to be formatted as they are in
        // the tag itself, which is to say that it is formatted as NN/TT tracks, where
        // N is the number and T is the total. Parse the number while ignoring the
        // total, as we have no use for it.
        cursor.getStringOrNull(trackIndex)?.parseId3v2PositionField()?.let { rawSong.track = it }
        cursor.getStringOrNull(discIndex)?.parseId3v2PositionField()?.let { rawSong.disc = it }
    }

    class Factory : TagInterpreterFactory {
        override val projection: Array<String>
            get() =
                arrayOf(
                    MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER,
                    MediaStore.Audio.AudioColumns.DISC_NUMBER)

        override fun wrap(cursor: Cursor): TagInterpreter = Api30TagInterpreter(cursor)
    }
}
