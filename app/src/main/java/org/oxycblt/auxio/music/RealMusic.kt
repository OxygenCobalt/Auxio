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
 
package org.oxycblt.auxio.music

import android.content.Context
import androidx.annotation.VisibleForTesting
import java.security.MessageDigest
import java.text.CollationKey
import java.text.Collator
import java.util.UUID
import kotlin.math.max
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.format.Date
import org.oxycblt.auxio.music.format.Disc
import org.oxycblt.auxio.music.format.ReleaseType
import org.oxycblt.auxio.music.library.Sort
import org.oxycblt.auxio.music.parsing.parseId3GenreNames
import org.oxycblt.auxio.music.parsing.parseMultiValue
import org.oxycblt.auxio.music.storage.Directory
import org.oxycblt.auxio.music.storage.MimeType
import org.oxycblt.auxio.music.storage.Path
import org.oxycblt.auxio.music.storage.toAudioUri
import org.oxycblt.auxio.music.storage.toCoverUri
import org.oxycblt.auxio.util.nonZeroOrNull
import org.oxycblt.auxio.util.toUuidOrNull
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * Library-backed implementation of [RealSong].
 * @param raw The [Raw] to derive the member data from.
 * @param musicSettings [MusicSettings] to perform further user-configured parsing.
 * @author Alexander Capehart (OxygenCobalt)
 */
class RealSong(raw: Raw, musicSettings: MusicSettings) : Song {
    override val uid =
    // Attempt to use a MusicBrainz ID first before falling back to a hashed UID.
    raw.musicBrainzId?.toUuidOrNull()?.let { Music.UID.musicBrainz(MusicMode.SONGS, it) }
            ?: Music.UID.auxio(MusicMode.SONGS) {
                // Song UIDs are based on the raw data without parsing so that they remain
                // consistent across music setting changes. Parents are not held up to the
                // same standard since grouping is already inherently linked to settings.
                update(raw.name)
                update(raw.albumName)
                update(raw.date)

                update(raw.track)
                update(raw.disc)

                update(raw.artistNames)
                update(raw.albumArtistNames)
            }
    override val rawName = requireNotNull(raw.name) { "Invalid raw: No title" }
    override val rawSortName = raw.sortName
    override val collationKey = makeCollationKey(this)
    override fun resolveName(context: Context) = rawName

    override val track = raw.track
    override val disc = raw.disc?.let { Disc(it, raw.subtitle) }
    override val date = raw.date
    override val uri = requireNotNull(raw.mediaStoreId) { "Invalid raw: No id" }.toAudioUri()
    override val path =
        Path(
            name = requireNotNull(raw.fileName) { "Invalid raw: No display name" },
            parent = requireNotNull(raw.directory) { "Invalid raw: No parent directory" })
    override val mimeType =
        MimeType(
            fromExtension = requireNotNull(raw.extensionMimeType) { "Invalid raw: No mime type" },
            fromFormat = null)
    override val size = requireNotNull(raw.size) { "Invalid raw: No size" }
    override val durationMs = requireNotNull(raw.durationMs) { "Invalid raw: No duration" }
    override val dateAdded = requireNotNull(raw.dateAdded) { "Invalid raw: No date added" }
    private var _album: RealAlbum? = null
    override val album: Album
        get() = unlikelyToBeNull(_album)

    // Note: Only compare by UID so songs that differ only in MBID are treated differently.
    override fun hashCode() = uid.hashCode()
    override fun equals(other: Any?) = other is Song && uid == other.uid

    private val artistMusicBrainzIds = raw.artistMusicBrainzIds.parseMultiValue(musicSettings)
    private val artistNames = raw.artistNames.parseMultiValue(musicSettings)
    private val artistSortNames = raw.artistSortNames.parseMultiValue(musicSettings)
    private val rawIndividualArtists =
        artistNames.mapIndexed { i, name ->
            RealArtist.Raw(
                artistMusicBrainzIds.getOrNull(i)?.toUuidOrNull(),
                name,
                artistSortNames.getOrNull(i))
        }

