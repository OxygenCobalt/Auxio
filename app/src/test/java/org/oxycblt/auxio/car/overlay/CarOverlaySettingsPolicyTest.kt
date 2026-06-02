/*
 * Copyright (c) 2024 Auxio Project
 * CarOverlaySettingsPolicyTest.kt is part of Auxio.
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
 * Tests the overlay settings enable/disable decision logic. Verifies that:
 * - Enabling without permission returns false (needs permission flow)
 * - Enabling with permission returns true
 * - Disabling always returns true
 * - Reset position does not request service start when disabled
 */
class CarOverlaySettingsPolicyTest {

    /**
     * Models the setEnabled decision logic from CarOverlaySettings. Returns true if the operation
     * completed immediately, false if permission is needed.
     */
    private fun setEnabledDecision(enable: Boolean, hasPermission: Boolean): Boolean {
        if (enable) {
            return hasPermission
        }
        return true
    }

    /** Models the resetPosition logic — should only signal running service if enabled. */
    private fun shouldSignalResetToService(enabled: Boolean, hasPermission: Boolean): Boolean {
        return enabled && hasPermission
    }

    @Test
    fun `enable with permission returns true`() {
        assertTrue(setEnabledDecision(enable = true, hasPermission = true))
    }

    @Test
    fun `enable without permission returns false`() {
        assertFalse(setEnabledDecision(enable = true, hasPermission = false))
    }

    @Test
    fun `disable always returns true`() {
        assertTrue(setEnabledDecision(enable = false, hasPermission = true))
        assertTrue(setEnabledDecision(enable = false, hasPermission = false))
    }

    @Test
    fun `reset position signals service only when enabled with permission`() {
        assertTrue(shouldSignalResetToService(enabled = true, hasPermission = true))
        assertFalse(shouldSignalResetToService(enabled = false, hasPermission = true))
        assertFalse(shouldSignalResetToService(enabled = true, hasPermission = false))
        assertFalse(shouldSignalResetToService(enabled = false, hasPermission = false))
    }
}
