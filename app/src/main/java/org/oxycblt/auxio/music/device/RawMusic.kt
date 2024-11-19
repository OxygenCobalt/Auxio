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
import org.oxycblt.auxio.music.stack.fs.DeviceFile
import org.oxycblt.auxio.music.info.Date
import org.oxycblt.auxio.music.info.ReleaseType

/**
 * Raw information about a [SongImpl] obtained from the filesystem/Extractor instances.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
data class RawSong(
    val file: DeviceFile,
    /** @see Song.durationMs */
    var durationMs: Long? = null,
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
    override val musicBrainzId: UUID?,
    /** @see Music.name */
    override val name: String,
    /** @see Music.name */
    val sortName: String?,
    /** @see Album.releaseType */
    val releaseType: ReleaseType?,
    /** @see RawArtist.name */
    val rawArtists: List<RawArtist>
) : MusicBrainzGroupable

/**
 * Raw information about an [ArtistImpl] obtained from the component [SongImpl] and [AlbumImpl]
 * instances.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
data class RawArtist(
    /** @see Music.UID */
    override val musicBrainzId: UUID? = null,
    /** @see Music.name */
    override val name: String? = null,
    /** @see Music.name */
    val sortName: String? = null
) : MusicBrainzGroupable

/**
 * Raw information about a [GenreImpl] obtained from the component [SongImpl] instances.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
data class RawGenre(
    /** @see Music.name */
    override val name: String? = null
) : NameGroupable

interface NameGroupable {
    val name: String?
}

interface MusicBrainzGroupable : NameGroupable {
    val musicBrainzId: UUID?
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
