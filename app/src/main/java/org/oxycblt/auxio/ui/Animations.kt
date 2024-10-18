/*
 * Copyright (c) 2024 Auxio Project
 * Animations.kt is part of Auxio.
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

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.R as MR
import com.google.android.material.motion.MotionUtils

data class Anim(val interpolator: TimeInterpolator, val duration: Long) {
    inline fun genericFloat(
        from: Float,
        to: Float,
        delayMs: Long = 0,
        crossinline update: (Float) -> Unit
    ): ValueAnimator =
        ValueAnimator.ofFloat(from, to).apply {
            startDelay = delayMs
            duration = this@Anim.duration
            interpolator = this@Anim.interpolator
            addUpdateListener { update(animatedValue as Float) }
        }
}

object StationaryAnim {
    fun forMediumComponent(context: Context) =
        Anim(
            MotionUtils.resolveThemeInterpolator(
                context, MR.attr.motionEasingStandardInterpolator, FastOutSlowInInterpolator()),
            MotionUtils.resolveThemeDuration(context, MR.attr.motionDurationMedium2, 300).toLong())
}

object InAnim {
    fun forSmallComponent(context: Context) =
        Anim(
            MotionUtils.resolveThemeInterpolator(
                context,
                MR.attr.motionEasingStandardDecelerateInterpolator,
                FastOutSlowInInterpolator()),
            MotionUtils.resolveThemeDuration(context, MR.attr.motionDurationMedium1, 300).toLong())

    fun forMediumComponent(context: Context) =
        Anim(
            MotionUtils.resolveThemeInterpolator(
                context,
                MR.attr.motionEasingEmphasizedDecelerateInterpolator,
                FastOutSlowInInterpolator()),
            MotionUtils.resolveThemeDuration(context, MR.attr.motionDurationMedium2, 300).toLong())
}

object OutAnim {
    fun forSmallComponent(context: Context) =
        Anim(
            MotionUtils.resolveThemeInterpolator(
                context,
                MR.attr.motionEasingStandardAccelerateInterpolator,
                FastOutSlowInInterpolator()),
            MotionUtils.resolveThemeDuration(context, MR.attr.motionDurationShort2, 100).toLong())

    fun forMediumComponent(context: Context) =
        Anim(
            MotionUtils.resolveThemeInterpolator(
                context,
                MR.attr.motionEasingEmphasizedAccelerateInterpolator,
                FastOutSlowInInterpolator()),
            MotionUtils.resolveThemeDuration(context, MR.attr.motionDurationShort4, 250).toLong())
}
