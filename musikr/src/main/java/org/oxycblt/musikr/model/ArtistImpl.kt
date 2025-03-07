/*
 * Copyright (c) 2024 Auxio Project
 * ArtistImpl.kt is part of Auxio.
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
import org.oxycblt.musikr.Genre
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.Song
import org.oxycblt.musikr.covers.CoverCollection
import org.oxycblt.musikr.tag.interpret.PreArtist
import org.oxycblt.musikr.util.update

internal interface ArtistCore {
    val preArtist: PreArtist
    val songs: Set<Song>
    val albums: Set<Album>

    fun resolveGenres(): Set<Genre>
}

/**
 * Library-backed implementation of [Artist].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
internal class ArtistImpl(private val core: ArtistCore) : Artist {
    override val uid =
        // Attempt to use a MusicBrainz ID first before falling back to a hashed UID.
        core.preArtist.musicBrainzId?.let { Music.UID.musicBrainz(Music.UID.Item.ARTIST, it) }
            ?: Music.UID.auxio(Music.UID.Item.ARTIST) { update(core.preArtist.rawName) }
    override val name = core.preArtist.name

    override val songs = core.songs
    override var explicitAlbums = core.albums
    override var implicitAlbums = core.songs.mapTo(mutableSetOf()) { it.album } - core.albums

    override val genres: List<Genre>
        get() = core.resolveGenres().toList()

    override val durationMs = core.songs.sumOf { it.durationMs }
    override val covers = CoverCollection.from(core.songs.mapNotNull { it.cover })

    private val hashCode =
        31 * (31 * uid.hashCode() + core.preArtist.hashCode()) * core.songs.hashCode()

    override fun hashCode() = hashCode

    override fun equals(other: Any?) =
        other is ArtistImpl &&
            uid == other.uid &&
            core.preArtist == other.core.preArtist &&
            songs == other.songs

    override fun toString() = "Artist(uid=$uid, name=$name)"
}
