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
import org.oxycblt.auxio.image.extractor.Cover
import org.oxycblt.auxio.image.extractor.ParentCover
import org.oxycblt.auxio.list.sort.Sort
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicType
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.fs.MimeType
import org.oxycblt.auxio.music.fs.toAlbumCoverUri
import org.oxycblt.auxio.music.fs.toAudioUri
import org.oxycblt.auxio.music.fs.toSongCoverUri
import org.oxycblt.auxio.music.info.Date
import org.oxycblt.auxio.music.info.Disc
import org.oxycblt.auxio.music.info.Name
import org.oxycblt.auxio.music.info.ReleaseType
import org.oxycblt.auxio.music.metadata.Separators
import org.oxycblt.auxio.music.metadata.parseId3GenreNames
import org.oxycblt.auxio.playback.replaygain.ReplayGainAdjustment
import org.oxycblt.auxio.util.positiveOrNull
import org.oxycblt.auxio.util.toUuidOrNull
import org.oxycblt.auxio.util.unlikelyToBeNull
import org.oxycblt.auxio.util.update

/**
 * Library-backed implementation of [Song].
 *
 * @param rawSong The [RawSong] to derive the member data from.
 * @param nameFactory The [Name.Known.Factory] to interpret name information with.
 * @param separators The [Separators] to parse multi-value tags with.
 * @author Alexander Capehart (OxygenCobalt)
 */
