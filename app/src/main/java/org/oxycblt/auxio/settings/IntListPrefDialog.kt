/*
 * Copyright (c) 2021 Auxio Project
 * IntListPrefDialog.kt is part of Auxio.
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

package org.oxycblt.auxio.settings

import android.util.TypedValue
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updatePadding
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.ui.LifecycleDialog
import org.oxycblt.auxio.util.resolveAttr

class IntListPrefDialog(private val pref: IntListPreference) : LifecycleDialog() {
    override fun onConfigDialog(builder: AlertDialog.Builder) {
        // Don't set the title. Instead. Set a custom title view so that the padding actually
        // works between the title and this dialog's contents. I can't believe I have to do this.
        val titleView = AppCompatTextView(requireContext()).apply {
            text = pref.title
            typeface = ResourcesCompat.getFont(context, R.font.inter_bold)
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimensionPixelSize(R.dimen.text_size_large).toFloat()
            )
            setTextColor(android.R.attr.textColorPrimary.resolveAttr(context))

            // We have to make the bottom padding account for the ListView's immutable top padding,
            // because Android's dialog code is a massive pile of broken spaghetti.
            val padding = resources.getDimension(R.dimen.spacing_medium).toInt()
            val paddingHack = resources.getDimension(R.dimen.spacing_small).toInt()

            updatePadding(left = padding, top = padding, right = padding, bottom = paddingHack)
        }

        builder.setCustomTitle(titleView)

        builder.setSingleChoiceItems(pref.entries, pref.getValueIndex()) { _, index ->
            pref.setValueIndex(index)

            dismiss()
        }

        builder.setNegativeButton(android.R.string.cancel, null)
    }

    companion object {
        const val TAG = BuildConfig.APPLICATION_ID + ".tag.INT_PREF"
    }
}
