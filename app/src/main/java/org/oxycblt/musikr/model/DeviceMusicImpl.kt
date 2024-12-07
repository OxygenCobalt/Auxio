/*
 * Copyright (c) 2023 Auxio Project
 * DeviceMusicImpl.kt is part of Auxio.
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
import org.oxycblt.auxio.music.MusicType
import org.oxycblt.musikr.Song
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.tag.Date
import org.oxycblt.musikr.tag.interpret.PreAlbum
import org.oxycblt.musikr.tag.interpret.PreArtist
import org.oxycblt.musikr.tag.interpret.PreGenre
import org.oxycblt.musikr.tag.interpret.PreSong
import org.oxycblt.auxio.util.update

interface SongHandle {
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
class SongImpl(private val handle: SongHandle) : Song {
    private val preSong = handle.preSong

    override val uid = preSong.computeUid()
    override val name = preSong.name
    override val track = preSong.track
    override val disc = preSong.disc
    override val date = preSong.date
    override val uri = preSong.uri
    override val path = preSong.path
    override val mimeType = preSong.mimeType
    override val size = preSong.size
    override val durationMs = preSong.durationMs
    override val replayGainAdjustment = preSong.replayGainAdjustment
    override val lastModified = preSong.lastModified
    override val dateAdded = preSong.dateAdded
    override val cover = Cover.single(this)
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

interface AlbumHandle {
    val preAlbum: PreAlbum
    val songs: List<Song>

    fun resolveArtists(): List<Artist>
}

/**
 * Library-backed implementation of [Album].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class AlbumImpl(private val handle: AlbumHandle) : Album {
    private val preAlbum = handle.preAlbum

    override val uid =
        // Attempt to use a MusicBrainz ID first before falling back to a hashed UID.
        preAlbum.musicBrainzId?.let { Music.UID.musicBrainz(MusicType.ALBUMS, it) }
            ?: Music.UID.auxio(MusicType.ALBUMS) {
                // Hash based on only names despite the presence of a date to increase stability.
                // I don't know if there is any situation where an artist will have two albums with
                // the exact same name, but if there is, I would love to know.
                update(preAlbum.rawName)
                update(preAlbum.preArtists.map { it.rawName })
            }
    override val name = preAlbum.name
    override val releaseType = preAlbum.releaseType
    override val durationMs = handle.songs.sumOf { it.durationMs }
    override val dateAdded = handle.songs.minOf { it.dateAdded }
    override val cover = Cover.multi(handle.songs)
    override val dates: Date.Range? =
        handle.songs.mapNotNull { it.date }.ifEmpty { null }?.run { Date.Range(min(), max()) }

    override val artists: List<Artist>
        get() = handle.resolveArtists()

    override val songs = handle.songs

    private val hashCode = 31 * (31 * uid.hashCode() + preAlbum.hashCode()) + songs.hashCode()

    override fun hashCode() = hashCode

    override fun equals(other: Any?) =
        other is AlbumImpl && uid == other.uid && preAlbum == other.preAlbum && songs == other.songs

    override fun toString() = "Album(uid=$uid, name=$name)"
}

interface ArtistHandle {
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
class ArtistImpl(private val handle: ArtistHandle) : Artist {
    override val uid =
        // Attempt to use a MusicBrainz ID first before falling back to a hashed UID.
        handle.preArtist.musicBrainzId?.let { Music.UID.musicBrainz(MusicType.ARTISTS, it) }
            ?: Music.UID.auxio(MusicType.ARTISTS) { update(handle.preArtist.rawName) }
    override val name = handle.preArtist.name

    override val songs = handle.songs
    override var explicitAlbums = handle.albums
    override var implicitAlbums = handle.songs.mapTo(mutableSetOf()) { it.album } - handle.albums

    override val genres: List<Genre>
        get() = handle.resolveGenres().toList()

    override val durationMs = handle.songs.sumOf { it.durationMs }
    override val cover = Cover.multi(handle.songs)

    private val hashCode =
        31 * (31 * uid.hashCode() + handle.preArtist.hashCode()) * handle.songs.hashCode()

    override fun hashCode() = hashCode

    override fun equals(other: Any?) =
        other is ArtistImpl &&
            uid == other.uid &&
            handle.preArtist == other.handle.preArtist &&
            songs == other.songs

    override fun toString() = "Artist(uid=$uid, name=$name)"
}

interface GenreHandle {
    val preGenre: PreGenre
    val songs: Set<Song>
    val artists: Set<Artist>
}

/**
 * Library-backed implementation of [Genre].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class GenreImpl(private val handle: GenreHandle) : Genre {
    override val uid = Music.UID.auxio(MusicType.GENRES) { update(handle.preGenre.rawName) }
    override val name = handle.preGenre.name

    override val songs = mutableSetOf<Song>()
    override val artists = mutableSetOf<Artist>()
    override val durationMs = handle.songs.sumOf { it.durationMs }
    override val cover = Cover.multi(handle.songs)

    private val hashCode =
        31 * (31 * uid.hashCode() + handle.preGenre.hashCode()) + songs.hashCode()

    override fun hashCode() = hashCode

    override fun equals(other: Any?) =
        other is GenreImpl &&
            uid == other.uid &&
            handle.preGenre == other.handle.preGenre &&
            songs == other.songs

    override fun toString() = "Genre(uid=$uid, name=$name)"
}
