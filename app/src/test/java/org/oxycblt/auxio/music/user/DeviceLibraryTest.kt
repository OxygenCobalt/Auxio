/*
 * Copyright (c) 2023 Auxio Project
 * DeviceLibraryTest.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.user

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicType
import org.oxycblt.auxio.music.device.AlbumImpl
import org.oxycblt.auxio.music.device.ArtistImpl
import org.oxycblt.auxio.music.device.DeviceLibraryImpl
import org.oxycblt.auxio.music.device.GenreImpl
import org.oxycblt.auxio.music.device.SongImpl
import org.oxycblt.auxio.music.fs.Components
import org.oxycblt.auxio.music.fs.Path

class DeviceLibraryTest {

    @Test
    fun deviceLibrary_withSongs() {
        val songUidA = Music.UID.auxio(MusicType.SONGS)
        val songUidB = Music.UID.auxio(MusicType.SONGS)
        val songA =
            mockk<SongImpl> {
                every { uid } returns songUidA
                every { durationMs } returns 0
                every { path } returns Path(mockk(), Components.parseUnix("./"))
                every { finalize() } returns this
            }
        val songB =
            mockk<SongImpl> {
                every { uid } returns songUidB
                every { durationMs } returns 1
                every { path } returns Path(mockk(), Components.parseUnix("./"))
                every { finalize() } returns this
            }
        val deviceLibrary = DeviceLibraryImpl(listOf(songA, songB), listOf(), listOf(), listOf())
        verify {
            songA.finalize()
            songB.finalize()
        }
        val foundSongA = deviceLibrary.findSong(songUidA)!!
        assertEquals(songUidA, foundSongA.uid)
        assertEquals(0L, foundSongA.durationMs)
        val foundSongB = deviceLibrary.findSong(songUidB)!!
        assertEquals(songUidB, foundSongB.uid)
        assertEquals(1L, foundSongB.durationMs)
    }

    @Test
    fun deviceLibrary_withAlbums() {
        val albumUidA = Music.UID.auxio(MusicType.ALBUMS)
        val albumUidB = Music.UID.auxio(MusicType.ALBUMS)
        val albumA =
            mockk<AlbumImpl> {
                every { uid } returns albumUidA
                every { durationMs } returns 0
                every { finalize() } returns this
            }
        val albumB =
            mockk<AlbumImpl> {
                every { uid } returns albumUidB
                every { durationMs } returns 1
                every { finalize() } returns this
            }
        val deviceLibrary = DeviceLibraryImpl(listOf(), listOf(albumA, albumB), listOf(), listOf())
        verify {
            albumA.finalize()
            albumB.finalize()
        }
        val foundAlbumA = deviceLibrary.findAlbum(albumUidA)!!
        assertEquals(albumUidA, foundAlbumA.uid)
        assertEquals(0L, foundAlbumA.durationMs)
        val foundAlbumB = deviceLibrary.findAlbum(albumUidB)!!
        assertEquals(albumUidB, foundAlbumB.uid)
        assertEquals(1L, foundAlbumB.durationMs)
    }

    @Test
    fun deviceLibrary_withArtists() {
        val artistUidA = Music.UID.auxio(MusicType.ARTISTS)
        val artistUidB = Music.UID.auxio(MusicType.ARTISTS)
        val artistA =
            mockk<ArtistImpl> {
                every { uid } returns artistUidA
                every { durationMs } returns 0
                every { finalize() } returns this
            }
        val artistB =
            mockk<ArtistImpl> {
                every { uid } returns artistUidB
                every { durationMs } returns 1
                every { finalize() } returns this
            }
        val deviceLibrary =
            DeviceLibraryImpl(listOf(), listOf(), listOf(artistA, artistB), listOf())
        verify {
            artistA.finalize()
            artistB.finalize()
        }
        val foundArtistA = deviceLibrary.findArtist(artistUidA)!!
        assertEquals(artistUidA, foundArtistA.uid)
        assertEquals(0L, foundArtistA.durationMs)
        val foundArtistB = deviceLibrary.findArtist(artistUidB)!!
        assertEquals(artistUidB, foundArtistB.uid)
        assertEquals(1L, foundArtistB.durationMs)
    }

    @Test
    fun deviceLibrary_withGenres() {
        val genreUidA = Music.UID.auxio(MusicType.GENRES)
        val genreUidB = Music.UID.auxio(MusicType.GENRES)
        val genreA =
            mockk<GenreImpl> {
                every { uid } returns genreUidA
                every { durationMs } returns 0
                every { finalize() } returns this
            }
        val genreB =
            mockk<GenreImpl> {
                every { uid } returns genreUidB
                every { durationMs } returns 1
                every { finalize() } returns this
            }
        val deviceLibrary = DeviceLibraryImpl(listOf(), listOf(), listOf(), listOf(genreA, genreB))
        verify {
            genreA.finalize()
            genreB.finalize()
        }
        val foundGenreA = deviceLibrary.findGenre(genreUidA)!!
        assertEquals(genreUidA, foundGenreA.uid)
        assertEquals(0L, foundGenreA.durationMs)
        val foundGenreB = deviceLibrary.findGenre(genreUidB)!!
        assertEquals(genreUidB, foundGenreB.uid)
        assertEquals(1L, foundGenreB.durationMs)
    }

    @Test
    fun deviceLibrary_equals() {
        val songA =
            mockk<SongImpl> {
                every { uid } returns Music.UID.auxio(MusicType.SONGS)
                every { path } returns Path(mockk(), Components.parseUnix("./"))
                every { finalize() } returns this
            }
        val songB =
            mockk<SongImpl> {
                every { uid } returns Music.UID.auxio(MusicType.SONGS)
                every { path } returns Path(mockk(), Components.parseUnix("./"))
                every { finalize() } returns this
            }
        val album =
            mockk<AlbumImpl> {
                every { uid } returns mockk()
                every { finalize() } returns this
            }

        val deviceLibraryA = DeviceLibraryImpl(listOf(songA), listOf(album), listOf(), listOf())
        val deviceLibraryB = DeviceLibraryImpl(listOf(songA), listOf(), listOf(), listOf())
        val deviceLibraryC = DeviceLibraryImpl(listOf(songB), listOf(album), listOf(), listOf())
        assertEquals(deviceLibraryA, deviceLibraryB)
        assertEquals(deviceLibraryA.hashCode(), deviceLibraryA.hashCode())
        assertNotEquals(deviceLibraryA, deviceLibraryC)
        assertNotEquals(deviceLibraryA.hashCode(), deviceLibraryC.hashCode())
    }
}
