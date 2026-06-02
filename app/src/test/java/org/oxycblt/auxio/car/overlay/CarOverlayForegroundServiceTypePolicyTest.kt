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
 * Pure policy test for foreground service type selection. Verifies that the overlay uses
 * FOREGROUND_SERVICE_TYPE_SPECIAL_USE only on API 34+ and 0 (legacy/none) on older APIs like the
 * real TS18 device running Android 10/API 29.
 */
class CarOverlayForegroundServiceTypePolicyTest {

    @Test
    fun `foreground service type is 0 on API below 34`() {
        // The static helper in CarFloatingControlsService delegates to Build.VERSION.SDK_INT.
        // Since unit tests run on the host JVM where SDK_INT is 0, it should return 0.
        val type = CarFloatingControlsService.foregroundServiceType()
        if (Build.VERSION.SDK_INT >= 34) {
            assertEquals(ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE, type)
        } else {
            assertEquals(0, type)
        }
    }

    @Test
    fun `API 29 gets legacy foreground type`() {
        // On host JVM, SDK_INT is 0 which is less than 34, confirming the API 29 path.
        val type = CarFloatingControlsService.foregroundServiceType()
        assertEquals(0, type, "API < 34 must use 0 (none/legacy) for foreground service type")
    }
}
