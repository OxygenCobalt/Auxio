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
import org.junit.Assert.assertTrue
import org.junit.Test

class DateTest {
    @Test
    fun date_equals_varyingPrecision() {
        assertTrue(
            requireNotNull(Date.from("2016-08-16T00:01:02")) !=
                requireNotNull(Date.from("2016-08-16")))
    }

    @Test
    fun date_compareTo_dates() {
        val a = requireNotNull(Date.from("2016-08-16T00:01:02"))
        val b = requireNotNull(Date.from("2016-09-16T00:01:02"))
        assertEquals(-1, a.compareTo(b))
    }

    @Test
    fun date_compareTo_times() {
        val a = requireNotNull(Date.from("2016-08-16T00:02:02"))
        val b = requireNotNull(Date.from("2016-08-16T00:01:02"))
        assertEquals(1, a.compareTo(b))
    }

    @Test
    fun date_compareTo_varyingPrecision() {
        val a = requireNotNull(Date.from("2016-08-16T00:01:02"))
        val b = requireNotNull(Date.from("2016-08-16"))
        assertEquals(
            1,
            a.compareTo(b),
        )
    }

    @Test
    fun date_from_values() {
        assertEquals("2016", Date.from(2016).toString())
        assertEquals("2016-08-16", Date.from(2016, 8, 16).toString())
        assertEquals("2016-08-16T00:01Z", Date.from(2016, 8, 16, 0, 1).toString())
    }

    @Test
    fun date_from_yearDate() {
        assertEquals("2016-08-16", Date.from(20160816).toString())
        assertEquals("2016-08-16", Date.from("20160816").toString())
    }

    @Test
    fun date_from_timestamp() {
        assertEquals("2016-08-16T00:01:02Z", Date.from("2016-08-16T00:01:02").toString())
        assertEquals("2016-08-16T00:01:02Z", Date.from("2016-08-16 00:01:02").toString())
    }

    @Test
    fun date_from_lesserPrecision() {
        assertEquals("2016", Date.from("2016").toString())
        assertEquals("2016-08", Date.from("2016-08").toString())
        assertEquals("2016-08-16", Date.from("2016-08-16").toString())
        assertEquals("2016-08-16T00:01Z", Date.from("2016-08-16T00:01").toString())
    }

    @Test
    fun date_from_wack() {
        assertEquals(null, Date.from(0))
        assertEquals(null, Date.from(""))
        assertEquals(null, Date.from("2016-08-16:00:01:02"))
        assertEquals("2016-11", Date.from("2016-11-32 25:43:01").toString())
    }

    @Test
    fun dateRange_from_correct() {
        val range =
            requireNotNull(
                Date.Range.from(
                    listOf(
                        requireNotNull(Date.from("2016-08-16T00:01:02")),
                        requireNotNull(Date.from("2016-07-16")),
                        requireNotNull(Date.from("2014-03-12T00")),
                        requireNotNull(Date.from("2022-12-22T22:22:22")))))
        assertEquals("2014-03-12T00Z", range.min.toString())
        assertEquals("2022-12-22T22:22:22Z", range.max.toString())
    }

    @Test
    fun dateRange_from_one() {
        val range =
            requireNotNull(
                Date.Range.from(listOf(requireNotNull(Date.from("2016-08-16T00:01:02")))))
        assertEquals("2016-08-16T00:01:02Z", range.min.toString())
        assertEquals("2016-08-16T00:01:02Z", range.max.toString())
    }

    @Test
    fun dateRange_from_none() {
        assertEquals(null, Date.Range.from(listOf()))
    }
}
