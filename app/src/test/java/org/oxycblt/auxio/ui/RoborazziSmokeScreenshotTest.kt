/*
 * Copyright (c) 2026 Auxio Project
 * RoborazziSmokeScreenshotTest.kt is part of Auxio.
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

import android.app.Activity
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import org.junit.runner.RunWith
import org.oxycblt.auxio.R
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "land")
class RoborazziSmokeScreenshotTest {
    @Test
    fun capturePlaybackBarLayout() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val themed = ContextThemeWrapper(activity, R.style.Theme_Auxio)

        val parent = FrameLayout(themed)
        activity.setContentView(parent)

        val view =
            LayoutInflater.from(themed).inflate(R.layout.fragment_playback_bar, parent, false)
        parent.addView(
            view,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ),
        )

        parent.measure(
            View.MeasureSpec.makeMeasureSpec(SCREEN_WIDTH_PX, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(SCREEN_HEIGHT_LIMIT_PX, View.MeasureSpec.AT_MOST),
        )

        val measuredHeight = parent.measuredHeight.coerceAtLeast(1)
        parent.layout(0, 0, SCREEN_WIDTH_PX, measuredHeight)

        parent.captureRoboImage("playback-bar-landscape.png")
    }

    private companion object {
        const val SCREEN_WIDTH_PX = 1280
        const val SCREEN_HEIGHT_LIMIT_PX = 720
    }
}
