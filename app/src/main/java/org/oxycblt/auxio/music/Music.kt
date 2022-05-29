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
import android.provider.MediaStore
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.indexer.id3v2GenreName
import org.oxycblt.auxio.music.indexer.withoutArticle
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
    /** The URI linking to this song's file. */
    val uri: Uri,
    /** The total duration of this song, in millis. */
    val durationMs: Long,
    /** The track number of this song, null if there isn't any. */
    val track: Int?,
    /** The disc number of this song, null if there isn't any. */
    val disc: Int?,
    /** Internal field. Do not use. */
    val _year: Int?,
    /** Internal field. Do not use. */
    val _albumName: String,
    /** Internal field. Do not use. */
    val _albumCoverUri: Uri,
    /** Internal field. Do not use. */
    val _artistName: String?,
    /** Internal field. Do not use. */
    val _albumArtistName: String?,
    /** Internal field. Do not use. */
    val _genreName: String?
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
            var result =
                (_artistGroupingName?.lowercase() ?: MediaStore.UNKNOWN_STRING).hashCode().toLong()
            result = 31 * result + _albumName.lowercase().hashCode()
            return result
        }

    /** Internal field. Do not use. */
    val _artistGroupingName: String?
        get() = _albumArtistName ?: _artistName

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
    val _artistGroupingName: String?,
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
        get() = (_artistGroupingName?.lowercase() ?: MediaStore.UNKNOWN_STRING).hashCode().toLong()

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
        get() = rawName?.id3v2GenreName

    override fun resolveName(context: Context) =
        rawName?.id3v2GenreName ?: context.getString(R.string.def_genre)
}
