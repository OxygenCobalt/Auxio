/*
 * Copyright (c) 2022 Auxio Project
 * AnimatedMaterialButton.kt is part of Auxio.
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
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.R as MR
import com.google.android.material.button.MaterialButton
import com.google.android.material.motion.MotionUtils
import org.oxycblt.auxio.ui.RippleFixMaterialButton
import org.oxycblt.auxio.util.logD

/**
 * A [MaterialButton] that automatically morphs from a circle to a squircle shape appearance when
 * [isActivated] changes.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class AnimatedMaterialButton : RippleFixMaterialButton {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    private var currentCornerRadiusRatio = 0f
    private var animator: ValueAnimator? = null

    private val matInterpolator =
        MotionUtils.resolveThemeInterpolator(
            context, MR.attr.motionEasingStandardInterpolator, FastOutSlowInInterpolator())

    private val matDuration =
        MotionUtils.resolveThemeDuration(context, MR.attr.motionDurationMedium2, 300)

    override fun setActivated(activated: Boolean) {
        super.setActivated(activated)

        // Activated -> Squircle (30% Radius), Inactive -> Circle (50% Radius)
        val targetRadius = if (activated) 0.3f else 0.5f
        if (!isLaidOut) {
            // Not laid out, initialize it without animation before drawing.
            logD("Not laid out, immediately updating corner radius")
            updateCornerRadiusRatio(targetRadius)
            return
        }

        logD("Starting corner radius animation")
        animator?.cancel()
        animator =
            ValueAnimator.ofFloat(currentCornerRadiusRatio, targetRadius).apply {
                duration = matDuration.toLong()
                interpolator = matInterpolator
                addUpdateListener { updateCornerRadiusRatio(animatedValue as Float) }
                start()
            }
    }

    private fun updateCornerRadiusRatio(ratio: Float) {
        currentCornerRadiusRatio = ratio
        // Can't reproduce the intrinsic ratio corner radius, just manually implement it with
        // a dimension value.
        shapeAppearanceModel = shapeAppearanceModel.withCornerSize { it.width() * ratio }
    }
}
