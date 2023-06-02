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
 
package org.oxycblt.auxio.music.device

import org.oxycblt.auxio.R
import org.oxycblt.auxio.list.Sort
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.fs.MimeType
import org.oxycblt.auxio.music.fs.Path
import org.oxycblt.auxio.music.fs.toAudioUri
import org.oxycblt.auxio.music.fs.toCoverUri
import org.oxycblt.auxio.music.info.Date
import org.oxycblt.auxio.music.info.Disc
import org.oxycblt.auxio.music.info.Name
import org.oxycblt.auxio.music.info.ReleaseType
import org.oxycblt.auxio.music.metadata.parseId3GenreNames
import org.oxycblt.auxio.music.metadata.parseMultiValue
import org.oxycblt.auxio.util.nonZeroOrNull
import org.oxycblt.auxio.util.toUuidOrNull
import org.oxycblt.auxio.util.unlikelyToBeNull
import org.oxycblt.auxio.util.update

/**
 * Library-backed implementation of [Song].
 *
 * @param rawSong The [RawSong] to derive the member data from.
 * @param musicSettings [MusicSettings] to for user parsing configuration.
 * @author Alexander Capehart (OxygenCobalt)
 */
class SongImpl(private val rawSong: RawSong, musicSettings: MusicSettings) : Song {
    override val uid =
        // Attempt to use a MusicBrainz ID first before falling back to a hashed UID.
        rawSong.musicBrainzId?.toUuidOrNull()?.let { Music.UID.musicBrainz(MusicMode.SONGS, it) }
            ?: Music.UID.auxio(MusicMode.SONGS) {
                // Song UIDs are based on the raw data without parsing so that they remain
                // consistent across music setting changes. Parents are not held up to the
                // same standard since grouping is already inherently linked to settings.
                update(rawSong.name)
                update(rawSong.albumName)
                update(rawSong.date)

                update(rawSong.track)
                update(rawSong.disc)

                update(rawSong.artistNames)
                update(rawSong.albumArtistNames)
            }
    override val name =
        Name.Known.from(
            requireNotNull(rawSong.name) { "Invalid raw: No title" },
            rawSong.sortName,
            musicSettings)

    override val track = rawSong.track
    override val disc = rawSong.disc?.let { Disc(it, rawSong.subtitle) }
    override val date = rawSong.date
    override val uri = requireNotNull(rawSong.mediaStoreId) { "Invalid raw: No id" }.toAudioUri()
    override val path =
        Path(
            name = requireNotNull(rawSong.fileName) { "Invalid raw: No display name" },
            parent = requireNotNull(rawSong.directory) { "Invalid raw: No parent directory" })
    override val mimeType =
        MimeType(
            fromExtension =
                requireNotNull(rawSong.extensionMimeType) { "Invalid raw: No mime type" },
            fromFormat = null)
    override val size = requireNotNull(rawSong.size) { "Invalid raw: No size" }
    override val durationMs = requireNotNull(rawSong.durationMs) { "Invalid raw: No duration" }
    override val dateAdded = requireNotNull(rawSong.dateAdded) { "Invalid raw: No date added" }
    private var _album: AlbumImpl? = null
    override val album: Album
        get() = unlikelyToBeNull(_album)

    private val hashCode = 31 * uid.hashCode() + rawSong.hashCode()

    override fun hashCode() = hashCode
    override fun equals(other: Any?) =
        other is SongImpl && uid == other.uid && rawSong == other.rawSong
    override fun toString() = "Song(uid=$uid, name=$name)"

    private val artistMusicBrainzIds = rawSong.artistMusicBrainzIds.parseMultiValue(musicSettings)
    private val artistNames = rawSong.artistNames.parseMultiValue(musicSettings)
    private val artistSortNames = rawSong.artistSortNames.parseMultiValue(musicSettings)
    private val rawIndividualArtists =
        artistNames.mapIndexed { i, name ->
            RawArtist(
                artistMusicBrainzIds.getOrNull(i)?.toUuidOrNull(),
                name,
                artistSortNames.getOrNull(i))
        }

    private val albumArtistMusicBrainzIds =
        rawSong.albumArtistMusicBrainzIds.parseMultiValue(musicSettings)
    private val albumArtistNames = rawSong.albumArtistNames.parseMultiValue(musicSettings)
    private val albumArtistSortNames = rawSong.albumArtistSortNames.parseMultiValue(musicSettings)
    private val rawAlbumArtists =
        albumArtistNames.mapIndexed { i, name ->
            RawArtist(
                albumArtistMusicBrainzIds.getOrNull(i)?.toUuidOrNull(),
                name,
                albumArtistSortNames.getOrNull(i))
        }

