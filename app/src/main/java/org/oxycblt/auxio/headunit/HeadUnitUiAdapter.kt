/*
 * Copyright (c) 2024 Auxio Project
 * HeadUnitUiAdapter.kt is part of Auxio.
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

package org.oxycblt.auxio.headunit

import android.content.res.Resources
import android.view.View
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import org.oxycblt.auxio.R

/**
 * Applies head-unit UI adjustments (large touch targets and enlarged text) to playback views.
 *
 * Fragment-specific concerns (album-art visibility, driver-side ConstraintSet flips) are handled in
 * each fragment; this adapter only owns the shared sizing logic.
 */
object HeadUnitUiAdapter {

    /**
     * Expands the minimum touch-target size of each view in [buttons] to the head-unit button
     * dimension when [largeControls] is `true`.
     */
    fun applyLargeControls(resources: Resources, largeControls: Boolean, buttons: List<View>) {
        if (!largeControls) return
        val size = resources.getDimensionPixelSize(R.dimen.size_touchable_head_unit)
        buttons.forEach { view ->
            view.minimumWidth = size
            view.minimumHeight = size
        }
    }

    /**
     * Sizes media controls as rounded-square targets. The explicit layout dimensions keep repeat,
     * skip, and shuffle visually consistent while allowing the primary play/pause target to remain
     * larger and more prominent.
     */
    fun applyUniformMediaControls(
        resources: Resources,
        largeControls: Boolean,
        buttons: List<View>,
        compact: Boolean = false,
        primaryButton: View? = null,
    ) {
        if (!largeControls) return
        val size =
            resources.getDimensionPixelSize(
                if (compact) R.dimen.size_touchable_head_unit_media_compact
                else R.dimen.size_touchable_head_unit_media
            )
        val primarySize =
            resources.getDimensionPixelSize(
                if (compact) R.dimen.size_touchable_head_unit_media_compact_primary
                else R.dimen.size_touchable_head_unit_media_primary
            )
        buttons.forEach { view ->
            val buttonSize = if (view == primaryButton) primarySize else size
            view.minimumWidth = buttonSize
            view.minimumHeight = buttonSize
            view.updateLayoutParams {
                width = buttonSize
                height = buttonSize
            }
        }
    }

    /**
     * Enlarges [songView] and [artistView] text to head-unit readable sizes when [largeControls] is
     * `true`.
     */
    fun applyLargePlaybackText(
        resources: Resources,
        largeControls: Boolean,
        songView: TextView,
        artistView: TextView,
    ) {
        if (!largeControls) return
        val scaledDensity = resources.displayMetrics.scaledDensity
        songView.textSize =
            resources.getDimension(R.dimen.text_size_head_unit_title) / scaledDensity
        artistView.textSize =
            resources.getDimension(R.dimen.text_size_head_unit_subtitle) / scaledDensity
    }
}
