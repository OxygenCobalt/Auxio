/*
 * Copyright (c) 2021 Auxio Project
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
 
@file:Suppress("PropertyName", "FunctionName")

package org.oxycblt.auxio.music

import android.content.Context
import android.os.Parcelable
import androidx.annotation.VisibleForTesting
import java.security.MessageDigest
import java.text.CollationKey
import java.text.Collator
import java.util.UUID
import kotlin.math.max
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.oxycblt.auxio.R
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.music.library.Sort
import org.oxycblt.auxio.music.parsing.parseId3GenreNames
import org.oxycblt.auxio.music.parsing.parseMultiValue
import org.oxycblt.auxio.music.storage.*
import org.oxycblt.auxio.music.tags.Date
import org.oxycblt.auxio.music.tags.ReleaseType
import org.oxycblt.auxio.util.nonZeroOrNull
import org.oxycblt.auxio.util.unlikelyToBeNull

// --- MUSIC MODELS ---

/**
 * Abstract music data. This contains universal information about all concrete music
 * implementations, such as identification information and names.
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed class Music : Item {
    /**
     * A unique identifier for this music item.
     * @see UID
     */
    abstract val uid: UID

    /**
     * The raw name of this item as it was extracted from the file-system. Will be null if the
     * item's name is unknown. When showing this item in a UI, avoid this in favor of [resolveName].
     */
    abstract val rawName: String?

    /**
     * Returns a name suitable for use in the app UI. This should be favored over [rawName] in
     * nearly all cases.
     * @param context [Context] required to obtain placeholder text or formatting information.
     * @return A human-readable string representing the name of this music. In the case that the
     * item does not have a name, an analogous "Unknown X" name is returned.
     */
    abstract fun resolveName(context: Context): String

    /**
     * The raw sort name of this item as it was extracted from the file-system. This can be used not
     * only when sorting music, but also trying to locate music based on a fuzzy search by the user.
     * Will be null if the item has no known sort name.
     */
    abstract val rawSortName: String?

    /**
     * A [CollationKey] derived from [rawName] and [rawSortName] that can be used to sort items in a
     * semantically-correct manner. Will be null if the item has no name.
     *
     * The key will have the following attributes:
     * - If [rawSortName] is present, this key will be derived from it. Otherwise [rawName] is used.
     * - If the string begins with an article, such as "the", it will be stripped, as is usually
     * convention for sorting media. This is not internationalized.
     */
    abstract val collationKey: CollationKey?

    /**
     * Finalize this item once the music library has been fully constructed. This is where any final
     * ordering or sanity checking should occur. **This function is internal to the music package.
     * Do not use it elsewhere.**
     */
    abstract fun _finalize()

    /**
     * Provided implementation to create a [CollationKey] in the way described by [collationKey].
     * This should be used in all overrides of all [CollationKey].
     * @return A [CollationKey] that follows the specification described by [collationKey].
     */
    protected fun makeCollationKeyImpl(): CollationKey? {
        val sortName =
            (rawSortName ?: rawName)?.run {
                when {
                    length > 5 && startsWith("the ", ignoreCase = true) -> substring(4)
                    length > 4 && startsWith("an ", ignoreCase = true) -> substring(3)
                    length > 3 && startsWith("a ", ignoreCase = true) -> substring(2)
                    else -> this
                }
            }

        return COLLATOR.getCollationKey(sortName)
    }

    /**
     * Join a list of [Music]'s resolved names into a string in a localized manner, using
     * [R.string.fmt_list].
     * @param context [Context] required to obtain localized formatting.
     * @param values The list of [Music] to format.
     * @return A single string consisting of the values delimited by a localized separator.
     */
    protected fun resolveNames(context: Context, values: List<Music>): String {
        if (values.isEmpty()) {
            // Nothing to do.
            return ""
        }

        var joined = values.first().resolveName(context)
        for (i in 1..values.lastIndex) {
            // Chain all previous values with the next value in the list with another delimiter.
            joined = context.getString(R.string.fmt_list, joined, values[i].resolveName(context))
        }
        return joined
    }

    // Note: We solely use the UID in comparisons so that certain items that differ in all
    // but UID are treated differently.

    override fun hashCode() = uid.hashCode()

    override fun equals(other: Any?) =
        other is Music && javaClass == other.javaClass && uid == other.uid

    /**
     * A unique identifier for a piece of music.
     *
     * [UID] enables a much cheaper and more reliable form of differentiating music, derived from
     * either a hash of meaningful metadata or the MusicBrainz ID spec. Using this enables several
     * improvements to music management in this app, including:
     *
     * - Proper differentiation of identical music. It's common for large, well-tagged libraries to
     * have functionally duplicate items that are differentiated with MusicBrainz IDs, and so [UID]
     * allows us to properly differentiate between these in the app.
     * - Better music persistence between restarts. Whereas directly storing song names would be
     * prone to collisions, and storing MediaStore IDs would drift rapidly as the music library
     * changes, [UID] enables a much stronger form of persistence given it's unique link to a
     * specific files metadata configuration, which is unlikely to collide with another item or
     * drift as the music library changes.
     *
     * Note: Generally try to use [UID] as a black box that can only be read, written, and compared.
     * It will not be fun if you try to manipulate it in any other manner.
     *
     * @author Alexander Capehart (OxygenCobalt)
     */
    @Parcelize
    class UID
    private constructor(
        private val format: Format,
        private val mode: MusicMode,
        private val uuid: UUID
    ) : Parcelable {
        // Cache the hashCode for HashMap efficiency.
        @IgnoredOnParcel private var hashCode = format.hashCode()

        init {
            hashCode = 31 * hashCode + mode.hashCode()
            hashCode = 31 * hashCode + uuid.hashCode()
        }

        override fun hashCode() = hashCode

        override fun equals(other: Any?) =
            other is UID && format == other.format && mode == other.mode && uuid == other.uuid

        override fun toString() = "${format.namespace}:${mode.intCode.toString(16)}-$uuid"

        /**
         * Internal marker of [Music.UID] format type.
         * @param namespace Namespace to use in the [Music.UID]'s string representation.
         */
        private enum class Format(val namespace: String) {
            /** @see auxio */
            AUXIO("org.oxycblt.auxio"),
            /** @see musicBrainz */
            MUSICBRAINZ("org.musicbrainz")
        }

        companion object {
            /**
             * Creates an Auxio-style [UID] with a [UUID] composed of a hash of the non-subjective,
             * unlikely-to-change metadata of the music.
             * @param mode The analogous [MusicMode] of the item that created this [UID].
             * @param updates Block to update the [MessageDigest] hash with the metadata of the
             * item. Make sure the metadata hashed semantically aligns with the format
             * specification.
             * @return A new auxio-style [UID].
             */
            fun auxio(mode: MusicMode, updates: MessageDigest.() -> Unit): UID {
                val digest =
                    MessageDigest.getInstance("SHA-256").run {
                        updates()
                        digest()
                    }
                // Convert the digest to a UUID. This does cleave off some of the hash, but this
                // is considered okay.
                val uuid =
                    UUID(
                        digest[0]
                            .toLong()
                            .shl(56)
                            .or(digest[1].toLong().and(0xFF).shl(48))
                            .or(digest[2].toLong().and(0xFF).shl(40))
                            .or(digest[3].toLong().and(0xFF).shl(32))
                            .or(digest[4].toLong().and(0xFF).shl(24))
                            .or(digest[5].toLong().and(0xFF).shl(16))
                            .or(digest[6].toLong().and(0xFF).shl(8))
                            .or(digest[7].toLong().and(0xFF)),
                        digest[8]
                            .toLong()
                            .shl(56)
                            .or(digest[9].toLong().and(0xFF).shl(48))
                            .or(digest[10].toLong().and(0xFF).shl(40))
                            .or(digest[11].toLong().and(0xFF).shl(32))
                            .or(digest[12].toLong().and(0xFF).shl(24))
                            .or(digest[13].toLong().and(0xFF).shl(16))
                            .or(digest[14].toLong().and(0xFF).shl(8))
                            .or(digest[15].toLong().and(0xFF)))
                return UID(Format.AUXIO, mode, uuid)
            }

            /**
             * Creates a MusicBrainz-style [UID] with a [UUID] derived from the MusicBrainz ID
             * extracted from a file.
             * @param mode The analogous [MusicMode] of the item that created this [UID].
             * @param mbid The analogous MusicBrainz ID for this item that was extracted from a
             * file.
             * @return A new MusicBrainz-style [UID].
             */
            fun musicBrainz(mode: MusicMode, mbid: UUID): UID = UID(Format.MUSICBRAINZ, mode, mbid)

            /**
             * Convert a [UID]'s string representation back into a concrete [UID] instance.
             * @param uid The [UID]'s string representation, formatted as
             * `format_namespace:music_mode_int-uuid`.
             * @return A [UID] converted from the string representation, or null if the string
             * representation was invalid.
             */
            fun fromString(uid: String): UID? {
                val split = uid.split(':', limit = 2)
                if (split.size != 2) {
                    return null
                }

                val format =
                    when (split[0]) {
                        Format.AUXIO.namespace -> Format.AUXIO
                        Format.MUSICBRAINZ.namespace -> Format.MUSICBRAINZ
                        else -> return null
                    }

                val ids = split[1].split('-', limit = 2)
                if (ids.size != 2) {
                    return null
                }

                val mode =
                    MusicMode.fromIntCode(ids[0].toIntOrNull(16) ?: return null) ?: return null
                val uuid = ids[1].toUuidOrNull() ?: return null
                return UID(format, mode, uuid)
            }
        }
    }

    private companion object {
        /** Cached collator instance re-used with [makeCollationKeyImpl]. */
        val COLLATOR: Collator = Collator.getInstance().apply { strength = Collator.PRIMARY }
    }
}

