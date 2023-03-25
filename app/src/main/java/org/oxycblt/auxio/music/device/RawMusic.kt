/*
 * Copyright (c) 2023 Auxio Project
 * RawMusic.kt is part of Auxio.
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

import java.util.UUID
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.music.fs.Directory
import org.oxycblt.auxio.music.metadata.*

/**
 * Raw information about a [SongImpl] obtained from the filesystem/Extractor instances.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class RawSong(
    /**
     * The ID of the [SongImpl]'s audio file, obtained from MediaStore. Note that this ID is highly
     * unstable and should only be used for accessing the audio file.
     */
    var mediaStoreId: Long? = null,
    /** @see Song.dateAdded */
    var dateAdded: Long? = null,
    /** The latest date the [SongImpl]'s audio file was modified, as a unix epoch timestamp. */
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
    /** @see RawAlbum.mediaStoreId */
    var albumMediaStoreId: Long? = null,
    /** @see RawAlbum.musicBrainzId */
    var albumMusicBrainzId: String? = null,
    /** @see RawAlbum.name */
    var albumName: String? = null,
    /** @see RawAlbum.sortName */
    var albumSortName: String? = null,
    /** @see RawAlbum.releaseType */
    var releaseTypes: List<String> = listOf(),
    /** @see RawArtist.musicBrainzId */
    var artistMusicBrainzIds: List<String> = listOf(),
    /** @see RawArtist.name */
    var artistNames: List<String> = listOf(),
    /** @see RawArtist.sortName */
    var artistSortNames: List<String> = listOf(),
    /** @see RawArtist.musicBrainzId */
    var albumArtistMusicBrainzIds: List<String> = listOf(),
    /** @see RawArtist.name */
    var albumArtistNames: List<String> = listOf(),
    /** @see RawArtist.sortName */
    var albumArtistSortNames: List<String> = listOf(),
    /** @see RawGenre.name */
    var genreNames: List<String> = listOf()
)

/**
 * Raw information about an [AlbumImpl] obtained from the component [SongImpl] instances.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class RawAlbum(
    /**
     * The ID of the [AlbumImpl]'s grouping, obtained from MediaStore. Note that this ID is highly
     * unstable and should only be used for accessing the system-provided cover art.
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
    /** @see RawArtist.name */
    val rawArtists: List<RawArtist>
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
        other is RawAlbum &&
            when {
                musicBrainzId != null && other.musicBrainzId != null ->
                    musicBrainzId == other.musicBrainzId
                musicBrainzId == null && other.musicBrainzId == null ->
                    name.equals(other.name, true) && rawArtists == other.rawArtists
                else -> false
            }
}

/**
 * Raw information about an [ArtistImpl] obtained from the component [SongImpl] and [AlbumImpl]
 * instances.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class RawArtist(
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
        other is RawArtist &&
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

/**
 * Raw information about a [GenreImpl] obtained from the component [SongImpl] instances.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class RawGenre(
    /** @see Music.rawName */
    val name: String? = null
) {

    // Cache the hashCode for HashMap efficiency.
    private val hashCode = name?.lowercase().hashCode()

    // Only group by the lowercase genre name. This allows Genre grouping to be
    // case-insensitive, which may be helpful in some libraries with different ways of
    // formatting genres.
    override fun hashCode() = hashCode

    override fun equals(other: Any?) =
        other is RawGenre &&
            when {
                name != null && other.name != null -> name.equals(other.name, true)
                name == null && other.name == null -> true
                else -> false
            }
}
