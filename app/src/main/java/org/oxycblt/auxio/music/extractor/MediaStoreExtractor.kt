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
import android.database.Cursor
import android.os.Build
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import java.io.File
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.yield
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.music.library.RawSong
import org.oxycblt.auxio.music.metadata.Date
import org.oxycblt.auxio.music.metadata.parseId3v2PositionField
import org.oxycblt.auxio.music.metadata.transformPositionField
import org.oxycblt.auxio.music.storage.Directory
import org.oxycblt.auxio.music.storage.contentResolverSafe
import org.oxycblt.auxio.music.storage.directoryCompat
import org.oxycblt.auxio.music.storage.mediaStoreVolumeNameCompat
import org.oxycblt.auxio.music.storage.safeQuery
import org.oxycblt.auxio.music.storage.storageVolumesCompat
import org.oxycblt.auxio.music.storage.useQuery
import org.oxycblt.auxio.util.getSystemServiceCompat
import org.oxycblt.auxio.util.logD

/**
 * The layer that loads music from the [MediaStore] database. This is an intermediate step in the
 * music extraction process and primarily intended for redundancy for files not natively supported
 * by [MetadataExtractor]. Solely relying on this is not recommended, as it often produces bad
 * metadata.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface MediaStoreExtractor {
    /**
     * Query the media database, initializing this instance in the process.
     * @return The new [Cursor] returned by the media databases.
     */
    suspend fun query(): Cursor

    /**
     * Consume the [Cursor] loaded after [query].
     * @param cache A [MetadataCache] used to avoid extracting metadata for cached songs, or null if
     * no [MetadataCache] was available.
     * @param incompleteSongs A channel where songs that could not be retrieved from the
     * [MetadataCache] should be sent to.
     * @param completeSongs A channel where completed songs should be sent to.
     */
    suspend fun consume(
        cache: MetadataCache?,
        incompleteSongs: Channel<RawSong>,
        completeSongs: Channel<RawSong>
    )

    companion object {
        /**
         * Create a framework-backed instance.
         * @param context [Context] required.
         * @return A new [RealMediaStoreExtractor] that will work best on the device's API level.
         */
        fun from(context: Context): MediaStoreExtractor =
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Api30MediaStoreExtractor(context)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Api29MediaStoreExtractor(context)
                else -> Api21MediaStoreExtractor(context)
            }
    }
}

private abstract class RealMediaStoreExtractor(private val context: Context) : MediaStoreExtractor {
    private var cursor: Cursor? = null
    private var idIndex = -1
    private var titleIndex = -1
    private var displayNameIndex = -1
    private var mimeTypeIndex = -1
    private var sizeIndex = -1
    private var dateAddedIndex = -1
    private var dateModifiedIndex = -1
    private var durationIndex = -1
    private var yearIndex = -1
    private var albumIndex = -1
    private var albumIdIndex = -1
    private var artistIndex = -1
    private var albumArtistIndex = -1
    private val genreNamesMap = mutableMapOf<Long, String>()

    /**
     * The [StorageVolume]s currently scanned by [MediaStore]. This should be used to transform path
     * information from the database into volume-aware paths.
     */
    protected var volumes = listOf<StorageVolume>()
        private set

