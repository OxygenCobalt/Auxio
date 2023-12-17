/*
 * Copyright (c) 2023 Auxio Project
 * CacheRepositoryTest.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.cache

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerifyAll
import io.mockk.coVerifySequence
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import java.lang.IllegalStateException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.oxycblt.auxio.music.device.RawSong
import org.oxycblt.auxio.music.info.Date

class CacheRepositoryTest {
    @Test
    fun cache_read_noInvalidate() {
        val dao =
            mockk<CachedSongsDao> {
                coEvery { readSongs() }.returnsMany(listOf(CACHED_SONG_A, CACHED_SONG_B))
            }
        val cacheRepository = CacheRepositoryImpl(dao)
        val cache = requireNotNull(runBlocking { cacheRepository.readCache() })
        coVerifyAll { dao.readSongs() }
        assertFalse(cache.invalidated)

        val songA = RawSong(mediaStoreId = 0, dateAdded = 1, dateModified = 2)
        assertTrue(cache.populate(songA))
        assertEquals(RAW_SONG_A, songA)

        assertFalse(cache.invalidated)

        val songB = RawSong(mediaStoreId = 9, dateAdded = 10, dateModified = 11)
        assertTrue(cache.populate(songB))
        assertEquals(RAW_SONG_B, songB)

        assertFalse(cache.invalidated)
    }

    @Test
    fun cache_read_invalidate() {
        val dao =
            mockk<CachedSongsDao> {
                coEvery { readSongs() }.returnsMany(listOf(CACHED_SONG_A, CACHED_SONG_B))
            }
        val cacheRepository = CacheRepositoryImpl(dao)
        val cache = requireNotNull(runBlocking { cacheRepository.readCache() })
        coVerifyAll { dao.readSongs() }
        assertFalse(cache.invalidated)

        val nullStart = RawSong(mediaStoreId = 0, dateAdded = 0, dateModified = 0)
        val nullEnd = RawSong(mediaStoreId = 0, dateAdded = 0, dateModified = 0)
        assertFalse(cache.populate(nullStart))
        assertEquals(nullStart, nullEnd)

        assertTrue(cache.invalidated)

        val songB = RawSong(mediaStoreId = 9, dateAdded = 10, dateModified = 11)
        assertTrue(cache.populate(songB))
        assertEquals(RAW_SONG_B, songB)

        assertTrue(cache.invalidated)
    }

    @Test
    fun cache_read_crashes() {
        val dao = mockk<CachedSongsDao> { coEvery { readSongs() } throws IllegalStateException() }
        val cacheRepository = CacheRepositoryImpl(dao)
        assertEquals(null, runBlocking { cacheRepository.readCache() })
        coVerifyAll { dao.readSongs() }
    }

    @Test
    fun cache_write() {
        var currentlyStoredSongs = listOf<CachedSong>()
        val insertSongsArg = slot<List<CachedSong>>()
        val dao =
            mockk<CachedSongsDao> {
                coEvery { nukeSongs() } answers { currentlyStoredSongs = listOf() }

                coEvery { insertSongs(capture(insertSongsArg)) } answers
                    {
                        currentlyStoredSongs = insertSongsArg.captured
                    }
            }

        val cacheRepository = CacheRepositoryImpl(dao)

        val rawSongs = listOf(RAW_SONG_A, RAW_SONG_B)
        runBlocking { cacheRepository.writeCache(rawSongs) }

        val cachedSongs = listOf(CACHED_SONG_A, CACHED_SONG_B)
        coVerifySequence {
            dao.nukeSongs()
            dao.insertSongs(cachedSongs)
        }
        assertEquals(cachedSongs, currentlyStoredSongs)
    }

    @Test
    fun cache_write_nukeCrashes() {
        val dao =
            mockk<CachedSongsDao> {
                coEvery { nukeSongs() } throws IllegalStateException()
                coEvery { insertSongs(listOf()) } just Runs
            }
        val cacheRepository = CacheRepositoryImpl(dao)
        runBlocking { cacheRepository.writeCache(listOf()) }
        coVerifyAll { dao.nukeSongs() }
    }

    @Test
    fun cache_write_insertCrashes() {
        val dao =
            mockk<CachedSongsDao> {
                coEvery { nukeSongs() } just Runs
                coEvery { insertSongs(listOf()) } throws IllegalStateException()
            }
        val cacheRepository = CacheRepositoryImpl(dao)
        runBlocking { cacheRepository.writeCache(listOf()) }
        coVerifySequence {
            dao.nukeSongs()
            dao.insertSongs(listOf())
        }
    }

    private companion object {
        val CACHED_SONG_A =
            CachedSong(
                mediaStoreId = 0,
                dateAdded = 1,
                dateModified = 2,
                size = 3,
                durationMs = 4,
                replayGainTrackAdjustment = 5.5f,
                replayGainAlbumAdjustment = 6.6f,
                musicBrainzId = "Song MBID A",
                name = "Song Name A",
                sortName = "Song Sort Name A",
                track = 7,
                disc = 8,
                subtitle = "Subtitle A",
                date = Date.from("2020-10-10"),
                albumMusicBrainzId = "Album MBID A",
                albumName = "Album Name A",
                albumSortName = "Album Sort Name A",
                releaseTypes = listOf("Release Type A"),
                artistMusicBrainzIds = listOf("Artist MBID A"),
                artistNames = listOf("Artist Name A"),
                artistSortNames = listOf("Artist Sort Name A"),
                albumArtistMusicBrainzIds = listOf("Album Artist MBID A"),
                albumArtistNames = listOf("Album Artist Name A"),
                albumArtistSortNames = listOf("Album Artist Sort Name A"),
                genreNames = listOf("Genre Name A"),
            )

        val RAW_SONG_A =
            RawSong(
                mediaStoreId = 0,
                dateAdded = 1,
                dateModified = 2,
                size = 3,
                durationMs = 4,
                replayGainTrackAdjustment = 5.5f,
                replayGainAlbumAdjustment = 6.6f,
                musicBrainzId = "Song MBID A",
                name = "Song Name A",
                sortName = "Song Sort Name A",
                track = 7,
                disc = 8,
                subtitle = "Subtitle A",
                date = Date.from("2020-10-10"),
                albumMusicBrainzId = "Album MBID A",
                albumName = "Album Name A",
                albumSortName = "Album Sort Name A",
                releaseTypes = listOf("Release Type A"),
                artistMusicBrainzIds = listOf("Artist MBID A"),
                artistNames = listOf("Artist Name A"),
                artistSortNames = listOf("Artist Sort Name A"),
                albumArtistMusicBrainzIds = listOf("Album Artist MBID A"),
                albumArtistNames = listOf("Album Artist Name A"),
                albumArtistSortNames = listOf("Album Artist Sort Name A"),
                genreNames = listOf("Genre Name A"),
            )

        val CACHED_SONG_B =
            CachedSong(
                mediaStoreId = 9,
                dateAdded = 10,
                dateModified = 11,
                size = 12,
                durationMs = 13,
                replayGainTrackAdjustment = 14.14f,
                replayGainAlbumAdjustment = 15.15f,
                musicBrainzId = "Song MBID B",
                name = "Song Name B",
                sortName = "Song Sort Name B",
                track = 16,
                disc = 17,
                subtitle = "Subtitle B",
                date = Date.from("2021-11-11"),
                albumMusicBrainzId = "Album MBID B",
                albumName = "Album Name B",
                albumSortName = "Album Sort Name B",
                releaseTypes = listOf("Release Type B"),
                artistMusicBrainzIds = listOf("Artist MBID B"),
                artistNames = listOf("Artist Name B"),
                artistSortNames = listOf("Artist Sort Name B"),
                albumArtistMusicBrainzIds = listOf("Album Artist MBID B"),
                albumArtistNames = listOf("Album Artist Name B"),
                albumArtistSortNames = listOf("Album Artist Sort Name B"),
                genreNames = listOf("Genre Name B"),
            )

        val RAW_SONG_B =
            RawSong(
                mediaStoreId = 9,
                dateAdded = 10,
                dateModified = 11,
                size = 12,
                durationMs = 13,
                replayGainTrackAdjustment = 14.14f,
                replayGainAlbumAdjustment = 15.15f,
                musicBrainzId = "Song MBID B",
                name = "Song Name B",
                sortName = "Song Sort Name B",
                track = 16,
                disc = 17,
                subtitle = "Subtitle B",
                date = Date.from("2021-11-11"),
                albumMusicBrainzId = "Album MBID B",
                albumName = "Album Name B",
                albumSortName = "Album Sort Name B",
                releaseTypes = listOf("Release Type B"),
                artistMusicBrainzIds = listOf("Artist MBID B"),
                artistNames = listOf("Artist Name B"),
                artistSortNames = listOf("Artist Sort Name B"),
                albumArtistMusicBrainzIds = listOf("Album Artist MBID B"),
                albumArtistNames = listOf("Album Artist Name B"),
                albumArtistSortNames = listOf("Album Artist Sort Name B"),
                genreNames = listOf("Genre Name B"),
            )
    }
}
