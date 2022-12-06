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

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import org.oxycblt.auxio.R

/**
 * A [MaterialButton] that automatically morphs from a circle to a squircle shape appearance when it
 * is activated.
 * @author OxygenCobalt
 */
class AnimatedMaterialButton
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    MaterialButton(context, attrs, defStyleAttr) {
    private var currentCornerRadiusRatio = 0f
    private var animator: ValueAnimator? = null

    override fun setActivated(activated: Boolean) {
        super.setActivated(activated)

        val target = if (activated) 0.3f else 0.5f
        if (!isLaidOut) {
            updateCornerRadiusRatio(target)
            return
        }

        animator?.cancel()
        animator =
            ValueAnimator.ofFloat(currentCornerRadiusRatio, target).apply {
                duration = context.resources.getInteger(R.integer.anim_fade_enter_duration).toLong()
                addUpdateListener { updateCornerRadiusRatio(animatedValue as Float) }
                start()
            }
    }

    private fun updateCornerRadiusRatio(ratio: Float) {
        currentCornerRadiusRatio = ratio
        shapeAppearanceModel = shapeAppearanceModel.withCornerSize { it.width() * ratio }
    }
}