    private val _artists = mutableListOf<ArtistImpl>()
    override val artists: List<Artist>
        get() = _artists

    private val _genres = mutableListOf<GenreImpl>()
    override val genres: List<Genre>
        get() = _genres

    /**
     * The [RawAlbum] instances collated by the [Song]. This can be used to group [Song]s into an
     * [Album].
     */
    val rawAlbum =
        RawAlbum(
            mediaStoreId = requireNotNull(rawSong.albumMediaStoreId) { "Invalid raw: No album id" },
            musicBrainzId = rawSong.albumMusicBrainzId?.toUuidOrNull(),
            name = requireNotNull(rawSong.albumName) { "Invalid raw: No album name" },
            sortName = rawSong.albumSortName,
            releaseType = ReleaseType.parse(rawSong.releaseTypes.parseMultiValue(musicSettings)),
            rawArtists =
                rawAlbumArtists
                    .ifEmpty { rawIndividualArtists }
                    .ifEmpty { listOf(RawArtist(null, null)) })

    /**
     * The [RawArtist] instances collated by the [Song]. The artists of the song take priority,
     * followed by the album artists. If there are no artists, this field will be a single "unknown"
     * [RawArtist]. This can be used to group up [Song]s into an [Artist].
     */
    val rawArtists =
        rawIndividualArtists.ifEmpty { rawAlbumArtists }.ifEmpty { listOf(RawArtist()) }

    /**
     * The [RawGenre] instances collated by the [Song]. This can be used to group up [Song]s into a
     * [Genre]. ID3v2 Genre names are automatically converted to their resolved names.
     */
    val rawGenres =
        rawSong.genreNames
            .parseId3GenreNames(musicSettings)
            .map { RawGenre(it) }
            .ifEmpty { listOf(RawGenre()) }

    /**
     * Links this [Song] with a parent [Album].
     *
     * @param album The parent [Album] to link to.
     */
    fun link(album: AlbumImpl) {
        _album = album
    }

    /**
     * Links this [Song] with a parent [Artist].
     *
     * @param artist The parent [Artist] to link to.
     */
    fun link(artist: ArtistImpl) {
        _artists.add(artist)
    }

    /**
     * Links this [Song] with a parent [Genre].
     *
     * @param genre The parent [Genre] to link to.
     */
    fun link(genre: GenreImpl) {
        _genres.add(genre)
    }

    /**
     * Perform final validation and organization on this instance.
     *
     * @return This instance upcasted to [Song].
     */
    fun finalize(): Song {
        checkNotNull(_album) { "Malformed song: No album" }

        check(_artists.isNotEmpty()) { "Malformed song: No artists" }
        for (i in _artists.indices) {
            // Non-destructively reorder the linked artists so that they align with
            // the artist ordering within the song metadata.
            val newIdx = _artists[i].getOriginalPositionIn(rawArtists)
            val other = _artists[newIdx]
            _artists[newIdx] = _artists[i]
            _artists[i] = other
        }

        check(_genres.isNotEmpty()) { "Malformed song: No genres" }
        for (i in _genres.indices) {
            // Non-destructively reorder the linked genres so that they align with
            // the genre ordering within the song metadata.
            val newIdx = _genres[i].getOriginalPositionIn(rawGenres)
            val other = _genres[newIdx]
            _genres[newIdx] = _genres[i]
            _genres[i] = other
        }
        return this
    }
}

/**
 * Library-backed implementation of [Album].
 *
 * @param rawAlbum The [RawAlbum] to derive the member data from.
 * @param musicSettings [MusicSettings] to for user parsing configuration.
 * @param songs The [Song]s that are a part of this [Album]. These items will be linked to this
 *   [Album].
 * @author Alexander Capehart (OxygenCobalt)
 */
