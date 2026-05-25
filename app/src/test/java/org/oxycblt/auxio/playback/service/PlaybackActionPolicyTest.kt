/*
 * Copyright (c) 2024 Auxio Project
 * PlaybackActionPolicyTest.kt is part of Auxio.
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

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaybackActionPolicyTest {
    @Test
    fun `known playback actions are supported`() {
        assertTrue(PlaybackActionPolicy.isSupportedAction(PlaybackActions.ACTION_PLAY_PAUSE))
        assertTrue(PlaybackActionPolicy.isSupportedAction(PlaybackActions.ACTION_SKIP_NEXT))
        assertTrue(PlaybackActionPolicy.isSupportedAction(PlaybackActions.ACTION_EXIT))
    }

    @Test
    fun `unknown or null actions are rejected safely`() {
        assertFalse(PlaybackActionPolicy.isSupportedAction(null))
        assertFalse(PlaybackActionPolicy.isSupportedAction("org.oxycblt.auxio.action.UNKNOWN"))
    }
}
