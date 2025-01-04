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

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.AttrRes
import androidx.core.graphics.toRectF
import androidx.core.view.isInvisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.R as MR
import com.google.android.material.button.MaterialButton
import com.google.android.material.motion.MotionUtils

class AnimConfig(
    context: Context,
    @AttrRes interpolatorRes: Int,
    @AttrRes durationRes: Int,
    defaultDuration: Int
) {
    val interpolator: TimeInterpolator =
        MotionUtils.resolveThemeInterpolator(context, interpolatorRes, FastOutSlowInInterpolator())
    val duration: Long =
        MotionUtils.resolveThemeDuration(context, durationRes, defaultDuration).toLong()

    companion object {
        val STANDARD = MR.attr.motionEasingStandardInterpolator
        val EMPHASIZED = MR.attr.motionEasingEmphasizedInterpolator
        val EMPHASIZED_ACCELERATE = MR.attr.motionEasingEmphasizedAccelerateInterpolator
        val EMPHASIZED_DECELERATE = MR.attr.motionEasingEmphasizedDecelerateInterpolator
        val SHORT1 = MR.attr.motionDurationShort1 to 50
        val SHORT2 = MR.attr.motionDurationShort2 to 100
        val SHORT3 = MR.attr.motionDurationShort3 to 150
        val SHORT4 = MR.attr.motionDurationShort4 to 200
        val MEDIUM1 = MR.attr.motionDurationMedium1 to 250
        val MEDIUM2 = MR.attr.motionDurationMedium2 to 300
        val MEDIUM3 = MR.attr.motionDurationMedium3 to 350
        val MEDIUM4 = MR.attr.motionDurationMedium4 to 400
        val LONG1 = MR.attr.motionDurationLong1 to 450
        val LONG2 = MR.attr.motionDurationLong2 to 500
        val LONG3 = MR.attr.motionDurationLong3 to 550
        val LONG4 = MR.attr.motionDurationLong4 to 600
        val EXTRA_LONG1 = MR.attr.motionDurationExtraLong1 to 700
        val EXTRA_LONG2 = MR.attr.motionDurationExtraLong2 to 800
        val EXTRA_LONG3 = MR.attr.motionDurationExtraLong3 to 900
        val EXTRA_LONG4 = MR.attr.motionDurationExtraLong4 to 1000

        fun of(context: Context, @AttrRes interpolator: Int, duration: Pair<Int, Int>) =
            AnimConfig(context, interpolator, duration.first, duration.second)
    }

    inline fun genericFloat(
        from: Float,
        to: Float,
        delayMs: Long = 0,
        crossinline update: (Float) -> Unit
    ): ValueAnimator =
        ValueAnimator.ofFloat(from, to).apply {
            startDelay = delayMs
            duration = this@AnimConfig.duration
            interpolator = this@AnimConfig.interpolator
            addUpdateListener { update(animatedValue as Float) }
        }
}

class MaterialCornerAnim(context: Context) {
    private val config = AnimConfig.of(context, AnimConfig.STANDARD, AnimConfig.MEDIUM2)

    fun animate(button: MaterialButton, sizeDp: Float): Animator {
        val shapeModel = button.shapeAppearanceModel
        val bounds = Rect(0, 0, button.width, button.height)
        val start = shapeModel.topRightCornerSize.getCornerSize(bounds.toRectF())
        return config.genericFloat(start, sizeDp) { size ->
            button.shapeAppearanceModel = shapeModel.withCornerSize { size }
        }
    }
}

