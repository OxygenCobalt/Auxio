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
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.extractor.parseId3GenreNames
import org.oxycblt.auxio.music.extractor.parseMultiValue
import org.oxycblt.auxio.music.extractor.parseReleaseType
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.util.nonZeroOrNull
import org.oxycblt.auxio.util.unlikelyToBeNull
import java.security.MessageDigest
import java.text.CollationKey
import java.text.Collator
import java.util.UUID
import kotlin.math.max

// --- MUSIC MODELS ---

/** [Item] variant that represents a music item. */
sealed class Music : Item {
    abstract val uid: UID

    /** The raw name of this item. Null if unknown. */
    abstract val rawName: String?

    /** The raw sorting name of this item. Null if not present. */
    abstract val rawSortName: String?

    /**
     * A key used by the sorting system that takes into account the sort tags of this item,
     * any (english) articles that prefix the names, and collation rules.
     */
    abstract val collationKey: CollationKey?

    /**
     * Resolve a name from it's raw form to a form suitable to be shown in a UI.
     * Null values will be resolved into their string form with this function.
     */
    abstract fun resolveName(context: Context): String

    // Equality is based on UIDs, as some items (Especially artists) can have identical
    // properties (Name) yet non-identical UIDs due to MusicBrainz tags

    override fun hashCode() = uid.hashCode()

    override fun equals(other: Any?) =
        other is Music && javaClass == other.javaClass && uid == other.uid

