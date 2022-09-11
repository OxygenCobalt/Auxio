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
import org.oxycblt.auxio.music.Date
import org.oxycblt.auxio.music.Directory
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.directoryCompat
import org.oxycblt.auxio.music.mediaStoreVolumeNameCompat
import org.oxycblt.auxio.music.queryCursor
import org.oxycblt.auxio.music.storageVolumesCompat
import org.oxycblt.auxio.music.useQuery
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.contentResolverSafe
import org.oxycblt.auxio.util.getSystemServiceCompat
import org.oxycblt.auxio.util.logD
import java.io.File

/*
 * This file acts as the base for most the black magic required to get a remotely sensible music
 * indexing system while still optimizing for time. I would recommend you leave this file now
 * before you lose your sanity trying to understand the hoops I had to jump through for this system,
 * but if you really want to stay, here's a debrief on why this code is so awful.
 *
 * MediaStore is not a good API. It is not even a bad API. Calling it a bad API is an insult to
 * other bad android APIs, like CoordinatorLayout or InputMethodManager. No. MediaStore is a crime
 * against humanity and probably a way to summon Zalgo if you look at it the wrong way.
 *
 * You think that if you wanted to query a song's genre from a media database, you could just put
 * "genre" in the query and it would return it, right? But not with MediaStore! No, that's too
 * straightforward for this contract that was dropped on it's head as a baby. So instead, you have
 * to query for each genre, query all the songs in each genre, and then iterate through those songs
 * to link every song with their genre. This is not documented anywhere, and the O(mom im scared)
 * algorithm you have to run to get it working single-handedly DOUBLES Auxio's query times. At no
 * point have the devs considered that this system is absolutely insane, and instead focused on
 * adding infuriat- I mean nice proprietary extensions to MediaStore for their own Google Play
 * Music, and of course every Google Play Music user knew how great that turned out!
 *
 * It's not even ergonomics that makes this API bad. It's base implementation is completely borked
 * as well. Did you know that MediaStore doesn't accept dates that aren't from ID3v2.3 MP3 files? I
 * sure didn't, until I decided to upgrade my music collection to ID3v2.4 and FLAC only to see that
 * the metadata parser has a brain aneurysm the moment it stumbles upon a dreaded TRDC or DATE tag.
 * Once again, this is because internally android uses an ancient in-house metadata parser to get
 * everything indexed, and so far they have not bothered to modernize this parser or even switch it
 * to something that actually works, not even in Android 12. ID3v2.4 has been around for *21
 * years.* *It can drink now.*
 *
 * Not to mention all the other infuriating quirks. Album artists can't be accessed from the albums
 * table, so we have to go for the less efficient "make a big query on all the songs lol" method so
 * that songs don't end up fragmented across artists. Pretty much every OEM has added some extension
 * or quirk to MediaStore that I cannot reproduce, with some OEMs (COUGHSAMSUNGCOUGH) crippling the
 * normal tables so that you're railroaded into their music app. I have to use a semi-deprecated
 * field to work with file paths, and the supposedly "modern" method is SLOWER and causes even more
 * problems since some devices just don't expose those fields for some insane reason. Sometimes
 * music will have a deformed clone that I can't filter out, sometimes Genres will just break for
 * no reason, and sometimes tags encoded in UTF-8 will be interpreted as anything from UTF-16 to
 * Latin-1 to *Shift JIS* WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY WHY
 *
 * Is there anything we can do about it? No. Google has routinely shut down issues that begged
 * google to fix glaring issues with MediaStore or to just take the API behind the woodshed and
 * shoot it. Largely because they have zero incentive to improve it given how "obscure" local music
 * listening is. As a result, Auxio exposes an option to use an internal parser based on ExoPlayer
 * that at least tries to correct the insane metadata that this API returns, but not only is that
 * system horrifically slow and bug-prone, it also faces the even larger issue of how google keeps
 * trying to kill the filesystem and force you into their ContentResolver API. In the future
 * MediaStore could be the only system we have, which is also the day that greenland melts and
 * birthdays stop happening forever.
 *
 * I'm pretty sure nothing is going to happen and MediaStore will continue to be neglected and
 * probably deprecated eventually for a "new" API that just coincidentally excludes music indexing.
 * Because go screw yourself for wanting to listen to music you own. Be a good consoomer and listen
 * to your AlgoPop StreamMix™.
 *
 * I wish I was born in the neolithic.
 */

