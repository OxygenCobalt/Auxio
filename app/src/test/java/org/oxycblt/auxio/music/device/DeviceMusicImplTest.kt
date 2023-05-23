/*
 * Copyright (c) 2023 Auxio Project
 * DeviceMusicImplTest.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.device

import java.util.UUID
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceMusicImplTest {
    @Test
    fun albumRaw_equals_inconsistentCase() {
        val a =
            RawAlbum(
                mediaStoreId = -1,
                musicBrainzId = null,
                name = "Paraglow",
                sortName = null,
                releaseType = null,
                rawArtists = listOf(RawArtist(name = "Parannoul"), RawArtist(name = "Asian Glow")))
        val b =
            RawAlbum(
                mediaStoreId = -1,
                musicBrainzId = null,
                name = "paraglow",
                sortName = null,
                releaseType = null,
                rawArtists = listOf(RawArtist(name = "Parannoul"), RawArtist(name = "Asian glow")))
        assertTrue(a == b)
        assertTrue(a.hashCode() == b.hashCode())
    }

    @Test
    fun albumRaw_equals_withMbids() {
        val a =
            RawAlbum(
                mediaStoreId = -1,
                musicBrainzId = UUID.fromString("c7b245c9-8099-32ea-af95-893acedde2cf"),
                name = "Weezer",
                sortName = "Blue Album",
                releaseType = null,
                rawArtists = listOf(RawArtist(name = "Weezer")))
        val b =
            RawAlbum(
                mediaStoreId = -1,
                musicBrainzId = UUID.fromString("923d5ba6-7eee-3bce-bcb2-c913b2bd69d4"),
                name = "Weezer",
                sortName = "Green Album",
                releaseType = null,
                rawArtists = listOf(RawArtist(name = "Weezer")))
        assertTrue(a != b)
        assertTrue(a.hashCode() != b.hashCode())
    }

    @Test
    fun albumRaw_equals_inconsistentMbids() {
        val a =
            RawAlbum(
                mediaStoreId = -1,
                musicBrainzId = UUID.fromString("c7b245c9-8099-32ea-af95-893acedde2cf"),
                name = "Weezer",
                sortName = "Blue Album",
                releaseType = null,
                rawArtists = listOf(RawArtist(name = "Weezer")))
        val b =
            RawAlbum(
                mediaStoreId = -1,
                musicBrainzId = null,
                name = "Weezer",
                sortName = "Green Album",
                releaseType = null,
                rawArtists = listOf(RawArtist(name = "Weezer")))
        assertTrue(a != b)
        assertTrue(a.hashCode() != b.hashCode())
    }

    @Test
    fun albumRaw_equals_withArtists() {
        val a =
            RawAlbum(
                mediaStoreId = -1,
                musicBrainzId = null,
                name = "Album",
                sortName = null,
                releaseType = null,
                rawArtists = listOf(RawArtist(name = "Artist A")))
        val b =
            RawAlbum(
                mediaStoreId = -1,
                musicBrainzId = null,
                name = "Album",
                sortName = null,
                releaseType = null,
                rawArtists = listOf(RawArtist(name = "Artist B")))
        assertTrue(a != b)
        assertTrue(a.hashCode() != b.hashCode())
    }

    @Test
    fun artistRaw_equals_inconsistentCase() {
        val a = RawArtist(musicBrainzId = null, name = "Parannoul")
        val b = RawArtist(musicBrainzId = null, name = "parannoul")
        assertTrue(a == b)
        assertTrue(a.hashCode() == b.hashCode())
    }

    @Test
    fun artistRaw_equals_withMbids() {
        val a =
            RawArtist(
                musicBrainzId = UUID.fromString("677325ef-d850-44bb-8258-0d69bbc0b3f7"),
                name = "Artist")
        val b =
            RawArtist(
                musicBrainzId = UUID.fromString("6b625592-d88d-48c8-ac1a-c5b476d78bcc"),
                name = "Artist")
        assertTrue(a != b)
        assertTrue(a.hashCode() != b.hashCode())
    }

    @Test
    fun artistRaw_equals_inconsistentMbids() {
        val a =
            RawArtist(
                musicBrainzId = UUID.fromString("677325ef-d850-44bb-8258-0d69bbc0b3f7"),
                name = "Artist")
        val b = RawArtist(musicBrainzId = null, name = "Artist")
        assertTrue(a != b)
        assertTrue(a.hashCode() != b.hashCode())
    }

    @Test
    fun artistRaw_equals_missingNames() {
        val a = RawArtist(name = null)
        val b = RawArtist(name = null)
        assertTrue(a == b)
        assertTrue(a.hashCode() == b.hashCode())
    }

    @Test
    fun artistRaw_equals_inconsistentNames() {
        val a = RawArtist(name = null)
        val b = RawArtist(name = "Parannoul")
        assertTrue(a != b)
        assertTrue(a.hashCode() != b.hashCode())
    }

    @Test
    fun genreRaw_equals_inconsistentCase() {
        val a = RawGenre("Future Garage")
        val b = RawGenre("future garage")
        assertTrue(a == b)
        assertTrue(a.hashCode() == b.hashCode())
    }

    @Test
    fun genreRaw_equals_missingNames() {
        val a = RawGenre(name = null)
        val b = RawGenre(name = null)
        assertTrue(a == b)
        assertTrue(a.hashCode() == b.hashCode())
    }

    @Test
    fun genreRaw_equals_inconsistentNames() {
        val a = RawGenre(name = null)
        val b = RawGenre(name = "Future Garage")
        assertTrue(a != b)
        assertTrue(a.hashCode() != b.hashCode())
    }
}
