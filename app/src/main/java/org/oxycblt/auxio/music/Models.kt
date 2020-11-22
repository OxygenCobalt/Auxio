package org.oxycblt.auxio.music

import android.net.Uri

// --- MUSIC MODELS ---

/**
 * The base data object for all music.
 * @property id The ID that is assigned to this object
 * @property name The name of this object (Such as a song title)
 * @author OxygenCobalt
 */
sealed class BaseModel {
    abstract val id: Long
    abstract val name: String
}

/**
 * The data object for a song. Inherits [BaseModel].
 * @property albumId  The Song's Album ID. Never use this outside of when attaching a song to its album.
 * @property track    The Song's Track number
 * @property duration The duration of the song, in millis.
 * @property album    The Song's parent album. Use this instead of [albumId].
 * @property seconds  The Song's duration in seconds
 * @property formattedDuration The Song's duration as a duration string.
 * @author OxygenCobalt
 */
data class Song(
    override val id: Long = -1,
    override var name: String,
    val albumId: Long = -1,
    val track: Int = -1,
    val duration: Long = 0,
) : BaseModel() {
    lateinit var album: Album

    val seconds = duration / 1000
    val formattedDuration: String = seconds.toDuration()
}

/**
 * The data object for an album. Inherits [BaseModel].
 * @property artistId The Album's parent artist ID. Do not use this outside of attaching an album to its parent artist.
 * @property coverUri The [Uri] for the album's cover. **Load this using Coil.**
 * @property year     The year this album was released. 0 if there is none in the metadata.
 * @property artist   The Album's parent [Artist]. use this instead of [artistId]
 * @property songs    The Album's child [Song]s.
 * @property totalDuration The combined duration of all of the album's child songs, formatted.
 * @author OxygenCobalt
 */
data class Album(
    override val id: Long = -1,
    override val name: String,
    val artistId: Long = -1,
    val coverUri: Uri = Uri.EMPTY,
    val year: Int = 0
) : BaseModel() {
    lateinit var artist: Artist

    val songs = mutableListOf<Song>()
    val totalDuration: String by lazy {
        var seconds: Long = 0
        songs.forEach {
            seconds += it.seconds
        }
        seconds.toDuration()
    }
}

/**
 * The data object for an artist. Inherits [BaseModel]
 * @author OxygenCobalt
 */
data class Artist(
    override val id: Long = -1,
    override var name: String
) : BaseModel() {
    val albums = mutableListOf<Album>()
    val genres = mutableListOf<Genre>()

    val songs: List<Song> by lazy {
        val songs = mutableListOf<Song>()
        albums.forEach {
            songs.addAll(it.songs)
        }
        songs
    }
}

/**
 * The data object for a genre. Inherits [BaseModel]
 * @property artists The list of all [Artist]s in this genre
 * @property songs   The list of all [Song]s in this genre.
 * @author OxygenCobalt
 */
data class Genre(
    override val id: Long = -1,
    override var name: String,
) : BaseModel() {
    val artists = mutableListOf<Artist>()

    val albums: List<Album> by lazy {
        val albums = mutableListOf<Album>()
        artists.forEach {
            albums.addAll(it.albums)
        }
        albums
    }
    val songs: List<Song> by lazy {
        val songs = mutableListOf<Song>()
        artists.forEach {
            songs.addAll(it.songs)
        }
        songs
    }
}

/**
 * A data object used solely for the "Header" UI element. Inherits [BaseModel].
 */
data class Header(
    override val id: Long = -1,
    override var name: String = ""
) : BaseModel()
