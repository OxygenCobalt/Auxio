/*
 * Copyright (c) 2023 Auxio Project
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
import org.oxycblt.auxio.music.model.FakeLibrary
import org.oxycblt.auxio.music.system.FakeIndexer
import org.oxycblt.auxio.music.system.Indexer
import org.oxycblt.auxio.util.forceClear

class MusicViewModelTest {
    @Test
    fun indexerState() {
        val indexer =
            TestIndexer().apply { state = Indexer.State.Indexing(Indexer.Indexing.Indeterminate) }
        val musicViewModel = MusicViewModel(indexer)
        assertTrue(indexer.listener is MusicViewModel)
        assertEquals(
            Indexer.Indexing.Indeterminate,
            (musicViewModel.indexerState.value as Indexer.State.Indexing).indexing)
        indexer.state = null
        assertEquals(null, musicViewModel.indexerState.value)
        musicViewModel.forceClear()
        assertTrue(indexer.listener == null)
    }

    @Test
    fun statistics() {
        val indexer =
            TestIndexer().apply { state = Indexer.State.Complete(Result.success(TestLibrary())) }
        val musicViewModel = MusicViewModel(indexer)
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
        val indexer = TestIndexer()
        val musicViewModel = MusicViewModel(indexer)
        musicViewModel.refresh()
        musicViewModel.rescan()
        assertEquals(listOf(true, false), indexer.requests)
    }

    private class TestIndexer : FakeIndexer() {
        var listener: Indexer.Listener? = null
        var state: Indexer.State? = null
            set(value) {
                field = value
                listener?.onIndexerStateChanged(value)
            }

        val requests = mutableListOf<Boolean>()

        override fun registerListener(listener: Indexer.Listener) {
            this.listener = listener
            listener.onIndexerStateChanged(state)
        }

        override fun unregisterListener(listener: Indexer.Listener) {
            this.listener = null
        }

        override fun requestReindex(withCache: Boolean) {
            requests.add(withCache)
        }
    }

    private class TestLibrary : FakeLibrary() {
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
