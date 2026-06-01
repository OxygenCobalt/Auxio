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

import android.graphics.Color
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.android.material.button.MaterialButton
import java.io.File
import org.junit.Test
import org.junit.runner.RunWith
import org.oxycblt.auxio.R
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class RoborazziSmokeScreenshotTest {
    @Test
    fun capturePlaybackBarLayout() {
        captureLayout(
            layoutId = R.layout.fragment_playback_bar,
            widthPx = LANDSCAPE_WIDTH_PX,
            heightPx = LANDSCAPE_HEIGHT_PX,
            outputName = "playback-bar-landscape.png",
        )
    }

    @Test
    @Config(qualifiers = "w1280dp-h720dp-land")
    fun capturePlaybackPanelLandscapeShuffleOff() {
        captureLayout(
            layoutId = R.layout.fragment_playback_panel,
            widthPx = LANDSCAPE_WIDTH_PX,
            heightPx = LANDSCAPE_HEIGHT_PX,
            outputName = "playback-page-shuffle-off-landscape.png",
            shuffleChecked = false,
        )
    }

    @Test
    @Config(qualifiers = "w1280dp-h720dp-land")
    fun capturePlaybackPanelLandscapeShuffleOn() {
        captureLayout(
            layoutId = R.layout.fragment_playback_panel,
            widthPx = LANDSCAPE_WIDTH_PX,
            heightPx = LANDSCAPE_HEIGHT_PX,
            outputName = "playback-page-shuffle-on-landscape.png",
            shuffleChecked = true,
        )
    }

    @Test
    @Config(qualifiers = "w720dp-h1280dp-port")
    fun capturePlaybackPanelPortraitShuffleOff() {
        captureLayout(
            layoutId = R.layout.fragment_playback_panel,
            widthPx = PORTRAIT_WIDTH_PX,
            heightPx = PORTRAIT_HEIGHT_PX,
            outputName = "playback-page-shuffle-off-portrait.png",
            shuffleChecked = false,
        )
    }

    @Test
    @Config(qualifiers = "w720dp-h1280dp-port")
    fun capturePlaybackPanelPortraitShuffleOn() {
        captureLayout(
            layoutId = R.layout.fragment_playback_panel,
            widthPx = PORTRAIT_WIDTH_PX,
            heightPx = PORTRAIT_HEIGHT_PX,
            outputName = "playback-page-shuffle-on-portrait.png",
            shuffleChecked = true,
        )
    }

    private fun captureLayout(
        layoutId: Int,
        widthPx: Int,
        heightPx: Int,
        outputName: String,
        shuffleChecked: Boolean? = null,
    ) {
        val base = ApplicationProvider.getApplicationContext<android.content.Context>()
        val themed = ContextThemeWrapper(base, R.style.Theme_Auxio)

        val parent =
            FrameLayout(themed).apply {
                setBackgroundColor(Color.TRANSPARENT)
                layoutParams = ViewGroup.LayoutParams(widthPx, heightPx)
            }

        val view = LayoutInflater.from(themed).inflate(layoutId, parent, false)
        shuffleChecked?.let { checked ->
            view.findViewById<MaterialButton>(R.id.playback_shuffle)?.isChecked = checked
        }
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

        val output = File("build/outputs/roborazzi/$outputName")
        requireNotNull(output.parentFile).mkdirs()
        parent.captureRoboImage(output.path)
    }

    private companion object {
        const val LANDSCAPE_WIDTH_PX = 1280
        const val LANDSCAPE_HEIGHT_PX = 720
        const val PORTRAIT_WIDTH_PX = 720
        const val PORTRAIT_HEIGHT_PX = 1280
    }
}
