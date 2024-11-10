package org.oxycblt.auxio.music.device

import org.oxycblt.auxio.R
import org.oxycblt.auxio.image.extractor.Cover
import org.oxycblt.auxio.music.fs.MimeType
import org.oxycblt.auxio.music.fs.toAlbumCoverUri
import org.oxycblt.auxio.music.fs.toAudioUri
import org.oxycblt.auxio.music.fs.toSongCoverUri
import org.oxycblt.auxio.music.info.Disc
import org.oxycblt.auxio.music.info.Name
import org.oxycblt.auxio.music.info.ReleaseType
import org.oxycblt.auxio.music.metadata.Separators
import org.oxycblt.auxio.music.metadata.parseId3GenreNames
import org.oxycblt.auxio.playback.replaygain.ReplayGainAdjustment
import org.oxycblt.auxio.util.toUuidOrNull

interface SongInterpreter {
    fun consume(rawSong: RawSong): PreSong

    interface Factory {
        fun create(nameFactory: Name.Known.Factory, separators: Separators): SongInterpreter
    }
}

class SongInterpreterFactory : SongInterpreter.Factory {
    override fun create(nameFactory: Name.Known.Factory, separators: Separators) =
        SongInterpreterImpl(nameFactory, separators)
}

class SongInterpreterImpl(
    private val nameFactory: Name.Known.Factory,
    private val separators: Separators
) : SongInterpreter {
    override fun consume(rawSong: RawSong): PreSong {
        val individualPreArtists = makePreArtists(
            rawSong.artistMusicBrainzIds,
            rawSong.artistNames,
            rawSong.artistSortNames
        )
        val albumPreArtists = makePreArtists(
            rawSong.albumArtistMusicBrainzIds,
            rawSong.albumArtistNames,
            rawSong.albumArtistSortNames
        )
        val preAlbum = makePreAlbum(rawSong, individualPreArtists, albumPreArtists)
        val rawArtists =
            individualPreArtists.ifEmpty { albumPreArtists }.ifEmpty { listOf(unknownPreArtist()) }
        val rawGenres =
            makePreGenres(rawSong).ifEmpty { listOf(unknownPreGenre()) }
        val uri = need(rawSong, "uri", rawSong.mediaStoreId).toAudioUri()
        return PreSong(
            musicBrainzId = rawSong.musicBrainzId?.toUuidOrNull(),
            name = nameFactory.parse(need(rawSong, "name", rawSong.name), rawSong.sortName),
            rawName = rawSong.name,
            track = rawSong.track,
            disc = rawSong.disc?.let { Disc(it, rawSong.subtitle) },
            date = rawSong.date,
            uri = uri,
            cover = inferCover(rawSong),
            path = need(rawSong, "path", rawSong.path),
            mimeType = MimeType(
                need(rawSong, "mime type", rawSong.extensionMimeType),
                null
            ),
            size = need(rawSong, "size", rawSong.size),
            durationMs = need(rawSong, "duration", rawSong.durationMs),
            replayGainAdjustment = ReplayGainAdjustment(
                rawSong.replayGainTrackAdjustment,
                rawSong.replayGainAlbumAdjustment,
            ),
            dateAdded = need(rawSong, "date added", rawSong.dateAdded),
            preAlbum = preAlbum,
            preArtists = rawArtists,
            preGenres = rawGenres
        )
    }

    private fun <T> need(rawSong: RawSong, what: String, value: T?) =
        requireNotNull(value) { "Invalid $what for song ${rawSong.path}: No $what" }

    private fun inferCover(rawSong: RawSong): Cover {
        val uri = need(rawSong, "uri", rawSong.mediaStoreId).toAudioUri()
        return rawSong.coverPerceptualHash?.let {
            Cover.Embedded(
                requireNotNull(rawSong.mediaStoreId) { "Invalid raw ${rawSong.path}: No id" }
                    .toSongCoverUri(),
                uri,
                it)
        } ?: Cover.External(requireNotNull(rawSong.albumMediaStoreId).toAlbumCoverUri())
    }

    private fun makePreAlbum(
        rawSong: RawSong,
        individualPreArtists: List<PreArtist>,
        albumPreArtists: List<PreArtist>
    ): PreAlbum {
        val rawAlbumName = need(rawSong, "album name", rawSong.albumName)
        return PreAlbum(
            musicBrainzId = rawSong.albumMusicBrainzId?.toUuidOrNull(),
            name = nameFactory.parse(rawAlbumName, rawSong.albumSortName),
            rawName = rawAlbumName,
            releaseType = ReleaseType.parse(separators.split(rawSong.releaseTypes))
                ?: ReleaseType.Album(null),
            preArtists =
            albumPreArtists
                .ifEmpty { individualPreArtists }
                .ifEmpty { listOf(unknownPreArtist()) })
    }

    private fun makePreArtists(
        rawMusicBrainzIds: List<String>,
        rawNames: List<String>,
        rawSortNames: List<String>
    ): List<PreArtist> {
        val musicBrainzIds = separators.split(rawMusicBrainzIds)
        val names = separators.split(rawNames)
        val sortNames = separators.split(rawSortNames)
        return names
            .mapIndexed { i, name ->
                makePreArtist(
                    musicBrainzIds.getOrNull(i),
                    name,
                    sortNames.getOrNull(i)
                )
            }

    }

    private fun makePreArtist(
        musicBrainzId: String?,
        rawName: String?,
        sortName: String?
    ): PreArtist {
        val name =
            rawName?.let { nameFactory.parse(it, sortName) } ?: Name.Unknown(R.string.def_artist)
        val musicBrainzId = musicBrainzId?.toUuidOrNull()
        return PreArtist(musicBrainzId, name, rawName)
    }

    private fun unknownPreArtist() =
        PreArtist(null, Name.Unknown(R.string.def_artist), null)

    private fun makePreGenres(rawSong: RawSong): List<PreGenre> {
        val genreNames =
            rawSong.genreNames.parseId3GenreNames() ?: separators.split(rawSong.genreNames)
        return genreNames.map { makePreGenre(it) }
    }

    private fun makePreGenre(rawName: String?) =
        PreGenre(rawName?.let { nameFactory.parse(it, null) } ?: Name.Unknown(R.string.def_genre),
            rawName)

    private fun unknownPreGenre() =
        PreGenre(Name.Unknown(R.string.def_genre), null)

}