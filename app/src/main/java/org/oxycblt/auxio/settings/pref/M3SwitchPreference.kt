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
 * A [SwitchPreferenceCompat] that emulates the M3 switches until the design team
 * actually bothers to add them to MDC.
 */
class M3SwitchPreference @JvmOverloads constructor(
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
