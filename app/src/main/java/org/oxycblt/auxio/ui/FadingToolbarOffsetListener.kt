/*
 * Copyright (c) 2026 Auxio Project
 * FadingToolbarOffsetListener.kt is part of Auxio.
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

import android.view.View
import androidx.core.view.updatePadding
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

class FadingToolbarOffsetListener(private val toolbar: View, private val content: View) :
    AppBarLayout.OnOffsetChangedListener {
    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val range = appBarLayout.totalScrollRange
        // Fade out the toolbar as the AppBarLayout collapses. To prevent status bar overlap,
        // the alpha transition is shifted such that the Toolbar becomes fully transparent
        // when the AppBarLayout is only at half-collapsed.
        toolbar.alpha = 1f - (abs(verticalOffset.toFloat()) / (range.toFloat() / 2))
        content.updatePadding(bottom = range + verticalOffset)
    }
}
