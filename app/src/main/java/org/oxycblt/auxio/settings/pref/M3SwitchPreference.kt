package org.oxycblt.auxio.settings.pref

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.SwitchCompat
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreferenceCompat
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.resolveDrawable
import org.oxycblt.auxio.util.resolveStateList

/**
 * A [SwitchPreferenceCompat] that emulates the M3 switches until the design team
 * actually bothers to add them to MDC.
 */
class M3SwitchPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.switchPreferenceCompatStyle,
    defStyleRes: Int = 0
) : SwitchPreferenceCompat(context, attrs, defStyleAttr, defStyleRes) {
    // Lollipop cannot into ColorStateList, disable this feature on that version
    private var needToUpdateSwitch = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        if (needToUpdateSwitch) {
            val switch = holder.findViewById(androidx.preference.R.id.switchWidget)

            if (switch is SwitchCompat) {
                switch.apply {
                    trackDrawable = R.drawable.ui_m3_switch_track.resolveDrawable(context)
                    trackTintList = R.color.sel_m3_switch_track.resolveStateList(context)
                    thumbDrawable = R.drawable.ui_m3_switch_thumb.resolveDrawable(context)
                    thumbTintList = R.color.sel_m3_switch_thumb.resolveStateList(context)
                }

                needToUpdateSwitch = false
            }
        }
    }
}
