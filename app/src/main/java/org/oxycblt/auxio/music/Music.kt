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
 
package org.oxycblt.auxio.music

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import org.oxycblt.auxio.ui.Item

// --- MUSIC MODELS ---

/** [Item] variant that represents a music item. */
sealed class Music : Item() {
    /** The raw name of this item. */
    abstract val rawName: String
    /**
     * A name resolved from it's raw form to a form suitable to be shown in a ui. Ex. "unknown"
     * would become Unknown Artist, (124) would become its proper genre name, etc.
     */
    abstract val resolvedName: String
}

/**
 * [Music] variant that denotes that this object is a parent of other data objects, such as an
 * [Album] or [Artist]
 */
sealed class MusicParent : Music()

/** The data object for a song. */
data class Song(
    override val rawName: String,
    /** The file name of this song, excluding the full path. */
    val fileName: String,
    /** The total duration of this song, in millis. */
    val duration: Long,
    /** The track number of this song, null if there isn't any. */
    val track: Int?,
    /** Internal field. Do not use. */
    val internalMediaStoreId: Long,
    /** Internal field. Do not use. */
    val internalMediaStoreYear: Int?,
    /** Internal field. Do not use. */
    val internalMediaStoreAlbumName: String,
    /** Internal field. Do not use. */
    val internalMediaStoreAlbumId: Long,
    /** Internal field. Do not use. */
    val internalMediaStoreArtistName: String?,
    /** Internal field. Do not use. */
    val internalMediaStoreAlbumArtistName: String?,
) : Music() {
    override val id: Long
        get() {
            var result = rawName.hashCode().toLong()
            result = 31 * result + album.rawName.hashCode()
            result = 31 * result + album.artist.rawName.hashCode()
            result = 31 * result + (track ?: 0)
            result = 31 * result + duration.hashCode()
            return result
        }

    override val resolvedName: String
        get() = rawName

    /** The URI for this song. */
    val uri: Uri
        get() =
            ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, internalMediaStoreId)
    /** The duration of this song, in seconds (rounded down) */
    val seconds: Long
        get() = duration / 1000
    /** The seconds of this song, but as a duration. */
    val formattedDuration: String
        get() = seconds.toDuration(false)

    private var mAlbum: Album? = null
    /** The album of this song. */
    val album: Album
        get() = requireNotNull(mAlbum)

    private var mGenre: Genre? = null
    /** The genre of this song. Will be an "unknown genre" if the song does not have any. */
    val genre: Genre
        get() = requireNotNull(mGenre)

    /** An album name resolved to this song in particular. */
    val resolvedAlbumName: String
        get() = album.resolvedName

    /** An artist name resolved to this song in particular. */
    val resolvedArtistName: String
        get() = internalMediaStoreArtistName ?: album.artist.resolvedName

    /** Internal field. Do not use. */
    val internalAlbumGroupingId: Long
        get() {
            var result = internalGroupingArtistName.lowercase().hashCode().toLong()
            result = 31 * result + internalMediaStoreAlbumName.lowercase().hashCode()
            return result
        }

    /** Internal field. Do not use. */
    val internalGroupingArtistName: String
        get() =
            internalMediaStoreAlbumArtistName
                ?: internalMediaStoreArtistName ?: MediaStore.UNKNOWN_STRING

    /** Internal field. Do not use. */
    val internalIsMissingAlbum: Boolean
        get() = mAlbum == null
    /** Internal field. Do not use. */
    val internalIsMissingArtist: Boolean
        get() = mAlbum?.internalIsMissingArtist ?: true
    /** Internal field. Do not use. */
    val internalIsMissingGenre: Boolean
        get() = mGenre == null

    /** Internal method. Do not use. */
    fun internalLinkAlbum(album: Album) {
        mAlbum = album
    }

    /** Internal method. Do not use. */
    fun internalLinkGenre(genre: Genre) {
        mGenre = genre
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
    val songs: List<Song>,
    /** Internal field. Do not use. */
    val internalGroupingArtistName: String,
) : MusicParent() {
    init {
        for (song in songs) {
            song.internalLinkAlbum(this)
        }
    }

    override val id: Long
        get() {
            var result = rawName.hashCode().toLong()
            result = 31 * result + artist.rawName.hashCode()
            result = 31 * result + (year ?: 0)
            return result
        }

    override val resolvedName: String
        get() = rawName

    /** The formatted total duration of this album */
    val totalDuration: String
        get() = songs.sumOf { it.seconds }.toDuration(false)

    private var mArtist: Artist? = null
    /** The parent artist of this album. */
    val artist: Artist
        get() = requireNotNull(mArtist)

    /** The artist name, resolved to this album in particular. */
    val resolvedArtistName: String
        get() = artist.resolvedName

    /** Internal field. Do not use. */
    val internalArtistGroupingId: Long
        get() = internalGroupingArtistName.lowercase().hashCode().toLong()

    /** Internal field. Do not use. */
    val internalIsMissingArtist: Boolean
        get() = mArtist == null

    /** Internal method. Do not use. */
    fun internalLinkArtist(artist: Artist) {
        mArtist = artist
    }
}

/**
 * The [MusicParent] for an *album* artist. This reflects a group of songs with the same(ish) album
 * artist or artist field, not the individual performers of an artist.
 */
data class Artist(
    override val rawName: String,
    override val resolvedName: String,
    /** The albums of this artist. */
    val albums: List<Album>
) : MusicParent() {
    init {
        for (album in albums) {
            album.internalLinkArtist(this)
        }
    }

    override val id = rawName.hashCode().toLong()

    /** The songs of this artist. */
    val songs = albums.flatMap { it.songs }
}

/** The data object for a genre. */
data class Genre(
    override val rawName: String,
    override val resolvedName: String,
    val songs: List<Song>
) : MusicParent() {
    init {
        for (song in songs) {
            song.internalLinkGenre(this)
        }
    }

    override val id = rawName.hashCode().toLong()

    /** The formatted total duration of this genre */
    val totalDuration: String
        get() = songs.sumOf { it.seconds }.toDuration(false)
}