    private val albumArtistMusicBrainzIds =
        raw.albumArtistMusicBrainzIds.parseMultiValue(musicSettings)
    private val albumArtistNames = raw.albumArtistNames.parseMultiValue(musicSettings)
    private val albumArtistSortNames = raw.albumArtistSortNames.parseMultiValue(musicSettings)
    private val rawAlbumArtists =
        albumArtistNames.mapIndexed { i, name ->
            RealArtist.Raw(
                albumArtistMusicBrainzIds.getOrNull(i)?.toUuidOrNull(),
                name,
                albumArtistSortNames.getOrNull(i))
        }

    private val _artists = mutableListOf<RealArtist>()
    override val artists: List<Artist>
        get() = _artists
    override fun resolveArtistContents(context: Context) = resolveNames(context, artists)
    override fun areArtistContentsTheSame(other: Song): Boolean {
        for (i in 0 until max(artists.size, other.artists.size)) {
            val a = artists.getOrNull(i) ?: return false
            val b = other.artists.getOrNull(i) ?: return false
            if (a.rawName != b.rawName) {
                return false
            }
        }

        return true
    }

    private val _genres = mutableListOf<RealGenre>()
    override val genres: List<Genre>
        get() = _genres
    override fun resolveGenreContents(context: Context) = resolveNames(context, genres)

    /**
     * The [RealAlbum.Raw] instances collated by the [RealSong]. This can be used to group
     * [RealSong]s into an [RealAlbum].
     */
    val rawAlbum =
        RealAlbum.Raw(
            mediaStoreId = requireNotNull(raw.albumMediaStoreId) { "Invalid raw: No album id" },
            musicBrainzId = raw.albumMusicBrainzId?.toUuidOrNull(),
            name = requireNotNull(raw.albumName) { "Invalid raw: No album name" },
            sortName = raw.albumSortName,
            releaseType = ReleaseType.parse(raw.releaseTypes.parseMultiValue(musicSettings)),
            rawArtists =
                rawAlbumArtists
                    .ifEmpty { rawIndividualArtists }
                    .ifEmpty { listOf(RealArtist.Raw(null, null)) })

    /**
     * The [RealArtist.Raw] instances collated by the [RealSong]. The artists of the song take
     * priority, followed by the album artists. If there are no artists, this field will be a single
     * "unknown" [RealArtist.Raw]. This can be used to group up [RealSong]s into an [RealArtist].
     */
    val rawArtists =
        rawIndividualArtists.ifEmpty { rawAlbumArtists }.ifEmpty { listOf(RealArtist.Raw()) }

    /**
     * The [RealGenre.Raw] instances collated by the [RealSong]. This can be used to group up
     * [RealSong]s into a [RealGenre]. ID3v2 Genre names are automatically converted to their
     * resolved names.
     */
    val rawGenres =
        raw.genreNames
            .parseId3GenreNames(musicSettings)
            .map { RealGenre.Raw(it) }
            .ifEmpty { listOf(RealGenre.Raw()) }

    /**
     * Links this [RealSong] with a parent [RealAlbum].
     * @param album The parent [RealAlbum] to link to.
     */
    fun link(album: RealAlbum) {
        _album = album
    }

    /**
     * Links this [RealSong] with a parent [RealArtist].
     * @param artist The parent [RealArtist] to link to.
     */
    fun link(artist: RealArtist) {
        _artists.add(artist)
    }

