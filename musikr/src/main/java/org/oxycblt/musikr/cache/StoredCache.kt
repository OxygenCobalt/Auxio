package org.oxycblt.musikr.cache

import android.content.Context
import org.oxycblt.musikr.cover.StoredCovers
import org.oxycblt.musikr.fs.DeviceFile
import org.oxycblt.musikr.pipeline.RawSong

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

    override suspend fun write(song: RawSong) =
        writeDao.updateSong(CachedSong.fromRawSong(song))

    override suspend fun finalize() {
        // Anything not create during this cache's use implies that it has not been
        // access during this run and should be pruned.
        writeDao.pruneOlderThan(created)
    }
}

private class VisibleStoredCache(
    private val visibleDao: VisibleCacheDao,
    writeDao: CacheWriteDao
) : BaseStoredCache(writeDao) {
    override suspend fun read(file: DeviceFile, storedCovers: StoredCovers): CacheResult {
        val song =
            visibleDao.selectSong(file.uri.toString()) ?: return CacheResult.Miss(file, null)
        if (song.modifiedMs != file.lastModified) {
            // We *found* this file earlier, but it's out of date.
            // Send back it with the timestamp so it will be re-used.
            // The touch timestamp will be updated on write.
            return CacheResult.Miss(file, song.addedMs)
        }
        // Valid file, update the touch time.
        visibleDao.touch(file.uri.toString())
        return CacheResult.Hit(song.intoRawSong(file, storedCovers))
    }

    class Factory(private val cacheDatabase: CacheDatabase) : Cache.Factory() {
        override fun open() = VisibleStoredCache(cacheDatabase.visibleDao(), cacheDatabase.writeDao())
    }
}

private class InvisibleStoredCache(
    private val invisibleCacheDao: InvisibleCacheDao,
    writeDao: CacheWriteDao
) : BaseStoredCache(writeDao) {
    override suspend fun read(file: DeviceFile, storedCovers: StoredCovers) =
        CacheResult.Miss(file, invisibleCacheDao.selectAddedMs(file.uri.toString()))

    class Factory(private val cacheDatabase: CacheDatabase) : Cache.Factory() {
        override fun open() = InvisibleStoredCache(cacheDatabase.invisibleDao(), cacheDatabase.writeDao())
    }
}
