/*
 * Copyright (c) 2023 Auxio Project
 * GenreImpl.kt is part of Auxio.
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

import org.oxycblt.musikr.Artist
import org.oxycblt.musikr.Genre
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.Song
import org.oxycblt.musikr.cover.CoverCollection
import org.oxycblt.musikr.tag.interpret.PreGenre
import org.oxycblt.musikr.util.update

internal interface GenreCore {
    val preGenre: PreGenre
    val songs: Set<Song>
    val artists: Set<Artist>
}

/**
 * Library-backed implementation of [Genre].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
internal class GenreImpl(private val core: GenreCore) : Genre {
    override val uid = Music.UID.auxio(Music.UID.Item.GENRE) { update(core.preGenre.rawName) }
    override val name = core.preGenre.name

    override val songs = core.songs
    override val artists = core.artists
    override val durationMs = core.songs.sumOf { it.durationMs }
    override val covers = CoverCollection.from(core.songs.mapNotNull { it.cover })

    private val hashCode = 31 * (31 * uid.hashCode() + core.preGenre.hashCode()) + songs.hashCode()

    override fun hashCode() = hashCode

    override fun equals(other: Any?) =
        other is GenreImpl &&
            uid == other.uid &&
            core.preGenre == other.core.preGenre &&
            songs == other.songs

    override fun toString() = "Genre(uid=$uid, name=$name)"
}