/**
 * An abstract grouping of [Song]s and other [Music] data.
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed class MusicParent : Music() {
    /** The [Song]s in this this group. */
    abstract val songs: List<Song>

    // Note: Append song contents to MusicParent equality so that Groups with
    // the same UID but different contents are not equal.

    override fun hashCode() = 31 * uid.hashCode() + songs.hashCode()

    override fun equals(other: Any?) =
        other is MusicParent &&
            javaClass == other.javaClass &&
            uid == other.uid &&
            songs == other.songs
}

/**
 * A song. Perhaps the foundation of the entirety of Auxio.
 * @param raw The [Song.Raw] to derive the member data from.
 * @param musicSettings [MusicSettings] to perform further user-configured parsing.
 * @author Alexander Capehart (OxygenCobalt)
 */
class Song constructor(raw: Raw, musicSettings: MusicSettings) : Music() {
    override val uid =
    // Attempt to use a MusicBrainz ID first before falling back to a hashed UID.
    raw.musicBrainzId?.toUuidOrNull()?.let { UID.musicBrainz(MusicMode.SONGS, it) }
            ?: UID.auxio(MusicMode.SONGS) {
                // Song UIDs are based on the raw data without parsing so that they remain
                // consistent across music setting changes. Parents are not held up to the
                // same standard since grouping is already inherently linked to settings.
                update(raw.name)
                update(raw.albumName)
                update(raw.date)

                update(raw.track)
                update(raw.disc)

                update(raw.artistNames)
                update(raw.albumArtistNames)
            }
    override val rawName = requireNotNull(raw.name) { "Invalid raw: No title" }
    override val rawSortName = raw.sortName
    override val collationKey = makeCollationKeyImpl()
    override fun resolveName(context: Context) = rawName

