package org.oxycblt.auxio.music.stack.interpret.model

import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Playlist

interface Library {
    val songs: Collection<Song>
    val albums: Collection<Album>
    val artists: Collection<Artist>
    val genres: Collection<Genre>
    val playlists: Collection<Playlist>

    fun findSong(uid: Music.UID): Song?
    fun findAlbum(uid: Music.UID): Album?
    fun findArtist(uid: Music.UID): Artist?
    fun findGenre(uid: Music.UID): Genre?
    fun findPlaylist(uid: Music.UID): Playlist?
}

interface MutableLibrary : Library {
    suspend fun createPlaylist(name: String, songs: List<Song>): MutableLibrary
    suspend fun renamePlaylist(playlist: Playlist, name: String): MutableLibrary
    suspend fun addToPlaylist(playlist: Playlist, songs: List<Song>): MutableLibrary
    suspend fun rewritePlaylist(playlist: Playlist, songs: List<Song>): MutableLibrary
    suspend fun deletePlaylist(playlist: Playlist): MutableLibrary
}

class LibraryImpl(
    override val songs: Collection<SongImpl>,
    override val albums: Collection<AlbumImpl>,
    override val artists: Collection<ArtistImpl>,
    override val genres: Collection<GenreImpl>
) : MutableLibrary {
    override val playlists = emptySet<Playlist>()

    override fun findSong(uid: Music.UID): Song? {
        TODO("Not yet implemented")
    }

    override fun findAlbum(uid: Music.UID): Album? {
        TODO("Not yet implemented")
    }

    override fun findArtist(uid: Music.UID): Artist? {
        TODO("Not yet implemented")
    }

    override fun findGenre(uid: Music.UID): Genre? {
        TODO("Not yet implemented")
    }

    override fun findPlaylist(uid: Music.UID): Playlist? {
        TODO("Not yet implemented")
    }

    override suspend fun createPlaylist(name: String, songs: List<Song>): MutableLibrary {
        TODO("Not yet implemented")
    }

    override suspend fun renamePlaylist(playlist: Playlist, name: String): MutableLibrary {
        TODO("Not yet implemented")
    }

    override suspend fun addToPlaylist(playlist: Playlist, songs: List<Song>): MutableLibrary {
        TODO("Not yet implemented")
    }

    override suspend fun rewritePlaylist(playlist: Playlist, songs: List<Song>): MutableLibrary {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlaylist(playlist: Playlist): MutableLibrary {
        TODO("Not yet implemented")
    }
}
