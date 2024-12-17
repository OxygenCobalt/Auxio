package org.oxycblt.auxio.music

import org.oxycblt.musikr.Library
import org.oxycblt.musikr.MutableLibrary
import org.oxycblt.musikr.Playlist
import org.oxycblt.musikr.Song
import java.util.UUID

interface RevisionedLibrary : Library {
    val revision: UUID
}

class MutableRevisionedLibrary(
    override val revision: UUID,
    private val inner: MutableLibrary
) : RevisionedLibrary, Library by inner, MutableLibrary {
    override suspend fun createPlaylist(name: String, songs: List<Song>) =
        MutableRevisionedLibrary(revision, inner.createPlaylist(name, songs))

    override suspend fun renamePlaylist(playlist: Playlist, name: String) =
        MutableRevisionedLibrary(revision, inner.renamePlaylist(playlist, name))

    override suspend fun addToPlaylist(playlist: Playlist, songs: List<Song>) =
        MutableRevisionedLibrary(revision, inner.addToPlaylist(playlist, songs))

    override suspend fun rewritePlaylist(playlist: Playlist, songs: List<Song>) =
        MutableRevisionedLibrary(revision, inner.rewritePlaylist(playlist, songs))

    override suspend fun deletePlaylist(playlist: Playlist) =
        MutableRevisionedLibrary(revision, inner.deletePlaylist(playlist))

}