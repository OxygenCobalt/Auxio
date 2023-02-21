/*
 * Copyright (c) 2023 Auxio Project
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
 
package org.oxycblt.auxio.music.model

import android.content.Context
import androidx.annotation.VisibleForTesting
import java.security.MessageDigest
import java.text.CollationKey
import java.text.Collator
import org.oxycblt.auxio.R
import org.oxycblt.auxio.list.Sort
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.metadata.Date
import org.oxycblt.auxio.music.metadata.Disc
import org.oxycblt.auxio.music.metadata.ReleaseType
import org.oxycblt.auxio.music.metadata.parseId3GenreNames
import org.oxycblt.auxio.music.metadata.parseMultiValue
import org.oxycblt.auxio.music.storage.MimeType
import org.oxycblt.auxio.music.storage.Path
import org.oxycblt.auxio.music.storage.toAudioUri
import org.oxycblt.auxio.music.storage.toCoverUri
import org.oxycblt.auxio.util.nonZeroOrNull
import org.oxycblt.auxio.util.toUuidOrNull
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * Library-backed implementation of [Song].
 * @param rawSong The [RawSong] to derive the member data from.
 * @param musicSettings [MusicSettings] to for user parsing configuration.
 * @author Alexander Capehart (OxygenCobalt)
 */
class SongImpl(rawSong: RawSong, musicSettings: MusicSettings) : Song {
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
    override val rawName = requireNotNull(rawSong.name) { "Invalid raw: No title" }
    override val rawSortName = rawSong.sortName
    override val collationKey = makeCollationKey(musicSettings)
    override fun resolveName(context: Context) = rawName

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

    // Note: Only compare by UID so songs that differ only in MBID are treated differently.
    override fun hashCode() = uid.hashCode()
    override fun equals(other: Any?) = other is Song && uid == other.uid

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
     * @param album The parent [Album] to link to.
     */
    fun link(album: AlbumImpl) {
        _album = album
    }

    /**
     * Links this [Song] with a parent [Artist].
     * @param artist The parent [Artist] to link to.
     */
    fun link(artist: ArtistImpl) {
        _artists.add(artist)
    }

    /**
     * Links this [Song] with a parent [Genre].
     * @param genre The parent [Genre] to link to.
     */
    fun link(genre: GenreImpl) {
        _genres.add(genre)
    }

    /**
     * Perform final validation and organization on this instance.
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
 * @param rawAlbum The [RawAlbum] to derive the member data from.
 * @param musicSettings [MusicSettings] to for user parsing configuration.
 * @param songs The [Song]s that are a part of this [Album]. These items will be linked to this
 * [Album].
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
    override val rawName = rawAlbum.name
    override val rawSortName = rawAlbum.sortName
    override val collationKey = makeCollationKey(musicSettings)
    override fun resolveName(context: Context) = rawName

    override val dates = Date.Range.from(songs.mapNotNull { it.date })
    override val releaseType = rawAlbum.releaseType ?: ReleaseType.Album(null)
    override val coverUri = rawAlbum.mediaStoreId.toCoverUri()
    override val durationMs: Long
    override val dateAdded: Long

    // Note: Append song contents to MusicParent equality so that Groups with
    // the same UID but different contents are not equal.
    override fun hashCode() = 31 * uid.hashCode() + songs.hashCode()
    override fun equals(other: Any?) =
        other is AlbumImpl && uid == other.uid && songs == other.songs

    private val _artists = mutableListOf<ArtistImpl>()
    override val artists: List<Artist>
        get() = _artists

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
    }

    /**
     * The [RawArtist] instances collated by the [Album]. The album artists of the song take
     * priority, followed by the artists. If there are no artists, this field will be a single
     * "unknown" [RawArtist]. This can be used to group up [Album]s into an [Artist].
     */
    val rawArtists = rawAlbum.rawArtists

    /**
     * Links this [Album] with a parent [Artist].
     * @param artist The parent [Artist] to link to.
     */
    fun link(artist: ArtistImpl) {
        _artists.add(artist)
    }

    /**
     * Perform final validation and organization on this instance.
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
 * @param rawArtist The [RawArtist] to derive the member data from.
 * @param musicSettings [MusicSettings] to for user parsing configuration.
 * @param songAlbums A list of the [Song]s and [Album]s that are a part of this [Artist] , either
 * through artist or album artist tags. Providing [Song]s to the artist is optional. These instances
 * will be linked to this [Artist].
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
    override val rawName = rawArtist.name
    override val rawSortName = rawArtist.sortName
    override val collationKey = makeCollationKey(musicSettings)
    override fun resolveName(context: Context) = rawName ?: context.getString(R.string.def_artist)
    override val songs: List<Song>

    override val albums: List<Album>
    override val durationMs: Long?
    override val isCollaborator: Boolean

    // Note: Append song contents to MusicParent equality so that Groups with
    // the same UID but different contents are not equal.
    override fun hashCode() = 31 * uid.hashCode() + songs.hashCode()
    override fun equals(other: Any?) =
        other is ArtistImpl && uid == other.uid && songs == other.songs

    override lateinit var genres: List<Genre>

    init {
        val distinctSongs = mutableSetOf<Song>()
        val distinctAlbums = mutableSetOf<Album>()

        var noAlbums = true

        for (music in songAlbums) {
            when (music) {
                is SongImpl -> {
                    music.link(this)
                    distinctSongs.add(music)
                    distinctAlbums.add(music.album)
                }
                is AlbumImpl -> {
                    music.link(this)
                    distinctAlbums.add(music)
                    noAlbums = false
                }
                else -> error("Unexpected input music ${music::class.simpleName}")
            }
        }

        songs = distinctSongs.toList()
        albums = distinctAlbums.toList()
        durationMs = songs.sumOf { it.durationMs }.nonZeroOrNull()
        isCollaborator = noAlbums
    }

    /**
     * Returns the original position of this [Artist]'s [RawArtist] within the given [RawArtist]
     * list. This can be used to create a consistent ordering within child [Artist] lists based on
     * the original tag order.
     * @param rawArtists The [RawArtist] instances to check. It is assumed that this [Artist]'s
     * [RawArtist] will be within the list.
     * @return The index of the [Artist]'s [RawArtist] within the list.
     */
    fun getOriginalPositionIn(rawArtists: List<RawArtist>) = rawArtists.indexOf(rawArtist)

