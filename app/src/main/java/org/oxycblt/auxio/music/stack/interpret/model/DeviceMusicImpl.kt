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
 
package org.oxycblt.auxio.music.stack.interpret.model

import kotlin.math.min
import org.oxycblt.auxio.image.extractor.ParentCover
import org.oxycblt.auxio.list.sort.Sort
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicType
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.info.Date
import org.oxycblt.auxio.music.stack.interpret.linker.LinkedAlbum
import org.oxycblt.auxio.music.stack.interpret.linker.LinkedSong
import org.oxycblt.auxio.music.stack.interpret.prepare.PreArtist
import org.oxycblt.auxio.music.stack.interpret.prepare.PreGenre
import org.oxycblt.auxio.util.update

/**
 * Library-backed implementation of [Song].
 *
 * @param linkedSong The completed [LinkedSong] all metadata van be inferred from
 * @author Alexander Capehart (OxygenCobalt)
 */
class SongImpl(linkedSong: LinkedSong) : Song {
    private val preSong = linkedSong.preSong

    override val uid =
        // Attempt to use a MusicBrainz ID first before falling back to a hashed UID.
        preSong.musicBrainzId?.let { Music.UID.musicBrainz(MusicType.SONGS, it) }
            ?: Music.UID.auxio(MusicType.SONGS) {
                // Song UIDs are based on the raw data without parsing so that they remain
                // consistent across music setting changes. Parents are not held up to the
                // same standard since grouping is already inherently linked to settings.
                update(preSong.rawName)
                update(preSong.preAlbum.rawName)
                update(preSong.date)

                update(preSong.track)
                update(preSong.disc?.number)

                update(preSong.preArtists.map { it.rawName })
                update(preSong.preAlbum.preArtists.map { it.rawName })
            }
    override val name = preSong.name
    override val track = preSong.track
    override val disc = preSong.disc
    override val date = preSong.date
    override val uri = preSong.uri
    override val cover = preSong.cover
    override val path = preSong.path
    override val mimeType = preSong.mimeType
    override val size = preSong.size
    override val durationMs = preSong.durationMs
    override val replayGainAdjustment = preSong.replayGainAdjustment
    override val dateAdded = preSong.dateAdded
    override val album = linkedSong.album.resolve(this)
    override val artists = linkedSong.artists.resolve(this)
    override val genres = linkedSong.genres.resolve(this)

    private val hashCode = 31 * uid.hashCode() + preSong.hashCode()

    override fun hashCode() = hashCode

    override fun equals(other: Any?) =
        other is SongImpl && uid == other.uid && preSong == other.preSong

    override fun toString() = "Song(uid=$uid, name=$name)"
}

/**
 * Library-backed implementation of [Album].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class AlbumImpl(linkedAlbum: LinkedAlbum) : Album {
    private val preAlbum = linkedAlbum.preAlbum

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
    override var durationMs = 0L
    override var dateAdded = 0L
    override lateinit var cover: ParentCover
    override var dates: Date.Range? = null

    override val artists = linkedAlbum.artists.resolve(this)
    override val songs = mutableSetOf<Song>()

    private var hashCode = 31 * uid.hashCode() + preAlbum.hashCode()

    override fun hashCode() = hashCode

    // Since equality on public-facing music models is not identical to the tag equality,
    // we just compare raw instances and how they are interpreted.
    override fun equals(other: Any?) =
        other is AlbumImpl && uid == other.uid && preAlbum == other.preAlbum && songs == other.songs

    override fun toString() = "Album(uid=$uid, name=$name)"

    fun link(song: SongImpl) {
        songs.add(song)
        durationMs += song.durationMs
        dateAdded = min(dateAdded, song.dateAdded)
        if (song.date != null) {
            dates =
                dates?.let {
                    if (song.date < it.min) Date.Range(song.date, it.max)
                    else if (song.date > it.max) Date.Range(it.min, song.date) else it
                } ?: Date.Range(song.date, song.date)
        }
        hashCode = 31 * hashCode + song.hashCode()
    }

    /**
     * Perform final validation and organization on this instance.
     *
     * @return This instance upcasted to [Album].
     */
    fun finalize(): Album {
        return this
    }
}