    override suspend fun query(): Cursor {
        val start = System.currentTimeMillis()
        val musicSettings = MusicSettings.from(context)
        val storageManager = context.getSystemServiceCompat(StorageManager::class)

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
            context.contentResolverSafe
                .safeQuery(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selector,
                    args.toTypedArray())
                .also { cursor = it }
        logD("Song query succeeded [Projected total: ${cursor.count}]")

        // Set up cursor indices for later use.
        idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
        titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
        displayNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
        mimeTypeIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.MIME_TYPE)
        sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.SIZE)
        dateAddedIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_ADDED)
        dateModifiedIndex =
            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_MODIFIED)
        durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
        yearIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.YEAR)
        albumIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
        albumIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)
        artistIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
        albumArtistIndex = cursor.getColumnIndexOrThrow(AUDIO_COLUMN_ALBUM_ARTIST)

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
                        // Assume that a song can't inhabit multiple genre entries, as I doubt
                        // MediaStore is actually aware that songs can have multiple genres.
                        genreNamesMap[cursor.getLong(songIdIndex)] = name
                    }
                }
            }
        }

        volumes = storageManager.storageVolumesCompat
        logD("Finished initialization in ${System.currentTimeMillis() - start}ms")

        return cursor
    }

    override suspend fun consume(
        cache: MetadataCache?,
        incompleteSongs: Channel<RawSong>,
        completeSongs: Channel<RawSong>
    ) {
        val cursor = requireNotNull(cursor) { "Must call query first before running consume" }
        while (cursor.moveToNext()) {
            val rawSong = RawSong()
            populateFileData(cursor, rawSong)
            if (cache?.populate(rawSong) == true) {
                completeSongs.send(rawSong)
            } else {
                populateMetadata(cursor, rawSong)
                incompleteSongs.send(rawSong)
            }
            yield()
        }
        // Free the cursor and signal that no more incomplete songs will be produced by
        // this extractor.
        cursor.close()
        incompleteSongs.close()
        this.cursor = null
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
     * @see addDirToSelector
     */
    protected abstract val dirSelectorTemplate: String

    /**
     * Add a [Directory] to the given list of projection selector arguments.
     * @param dir The [Directory] to add.
     * @param args The destination list to append selector arguments to that are analogous to the
     * given [Directory].
     * @return true if the [Directory] was added, false otherwise.
     * @see dirSelectorTemplate
     */
    protected abstract fun addDirToSelector(dir: Directory, args: MutableList<String>): Boolean

    /**
     * Populate a [RawSong] with the "File Data" of the given [MediaStore] [Cursor], which is the
     * data that cannot be cached. This includes any information not intrinsic to the file and
     * instead dependent on the file-system, which could change without invalidating the cache due
     * to volume additions or removals.
     * @param cursor The [Cursor] to read from.
     * @param rawSong The [RawSong] to populate.
     * @see populateMetadata
     */
    protected open fun populateFileData(cursor: Cursor, rawSong: RawSong) {
        rawSong.mediaStoreId = cursor.getLong(idIndex)
        rawSong.dateAdded = cursor.getLong(dateAddedIndex)
        rawSong.dateModified = cursor.getLong(dateAddedIndex)
        // Try to use the DISPLAY_NAME column to obtain a (probably sane) file name
        // from the android system.
        rawSong.fileName = cursor.getStringOrNull(displayNameIndex)
        rawSong.extensionMimeType = cursor.getString(mimeTypeIndex)
        rawSong.albumMediaStoreId = cursor.getLong(albumIdIndex)
    }

    /**
     * Populate a [RawSong] with the Metadata of the given [MediaStore] [Cursor], which is the data
     * about a [RawSong] that can be cached. This includes any information intrinsic to the file or
     * it's file format, such as music tags.
     * @param cursor The [Cursor] to read from.
     * @param rawSong The [RawSong] to populate.
     * @see populateFileData
     */
    protected open fun populateMetadata(cursor: Cursor, rawSong: RawSong) {
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
        // in, but in practice it is more often "0" (as in /storage/emulated/0), even when it the
        // file is not actually in the root internal storage directory. We can't do anything to
        // fix this, really.
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

private class Api21MediaStoreExtractor(context: Context) : RealMediaStoreExtractor(context) {
    private var trackIndex = -1
    private var dataIndex = -1

    override suspend fun query(): Cursor {
        val cursor = super.query()
        // Set up cursor indices for later use.
        trackIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TRACK)
        dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
        return cursor
    }

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

    override fun populateFileData(cursor: Cursor, rawSong: RawSong) {
        super.populateFileData(cursor, rawSong)

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

    override fun populateMetadata(cursor: Cursor, rawSong: RawSong) {
        super.populateMetadata(cursor, rawSong)
        // See unpackTrackNo/unpackDiscNo for an explanation
        // of how this column is set up.
        val rawTrack = cursor.getIntOrNull(trackIndex)
        if (rawTrack != null) {
            rawTrack.unpackTrackNo()?.let { rawSong.track = it }
            rawTrack.unpackDiscNo()?.let { rawSong.disc = it }
        }
    }
}

/**
 * A [RealMediaStoreExtractor] that implements common behavior supported from API 29 onwards.
 * @param context [Context] required to query the media database.
 * @author Alexander Capehart (OxygenCobalt)
 */
@RequiresApi(Build.VERSION_CODES.Q)
private open class BaseApi29MediaStoreExtractor(context: Context) :
    RealMediaStoreExtractor(context) {
    private var volumeIndex = -1
    private var relativePathIndex = -1

    override suspend fun query(): Cursor {
        val cursor = super.query()
        // Set up cursor indices for later use.
        volumeIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.VOLUME_NAME)
        relativePathIndex =
            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.RELATIVE_PATH)
        return cursor
    }

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

    override fun populateFileData(cursor: Cursor, rawSong: RawSong) {
        super.populateFileData(cursor, rawSong)
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

/**
 * A [RealMediaStoreExtractor] that completes the music loading process in a way compatible with at
 * API
 * 29.
 * @param context [Context] required to query the media database.
 * @author Alexander Capehart (OxygenCobalt)
 */
@RequiresApi(Build.VERSION_CODES.Q)
private open class Api29MediaStoreExtractor(context: Context) :
    BaseApi29MediaStoreExtractor(context) {
    private var trackIndex = -1

    override suspend fun query(): Cursor {
        val cursor = super.query()
        // Set up cursor indices for later use.
        trackIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TRACK)
        return cursor
    }

    override val projection: Array<String>
        get() = super.projection + arrayOf(MediaStore.Audio.AudioColumns.TRACK)

    override fun populateMetadata(cursor: Cursor, rawSong: RawSong) {
        super.populateMetadata(cursor, rawSong)
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

/**
 * A [RealMediaStoreExtractor] that completes the music loading process in a way compatible from API
 * 30 onwards.
 * @param context [Context] required to query the media database.
 * @author Alexander Capehart (OxygenCobalt)
 */
@RequiresApi(Build.VERSION_CODES.R)
private class Api30MediaStoreExtractor(context: Context) : BaseApi29MediaStoreExtractor(context) {
    private var trackIndex: Int = -1
    private var discIndex: Int = -1

    override suspend fun query(): Cursor {
        val cursor = super.query()
        // Set up cursor indices for later use.
        trackIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER)
        discIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISC_NUMBER)
        return cursor
    }

    override val projection: Array<String>
        get() =
            super.projection +
                arrayOf(
                    // API 30 grant us access to the superior CD_TRACK_NUMBER and DISC_NUMBER
                    // fields, which take the place of TRACK.
                    MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER,
                    MediaStore.Audio.AudioColumns.DISC_NUMBER)

    override fun populateMetadata(cursor: Cursor, rawSong: RawSong) {
        super.populateMetadata(cursor, rawSong)
        // Both CD_TRACK_NUMBER and DISC_NUMBER tend to be formatted as they are in
        // the tag itself, which is to say that it is formatted as NN/TT tracks, where
        // N is the number and T is the total. Parse the number while ignoring the
        // total, as we have no use for it.
        cursor.getStringOrNull(trackIndex)?.parseId3v2PositionField()?.let { rawSong.track = it }
        cursor.getStringOrNull(discIndex)?.parseId3v2PositionField()?.let { rawSong.disc = it }
    }
}

/**
 * Unpack the track number from a combined track + disc [Int] field. These fields appear within
 * MediaStore's TRACK column, and combine the track and disc value into a single field where the
 * disc number is the 4th+ digit.
 * @return The track number extracted from the combined integer value, or null if the value was
 * zero.
 */
private fun Int.unpackTrackNo() = transformPositionField(mod(1000), null)

/**
 * Unpack the disc number from a combined track + disc [Int] field. These fields appear within
 * MediaStore's TRACK column, and combine the track and disc value into a single field where the
 * disc number is the 4th+ digit.
 * @return The disc number extracted from the combined integer field, or null if the value was zero.
 */
private fun Int.unpackDiscNo() = transformPositionField(div(1000), null)
