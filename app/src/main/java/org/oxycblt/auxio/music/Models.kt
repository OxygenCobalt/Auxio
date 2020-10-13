package org.oxycblt.auxio.music

import android.net.Uri

// --- MUSIC MODELS ---
// TODO: Remove parent/child references so that they can be parcelable [Would require genre rework]
// TODO: Don't determine artist/album/song counts on the fly [If possible]

// The base model for all music
// This is used in a lot of general functions in order to cut down on code
sealed class BaseModel {
    abstract val id: Long
    abstract val name: String
}

// Song
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

// Album
data class Album(
    override val id: Long = -1,
    override val name: String,
    val artistName: String,
    val coverUri: Uri = Uri.EMPTY,
    val year: Int = 0
) : BaseModel() {
    lateinit var artist: Artist

    val songs = mutableListOf<Song>()
    val numSongs: Int get() = songs.size
    val totalDuration: String
        get() {
            var seconds: Long = 0
            songs.forEach {
                seconds += it.seconds
            }
            return seconds.toDuration()
        }
}

// Artist
data class Artist(
    override val id: Long = -1,
    override var name: String,
    val givenGenres: MutableList<Genre> = mutableListOf()
) : BaseModel() {
    val albums = mutableListOf<Album>()
    val genres = mutableListOf<Genre>()

    val numAlbums: Int get() = albums.size
    val numSongs: Int
        get() {
            var num = 0
            albums.forEach {
                num += it.numSongs
            }
            return num
        }
}

// Genre
data class Genre(
    override val id: Long = -1,
    override var name: String,
) : BaseModel() {
    val artists = mutableListOf<Artist>()

    val numArtists: Int get() = artists.size
    val numAlbums: Int
        get() {
            var num = 0
            artists.forEach {
                num += it.numAlbums
            }
            return num
        }
    val numSongs: Int
        get() {
            var num = 0
            artists.forEach {
                num += it.numSongs
            }
            return num
        }
}

// Header [Used for search, nothing else]
data class Header(
    override val id: Long = -1,
    override var name: String = "",
) : BaseModel()
