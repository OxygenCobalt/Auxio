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

import android.content.Context
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

sealed class Music : BaseModel() {
    abstract val name: String
    abstract val hash: Int
}

/**
 * [BaseModel] variant that denotes that this object is a parent of other data objects, such
 * as an [Album] or [Artist]
 */
sealed class Parent : Music() {
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

    fun linkArtist(artist: Artist) {
        mArtist = artist
    }

    override val hash: Int get() {
        var result = name.hashCode()
        result = 31 * result + artistName.hashCode()
        result = 31 * result + year
        return result
    }

    override val resolvedName: String
        get() = name
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
    override val resolvedName: String,
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
 */
data class Genre(
    override val id: Long,
    override val name: String,
    override val resolvedName: String
) : Parent() {
    private val mSongs = mutableListOf<Song>()
    val songs: List<Song> get() = mSongs

    val totalDuration: String get() =
        songs.sumOf { it.seconds }.toDuration()

    fun linkSong(song: Song) {
        mSongs.add(song)
        song.linkGenre(this)
    }

    override val hash = name.hashCode()
}

/**
 * The string used for a header instance. This class is a bit complex, mostly because it revolves
 * around passing string resources that are then resolved by the view instead of passing a context
 * directly.
 */
sealed class HeaderString {
    /** A single string resource. */
    class Single(@StringRes val id: Int) : HeaderString()
    /** A string resource with an argument. */
    class WithArg(@StringRes val id: Int, val arg: Arg) : HeaderString()

    /**
     * Resolve this instance into a string.
     */
    fun resolve(context: Context): String {
        return when (this) {
            is Single -> context.getString(id)
            is WithArg -> context.getString(id, arg.resolve(context))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return false

        return when (this) {
            is Single -> other is Single && other.id == id
            is WithArg -> other is WithArg && other.id == id && other.arg == arg
        }
    }

    override fun hashCode(): Int {
        return when (this) {
            is Single -> id.hashCode()
            is WithArg -> 31 * id.hashCode() * arg.hashCode()
        }
    }

    /**
     * An argument for the [WithArg] header string.
     */
    sealed class Arg {
        /** A string resource to be used as the argument */
        class Resource(@StringRes val id: Int) : Arg()
        /** A string value to be used as the argument */
        class Value(val string: String) : Arg()

        /** Resolve this argument instance into a string. */
        fun resolve(context: Context): String {
            return when (this) {
                is Resource -> context.getString(id)
                is Value -> string
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return false

            return when (this) {
                is Resource -> other is Resource && other.id == id
                is Value -> other is Value && other.string == this.string
            }
        }

        override fun hashCode(): Int {
            return when (this) {
                is Resource -> id.hashCode()
                is Value -> 31 * string.hashCode()
            }
        }
    }
}

/**
 * A data object used solely for the "Header" UI element.
 * @see HeaderString
 */
data class Header(
    override val id: Long,
    val string: HeaderString
) : BaseModel()

/**
 * A data object used for an action header. Like [Header], but with a button.
 * @see HeaderString
 */
data class ActionHeader(
    override val id: Long,
    val string: HeaderString,
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