    /** The track number. Will be null if no valid track number was present in the metadata. */
    val track = raw.track

    /** The disc number. Will be null if no valid disc number was present in the metadata. */
    val disc = raw.disc

    /** The release [Date]. Will be null if no valid date was present in the metadata. */
    val date = raw.date

    /**
     * The URI to the audio file that this instance was created from. This can be used to access the
     * audio file in a way that is scoped-storage-safe.
     */
    val uri = requireNotNull(raw.mediaStoreId) { "Invalid raw: No id" }.toAudioUri()

    /**
     * The [Path] to this audio file. This is only intended for display, [uri] should be favored
     * instead for accessing the audio file.
     */
    val path =
        Path(
            name = requireNotNull(raw.fileName) { "Invalid raw: No display name" },
            parent = requireNotNull(raw.directory) { "Invalid raw: No parent directory" })

    /** The [MimeType] of the audio file. Only intended for display. */
    val mimeType =
        MimeType(
            fromExtension = requireNotNull(raw.extensionMimeType) { "Invalid raw: No mime type" },
            fromFormat = null)

    /** The size of the audio file, in bytes. */
    val size = requireNotNull(raw.size) { "Invalid raw: No size" }

    /** The duration of the audio file, in milliseconds. */
    val durationMs = requireNotNull(raw.durationMs) { "Invalid raw: No duration" }

    /** The date the audio file was added to the device, as a unix epoch timestamp. */
    val dateAdded = requireNotNull(raw.dateAdded) { "Invalid raw: No date added" }

    private var _album: Album? = null
    /**
     * The parent [Album]. If the metadata did not specify an album, it's parent directory is used
     * instead.
     */
    val album: Album
        get() = unlikelyToBeNull(_album)

    private val artistMusicBrainzIds = raw.artistMusicBrainzIds.parseMultiValue(musicSettings)
    private val artistNames = raw.artistNames.parseMultiValue(musicSettings)
    private val artistSortNames = raw.artistSortNames.parseMultiValue(musicSettings)
    private val rawArtists =
        artistNames.mapIndexed { i, name ->
            Artist.Raw(
                artistMusicBrainzIds.getOrNull(i)?.toUuidOrNull(),
                name,
                artistSortNames.getOrNull(i))
        }

    private val albumArtistMusicBrainzIds =
        raw.albumArtistMusicBrainzIds.parseMultiValue(musicSettings)
    private val albumArtistNames = raw.albumArtistNames.parseMultiValue(musicSettings)
    private val albumArtistSortNames = raw.albumArtistSortNames.parseMultiValue(musicSettings)
    private val rawAlbumArtists =
        albumArtistNames.mapIndexed { i, name ->
            Artist.Raw(
                albumArtistMusicBrainzIds.getOrNull(i)?.toUuidOrNull(),
                name,
                albumArtistSortNames.getOrNull(i))
        }

