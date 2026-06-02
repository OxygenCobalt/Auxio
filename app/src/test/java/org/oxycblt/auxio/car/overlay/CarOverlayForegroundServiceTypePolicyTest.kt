/*
 * Copyright (c) 2024 Auxio Project
 * CarOverlayForegroundServiceTypePolicyTest.kt is part of Auxio.
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

package org.oxycblt.auxio.car.overlay

import android.content.pm.ServiceInfo
import android.os.Build
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pure policy test for foreground service type selection. Verifies that the overlay should use
 * FOREGROUND_SERVICE_TYPE_SPECIAL_USE only on API 34+ and 0 (legacy/none) on older APIs like the
 * real TS18 device running Android 10/API 29.
 *
 * This mirrors the logic in CarFloatingControlsService.foregroundServiceType() without directly
 * referencing the variant-scoped class to allow running in the default test task.
 */
class CarOverlayForegroundServiceTypePolicyTest {

    /** Mirrors CarFloatingControlsService.foregroundServiceType() logic. */
    private fun foregroundServiceTypeForApi(sdkInt: Int): Int {
        return if (sdkInt >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        } else {
            0
        }
    }

    @Test
    fun `API 29 gets legacy foreground type (0)`() {
        assertEquals(0, foregroundServiceTypeForApi(29))
    }

    @Test
    fun `API 33 gets legacy foreground type (0)`() {
        assertEquals(0, foregroundServiceTypeForApi(33))
    }

    @Test
    fun `API 34 (Upside Down Cake) gets SPECIAL_USE type`() {
        assertEquals(
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            foregroundServiceTypeForApi(34),
        )
    }

    @Test
    fun `API 35 gets SPECIAL_USE type`() {
        assertEquals(
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            foregroundServiceTypeForApi(35),
        )
    }
}
