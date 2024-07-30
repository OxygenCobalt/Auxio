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

        fun populateFileInfo(rawSong: RawSong): Boolean

        fun populateTags(rawSong: RawSong)
    }

    data class Constraints(val excludeNonMusic: Boolean, val musicDirs: MusicDirectories)

    companion object {
        /**
         * Create a framework-backed instance.
         *
         * @param context [Context] required.
         * @param pathInterpreterFactory A [MediaStorePathInterpreter.Factory] to use.
         * @return A new [MediaStoreExtractor] that will work best on the device's API level.
         */
        fun from(
            context: Context,
            pathInterpreterFactory: MediaStorePathInterpreter.Factory
        ): MediaStoreExtractor {
            val tagInterpreterFactory =
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Api30TagInterpreter.Factory()
                    else -> Api21TagInterpreter.Factory()
                }

            return MediaStoreExtractorImpl(context, pathInterpreterFactory, tagInterpreterFactory)
        }
    }
}

private class MediaStoreExtractorImpl(
    private val context: Context,
    private val mediaStorePathInterpreterFactory: MediaStorePathInterpreter.Factory,
    private val tagInterpreterFactory: TagInterpreter.Factory
) : MediaStoreExtractor {
    override suspend fun query(
        constraints: MediaStoreExtractor.Constraints
    ): MediaStoreExtractor.Query {
        val start = System.currentTimeMillis()

        val projection =
            BASE_PROJECTION +
                mediaStorePathInterpreterFactory.projection +
                tagInterpreterFactory.projection
        var uniSelector = BASE_SELECTOR
        var uniArgs = listOf<String>()

        // Filter out audio that is not music, if enabled.
        if (constraints.excludeNonMusic) {
            logD("Excluding non-music")
            uniSelector += " AND ${MediaStore.Audio.AudioColumns.IS_MUSIC}=1"
        }

        // Set up the projection to follow the music directory configuration.
        if (constraints.musicDirs.dirs.isNotEmpty()) {
            val pathSelector =
                mediaStorePathInterpreterFactory.createSelector(constraints.musicDirs.dirs)
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
            mediaStorePathInterpreterFactory.wrap(cursor),
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
            if (!query.populateFileInfo(rawSong)) {
                continue
            }
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
        private val mediaStorePathInterpreter: MediaStorePathInterpreter,
        private val tagInterpreter: TagInterpreter,
        private val genreNamesMap: Map<Long, String>
    ) : MediaStoreExtractor.Query {
        private val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
        private val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
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

        override fun populateFileInfo(rawSong: RawSong): Boolean {
            rawSong.mediaStoreId = cursor.getLong(idIndex)
            rawSong.dateAdded = cursor.getLong(dateAddedIndex)
            rawSong.dateModified = cursor.getLong(dateModifiedIndex)
            rawSong.extensionMimeType = cursor.getString(mimeTypeIndex)
            rawSong.albumMediaStoreId = cursor.getLong(albumIdIndex)
            rawSong.path = mediaStorePathInterpreter.extract() ?: return false
            return true
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
            // anything to fix this, really. We also can't really filter it out, since how can we
            // know when it corresponds to the folder and not, say, Low Roar's breakout album "0"?
            // Also, on some devices it's literally just null. To maintain behavior sanity just
            // replicate the majority behavior described prior.
            rawSong.albumName =
                cursor.getStringOrNull(albumIndex)
                    ?: requireNotNull(rawSong.path?.name) { "Invalid raw: No path" }
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

/**
 * Wrapper around a [Cursor] that interprets certain tags on a per-API basis.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
private sealed interface TagInterpreter {
    /**
     * Populate the [RawSong] with version-specific tags.
     *
     * @param rawSong The [RawSong] to populate.
     */
    fun populate(rawSong: RawSong)

    interface Factory {
        /** The columns that must be added to a query to support this interpreter. */
        val projection: Array<String>

        /**
         * Wrap a [Cursor] with this interpreter. This cursor should be the result of a query
         * containing the columns specified by [projection].
         *
         * @param cursor The [Cursor] to wrap.
         * @return A new [TagInterpreter] that will work best on the device's API level.
         */
        fun wrap(cursor: Cursor): TagInterpreter
    }
}

private class Api21TagInterpreter(private val cursor: Cursor) : TagInterpreter {
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

    class Factory : TagInterpreter.Factory {
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
     * @return The disc number extracted from the combined integer field, or null if the value was
     *   zero.
     */
    private fun Int.unpackDiscNo() = transformPositionField(div(1000), null)
}

private class Api30TagInterpreter(private val cursor: Cursor) : TagInterpreter {
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

    class Factory : TagInterpreter.Factory {
        override val projection: Array<String>
            get() =
                arrayOf(
                    MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER,
                    MediaStore.Audio.AudioColumns.DISC_NUMBER)

        override fun wrap(cursor: Cursor): TagInterpreter = Api30TagInterpreter(cursor)
    }
}
