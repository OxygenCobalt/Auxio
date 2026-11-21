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
import android.net.Uri
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.oxycblt.musikr.cache.Audio
import org.oxycblt.musikr.cache.Cache
import org.oxycblt.musikr.cache.CacheResult
import org.oxycblt.musikr.cache.CachedFile
import org.oxycblt.musikr.cache.MutableCache
import org.oxycblt.musikr.fs.AddedMs
import org.oxycblt.musikr.fs.Components
import org.oxycblt.musikr.fs.File
import org.oxycblt.musikr.fs.Path
import org.oxycblt.musikr.fs.Volume
import org.oxycblt.musikr.fs.path.VolumeManager
import org.oxycblt.musikr.metadata.Properties
import org.oxycblt.musikr.tag.parse.ParsedTags

/**
 * An immutable [Cache] backed by an internal Room database.
 *
 * Create an instance with [from].
 */
class DBCache
private constructor(private val readDao: CacheReadDao, private val volumeManager: VolumeManager) :
    Cache {
    private var mapping: Map<Uri, CachedFileData>? = null
    private val mappingLock = Mutex()

    private suspend fun loadMapping() =
        mappingLock.withLock {
            mapping ?: readDao.selectAllSongs().associateBy { it.uri }.also { mapping = it }
        }

    override suspend fun read(file: File): CacheResult {
        val dbSong = loadMapping()[file.uri] ?: return CacheResult.Miss(file)
        if (dbSong.modifiedMs != file.modifiedMs) {
            return CacheResult.Stale(file, dbSong.addedMs)
        }
        return CacheResult.Hit(dbSong.toCachedFile(file))
    }

    override suspend fun readAll(): List<CachedFile> =
        loadMapping().values.mapNotNull { dbSong ->
            if (dbSong.mimeType == null) return@mapNotNull null
            val file = dbSong.toFile(volumeManager) ?: return@mapNotNull null
            dbSong.toCachedFile(file)
        }

    private fun CachedFileData.toFile(volumeManager: VolumeManager): File? {
        val path = decodePath(volumeManager, pathVolume, pathComponents) ?: return null
        return File(
            uri = uri,
            path = path,
            addedMs =
                object : AddedMs {
                    override suspend fun resolve() = addedMs
                },
            modifiedMs = modifiedMs,
            mimeType = fileMimeType,
            size = size,
            parent = null,
        )
    }

    private fun CachedFileData.toCachedFile(file: File) =
        CachedFile(
            file,
            mimeType?.let {
                Audio(
                    Properties(mimeType, durationMs!!, bitrateKbps!!, sampleRateHz!!),
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
                        releaseTypes = releaseTypes!!,
                        artistMusicBrainzIds = artistMusicBrainzIds!!,
                        artistNames = artistNames!!,
                        artistSortNames = artistSortNames!!,
                        albumArtistMusicBrainzIds = albumArtistMusicBrainzIds!!,
                        albumArtistNames = albumArtistNames!!,
                        albumArtistSortNames = albumArtistSortNames!!,
                        genreNames = genreNames!!,
                        replayGainTrackAdjustment = replayGainTrackAdjustment,
                        replayGainAlbumAdjustment = replayGainAlbumAdjustment,
                    ),
                    coverId = coverId,
                )
            },
            addedMs = addedMs,
        )

    companion object {
        /**
         * Create a new instance of [DBCache] from the given [context].
         *
         * This instance should be a singleton, since it implicitly holds a Room database. As a
         * result, you should only create EITHER a [DBCache] or a [MutableDBCache].
         *
         * @param context The context to use to create the Room database.
         * @return A new instance of [DBCache].
         */
        fun from(context: Context) =
            from(CacheDatabase.from(context), VolumeManager.from(context))

        internal fun from(db: CacheDatabase, volumeManager: VolumeManager) =
            DBCache(db.readDao(), volumeManager)

        internal fun from(readDao: CacheReadDao, volumeManager: VolumeManager) =
            DBCache(readDao, volumeManager)
    }
}

/**
 * A mutable [Cache] backed by an internal Room database.
 *
 * Create an instance with [from].
 */