    /**
     * Links this [RealSong] with a parent [RealGenre].
     * @param genre The parent [RealGenre] to link to.
     */
    fun link(genre: RealGenre) {
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

    /** Raw information about a [RealSong] obtained from the filesystem/Extractor instances. */
    class Raw
    constructor(
        /**
         * The ID of the [RealSong]'s audio file, obtained from MediaStore. Note that this ID is
         * highly unstable and should only be used for accessing the audio file.
         */
        var mediaStoreId: Long? = null,
        /** @see Song.dateAdded */
        var dateAdded: Long? = null,
        /** The latest date the [RealSong]'s audio file was modified, as a unix epoch timestamp. */
        var dateModified: Long? = null,
        /** @see Song.path */
        var fileName: String? = null,
        /** @see Song.path */
        var directory: Directory? = null,
        /** @see Song.size */
        var size: Long? = null,
        /** @see Song.durationMs */
        var durationMs: Long? = null,
        /** @see Song.mimeType */
        var extensionMimeType: String? = null,
        /** @see Music.UID */
        var musicBrainzId: String? = null,
        /** @see Music.rawName */
        var name: String? = null,
        /** @see Music.rawSortName */
        var sortName: String? = null,
        /** @see Song.track */
        var track: Int? = null,
        /** @see Disc.number */
        var disc: Int? = null,
        /** @See Disc.name */
        var subtitle: String? = null,
        /** @see Song.date */
        var date: Date? = null,
        /** @see RealAlbum.Raw.mediaStoreId */
        var albumMediaStoreId: Long? = null,
        /** @see RealAlbum.Raw.musicBrainzId */
        var albumMusicBrainzId: String? = null,
        /** @see RealAlbum.Raw.name */
        var albumName: String? = null,
        /** @see RealAlbum.Raw.sortName */
        var albumSortName: String? = null,
        /** @see RealAlbum.Raw.releaseType */
        var releaseTypes: List<String> = listOf(),
        /** @see RealArtist.Raw.musicBrainzId */
        var artistMusicBrainzIds: List<String> = listOf(),
        /** @see RealArtist.Raw.name */
        var artistNames: List<String> = listOf(),
        /** @see RealArtist.Raw.sortName */
        var artistSortNames: List<String> = listOf(),
        /** @see RealArtist.Raw.musicBrainzId */
        var albumArtistMusicBrainzIds: List<String> = listOf(),
        /** @see RealArtist.Raw.name */
        var albumArtistNames: List<String> = listOf(),
        /** @see RealArtist.Raw.sortName */
        var albumArtistSortNames: List<String> = listOf(),
        /** @see RealGenre.Raw.name */
        var genreNames: List<String> = listOf()
    )
}

/**
 * Library-backed implementation of [RealAlbum].
 * @param raw The [RealAlbum.Raw] to derive the member data from.
 * @param songs The [RealSong]s that are a part of this [RealAlbum]. These items will be linked to
 * this [RealAlbum].
 * @author Alexander Capehart (OxygenCobalt)
 */
class RealAlbum(val raw: Raw, override val songs: List<RealSong>) : Album {
    override val uid =
    // Attempt to use a MusicBrainz ID first before falling back to a hashed UID.
    raw.musicBrainzId?.let { Music.UID.musicBrainz(MusicMode.ALBUMS, it) }
            ?: Music.UID.auxio(MusicMode.ALBUMS) {
                // Hash based on only names despite the presence of a date to increase stability.
                // I don't know if there is any situation where an artist will have two albums with
                // the exact same name, but if there is, I would love to know.
                update(raw.name)
                update(raw.rawArtists.map { it.name })
            }
    override val rawName = raw.name
    override val rawSortName = raw.sortName
    override val collationKey = makeCollationKey(this)
    override fun resolveName(context: Context) = rawName

    override val dates = Date.Range.from(songs.mapNotNull { it.date })
    override val releaseType = raw.releaseType ?: ReleaseType.Album(null)
    override val coverUri = raw.mediaStoreId.toCoverUri()
    override val durationMs: Long
    override val dateAdded: Long

    // Note: Append song contents to MusicParent equality so that Groups with
    // the same UID but different contents are not equal.
    override fun hashCode() = 31 * uid.hashCode() + songs.hashCode()
    override fun equals(other: Any?) = other is Album && uid == other.uid && songs == other.songs