    private val _artists = mutableListOf<Artist>()
    /**
     * The parent [Artist]s of this [Song]. Is often one, but there can be multiple if more than one
     * [Artist] name was specified in the metadata. Unliked [Album], artists are prioritized for
     * this field.
     */
    val artists: List<Artist>
        get() = _artists

    /**
     * Resolves one or more [Artist]s into a single piece of human-readable names.
     * @param context [Context] required for [resolveName]. formatter.
     */
    fun resolveArtistContents(context: Context) = resolveNames(context, artists)

    /**
     * Checks if the [Artist] *display* of this [Song] and another [Song] are equal. This will only
     * compare surface-level names, and not [Music.UID]s.
     * @param other The [Song] to compare to.
     * @return True if the [Artist] displays are equal, false otherwise
     */
    fun areArtistContentsTheSame(other: Song): Boolean {
        for (i in 0 until max(artists.size, other.artists.size)) {
            val a = artists.getOrNull(i) ?: return false
            val b = other.artists.getOrNull(i) ?: return false
            if (a.rawName != b.rawName) {
                return false
            }
        }

        return true
    }

    private val _genres = mutableListOf<Genre>()
    /**
     * The parent [Genre]s of this [Song]. Is often one, but there can be multiple if more than one
     * [Genre] name was specified in the metadata.
     */
    val genres: List<Genre>
        get() = _genres

    /**
     * Resolves one or more [Genre]s into a single piece human-readable names.
     * @param context [Context] required for [resolveName].
     */
    fun resolveGenreContents(context: Context) = resolveNames(context, genres)

    // --- INTERNAL FIELDS ---

    /**
     * The [Album.Raw] instances collated by the [Song]. This can be used to group [Song]s into an
     * [Album]. **This is only meant for use within the music package.**
     */
    val _rawAlbum =
        Album.Raw(
            mediaStoreId = requireNotNull(raw.albumMediaStoreId) { "Invalid raw: No album id" },
            musicBrainzId = raw.albumMusicBrainzId?.toUuidOrNull(),
            name = requireNotNull(raw.albumName) { "Invalid raw: No album name" },
            sortName = raw.albumSortName,
            releaseType = ReleaseType.parse(raw.releaseTypes.parseMultiValue(musicSettings)),
            rawArtists =
                rawAlbumArtists.ifEmpty { rawArtists }.ifEmpty { listOf(Artist.Raw(null, null)) })

    /**
     * The [Artist.Raw] instances collated by the [Song]. The artists of the song take priority,
     * followed by the album artists. If there are no artists, this field will be a single "unknown"
     * [Artist.Raw]. This can be used to group up [Song]s into an [Artist]. **This is only meant for
     * use within the music package.**
     */
    val _rawArtists = rawArtists.ifEmpty { rawAlbumArtists }.ifEmpty { listOf(Artist.Raw()) }

    /**
     * The [Genre.Raw] instances collated by the [Song]. This can be used to group up [Song]s into a
     * [Genre]. ID3v2 Genre names are automatically converted to their resolved names. **This is
     * only meant for use within the music package.**
     */
    val _rawGenres =
        raw.genreNames
            .parseId3GenreNames(musicSettings)
            .map { Genre.Raw(it) }
            .ifEmpty { listOf(Genre.Raw()) }

    /**
     * Links this [Song] with a parent [Album].
     * @param album The parent [Album] to link to. **This is only meant for use within the music
     * package.**
     */
    fun _link(album: Album) {
        _album = album
    }

    /**
     * Links this [Song] with a parent [Artist].
     * @param artist The parent [Artist] to link to. **This is only meant for use within the music
     * package.**
     */
    fun _link(artist: Artist) {
        _artists.add(artist)
    }

    /**
     * Links this [Song] with a parent [Genre].
     * @param genre The parent [Genre] to link to. **This is only meant for use within the music
     * package.**
     */
    fun _link(genre: Genre) {
        _genres.add(genre)
    }

    override fun _finalize() {
        checkNotNull(_album) { "Malformed song: No album" }

        check(_artists.isNotEmpty()) { "Malformed song: No artists" }
        for (i in _artists.indices) {
            // Non-destructively reorder the linked artists so that they align with
            // the artist ordering within the song metadata.
            val newIdx = _artists[i]._getOriginalPositionIn(_rawArtists)
            val other = _artists[newIdx]
            _artists[newIdx] = _artists[i]
            _artists[i] = other
        }

        check(_genres.isNotEmpty()) { "Malformed song: No genres" }
        for (i in _genres.indices) {
            // Non-destructively reorder the linked genres so that they align with
            // the genre ordering within the song metadata.
            val newIdx = _genres[i]._getOriginalPositionIn(_rawGenres)
            val other = _genres[newIdx]
            _genres[newIdx] = _genres[i]
            _genres[i] = other
        }
    }

