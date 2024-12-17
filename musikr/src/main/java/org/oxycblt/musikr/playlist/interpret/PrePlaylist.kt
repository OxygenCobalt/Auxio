package org.oxycblt.musikr.playlist.interpret

import org.oxycblt.musikr.playlist.PlaylistHandle
import org.oxycblt.musikr.playlist.SongPointer
import org.oxycblt.musikr.tag.Name

internal interface PrePlaylistInfo {
    val name: Name.Known
    val rawName: String?
    val handle: PlaylistHandle
}

internal data class PrePlaylist(
    override val name: Name.Known,
    override val rawName: String?,
    override val handle: PlaylistHandle,
    val songPointers: List<SongPointer>
) : PrePlaylistInfo

internal data class PostPlaylist(
    override val name: Name.Known,
    override val rawName: String?,
    override val handle: PlaylistHandle,
) : PrePlaylistInfo