    /**
     * Perform final validation and organization on this instance.
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
    override val rawName = rawGenre.name
    override val rawSortName = rawName
    override val collationKey = makeCollationKey(musicSettings)
    override fun resolveName(context: Context) = rawName ?: context.getString(R.string.def_genre)

    override val albums: List<Album>
    override val artists: List<Artist>
    override val durationMs: Long

    // Note: Append song contents to MusicParent equality so that Groups with
    // the same UID but different contents are not equal.
    override fun hashCode() = 31 * uid.hashCode() + songs.hashCode()
    override fun equals(other: Any?) =
        other is GenreImpl && uid == other.uid && songs == other.songs

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

        albums =
            Sort(Sort.Mode.ByName, Sort.Direction.ASCENDING)
                .albums(distinctAlbums)
                .sortedByDescending { album -> album.songs.count { it.genres.contains(this) } }
        artists = Sort(Sort.Mode.ByName, Sort.Direction.ASCENDING).artists(distinctArtists)
        durationMs = totalDuration
    }

    /**
     * Returns the original position of this [Genre]'s [RawGenre] within the given [RawGenre] list.
     * This can be used to create a consistent ordering within child [Genre] lists based on the
     * original tag order.
     * @param rawGenres The [RawGenre] instances to check. It is assumed that this [Genre] 's
     * [RawGenre] will be within the list.
     * @return The index of the [Genre]'s [RawGenre] within the list.
     */
    fun getOriginalPositionIn(rawGenres: List<RawGenre>) = rawGenres.indexOf(rawGenre)

    /**
     * Perform final validation and organization on this instance.
     * @return This instance upcasted to [Genre].
     */
    fun finalize(): Music {
        check(songs.isNotEmpty()) { "Malformed genre: Empty" }
        return this
    }
}

/**
 * Update a [MessageDigest] with a lowercase [String].
 * @param string The [String] to hash. If null, it will not be hashed.
 */
@VisibleForTesting
fun MessageDigest.update(string: String?) {
    if (string != null) {
        update(string.lowercase().toByteArray())
    } else {
        update(0)
    }
}

/**
 * Update a [MessageDigest] with the string representation of a [Date].
 * @param date The [Date] to hash. If null, nothing will be done.
 */
@VisibleForTesting
fun MessageDigest.update(date: Date?) {
    if (date != null) {
        update(date.toString().toByteArray())
    } else {
        update(0)
    }
}

/**
 * Update a [MessageDigest] with the lowercase versions of all of the input [String]s.
 * @param strings The [String]s to hash. If a [String] is null, it will not be hashed.
 */
@VisibleForTesting
fun MessageDigest.update(strings: List<String?>) {
    strings.forEach(::update)
}

/**
 * Update a [MessageDigest] with the little-endian bytes of a [Int].
 * @param n The [Int] to write. If null, nothing will be done.
 */
@VisibleForTesting
fun MessageDigest.update(n: Int?) {
    if (n != null) {
        update(byteArrayOf(n.toByte(), n.shr(8).toByte(), n.shr(16).toByte(), n.shr(24).toByte()))
    } else {
        update(0)
    }
}

/** Cached collator instance re-used with [makeCollationKey]. */
private val COLLATOR: Collator = Collator.getInstance().apply { strength = Collator.PRIMARY }

/**
 * Provided implementation to create a [CollationKey] in the way described by [Music.collationKey].
 * This should be used in all overrides of all [CollationKey].
 * @param musicSettings [MusicSettings] required for user parsing configuration.
 * @return A [CollationKey] that follows the specification described by [Music.collationKey].
 */
private fun Music.makeCollationKey(musicSettings: MusicSettings): CollationKey? {
    var sortName = (rawSortName ?: rawName) ?: return null

    if (musicSettings.automaticSortNames) {
        sortName =
            sortName.run {
                when {
                    length > 5 && startsWith("the ", ignoreCase = true) -> substring(4)
                    length > 4 && startsWith("an ", ignoreCase = true) -> substring(3)
                    length > 3 && startsWith("a ", ignoreCase = true) -> substring(2)
                    else -> this
                }
            }
    }

    return COLLATOR.getCollationKey(sortName)
}