    /**
     * Raw information about a [Song] obtained from the filesystem/Extractor instances. **This is
     * only meant for use within the music package.**
     */
    class Raw
    constructor(
        /**
         * The ID of the [Song]'s audio file, obtained from MediaStore. Note that this ID is highly
         * unstable and should only be used for accessing the audio file.
         */
        var mediaStoreId: Long? = null,
        /** @see Song.dateAdded */
        var dateAdded: Long? = null,
        /** The latest date the [Song]'s audio file was modified, as a unix epoch timestamp. */
        var dateModified: Long? = null,
        /** @see Song.path */
        var fileName: String? = null,
        /** @see Song.path */
        var directory: Directory? = null,
        /** @see Song.size */
        var size: Long? = null,
        /** @see Song.durationMs */
        var durationMs: Long? = null,
        /** @see Song.mimeType */
        var extensionMimeType: String? = null,
        /** @see Music.UID */
        var musicBrainzId: String? = null,
        /** @see Music.rawName */
        var name: String? = null,
        /** @see Music.rawSortName */
        var sortName: String? = null,
        /** @see Song.track */
        var track: Int? = null,
        /** @see Song.disc */
        var disc: Int? = null,
        /** @see Song.date */
        var date: Date? = null,
        /** @see Album.Raw.mediaStoreId */
        var albumMediaStoreId: Long? = null,
        /** @see Album.Raw.musicBrainzId */
        var albumMusicBrainzId: String? = null,
        /** @see Album.Raw.name */
        var albumName: String? = null,
        /** @see Album.Raw.sortName */
        var albumSortName: String? = null,
        /** @see Album.Raw.releaseType */
        var releaseTypes: List<String> = listOf(),
        /** @see Artist.Raw.musicBrainzId */
        var artistMusicBrainzIds: List<String> = listOf(),
        /** @see Artist.Raw.name */
        var artistNames: List<String> = listOf(),
        /** @see Artist.Raw.sortName */
        var artistSortNames: List<String> = listOf(),
        /** @see Artist.Raw.musicBrainzId */
        var albumArtistMusicBrainzIds: List<String> = listOf(),
        /** @see Artist.Raw.name */
        var albumArtistNames: List<String> = listOf(),
        /** @see Artist.Raw.sortName */
        var albumArtistSortNames: List<String> = listOf(),
        /** @see Genre.Raw.name */
        var genreNames: List<String> = listOf()
    )
}

/**
 * An abstract release group. While it may be called an album, it encompasses other types of
 * releases like singles, EPs, and compilations.
 * @param raw The [Album.Raw] to derive the member data from.
 * @param songs The [Song]s that are a part of this [Album]. These items will be linked to this
 * [Album].
 * @author Alexander Capehart (OxygenCobalt)
 */
class Album constructor(raw: Raw, override val songs: List<Song>) : MusicParent() {
    override val uid =
    // Attempt to use a MusicBrainz ID first before falling back to a hashed UID.
    raw.musicBrainzId?.let { UID.musicBrainz(MusicMode.ALBUMS, it) }
            ?: UID.auxio(MusicMode.ALBUMS) {
                // Hash based on only names despite the presence of a date to increase stability.
                // I don't know if there is any situation where an artist will have two albums with
                // the exact same name, but if there is, I would love to know.
                update(raw.name)
                update(raw.rawArtists.map { it.name })
            }
    override val rawName = raw.name
    override val rawSortName = raw.sortName
    override val collationKey = makeCollationKeyImpl()
    override fun resolveName(context: Context) = rawName

    /** The [Date.Range] that [Song]s in the [Album] were released. */
    val dates = Date.Range.from(songs.mapNotNull { it.date })

    /**
     * The [ReleaseType] of this album, signifying the type of release it actually is. Defaults to
     * [ReleaseType.Album].
     */
    val releaseType = raw.releaseType ?: ReleaseType.Album(null)
    /**
     * The URI to a MediaStore-provided album cover. These images will be fast to load, but at the
     * cost of image quality.
     */
    val coverUri = raw.mediaStoreId.toCoverUri()

    /** The duration of all songs in the album, in milliseconds. */
    val durationMs: Long

    /** The earliest date a song in this album was added, as a unix epoch timestamp. */
    val dateAdded: Long

    init {
        var totalDuration: Long = 0
        var earliestDateAdded: Long = Long.MAX_VALUE

        // Do linking and value generation in the same loop for efficiency.
        for (song in songs) {
            song._link(this)
            if (song.dateAdded < earliestDateAdded) {
                earliestDateAdded = song.dateAdded
            }
            totalDuration += song.durationMs
        }

        durationMs = totalDuration
        dateAdded = earliestDateAdded
    }

