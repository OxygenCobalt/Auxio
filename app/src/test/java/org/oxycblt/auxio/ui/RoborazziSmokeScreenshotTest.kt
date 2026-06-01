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
import android.widget.TextView
import androidx.core.view.isVisible
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayout
import org.junit.Test
import org.junit.runner.RunWith
import org.oxycblt.auxio.R
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class RoborazziSmokeScreenshotTest {
    @Test
    @Config(qualifiers = "w1280dp-h720dp-land")
    fun captureMainLaunchLandscape() {
        captureHome("main-launch-landscape.png", LANDSCAPE_WIDTH_PX, LANDSCAPE_HEIGHT_PX)
    }

    @Test
    @Config(qualifiers = "w412dp-h915dp-port")
    fun captureMainLaunchPortrait() {
        captureHome("main-launch-portrait.png", PORTRAIT_WIDTH_PX, PORTRAIT_HEIGHT_PX)
    }

    @Test
    @Config(qualifiers = "w1280dp-h720dp-land")
    fun capturePlaybackShuffleOffLandscape() {
        capturePlayback(
            "playback-page-shuffle-off-landscape.png",
            LANDSCAPE_WIDTH_PX,
            LANDSCAPE_HEIGHT_PX,
            shuffleEnabled = false,
        )
    }

    @Test
    @Config(qualifiers = "w1280dp-h720dp-land")
    fun capturePlaybackShuffleOnLandscape() {
        capturePlayback(
            "playback-page-shuffle-on-landscape.png",
            LANDSCAPE_WIDTH_PX,
            LANDSCAPE_HEIGHT_PX,
            shuffleEnabled = true,
        )
    }

    @Test
    @Config(qualifiers = "w412dp-h915dp-port")
    fun capturePlaybackShuffleOffPortrait() {
        capturePlayback(
            "playback-page-shuffle-off-portrait.png",
            PORTRAIT_WIDTH_PX,
            PORTRAIT_HEIGHT_PX,
            shuffleEnabled = false,
        )
    }

    @Test
    @Config(qualifiers = "w412dp-h915dp-port")
    fun capturePlaybackShuffleOnPortrait() {
        capturePlayback(
            "playback-page-shuffle-on-portrait.png",
            PORTRAIT_WIDTH_PX,
            PORTRAIT_HEIGHT_PX,
            shuffleEnabled = true,
        )
    }

    private fun captureHome(fileName: String, widthPx: Int, heightPx: Int) {
        captureFullScreen(fileName, widthPx, heightPx, R.layout.fragment_home) { view, themed ->
            view.findViewById<TabLayout>(R.id.home_tabs).apply {
                listOf("Songs", "Albums", "Artists", "Playlists").forEach { label ->
                    addTab(newTab().setText(label))
                }
            }
            view.findViewById<ChipGroup>(R.id.home_quick_picks).apply {
                addView(chip(themed, "Shuffle all"))
                addView(chip(themed, "Recently added"))
                addView(chip(themed, "Head-unit mix"))
            }
            view.findViewById<ChipGroup>(R.id.home_metadata_chips).apply {
                addView(chip(themed, "TS18"))
                addView(chip(themed, "DoFun"))
                addView(chip(themed, "Topway"))
            }
            view.findViewById<View>(R.id.home_indexing_container).isVisible = false
        }
    }

    private fun capturePlayback(
        fileName: String,
        widthPx: Int,
        heightPx: Int,
        shuffleEnabled: Boolean,
    ) {
        captureFullScreen(fileName, widthPx, heightPx, R.layout.fragment_playback_panel) { view, _
            ->
            view.findViewById<TextView>(R.id.playback_song).text = "Dream Nexus Highway"
            view.findViewById<TextView>(R.id.playback_artist).text = "Auxio-TS • DoFun preview"
            view.findViewById<MaterialButton>(R.id.playback_shuffle).isChecked = shuffleEnabled
            view.findViewById<MaterialButton>(R.id.playback_repeat).isChecked = false
        }
    }

    private fun captureFullScreen(
        fileName: String,
        widthPx: Int,
        heightPx: Int,
        layoutRes: Int,
        configure: (View, ContextThemeWrapper) -> Unit,
    ) {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val themed = ContextThemeWrapper(activity, R.style.Theme_Auxio)
        val parent = FrameLayout(themed)
        activity.setContentView(parent)

        val view = LayoutInflater.from(themed).inflate(layoutRes, parent, false)
        configure(view, themed)
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

        parent.captureRoboImage(fileName)
    }

    private fun chip(themed: ContextThemeWrapper, text: String) =
        Chip(themed).apply {
            this.text = text
            isCheckable = false
        }

    private companion object {
        const val LANDSCAPE_WIDTH_PX = 1280
        const val LANDSCAPE_HEIGHT_PX = 720
        const val PORTRAIT_WIDTH_PX = 412
        const val PORTRAIT_HEIGHT_PX = 915
    }
}
