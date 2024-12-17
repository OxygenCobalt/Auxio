package org.oxycblt.musikr.playlist.interpret

import org.oxycblt.musikr.Interpretation
import org.oxycblt.musikr.playlist.PlaylistFile
import org.oxycblt.musikr.playlist.PlaylistHandle
import org.oxycblt.musikr.playlist.SongPointer

internal interface PlaylistInterpreter {
    fun interpret(file: PlaylistFile, interpretation: Interpretation): PrePlaylist

    fun interpret(
        name: String,
        handle: PlaylistHandle,
        interpretation: Interpretation
    ): PostPlaylist

    companion object {
        fun new(): PlaylistInterpreter = PlaylistInterpreterImpl
    }
}

private data object PlaylistInterpreterImpl : PlaylistInterpreter {
    override fun interpret(file: PlaylistFile, interpretation: Interpretation) =
        PrePlaylist(
            name = interpretation.naming.name(file.name, null),
            rawName = file.name,
            handle = file.handle,
            songPointers = file.songPointers
        )

    override fun interpret(
        name: String,
        handle: PlaylistHandle,
        interpretation: Interpretation
    ): PostPlaylist =
        PostPlaylist(
            name = interpretation.naming.name(name, null),
            rawName = name,
            handle = handle
        )

}