class AlbumImpl(
    private val rawAlbum: RawAlbum,
    musicSettings: MusicSettings,
    override val songs: List<SongImpl>
) : Album {
    override val uid =
        // Attempt to use a MusicBrainz ID first before falling back to a hashed UID.
        rawAlbum.musicBrainzId?.let { Music.UID.musicBrainz(MusicMode.ALBUMS, it) }
            ?: Music.UID.auxio(MusicMode.ALBUMS) {
                // Hash based on only names despite the presence of a date to increase stability.
                // I don't know if there is any situation where an artist will have two albums with
                // the exact same name, but if there is, I would love to know.
                update(rawAlbum.name)
                update(rawAlbum.rawArtists.map { it.name })
            }
    override val name = Name.Known.from(rawAlbum.name, rawAlbum.sortName, musicSettings)

    override val dates = Date.Range.from(songs.mapNotNull { it.date })
    override val releaseType = rawAlbum.releaseType ?: ReleaseType.Album(null)
    override val coverUri = rawAlbum.mediaStoreId.toCoverUri()
    override val durationMs: Long
    override val dateAdded: Long

    private val _artists = mutableListOf<ArtistImpl>()
    override val artists: List<Artist>
        get() = _artists

    private var hashCode = uid.hashCode()

    init {
        var totalDuration: Long = 0
        var earliestDateAdded: Long = Long.MAX_VALUE

        // Do linking and value generation in the same loop for efficiency.
        for (song in songs) {
            song.link(this)
            if (song.dateAdded < earliestDateAdded) {
                earliestDateAdded = song.dateAdded
            }
            totalDuration += song.durationMs
        }

        durationMs = totalDuration
        dateAdded = earliestDateAdded

        hashCode = 31 * hashCode + rawAlbum.hashCode()
        hashCode = 31 * hashCode + songs.hashCode()
    }

    override fun hashCode() = hashCode
    override fun equals(other: Any?) =
        other is AlbumImpl && uid == other.uid && rawAlbum == other.rawAlbum && songs == other.songs
    override fun toString() = "Album(uid=$uid, name=$name)"

    /**
     * The [RawArtist] instances collated by the [Album]. The album artists of the song take
     * priority, followed by the artists. If there are no artists, this field will be a single
     * "unknown" [RawArtist]. This can be used to group up [Album]s into an [Artist].
     */
    val rawArtists = rawAlbum.rawArtists

    /**
     * Links this [Album] with a parent [Artist].
     *
     * @param artist The parent [Artist] to link to.
     */
    fun link(artist: ArtistImpl) {
        _artists.add(artist)
    }

    /**
     * Perform final validation and organization on this instance.
     *
     * @return This instance upcasted to [Album].
     */
    fun finalize(): Album {
        check(songs.isNotEmpty()) { "Malformed album: Empty" }
        check(_artists.isNotEmpty()) { "Malformed album: No artists" }
        for (i in _artists.indices) {
            // Non-destructively reorder the linked artists so that they align with
            // the artist ordering within the song metadata.
            val newIdx = _artists[i].getOriginalPositionIn(rawArtists)
            val other = _artists[newIdx]
            _artists[newIdx] = _artists[i]
            _artists[i] = other
        }
        return this
    }
}

/**
 * Library-backed implementation of [Artist].
 *
 * @param rawArtist The [RawArtist] to derive the member data from.
 * @param musicSettings [MusicSettings] to for user parsing configuration.
 * @param songAlbums A list of the [Song]s and [Album]s that are a part of this [Artist] , either
 *   through artist or album artist tags. Providing [Song]s to the artist is optional. These
 *   instances will be linked to this [Artist].
 * @author Alexander Capehart (OxygenCobalt)
 */
