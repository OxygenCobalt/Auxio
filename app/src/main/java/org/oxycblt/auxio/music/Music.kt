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

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.text.isDigitsOnly
import org.oxycblt.auxio.R
import org.oxycblt.auxio.ui.Item
import org.oxycblt.auxio.util.unlikelyToBeNull

// --- MUSIC MODELS ---

/** [Item] variant that represents a music item. */
sealed class Music : Item() {
    /** The raw name of this item. Null if unknown. */
    abstract val rawName: String?

    /** The name of this item used for sorting. Null if unknown. */
    abstract val sortName: String?

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
    /** The file name of this song, excluding the full path. */
    val fileName: String,
    /** The total duration of this song, in millis. */
    val durationMs: Long,
    /** The track number of this song, null if there isn't any. */
    val track: Int?,
    /** The disc number of this song, null if there isn't any. */
    val disc: Int?,
    /** Internal field. Do not use. */
    val _mediaStoreId: Long,
    /** Internal field. Do not use. */
    val _mediaStoreYear: Int?,
    /** Internal field. Do not use. */
    val _mediaStoreAlbumName: String,
    /** Internal field. Do not use. */
    val _mediaStoreAlbumId: Long,
    /** Internal field. Do not use. */
    val _mediaStoreArtistName: String?,
    /** Internal field. Do not use. */
    val _mediaStoreAlbumArtistName: String?,
) : Music() {
    override val id: Long
        get() {
            var result = rawName.hashCode().toLong()
            result = 31 * result + album.rawName.hashCode()
            result = 31 * result + album.artist.rawName.hashCode()
            result = 31 * result + (track ?: 0)
            // TODO: Rework hashing to add discs and handle null values correctly
            result = 31 * result + durationMs.hashCode()
            return result
        }

    override val sortName: String
        get() = rawName.withoutArticle

    override fun resolveName(context: Context) = rawName

    /** The URI for this song. */
    val uri: Uri
        get() =
            ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, _mediaStoreId)
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
    val individualRawArtistName: String?
        get() = _mediaStoreArtistName ?: album.artist.rawName

    /**
     * Resolve the artist name for this song in particular. First uses the artist tag, and then
     * falls back to the album artist tag (i.e parent artist name)
     */
    fun resolveIndividualArtistName(context: Context) =
        _mediaStoreArtistName ?: album.artist.resolveName(context)

    /** Internal field. Do not use. */
    val _albumGroupingId: Long
        get() {
            var result = _artistGroupingName.lowercase().hashCode().toLong()
            result = 31 * result + _mediaStoreAlbumName.lowercase().hashCode()
            return result
        }

    /** Internal field. Do not use. */
    val _artistGroupingName: String
        get() = _mediaStoreAlbumArtistName ?: _mediaStoreArtistName ?: MediaStore.UNKNOWN_STRING

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
    /** The latest year of the songs in this album. Null if none of the songs had metadata. */
    val year: Int?,
    /** The URI for the cover art corresponding to this album. */
    val albumCoverUri: Uri,
    /** The songs of this album. */
    override val songs: List<Song>,
    /** Internal field. Do not use. */
    val _artistGroupingName: String,
) : MusicParent() {
    init {
        for (song in songs) {
            song._link(this)
        }
    }

    override val id: Long
        get() {
            var result = rawName.hashCode().toLong()
            result = 31 * result + artist.rawName.hashCode()
            result = 31 * result + (year ?: 0)
            return result
        }

    override val sortName: String
        get() = rawName.withoutArticle

    override fun resolveName(context: Context) = rawName

    private var _artist: Artist? = null
    /** The parent artist of this album. */
    val artist: Artist
        get() = unlikelyToBeNull(_artist)

    /** Internal field. Do not use. */
    val _artistGroupingId: Long
        get() = _artistGroupingName.lowercase().hashCode().toLong()

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
    /** The albums of this artist. */
    val albums: List<Album>
) : MusicParent() {
    init {
        for (album in albums) {
            album._link(this)
        }
    }

    override val id: Long
        get() = (rawName ?: MediaStore.UNKNOWN_STRING).hashCode().toLong()

    override val sortName: String?
        get() = rawName?.withoutArticle

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

    override val id: Long
        get() = (rawName ?: MediaStore.UNKNOWN_STRING).hashCode().toLong()

    override val sortName: String?
        get() = rawName?.genreNameCompat

    override fun resolveName(context: Context) =
        rawName?.genreNameCompat ?: context.getString(R.string.def_genre)
}

/**
 * Slice a string so that any preceding articles like The/A(n) are truncated. This is hilariously
 * anglo-centric, but its mostly for MediaStore compat and hopefully shouldn't run with other
 * languages.
 */
private val String.withoutArticle: String
    get() {
        if (length > 5 && startsWith("the ", ignoreCase = true)) {
            return slice(4..lastIndex)
        }

        if (length > 4 && startsWith("an ", ignoreCase = true)) {
            return slice(3..lastIndex)
        }

        if (length > 3 && startsWith("a ", ignoreCase = true)) {
            return slice(2..lastIndex)
        }

        return this
    }

/**
 * Decodes the genre name from an ID3(v2) constant. See [genreConstantTable] for the genre constant
 * map that Auxio uses.
 */
