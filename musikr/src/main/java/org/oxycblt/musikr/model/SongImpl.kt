/*
 * Copyright (c) 2024 Auxio Project
 * SongImpl.kt is part of Auxio.
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
import org.oxycblt.musikr.Song
import org.oxycblt.musikr.tag.interpret.PreSong

internal interface SongCore {
    val preSong: PreSong

    fun resolveAlbum(): Album

    fun resolveArtists(): List<Artist>

    fun resolveGenres(): List<Genre>
}

/**
 * Library-backed implementation of [Song].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
internal class SongImpl(private val handle: SongCore) : Song {
    private val preSong = handle.preSong

    override val uid = preSong.uid
    override val name = preSong.name
    override val track = preSong.track
    override val disc = preSong.disc
    override val date = preSong.date
    override val uri = preSong.uri
    override val path = preSong.path
    override val format = preSong.format
    override val size = preSong.size
    override val durationMs = preSong.durationMs
    override val bitrateKbps = preSong.bitrateKbps
    override val sampleRateHz = preSong.sampleRateHz
    override val replayGainAdjustment = preSong.replayGainAdjustment
    override val lastModified = preSong.lastModified
    override val dateAdded = preSong.dateAdded
    override val cover = preSong.cover
    override val album: Album
        get() = handle.resolveAlbum()

    override val artists: List<Artist>
        get() = handle.resolveArtists()

    override val genres: List<Genre>
        get() = handle.resolveGenres()

    private val hashCode = 31 * uid.hashCode() + preSong.hashCode()

    override fun hashCode() = hashCode

    override fun equals(other: Any?) =
        other is SongImpl && uid == other.uid && preSong == other.preSong

    override fun toString() = "Song(uid=$uid, name=$name)"
}
