/*
 * Copyright (c) 2021 Auxio Project
 * Models.kt is part of Auxio.
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
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

// --- MUSIC MODELS ---

/**
 * The base data object for all music.
 * @property id A unique ID for this object. ***THIS IS NOT A MEDIASTORE ID!**
 */
sealed class BaseModel {
    abstract val id: Long
}

/**
 * A [BaseModel] variant that represents a music item.
 * @property name The raw name of this track
 */
sealed class Music : BaseModel() {
    abstract val name: String
}

/**
 * [Music] variant that denotes that this object is a parent of other data objects, such
 * as an [Album] or [Artist]
 * @property resolvedName A name resolved from it's raw form to a form suitable to be shown in
 * a ui. Ex. "unknown" would become Unknown Artist, (124) would become its proper genre name, etc.
 */
sealed class MusicParent : Music() {
    abstract val resolvedName: String
}

/**
 * The data object for a song. Inherits [BaseModel].
 */
data class Song(
    override val name: String,
    /** The file name of this song, excluding the full path. */
    val fileName: String,
    /** The total duration of this song, in millis. */
    val duration: Long,
    /** The track number of this song. */
    val track: Int,
    /** Internal field. Do not use. */
    val _mediaStoreId: Long,
    /** Internal field. Do not use. */
    val _mediaStoreArtistName: String?,
    /** Internal field. Do not use. */
    val _mediaStoreAlbumArtistName: String?,
    /** Internal field. Do not use. */
    val _mediaStoreAlbumId: Long,
    /** Internal field. Do not use. */
    val _mediaStoreAlbumName: String,
    /** Internal field. Do not use. */
    val _mediaStoreYear: Int
) : Music() {
    override val id: Long get() {
        var result = name.hashCode().toLong()
        result = 31 * result + album.name.hashCode()
        result = 31 * result + album.artist.name.hashCode()
        result = 31 * result + track
        result = 31 * result + duration.hashCode()
        return result
    }

    /** The URI for this song. */
    val uri: Uri get() = ContentUris.withAppendedId(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, _mediaStoreId
    )
    /** The duration of this song, in seconds (rounded down) */
    val seconds: Long get() = duration / 1000
    /** The seconds of this song, but as a duration. */
    val formattedDuration: String get() = seconds.toDuration(false)

    private var mAlbum: Album? = null
    /** The album of this song. */
    val album: Album get() = requireNotNull(mAlbum)

    var mGenre: Genre? = null
    /** The genre of this song. May be null due to MediaStore insanity. */
    val genre: Genre? get() = mGenre

    /** An album name resolved to this song in particular. */
    val resolvedAlbumName: String get() =
        album.resolvedName

    /** An artist name resolved to this song in particular. */
    val resolvedArtistName: String get() =
        _mediaStoreArtistName ?: album.artist.resolvedName

    /** Internal method. Do not use. */
    fun mediaStoreLinkAlbum(album: Album) {
        mAlbum = album
    }

    /** Internal method. Do not use. */
    fun mediaStoreLinkGenre(genre: Genre) {
        mGenre = genre
    }
}

/**
 * The data object for an album. Inherits [MusicParent].
 */
data class Album(
    override val name: String,
    /** The latest year of the songs in this album. */
    val year: Int,
    /** The URI for the cover art corresponding to this album. */
    val albumCoverUri: Uri,
    /** The songs of this album. */
    val songs: List<Song>,
    /** Internal field. Do not use. */
    val _mediaStoreArtistName: String,
) : MusicParent() {
    init {
        songs.forEach { song ->
            song.mediaStoreLinkAlbum(this)
        }
    }

    override val id: Long get() {
        var result = name.hashCode().toLong()
        result = 31 * result + artist.name.hashCode()
        result = 31 * result + year
        return result
    }

    override val resolvedName: String
        get() = name

    /** The formatted total duration of this album */
    val totalDuration: String get() =
        songs.sumOf { it.seconds }.toDuration(false)

    private var mArtist: Artist? = null
    /** The parent artist of this album. */
    val artist: Artist get() = requireNotNull(mArtist)

    /** The artist name, resolved to this album in particular. */
    val resolvedArtistName: String get() =
        artist.resolvedName

    /** Internal method. Do not use. */
    fun mediaStoreLinkArtist(artist: Artist) {
        mArtist = artist
    }
}

/**
 * The [MusicParent] for an *album* artist. This reflects a group of songs with the same(ish)
 * album artist or artist field, not the individual performers of an artist.
 */
data class Artist(
    override val name: String,
    override val resolvedName: String,
    /** The albums of this artist. */
    val albums: List<Album>
) : MusicParent() {
    init {
        albums.forEach { album ->
            album.mediaStoreLinkArtist(this)
        }
    }

    override val id = name.hashCode().toLong()

    /** The songs of this artist. */
    val songs = albums.flatMap { it.songs }
}

/**
 * The data object for a genre. Inherits [MusicParent]
 */
data class Genre(
    override val name: String,
    override val resolvedName: String,
    /** Internal field. Do not use. */
    val _mediaStoreId: Long
) : MusicParent() {
    override val id = name.hashCode().toLong()

    /** The formatted total duration of this genre */
    val totalDuration: String get() =
        songs.sumOf { it.seconds }.toDuration(false)

    private val mSongs = mutableListOf<Song>()
    val songs: List<Song> get() = mSongs

    /** Internal method. Do not use. */
    fun linkSong(song: Song) {
        mSongs.add(song)
        song.mediaStoreLinkGenre(this)
    }
}

/**
 * A data object used solely for the "Header" UI element.
 */
data class Header(
    override val id: Long,
    /** The string resource used for the header. */
    @StringRes val string: Int
) : BaseModel()

/**
 * A data object used for an action header. Like [Header], but with a button.
 * @see Header
 */
data class ActionHeader(
    override val id: Long,
    /** The string resource used for the header. */
    @StringRes val string: Int,
    /** The icon resource used for the header action. */
    @DrawableRes val icon: Int,
    /** The string resource used for the header action's content description. */
    @StringRes val desc: Int,
    /** A callback for when this item is clicked. */
    val onClick: (View) -> Unit,
) : BaseModel() {
    // All lambdas are not equal to each-other, so we override equals/hashCode and exclude them.

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ActionHeader) return false

        if (id != other.id) return false
        if (string != other.string) return false
        if (icon != other.icon) return false
        if (desc != other.desc) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + string.hashCode()
        result = 31 * result + icon
        result = 31 * result + desc

        return result
    }
}