    private val _artists = mutableListOf<Artist>()
    /**
     * The parent [Artist]s of this [Album]. Is often one, but there can be multiple if more than
     * one [Artist] name was specified in the metadata of the [Song]'s. Unlike [Song], album artists
     * are prioritized for this field.
     */
    val artists: List<Artist>
        get() = _artists

    /**
     * Resolves one or more [Artist]s into a single piece of human-readable names.
     * @param context [Context] required for [resolveName].
     */
    fun resolveArtistContents(context: Context) = resolveNames(context, artists)

    /**
     * Checks if the [Artist] *display* of this [Album] and another [Album] are equal. This will
     * only compare surface-level names, and not [Music.UID]s.
     * @param other The [Album] to compare to.
     * @return True if the [Artist] displays are equal, false otherwise
     */
    fun areArtistContentsTheSame(other: Album): Boolean {
        for (i in 0 until max(artists.size, other.artists.size)) {
            val a = artists.getOrNull(i) ?: return false
            val b = other.artists.getOrNull(i) ?: return false
            if (a.rawName != b.rawName) {
                return false
            }
        }

        return true
    }

    // --- INTERNAL FIELDS ---

    /**
     * The [Artist.Raw] instances collated by the [Album]. The album artists of the song take
     * priority, followed by the artists. If there are no artists, this field will be a single
     * "unknown" [Artist.Raw]. This can be used to group up [Album]s into an [Artist]. **This is
     * only meant for use within the music package.**
     */
    val _rawArtists = raw.rawArtists

    /**
     * Links this [Album] with a parent [Artist].
     * @param artist The parent [Artist] to link to. **This is only meant for use within the music
     * package.**
     */
    fun _link(artist: Artist) {
        _artists.add(artist)
    }

    override fun _finalize() {
        check(songs.isNotEmpty()) { "Malformed album: Empty" }
        check(_artists.isNotEmpty()) { "Malformed album: No artists" }
        for (i in _artists.indices) {
            // Non-destructively reorder the linked artists so that they align with
            // the artist ordering within the song metadata.
            val newIdx = _artists[i]._getOriginalPositionIn(_rawArtists)
            val other = _artists[newIdx]
            _artists[newIdx] = _artists[i]
            _artists[i] = other
        }
    }

    /**
     * Raw information about an [Album] obtained from the component [Song] instances. **This is only
     * meant for use within the music package.**
     */
    class Raw(
        /**
         * The ID of the [Album]'s grouping, obtained from MediaStore. Note that this ID is highly
         * unstable and should only be used for accessing the system-provided cover art.
         */
        val mediaStoreId: Long,
        /** @see Music.uid */
        val musicBrainzId: UUID?,
        /** @see Music.rawName */
        val name: String,
        /** @see Music.rawSortName */
        val sortName: String?,
        /** @see Album.releaseType */
        val releaseType: ReleaseType?,
        /** @see Artist.Raw.name */
        val rawArtists: List<Artist.Raw>
    ) {
        // Albums are grouped as follows:
        // - If we have a MusicBrainz ID, only group by it. This allows different Albums with the
        // same name to be differentiated, which is common in large libraries.
        // - If we do not have a MusicBrainz ID, compare by the lowercase album name and lowercase
        // artist name. This allows for case-insensitive artist/album grouping, which can be common
        // for albums/artists that have different naming (ex. "RAMMSTEIN" vs. "Rammstein").

        // Cache the hash-code for HashMap efficiency.
        private val hashCode =
            musicBrainzId?.hashCode() ?: (31 * name.lowercase().hashCode() + rawArtists.hashCode())

        override fun hashCode() = hashCode

        override fun equals(other: Any?) =
            other is Raw &&
                when {
                    musicBrainzId != null && other.musicBrainzId != null ->
                        musicBrainzId == other.musicBrainzId
                    musicBrainzId == null && other.musicBrainzId == null ->
                        name.equals(other.name, true) && rawArtists == other.rawArtists
                    else -> false
                }
    }
}

/**
 * An abstract artist. These are actually a combination of the artist and album artist tags from
 * within the library, derived from [Song]s and [Album]s respectively.
 * @param raw The [Artist.Raw] to derive the member data from.
 * @param songAlbums A list of the [Song]s and [Album]s that are a part of this [Artist], either
 * through artist or album artist tags. Providing [Song]s to the artist is optional. These instances
 * will be linked to this [Artist].
 * @author Alexander Capehart (OxygenCobalt)
 */
