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

class DateTest {
    // TODO: Incomplete

    @Test
    fun date_fromYear() {
        assertEquals(Date.from(2016).toString(), "2016")
    }

    @Test
    fun date_fromDate() {
        assertEquals(Date.from(2016, 8, 16).toString(), "2016-08-16")
    }

    @Test
    fun date_fromDatetime() {
        assertEquals(Date.from(2016, 8, 16, 0, 1).toString(), "2016-08-16T00:01Z")
    }

    @Test
    fun date_fromFormalTimestamp() {
        assertEquals(Date.from("2016-08-16T00:01:02").toString(), "2016-08-16T00:01:02Z")
    }

    @Test
    fun date_fromSpacedTimestamp() {
        assertEquals(Date.from("2016-08-16 00:01:02").toString(), "2016-08-16T00:01:02Z")
    }

    @Test
    fun date_fromDatestamp() {
        assertEquals(Date.from("2016-08-16").toString(), "2016-08-16")
    }

    @Test
    fun date_fromWeirdDateTimestamp() {
        assertEquals(Date.from("2016-08-16T00:01").toString(), "2016-08-16T00:01Z")
    }

    @Test
    fun date_fromWeirdDatestamp() {
        assertEquals(Date.from("2016-08").toString(), "2016-08")
    }

    @Test
    fun date_fromYearStamp() {
        assertEquals(Date.from("2016").toString(), "2016")
    }

    @Test
    fun date_fromWackTimestamp() {
        assertEquals(Date.from("2016-11-32 25:43:01").toString(), "2016-11")
    }

    @Test
    fun date_fromWackYear() {
        assertEquals(Date.from(0), null)
    }
}