    private val _artists = mutableListOf<RealArtist>()
    override val artists: List<Artist>
        get() = _artists
    override fun resolveArtistContents(context: Context) = resolveNames(context, artists)
    override fun areArtistContentsTheSame(other: Album): Boolean {
        for (i in 0 until max(artists.size, other.artists.size)) {
            val a = artists.getOrNull(i) ?: return false
            val b = other.artists.getOrNull(i) ?: return false
            if (a.rawName != b.rawName) {
                return false
            }
        }

        return true
    }
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
     * The [RealArtist.Raw] instances collated by the [RealAlbum]. The album artists of the song
     * take priority, followed by the artists. If there are no artists, this field will be a single
     * "unknown" [RealArtist.Raw]. This can be used to group up [RealAlbum]s into an [RealArtist].
     */
    val rawArtists = raw.rawArtists

    /**
     * Links this [RealAlbum] with a parent [RealArtist].
     * @param artist The parent [RealArtist] to link to.
     */
    fun link(artist: RealArtist) {
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

    /** Raw information about an [RealAlbum] obtained from the component [RealSong] instances. */
    class Raw(
        /**
         * The ID of the [RealAlbum]'s grouping, obtained from MediaStore. Note that this ID is
         * highly unstable and should only be used for accessing the system-provided cover art.
         */
        val mediaStoreId: Long,
        /** @see Music.uid */
        val musicBrainzId: UUID?,
        /** @see Music.rawName */
        val name: String,
        /** @see Music.rawSortName */
        val sortName: String?,
        /** @see Album.releaseType */
        val releaseType: ReleaseType?,
        /** @see RealArtist.Raw.name */
        val rawArtists: List<RealArtist.Raw>
    ) {
        // Albums are grouped as follows:
        // - If we have a MusicBrainz ID, only group by it. This allows different Albums with the
        // same name to be differentiated, which is common in large libraries.
        // - If we do not have a MusicBrainz ID, compare by the lowercase album name and lowercase
        // artist name. This allows for case-insensitive artist/album grouping, which can be common
        // for albums/artists that have different naming (ex. "RAMMSTEIN" vs. "Rammstein").

        // Cache the hash-code for HashMap efficiency.
        private val hashCode =
            musicBrainzId?.hashCode() ?: (31 * name.lowercase().hashCode() + rawArtists.hashCode())

        override fun hashCode() = hashCode

        override fun equals(other: Any?) =
            other is Raw &&
                when {
                    musicBrainzId != null && other.musicBrainzId != null ->
                        musicBrainzId == other.musicBrainzId
                    musicBrainzId == null && other.musicBrainzId == null ->
                        name.equals(other.name, true) && rawArtists == other.rawArtists
                    else -> false
                }
    }
}

/**
 * Library-backed implementation of [RealArtist].
 * @param raw The [RealArtist.Raw] to derive the member data from.
 * @param songAlbums A list of the [RealSong]s and [RealAlbum]s that are a part of this [RealArtist]
 * , either through artist or album artist tags. Providing [RealSong]s to the artist is optional.
 * These instances will be linked to this [RealArtist].
 * @author Alexander Capehart (OxygenCobalt)
 */
class RealArtist constructor(private val raw: Raw, songAlbums: List<Music>) : Artist {
    override val uid =
    // Attempt to use a MusicBrainz ID first before falling back to a hashed UID.
    raw.musicBrainzId?.let { Music.UID.musicBrainz(MusicMode.ARTISTS, it) }
            ?: Music.UID.auxio(MusicMode.ARTISTS) { update(raw.name) }
    override val rawName = raw.name
    override val rawSortName = raw.sortName
    override val collationKey = makeCollationKey(this)
    override fun resolveName(context: Context) = rawName ?: context.getString(R.string.def_artist)
    override val songs: List<Song>

    override val albums: List<Album>
    override val durationMs: Long?
    override val isCollaborator: Boolean

