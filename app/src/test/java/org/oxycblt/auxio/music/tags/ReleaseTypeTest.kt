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
 
package org.oxycblt.auxio.music.tags

import org.junit.Assert.assertEquals
import org.junit.Test

class ReleaseTypeTest {
    @Test
    fun releaseType_parse_primary() {
        assertEquals(ReleaseType.Album(null), ReleaseType.parse(listOf("album")))
        assertEquals(ReleaseType.EP(null), ReleaseType.parse(listOf("ep")))
        assertEquals(ReleaseType.Single(null), ReleaseType.parse(listOf("single")))
    }

    @Test
    fun releaseType_parse_secondary() {
        assertEquals(
            ReleaseType.Compilation(null), ReleaseType.parse(listOf("album", "compilation")))
        assertEquals(ReleaseType.Soundtrack, ReleaseType.parse(listOf("album", "soundtrack")))
        assertEquals(ReleaseType.Mix, ReleaseType.parse(listOf("album", "dj-mix")))
        assertEquals(ReleaseType.Mixtape, ReleaseType.parse(listOf("album", "mixtape/street")))
    }

    @Test
    fun releaseType_parse_modifiers() {
        assertEquals(
            ReleaseType.Album(ReleaseType.Refinement.LIVE),
            ReleaseType.parse(listOf("album", "live")))
        assertEquals(
            ReleaseType.Album(ReleaseType.Refinement.REMIX),
            ReleaseType.parse(listOf("album", "remix")))
        assertEquals(
            ReleaseType.EP(ReleaseType.Refinement.LIVE), ReleaseType.parse(listOf("ep", "live")))
        assertEquals(
            ReleaseType.EP(ReleaseType.Refinement.REMIX), ReleaseType.parse(listOf("ep", "remix")))
        assertEquals(
            ReleaseType.Single(ReleaseType.Refinement.LIVE),
            ReleaseType.parse(listOf("single", "live")))
        assertEquals(
            ReleaseType.Single(ReleaseType.Refinement.REMIX),
            ReleaseType.parse(listOf("single", "remix")))
    }

    @Test
    fun releaseType_parse_secondaryModifiers() {
        assertEquals(
            ReleaseType.Compilation(ReleaseType.Refinement.LIVE),
            ReleaseType.parse(listOf("album", "compilation", "live")))
        assertEquals(
            ReleaseType.Compilation(ReleaseType.Refinement.REMIX),
            ReleaseType.parse(listOf("album", "compilation", "remix")))
    }

    @Test
    fun releaseType_parse_orphanedSecondary() {
        assertEquals(ReleaseType.Compilation(null), ReleaseType.parse(listOf("compilation")))
        assertEquals(ReleaseType.Soundtrack, ReleaseType.parse(listOf("soundtrack")))
        assertEquals(ReleaseType.Mix, ReleaseType.parse(listOf("dj-mix")))
        assertEquals(ReleaseType.Mixtape, ReleaseType.parse(listOf("mixtape/street")))
    }

    @Test
    fun releaseType_parse_orphanedModifier() {
        assertEquals(
            ReleaseType.Album(ReleaseType.Refinement.LIVE), ReleaseType.parse(listOf("live")))
        assertEquals(
            ReleaseType.Album(ReleaseType.Refinement.REMIX), ReleaseType.parse(listOf("remix")))
    }
}
