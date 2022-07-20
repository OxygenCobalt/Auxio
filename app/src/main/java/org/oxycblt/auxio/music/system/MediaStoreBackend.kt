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
 
package org.oxycblt.auxio.music.system

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
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.contentResolverSafe
import org.oxycblt.auxio.util.getSystemServiceSafe
import org.oxycblt.auxio.util.logD

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
 * algorithm you have to run to get it working single-handedly DOUBLES Auxio's loading times. At no
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
 * Represents a [Indexer.Backend] that loads music from the media database ([MediaStore]). This is
 * not a fully-featured class by itself, and it's API-specific derivatives should be used instead.
 * @author OxygenCobalt
 */
abstract class MediaStoreBackend : Indexer.Backend {
    private var idIndex = -1
    private var titleIndex = -1
    private var displayNameIndex = -1
    private var mimeTypeIndex = -1
    private var sizeIndex = -1
    private var dateAddedIndex = -1
    private var durationIndex = -1
    private var yearIndex = -1
    private var albumIndex = -1
    private var albumIdIndex = -1
    private var artistIndex = -1
    private var albumArtistIndex = -1

    protected val volumes = mutableListOf<StorageVolume>()

    override fun query(context: Context): Cursor {
        val settings = Settings(context)
        val storageManager = context.getSystemServiceSafe(StorageManager::class)
        volumes.addAll(storageManager.storageVolumesCompat)
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

        logD("Starting query [proj: ${projection.toList()}, selector: $selector, args: $args]")

        return requireNotNull(
            context.contentResolverSafe.queryCursor(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selector,
                args.toTypedArray())) { "Content resolver failure: No Cursor returned" }
    }