/**
 * The layer that loads music from the MediaStore database. This is an intermediate step in the
 * music loading process.
 * @author OxygenCobalt
 */
abstract class MediaStoreLayer(private val context: Context, private val cacheLayer: CacheLayer) {
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

    private val settings = Settings(context)
    private val genreNamesMap = mutableMapOf<Long, List<String>>()

    private val _volumes = mutableListOf<StorageVolume>()
    protected val volumes: List<StorageVolume>
        get() = _volumes

    /** Initialize this instance by making a query over the media database. */
    open fun init(): Cursor {
        logD("Initializing")
        val start = System.currentTimeMillis()

        cacheLayer.init()

        val storageManager = context.getSystemServiceCompat(StorageManager::class)
        _volumes.addAll(storageManager.storageVolumesCompat)
        val dirs = settings.getMusicDirs(storageManager)

        val args = mutableListOf<String>()
        var selector = BASE_SELECTOR

        if (dirs.dirs.isNotEmpty()) {
            // Need to select for directories. The path query is the same, only difference is
            // the presence of a NOT.
            selector +=
                if (dirs.shouldInclude) {
                    logD("Need to select dirs (Include)")
                    " AND ("
                } else {
                    logD("Need to select dirs (Exclude)")
                    " AND NOT ("
                }

            // Each impl adds the directories that they want selected.
            for (i in dirs.dirs.indices) {
                if (addDirToSelectorArgs(dirs.dirs[i], args)) {
                    selector +=
                        if (i < dirs.dirs.lastIndex) {
                            "$dirSelector OR "
                        } else {
                            dirSelector
                        }
                }
            }

            selector += ')'
        }

        logD("Starting song query [proj: ${projection.toList()}, selector: $selector, args: $args]")

        val cursor =
            requireNotNull(
                context.contentResolverSafe.queryCursor(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selector,
                    args.toTypedArray()
                )
            ) { "Content resolver failure: No Cursor returned" }
                .also { cursor = it }

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

        logD("Song query succeeded [Projected total: ${cursor.count}]")

        logD("Assembling genre map")

        // Since we can't obtain the genre tag from a song query, we must construct
        // our own equivalent from genre database queries. Theoretically, this isn't
        // needed since MetadataLayer will fill this in for us, but there are some
        // obscure formats where genre support is only really covered by this.
        context.contentResolverSafe.useQuery(
            MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Audio.Genres._ID, MediaStore.Audio.Genres.NAME)
        ) { genreCursor ->
            val idIndex = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID)
            val nameIndex = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME)

            while (genreCursor.moveToNext()) {
                // We can't assume what format these genres are derived from, so we just have
                // to assume ID3v2 rules.
                val id = genreCursor.getLong(idIndex)
                val names = (genreCursor.getStringOrNull(nameIndex) ?: continue)
                    .parseId3GenreNames(settings)

                context.contentResolverSafe.useQuery(
                    MediaStore.Audio.Genres.Members.getContentUri(VOLUME_EXTERNAL, id),
                    arrayOf(MediaStore.Audio.Genres.Members._ID)
                ) { cursor ->
                    val songIdIndex =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members._ID)

                    while (cursor.moveToNext()) {
                        // Assume that a song can't inhabit multiple genre entries, as I doubt
                        // Android is smart enough to separate genres into separators.
                        genreNamesMap[cursor.getLong(songIdIndex)] = names
                    }
                }
            }
        }

        logD("Finished initialization in ${System.currentTimeMillis() - start}ms")

        return cursor
    }

    /** Finalize this instance by closing the cursor and finalizing the cache. */
    fun finalize(rawSongs: List<Song.Raw>) {
        cursor?.close()
        cursor = null

        cacheLayer.finalize(rawSongs)
    }

    /**
     * Populate a [raw] with whatever the next value in the cursor is.
     *
     * This returns true if the song could be restored from cache, false if metadata had to be
     * re-extracted, and null if the cursor is exhausted.
     */
    fun populateRaw(raw: Song.Raw): Boolean? {
        val cursor = requireNotNull(cursor) { "MediaStoreLayer is not properly initialized" }
        if (!cursor.moveToNext()) {
            logD("Cursor is exhausted")
            return null
        }

        // Populate the minimum required fields to maybe obtain a cache entry.
        raw.mediaStoreId = cursor.getLong(idIndex)
        raw.dateAdded = cursor.getLong(dateAddedIndex)
        raw.dateModified = cursor.getLong(dateAddedIndex)

        if (cacheLayer.maybePopulateCachedRaw(raw)) {
            // We found a valid cache entry, no need to extract metadata.
            logD("Found cached raw: ${raw.name}")
            return true
        }

        buildRaw(cursor, raw)

        // We had to freshly make this raw, return false
        return false
    }

    /**
     * The projection to use when querying media. Add version-specific columns here in an
     * implementation.
     */
    open val projection: Array<String>
        get() =
            arrayOf(
                // These columns are guaranteed to work on all versions of android
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.MIME_TYPE,
                MediaStore.Audio.AudioColumns.SIZE,
                MediaStore.Audio.AudioColumns.DATE_ADDED,
                MediaStore.Audio.AudioColumns.DATE_MODIFIED,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.YEAR,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.ARTIST,
                AUDIO_COLUMN_ALBUM_ARTIST
            )

    abstract val dirSelector: String
    abstract fun addDirToSelectorArgs(dir: Directory, args: MutableList<String>): Boolean

    /**
     * Build an [Song.Raw] based on the current cursor values. Each implementation should try to
     * obtain an upstream [Song.Raw] first, and then populate it with version-specific fields
     * outlined in [projection].
     */
    protected open fun buildRaw(cursor: Cursor, raw: Song.Raw) {
        raw.name = cursor.getString(titleIndex)

        raw.extensionMimeType = cursor.getString(mimeTypeIndex)
        raw.size = cursor.getLong(sizeIndex)

        // Try to use the DISPLAY_NAME field to obtain a (probably sane) file name
        // from the android system.
        raw.displayName = cursor.getStringOrNull(displayNameIndex)

        raw.durationMs = cursor.getLong(durationIndex)
        raw.date = cursor.getIntOrNull(yearIndex)?.let(Date::from)

        // A non-existent album name should theoretically be the name of the folder it contained
        // in, but in practice it is more often "0" (as in /storage/emulated/0), even when it the
        // file is not actually in the root internal storage directory. We can't do anything to
        // fix this, really.
        raw.albumName = cursor.getString(albumIndex)
        raw.albumMediaStoreId = cursor.getLong(albumIdIndex)

        // Android does not make a non-existent artist tag null, it instead fills it in
        // as <unknown>, which makes absolutely no sense given how other fields default
        // to null if they are not present. If this field is <unknown>, null it so that
        // it's easier to handle later.
        raw.artistNames =
            cursor.getString(artistIndex).run {
                if (this != MediaStore.UNKNOWN_STRING) {
                    // While we can't natively parse multi-value tags from MediaStore itself, we
                    // can still parse by user-defined separators.
                    maybeParseSeparators(settings)
                } else {
                    null
                }
            }

        // The album artist field is nullable and never has placeholder values.
        raw.albumArtistNames =
            cursor.getStringOrNull(albumArtistIndex)?.maybeParseSeparators(settings)

        // Get the genre value we had to query for in initialization
        raw.genreNames = genreNamesMap[raw.mediaStoreId]
    }

    companion object {
        /**
         * The album_artist MediaStore field has existed since at least API 21, but until API 30 it
         * was a proprietary extension for Google Play Music and was not documented. Since this
         * field probably works on all versions Auxio supports, we suppress the warning about using
         * a possibly-unsupported constant.
         */
        @Suppress("InlinedApi")
        private const val AUDIO_COLUMN_ALBUM_ARTIST = MediaStore.Audio.AudioColumns.ALBUM_ARTIST

        /**
         * External has existed since at least API 21, but no constant existed for it until API 29.
         * This constant is safe to use.
         */
        @Suppress("InlinedApi")
        private const val VOLUME_EXTERNAL = MediaStore.VOLUME_EXTERNAL

        /**
         * The base selector that works across all versions of android. Does not exclude
         * directories.
         */
        private const val BASE_SELECTOR =
            "${MediaStore.Audio.Media.IS_MUSIC}=1 " + "AND NOT ${MediaStore.Audio.Media.SIZE}=0"
    }
}

