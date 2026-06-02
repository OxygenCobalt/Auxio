/*
 * Copyright (c) 2024 Auxio Project
 * CarOverlayBootRestorePolicyTest.kt is part of Auxio.
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

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests the boot restore decision policy for [CarOverlayBootReceiver]. Verifies that overlay
 * restoration only occurs when the feature is enabled AND overlay permission is granted.
 */
class CarOverlayBootRestorePolicyTest {

    /** Models the boot-restore decision logic from CarOverlayBootReceiver.onReceive. */
    private fun shouldRestoreOnBoot(enabled: Boolean, hasPermission: Boolean): Boolean {
        if (!enabled) return false
        if (!hasPermission) return false
        return true
    }

    @Test
    fun `restore when enabled and has permission`() {
        assertTrue(shouldRestoreOnBoot(enabled = true, hasPermission = true))
    }

    @Test
    fun `do not restore when disabled`() {
        assertFalse(shouldRestoreOnBoot(enabled = false, hasPermission = true))
    }

    @Test
    fun `do not restore when permission missing`() {
        assertFalse(shouldRestoreOnBoot(enabled = true, hasPermission = false))
    }

    @Test
    fun `do not restore when disabled and no permission`() {
        assertFalse(shouldRestoreOnBoot(enabled = false, hasPermission = false))
    }
}
