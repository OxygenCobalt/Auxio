/*
 * Copyright (c) 2026 Auxio Project
 * PlaybackButtonSquareLayoutTest.kt is part of Auxio.
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

package org.oxycblt.auxio.ui

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.oxycblt.auxio.R
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class PlaybackButtonSquareLayoutTest {
    @Test
    @Config(qualifiers = "w1280dp-h720dp-land")
    fun `playback bar controls are square in landscape`() {
        assertPlaybackButtonsSquare(
            R.layout.fragment_playback_bar,
            LANDSCAPE_WIDTH,
            LANDSCAPE_HEIGHT,
        )
    }

    @Test
    @Config(qualifiers = "w320dp-h320dp-port")
    fun `default playback panel controls are square`() {
        assertPlaybackButtonsSquare(R.layout.fragment_playback_panel, COMPACT_WIDTH, COMPACT_HEIGHT)
    }

    @Test
    @Config(qualifiers = "w640dp-h360dp-land")
    fun `h360 playback panel controls are square`() {
        assertPlaybackButtonsSquare(R.layout.fragment_playback_panel, H360_WIDTH, H360_HEIGHT)
    }

    @Test
    @Config(qualifiers = "w1280dp-h720dp-land")
    fun `h520 playback panel controls are square in landscape`() {
        assertPlaybackButtonsSquare(
            R.layout.fragment_playback_panel,
            LANDSCAPE_WIDTH,
            LANDSCAPE_HEIGHT,
        )
    }

    @Test
    @Config(qualifiers = "w720dp-h1280dp-port")
    fun `h520 playback panel controls are square in portrait`() {
        assertPlaybackButtonsSquare(
            R.layout.fragment_playback_panel,
            PORTRAIT_WIDTH,
            PORTRAIT_HEIGHT,
        )
    }

    private fun assertPlaybackButtonsSquare(@LayoutRes layoutId: Int, widthPx: Int, heightPx: Int) {
        val root = inflateAndLayout(layoutId, widthPx, heightPx)
        for (id in PLAYBACK_BUTTON_IDS) {
            val button = root.findViewById<View>(id)
            assertTrue("Missing playback button id=$id", button != null)
            assertEquals(
                "Playback button ${root.resources.getResourceEntryName(id)} must be square",
                button.measuredHeight,
                button.measuredWidth,
            )
        }
    }

    private fun inflateAndLayout(@LayoutRes layoutId: Int, widthPx: Int, heightPx: Int): View {
        val base = ApplicationProvider.getApplicationContext<android.content.Context>()
        val themed = ContextThemeWrapper(base, R.style.Theme_Auxio)
        val parent = FrameLayout(themed)
        val view = LayoutInflater.from(themed).inflate(layoutId, parent, false)
        parent.addView(
            view,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )
        parent.measure(
            View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.EXACTLY),
        )
        parent.layout(0, 0, widthPx, heightPx)
        return parent
    }

    private companion object {
        val PLAYBACK_BUTTON_IDS =
            intArrayOf(
                R.id.playback_repeat,
                R.id.playback_skip_prev,
                R.id.playback_play_pause,
                R.id.playback_skip_next,
                R.id.playback_shuffle,
            )
        const val COMPACT_WIDTH = 320
        const val COMPACT_HEIGHT = 320
        const val H360_WIDTH = 640
        const val H360_HEIGHT = 360
        const val LANDSCAPE_WIDTH = 1280
        const val LANDSCAPE_HEIGHT = 720
        const val PORTRAIT_WIDTH = 720
        const val PORTRAIT_HEIGHT = 1280
    }
}
