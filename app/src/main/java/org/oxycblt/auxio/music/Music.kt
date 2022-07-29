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
import android.net.Uri
import kotlin.math.max
import kotlin.math.min
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.util.inRangeOrNull
import org.oxycblt.auxio.util.nonZeroOrNull
import org.oxycblt.auxio.util.unlikelyToBeNull

// --- MUSIC MODELS ---

/** [Item] variant that represents a music item. */
sealed class Music : Item() {
    /** The raw name of this item. Null if unknown. */
    abstract val rawName: String?

    /** The raw sorting name of this item. Null if not present. */
    abstract val rawSortName: String?

    /**
     * The name of this item used for sorting.This should not be used outside of sorting and
     * fast-scrolling.
     */
    val sortName: String?
        get() = rawSortName ?: rawName?.parseSortName()

    /**
     * Resolve a name from it's raw form to a form suitable to be shown in a ui. Ex. "unknown" would
     * become Unknown Artist, (124) would become its proper genre name, etc.
     */
    abstract fun resolveName(context: Context): String
}

/**
 * [Music] variant that denotes that this object is a parent of other data objects, such as an
 * [Album] or [Artist]
 */
sealed class MusicParent : Music() {
    /** The songs that this parent owns. */
    abstract val songs: List<Song>

    /** The formatted total duration of this genre */
    val durationSecs: Long
        get() = songs.sumOf { it.durationSecs }
}

/** The data object for a song. */
data class Song(
    override val rawName: String,
    override val rawSortName: String?,
    /** The path of this song. */
    val path: Path,
    /** The URI linking to this song's file. */
    val uri: Uri,
    /** The mime type of this song. */
    val mimeType: MimeType,
    /** The size of this song (in bytes) */
    val size: Long,
    /** The datetime at which this media item was added, represented as a unix timestamp. */
    val dateAdded: Long,
    /** The total duration of this song, in millis. */
    val durationMs: Long,
    /** The track number of this song, null if there isn't any. */
    val track: Int?,
    /** The disc number of this song, null if there isn't any. */
    val disc: Int?,
    /** Internal field. Do not use. */
    val _date: Date?,
    /** Internal field. Do not use. */
    val _albumName: String,
    /** Internal field. Do not use. */
    val _albumSortName: String?,
    /** Internal field. Do not use. */
    val _albumReleaseType: ReleaseType?,
    /** Internal field. Do not use. */
    val _albumCoverUri: Uri,
    /** Internal field. Do not use. */
    val _artistName: String?,
    /** Internal field. Do not use. */
    val _artistSortName: String?,
    /** Internal field. Do not use. */
    val _albumArtistName: String?,
    /** Internal field. Do not use. */
    val _albumArtistSortName: String?,
    /** Internal field. Do not use. */
    val _genreName: String?
) : Music() {
    override val id: Long
        get() {
            var result = rawName.toMusicId()
            result = 31 * result + album.rawName.toMusicId()
            result = 31 * result + album.artist.rawName.toMusicId()
            result = 31 * result + (track ?: 0)
            result = 31 * result + (disc ?: 0)
            result = 31 * result + durationMs
            return result
        }

    override fun resolveName(context: Context) = rawName

    /** The duration of this song, in seconds (rounded down) */
    val durationSecs: Long
        get() = durationMs / 1000

    private var _album: Album? = null
    /** The album of this song. */
    val album: Album
        get() = unlikelyToBeNull(_album)

    private var _genre: Genre? = null
    /** The genre of this song. Will be an "unknown genre" if the song does not have any. */
    val genre: Genre
        get() = unlikelyToBeNull(_genre)

    /**
     * The raw artist name for this song in particular. First uses the artist tag, and then falls
     * back to the album artist tag (i.e parent artist name). Null if name is unknown.
     */
    val individualArtistRawName: String?
        get() = _artistName ?: album.artist.rawName

    /**
     * Resolve the artist name for this song in particular. First uses the artist tag, and then
     * falls back to the album artist tag (i.e parent artist name)
     */
    fun resolveIndividualArtistName(context: Context) =
        _artistName ?: album.artist.resolveName(context)

    /** Internal field. Do not use. */
    val _albumGroupingId: Long
        get() {
            var result = _artistGroupingName.toMusicId()
            result = 31 * result + _albumName.toMusicId()
            return result
        }

    /** Internal field. Do not use. */
    val _genreGroupingId: Long
        get() = _genreName.toMusicId()

    /** Internal field. Do not use. */
    val _artistGroupingName: String?
        get() = _albumArtistName ?: _artistName

    /** Internal field. Do not use. */
    val _artistGroupingSortName: String?
        get() =
            when {
                _albumArtistName != null -> _albumArtistSortName
                _artistName != null -> _artistSortName
                else -> null
            }

    /** Internal field. Do not use. */
    val _isMissingAlbum: Boolean
        get() = _album == null
    /** Internal field. Do not use. */
    val _isMissingArtist: Boolean
        get() = _album?._isMissingArtist ?: true
    /** Internal field. Do not use. */
    val _isMissingGenre: Boolean
        get() = _genre == null

    /** Internal method. Do not use. */
    fun _link(album: Album) {
        _album = album
    }

    /** Internal method. Do not use. */
    fun _link(genre: Genre) {
        _genre = genre
    }
}

