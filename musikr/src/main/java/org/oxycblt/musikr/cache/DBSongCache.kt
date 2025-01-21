/*
 * Copyright (c) 2024 Auxio Project
 * DBSongCache.kt is part of Auxio.
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
 
package org.oxycblt.musikr.cache

import android.content.Context
import org.oxycblt.musikr.Song
import org.oxycblt.musikr.fs.device.DeviceFile
import org.oxycblt.musikr.metadata.Properties
import org.oxycblt.musikr.tag.parse.ParsedTags

class DBSongCache
private constructor(private val readDao: CacheReadDao, private val writeDao: CacheWriteDao) :
    MutableSongCache {
    override suspend fun read(file: DeviceFile): CacheResult {
        val data = readDao.selectSong(file.uri.toString()) ?: return CacheResult.Miss(file)
        if (data.modifiedMs != file.modifiedMs) {
            // We *found* this file earlier, but it's out of date.
            // Send back it with the timestamp so it will be re-used.
            // The touch timestamp will be updated on write.
            return CacheResult.Outdated(file, data.addedMs)
        }
        val cachedSong =
            data.run {
                CachedSong(
                    file,
                    Properties(mimeType, durationMs, bitrateHz, sampleRateHz),
                    ParsedTags(
                        musicBrainzId = musicBrainzId,
                        name = name,
                        sortName = sortName,
                        durationMs = durationMs,
                        track = track,
                        disc = disc,
                        subtitle = subtitle,
                        date = date,
                        albumMusicBrainzId = albumMusicBrainzId,
                        albumName = albumName,
                        albumSortName = albumSortName,
                        releaseTypes = releaseTypes,
                        artistMusicBrainzIds = artistMusicBrainzIds,
                        artistNames = artistNames,
                        artistSortNames = artistSortNames,
                        albumArtistMusicBrainzIds = albumArtistMusicBrainzIds,
                        albumArtistNames = albumArtistNames,
                        albumArtistSortNames = albumArtistSortNames,
                        genreNames = genreNames,
                        replayGainTrackAdjustment = replayGainTrackAdjustment,
                        replayGainAlbumAdjustment = replayGainAlbumAdjustment),
                    coverId = coverId,
                    addedMs = addedMs)
            }
        return CacheResult.Hit(cachedSong)
    }

    override suspend fun write(song: CachedSong) {
        writeDao.updateSong(
            CachedSongData(
                uri = song.file.uri.toString(),
                modifiedMs = song.file.modifiedMs,
                addedMs = song.addedMs,
                mimeType = song.properties.mimeType,
                durationMs = song.properties.durationMs,
                bitrateHz = song.properties.bitrateKbps,
                sampleRateHz = song.properties.sampleRateHz,
                musicBrainzId = song.tags.musicBrainzId,
                name = song.tags.name,
                sortName = song.tags.sortName,
                track = song.tags.track,
                disc = song.tags.disc,
                subtitle = song.tags.subtitle,
                date = song.tags.date,
                albumMusicBrainzId = song.tags.albumMusicBrainzId,
                albumName = song.tags.albumName,
                albumSortName = song.tags.albumSortName,
                releaseTypes = song.tags.releaseTypes,
                artistMusicBrainzIds = song.tags.artistMusicBrainzIds,
                artistNames = song.tags.artistNames,
                artistSortNames = song.tags.artistSortNames,
                albumArtistMusicBrainzIds = song.tags.albumArtistMusicBrainzIds,
                albumArtistNames = song.tags.albumArtistNames,
                albumArtistSortNames = song.tags.albumArtistSortNames,
                genreNames = song.tags.genreNames,
                replayGainTrackAdjustment = song.tags.replayGainTrackAdjustment,
                replayGainAlbumAdjustment = song.tags.replayGainAlbumAdjustment,
                coverId = song.coverId))
    }

    override suspend fun cleanup(exclude: Collection<Song>) {
        writeDao.deleteExcludingUris(exclude.map { it.uri.toString() })
    }

    companion object {
        fun from(context: Context) =
            CacheDatabase.from(context).run { DBSongCache(readDao(), writeDao()) }
    }
}
