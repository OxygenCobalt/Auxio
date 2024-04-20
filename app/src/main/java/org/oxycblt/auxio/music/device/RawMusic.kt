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
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.fs.Path
import org.oxycblt.auxio.music.info.Date
import org.oxycblt.auxio.music.info.ReleaseType

/**
 * Raw information about a [SongImpl] obtained from the filesystem/Extractor instances.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
data class RawSong(
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
    var path: Path? = null,
    /** @see Song.size */
    var size: Long? = null,
    /** @see Song.durationMs */
    var durationMs: Long? = null,
    /** @see Song.mimeType */
    var extensionMimeType: String? = null,
    /** @see Song.replayGainAdjustment */
    var replayGainTrackAdjustment: Float? = null,
    /** @see Song.replayGainAdjustment */
    var replayGainAlbumAdjustment: Float? = null,
    /** @see Music.UID */
    var musicBrainzId: String? = null,
    /** @see Music.name */
    var name: String? = null,
    /** @see Music.name */
    var sortName: String? = null,
    /** @see Song.track */
    var track: Int? = null,
    /** @see Song.disc */
    var disc: Int? = null,
    /** @See Song.disc */
    var subtitle: String? = null,
    /** @see Song.date */
    var date: Date? = null,
    /** @see Song.cover */
    var coverPerceptualHash: String? = null,
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
data class RawAlbum(
    /**
     * The ID of the [AlbumImpl]'s grouping, obtained from MediaStore. Note that this ID is highly
     * unstable and should only be used for accessing the system-provided cover art.
     */
    val mediaStoreId: Long,
    /** @see Music.uid */
    val musicBrainzId: UUID?,
    /** @see Music.name */
    val name: String,
    /** @see Music.name */
    val sortName: String?,
    /** @see Album.releaseType */
    val releaseType: ReleaseType?,
    /** @see RawArtist.name */
    val rawArtists: List<RawArtist>
) {
    val key = Key(this)

    /**
     * Allows [RawAlbum]s to be compared by "fundamental" information that is unlikely to change on
     * an item-by-item
     */
    data class Key(private val inner: RawAlbum) {
        // Albums are grouped as follows:
        // - If we have a MusicBrainz ID, only group by it. This allows different Albums with the
        // same name to be differentiated, which is common in large libraries.
        // - If we do not have a MusicBrainz ID, compare by the lowercase album name and lowercase
        // artist name. This allows for case-insensitive artist/album grouping, which can be common
        // for albums/artists that have different naming (ex. "RAMMSTEIN" vs. "Rammstein").
        private val artistKeys = inner.rawArtists.map { it.key }

        // Cache the hash-code for HashMap efficiency.
        private val hashCode =
            inner.musicBrainzId?.hashCode()
                ?: (31 * inner.name.lowercase().hashCode() + artistKeys.hashCode())

        override fun hashCode() = hashCode

        override fun equals(other: Any?) =
            other is Key &&
                when {
                    inner.musicBrainzId != null && other.inner.musicBrainzId != null ->
                        inner.musicBrainzId == other.inner.musicBrainzId
                    inner.musicBrainzId == null && other.inner.musicBrainzId == null ->
                        inner.name.equals(other.inner.name, true) && artistKeys == other.artistKeys
                    else -> false
                }
    }
}

/**
 * Raw information about an [ArtistImpl] obtained from the component [SongImpl] and [AlbumImpl]
 * instances.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
data class RawArtist(
    /** @see Music.UID */
    val musicBrainzId: UUID? = null,
    /** @see Music.name */
    val name: String? = null,
    /** @see Music.name */
    val sortName: String? = null
) {
    val key = Key(this)

    /**
     * Allows [RawArtist]s to be compared by "fundamental" information that is unlikely to change on
     * an item-by-item
     */
    data class Key(private val inner: RawArtist) {
        // Artists are grouped as follows:
        // - If we have a MusicBrainz ID, only group by it. This allows different Artists with the
        // same name to be differentiated, which is common in large libraries.
        // - If we do not have a MusicBrainz ID, compare by the lowercase name. This allows artist
        // grouping to be case-insensitive.

        // Cache the hashCode for HashMap efficiency.
        val hashCode = inner.musicBrainzId?.hashCode() ?: inner.name?.lowercase().hashCode()

        // Compare names and MusicBrainz IDs in order to differentiate artists with the
        // same name in large libraries.

        override fun hashCode() = hashCode

        override fun equals(other: Any?) =
            other is Key &&
                when {
                    inner.musicBrainzId != null && other.inner.musicBrainzId != null ->
                        inner.musicBrainzId == other.inner.musicBrainzId
                    inner.musicBrainzId == null && other.inner.musicBrainzId == null ->
                        when {
                            inner.name != null && other.inner.name != null ->
                                inner.name.equals(other.inner.name, true)
                            inner.name == null && other.inner.name == null -> true
                            else -> false
                        }
                    else -> false
                }
    }
}

/**
 * Raw information about a [GenreImpl] obtained from the component [SongImpl] instances.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
data class RawGenre(
    /** @see Music.name */
    val name: String? = null
) {
    val key = Key(this)

    /**
     * Allows [RawGenre]s to be compared by "fundamental" information that is unlikely to change on
     * an item-by-item
     */
    data class Key(private val inner: RawGenre) {
        // Cache the hashCode for HashMap efficiency.
        private val hashCode = inner.name?.lowercase().hashCode()

        // Only group by the lowercase genre name. This allows Genre grouping to be
        // case-insensitive, which may be helpful in some libraries with different ways of
        // formatting genres.
        override fun hashCode() = hashCode

        override fun equals(other: Any?) =
            other is Key &&
                when {
                    inner.name != null && other.inner.name != null ->
                        inner.name.equals(other.inner.name, true)
                    inner.name == null && other.inner.name == null -> true
                    else -> false
                }
    }
}

/**
 * Represents grouped music information and the prioritized raw information to eventually derive a
 * [Music] implementation instance from.
 *
 * @param raw The current [PrioritizedRaw] that will be used for the finalized music information.
 * @param music The child [Music] instances of the music information to be created.
 */
data class Grouping<R, M : Music>(var raw: PrioritizedRaw<R, M>, val music: MutableSet<M>)

/**
 * Represents a [RawAlbum], [RawArtist], or [RawGenre] specifically chosen to create a [Music]
 * instance from due to it being the most likely source of truth.
 *
 * @param inner The raw music instance that will be used.
 * @param src The [Music] instance that the raw information was derived from.
 */
data class PrioritizedRaw<R, M : Music>(val inner: R, val src: M)