class Artist constructor(private val raw: Raw, songAlbums: List<Music>) : MusicParent() {
    override val uid =
    // Attempt to use a MusicBrainz ID first before falling back to a hashed UID.
    raw.musicBrainzId?.let { UID.musicBrainz(MusicMode.ARTISTS, it) }
            ?: UID.auxio(MusicMode.ARTISTS) { update(raw.name) }
    override val rawName = raw.name
    override val rawSortName = raw.sortName
    override val collationKey = makeCollationKeyImpl()
    override fun resolveName(context: Context) = rawName ?: context.getString(R.string.def_artist)
    override val songs: List<Song>

    /**
     * All of the [Album]s this artist is credited to. Note that any [Song] credited to this artist
     * will have it's [Album] considered to be "indirectly" linked to this [Artist], and thus
     * included in this list.
     */
    val albums: List<Album>

    /**
     * The duration of all [Song]s in the artist, in milliseconds. Will be null if there are no
     * songs.
     */
    val durationMs: Long?

    /**
     * Whether this artist is considered a "collaborator", i.e it is not directly credited on any
     * [Album].
     */
    val isCollaborator: Boolean

    init {
        val distinctSongs = mutableSetOf<Song>()
        val distinctAlbums = mutableSetOf<Album>()

        var noAlbums = true

        for (music in songAlbums) {
            when (music) {
                is Song -> {
                    music._link(this)
                    distinctSongs.add(music)
                    distinctAlbums.add(music.album)
                }
                is Album -> {
                    music._link(this)
                    distinctAlbums.add(music)
                    noAlbums = false
                }
                else -> error("Unexpected input music ${music::class.simpleName}")
            }
        }

        songs = distinctSongs.toList()
        albums = distinctAlbums.toList()
        durationMs = songs.sumOf { it.durationMs }.nonZeroOrNull()
        isCollaborator = noAlbums
    }

    private lateinit var genres: List<Genre>

    /**
     * Resolves one or more [Genre]s into a single piece of human-readable names.
     * @param context [Context] required for [resolveName].
     */
    fun resolveGenreContents(context: Context) = resolveNames(context, genres)

    /**
     * Checks if the [Genre] *display* of this [Artist] and another [Artist] are equal. This will
     * only compare surface-level names, and not [Music.UID]s.
     * @param other The [Artist] to compare to.
     * @return True if the [Genre] displays are equal, false otherwise
     */
    fun areGenreContentsTheSame(other: Artist): Boolean {
        for (i in 0 until max(genres.size, other.genres.size)) {
            val a = genres.getOrNull(i) ?: return false
            val b = other.genres.getOrNull(i) ?: return false
            if (a.rawName != b.rawName) {
                return false
            }
        }

        return true
    }

    // --- INTERNAL METHODS ---

    /**
     * Returns the original position of this [Artist]'s [Artist.Raw] within the given [Artist.Raw]
     * list. This can be used to create a consistent ordering within child [Artist] lists based on
     * the original tag order.
     * @param rawArtists The [Artist.Raw] instances to check. It is assumed that this [Artist]'s
     * [Artist.Raw] will be within the list.
     * @return The index of the [Artist]'s [Artist.Raw] within the list. **This is only meant for
     * use within the music package.**
     */
    fun _getOriginalPositionIn(rawArtists: List<Raw>) = rawArtists.indexOf(raw)

    override fun _finalize() {
        check(songs.isNotEmpty() || albums.isNotEmpty()) { "Malformed artist: Empty" }
        genres =
            Sort(Sort.Mode.ByName, true)
                .genres(songs.flatMapTo(mutableSetOf()) { it.genres })
                .sortedByDescending { genre -> songs.count { it.genres.contains(genre) } }
    }

    /**
     * Raw information about an [Artist] obtained from the component [Song] and [Album] instances.
     * **This is only meant for use within the music package.**
     */
    class Raw(
        /** @see Music.UID */
        val musicBrainzId: UUID? = null,
        /** @see Music.rawName */
        val name: String? = null,
        /** @see Music.rawSortName */
        val sortName: String? = null
    ) {
        // Artists are grouped as follows:
        // - If we have a MusicBrainz ID, only group by it. This allows different Artists with the
        // same name to be differentiated, which is common in large libraries.
        // - If we do not have a MusicBrainz ID, compare by the lowercase name. This allows artist
        // grouping to be case-insensitive.

        // Cache the hashCode for HashMap efficiency.
        private val hashCode = musicBrainzId?.hashCode() ?: name?.lowercase().hashCode()

        // Compare names and MusicBrainz IDs in order to differentiate artists with the
        // same name in large libraries.

        override fun hashCode() = hashCode

        override fun equals(other: Any?) =
            other is Raw &&
                when {
                    musicBrainzId != null && other.musicBrainzId != null ->
                        musicBrainzId == other.musicBrainzId
                    musicBrainzId == null && other.musicBrainzId == null ->
                        when {
                            name != null && other.name != null -> name.equals(other.name, true)
                            name == null && other.name == null -> true
                            else -> false
                        }
                    else -> false
                }
    }
}