    /**
     * Workaround to allow for easy collation key generation in the initializer without
     * base-class initialization issues or slow lazy initialization.
     */
    protected fun makeCollationKeyImpl(): CollationKey? {
        val sortName = (rawSortName ?: rawName)?.run {
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
     * Called when the library has been linked and validation/construction steps dependent
     * on linked items should run. It's also used to do last-step initialization of fields
     * that require any parent values that would not be present during startup.
     */
    abstract fun _finalize()

    /**
     * A unique identifier for a piece of music.
     *
     * UID enables a much cheaper and more reliable form of differentiating music, derived from
     * either a hash of meaningful metadata or the MusicBrainz UUID spec. It is the default datatype
     * used when comparing music, and it is also the datatype used when serializing music to
     * external sources, as it can persist across app restarts and does not need to encode useless
     * information about the relationships between items.
     *
     * Note: While the core of a UID is a UUID. The whole is not technically a UUID, with
     * string representation in particular having multiple extensions to increase uniqueness.
     * Please don't try to do anything interesting with this and just assume it's a black box
     * that can only be compared, serialized, and deserialized.
     *
     * @author OxygenCobalt
     */
    @Parcelize
    class UID private constructor(
        private val format: Format,
        private val mode: MusicMode,
        private val uuid: UUID
    ) : Parcelable {
        // Cache the hashCode for speed
        @IgnoredOnParcel private var hashCode = format.hashCode()

        init {
            hashCode = 31 * hashCode + mode.hashCode()
            hashCode = 31 * hashCode + uuid.hashCode()
        }

        override fun hashCode() = hashCode

        override fun equals(other: Any?) = other is UID &&
            format == other.format &&
            mode == other.mode && uuid == other.uuid

        // UID string format is roughly:
        // format_namespace:music_mode_int-uuid
        override fun toString() = "${format.namespace}:${mode.intCode.toString(16)}-$uuid"

        private enum class Format(val namespace: String) {
            AUXIO("org.oxycblt.auxio"),
            MUSICBRAINZ("org.musicbrainz")
        }

        companion object {
            /** Parse a [UID] from the string [uid]. Returns null if not valid. */
            fun fromString(uid: String): UID? {
                val split = uid.split(':', limit = 2)
                if (split.size != 2) {
                    return null
                }

                val format = when (split[0]) {
                    Format.AUXIO.namespace -> Format.AUXIO
                    Format.MUSICBRAINZ.namespace -> Format.MUSICBRAINZ
                    else -> return null
                }

                val ids = split[1].split('-', limit = 2)
                if (ids.size != 2) {
                    return null
                }

                val mode = MusicMode.fromInt(ids[0].toIntOrNull(16) ?: return null) ?: return null
                val uuid = ids[1].toUuidOrNull() ?: return null

                return UID(format, mode, uuid)
            }

            /**
             * Make a UUID derived from the MD5 hash of the data digested in [updates].
             */
            fun auxio(mode: MusicMode, updates: MessageDigest.() -> Unit): UID {
                // Auxio hashes consist of the MD5 hash of the non-subjective, consistent
                // tags in a music item. For easier use with MusicBrainz IDs, we transform
                // this into a UUID too.
                val digest = MessageDigest.getInstance("MD5")
                updates(digest)
                val uuid = digest.digest().toUuid()
                return UID(Format.AUXIO, mode, uuid)
            }

            /**
             * Make a UUID derived from a MusicBrainz ID.
             */
            fun musicBrainz(mode: MusicMode, uuid: UUID): UID =
                UID(Format.MUSICBRAINZ, mode, uuid)
        }
    }

    companion object {
        private val COLLATOR = Collator.getInstance().apply {
            strength = Collator.PRIMARY
        }
    }
}

/**
 * [Music] variant that denotes that this object is a parent of other data objects, such as an
 * [Album] or [Artist]
 */
sealed class MusicParent : Music() {
    /** The songs that this parent owns. */
    abstract val songs: List<Song>
}

/**
 * A song.
 * @author OxygenCobalt
 */
class Song constructor(raw: Raw, settings: Settings) : Music() {
    override val uid = raw.musicBrainzId?.toUuidOrNull()?.let { UID.musicBrainz(MusicMode.SONGS, it) }
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

    /** The track number of this song in it's album.. */
    val track = raw.track

    /** The disc number of this song in it's album. */
    val disc = raw.disc

    /** The date of this song. May differ from the album date. */
    val date = raw.date

    /** The URI pointing towards this audio file. */
    val uri = requireNotNull(raw.mediaStoreId) { "Invalid raw: No id" }.audioUri

    /**
     * The path component of the audio file for this music. Only intended for display. Use [uri] to
     * open the audio file.
     */
    val path =
        Path(
            name = requireNotNull(raw.fileName) { "Invalid raw: No display name" },
            parent = requireNotNull(raw.directory) { "Invalid raw: No parent directory" }
        )

    /** The mime type of the audio file. Only intended for display. */
    val mimeType =
        MimeType(
            fromExtension = requireNotNull(raw.extensionMimeType) { "Invalid raw: No mime type" },
            fromFormat = raw.formatMimeType
        )

    /** The size of this audio file. */
    val size = requireNotNull(raw.size) { "Invalid raw: No size" }

    /** The duration of this audio file, in millis. */
    val durationMs = requireNotNull(raw.durationMs) { "Invalid raw: No duration" }

    /** The date this audio file was added, as a unix epoch timestamp. */
    val dateAdded = requireNotNull(raw.dateAdded) { "Invalid raw: No date added" }

    private var _album: Album? = null

    /**
     * The album of this song. Every song is guaranteed to have one and only one album,
     * with a "directory" album being used if no album tag can be found.
     */
    val album: Album
        get() = unlikelyToBeNull(_album)

    private val artistMusicBrainzIds = raw.artistMusicBrainzIds.parseMultiValue(settings)

    private val artistNames = raw.artistNames.parseMultiValue(settings)

    private val artistSortNames = raw.artistSortNames.parseMultiValue(settings)

    private val albumArtistMusicBrainzIds = raw.albumArtistMusicBrainzIds.parseMultiValue(settings)

    private val albumArtistNames = raw.albumArtistNames.parseMultiValue(settings)

    private val albumArtistSortNames = raw.albumArtistSortNames.parseMultiValue(settings)

    private val rawArtists = artistNames.mapIndexed { i, name ->
        Artist.Raw(artistMusicBrainzIds.getOrNull(i)?.toUuidOrNull(), name, artistSortNames.getOrNull(i))
    }

    private val rawAlbumArtists = albumArtistNames.mapIndexed { i, name ->
        Artist.Raw(albumArtistMusicBrainzIds.getOrNull(i)?.toUuidOrNull(), name, albumArtistSortNames.getOrNull(i))
    }

    private val _artists = mutableListOf<Artist>()

    /**
     * The artists of this song. Most often one, but there could be multiple. These artists
     * are derived from the artists tag and not the album artists tag, so they may differ from
     * the artists of the album.
     */
    val artists: List<Artist>
        get() = _artists

    /**
     * Resolve the artists of this song into a human-readable name. First tries to use artist
     * tags, then falls back to album artist tags.
     */
    fun resolveArtistContents(context: Context) =
        artists.joinToString { it.resolveName(context) }

    /**
     * Utility method for recyclerview diffing that checks if resolveArtistContents is the
     * same without a context.
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
     * The genres of this song. Most often one, but there could be multiple. There will always be at
     * least one genre, even if it is an "unknown genre" instance.
     */
    val genres: List<Genre>
        get() = _genres

    /**
     * Resolve the genres of the song into a human-readable string.
     */
    fun resolveGenreContents(context: Context) = genres.joinToString { it.resolveName(context) }

    // --- INTERNAL FIELDS ---

    val _rawGenres = raw.genreNames.parseId3GenreNames(settings)
        .map { Genre.Raw(it) }.ifEmpty { listOf(Genre.Raw()) }

    val _rawArtists = rawArtists.ifEmpty { rawAlbumArtists }.ifEmpty {
        listOf(Artist.Raw())
    }

    val _rawAlbum =
        Album.Raw(
            mediaStoreId = requireNotNull(raw.albumMediaStoreId) { "Invalid raw: No album id" },
            musicBrainzId = raw.albumMusicBrainzId?.toUuidOrNull(),
            name = requireNotNull(raw.albumName) { "Invalid raw: No album name" },
            sortName = raw.albumSortName,
            releaseType = raw.albumReleaseType.parseReleaseType(settings),
            rawArtists = rawAlbumArtists.ifEmpty { rawArtists }.ifEmpty { listOf(Artist.Raw(null, null)) }
        )

    fun _link(album: Album) {
        _album = album
    }

    fun _link(artist: Artist) {
        _artists.add(artist)
    }

    fun _link(genre: Genre) {
        _genres.add(genre)
    }

    override fun _finalize() {
        checkNotNull(_album) { "Malformed song: No album" }
        check(_artists.isNotEmpty()) { "Malformed song: No artists" }
        Sort(Sort.Mode.ByName, true).artistsInPlace(_artists)
        check(_genres.isNotEmpty()) { "Malformed song: No genres" }
        Sort(Sort.Mode.ByName, true).genresInPlace(_genres)
    }

    class Raw
    constructor(
        var mediaStoreId: Long? = null,
        var musicBrainzId: String? = null,
        var name: String? = null,
        var fileName: String? = null,
        var sortName: String? = null,
        var directory: Directory? = null,
        var extensionMimeType: String? = null,
        var formatMimeType: String? = null,
        var size: Long? = null,
        var dateAdded: Long? = null,
        var dateModified: Long? = null,
        var durationMs: Long? = null,
        var track: Int? = null,
        var disc: Int? = null,
        var date: Date? = null,
        var albumMediaStoreId: Long? = null,
        var albumMusicBrainzId: String? = null,
        var albumName: String? = null,
        var albumSortName: String? = null,
        var albumReleaseType: List<String> = listOf(),
        var artistMusicBrainzIds: List<String> = listOf(),
        var artistNames: List<String> = listOf(),
        var artistSortNames: List<String> = listOf(),
        var albumArtistMusicBrainzIds: List<String> = listOf(),
        var albumArtistNames: List<String> = listOf(),
        var albumArtistSortNames: List<String> = listOf(),
        var genreNames: List<String> = listOf()
    )
}

/**
 * An album.
 * @author OxygenCobalt
 */
class Album constructor(raw: Raw, override val songs: List<Song>) : MusicParent() {
    override val uid = raw.musicBrainzId?.let { UID.musicBrainz(MusicMode.ALBUMS, it) }
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

    /** The earliest date this album was released. */
    val date: Date?

    /** The release type of this album, such as "EP". Defaults to "Album". */
    val releaseType = raw.releaseType ?: ReleaseType.Album(null)

    /**
     * The album cover URI for this album. Usually low quality, so using Coil is recommended
     * instead.
     */
    val coverUri = raw.mediaStoreId.albumCoverUri

    /** The total duration of songs in this album, in millis. */
    val durationMs: Long

    /** The earliest date a song in this album was added. */
    val dateAdded: Long

    /**
     * The artists of this album. Usually one, but there may be more. These are derived from
     * the album artist first, so they may differ from the song artists.
     */
    private val _artists = mutableListOf<Artist>()
    val artists: List<Artist> get() = _artists

    /**
     * Resolve the artists of this album in a human-readable manner.
     */
    fun resolveArtistContents(context: Context) =
        artists.joinToString { it.resolveName(context) }

    /**
     * Utility for RecyclerView differs to check if resolveArtistContents is the same without
     * a context.
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

    init {
        var earliestDate: Date? = null
        var totalDuration: Long = 0
        var earliestDateAdded: Long = 0

        // Do linking and value generation in the same loop to save time
        for (song in songs) {
            song._link(this)

            if (song.date != null) {
                if (earliestDate == null || song.date < earliestDate) {
                    earliestDate = song.date
                }
            }

            if (song.dateAdded < earliestDateAdded) {
                earliestDateAdded = song.dateAdded
            }

            totalDuration += song.durationMs
        }

        date = earliestDate
        durationMs = totalDuration
        dateAdded = earliestDateAdded
    }

    // --- INTERNAL FIELDS ---

    val _rawArtists = raw.rawArtists

    fun _link(artist: Artist) {
        _artists.add(artist)
    }

    override fun _finalize() {
        check(songs.isNotEmpty()) { "Malformed album: Empty" }
        check(_artists.isNotEmpty()) { "Malformed album: No artists" }
        Sort(Sort.Mode.ByName, true).artistsInPlace(_artists)
    }

    class Raw(
        val mediaStoreId: Long,
        val musicBrainzId: UUID?,
        val name: String,
        val sortName: String?,
        val releaseType: ReleaseType?,
        val rawArtists: List<Artist.Raw>
    ) {
        private val hashCode =
            musicBrainzId?.hashCode() ?: (31 * name.lowercase().hashCode() + rawArtists.hashCode())

        override fun hashCode() = hashCode

        override fun equals(other: Any?): Boolean {
            if (other !is Raw) return false
            if (musicBrainzId != null && other.musicBrainzId != null &&
                musicBrainzId == other.musicBrainzId
            ) {
                return true
            }

            return name.equals(other.name, true) && rawArtists == other.rawArtists
        }
    }
}

/**
 * An abstract artist. This is derived from both album artist values and artist values in
 * albums and songs respectively.
 * @author OxygenCobalt
 */
class Artist
constructor(raw: Raw, songAlbums: List<Music>) : MusicParent() {
    override val uid = raw.musicBrainzId?.let { UID.musicBrainz(MusicMode.ARTISTS, it) } ?: UID.auxio(MusicMode.ARTISTS) { update(raw.name) }

    override val rawName = raw.name

    override val rawSortName = raw.sortName

    override val collationKey = makeCollationKeyImpl()

    override fun resolveName(context: Context) = rawName ?: context.getString(R.string.def_artist)

    /**
     * The songs of this artist. This might be empty.
     */
    override val songs: List<Song>

    /** The total duration of songs in this artist, in millis. Null if there are no songs. */
    val durationMs: Long?

    /** The albums of this artist. This will never be empty. */
    val albums: List<Album>

    private lateinit var genres: List<Genre>

    /**
     * Resolve the combined genres of this artist into a human-readable string.
     */
    fun resolveGenreContents(context: Context) = genres.joinToString { it.resolveName(context) }

    /**
     * Utility for RecyclerView differs to check if resolveGenreContents is the same without
     * a context.
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

    init {
        val distinctSongs = mutableSetOf<Song>()
        val distinctAlbums = mutableSetOf<Album>()

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
                }

                else -> error("Unexpected input music ${music::class.simpleName}")
            }
        }

        songs = distinctSongs.toList()
        albums = distinctAlbums.toList()
        durationMs = songs.sumOf { it.durationMs }.nonZeroOrNull()
    }

    override fun _finalize() {
        check(songs.isNotEmpty() || albums.isNotEmpty()) { "Malformed artist: Empty" }

        genres = Sort(Sort.Mode.ByName, true).genres(songs.flatMapTo(mutableSetOf()) { it.genres })
            .sortedByDescending { genre -> songs.count { it.genres.contains(genre) } }
    }

    class Raw(val musicBrainzId: UUID? = null, val name: String? = null, val sortName: String? = null) {
        private val hashCode = musicBrainzId?.hashCode() ?: name?.lowercase().hashCode()

        override fun hashCode() = hashCode

        override fun equals(other: Any?): Boolean {
            if (other !is Raw) return false

            if (musicBrainzId != null && other.musicBrainzId != null &&
                musicBrainzId == other.musicBrainzId
            ) {
                return true
            }

            return when {
                name != null && other.name != null -> name.equals(other.name, true)
                name == null && other.name == null -> true
                else -> false
            }
        }
    }
}

/**
 * A genre.
 * @author OxygenCobalt
 */
class Genre constructor(raw: Raw, override val songs: List<Song>) : MusicParent() {
    override val uid = UID.auxio(MusicMode.GENRES) { update(raw.name) }

    override val rawName = raw.name

    // Sort tags don't make sense on genres
    override val rawSortName = rawName

    override val collationKey = makeCollationKeyImpl()

    override fun resolveName(context: Context) = rawName ?: context.getString(R.string.def_genre)

    /** The total duration of the songs in this genre, in millis. */
    val durationMs: Long

    /** The albums of this genre. */
    val albums: List<Album>

    init {
        var totalDuration = 0L
        val distinctAlbums = mutableSetOf<Album>()

        for (song in songs) {
            song._link(this)
            distinctAlbums.add(song.album)
            totalDuration += song.durationMs
        }

        durationMs = totalDuration

        albums = Sort(Sort.Mode.ByName, true).albums(distinctAlbums)
            .sortedByDescending { album ->
                album.songs.count { it.genres.contains(this) }
            }
    }

    override fun _finalize() {
        check(songs.isNotEmpty()) { "Malformed genre: Empty" }
    }

    class Raw(val name: String? = null) {
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

// Hashing extensions

/** Update the digest using the lowercase variant of a string, or don't update if null. */
fun MessageDigest.update(string: String?) {
    if (string == null) return
    update(string.lowercase().toByteArray())
}

/** Update the digest using a date. */
fun MessageDigest.update(date: Date?) {
    if (date == null) return
    update(date.toString().toByteArray())
}

/** Update the digest using a list of strings. */
fun MessageDigest.update(strings: List<String?>) {
    strings.forEach(::update)
}

// Note: All methods regarding integer byte-mucking must be little-endian

/**
 * Update the digest using the little-endian byte representation of a byte, or do not update if
 * null.
 */
fun MessageDigest.update(n: Int?) {
    if (n == null) return
    update(byteArrayOf(n.toByte(), n.shr(8).toByte(), n.shr(16).toByte(), n.shr(24).toByte()))
}

/**
 * Update the digest using the little-endian byte representation of a long, or do not update if
 * null.
 */
fun MessageDigest.update(n: Long?) {
    if (n == null) return
    update(
        byteArrayOf(
            n.toByte(),
            n.shr(8).toByte(),
            n.shr(16).toByte(),
            n.shr(24).toByte(),
            n.shr(32).toByte(),
            n.shr(40).toByte(),
            n.shl(48).toByte(),
            n.shr(56).toByte()
        )
    )
}

/**
 * Convert an array of 16 bytes to a UUID. Java is a bit strange in that it represents their UUIDs
 * as two longs, however we will not assume that the given bytes represent two little endian longs.
 * We will treat them as a raw sequence of bytes and serialize them as such.
 */
fun ByteArray.toUuid(): UUID {
    check(size == 16)
    return UUID(
        get(0)
            .toLong()
            .shl(56)
            .or(get(1).toLong().and(0xFF).shl(48))
            .or(get(2).toLong().and(0xFF).shl(40))
            .or(get(3).toLong().and(0xFF).shl(32))
            .or(get(4).toLong().and(0xFF).shl(24))
            .or(get(5).toLong().and(0xFF).shl(16))
            .or(get(6).toLong().and(0xFF).shl(8))
            .or(get(7).toLong().and(0xFF)),
        get(8)
            .toLong()
            .shl(56)
            .or(get(9).toLong().and(0xFF).shl(48))
            .or(get(10).toLong().and(0xFF).shl(40))
            .or(get(11).toLong().and(0xFF).shl(32))
            .or(get(12).toLong().and(0xFF).shl(24))
            .or(get(13).toLong().and(0xFF).shl(16))
            .or(get(14).toLong().and(0xFF).shl(8))
            .or(get(15).toLong().and(0xFF))
    )
}
