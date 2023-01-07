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

class AlbumTypeTest {
    @Test
    fun albumType_parse_primary() {
        assertEquals(Album.Type.Album(null), Album.Type.parse(listOf("album")))
        assertEquals(Album.Type.EP(null), Album.Type.parse(listOf("ep")))
        assertEquals(Album.Type.Single(null), Album.Type.parse(listOf("single")))
    }

    @Test
    fun albumType_parse_secondary() {
        assertEquals(Album.Type.Compilation(null), Album.Type.parse(listOf("album", "compilation")))
        assertEquals(Album.Type.Soundtrack, Album.Type.parse(listOf("album", "soundtrack")))
        assertEquals(Album.Type.Mix, Album.Type.parse(listOf("album", "dj-mix")))
        assertEquals(Album.Type.Mixtape, Album.Type.parse(listOf("album", "mixtape/street")))
    }

    @Test
    fun albumType_parse_modifiers() {
        assertEquals(
            Album.Type.Album(Album.Type.Refinement.LIVE), Album.Type.parse(listOf("album", "live")))
        assertEquals(
            Album.Type.Album(Album.Type.Refinement.REMIX),
            Album.Type.parse(listOf("album", "remix")))
        assertEquals(
            Album.Type.EP(Album.Type.Refinement.LIVE), Album.Type.parse(listOf("ep", "live")))
        assertEquals(
            Album.Type.EP(Album.Type.Refinement.REMIX), Album.Type.parse(listOf("ep", "remix")))
        assertEquals(
            Album.Type.Single(Album.Type.Refinement.LIVE),
            Album.Type.parse(listOf("single", "live")))
        assertEquals(
            Album.Type.Single(Album.Type.Refinement.REMIX),
            Album.Type.parse(listOf("single", "remix")))
    }

    @Test
    fun albumType_parse_secondaryModifiers() {
        assertEquals(
            Album.Type.Compilation(Album.Type.Refinement.LIVE),
            Album.Type.parse(listOf("album", "compilation", "live")))
        assertEquals(
            Album.Type.Compilation(Album.Type.Refinement.REMIX),
            Album.Type.parse(listOf("album", "compilation", "remix")))
    }

    @Test
    fun albumType_parse_orphanedSecondary() {
        assertEquals(Album.Type.Compilation(null), Album.Type.parse(listOf("compilation")))
        assertEquals(Album.Type.Soundtrack, Album.Type.parse(listOf("soundtrack")))
        assertEquals(Album.Type.Mix, Album.Type.parse(listOf("dj-mix")))
        assertEquals(Album.Type.Mixtape, Album.Type.parse(listOf("mixtape/street")))
    }

    @Test
    fun albumType_parse_orphanedModifier() {
        assertEquals(Album.Type.Album(Album.Type.Refinement.LIVE), Album.Type.parse(listOf("live")))
        assertEquals(
            Album.Type.Album(Album.Type.Refinement.REMIX), Album.Type.parse(listOf("remix")))
    }
}
