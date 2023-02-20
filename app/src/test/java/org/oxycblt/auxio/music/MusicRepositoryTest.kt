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
import org.junit.Test
import org.oxycblt.auxio.music.model.FakeLibrary
import org.oxycblt.auxio.music.model.Library

class MusicRepositoryTest {
    @Test
    fun listeners() {
        val listener = TestListener()
        val impl =
            MusicRepositoryImpl().apply {
                library = null
                addListener(listener)
            }
        impl.library = TestLibrary(0)
        assertEquals(listOf(null, TestLibrary(0)), listener.updates)

        val listener2 = TestListener()
        impl.addListener(listener2)
        impl.library = TestLibrary(1)
        assertEquals(listOf(TestLibrary(0), TestLibrary(1)), listener2.updates)
    }

    private class TestListener : MusicRepository.Listener {
        val updates = mutableListOf<Library?>()

        override fun onLibraryChanged(library: Library?) {
            updates.add(library)
        }
    }

    private data class TestLibrary(private val id: Int) : FakeLibrary()
}