    override fun buildSongs(
        context: Context,
        cursor: Cursor,
        emitIndexing: (Indexer.Indexing) -> Unit
    ): List<Song> {
        val audios = mutableListOf<Audio>()
        while (cursor.moveToNext()) {
            audios.add(buildAudio(context, cursor))
            if (cursor.position % 50 == 0) {
                // Only check for a cancellation every 50 songs or so (~20ms).
                // While this seems redundant, each call to emitIndexing checks for a
                // cancellation of the co-routine this loading task is running on.
                emitIndexing(Indexer.Indexing.Indeterminate)
            }
        }

        // The audio is not actually complete at this point, as we cannot obtain a genre
        // through a song query. Instead, we have to do the hack where we iterate through
        // every genre and assign it's name to audios that match it's child ID.
        context.contentResolverSafe.useQuery(
            MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Audio.Genres._ID, MediaStore.Audio.Genres.NAME)) { genreCursor ->
            val idIndex = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID)
            val nameIndex = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME)

            while (genreCursor.moveToNext()) {
                // Genre names could theoretically be anything, including null for some reason.
                // Null values are junk and should be ignored, but since we cannot assume the
                // format a genre was derived from, we have to treat them like they are ID3
                // genres, even when they might not be.
                val id = genreCursor.getLong(idIndex)
                val name = (genreCursor.getStringOrNull(nameIndex) ?: continue).parseId3GenreName()

                context.contentResolverSafe.useQuery(
                    MediaStore.Audio.Genres.Members.getContentUri(VOLUME_EXTERNAL, id),
                    arrayOf(MediaStore.Audio.Genres.Members._ID)) { cursor ->
                    val songIdIndex =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members._ID)

                    while (cursor.moveToNext()) {
                        val songId = cursor.getLong(songIdIndex)
                        audios.find { it.id == songId }?.let { song -> song.genre = name }

                        if (cursor.position % 50 == 0) {
                            // Only check for a cancellation every 50 songs or so (~20ms).
                            emitIndexing(Indexer.Indexing.Indeterminate)
                        }
                    }
                }
            }

            // Check for a cancellation every time we finish a genre too, in the case that
            // the genre has <50 songs.
            emitIndexing(Indexer.Indexing.Indeterminate)
        }

        return audios.map { it.toSong() }
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
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.YEAR,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.ARTIST,
                AUDIO_COLUMN_ALBUM_ARTIST)

    abstract val dirSelector: String
    abstract fun addDirToSelectorArgs(dir: Directory, args: MutableList<String>): Boolean

    /**
     * Build an [Audio] based on the current cursor values. Each implementation should try to obtain
     * an upstream [Audio] first, and then populate it with version-specific fields outlined in
     * [projection].
     */
    open fun buildAudio(context: Context, cursor: Cursor): Audio {
        // Initialize our cursor indices if we haven't already.
        if (idIndex == -1) {
            idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
            titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
            displayNameIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
            mimeTypeIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.MIME_TYPE)
            sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.SIZE)
            dateAddedIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_ADDED)
            durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
            yearIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.YEAR)
            albumIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
            albumIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)
            artistIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            albumArtistIndex = cursor.getColumnIndexOrThrow(AUDIO_COLUMN_ALBUM_ARTIST)
        }

        val audio = Audio()

        audio.id = cursor.getLong(idIndex)
        audio.title = cursor.getString(titleIndex)

        audio.extensionMimeType = cursor.getString(mimeTypeIndex)
        audio.size = cursor.getLong(sizeIndex)
        audio.dateAdded = cursor.getLong(dateAddedIndex)

        // Try to use the DISPLAY_NAME field to obtain a (probably sane) file name
        // from the android system.
        audio.displayName = cursor.getStringOrNull(displayNameIndex)

        audio.duration = cursor.getLong(durationIndex)
        audio.date = cursor.getIntOrNull(yearIndex)?.let(Date::from)

        // A non-existent album name should theoretically be the name of the folder it contained
        // in, but in practice it is more often "0" (as in /storage/emulated/0), even when it the
        // file is not actually in the root internal storage directory. We can't do anything to
        // fix this, really.
        audio.album = cursor.getString(albumIndex)
        audio.albumId = cursor.getLong(albumIdIndex)

        // Android does not make a non-existent artist tag null, it instead fills it in
        // as <unknown>, which makes absolutely no sense given how other fields default
        // to null if they are not present. If this field is <unknown>, null it so that
        // it's easier to handle later.
        audio.artist =
            cursor.getString(artistIndex).run {
                if (this != MediaStore.UNKNOWN_STRING) this else null
            }

        // The album artist field is nullable and never has placeholder values.
        audio.albumArtist = cursor.getStringOrNull(albumArtistIndex)

        return audio
    }

    /**
     * Represents a song as it is represented by MediaStore. This is progressively mutated in the
     * chain of Backend instances until it is complete enough to be transformed into an immutable
     * song.
     */
    data class Audio(
        var id: Long? = null,
        var title: String? = null,
        var sortTitle: String? = null,
        var displayName: String? = null,
        var dir: Directory? = null,
        var extensionMimeType: String? = null,
        var formatMimeType: String? = null,
        var size: Long? = null,
        var dateAdded: Long? = null,
        var duration: Long? = null,
        var track: Int? = null,
        var disc: Int? = null,
        var date: Date? = null,
        var albumId: Long? = null,
        var album: String? = null,
        var sortAlbum: String? = null,
        var releaseType: ReleaseType? = null,
        var artist: String? = null,
        var sortArtist: String? = null,
        var albumArtist: String? = null,
        var sortAlbumArtist: String? = null,
        var genre: String? = null
    ) {
        fun toSong() =
            Song(
                // Assert that the fields that should always exist are present. I can't confirm
                // that every device provides these fields, but it seems likely that they do.
                rawName = requireNotNull(title) { "Malformed audio: No title" },
                rawSortName = sortTitle,
                path =
                    Path(
                        name = requireNotNull(displayName) { "Malformed audio: No display name" },
                        parent = requireNotNull(dir) { "Malformed audio: No parent directory" }),
                uri = requireNotNull(id) { "Malformed audio: No id" }.audioUri,
                mimeType =
                    MimeType(
                        fromExtension =
                            requireNotNull(extensionMimeType) { "Malformed audio: No mime type" },
                        fromFormat = formatMimeType),
                size = requireNotNull(size) { "Malformed audio: No size" },
                dateAdded = requireNotNull(dateAdded) { "Malformed audio: No date added" },
                durationMs = requireNotNull(duration) { "Malformed audio: No duration" },
                track = track,
                disc = disc,
                _date = date,
                _albumName = requireNotNull(album) { "Malformed audio: No album name" },
                _albumSortName = sortAlbum,
                _albumReleaseType = releaseType,
                _albumCoverUri =
                    requireNotNull(albumId) { "Malformed audio: No album id" }.albumCoverUri,
                _artistName = artist,
                _artistSortName = sortArtist,
                _albumArtistName = albumArtist,
                _albumArtistSortName = sortAlbumArtist,
                _genreName = genre)
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
        @Suppress("InlinedApi") private const val VOLUME_EXTERNAL = MediaStore.VOLUME_EXTERNAL

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
 * A [MediaStoreBackend] that completes the music loading process in a way compatible from
 * @author OxygenCobalt
 */
class Api21MediaStoreBackend : MediaStoreBackend() {
    private var trackIndex = -1
    private var dataIndex = -1

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

    override fun buildAudio(context: Context, cursor: Cursor): Audio {
        val audio = super.buildAudio(context, cursor)

        // Initialize our indices if we have not already.
        if (trackIndex == -1) {
            trackIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TRACK)
            dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
        }

        val data = cursor.getString(dataIndex)

        // On some OEM devices below API 29, DISPLAY_NAME may not be present. I assume
        // that this only applies to below API 29, as beyond API 29, this field not being
        // present would completely break the scoped storage system. Fill it in with DATA
        // if it's not available.
        if (audio.displayName == null) {
            audio.displayName = data.substringAfterLast(File.separatorChar, "").ifEmpty { null }
        }

        // Find the volume that transforms the DATA field into a relative path. This is
        // the volume and relative path we will use.
        val rawPath = data.substringBeforeLast(File.separatorChar)
        for (volume in volumes) {
            val volumePath = volume.directoryCompat ?: continue
            val strippedPath = rawPath.removePrefix(volumePath)
            if (strippedPath != rawPath) {
                audio.dir = Directory(volume, strippedPath.removePrefix(File.separator))
                break
            }
        }

        val rawTrack = cursor.getIntOrNull(trackIndex)
        if (rawTrack != null) {
            rawTrack.unpackTrackNo()?.let { audio.track = it }
            rawTrack.unpackDiscNo()?.let { audio.disc = it }
        }

        return audio
    }
}

