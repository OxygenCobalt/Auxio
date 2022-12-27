/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.playback.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

/**
 * A [FrameLayout] that programmatically overrides the child layout to a left-to-right (LTR) layout
 * direction. This is useful for "Timeline" elements that Material Design recommends be LTR in all
 * cases. This layout can only contain one child, to prevent conflicts with other layout components.
 * @author Alexander Capehart (OxygenCobalt)
 */
open class ForcedLTRFrameLayout
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    override fun onFinishInflate() {
        super.onFinishInflate()
        check(childCount == 1) { "This layout should only contain one child" }
        getChildAt(0).layoutDirection = View.LAYOUT_DIRECTION_LTR
    }
}