class SongImpl(
    private val rawSong: RawSong,
    private val nameFactory: Name.Known.Factory,
    private val separators: Separators
) : Song {
    override val uid =
        // Attempt to use a MusicBrainz ID first before falling back to a hashed UID.
        rawSong.musicBrainzId?.toUuidOrNull()?.let { Music.UID.musicBrainz(MusicType.SONGS, it) }
            ?: Music.UID.auxio(MusicType.SONGS) {
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
        nameFactory.parse(
            requireNotNull(rawSong.name) { "Invalid raw ${rawSong.path}: No title" },
            rawSong.sortName)

    override val track = rawSong.track
    override val disc = rawSong.disc?.let { Disc(it, rawSong.subtitle) }
    override val date = rawSong.date
    override val uri =
        requireNotNull(rawSong.mediaStoreId) { "Invalid raw ${rawSong.path}: No id" }.toAudioUri()
    override val path = requireNotNull(rawSong.path) { "Invalid raw ${rawSong.path}: No path" }
    override val mimeType =
        MimeType(
            fromExtension =
                requireNotNull(rawSong.extensionMimeType) {
                    "Invalid raw ${rawSong.path}: No mime type"
                },
            fromFormat = null)
    override val size = requireNotNull(rawSong.size) { "Invalid raw ${rawSong.path}: No size" }
    override val durationMs =
        requireNotNull(rawSong.durationMs) { "Invalid raw ${rawSong.path}: No duration" }
    override val replayGainAdjustment =
        ReplayGainAdjustment(
            track = rawSong.replayGainTrackAdjustment, album = rawSong.replayGainAlbumAdjustment)

    override val dateAdded =
        requireNotNull(rawSong.dateAdded) { "Invalid raw ${rawSong.path}: No date added" }

    private var _album: AlbumImpl? = null
    override val album: Album
        get() = unlikelyToBeNull(_album)

    private val _artists = mutableListOf<ArtistImpl>()
    override val artists: List<Artist>
        get() = _artists

    private val _genres = mutableListOf<GenreImpl>()
    override val genres: List<Genre>
        get() = _genres

    override val cover =
        rawSong.coverPerceptualHash?.let {
            // We were able to confirm that the song had a parsable cover and can be used on
            // a per-song basis. Otherwise, just fall back to a per-album cover instead, as
            // it implies either a cover.jpg pattern is used (likely) or ExoPlayer does not
            // support the cover metadata of a given spec (unlikely).
            Cover.Embedded(
                requireNotNull(rawSong.mediaStoreId) { "Invalid raw ${rawSong.path}: No id" }
                    .toSongCoverUri(),
                uri,
                it)
        }
            ?: Cover.External(requireNotNull(rawSong.albumMediaStoreId).toAlbumCoverUri())

    /**
     * The [RawAlbum] instances collated by the [Song]. This can be used to group [Song]s into an
     * [Album].
     */
    val rawAlbum: RawAlbum

    /**
     * The [RawArtist] instances collated by the [Song]. The artists of the song take priority,
     * followed by the album artists. If there are no artists, this field will be a single "unknown"
     * [RawArtist]. This can be used to group up [Song]s into an [Artist].
     */
    val rawArtists: List<RawArtist>

    /**
     * The [RawGenre] instances collated by the [Song]. This can be used to group up [Song]s into a
     * [Genre]. ID3v2 Genre names are automatically converted to their resolved names.
     */
    val rawGenres: List<RawGenre>

    private var hashCode: Int = uid.hashCode()

    init {
        val artistMusicBrainzIds = separators.split(rawSong.artistMusicBrainzIds)
        val artistNames = separators.split(rawSong.artistNames)
        val artistSortNames = separators.split(rawSong.artistSortNames)
        val rawIndividualArtists =
            artistNames
                .mapIndexed { i, name ->
                    RawArtist(
                        artistMusicBrainzIds.getOrNull(i)?.toUuidOrNull(),
                        name,
                        artistSortNames.getOrNull(i))
                }
                .distinctBy { it.key }

        val albumArtistMusicBrainzIds = separators.split(rawSong.albumArtistMusicBrainzIds)
        val albumArtistNames = separators.split(rawSong.albumArtistNames)
        val albumArtistSortNames = separators.split(rawSong.albumArtistSortNames)
        val rawAlbumArtists =
            albumArtistNames
                .mapIndexed { i, name ->
                    RawArtist(
                        albumArtistMusicBrainzIds.getOrNull(i)?.toUuidOrNull(),
                        name,
                        albumArtistSortNames.getOrNull(i))
                }
                .distinctBy { it.key }

        rawAlbum =
            RawAlbum(
                mediaStoreId =
                    requireNotNull(rawSong.albumMediaStoreId) {
                        "Invalid raw ${rawSong.path}: No album id"
                    },
                musicBrainzId = rawSong.albumMusicBrainzId?.toUuidOrNull(),
                name =
                    requireNotNull(rawSong.albumName) {
                        "Invalid raw ${rawSong.path}: No album name"
                    },
                sortName = rawSong.albumSortName,
                releaseType = ReleaseType.parse(separators.split(rawSong.releaseTypes)),
                rawArtists =
                    rawAlbumArtists
                        .ifEmpty { rawIndividualArtists }
                        .ifEmpty { listOf(RawArtist()) })

        rawArtists =
            rawIndividualArtists.ifEmpty { rawAlbumArtists }.ifEmpty { listOf(RawArtist()) }

        val genreNames =
            (rawSong.genreNames.parseId3GenreNames() ?: separators.split(rawSong.genreNames))
        rawGenres =
            genreNames.map { RawGenre(it) }.distinctBy { it.key }.ifEmpty { listOf(RawGenre()) }

        hashCode = 31 * hashCode + rawSong.hashCode()
        hashCode = 31 * hashCode + nameFactory.hashCode()
    }

    override fun hashCode() = hashCode

    // Since equality on public-facing music models is not identical to the tag equality,
    // we just compare raw instances and how they are interpreted.
    override fun equals(other: Any?) =
        other is SongImpl &&
            uid == other.uid &&
            nameFactory == other.nameFactory &&
            separators == other.separators &&
            rawSong == other.rawSong

    override fun toString() = "Song(uid=$uid, name=$name)"

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
        checkNotNull(_album) { "Malformed song ${path}: No album" }

        check(_artists.isNotEmpty()) { "Malformed song ${path}: No artists" }
        check(_artists.size == rawArtists.size) {
            "Malformed song ${path}: Artist grouping mismatch"
        }
        for (i in _artists.indices) {
            // Non-destructively reorder the linked artists so that they align with
            // the artist ordering within the song metadata.
            val newIdx = _artists[i].getOriginalPositionIn(rawArtists)
            val other = _artists[newIdx]
            _artists[newIdx] = _artists[i]
            _artists[i] = other
        }

        check(_genres.isNotEmpty()) { "Malformed song ${path}: No genres" }
        check(_genres.size == rawGenres.size) { "Malformed song ${path}: Genre grouping mismatch" }
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
 * @param grouping [Grouping] to derive the member data from.
 * @param nameFactory The [Name.Known.Factory] to interpret name information with.
 * @author Alexander Capehart (OxygenCobalt)
 */
class AlbumImpl(
    grouping: Grouping<RawAlbum, SongImpl>,
    private val nameFactory: Name.Known.Factory
) : Album {
    private val rawAlbum = grouping.raw.inner

    override val uid =
        // Attempt to use a MusicBrainz ID first before falling back to a hashed UID.
        rawAlbum.musicBrainzId?.let { Music.UID.musicBrainz(MusicType.ALBUMS, it) }
            ?: Music.UID.auxio(MusicType.ALBUMS) {
                // Hash based on only names despite the presence of a date to increase stability.
                // I don't know if there is any situation where an artist will have two albums with
                // the exact same name, but if there is, I would love to know.
                update(rawAlbum.name)
                update(rawAlbum.rawArtists.map { it.name })
            }
    override val name = nameFactory.parse(rawAlbum.name, rawAlbum.sortName)
    override val dates: Date.Range?
    override val releaseType = rawAlbum.releaseType ?: ReleaseType.Album(null)
    override val durationMs: Long
    override val dateAdded: Long
    override val cover: ParentCover

    private val _artists = mutableListOf<ArtistImpl>()
    override val artists: List<Artist>
        get() = _artists

    override val songs: Set<Song> = grouping.music

    private var hashCode = uid.hashCode()

    init {
        var totalDuration: Long = 0
        var minDate: Date? = null
        var maxDate: Date? = null
        var earliestDateAdded: Long = Long.MAX_VALUE

        // Do linking and value generation in the same loop for efficiency.
        for (song in grouping.music) {
            song.link(this)

            if (song.date != null) {
                val min = minDate
                if (min == null || song.date < min) {
                    minDate = song.date
                }

                val max = maxDate
                if (max == null || song.date > max) {
                    maxDate = song.date
                }
            }

            if (song.dateAdded < earliestDateAdded) {
                earliestDateAdded = song.dateAdded
            }
            totalDuration += song.durationMs
        }

        val min = minDate
        val max = maxDate
        dates = if (min != null && max != null) Date.Range(min, max) else null
        durationMs = totalDuration
        dateAdded = earliestDateAdded

        cover = ParentCover.from(grouping.raw.src.cover, songs)

        hashCode = 31 * hashCode + rawAlbum.hashCode()
        hashCode = 31 * hashCode + nameFactory.hashCode()
        hashCode = 31 * hashCode + songs.hashCode()
    }

    override fun hashCode() = hashCode

    // Since equality on public-facing music models is not identical to the tag equality,
    // we just compare raw instances and how they are interpreted.
    override fun equals(other: Any?) =
        other is AlbumImpl &&
            uid == other.uid &&
            rawAlbum == other.rawAlbum &&
            nameFactory == other.nameFactory &&
            songs == other.songs

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
        check(songs.isNotEmpty()) { "Malformed album $name: Empty" }
        check(_artists.isNotEmpty()) { "Malformed album $name: No artists" }
        check(_artists.size == rawArtists.size) {
            "Malformed album $name: Artist grouping mismatch"
        }
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
 * @param grouping [Grouping] to derive the member data from.
 * @param nameFactory The [Name.Known.Factory] to interpret name information with.
 * @author Alexander Capehart (OxygenCobalt)
 */
class ArtistImpl(
    grouping: Grouping<RawArtist, Music>,
    private val nameFactory: Name.Known.Factory
) : Artist {
    private val rawArtist = grouping.raw.inner

    override val uid =
        // Attempt to use a MusicBrainz ID first before falling back to a hashed UID.
        rawArtist.musicBrainzId?.let { Music.UID.musicBrainz(MusicType.ARTISTS, it) }
            ?: Music.UID.auxio(MusicType.ARTISTS) { update(rawArtist.name) }
    override val name =
        rawArtist.name?.let { nameFactory.parse(it, rawArtist.sortName) }
            ?: Name.Unknown(R.string.def_artist)

    override val songs: Set<Song>
    override val explicitAlbums: Set<Album>
    override val implicitAlbums: Set<Album>
    override val durationMs: Long?
    override val cover: ParentCover

    override lateinit var genres: List<Genre>

    private var hashCode = uid.hashCode()

    init {
        val distinctSongs = mutableSetOf<Song>()
        val albumMap = mutableMapOf<Album, Boolean>()

        for (music in grouping.music) {
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
                else -> error("Unexpected input music $music in $name ${music::class.simpleName}")
            }
        }

        songs = distinctSongs
        val albums = albumMap.keys
        explicitAlbums = albums.filterTo(mutableSetOf()) { albumMap[it] == true }
        implicitAlbums = albums.filterNotTo(mutableSetOf()) { albumMap[it] == true }
        durationMs = songs.sumOf { it.durationMs }.positiveOrNull()

        val singleCover =
            when (val src = grouping.raw.src) {
                is SongImpl -> src.cover
                is AlbumImpl -> src.cover.single
                else -> error("Unexpected input source $src in $name ${src::class.simpleName}")
            }
        cover = ParentCover.from(singleCover, songs)

        hashCode = 31 * hashCode + rawArtist.hashCode()
        hashCode = 31 * hashCode + nameFactory.hashCode()
        hashCode = 31 * hashCode + songs.hashCode()
    }

    // Note: Append song contents to MusicParent equality so that artists with
    // the same UID but different songs are not equal.
    override fun hashCode() = hashCode

    // Since equality on public-facing music models is not identical to the tag equality,
    // we just compare raw instances and how they are interpreted.
    override fun equals(other: Any?) =
        other is ArtistImpl &&
            uid == other.uid &&
            rawArtist == other.rawArtist &&
            nameFactory == other.nameFactory &&
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
 * @param grouping [Grouping] to derive the member data from.
 * @param nameFactory The [Name.Known.Factory] to interpret name information with.
 * @author Alexander Capehart (OxygenCobalt)
 */
class GenreImpl(
    grouping: Grouping<RawGenre, SongImpl>,
    private val nameFactory: Name.Known.Factory
) : Genre {
    private val rawGenre = grouping.raw.inner

    override val uid = Music.UID.auxio(MusicType.GENRES) { update(rawGenre.name) }
    override val name =
        rawGenre.name?.let { nameFactory.parse(it, rawGenre.name) }
            ?: Name.Unknown(R.string.def_genre)

    override val songs: Set<Song>
    override val artists: Set<Artist>
    override val durationMs: Long
    override val cover: ParentCover

    private var hashCode = uid.hashCode()

    init {
        val distinctArtists = mutableSetOf<Artist>()
        var totalDuration = 0L

        for (song in grouping.music) {
            song.link(this)
            distinctArtists.addAll(song.artists)
            totalDuration += song.durationMs
        }

        songs = grouping.music
        artists = distinctArtists
        durationMs = totalDuration

        cover = ParentCover.from(grouping.raw.src.cover, songs)

        hashCode = 31 * hashCode + rawGenre.hashCode()
        hashCode = 31 * hashCode + nameFactory.hashCode()
        hashCode = 31 * hashCode + songs.hashCode()
    }

    override fun hashCode() = hashCode

    override fun equals(other: Any?) =
        other is GenreImpl &&
            uid == other.uid &&
            rawGenre == other.rawGenre &&
            nameFactory == other.nameFactory &&
            songs == other.songs

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
        check(songs.isNotEmpty()) { "Malformed genre $name: Empty" }
        return this
    }
}