// Note: The separation between version-specific backends may not be the cleanest. To preserve
// speed, we only want to add redundancy on known issues, not with possible issues.

/**
 * A [MediaStoreLayer] that completes the music loading process in a way compatible from
 * API 21 onwards to API 29.
 * @author OxygenCobalt
 */
class Api21MediaStoreLayer(context: Context, cacheLayer: CacheLayer) :
    MediaStoreLayer(context, cacheLayer) {
    private var trackIndex = -1
    private var dataIndex = -1

    override fun init(): Cursor {
        val cursor = super.init()
        trackIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TRACK)
        dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
        return cursor
    }

    override val projection: Array<String>
        get() =
            super.projection +
                arrayOf(MediaStore.Audio.AudioColumns.TRACK, MediaStore.Audio.AudioColumns.DATA)

    override val dirSelector: String
        get() = "${MediaStore.Audio.Media.DATA} LIKE ?"

    override fun addDirToSelectorArgs(dir: Directory, args: MutableList<String>): Boolean {
        // Generate an equivalent DATA value from the volume directory and the relative path.
        args.add("${dir.volume.directoryCompat ?: return false}/${dir.relativePath}%")
        return true
    }

    override fun buildRaw(cursor: Cursor, raw: Song.Raw) {
        super.buildRaw(cursor, raw)

        // DATA is equivalent to the absolute path of the file.
        val data = cursor.getString(dataIndex)

        // On some OEM devices below API 29, DISPLAY_NAME may not be present. I assume
        // that this only applies to below API 29, as beyond API 29, this field not being
        // present would completely break the scoped storage system. Fill it in with DATA
        // if it's not available.
        if (raw.displayName == null) {
            raw.displayName = data.substringAfterLast(File.separatorChar, "").ifEmpty { null }
        }

        // Find the volume that transforms the DATA field into a relative path. This is
        // the volume and relative path we will use.
        val rawPath = data.substringBeforeLast(File.separatorChar)
        for (volume in volumes) {
            val volumePath = volume.directoryCompat ?: continue
            val strippedPath = rawPath.removePrefix(volumePath)
            if (strippedPath != rawPath) {
                raw.directory = Directory.from(volume, strippedPath)
                break
            }
        }

        val rawTrack = cursor.getIntOrNull(trackIndex)
        if (rawTrack != null) {
            rawTrack.unpackTrackNo()?.let { raw.track = it }
            rawTrack.unpackDiscNo()?.let { raw.disc = it }
        }
    }
}

