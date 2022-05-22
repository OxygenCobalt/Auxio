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
 
package org.oxycblt.auxio.settings.pref

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.SwitchCompat
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreferenceCompat
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getColorStateListSafe
import org.oxycblt.auxio.util.getDrawableSafe

/**
 * A [SwitchPreferenceCompat] that emulates the M3 switches until the design team actually bothers
 * to add them to MDC
 *
 * TODO: Remove this once MaterialSwitch is stabilized.
 */
class M3SwitchPreference
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.switchPreferenceCompatStyle,
    defStyleRes: Int = 0
) : SwitchPreferenceCompat(context, attrs, defStyleAttr, defStyleRes) {
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        // Lollipop cannot into ColorStateList, disable this feature on that version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val switch = holder.findViewById(androidx.preference.R.id.switchWidget)
            if (switch is SwitchCompat) {
                switch.apply {
                    trackDrawable = context.getDrawableSafe(R.drawable.ui_m3_switch_track)
                    trackTintList = context.getColorStateListSafe(R.color.sel_m3_switch_track)
                    thumbDrawable = context.getDrawableSafe(R.drawable.ui_m3_switch_thumb)
                    thumbTintList = context.getColorStateListSafe(R.color.sel_m3_switch_thumb)
                }
            }
        }
    }
}
