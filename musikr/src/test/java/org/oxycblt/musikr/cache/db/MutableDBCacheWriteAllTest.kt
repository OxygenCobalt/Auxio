/*
 * Copyright (c) 2025 Auxio Project
 * MutableDBCacheWriteAllTest.kt is part of Auxio.
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

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.oxycblt.musikr.cache.CachedFile
import org.oxycblt.musikr.fs.File

class MutableDBCacheWriteAllTest {
    private val mockReadDao = mockk<CacheReadDao>(relaxed = true)
    private val mockWriteDao = mockk<CacheWriteDao>(relaxed = true)
    private val dbCache = DBCache.from(mockReadDao)
    private val cache = MutableDBCache.from(dbCache, mockWriteDao)

    @Test
    fun writeAll_emptyList_doesNotCallDao() = runTest {
        cache.writeAll(emptyList())

        coVerify(exactly = 0) { mockWriteDao.updateSongs(any()) }
    }

    @Test
    fun writeAll_fewerThanBatchSize_oneInsert() = runTest {
        coEvery { mockWriteDao.updateSongs(any()) } just Runs

        cache.writeAll(makeCachedFiles(100))

        coVerify(exactly = 1) { mockWriteDao.updateSongs(any()) }
    }

    @Test
    fun writeAll_exactlyBatchSize_oneInsert() = runTest {
        coEvery { mockWriteDao.updateSongs(any()) } just Runs

        cache.writeAll(makeCachedFiles(500))

        coVerify(exactly = 1) { mockWriteDao.updateSongs(any()) }
    }

    @Test
    fun writeAll_overBatchSize_multipleInserts() = runTest {
        coEvery { mockWriteDao.updateSongs(any()) } just Runs

        cache.writeAll(makeCachedFiles(1100))

        coVerify(exactly = 3) { mockWriteDao.updateSongs(any()) }
    }

    @Test
    fun writeAll_chunkSizes_matchBatchBoundaries() = runTest {
        val capturedChunks = mutableListOf<List<CachedFileData>>()
        coEvery { mockWriteDao.updateSongs(capture(capturedChunks)) } just Runs

        cache.writeAll(makeCachedFiles(1100))

        assertEquals(3, capturedChunks.size)
        assertEquals(500, capturedChunks[0].size)
        assertEquals(500, capturedChunks[1].size)
        assertEquals(100, capturedChunks[2].size)
    }

    @Test
    fun writeAll_remainder_isFlushedInFinalChunk() = runTest {
        val capturedChunks = mutableListOf<List<CachedFileData>>()
        coEvery { mockWriteDao.updateSongs(capture(capturedChunks)) } just Runs

        cache.writeAll(makeCachedFiles(501))

        assertEquals(2, capturedChunks.size)
        assertEquals(500, capturedChunks[0].size)
        assertEquals(1, capturedChunks[1].size)
    }

    private fun makeCachedFiles(count: Int): List<CachedFile> =
        (1..count).map { n ->
            val file = mockk<File>(relaxed = true)
            every { file.uri } returns mockk(relaxed = true)
            every { file.modifiedMs } returns n.toLong()
            CachedFile(file = file, audio = null, addedMs = n.toLong())
        }
}
