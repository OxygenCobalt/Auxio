/*
 * Copyright (c) 2024 Auxio Project
 * StoredCache.kt is part of Auxio.
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
import org.oxycblt.musikr.cover.Covers
import org.oxycblt.musikr.fs.DeviceFile
import org.oxycblt.musikr.metadata.Properties
import org.oxycblt.musikr.pipeline.RawSong
import org.oxycblt.musikr.tag.parse.ParsedTags

interface StoredCache {
    fun visible(): Cache.Factory

    fun invisible(): Cache.Factory

    companion object {
        fun from(context: Context): StoredCache = StoredCacheImpl(CacheDatabase.from(context))
    }
}

private class StoredCacheImpl(private val cacheDatabase: CacheDatabase) : StoredCache {
    override fun visible(): Cache.Factory = VisibleStoredCache.Factory(cacheDatabase)

    override fun invisible(): Cache.Factory = InvisibleStoredCache.Factory(cacheDatabase)
}

private abstract class BaseStoredCache(protected val writeDao: CacheWriteDao) : Cache() {
    private val created = System.nanoTime()

    override suspend fun write(song: RawSong) = writeDao.updateSong(CachedSong.fromRawSong(song))

    override suspend fun finalize() {
        // Anything not create during this cache's use implies that it has not been
        // access during this run and should be pruned.
        writeDao.pruneOlderThan(created)
    }
}

private class VisibleStoredCache(private val visibleDao: VisibleCacheDao, writeDao: CacheWriteDao) :
    BaseStoredCache(writeDao) {
    override suspend fun read(file: DeviceFile, covers: Covers): CacheResult {
        val cachedSong = visibleDao.selectSong(file.uri.toString()) ?: return CacheResult.Miss(file)
        if (cachedSong.modifiedMs != file.modifiedMs) {
            // We *found* this file earlier, but it's out of date.
            // Send back it with the timestamp so it will be re-used.
            // The touch timestamp will be updated on write.
            return CacheResult.Outdated(file, cachedSong.addedMs)
        }
        // Valid file, update the touch time.
        visibleDao.touch(file.uri.toString())
        return cachedSong.run {
            CacheResult.Hit(
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
    }

    class Factory(private val cacheDatabase: CacheDatabase) : Cache.Factory() {
        override fun open() =
            VisibleStoredCache(cacheDatabase.visibleDao(), cacheDatabase.writeDao())
    }
}

private class InvisibleStoredCache(
    private val invisibleCacheDao: InvisibleCacheDao,
    writeDao: CacheWriteDao
) : BaseStoredCache(writeDao) {
    override suspend fun read(file: DeviceFile, covers: Covers): CacheResult {
        val addedMs =
            invisibleCacheDao.selectAddedMs(file.uri.toString()) ?: return CacheResult.Miss(file)
        return CacheResult.Outdated(file, addedMs)
    }

    class Factory(private val cacheDatabase: CacheDatabase) : Cache.Factory() {
        override fun open() =
            InvisibleStoredCache(cacheDatabase.invisibleDao(), cacheDatabase.writeDao())
    }
}