    // Note: Append song contents to MusicParent equality so that Groups with
    // the same UID but different contents are not equal.
    override fun hashCode() = 31 * uid.hashCode() + songs.hashCode()
    override fun equals(other: Any?) = other is Album && uid == other.uid && songs == other.songs

    override lateinit var genres: List<Genre>
    override fun resolveGenreContents(context: Context) = resolveNames(context, genres)
    override fun areGenreContentsTheSame(other: Artist): Boolean {
        for (i in 0 until max(genres.size, other.genres.size)) {
            val a = genres.getOrNull(i) ?: return false
            val b = other.genres.getOrNull(i) ?: return false
            if (a.rawName != b.rawName) {
                return false
            }
        }

        return true
    }

    init {
        val distinctSongs = mutableSetOf<Song>()
        val distinctAlbums = mutableSetOf<Album>()

        var noAlbums = true

        for (music in songAlbums) {
            when (music) {
                is RealSong -> {
                    music.link(this)
                    distinctSongs.add(music)
                    distinctAlbums.add(music.album)
                }
                is RealAlbum -> {
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
     * Returns the original position of this [RealArtist]'s [RealArtist.Raw] within the given
     * [RealArtist.Raw] list. This can be used to create a consistent ordering within child
     * [RealArtist] lists based on the original tag order.
     * @param rawArtists The [RealArtist.Raw] instances to check. It is assumed that this
     * [RealArtist]'s [RealArtist.Raw] will be within the list.
     * @return The index of the [RealArtist]'s [RealArtist.Raw] within the list.
     */
    fun getOriginalPositionIn(rawArtists: List<Raw>) = rawArtists.indexOf(raw)

    /**
     * Perform final validation and organization on this instance.
     * @return This instance upcasted to [Artist].
     */
    fun finalize(): Artist {
        check(songs.isNotEmpty() || albums.isNotEmpty()) { "Malformed artist: Empty" }
        genres =
            Sort(Sort.Mode.ByName, true)
                .genres(songs.flatMapTo(mutableSetOf()) { it.genres })
                .sortedByDescending { genre -> songs.count { it.genres.contains(genre) } }
        return this
    }

    /**
     * Raw information about an [RealArtist] obtained from the component [RealSong] and [RealAlbum]
     * instances.
     */
    class Raw(
        /** @see Music.UID */
        val musicBrainzId: UUID? = null,
        /** @see Music.rawName */
        val name: String? = null,
        /** @see Music.rawSortName */
        val sortName: String? = null
    ) {
        // Artists are grouped as follows:
        // - If we have a MusicBrainz ID, only group by it. This allows different Artists with the
        // same name to be differentiated, which is common in large libraries.
        // - If we do not have a MusicBrainz ID, compare by the lowercase name. This allows artist
        // grouping to be case-insensitive.

        // Cache the hashCode for HashMap efficiency.
        private val hashCode = musicBrainzId?.hashCode() ?: name?.lowercase().hashCode()

        // Compare names and MusicBrainz IDs in order to differentiate artists with the
        // same name in large libraries.

        override fun hashCode() = hashCode

        override fun equals(other: Any?) =
            other is Raw &&
                when {
                    musicBrainzId != null && other.musicBrainzId != null ->
                        musicBrainzId == other.musicBrainzId
                    musicBrainzId == null && other.musicBrainzId == null ->
                        when {
                            name != null && other.name != null -> name.equals(other.name, true)
                            name == null && other.name == null -> true
                            else -> false
                        }
                    else -> false
                }
    }
}
/**
 * Library-backed implementation of [RealGenre].
 * @author Alexander Capehart (OxygenCobalt)
 */
class RealGenre constructor(private val raw: Raw, override val songs: List<RealSong>) : Genre {
    override val uid = Music.UID.auxio(MusicMode.GENRES) { update(raw.name) }
    override val rawName = raw.name
    override val rawSortName = rawName
    override val collationKey = makeCollationKey(this)
    override fun resolveName(context: Context) = rawName ?: context.getString(R.string.def_genre)

    override val albums: List<Album>
    override val artists: List<Artist>
    override val durationMs: Long

    // Note: Append song contents to MusicParent equality so that Groups with
    // the same UID but different contents are not equal.
    override fun hashCode() = 31 * uid.hashCode() + songs.hashCode()
    override fun equals(other: Any?) = other is Album && uid == other.uid && songs == other.songs

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
            Sort(Sort.Mode.ByName, true).albums(distinctAlbums).sortedByDescending { album ->
                album.songs.count { it.genres.contains(this) }
            }
        artists = Sort(Sort.Mode.ByName, true).artists(distinctArtists)
        durationMs = totalDuration
    }

    /**
     * Returns the original position of this [RealGenre]'s [RealGenre.Raw] within the given
     * [RealGenre.Raw] list. This can be used to create a consistent ordering within child
     * [RealGenre] lists based on the original tag order.
     * @param rawGenres The [RealGenre.Raw] instances to check. It is assumed that this [RealGenre]
     * 's [RealGenre.Raw] will be within the list.
     * @return The index of the [RealGenre]'s [RealGenre.Raw] within the list.
     */
    fun getOriginalPositionIn(rawGenres: List<Raw>) = rawGenres.indexOf(raw)

    /**
     * Perform final validation and organization on this instance.
     * @return This instance upcasted to [Genre].
     */
    fun finalize(): Music {
        check(songs.isNotEmpty()) { "Malformed genre: Empty" }
        return this
    }

    /** Raw information about a [RealGenre] obtained from the component [RealSong] instances. */
    class Raw(
        /** @see Music.rawName */
        val name: String? = null
    ) {
        // Only group by the lowercase genre name. This allows Genre grouping to be
        // case-insensitive, which may be helpful in some libraries with different ways of
        // formatting genres.

        // Cache the hashCode for HashMap efficiency.
        private val hashCode = name?.lowercase().hashCode()

        override fun hashCode() = hashCode

        override fun equals(other: Any?) =
            other is Raw &&
                when {
                    name != null && other.name != null -> name.equals(other.name, true)
                    name == null && other.name == null -> true
                    else -> false
                }
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

/**
 * Join a list of [Music]'s resolved names into a string in a localized manner, using
 * [R.string.fmt_list].
 * @param context [Context] required to obtain localized formatting.
 * @param values The list of [Music] to format.
 * @return A single string consisting of the values delimited by a localized separator.
 */
private fun resolveNames(context: Context, values: List<Music>): String {
    if (values.isEmpty()) {
        // Nothing to do.
        return ""
    }

    var joined = values.first().resolveName(context)
    for (i in 1..values.lastIndex) {
        // Chain all previous values with the next value in the list with another delimiter.
        joined = context.getString(R.string.fmt_list, joined, values[i].resolveName(context))
    }
    return joined
}

/** Cached collator instance re-used with [makeCollationKey]. */
private val COLLATOR: Collator = Collator.getInstance().apply { strength = Collator.PRIMARY }

/**
 * Provided implementation to create a [CollationKey] in the way described by [Music.collationKey].
 * This should be used in all overrides of all [CollationKey].
 * @param music The [Music] to create the [CollationKey] for.
 * @return A [CollationKey] that follows the specification described by [Music.collationKey].
 */
private fun makeCollationKey(music: Music): CollationKey? {
    val sortName =
        (music.rawSortName ?: music.rawName)?.run {
            when {
                length > 5 && startsWith("the ", ignoreCase = true) -> substring(4)
                length > 4 && startsWith("an ", ignoreCase = true) -> substring(3)
                length > 3 && startsWith("a ", ignoreCase = true) -> substring(2)
                else -> this
            }
        }

    return COLLATOR.getCollationKey(sortName)
}
