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
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Date.Companion.from
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.util.inRangeOrNull
import org.oxycblt.auxio.util.nonZeroOrNull
import org.oxycblt.auxio.util.unlikelyToBeNull
import java.security.MessageDigest
import java.util.UUID
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass

// --- MUSIC MODELS ---

/** [Item] variant that represents a music item. */
sealed class Music : Item {
    abstract val uid: UID

    /** The raw name of this item. Null if unknown. */
    abstract val rawName: String?

    /** The raw sorting name of this item. Null if not present. */
    abstract val rawSortName: String?

    /**
     * The name of this item used for sorting.This should not be used outside of sorting and
     * fast-scrolling.
     */
    val sortName: String?
        get() =
            rawSortName
                ?: rawName?.run {
                    when {
                        length > 5 && startsWith("the ", ignoreCase = true) -> substring(4)
                        length > 4 && startsWith("an ", ignoreCase = true) -> substring(3)
                        length > 3 && startsWith("a ", ignoreCase = true) -> substring(2)
                        else -> this
                    }
                }

    /**
     * Resolve a name from it's raw form to a form suitable to be shown in a ui. Ex. "unknown" would
     * become Unknown Artist, (124) would become its proper genre name, etc.
     */
    abstract fun resolveName(context: Context): String

    // Equality is based on UIDs, as some items (Especially artists) can have identical
    // properties (Name) yet non-identical UIDs due to MusicBrainz tags

    override fun hashCode() = uid.hashCode()

    override fun equals(other: Any?) =
        other is Music && javaClass == other.javaClass && uid == other.uid

    /**
     * A unique identifier for a piece of music.
     *
     * UID enables a much cheaper and more reliable form of differentiating music, derived from
     * either a hash of meaningful metadata or the MusicBrainz UUID spec. It is the default datatype
     * used when comparing music, and it is also the datatype used when serializing music to
     * external sources, as it can persist across app restarts and does not need to encode useless
     * information about the relationships between items.
     *
     * TODO: MusicBrainz tags
     *
     * @author OxygenCobalt
     */
    @Parcelize
    class UID private constructor(private val tag: String, private val uuid: UUID) : Parcelable {
        // Cache the hashCode for speed
        @IgnoredOnParcel private val hashCode = 31 * tag.hashCode() + uuid.hashCode()

        override fun hashCode() = hashCode

        override fun equals(other: Any?) = other is UID && tag == other.tag && uuid == other.uuid

        override fun toString() = "$tag:$uuid"

