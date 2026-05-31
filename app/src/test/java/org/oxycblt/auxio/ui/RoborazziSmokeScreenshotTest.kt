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

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.github.takahirom.roborazzi.captureRoboImage
import java.io.File
import org.junit.Test
import org.junit.runner.RunWith
import org.oxycblt.auxio.R
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "land")
class RoborazziSmokeScreenshotTest {
    @Test
    fun capturePlaybackBarLayout() {
        val base = ApplicationProvider.getApplicationContext<android.content.Context>()
        val themed = ContextThemeWrapper(base, R.style.Theme_Auxio)

        val parent = FrameLayout(themed)
        val view = LayoutInflater.from(themed).inflate(R.layout.fragment_playback_bar, parent, false)
        parent.addView(view)

        parent.measure(
            View.MeasureSpec.makeMeasureSpec(SCREEN_WIDTH_PX, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(SCREEN_HEIGHT_LIMIT_PX, View.MeasureSpec.AT_MOST),
        )

        val measuredHeight = parent.measuredHeight.coerceAtLeast(1)
        parent.layout(0, 0, SCREEN_WIDTH_PX, measuredHeight)

        val output = File("build/outputs/roborazzi/playback-bar-landscape.png")
        requireNotNull(output.parentFile).mkdirs()
        parent.captureRoboImage(output.path)
    }

    private companion object {
        const val SCREEN_WIDTH_PX = 1280
        const val SCREEN_HEIGHT_LIMIT_PX = 720
    }
}
