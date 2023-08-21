/*
 * Copyright (c) 2023 Auxio Project
 * FlipFloatingActionButton.kt is part of Auxio.
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
 
package org.oxycblt.auxio.home

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.material.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.oxycblt.auxio.util.logD

/**
 * An extension of [FloatingActionButton] that enables the ability to fade in and out between
 * several states, as in the Material Design 3 specification.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class FlipFloatingActionButton
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.floatingActionButtonStyle
) : FloatingActionButton(context, attrs, defStyleAttr) {
    private var pendingConfig: PendingConfig? = null
    private var flipping = false

    override fun show() {
        // Will already show eventually, need to do nothing.
        if (flipping) {
            logD("Already flipping, aborting show")
            return
        }
        // Apply the new configuration possibly set in flipTo. This should occur even if
        // a flip was canceled by a hide.
        pendingConfig?.run {
            logD("Applying pending configuration")
            setImageResource(iconRes)
            contentDescription = context.getString(contentDescriptionRes)
            setOnClickListener(clickListener)
        }
        pendingConfig = null
        logD("Beginning show")
        super.show()
    }

    override fun hide() {
        if (flipping) {
            logD("Hide was called, aborting flip")
        }
        // Not flipping anymore, disable the flag so that the FAB is not re-shown.
        flipping = false
        // Don't pass any kind of listener so that future flip operations will not be able
        // to show the FAB again.
        logD("Beginning hide")
        super.hide()
    }

    /**
     * Flip to a new FAB state.
     *
     * @param iconRes The resource of the new FAB icon.
     * @param contentDescriptionRes The resource of the new FAB content description.
     */
    fun flipTo(
        @DrawableRes iconRes: Int,
        @StringRes contentDescriptionRes: Int,
        clickListener: OnClickListener
    ) {
        // Avoid doing a flip if the given config is already being applied.
        if (tag == iconRes) return
        tag = iconRes
        pendingConfig = PendingConfig(iconRes, contentDescriptionRes, clickListener)

        // Already hiding for whatever reason, apply the configuration when the FAB is shown again.
        if (!isOrWillBeHidden) {
            logD("Starting hide for flip")
            flipping = true
            // We will re-show the FAB later, assuming that there was not a prior flip operation.
            super.hide(FlipVisibilityListener())
        } else {
            logD("Already hiding, will apply config later")
        }
    }

    private data class PendingConfig(
        @DrawableRes val iconRes: Int,
        @StringRes val contentDescriptionRes: Int,
        val clickListener: OnClickListener
    )

    private inner class FlipVisibilityListener : OnVisibilityChangedListener() {
        override fun onHidden(fab: FloatingActionButton) {
            if (!flipping) return
            logD("Starting show for flip")
            flipping = false
            show()
        }
    }
}