class MaterialFader
private constructor(
    context: Context,
    private val scale: Float,
    @AttrRes outInterpolator: Int,
    alphaOutDuration: Pair<Int, Int>,
    scaleOutDuration: Pair<Int, Int>,
    inInterpolator: Int,
    alphaInDuration: Pair<Int, Int>,
    scaleInDuration: Pair<Int, Int>
) {
    private val alphaOutConfig = AnimConfig.of(context, outInterpolator, alphaOutDuration)
    private val scaleOutConfig = AnimConfig.of(context, outInterpolator, scaleOutDuration)
    private val alphaInConfig = AnimConfig.of(context, inInterpolator, alphaInDuration)
    private val scaleInConfig = AnimConfig.of(context, inInterpolator, scaleInDuration)

    fun jumpToFadeOut(view: View) {
        view.apply {
            alpha = 0f
            scaleX = scale
            scaleY = scale
            isInvisible = true
        }
    }

    fun jumpToFadeIn(view: View) {
        view.apply {
            alpha = 1f
            scaleX = 1.0f
            scaleY = 1.0f
            isInvisible = false
        }
    }

    fun fadeOut(view: View): Animator {
        if (!view.isLaidOut) {
            jumpToFadeOut(view)
            return AnimatorSet()
        }

        val alphaAnimator =
            alphaOutConfig.genericFloat(view.alpha, 0f) {
                view.alpha = it
                view.isInvisible = view.alpha == 0f
            }
        val scaleXAnimator = scaleOutConfig.genericFloat(view.scaleX, scale) { view.scaleX = it }
        val scaleYAnimator = scaleOutConfig.genericFloat(view.scaleY, scale) { view.scaleY = it }
        return AnimatorSet().apply { playTogether(alphaAnimator, scaleXAnimator, scaleYAnimator) }
    }

    fun fadeIn(view: View): Animator {
        if (!view.isLaidOut) {
            jumpToFadeIn(view)
            return AnimatorSet()
        }
        val alphaAnimator =
            alphaInConfig.genericFloat(view.alpha, 1f) {
                view.alpha = it
                view.isInvisible = view.alpha == 0f
            }
        val scaleXAnimator = scaleInConfig.genericFloat(view.scaleX, 1.0f) { view.scaleX = it }
        val scaleYAnimator = scaleInConfig.genericFloat(view.scaleY, 1.0f) { view.scaleY = it }
        return AnimatorSet().apply { playTogether(alphaAnimator, scaleXAnimator, scaleYAnimator) }
    }

    companion object {
        fun quickLopsided(context: Context) =
            MaterialFader(
                context,
                0.6f,
                AnimConfig.EMPHASIZED_ACCELERATE,
                AnimConfig.SHORT1,
                AnimConfig.SHORT3,
                AnimConfig.EMPHASIZED_DECELERATE,
                AnimConfig.SHORT1,
                AnimConfig.MEDIUM3)

        fun symmetric(context: Context) =
            MaterialFader(
                context,
                0.9f,
                AnimConfig.EMPHASIZED_ACCELERATE,
                AnimConfig.SHORT3,
                AnimConfig.MEDIUM1,
                AnimConfig.EMPHASIZED_DECELERATE,
                AnimConfig.SHORT3,
                AnimConfig.MEDIUM1)
    }
}

class MaterialFlipper(context: Context) {
    private val fader = MaterialFader.symmetric(context)

    fun jump(from: View) {
        fader.jumpToFadeOut(from)
    }

    fun flip(from: View, to: View): Animator {
        val outAnimator = fader.fadeOut(from)
        val inAnimator = fader.fadeIn(to).apply { startDelay = outAnimator.totalDuration }
        return AnimatorSet().apply { playTogether(outAnimator, inAnimator) }
    }
}

class MaterialSlider
private constructor(
    context: Context,
    private val x: Int?,
    inDuration: Pair<Int, Int>,
    outDuration: Pair<Int, Int>
) {
    private val outConfig = AnimConfig.of(context, AnimConfig.EMPHASIZED_ACCELERATE, outDuration)
    private val inConfig = AnimConfig.of(context, AnimConfig.EMPHASIZED_DECELERATE, inDuration)

    fun jumpOut(view: View) {
        if (x == null) {
            view.translationX = 100000f
        }
        view.translationX = (x ?: view.width).toFloat()
    }

    fun slideOut(view: View): Animator {
        val target = (x ?: view.width).toFloat()
        if (view.translationX > target) {
            view.translationX = target
        }
        val animator = outConfig.genericFloat(view.translationX, target) { view.translationX = it }
        return animator
    }

    fun slideIn(view: View): Animator {
        val animator = inConfig.genericFloat(view.translationX, 0f) { view.translationX = it }
        return animator
    }

    companion object {
        fun small(context: Context, x: Int?) =
            MaterialSlider(context, x, AnimConfig.SHORT3, AnimConfig.MEDIUM1)

        fun large(context: Context, x: Int?) =
            MaterialSlider(context, x, AnimConfig.MEDIUM3, AnimConfig.SHORT3)
    }
}

class MaterialFadingSlider(private val slider: MaterialSlider) {
    fun jumpOut(view: View) {
        slider.jumpOut(view)
        view.alpha = 0f
    }

    fun slideOut(view: View): Animator {
        val slideOut = slider.slideOut(view)
        val alphaOut =
            ValueAnimator.ofFloat(1f, 0f).apply {
                duration = slideOut.duration
                addUpdateListener { view.alpha = it.animatedValue as Float }
            }
        return AnimatorSet().apply { playTogether(slideOut, alphaOut) }
    }

    fun slideIn(view: View): Animator {
        val slideIn = slider.slideIn(view)
        val alphaIn =
            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = slideIn.duration
                addUpdateListener { view.alpha = it.animatedValue as Float }
            }
        return AnimatorSet().apply { playTogether(slideIn, alphaIn) }
    }
}
