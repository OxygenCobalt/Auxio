/*
 * Copyright (c) 2024 Auxio Project
 * PendingIntentRequestCodePolicyTest.kt is part of Auxio.
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

package org.oxycblt.auxio.playback.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PendingIntentRequestCodePolicyTest {
    @Test
    fun `same action returns stable request code`() {
        val action = "org.oxycblt.auxio.action.OPEN_QUEUE"
        assertEquals(
            PendingIntentRequestCodePolicy.forAction(action),
            PendingIntentRequestCodePolicy.forAction(action),
        )
    }

    @Test
    fun `different actions use different request codes`() {
        assertNotEquals(
            PendingIntentRequestCodePolicy.forAction("a"),
            PendingIntentRequestCodePolicy.forAction("b"),
        )
    }
}
