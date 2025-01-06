/*
 * Copyright (c) 2024 Auxio Project
 * AlbumImpl.kt is part of Auxio.
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
 
package org.oxycblt.musikr.model

import org.oxycblt.musikr.Album
import org.oxycblt.musikr.Artist
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.Song
import org.oxycblt.musikr.cover.CoverCollection
import org.oxycblt.musikr.tag.Date
import org.oxycblt.musikr.tag.interpret.PreAlbum
import org.oxycblt.musikr.util.update

internal interface AlbumCore {
    val preAlbum: PreAlbum
    val songs: Set<Song>

    fun resolveArtists(): List<Artist>
}

/**
 * Library-backed implementation of [Album].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class AlbumImpl internal constructor(private val core: AlbumCore) : Album {
    private val preAlbum = core.preAlbum

    override val uid =
        // Attempt to use a MusicBrainz ID first before falling back to a hashed UID.
        preAlbum.musicBrainzId?.let { Music.UID.musicBrainz(Music.UID.Item.ALBUM, it) }
            ?: Music.UID.auxio(Music.UID.Item.ALBUM) {
                // Hash based on only names despite the presence of a date to increase stability.
                // I don't know if there is any situation where an artist will have two albums with
                // the exact same name, but if there is, I would love to know.
                update(preAlbum.rawName)
                update(preAlbum.preArtists.map { it.rawName })
            }
    override val name = preAlbum.name
    override val releaseType = preAlbum.releaseType
    override val durationMs = core.songs.sumOf { it.durationMs }
    override val dateAdded = core.songs.minOf { it.dateAdded }
    override val covers = CoverCollection.from(core.songs.mapNotNull { it.cover })
    override val dates: Date.Range? =
        core.songs.mapNotNull { it.date }.ifEmpty { null }?.run { Date.Range(min(), max()) }

    override val artists: List<Artist>
        get() = core.resolveArtists()

    override val songs = core.songs

    private val hashCode = 31 * (31 * uid.hashCode() + preAlbum.hashCode()) + songs.hashCode()

    override fun hashCode() = hashCode

    override fun equals(other: Any?) =
        other is AlbumImpl && uid == other.uid && preAlbum == other.preAlbum && songs == other.songs

    override fun toString() = "Album(uid=$uid, name=$name)"

    fun a(other: AlbumImpl) = uid == other.uid
}