/** The data object for an album. */
data class Album(
    override val rawName: String,
    override val rawSortName: String?,
    /** The date this album was released. */
    val date: Date?,
    /**
     * The type of release this album represents. Null if release types were not applicable to this
     * library.
     */
    val releaseType: ReleaseType?,
    /** The URI for the cover image corresponding to this album. */
    val coverUri: Uri,
    /** The songs of this album. */
    override val songs: List<Song>,
    /** Internal field. Do not use. */
    val _artistGroupingName: String?,
    /** Internal field. Do not use. */
    val _artistGroupingSortName: String?
) : MusicParent() {
    init {
        for (song in songs) {
            song._link(this)
        }
    }

    override val id: Long
        get() {
            var result = rawName.toMusicId()
            result = 31 * result + artist.rawName.toMusicId()
            result = 31 * result + (date?.year ?: 0)
            return result
        }

    override fun resolveName(context: Context) = rawName

    private var _artist: Artist? = null
    /** The parent artist of this album. */
    val artist: Artist
        get() = unlikelyToBeNull(_artist)

    /** Internal field. Do not use. */
    val _artistGroupingId: Long
        get() = _artistGroupingName.toMusicId()

    /** Internal field. Do not use. */
    val _isMissingArtist: Boolean
        get() = _artist == null

    /** Internal method. Do not use. */
    fun _link(artist: Artist) {
        _artist = artist
    }
}

/**
 * The [MusicParent] for an *album* artist. This reflects a group of songs with the same(ish) album
 * artist or artist field, not the individual performers of an artist.
 */
data class Artist(
    override val rawName: String?,
    override val rawSortName: String?,
    /** The albums of this artist. */
    val albums: List<Album>
) : MusicParent() {
    init {
        for (album in albums) {
            album._link(this)
        }
    }

    override val id: Long
        get() = rawName.toMusicId()

    override fun resolveName(context: Context) = rawName ?: context.getString(R.string.def_artist)

    /** The songs of this artist. */
    override val songs = albums.flatMap { it.songs }
}

/** The data object for a genre. */
data class Genre(override val rawName: String?, override val songs: List<Song>) : MusicParent() {
    init {
        for (song in songs) {
            song._link(this)
        }
    }

    // Sort tags don't make sense on genres
    override val rawSortName: String?
        get() = rawName

    override val id: Long
        get() = rawName.toMusicId()

    override fun resolveName(context: Context) = rawName ?: context.getString(R.string.def_genre)
}

private fun String?.toMusicId(): Long {
    if (this == null) {
        // Pre-calculated hash of MediaStore.UNKNOWN_STRING
        return 54493231833456
    }

    var result = 0L
    for (ch in lowercase()) {
        result = 31 * result + ch.code
    }
    return result
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
 * Date instances are immutable and their internal implementation is hidden. To instantiate one, use
 * [from]. The string representation of a Date is RFC 3339, with granular position depending on the
 * presence of particular tokens.
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
                """^(\d{4,})([-.](\d{2})([-.](\d{2})([T ](\d{2})([:.](\d{2})([:.](\d{2}))?)?)?)?)?$""")

        fun from(year: Int) = fromTokens(listOf(year))

        fun from(year: Int, month: Int, day: Int) = fromTokens(listOf(year, month, day))

        fun from(year: Int, month: Int, day: Int, hour: Int, minute: Int) =
            fromTokens(listOf(year, month, day, hour, minute))

        fun from(timestamp: String): Date? {
            val groups =
                (ISO8601_REGEX.matchEntire(timestamp) ?: return null)
                    .groupValues.mapIndexedNotNull { index, s ->
                        if (index % 2 != 0) s.toIntOrNull() else null
                    }

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

    enum class Refinement {
        LIVE,
        REMIX
    }

    companion object {
        fun parse(type: String) = parse(type.split('+'))

        fun parse(types: List<String>): ReleaseType {
            val primary = types[0].trim()

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
            val secondary = (getOrNull(secondaryIdx) ?: return target(null)).trim()

            return when {
                // Compilation is the only weird secondary release type, as it could
                // theoretically have additional modifiers including soundtrack, remix,
                // live, dj-mix, etc. However, since there is no real demand for me to
                // respond to those, I don't implement them simply for internal simplicity.
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
