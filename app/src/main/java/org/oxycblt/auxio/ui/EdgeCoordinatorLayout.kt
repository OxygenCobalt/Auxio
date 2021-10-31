/*
 * Copyright (c) 2021 Auxio Project
 * FuckedCoordinatorLayout.kt is part of Auxio.
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

import android.content.Context
import android.util.AttributeSet
import android.view.WindowInsets
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children

/**
 * Class that fixes an issue where [CoordinatorLayout] will override [onApplyWindowInsets]
 * and delegate the job to ***LAYOUT BEHAVIOR INSTANCES*** instead of the actual views.
 *
 * I can't believe I have to do this.
 */
class EdgeCoordinatorLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : CoordinatorLayout(context, attrs, defStyleAttr) {
    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        for (child in children) {
            child.onApplyWindowInsets(insets)
        }

        return insets
    }
}
