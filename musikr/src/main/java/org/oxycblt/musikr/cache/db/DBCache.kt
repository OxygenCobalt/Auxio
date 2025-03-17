/*
 * Copyright (c) 2025 Auxio Project
 * DBCache.kt is part of Auxio.
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
 
package org.oxycblt.musikr.cache.db

import android.content.Context
import org.oxycblt.musikr.cache.Cache
import org.oxycblt.musikr.cache.CacheResult
import org.oxycblt.musikr.cache.CachedSong
import org.oxycblt.musikr.cache.MutableCache
import org.oxycblt.musikr.fs.device.DeviceFile
import org.oxycblt.musikr.metadata.Properties
import org.oxycblt.musikr.tag.parse.ParsedTags

class DBCache private constructor(private val readDao: CacheReadDao) : Cache {
    override suspend fun read(file: DeviceFile): CacheResult {
        val dbSong = readDao.selectSong(file.uri.toString()) ?: return CacheResult.Miss(file)
        if (dbSong.modifiedMs != file.modifiedMs) {
            return CacheResult.Stale(file, dbSong.addedMs)
        }
        val song =
            CachedSong(
                file,
                Properties(
                    dbSong.mimeType, dbSong.durationMs, dbSong.bitrateKbps, dbSong.sampleRateHz),
                ParsedTags(
                    musicBrainzId = dbSong.musicBrainzId,
                    name = dbSong.name,
                    sortName = dbSong.sortName,
                    durationMs = dbSong.durationMs,
                    track = dbSong.track,
                    disc = dbSong.disc,
                    subtitle = dbSong.subtitle,
                    date = dbSong.date,
                    albumMusicBrainzId = dbSong.albumMusicBrainzId,
                    albumName = dbSong.albumName,
                    albumSortName = dbSong.albumSortName,
                    releaseTypes = dbSong.releaseTypes,
                    artistMusicBrainzIds = dbSong.artistMusicBrainzIds,
                    artistNames = dbSong.artistNames,
                    artistSortNames = dbSong.artistSortNames,
                    albumArtistMusicBrainzIds = dbSong.albumArtistMusicBrainzIds,
                    albumArtistNames = dbSong.albumArtistNames,
                    albumArtistSortNames = dbSong.albumArtistSortNames,
                    genreNames = dbSong.genreNames,
                    replayGainTrackAdjustment = dbSong.replayGainTrackAdjustment,
                    replayGainAlbumAdjustment = dbSong.replayGainAlbumAdjustment),
                coverId = dbSong.coverId,
                addedMs = dbSong.addedMs)
        return CacheResult.Hit(song)
    }

    companion object {
        fun from(context: Context) = from(CacheDatabase.from(context))

        internal fun from(db: CacheDatabase) = DBCache(db.readDao())
    }
}

class MutableDBCache
private constructor(private val inner: DBCache, private val writeDao: CacheWriteDao) :
    MutableCache {
    override suspend fun read(file: DeviceFile) = inner.read(file)

    override suspend fun write(cachedSong: CachedSong) {
        val dbSong =
            CachedSongData(
                uri = cachedSong.file.uri,
                modifiedMs = cachedSong.file.modifiedMs,
                addedMs = cachedSong.addedMs,
                mimeType = cachedSong.properties.mimeType,
                durationMs = cachedSong.properties.durationMs,
                bitrateKbps = cachedSong.properties.bitrateKbps,
                sampleRateHz = cachedSong.properties.sampleRateHz,
                musicBrainzId = cachedSong.tags.musicBrainzId,
                name = cachedSong.tags.name,
                sortName = cachedSong.tags.sortName,
                track = cachedSong.tags.track,
                disc = cachedSong.tags.disc,
                subtitle = cachedSong.tags.subtitle,
                date = cachedSong.tags.date,
                albumMusicBrainzId = cachedSong.tags.albumMusicBrainzId,
                albumName = cachedSong.tags.albumName,
                albumSortName = cachedSong.tags.albumSortName,
                releaseTypes = cachedSong.tags.releaseTypes,
                artistMusicBrainzIds = cachedSong.tags.artistMusicBrainzIds,
                artistNames = cachedSong.tags.artistNames,
                artistSortNames = cachedSong.tags.artistSortNames,
                albumArtistMusicBrainzIds = cachedSong.tags.albumArtistMusicBrainzIds,
                albumArtistNames = cachedSong.tags.albumArtistNames,
                albumArtistSortNames = cachedSong.tags.albumArtistSortNames,
                genreNames = cachedSong.tags.genreNames,
                replayGainTrackAdjustment = cachedSong.tags.replayGainTrackAdjustment,
                replayGainAlbumAdjustment = cachedSong.tags.replayGainAlbumAdjustment,
                coverId = cachedSong.coverId)
        writeDao.updateSong(dbSong)
    }

    override suspend fun cleanup(excluding: List<CachedSong>) {
        writeDao.deleteExcludingUris(excluding.mapTo(mutableSetOf()) { it.file.uri.toString() })
    }

    companion object {
        fun from(context: Context): MutableDBCache {
            val db = CacheDatabase.from(context)
            return MutableDBCache(DBCache.from(db), db.writeDao())
        }
    }
}