private val String.genreNameCompat: String
    get() {
        if (isDigitsOnly()) {
            // ID3v1, just parse as an integer
            return genreConstantTable.getOrNull(toInt()) ?: this
        }

        if (startsWith('(') && endsWith(')')) {
            // ID3v2.3/ID3v2.4, parse out the parentheses and get the integer
            // Any genres formatted as "(CHARS)" will be ignored.
            val genreInt = substring(1 until lastIndex).toIntOrNull()
            if (genreInt != null) {
                return genreConstantTable.getOrNull(genreInt) ?: this
            }
        }

        // Current name is fine.
        return this
    }

/**
 * A complete table of all the constant genre values for ID3(v2), including non-standard extensions.
 */
private val genreConstantTable =
    arrayOf(
        // ID3 Standard
        "Blues",
        "Classic Rock",
        "Country",
        "Dance",
        "Disco",
        "Funk",
        "Grunge",
        "Hip-Hop",
        "Jazz",
        "Metal",
        "New Age",
        "Oldies",
        "Other",
        "Pop",
        "R&B",
        "Rap",
        "Reggae",
        "Rock",
        "Techno",
        "Industrial",
        "Alternative",
        "Ska",
        "Death Metal",
        "Pranks",
        "Soundtrack",
        "Euro-Techno",
        "Ambient",
        "Trip-Hop",
        "Vocal",
        "Jazz+Funk",
        "Fusion",
        "Trance",
        "Classical",
        "Instrumental",
        "Acid",
        "House",
        "Game",
        "Sound Clip",
        "Gospel",
        "Noise",
        "AlternRock",
        "Bass",
        "Soul",
        "Punk",
        "Space",
        "Meditative",
        "Instrumental Pop",
        "Instrumental Rock",
        "Ethnic",
        "Gothic",
        "Darkwave",
        "Techno-Industrial",
        "Electronic",
        "Pop-Folk",
        "Eurodance",
        "Dream",
        "Southern Rock",
        "Comedy",
        "Cult",
        "Gangsta",
        "Top 40",
        "Christian Rap",
        "Pop/Funk",
        "Jungle",
        "Native American",
        "Cabaret",
        "New Wave",
        "Psychadelic",
        "Rave",
        "Showtunes",
        "Trailer",
        "Lo-Fi",
        "Tribal",
        "Acid Punk",
        "Acid Jazz",
        "Polka",
        "Retro",
        "Musical",
        "Rock & Roll",
        "Hard Rock",

        // Winamp extensions, more or less a de-facto standard
        "Folk",
        "Folk-Rock",
        "National Folk",
        "Swing",
        "Fast Fusion",
        "Bebob",
        "Latin",
        "Revival",
        "Celtic",
        "Bluegrass",
        "Avantgarde",
        "Gothic Rock",
        "Progressive Rock",
        "Psychedelic Rock",
        "Symphonic Rock",
        "Slow Rock",
        "Big Band",
        "Chorus",
        "Easy Listening",
        "Acoustic",
        "Humour",
        "Speech",
        "Chanson",
        "Opera",
        "Chamber Music",
        "Sonata",
        "Symphony",
        "Booty Bass",
        "Primus",
        "Porn Groove",
        "Satire",
        "Slow Jam",
        "Club",
        "Tango",
        "Samba",
        "Folklore",
        "Ballad",
        "Power Ballad",
        "Rhythmic Soul",
        "Freestyle",
        "Duet",
        "Punk Rock",
        "Drum Solo",
        "A capella",
        "Euro-House",
        "Dance Hall",
        "Goa",
        "Drum & Bass",
        "Club-House",
        "Hardcore",
        "Terror",
        "Indie",
        "Britpop",
        "Negerpunk",
        "Polsk Punk",
        "Beat",
        "Christian Gangsta",
        "Heavy Metal",
        "Black Metal",
        "Crossover",
        "Contemporary Christian",
        "Christian Rock",
        "Merengue",
        "Salsa",
        "Thrash Metal",
        "Anime",
        "JPop",
        "Synthpop",

        // Winamp 5.6+ extensions, also used by EasyTAG.
        // I only include this because post-rock is a based genre and deserves a slot.
        "Abstract",
        "Art Rock",
        "Baroque",
        "Bhangra",
        "Big Beat",
        "Breakbeat",
        "Chillout",
        "Downtempo",
        "Dub",
        "EBM",
        "Eclectic",
        "Electro",
        "Electroclash",
        "Emo",
        "Experimental",
        "Garage",
        "Global",
        "IDM",
        "Illbient",
        "Industro-Goth",
        "Jam Band",
        "Krautrock",
        "Leftfield",
        "Lounge",
        "Math Rock",
        "New Romantic",
        "Nu-Breakz",
        "Post-Punk",
        "Post-Rock",
        "Psytrance",
        "Shoegaze",
        "Space Rock",
        "Trop Rock",
        "World Music",
        "Neoclassical",
        "Audiobook",
        "Audio Theatre",
        "Neue Deutsche Welle",
        "Podcast",
        "Indie Rock",
        "G-Funk",
        "Dubstep",
        "Garage Rock",
        "Psybient")
