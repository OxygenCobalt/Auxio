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
 
package org.oxycblt.auxio.ui

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.core.view.updatePaddingRelative
import com.google.android.material.appbar.MaterialToolbar
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getDimenSizeSafe

/**
 * [MaterialToolbar] that automatically fixes padding in order to align with the M3 specs.
 * @author OxygenCobalt
 */
class M3Toolbar : MaterialToolbar {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)
    init {
        val tinySpacing = context.getDimenSizeSafe(R.dimen.spacing_tiny)

        if (navigationIcon != null) {
            updatePaddingRelative(start = tinySpacing)
        }

        if (menu != null) {
            updatePaddingRelative(end = tinySpacing)
        }
    }
}