/**
 * A [MediaStoreLayer] that selects directories and builds paths using the modern volume fields
 * available from API 29 onwards.
 * @author OxygenCobalt
 */
@RequiresApi(Build.VERSION_CODES.Q)
open class BaseApi29MediaStoreLayer(context: Context, cacheLayer: CacheLayer) :
    MediaStoreLayer(context, cacheLayer) {
    private var volumeIndex = -1
    private var relativePathIndex = -1

    override fun init(): Cursor {
        val cursor = super.init()

        volumeIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.VOLUME_NAME)
        relativePathIndex =
            cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.RELATIVE_PATH)

        return cursor
    }

    override val projection: Array<String>
        get() =
            super.projection +
                arrayOf(
                    MediaStore.Audio.AudioColumns.VOLUME_NAME,
                    MediaStore.Audio.AudioColumns.RELATIVE_PATH
                )

    override val dirSelector: String
        get() =
            "(${MediaStore.Audio.AudioColumns.VOLUME_NAME} LIKE ? " +
                "AND ${MediaStore.Audio.AudioColumns.RELATIVE_PATH} LIKE ?)"

    override fun addDirToSelectorArgs(dir: Directory, args: MutableList<String>): Boolean {
        // Leverage new the volume field when selecting our directories.
        args.add(dir.volume.mediaStoreVolumeNameCompat ?: return false)
        args.add("${dir.relativePath}%")
        return true
    }

    override fun buildRaw(cursor: Cursor, raw: Song.Raw) {
        super.buildRaw(cursor, raw)

        val volumeName = cursor.getString(volumeIndex)
        val relativePath = cursor.getString(relativePathIndex)

        // Find the StorageVolume whose MediaStore name corresponds to this song.
        // This is what we use for the Directory's volume.
        val volume = volumes.find { it.mediaStoreVolumeNameCompat == volumeName }
        if (volume != null) {
            raw.directory = Directory.from(volume, relativePath)
        }
    }
}

