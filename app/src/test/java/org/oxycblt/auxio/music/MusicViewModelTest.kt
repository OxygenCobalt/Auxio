/*
 * Copyright (c) 2023 Auxio Project
 * MusicViewModelTest.kt is part of Auxio.
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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.oxycblt.auxio.music.device.DeviceLibrary
import org.oxycblt.auxio.music.device.FakeDeviceLibrary
import org.oxycblt.auxio.util.forceClear

class MusicViewModelTest {
    @Test
    fun indexerState() {
        val indexer =
            TestMusicRepository().apply {
                indexingState = IndexingState.Indexing(IndexingProgress.Indeterminate)
            }
        val musicViewModel = MusicViewModel(indexer)
        assertTrue(indexer.updateListener is MusicViewModel)
        assertTrue(indexer.indexingListener is MusicViewModel)
        assertEquals(
            IndexingProgress.Indeterminate,
            (musicViewModel.indexingState.value as IndexingState.Indexing).progress)
        indexer.indexingState = null
        assertEquals(null, musicViewModel.indexingState.value)
        musicViewModel.forceClear()
        assertTrue(indexer.indexingListener == null)
    }

    @Test
    fun statistics() {
        val musicRepository = TestMusicRepository()
        val musicViewModel = MusicViewModel(musicRepository)
        assertEquals(null, musicViewModel.statistics.value)
        musicRepository.deviceLibrary = TestDeviceLibrary()
        assertEquals(
            MusicViewModel.Statistics(
                2,
                3,
                4,
                1,
                161616 * 2,
            ),
            musicViewModel.statistics.value)
    }

    @Test
    fun requests() {
        val indexer = TestMusicRepository()
        val musicViewModel = MusicViewModel(indexer)
        musicViewModel.refresh()
        musicViewModel.rescan()
        assertEquals(listOf(true, false), indexer.requests)
    }

    private class TestMusicRepository : FakeMusicRepository() {
        override var deviceLibrary: DeviceLibrary? = null
            set(value) {
                field = value
                updateListener?.onMusicChanges(
                    MusicRepository.Changes(deviceLibrary = true, userLibrary = false))
            }
        override var indexingState: IndexingState? = null
            set(value) {
                field = value
                indexingListener?.onIndexingStateChanged()
            }

        var updateListener: MusicRepository.UpdateListener? = null
        var indexingListener: MusicRepository.IndexingListener? = null
        val requests = mutableListOf<Boolean>()

        override fun addUpdateListener(listener: MusicRepository.UpdateListener) {
            listener.onMusicChanges(
                MusicRepository.Changes(deviceLibrary = true, userLibrary = false))
            this.updateListener = listener
        }

        override fun removeUpdateListener(listener: MusicRepository.UpdateListener) {
            this.updateListener = null
        }

        override fun addIndexingListener(listener: MusicRepository.IndexingListener) {
            listener.onIndexingStateChanged()
            this.indexingListener = listener
        }

        override fun removeIndexingListener(listener: MusicRepository.IndexingListener) {
            this.indexingListener = null
        }

        override fun requestIndex(withCache: Boolean) {
            requests.add(withCache)
        }
    }

    private class TestDeviceLibrary : FakeDeviceLibrary() {
        override val songs: List<Song>
            get() = listOf(TestSong(), TestSong())
        override val albums: List<Album>
            get() = listOf(FakeAlbum(), FakeAlbum(), FakeAlbum())
        override val artists: List<Artist>
            get() = listOf(FakeArtist(), FakeArtist(), FakeArtist(), FakeArtist())
        override val genres: List<Genre>
            get() = listOf(FakeGenre())
    }

    private class TestSong : FakeSong() {
        override val durationMs: Long
            get() = 161616
    }
}