/**
 * A genre of [Song]s.
 * @author Alexander Capehart (OxygenCobalt)
 */
class Genre constructor(private val raw: Raw, override val songs: List<Song>) : MusicParent() {
    override val uid = UID.auxio(MusicMode.GENRES) { update(raw.name) }
    override val rawName = raw.name
    override val rawSortName = rawName
    override val collationKey = makeCollationKeyImpl()
    override fun resolveName(context: Context) = rawName ?: context.getString(R.string.def_genre)

    /** The albums indirectly linked to by the [Song]s of this [Genre]. */
    val albums: List<Album>

    /** The artists indirectly linked to by the [Artist]s of this [Genre]. */
    val artists: List<Artist>

    /** The total duration of the songs in this genre, in milliseconds. */
    val durationMs: Long

    init {
        val distinctAlbums = mutableSetOf<Album>()
        val distinctArtists = mutableSetOf<Artist>()
        var totalDuration = 0L

        for (song in songs) {
            song._link(this)
            distinctAlbums.add(song.album)
            distinctArtists.addAll(song.artists)
            totalDuration += song.durationMs
        }

        albums =
            Sort(Sort.Mode.ByName, true).albums(distinctAlbums).sortedByDescending { album ->
                album.songs.count { it.genres.contains(this) }
            }
        artists = Sort(Sort.Mode.ByName, true).artists(distinctArtists)
        durationMs = totalDuration
    }

    // --- INTERNAL METHODS ---

    /**
     * Returns the original position of this [Genre]'s [Genre.Raw] within the given [Genre.Raw]
     * list. This can be used to create a consistent ordering within child [Genre] lists based on
     * the original tag order.
     * @param rawGenres The [Genre.Raw] instances to check. It is assumed that this [Genre]'s
     * [Genre.Raw] will be within the list.
     * @return The index of the [Genre]'s [Genre.Raw] within the list. **This is only meant for use
     * within the music package.**
     */
    fun _getOriginalPositionIn(rawGenres: List<Raw>) = rawGenres.indexOf(raw)

    override fun _finalize() {
        check(songs.isNotEmpty()) { "Malformed genre: Empty" }
    }

    /**
     * Raw information about a [Genre] obtained from the component [Song] instances. **This is only
     * meant for use within the music package.**
     */
    class Raw(
        /** @see Music.rawName */
        val name: String? = null
    ) {
        // Only group by the lowercase genre name. This allows Genre grouping to be
        // case-insensitive, which may be helpful in some libraries with different ways of
        // formatting genres.

        // Cache the hashCode for HashMap efficiency.
        private val hashCode = name?.lowercase().hashCode()

        override fun hashCode() = hashCode

        override fun equals(other: Any?) =
            other is Raw &&
                when {
                    name != null && other.name != null -> name.equals(other.name, true)
                    name == null && other.name == null -> true
                    else -> false
                }
    }
}

// --- MUSIC UID CREATION UTILITIES ---

/**
 * Convert a [String] to a [UUID].
 * @return A [UUID] converted from the [String] value, or null if the value was not valid.
 * @see UUID.fromString
 */
private fun String.toUuidOrNull(): UUID? =
    try {
        UUID.fromString(this)
    } catch (e: IllegalArgumentException) {
        null
    }

/**
 * Update a [MessageDigest] with a lowercase [String].
 * @param string The [String] to hash. If null, it will not be hashed.
 */
@VisibleForTesting
fun MessageDigest.update(string: String?) {
    if (string != null) {
        update(string.lowercase().toByteArray())
    } else {
        update(0)
    }
}

/**
 * Update a [MessageDigest] with the string representation of a [Date].
 * @param date The [Date] to hash. If null, nothing will be done.
 */
@VisibleForTesting
fun MessageDigest.update(date: Date?) {
    if (date != null) {
        update(date.toString().toByteArray())
    } else {
        update(0)
    }
}

/**
 * Update a [MessageDigest] with the lowercase versions of all of the input [String]s.
 * @param strings The [String]s to hash. If a [String] is null, it will not be hashed.
 */
@VisibleForTesting
fun MessageDigest.update(strings: List<String?>) {
    strings.forEach(::update)
}

/**
 * Update a [MessageDigest] with the little-endian bytes of a [Int].
 * @param n The [Int] to write. If null, nothing will be done.
 */
@VisibleForTesting
fun MessageDigest.update(n: Int?) {
    if (n != null) {
        update(byteArrayOf(n.toByte(), n.shr(8).toByte(), n.shr(16).toByte(), n.shr(24).toByte()))
    } else {
        update(0)
    }
}
