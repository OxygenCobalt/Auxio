package org.oxycblt.auxio.music

import android.net.Uri

// --- MUSIC MODELS ---
// FIXME: Remove parent/child references so that they can be parcelable?

// The base model for all music
// This is used in a lot of general functions in order to have generic utilities
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
    val artistId: Long = -1,
    val coverUri: Uri = Uri.EMPTY,
    val year: Int = 0
) : BaseModel() {
    lateinit var artist: Artist

    val songs = mutableListOf<Song>()
    val numSongs: Int get() = songs.size
    val totalDuration: String by lazy {
        var seconds: Long = 0
        songs.forEach {
            seconds += it.seconds
        }
        seconds.toDuration()
    }
}

// Artist
data class Artist(
    override val id: Long = -1,
    override var name: String
) : BaseModel() {
    val albums = mutableListOf<Album>()
    val genres = mutableListOf<Genre>()

    val numAlbums: Int get() = albums.size
    val numSongs: Int by lazy {
        var num = 0
        albums.forEach {
            num += it.numSongs
        }
        num
    }
    val songs: MutableList<Song> by lazy {
        val songs = mutableListOf<Song>()
        albums.forEach {
            songs.addAll(it.songs)
        }
        songs
    }
}

// Genre
data class Genre(
    override val id: Long = -1,
    override var name: String,
) : BaseModel() {
    val artists = mutableListOf<Artist>()

    val numArtists: Int get() = artists.size
    val numAlbums: Int by lazy {
        var num = 0
        artists.forEach {
            num += it.numAlbums
        }
        num
    }
    val numSongs: Int by lazy {
        var num = 0
        artists.forEach {
            num += it.numSongs
        }
        num
    }
    val songs: MutableList<Song> by lazy {
        val songs = mutableListOf<Song>()
        artists.forEach {
            songs.addAll(it.songs)
        }
        songs
    }
}

// Header [Used for search, nothing else]
data class Header(
    override val id: Long = -1,
    override var name: String = "",
) : BaseModel()