class MutableDBCache
private constructor(private val inner: DBCache, private val writeDao: CacheWriteDao) :
    MutableCache {
    override suspend fun read(file: File) = inner.read(file)

    override suspend fun readAll() = inner.readAll()

    override suspend fun write(cachedFile: CachedFile) {
        writeDao.updateSong(cachedFile.toDbData())
    }

    override suspend fun writeAll(cachedFiles: List<CachedFile>) {
        for (chunk in cachedFiles.chunked(BATCH_SIZE)) {
            writeDao.updateSongs(chunk.map { it.toDbData() })
        }
    }

    override suspend fun cleanup(excluding: List<CachedFile>) {
        writeDao.deleteExcludingUris(excluding.mapTo(mutableSetOf()) { it.file.uri.toString() })
    }

    companion object {
        /**
         * Create a new instance of [MutableDBCache] from the given [context].
         *
         * This instance should be a singleton, since it implicitly holds a Room database. As a
         * result, you should only create EITHER a [DBCache] or a [MutableDBCache].
         *
         * @param context The context to use to create the Room database.
         * @return A new instance of [MutableDBCache].
         */
        fun from(context: Context): MutableDBCache {
            val db = CacheDatabase.from(context)
            return MutableDBCache(DBCache.from(db, VolumeManager.from(context)), db.writeDao())
        }

        internal fun from(inner: DBCache, writeDao: CacheWriteDao) = MutableDBCache(inner, writeDao)

        private const val BATCH_SIZE = 500
    }
}

private fun CachedFile.toDbData() =
    CachedFileData(
        uri = file.uri,
        modifiedMs = file.modifiedMs,
        addedMs = addedMs,
        fileMimeType = file.mimeType,
        size = file.size,
        pathVolume = encodeVolume(file.path.volume),
        pathComponents = file.path.components.unixString,
        mimeType = audio?.properties?.mimeType,
        durationMs = audio?.properties?.durationMs,
        bitrateKbps = audio?.properties?.bitrateKbps,
        sampleRateHz = audio?.properties?.sampleRateHz,
        musicBrainzId = audio?.tags?.musicBrainzId,
        name = audio?.tags?.name,
        sortName = audio?.tags?.sortName,
        track = audio?.tags?.track,
        disc = audio?.tags?.disc,
        subtitle = audio?.tags?.subtitle,
        date = audio?.tags?.date,
        albumMusicBrainzId = audio?.tags?.albumMusicBrainzId,
        albumName = audio?.tags?.albumName,
        albumSortName = audio?.tags?.albumSortName,
        releaseTypes = audio?.tags?.releaseTypes,
        artistMusicBrainzIds = audio?.tags?.artistMusicBrainzIds,
        artistNames = audio?.tags?.artistNames,
        artistSortNames = audio?.tags?.artistSortNames,
        albumArtistMusicBrainzIds = audio?.tags?.albumArtistMusicBrainzIds,
        albumArtistNames = audio?.tags?.albumArtistNames,
        albumArtistSortNames = audio?.tags?.albumArtistSortNames,
        genreNames = audio?.tags?.genreNames,
        replayGainTrackAdjustment = audio?.tags?.replayGainTrackAdjustment,
        replayGainAlbumAdjustment = audio?.tags?.replayGainAlbumAdjustment,
        coverId = audio?.coverId,
    )

private const val VOLUME_INTERNAL = "internal"
private const val VOLUME_EXTERNAL_PREFIX = "external:"
private const val VOLUME_THIRD_PARTY_PREFIX = "third:"

private fun encodeVolume(volume: Volume) =
    when (volume) {
        is Volume.Internal -> VOLUME_INTERNAL
        is Volume.External -> VOLUME_EXTERNAL_PREFIX + volume.id.orEmpty()
        is Volume.ThirdParty -> VOLUME_THIRD_PARTY_PREFIX + volume.uri
    }

/** Inverse of [encodeVolume] + path components. Null if the volume is no longer present. */
private fun decodePath(volumeManager: VolumeManager, volume: String, components: String): Path? {
    val resolved =
        when {
            volume == VOLUME_INTERNAL -> volumeManager.getInternalVolume()
            volume.startsWith(VOLUME_EXTERNAL_PREFIX) -> {
                val id = volume.removePrefix(VOLUME_EXTERNAL_PREFIX).ifEmpty { null }
                volumeManager.getVolumes().find { it is Volume.External && it.id == id }
            }
            volume.startsWith(VOLUME_THIRD_PARTY_PREFIX) ->
                Volume.ThirdParty(Uri.parse(volume.removePrefix(VOLUME_THIRD_PARTY_PREFIX)))
            else -> null
        } ?: return null
    return Path(resolved, Components.parseUnix(components))
}