/**
 * A [MediaStoreBackend] that selects directories and builds paths using the modern volume fields
 * available from API 29 onwards.
 * @author OxygenCobalt
 */
@RequiresApi(Build.VERSION_CODES.Q)
open class BaseApi29MediaStoreBackend : MediaStoreBackend() {
    private var volumeIndex = -1
    private var relativePathIndex = -1

    override val projection: Array<String>
        get() =
            super.projection +
                arrayOf(
                    MediaStore.Audio.AudioColumns.VOLUME_NAME,
                    MediaStore.Audio.AudioColumns.RELATIVE_PATH)

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

    override fun buildAudio(context: Context, cursor: Cursor): Audio {
        val audio = super.buildAudio(context, cursor)

        if (volumeIndex == -1) {
            volumeIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.VOLUME_NAME)
            relativePathIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.RELATIVE_PATH)
        }

        val volumeName = cursor.getString(volumeIndex)
        val relativePath = cursor.getString(relativePathIndex)

        // Find the StorageVolume whose MediaStore name corresponds to this song.
        // This is what we use for the Directory's volume.
        val volume = volumes.find { it.mediaStoreVolumeNameCompat == volumeName }
        if (volume != null) {
            audio.dir = Directory(volume, relativePath.removeSuffix(File.separator))
        }

        return audio
    }
}

/**
 * A [MediaStoreBackend] that completes the music loading process in a way compatible with at least
 * API 29.
 * @author OxygenCobalt
 */
@RequiresApi(Build.VERSION_CODES.Q)
open class Api29MediaStoreBackend : BaseApi29MediaStoreBackend() {
    private var trackIndex = -1

    override val projection: Array<String>
        get() = super.projection + arrayOf(MediaStore.Audio.AudioColumns.TRACK)

    override fun buildAudio(context: Context, cursor: Cursor): Audio {
        val audio = super.buildAudio(context, cursor)

        if (trackIndex == -1) {
            trackIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TRACK)
        }

        // This backend is volume-aware, but does not support the modern track fields.
        // Use the old field instead.
        val rawTrack = cursor.getIntOrNull(trackIndex)
        if (rawTrack != null) {
            rawTrack.unpackTrackNo()?.let { audio.track = it }
            rawTrack.unpackDiscNo()?.let { audio.disc = it }
        }

        return audio
    }
}

/**
 * A [MediaStoreBackend] that completes the music loading process in a way compatible with at least
 * API 30.
 * @author OxygenCobalt
 */
@RequiresApi(Build.VERSION_CODES.R)
class Api30MediaStoreBackend : BaseApi29MediaStoreBackend() {
    private var trackIndex: Int = -1
    private var discIndex: Int = -1

    override val projection: Array<String>
        get() =
            super.projection +
                arrayOf(
                    MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER,
                    MediaStore.Audio.AudioColumns.DISC_NUMBER)

    override fun buildAudio(context: Context, cursor: Cursor): Audio {
        val audio = super.buildAudio(context, cursor)

        // Populate our indices if we have not already.
        if (trackIndex == -1) {
            trackIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER)
            discIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISC_NUMBER)
        }

        // Both CD_TRACK_NUMBER and DISC_NUMBER tend to be formatted as they are in
        // the tag itself, which is to say that it is formatted as NN/TT tracks, where
        // N is the number and T is the total. Parse the number while leaving out the
        // total, as we have no use for it.
        cursor.getStringOrNull(trackIndex)?.parsePositionNum()?.let { audio.track = it }
        cursor.getStringOrNull(discIndex)?.parsePositionNum()?.let { audio.disc = it }

        return audio
    }
}
