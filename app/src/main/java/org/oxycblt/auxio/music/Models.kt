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

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

// --- MUSIC MODELS ---

/**
 * The base data object for all music.
 * @property id The ID that is assigned to this object
 * @property name The name of this object (Such as a song title)
 */
sealed class BaseModel {
    abstract val id: Long
    abstract val name: String
}

/**
 * Provides a versatile static hash for a music item that will not change when
 * MediaStore changes.
 *
 * The reason why this is used is down a couple of reasons:
 * - MediaStore will refresh the unique ID of a piece of media whenever the library
 * changes, which creates bad UX
 * - Using song names makes collisions too common to be reliable
 * - Hashing into an integer makes databases both smaller and more efficent
 *
 * This does lock me into a "Load everything at once, lol" architecture for Auxio, but I
 * think its worth it.
 *
 * @property hash A unique-ish hash for this media item
 *
 * TODO: Make this hash stronger
 */
sealed interface Hashable {
    val hash: Int
}

/**
 * [BaseModel] variant that denotes that this object is a parent of other data objects, such
 * as an [Album] or [Artist]
 * @property displayName Name that handles the usage of [Genre.resolvedName]
 * and the normal [BaseModel.name]
 */
sealed class Parent : BaseModel(), Hashable {
    val displayName: String get() = if (this is Genre) {
        resolvedName
    } else {
        name
    }
}

/**
 * The data object for a song. Inherits [BaseModel].
 * @property fileName The raw filename for this track
 * @property albumId  The Song's Album ID.
 * Never use this outside of when attaching a song to its album.
 * @property track    The Song's Track number
 * @property duration The duration of the song, in millis.
 * @property album    The Song's parent album. Use this instead of [albumId].
 * @property genre    The Song's [Genre].
 * These are not ensured to be linked due to possible quirks in the genre loading system.
 * @property seconds  The Song's duration in seconds
 * @property formattedDuration The Song's duration as a duration string.
 */
data class Song(
    override val id: Long,
    override val name: String,
    val fileName: String,
    val albumName: String,
    val albumId: Long,
    val artistName: String,
    val year: Int,
    val track: Int,
    val duration: Long
) : BaseModel(), Hashable {
    private var mAlbum: Album? = null
    private var mGenre: Genre? = null

    val genre: Genre? get() = mGenre
    val album: Album get() = requireNotNull(mAlbum)

    val seconds: Long get() = duration / 1000
    val formattedDuration: String get() = (duration / 1000).toDuration()

    override val hash: Int get() {
        var result = name.hashCode()
        result = 31 * result + track
        result = 31 * result + duration.hashCode()
        return result
    }

    fun linkAlbum(album: Album) {
        mAlbum = album
    }

    fun linkGenre(genre: Genre) {
        mGenre = genre
    }
}

/**
 * The data object for an album. Inherits [Parent].
 * @property artistName    The name of the parent artist. Do not use this outside of creating the artist from albums
 * @property year          The year this album was released. 0 if there is none in the metadata.
 * @property artist        The Album's parent [Artist]. use this instead of [artistName]
 * @property songs         The Album's child [Song]s.
 * @property totalDuration The combined duration of all of the album's child songs, formatted.
 */
data class Album(
    override val id: Long,
    override val name: String,
    val artistName: String,
    val year: Int,
    val songs: List<Song>
) : Parent() {
    init {
        songs.forEach { song ->
            song.linkAlbum(this)
        }
    }

    private var mArtist: Artist? = null
    val artist: Artist get() = requireNotNull(mArtist)

    val totalDuration: String get() =
        songs.sumOf { it.seconds }.toDuration()

    override val hash: Int get() {
        var result = name.hashCode()
        result = 31 * result + artistName.hashCode()
        result = 31 * result + year
        return result
    }

    fun linkArtist(artist: Artist) {
        mArtist = artist
    }
}

/**
 * The data object for an artist. Inherits [Parent]
 * @property albums The list of all [Album]s in this artist
 * @property genre  The most prominent genre for this artist
 * @property songs  The list of all [Song]s in this artist
 */
data class Artist(
    override val id: Long,
    override val name: String,
    val albums: List<Album>
) : Parent() {
    init {
        albums.forEach { album ->
            album.linkArtist(this)
        }
    }

    val genre: Genre? by lazy {
        // Get the genre that corresponds to the most songs in this artist, which would be
        // the most "Prominent" genre.
        songs.groupBy { it.genre }.entries.maxByOrNull { it.value.size }?.key
    }

    val songs: List<Song> by lazy {
        albums.flatMap { it.songs }
    }

    override val hash = name.hashCode()
}

/**
 * The data object for a genre. Inherits [Parent]
 * @property songs   The list of all [Song]s in this genre.
 * @property resolvedName A name that has been resolved from its int-genre form to its named form.
 */
data class Genre(
    override val id: Long,
    override val name: String,
) : Parent() {
    private val mSongs = mutableListOf<Song>()
    val songs: List<Song> get() = mSongs

    val resolvedName =
        name.getGenreNameCompat() ?: name

    val totalDuration: String get() =
        songs.sumOf { it.seconds }.toDuration()

    override val hash = name.hashCode()

    fun linkSong(song: Song) {
        mSongs.add(song)
        song.linkGenre(this)
    }
}

/**
 * A data object used solely for the "Header" UI element.
 */
data class Header(
    override val id: Long,
    override val name: String,
) : BaseModel()

/**
 * A data object used for an action header. Like [Header], but with a button.
 * Inherits [BaseModel].
 */
data class ActionHeader(
    override val id: Long,
    override val name: String,
    @DrawableRes val icon: Int,
    @StringRes val desc: Int,
    val onClick: (View) -> Unit,
) : BaseModel() {
    // JVM can't into comparing lambdas, so we override equals/hashCode and exclude them.

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ActionHeader) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (icon != other.icon) return false
        if (desc != other.desc) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + icon
        result = 31 * result + desc

        return result
    }
}
