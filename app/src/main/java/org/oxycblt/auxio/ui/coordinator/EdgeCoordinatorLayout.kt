/*
 * Copyright (c) 2021 Auxio Project
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
 
package org.oxycblt.auxio.ui.coordinator

import android.content.Context
import android.util.AttributeSet
import android.view.WindowInsets
import androidx.annotation.AttrRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children

/**
 * Class that manually overrides the busted window inset functionality of CoordinatorLayout in favor
 * of a simple "delegate to child views" implementation.
 *
 * @author OxygenCobalt
 */
class EdgeCoordinatorLayout
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    CoordinatorLayout(context, attrs, defStyleAttr) {
    override fun dispatchApplyWindowInsets(insets: WindowInsets): WindowInsets {
        super.dispatchApplyWindowInsets(insets)

        for (child in children) {
            child.dispatchApplyWindowInsets(insets)
        }

        return insets
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        super.onApplyWindowInsets(insets)

        for (child in children) {
            child.onApplyWindowInsets(insets)
        }

        return insets
    }
}
