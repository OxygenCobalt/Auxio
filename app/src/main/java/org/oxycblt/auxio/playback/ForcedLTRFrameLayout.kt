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
 
package org.oxycblt.auxio.playback

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

/**
 * A class that programmatically overrides the child layout to a left-to-right (LTR) layout
 * direction.
 *
 * The Material Design guidelines state that any components that represent a "Timeline" should
 * always be LTR. In Auxio, this applies to most of the playback components. This layout in
 * particular overrides the layout direction in a way that will not disrupt how other views are laid
 * out.
 *
 * This layout can only contain one child.
 *
 * @author OxygenCobalt
 */
open class ForcedLTRFrameLayout
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    override fun onFinishInflate() {
        super.onFinishInflate()
        check(childCount == 1) { "This layout should only contain one child" }
        getChildAt(0).layoutDirection = View.LAYOUT_DIRECTION_LTR
    }
}
