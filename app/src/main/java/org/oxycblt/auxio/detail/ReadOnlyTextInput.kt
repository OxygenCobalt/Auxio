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
 
package org.oxycblt.auxio.detail

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import org.oxycblt.auxio.R

/**
 * A [TextInputEditText] that deliberately restricts all input except for selection. Yes, this is a
 * blatant abuse of Material Design Guidelines, but I also don't want to figure out how to plain
 * text selectable.
 *
 * @author OxygenCobalt
 */
class ReadOnlyTextInput
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    init {
        setTextIsSelectable(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusable = View.FOCUSABLE_AUTO
        }
    }

    override fun getFreezesText() = false

    override fun getDefaultEditable() = false

    override fun getDefaultMovementMethod() = null
}