/**
 * Library-backed implementation of [Artist].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class ArtistImpl(private val preArtist: PreArtist) : Artist {
    override val uid =
        // Attempt to use a MusicBrainz ID first before falling back to a hashed UID.
        preArtist.musicBrainzId?.let { Music.UID.musicBrainz(MusicType.ARTISTS, it) }
            ?: Music.UID.auxio(MusicType.ARTISTS) { update(preArtist.rawName) }
    override val name = preArtist.name

    override val songs = mutableSetOf<Song>()

    private val albums = mutableSetOf<Album>()
    private val albumMap = mutableMapOf<Album, Boolean>()
    override lateinit var explicitAlbums: Set<Album>
    override lateinit var implicitAlbums: Set<Album>

    override lateinit var genres: List<Genre>

    override var durationMs = 0L
    override lateinit var cover: ParentCover

    private var hashCode = 31 * uid.hashCode() + preArtist.hashCode()

    // Note: Append song contents to MusicParent equality so that artists with
    // the same UID but different songs are not equal.
    override fun hashCode() = hashCode

    // Since equality on public-facing music models is not identical to the tag equality,
    // we just compare raw instances and how they are interpreted.
    override fun equals(other: Any?) =
        other is ArtistImpl &&
            uid == other.uid &&
            preArtist == other.preArtist &&
            songs == other.songs

    override fun toString() = "Artist(uid=$uid, name=$name)"

    fun link(song: SongImpl) {
        songs.add(song)
        durationMs += song.durationMs
        if (albumMap[song.album] == null) {
            albumMap[song.album] = false
        }
        hashCode = 31 * hashCode + song.hashCode()
    }

    fun link(album: AlbumImpl) {
        albums.add(album)
        albumMap[album] = true
    }

    /**
     * Perform final validation and organization on this instance.
     *
     * @return This instance upcasted to [Artist].
     */
    fun finalize(): Artist {
        // There are valid artist configurations:
        // 1. No songs, no implicit albums, some explicit albums
        // 2. Some songs, no implicit albums, some explicit albums
        // 3. Some songs, some implicit albums, no implicit albums
        // 4. Some songs, some implicit albums, some explicit albums
        // I'm pretty sure the latter check could be reduced to just explicitAlbums.isNotEmpty,
        // but I can't be 100% certain.
        check(songs.isNotEmpty() || (implicitAlbums.size + explicitAlbums.size) > 0) {
            "Malformed artist $name: Empty"
        }
        explicitAlbums = albums.filterTo(mutableSetOf()) { albumMap[it] == true }
        implicitAlbums = albums.filterNotTo(mutableSetOf()) { albumMap[it] == true }
        genres =
            Sort(Sort.Mode.ByName, Sort.Direction.ASCENDING)
                .genres(songs.flatMapTo(mutableSetOf()) { it.genres })
                .sortedByDescending { genre -> songs.count { it.genres.contains(genre) } }
        return this
    }
}

/**
 * Library-backed implementation of [Genre].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class GenreImpl(private val preGenre: PreGenre) : Genre {
    override val uid = Music.UID.auxio(MusicType.GENRES) { update(preGenre.rawName) }
    override val name = preGenre.name

    override val songs = mutableSetOf<Song>()
    override val artists = mutableSetOf<Artist>()
    override var durationMs = 0L
    override lateinit var cover: ParentCover

    private var hashCode = uid.hashCode()

    override fun hashCode() = hashCode

    override fun equals(other: Any?) =
        other is GenreImpl && uid == other.uid && preGenre == other.preGenre && songs == other.songs

    override fun toString() = "Genre(uid=$uid, name=$name)"

    fun link(song: SongImpl) {
        songs.add(song)
        artists.addAll(song.artists)
        durationMs += song.durationMs
        hashCode = 31 * hashCode + song.hashCode()
    }

    /**
     * Perform final validation and organization on this instance.
     *
     * @return This instance upcasted to [Genre].
     */
    fun finalize(): Genre {
        return this
    }
}
