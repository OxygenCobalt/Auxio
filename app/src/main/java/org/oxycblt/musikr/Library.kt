package org.oxycblt.musikr

import org.oxycblt.musikr.fs.Path


interface Library {
    val songs: Collection<Song>
    val albums: Collection<Album>
    val artists: Collection<Artist>
    val genres: Collection<Genre>
    val playlists: Collection<Playlist>

    fun findSong(uid: Music.UID): Song?

    fun findSongByPath(path: Path): Song?

    fun findAlbum(uid: Music.UID): Album?

    fun findArtist(uid: Music.UID): Artist?

    fun findGenre(uid: Music.UID): Genre?

    fun findPlaylist(uid: Music.UID): Playlist?

    fun findPlaylistByName(name: String): Playlist?
}

interface MutableLibrary : Library {
    suspend fun createPlaylist(name: String, songs: List<Song>): MutableLibrary

    suspend fun renamePlaylist(playlist: Playlist, name: String): MutableLibrary

    suspend fun addToPlaylist(playlist: Playlist, songs: List<Song>): MutableLibrary

    suspend fun rewritePlaylist(playlist: Playlist, songs: List<Song>): MutableLibrary

    suspend fun deletePlaylist(playlist: Playlist): MutableLibrary
}
