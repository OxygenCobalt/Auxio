/*
 * Copyright (c) 2021 Auxio Project
 * EdgeFloatingActionButton.kt is part of Auxio.
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

package org.oxycblt.auxio.home

import android.content.Context
import android.util.AttributeSet
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.core.view.updatePadding
import org.oxycblt.auxio.util.systemBarsCompat

/**
 * A container for a FloatingActionButton that enables edge-to-edge support.
 * @author OxygenCobalt
 */
class FloatingActionButtonContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : FrameLayout(context, attrs, defStyleAttr) {
    init {
        clipToPadding = false
    }

    override fun dispatchApplyWindowInsets(insets: WindowInsets): WindowInsets {
        return onApplyWindowInsets(insets)
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        updatePadding(bottom = insets.systemBarsCompat.bottom)

        return insets
    }
}
