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
 */
sealed class BaseModel {
    abstract val id: Long
}

/**
 * A [BaseModel] variant that represents a music item.
 * @property name The raw name of this track
 * @property hash A stable, unique-ish hash for this item. Used for database work.
 */
sealed class Music : BaseModel() {
    abstract val name: String
    abstract val hash: Long
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
) : Music() {
    private var mAlbum: Album? = null
    private var mGenre: Genre? = null

    val genre: Genre? get() = mGenre
    val album: Album get() = requireNotNull(mAlbum)

    val seconds: Long get() = duration / 1000
    val formattedDuration: String get() = seconds.toDuration(false)

    override val hash: Long get() {
        var result = name.hashCode().toLong()
        result = 31 * result + albumName.hashCode()
        result = 31 * result + artistName.hashCode()
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
 * The data object for an album. Inherits [MusicParent].
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
) : MusicParent() {
    init {
        songs.forEach { song ->
            song.linkAlbum(this)
        }
    }

    private var mArtist: Artist? = null
    val artist: Artist get() = requireNotNull(mArtist)

    val totalDuration: String get() =
        songs.sumOf { it.seconds }.toDuration(false)

    fun linkArtist(artist: Artist) {
        mArtist = artist
    }

    override val hash: Long get() {
        var result = name.hashCode().toLong()
        result = 31 * result + artistName.hashCode()
        result = 31 * result + year
        return result
    }

    override val resolvedName: String
        get() = name
}

/**
 * The data object for an artist. Inherits [MusicParent]
 * @property albums The list of all [Album]s in this artist
 * @property genre  The most prominent genre for this artist
 * @property songs  The list of all [Song]s in this artist
 */
data class Artist(
    override val id: Long,
    override val name: String,
    override val resolvedName: String,
    val albums: List<Album>
) : MusicParent() {
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

    override val hash = name.hashCode().toLong()
}

/**
 * The data object for a genre. Inherits [MusicParent]
 * @property songs   The list of all [Song]s in this genre.
 */
data class Genre(
    override val id: Long,
    override val name: String,
    override val resolvedName: String
) : MusicParent() {
    private val mSongs = mutableListOf<Song>()
    val songs: List<Song> get() = mSongs

    val totalDuration: String get() =
        songs.sumOf { it.seconds }.toDuration(false)

    fun linkSong(song: Song) {
        mSongs.add(song)
        song.linkGenre(this)
    }

    override val hash = name.hashCode().toLong()
}

/**
 * A data object used solely for the "Header" UI element.
 */
data class Header(
    override val id: Long,
    @StringRes val string: Int
) : BaseModel()

/**
 * A data object used for an action header. Like [Header], but with a button.
 * @see Header
 */
data class ActionHeader(
    override val id: Long,
    @StringRes val string: Int,
    @DrawableRes val icon: Int,
    @StringRes val desc: Int,
    val onClick: (View) -> Unit,
) : BaseModel() {
    // JVM can't into comparing lambdas, so we override equals/hashCode and exclude them.

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