/**
 * A [MediaStoreLayer] that completes the music loading process in a way compatible with at least
 * API 29.
 * @author OxygenCobalt
 */
@RequiresApi(Build.VERSION_CODES.Q)
open class Api29MediaStoreLayer(context: Context, cacheLayer: CacheLayer) :
    BaseApi29MediaStoreLayer(context, cacheLayer) {
    private var trackIndex = -1

    override fun init(): Cursor {
        val cursor = super.init()
        trackIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TRACK)
        return cursor
    }

    override val projection: Array<String>
        get() = super.projection + arrayOf(MediaStore.Audio.AudioColumns.TRACK)

    override fun buildRaw(cursor: Cursor, raw: Song.Raw) {
        super.buildRaw(cursor, raw)

        // This backend is volume-aware, but does not support the modern track fields.
        // Use the old field instead.
        val rawTrack = cursor.getIntOrNull(trackIndex)
        if (rawTrack != null) {
            rawTrack.unpackTrackNo()?.let { raw.track = it }
            rawTrack.unpackDiscNo()?.let { raw.disc = it }
        }
    }
}

/**
 * A [MediaStoreLayer] that completes the music loading process in a way compatible with at least
 * API 30.
 * @author OxygenCobalt
 */
@RequiresApi(Build.VERSION_CODES.R)
class Api30MediaStoreLayer(context: Context, cacheLayer: CacheLayer) :
    BaseApi29MediaStoreLayer(context, cacheLayer) {
    private var trackIndex: Int = -1
    private var discIndex: Int = -1

    override fun init(): Cursor {
        val cursor = super.init()
        trackIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER)
        discIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISC_NUMBER)
        return cursor
    }

    override val projection: Array<String>
        get() =
            super.projection +
                arrayOf(
                    MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER,
                    MediaStore.Audio.AudioColumns.DISC_NUMBER
                )

    override fun buildRaw(cursor: Cursor, raw: Song.Raw) {
        super.buildRaw(cursor, raw)

        // Both CD_TRACK_NUMBER and DISC_NUMBER tend to be formatted as they are in
        // the tag itself, which is to say that it is formatted as NN/TT tracks, where
        // N is the number and T is the total. Parse the number while leaving out the
        // total, as we have no use for it.
        cursor.getStringOrNull(trackIndex)?.parsePositionNum()?.let { raw.track = it }
        cursor.getStringOrNull(discIndex)?.parsePositionNum()?.let { raw.disc = it }
    }
}