class ArtistImpl(
    private val rawArtist: RawArtist,
    musicSettings: MusicSettings,
    songAlbums: List<Music>
) : Artist {
    override val uid =
        // Attempt to use a MusicBrainz ID first before falling back to a hashed UID.
        rawArtist.musicBrainzId?.let { Music.UID.musicBrainz(MusicMode.ARTISTS, it) }
            ?: Music.UID.auxio(MusicMode.ARTISTS) { update(rawArtist.name) }
    override val name =
        rawArtist.name?.let { Name.Known.from(it, rawArtist.sortName, musicSettings) }
            ?: Name.Unknown(R.string.def_artist)

    override val songs: List<Song>
    override val albums: List<Album>
    override val explicitAlbums: List<Album>
    override val implicitAlbums: List<Album>
    override val durationMs: Long?

    override lateinit var genres: List<Genre>

    private var hashCode = uid.hashCode()

    init {
        val distinctSongs = mutableSetOf<Song>()
        val albumMap = mutableMapOf<Album, Boolean>()

        for (music in songAlbums) {
            when (music) {
                is SongImpl -> {
                    music.link(this)
                    distinctSongs.add(music)
                    if (albumMap[music.album] == null) {
                        albumMap[music.album] = false
                    }
                }
                is AlbumImpl -> {
                    music.link(this)
                    albumMap[music] = true
                }
                else -> error("Unexpected input music ${music::class.simpleName}")
            }
        }

        songs = distinctSongs.toList()
        albums = Sort(Sort.Mode.ByDate, Sort.Direction.DESCENDING).albums(albumMap.keys)
        explicitAlbums = albums.filter { unlikelyToBeNull(albumMap[it]) }
        implicitAlbums = albums.filterNot { unlikelyToBeNull(albumMap[it]) }
        durationMs = songs.sumOf { it.durationMs }.nonZeroOrNull()

        hashCode = 31 * hashCode + rawArtist.hashCode()
        hashCode = 31 * hashCode + songs.hashCode()
    }

    // Note: Append song contents to MusicParent equality so that artists with
    // the same UID but different songs are not equal.
    override fun hashCode() = hashCode

    override fun equals(other: Any?) =
        other is ArtistImpl &&
            uid == other.uid &&
            rawArtist == other.rawArtist &&
            songs == other.songs

    override fun toString() = "Artist(uid=$uid, name=$name)"

    /**
     * Returns the original position of this [Artist]'s [RawArtist] within the given [RawArtist]
     * list. This can be used to create a consistent ordering within child [Artist] lists based on
     * the original tag order.
     *
     * @param rawArtists The [RawArtist] instances to check. It is assumed that this [Artist]'s
     *   [RawArtist] will be within the list.
     * @return The index of the [Artist]'s [RawArtist] within the list.
     */
    fun getOriginalPositionIn(rawArtists: List<RawArtist>) =
        rawArtists.indexOfFirst { it.key == rawArtist.key }

    /**
     * Perform final validation and organization on this instance.
     *
     * @return This instance upcasted to [Artist].
     */
    fun finalize(): Artist {
        check(songs.isNotEmpty() || albums.isNotEmpty()) { "Malformed artist: Empty" }
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
 * @param rawGenre [RawGenre] to derive the member data from.
 * @param musicSettings [MusicSettings] to for user parsing configuration.
 * @param songs Child [SongImpl]s of this instance.
 * @author Alexander Capehart (OxygenCobalt)
 */
class GenreImpl(
    private val rawGenre: RawGenre,
    musicSettings: MusicSettings,
    override val songs: List<SongImpl>
) : Genre {
    override val uid = Music.UID.auxio(MusicMode.GENRES) { update(rawGenre.name) }
    override val name =
        rawGenre.name?.let { Name.Known.from(it, rawGenre.name, musicSettings) }
            ?: Name.Unknown(R.string.def_genre)

    override val artists: List<Artist>
    override val durationMs: Long

    private var hashCode = uid.hashCode()

    init {
        val distinctAlbums = mutableSetOf<Album>()
        val distinctArtists = mutableSetOf<Artist>()
        var totalDuration = 0L

        for (song in songs) {
            song.link(this)
            distinctAlbums.add(song.album)
            distinctArtists.addAll(song.artists)
            totalDuration += song.durationMs
        }

        artists = Sort(Sort.Mode.ByName, Sort.Direction.ASCENDING).artists(distinctArtists)
        durationMs = totalDuration
        hashCode = 31 * hashCode + rawGenre.hashCode()
        hashCode = 31 * hashCode + songs.hashCode()
    }

    override fun hashCode() = hashCode

    override fun equals(other: Any?) =
        other is GenreImpl && uid == other.uid && rawGenre == other.rawGenre && songs == other.songs

    override fun toString() = "Genre(uid=$uid, name=$name)"

    /**
     * Returns the original position of this [Genre]'s [RawGenre] within the given [RawGenre] list.
     * This can be used to create a consistent ordering within child [Genre] lists based on the
     * original tag order.
     *
     * @param rawGenres The [RawGenre] instances to check. It is assumed that this [Genre] 's
     *   [RawGenre] will be within the list.
     * @return The index of the [Genre]'s [RawGenre] within the list.
     */
    fun getOriginalPositionIn(rawGenres: List<RawGenre>) =
        rawGenres.indexOfFirst { it.key == rawGenre.key }

    /**
     * Perform final validation and organization on this instance.
     *
     * @return This instance upcasted to [Genre].
     */
    fun finalize(): Genre {
        check(songs.isNotEmpty()) { "Malformed genre: Empty" }
        return this
    }
}
