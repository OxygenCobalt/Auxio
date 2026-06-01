/*
 * Copyright (c) 2026 Auxio Project
 * StartupLibraryPolicyTest.kt is part of Auxio.
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

package org.oxycblt.auxio.music

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StartupLibraryPolicyTest {
    @Test
    fun `prior usable cached load with songs emits usable library without scan`() {
        val decision =
            StartupLibraryPolicy.onCachedLoadSucceeded(
                priorState = LibraryState.USABLE,
                songCount = 42,
                lastScanFailed = false,
            )

        assertEquals(LibraryState.USABLE, decision.libraryState)
        assertFalse(decision.requestScan)
    }

    @Test
    fun `prior usable cached load failure enters recovery and schedules one scan`() {
        val decision =
            StartupLibraryPolicy.onCachedLoadFailed(
                priorState = LibraryState.USABLE,
                lastScanFailed = false,
            )

        assertEquals(LibraryState.RECOVERY, decision.libraryState)
        assertTrue(decision.requestScan)
    }

    @Test
    fun `prior usable cached load failure after scan failure does not scan storm`() {
        val decision =
            StartupLibraryPolicy.onCachedLoadFailed(
                priorState = LibraryState.USABLE,
                lastScanFailed = true,
            )

        assertEquals(LibraryState.RECOVERY, decision.libraryState)
        assertFalse(decision.requestScan)
    }

    @Test
    fun `prior never without cached library schedules first scan once`() {
        val decision =
            StartupLibraryPolicy.onNoCachedRevision(
                priorState = LibraryState.NEVER,
                lastScanFailed = false,
            )

        assertEquals(LibraryState.NEVER, decision.libraryState)
        assertTrue(decision.requestScan)
    }

    @Test
    fun `prior empty cached load empty remains intentionally empty`() {
        val decision =
            StartupLibraryPolicy.onCachedLoadSucceeded(
                priorState = LibraryState.EMPTY,
                songCount = 0,
                lastScanFailed = false,
            )

        assertEquals(LibraryState.EMPTY, decision.libraryState)
        assertFalse(decision.requestScan)
    }

    @Test
    fun `prior usable cached load empty is recovery not intentional empty`() {
        val decision =
            StartupLibraryPolicy.onCachedLoadSucceeded(
                priorState = LibraryState.USABLE,
                songCount = 0,
                lastScanFailed = false,
            )

        assertEquals(LibraryState.RECOVERY, decision.libraryState)
        assertTrue(decision.requestScan)
    }

    @Test
    fun `scan failure preserves last known usable state`() {
        assertEquals(LibraryState.USABLE, StartupLibraryPolicy.onIndexFailed(LibraryState.USABLE))
    }

    @Test
    fun `scan failure before first library becomes recovery`() {
        assertEquals(LibraryState.RECOVERY, StartupLibraryPolicy.onIndexFailed(LibraryState.NEVER))
    }

    @Test
    fun `duplicate startup with in-memory library does not attempt cached load`() {
        assertFalse(
            StartupLibraryPolicy.shouldAttemptCachedLoad(
                hasInMemoryLibrary = true,
                revisionKnown = true,
            )
        )
    }

    @Test
    fun `manual refresh and rescan remain distinct cache modes`() {
        assertEquals(true, MusicScanRequestMode.REFRESH_WITH_CACHE)
        assertEquals(false, MusicScanRequestMode.RESCAN_WITH_CACHE)
    }

    @Test
    fun `production startup emits usable cached library and does not request scan`() = runBlocking {
        val harness = StartupHarness(priorState = LibraryState.USABLE)

        val decision = harness.run(cachedSongCount = 8)

        assertEquals(LibraryState.USABLE, decision.libraryState)
        assertEquals(8, harness.emittedSongCount)
        assertEquals(emptyList<Boolean>(), harness.scanRequests)
        assertEquals(LibraryState.USABLE, harness.persistedState)
    }

    @Test
    fun `production startup cached failure enters recovery and requests one cached scan`() =
        runBlocking {
            val harness = StartupHarness(priorState = LibraryState.USABLE)

            val decision = harness.run(loadFailure = IllegalStateException("bad cache"))

            assertEquals(LibraryState.RECOVERY, decision.libraryState)
            assertEquals(LibraryState.RECOVERY, harness.persistedState)
            assertEquals(listOf(MusicScanRequestMode.REFRESH_WITH_CACHE), harness.scanRequests)
            assertEquals(1, harness.cachedLoadFailures)
            assertEquals(null, harness.emittedSongCount)
        }

    @Test
    fun `production startup cached empty after usable is recoverable not intentional empty`() =
        runBlocking {
            val harness = StartupHarness(priorState = LibraryState.USABLE)

            val decision = harness.run(cachedSongCount = 0)

            assertEquals(LibraryState.RECOVERY, decision.libraryState)
            assertEquals(LibraryState.RECOVERY, harness.persistedState)
            assertEquals(listOf(MusicScanRequestMode.REFRESH_WITH_CACHE), harness.scanRequests)
            assertEquals(null, harness.emittedSongCount)
        }

    @Test
    fun `production startup first launch requests one cached scan`() = runBlocking {
        val harness = StartupHarness(priorState = LibraryState.NEVER, revisionKnown = false)

        val decision = harness.run()

        assertEquals(LibraryState.NEVER, decision.libraryState)
        assertEquals(listOf(MusicScanRequestMode.REFRESH_WITH_CACHE), harness.scanRequests)
        assertEquals(0, harness.cachedLoadAttempts)
    }

    @Test
    fun `production startup prior empty stays intentional and does not scan`() = runBlocking {
        val harness = StartupHarness(priorState = LibraryState.EMPTY)

        val decision = harness.run(cachedSongCount = 0)

        assertEquals(LibraryState.EMPTY, decision.libraryState)
        assertEquals(LibraryState.EMPTY, harness.persistedState)
        assertEquals(emptyList<Boolean>(), harness.scanRequests)
    }

    @Test
    fun `production duplicate startup with memory library does not load cache or scan`() =
        runBlocking {
            val harness =
                StartupHarness(priorState = LibraryState.USABLE, hasInMemoryLibrary = true)

            val decision = harness.run(cachedSongCount = 9)

            assertEquals(LibraryState.USABLE, decision.libraryState)
            assertEquals(0, harness.cachedLoadAttempts)
            assertEquals(null, harness.emittedSongCount)
            assertEquals(emptyList<Boolean>(), harness.scanRequests)
        }

    private class StartupHarness(
        private val priorState: LibraryState,
        private val revisionKnown: Boolean = true,
        private val hasInMemoryLibrary: Boolean = false,
        private val lastScanFailed: Boolean = false,
    ) {
        var persistedState: LibraryState? = null
        var emittedSongCount: Int? = null
        var cachedLoadFailures = 0
        var cachedLoadAttempts = 0
        val scanRequests = mutableListOf<Boolean>()

        suspend fun run(
            cachedSongCount: Int = 0,
            loadFailure: Exception? = null,
        ): StartupLibraryPolicy.Decision =
            StartupLibraryStartup.run(
                hasInMemoryLibrary = hasInMemoryLibrary,
                revisionKnown = revisionKnown,
                priorState = priorState,
                lastScanFailed = { lastScanFailed },
                loadCachedLibrary = {
                    cachedLoadAttempts++
                    loadFailure?.let { throw it }
                    cachedSongCount
                },
                cachedSongCount = { it },
                emitCachedLibrary = { emittedSongCount = it },
                emitCachedLoadFailure = { cachedLoadFailures++ },
                setLibraryState = { persistedState = it },
                requestIndex = { scanRequests.add(it) },
            )
    }
}