        companion object {
            /** Parse a [UID] from the string [uid]. Returns null if not valid. */
            fun fromString(uid: String): UID? {
                val split = uid.split(':', limit = 2)
                if (split.size != 2) {
                    return null
                }

                return UID(tag = split[0], split[1].toUuid() ?: return null)
            }

            /**
             * Make a UUID derived from the MD5 hash of the data digested in [updates].
             *
             * This is Auxio's UID format.
             */
            fun hashed(clazz: KClass<*>, updates: MessageDigest.() -> Unit): UID {
                // Auxio hashes consist of the MD5 hash of the non-subjective, consistent
                // tags in a music item. For easier use with MusicBrainz IDs, we
                val digest = MessageDigest.getInstance("MD5")
                updates(digest)
                val uuid = digest.digest().toUuid()
                val tag = "auxio.${unlikelyToBeNull(clazz.simpleName).lowercase()}"
                return UID(tag, uuid)
            }
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
class Song constructor(raw: Raw) : Music() {
    override val uid: UID

    override val rawName = requireNotNull(raw.name) { "Invalid raw: No title" }

    override val rawSortName = raw.sortName

    override fun resolveName(context: Context) = rawName

    /** The URI pointing towards this audio file. */
    val uri = requireNotNull(raw.mediaStoreId) { "Invalid raw: No id" }.audioUri

    /**
     * The path component of the audio file for this music. Only intended for display. Use [uri] to
     * open the audio file.
     */
    val path =
        Path(
            name = requireNotNull(raw.displayName) { "Invalid raw: No display name" },
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

    /** The track number of this song in it's album.. */
    val track = raw.track

    /** The disc number of this song in it's album. */
    val disc = raw.disc

    private var _album: Album? = null

    /** The album of this song. */
    val album: Album
        get() = unlikelyToBeNull(_album)

    // TODO: Multi-artist support
    // private val _artists: MutableList<Artist> = mutableListOf()

    private val artistName = raw.artistNames?.joinToString()
    private val albumArtistName = raw.albumArtistNames?.joinToString()
    private val artistSortName = raw.artistSortNames?.joinToString()
    private val albumArtistSortName = raw.albumArtistSortNames?.joinToString()

    /**
     * The raw artist name for this song in particular. First uses the artist tag, and then falls
     * back to the album artist tag (i.e parent artist name). Null if name is unknown.
     */
    val individualArtistRawName: String?
        get() = artistName ?: album.artist.rawName

    /**
     * Resolve the artist name for this song in particular. First uses the artist tag, and then
     * falls back to the album artist tag (i.e parent artist name)
     */
    fun resolveIndividualArtistName(context: Context) =
        artistName ?: album.artist.resolveName(context)

    private val _genres: MutableList<Genre> = mutableListOf()

    /**
     * The genres of this song. Most often one, but there could be multiple. There will always be at
     * least one genre, even if it is an "unknown genre" instance.
     */
    val genres: List<Genre>
        get() = _genres

    // --- INTERNAL FIELDS ---

    val _rawAlbum =
        Album.Raw(
            mediaStoreId = requireNotNull(raw.albumMediaStoreId) { "Invalid raw: No album id" },
            name = requireNotNull(raw.albumName) { "Invalid raw: No album name" },
            sortName = raw.albumSortName,
            date = raw.date,
            releaseType = raw.albumReleaseType,
            rawArtist =
            if (albumArtistName != null) {
                Artist.Raw(albumArtistName, albumArtistSortName)
            } else {
                Artist.Raw(artistName, artistSortName)
            }
        )

    val _rawGenres = raw.genreNames?.map { Genre.Raw(it) } ?: listOf(Genre.Raw(null))

    fun _link(album: Album) {
        _album = album
    }

    fun _link(genre: Genre) {
        _genres.add(genre)
    }

    fun _validate() {
        (checkNotNull(_album) { "Malformed song: album is null" })._validate()
        check(_genres.isNotEmpty()) { "Malformed song: genres are empty" }
    }

    init {
        // Generally, we calculate UIDs at the end since everything will definitely be initialized
        // by now.
        uid =
            UID.hashed(this::class) {
                update(rawName.lowercase())
                update(_rawAlbum.name.lowercase())
                update(_rawAlbum.date)

                update(artistName)
                update(albumArtistName)

                update(track)
                update(disc)
            }
    }

    class Raw
    constructor(
        var mediaStoreId: Long? = null,
        var name: String? = null,
        var sortName: String? = null,
        var displayName: String? = null,
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
        var albumName: String? = null,
        var albumSortName: String? = null,
        var albumReleaseType: ReleaseType? = null,
        var artistNames: List<String>? = null,
        var artistSortNames: List<String>? = null,
        var albumArtistNames: List<String>? = null,
        var albumArtistSortNames: List<String>? = null,
        var genreNames: List<String>? = null
    )
}

/**
 * An album.
 * @author OxygenCobalt
 */
class Album constructor(raw: Raw, override val songs: List<Song>) : MusicParent() {
    override val uid: UID

    override val rawName = raw.name

    override val rawSortName = raw.sortName

    override fun resolveName(context: Context) = rawName

    /** The latest date this album was released. */
    val date = raw.date

    /** The release type of this album, such as "EP". Defaults to "Album". */
    val releaseType = raw.releaseType ?: ReleaseType.Album(null)

    /**
     * The album cover URI for this album. Usually low quality, so using Coil is recommended
     * instead.
     */
    val coverUri = raw.mediaStoreId.albumCoverUri

    /** The earliest date a song in this album was added. */
    val dateAdded = songs.minOf { it.dateAdded }

    /** The total duration of songs in this album, in millis. */
    val durationMs = songs.sumOf { it.durationMs }

    private var _artist: Artist? = null

    /** The parent artist of this album. */
    val artist: Artist
        get() = unlikelyToBeNull(_artist)

    // --- INTERNAL FIELDS ---

    val _rawArtist = raw.rawArtist

    fun _link(artist: Artist) {
        _artist = artist
    }

    fun _validate() {
        checkNotNull(_artist) { "Invalid album: Artist is null " }
    }

    init {
        uid =
            UID.hashed(this::class) {
                update(rawName)
                update(_rawArtist.name)
                update(date)
            }

        for (song in songs) {
            song._link(this)
        }
    }

    class Raw(
        val mediaStoreId: Long,
        val name: String,
        val sortName: String?,
        val date: Date?,
        val releaseType: ReleaseType?,
        val rawArtist: Artist.Raw
    ) {
        private val hashCode = 31 * name.lowercase().hashCode() + rawArtist.hashCode()

        override fun hashCode() = hashCode

        override fun equals(other: Any?) =
            other is Raw && name.equals(other.name, true) && rawArtist == other.rawArtist
    }
}

/**
 * An artist. This is derived from the album artist first, and then the normal artist second.
 * @author OxygenCobalt
 */
class Artist
constructor(
    raw: Raw,
    /** The albums of this artist. */
    val albums: List<Album>
) : MusicParent() {
    override val uid: UID

    override val rawName = raw.name

    override val rawSortName = raw.sortName

    override fun resolveName(context: Context) = rawName ?: context.getString(R.string.def_artist)

    override val songs = albums.flatMap { it.songs }

    /** The total duration of songs in this artist, in millis. */
    val durationMs = songs.sumOf { it.durationMs }

    init {
        uid = UID.hashed(this::class) { update(rawName) }

        for (album in albums) {
            album._link(this)
        }
    }

    class Raw(val name: String?, val sortName: String?) {
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

/**
 * A genre.
 * @author OxygenCobalt
 */
class Genre constructor(raw: Raw, override val songs: List<Song>) : MusicParent() {
    override val uid: UID

    override val rawName = raw.name

    // Sort tags don't make sense on genres
    override val rawSortName = rawName

    override fun resolveName(context: Context) = rawName ?: context.getString(R.string.def_genre)

    /** The total duration of the songs in this genre, in millis. */
    val durationMs = songs.sumOf { it.durationMs }

    init {
        uid = UID.hashed(this::class) { update(rawName) }

        for (song in songs) {
            song._link(this)
        }
    }

    class Raw(val name: String?) {
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

/**
 * An ISO-8601/RFC 3339 Date.
 *
 * Unlike a typical Date within the standard library, this class just represents the ID3v2/Vorbis
 * date format, which is largely assumed to be a subset of ISO-8601. No validation outside of format
 * validation is done.
 *
 * The reasoning behind Date is that Auxio cannot trust any kind of metadata date to actually make
 * sense in a calendar, due to bad tagging, locale-specific issues, or simply from the limited
 * nature of tag formats. Thus, it's better to use an analogous data structure that will not mangle
 * or reject valid-ish dates.
 *
 * Date instances are immutable and their implementation is hidden. To instantiate one, use [from].
 * The string representation of a Date is RFC 3339, with granular position depending on the presence
 * of particular tokens.
 *
 * Please, **Do not use this for anything important related to time.** I cannot stress this enough.
 * This code will blow up if you try to do that.
 *
 * @author OxygenCobalt
 */
class Date private constructor(private val tokens: List<Int>) : Comparable<Date> {
    init {
        if (BuildConfig.DEBUG) {
            // Last-ditch sanity check to catch format bugs that might slip through
            check(tokens.size in 1..6) { "There must be 1-6 date tokens" }
            check(tokens.slice(0..min(tokens.lastIndex, 2)).all { it > 0 }) {
                "All date tokens must be non-zero "
            }
            check(tokens.slice(1..tokens.lastIndex).all { it < 100 }) {
                "All non-year tokens must be two digits"
            }
        }
    }

    val year: Int
        get() = tokens[0]

    /** Resolve the year field in a way suitable for the UI. */
    fun resolveYear(context: Context) = context.getString(R.string.fmt_number, year)

    private val month: Int?
        get() = tokens.getOrNull(1)

    private val day: Int?
        get() = tokens.getOrNull(2)

    private val hour: Int?
        get() = tokens.getOrNull(3)

    private val minute: Int?
        get() = tokens.getOrNull(4)

    private val second: Int?
        get() = tokens.getOrNull(5)

    override fun hashCode() = tokens.hashCode()

    override fun equals(other: Any?) = other is Date && tokens == other.tokens

    override fun compareTo(other: Date): Int {
        val comparator = Sort.Mode.NullableComparator.INT

        for (i in 0..(max(tokens.lastIndex, other.tokens.lastIndex))) {
            val result = comparator.compare(tokens.getOrNull(i), other.tokens.getOrNull(i))
            if (result != 0) {
                return result
            }
        }

        return 0
    }

    override fun toString() = StringBuilder().appendDate().toString()

    private fun StringBuilder.appendDate(): StringBuilder {
        append(year.toFixedString(4))
        append("-${(month ?: return this).toFixedString(2)}")
        append("-${(day ?: return this).toFixedString(2)}")
        append("T${(hour ?: return this).toFixedString(2)}")
        append(":${(minute ?: return this.append('Z')).toFixedString(2)}")
        append(":${(second ?: return this.append('Z')).toFixedString(2)}")
        return this.append('Z')
    }

    private fun Int.toFixedString(len: Int) = toString().padStart(len, '0')

    companion object {
        private val ISO8601_REGEX =
            Regex(
                """^(\d{4,})([-.](\d{2})([-.](\d{2})([T ](\d{2})([:.](\d{2})([:.](\d{2}))?)?)?)?)?$"""
            )

        fun from(year: Int) = fromTokens(listOf(year))

        fun from(year: Int, month: Int, day: Int) = fromTokens(listOf(year, month, day))

        fun from(year: Int, month: Int, day: Int, hour: Int, minute: Int) =
            fromTokens(listOf(year, month, day, hour, minute))

        fun from(timestamp: String): Date? {
            val groups =
                (ISO8601_REGEX.matchEntire(timestamp) ?: return null)
                    .groupValues
                    .mapIndexedNotNull { index, s -> if (index % 2 != 0) s.toIntOrNull() else null }

            return fromTokens(groups)
        }

        private fun fromTokens(tokens: List<Int>): Date? {
            val out = mutableListOf<Int>()
            validateTokens(tokens, out)
            if (out.isEmpty()) {
                return null
            }

            return Date(out)
        }

        private fun validateTokens(src: List<Int>, dst: MutableList<Int>) {
            dst.add(src.getOrNull(0)?.nonZeroOrNull() ?: return)
            dst.add(src.getOrNull(1)?.inRangeOrNull(1..12) ?: return)
            dst.add(src.getOrNull(2)?.inRangeOrNull(1..31) ?: return)
            dst.add(src.getOrNull(3)?.inRangeOrNull(0..23) ?: return)
            dst.add(src.getOrNull(4)?.inRangeOrNull(0..59) ?: return)
            dst.add(src.getOrNull(5)?.inRangeOrNull(0..59) ?: return)
        }
    }
}

/**
 * Represents the type of release a particular album is.
 *
 * This can be used to differentiate between album sub-types like Singles, EPs, Compilations, and
 * others. Internally, it operates on a reduced version of the MusicBrainz release type
 * specification. It can be extended if there is demand.
 *
 * @author OxygenCobalt
 */
sealed class ReleaseType {
    abstract val refinement: Refinement?
    abstract val stringRes: Int

    data class Album(override val refinement: Refinement?) : ReleaseType() {
        override val stringRes: Int
            get() =
                when (refinement) {
                    null -> R.string.lbl_album
                    Refinement.LIVE -> R.string.lbl_album_live
                    Refinement.REMIX -> R.string.lbl_album_remix
                }
    }

    data class EP(override val refinement: Refinement?) : ReleaseType() {
        override val stringRes: Int
            get() =
                when (refinement) {
                    null -> R.string.lbl_ep
                    Refinement.LIVE -> R.string.lbl_ep_live
                    Refinement.REMIX -> R.string.lbl_ep_remix
                }
    }

    data class Single(override val refinement: Refinement?) : ReleaseType() {
        override val stringRes: Int
            get() =
                when (refinement) {
                    null -> R.string.lbl_single
                    Refinement.LIVE -> R.string.lbl_single_live
                    Refinement.REMIX -> R.string.lbl_single_remix
                }
    }

    object Compilation : ReleaseType() {
        override val refinement: Refinement?
            get() = null

        override val stringRes: Int
            get() = R.string.lbl_compilation
    }

    object Soundtrack : ReleaseType() {
        override val refinement: Refinement?
            get() = null

        override val stringRes: Int
            get() = R.string.lbl_soundtrack
    }

    object Mixtape : ReleaseType() {
        override val refinement: Refinement?
            get() = null

        override val stringRes: Int
            get() = R.string.lbl_mixtape
    }

    /**
     * Roughly analogous to the MusicBrainz "live" and "remix" secondary types. Unlike the main
     * types, these only modify an existing, primary type. They are not implemented for secondary
     * types, however they may be expanded to compilations in the future.
     */
    enum class Refinement {
        LIVE,
        REMIX
    }

    companion object {
        fun parse(types: List<String>): ReleaseType {
            val primary = types[0]

            // Primary types should be the first one in sequence. The spec makes no mention of
            // whether primary types are a pre-requisite for secondary types, so we assume that
            // it isn't. There are technically two other types, but those are unrelated to music
            // and thus we don't support them.
            return when {
                primary.equals("album", true) -> types.parseSecondaryTypes(1) { Album(it) }
                primary.equals("ep", true) -> types.parseSecondaryTypes(1) { EP(it) }
                primary.equals("single", true) -> types.parseSecondaryTypes(1) { Single(it) }
                else -> types.parseSecondaryTypes(0) { Album(it) }
            }
        }

        private inline fun List<String>.parseSecondaryTypes(
            secondaryIdx: Int,
            target: (Refinement?) -> ReleaseType
        ): ReleaseType {
            val secondary = (getOrNull(secondaryIdx) ?: return target(null))

            return when {
                // Compilation is the only weird secondary release type, as it could
                // theoretically have additional modifiers including soundtrack, remix,
                // live, dj-mix, etc. However, since there is no real demand for me to
                // respond to those, I don't implement them simply for simplicity.
                secondary.equals("compilation", true) -> Compilation
                secondary.equals("soundtrack", true) -> Soundtrack
                secondary.equals("mixtape/street", true) -> Mixtape
                secondary.equals("live", true) -> target(Refinement.LIVE)
                secondary.equals("remix", true) -> target(Refinement.REMIX)
                else -> target(null)
            }
        }
    }
}
