package org.oxycblt.auxio.music.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.oxycblt.auxio.R
import org.oxycblt.auxio.image.extractor.Cover
import org.oxycblt.auxio.music.info.Disc
import org.oxycblt.auxio.music.info.Name
import org.oxycblt.auxio.music.info.ReleaseType
import org.oxycblt.auxio.music.metadata.Separators
import org.oxycblt.auxio.music.stack.AudioFile
import org.oxycblt.auxio.music.stack.extractor.parseId3GenreNames
import org.oxycblt.auxio.music.stack.fs.MimeType
import org.oxycblt.auxio.playback.replaygain.ReplayGainAdjustment
import org.oxycblt.auxio.util.toUuidOrNull

interface SongInterpreter {
    fun prepare(audioFiles: Flow<AudioFile>, interpretation: Interpretation): Flow<PreSong>
}

class SongInterpreterImpl(
    private val nameFactory: Name.Known.Factory,
    private val separators: Separators
) : SongInterpreter {
    override fun prepare(audioFiles: Flow<AudioFile>, interpretation: Interpretation) = audioFiles.map { audioFile ->
        val individualPreArtists = makePreArtists(
            audioFile.artistMusicBrainzIds,
            audioFile.artistNames,
            audioFile.artistSortNames
        )
        val albumPreArtists = makePreArtists(
            audioFile.albumArtistMusicBrainzIds,
            audioFile.albumArtistNames,
            audioFile.albumArtistSortNames
        )
        val preAlbum = makePreAlbum(audioFile, individualPreArtists, albumPreArtists)
        val rawArtists =
            individualPreArtists.ifEmpty { albumPreArtists }.ifEmpty { listOf(unknownPreArtist()) }
        val rawGenres =
            makePreGenres(audioFile).ifEmpty { listOf(unknownPreGenre()) }
        val uri = audioFile.deviceFile.uri
        PreSong(
            musicBrainzId = audioFile.musicBrainzId?.toUuidOrNull(),
            name = nameFactory.parse(need(audioFile, "name", audioFile.name), audioFile.sortName),
            rawName = audioFile.name,
            track = audioFile.track,
            disc = audioFile.disc?.let { Disc(it, audioFile.subtitle) },
            date = audioFile.date,
            uri = uri,
            cover = inferCover(audioFile),
            path = need(audioFile, "path", audioFile.deviceFile.path),
            mimeType = MimeType(
                need(audioFile, "mime type", audioFile.deviceFile.mimeType),
                null
            ),
            size = audioFile.deviceFile.size,
            durationMs = need(audioFile, "duration", audioFile.durationMs),
            replayGainAdjustment = ReplayGainAdjustment(
                audioFile.replayGainTrackAdjustment,
                audioFile.replayGainAlbumAdjustment,
            ),
            // TODO: Figure out what to do with date added
            dateAdded = audioFile.deviceFile.lastModified,
            preAlbum = preAlbum,
            preArtists = rawArtists,
            preGenres = rawGenres
        )
    }

    private fun <T> need(audioFile: AudioFile, what: String, value: T?) =
        requireNotNull(value) { "Invalid $what for song ${audioFile.deviceFile.path}: No $what" }

    private fun inferCover(audioFile: AudioFile): Cover {
        return Cover.Embedded(
            audioFile.deviceFile.uri,
            audioFile.deviceFile.uri,
            ""
        )
    }

    private fun makePreAlbum(
        audioFile: AudioFile,
        individualPreArtists: List<PreArtist>,
        albumPreArtists: List<PreArtist>
    ): PreAlbum {
        val rawAlbumName = need(audioFile, "album name", audioFile.albumName)
        return PreAlbum(
            musicBrainzId = audioFile.albumMusicBrainzId?.toUuidOrNull(),
            name = nameFactory.parse(rawAlbumName, audioFile.albumSortName),
            rawName = rawAlbumName,
            releaseType = ReleaseType.parse(separators.split(audioFile.releaseTypes))
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

    private fun makePreGenres(audioFile: AudioFile): List<PreGenre> {
        val genreNames =
            audioFile.genreNames.parseId3GenreNames() ?: separators.split(audioFile.genreNames)
        return genreNames.map { makePreGenre(it) }
    }

    private fun makePreGenre(rawName: String?) =
        PreGenre(rawName?.let { nameFactory.parse(it, null) } ?: Name.Unknown(R.string.def_genre),
            rawName)

    private fun unknownPreGenre() =
        PreGenre(Name.Unknown(R.string.def_genre), null)

}