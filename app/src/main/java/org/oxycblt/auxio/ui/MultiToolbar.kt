/*
 * Copyright (c) 2023 Auxio Project
 * MultiToolbar.kt is part of Auxio.
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

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.R as MR
import com.google.android.material.motion.MotionUtils
import org.oxycblt.auxio.util.logD

class MultiToolbar
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    private var animator: AnimatorSet? = null
    private var currentlyVisible = 0
    private val outInterpolator =
        MotionUtils.resolveThemeInterpolator(
            context,
            MR.attr.motionEasingEmphasizedAccelerateInterpolator,
            FastOutSlowInInterpolator())
    private val outDuration =
        MotionUtils.resolveThemeDuration(context, MR.attr.motionDurationShort4, 250).toLong()
    private val inInterpolator =
        MotionUtils.resolveThemeInterpolator(
            context,
            MR.attr.motionEasingEmphasizedDecelerateInterpolator,
            FastOutSlowInInterpolator())
    private val inDuration =
        MotionUtils.resolveThemeDuration(context, MR.attr.motionDurationMedium1, 300).toLong()

    override fun onFinishInflate() {
        super.onFinishInflate()
        for (i in 1 until childCount) {
            getChildAt(i).apply {
                alpha = 0f
                isInvisible = true
            }
        }
    }

    fun setVisible(@IdRes viewId: Int): Boolean {
        val index = children.indexOfFirst { it.id == viewId }
        if (index == currentlyVisible) return false
        logD("Switching toolbar visibility from $currentlyVisible -> $index")
        return animateToolbarsVisibility(currentlyVisible, index).also { currentlyVisible = index }
    }

    private fun animateToolbarsVisibility(from: Int, to: Int): Boolean {
        // TODO: Animate nicer Material Fade transitions using animators (Normal transitions
        //  don't work due to translation)
        // Set up the target transitions for both the inner and selection toolbars.
        val targetFromAlpha = 0f
        val targetToAlpha = 1f
        val fromView = getChildAt(from) as Toolbar
        val toView = getChildAt(to) as Toolbar

        if (fromView.alpha == targetFromAlpha && toView.alpha == targetToAlpha) {
            // Nothing to do.
            return false
        }

        if (!isLaidOut) {
            // Not laid out, just change it immediately while are not shown to the user.
            // This is an initialization, so we return false despite changing.
            logD("Not laid out, immediately updating visibility")
            fromView.apply {
                alpha = 0f
                isInvisible = true
            }
            toView.apply {
                alpha = 1f
                isInvisible = false
            }
            return false
        }

        logD("Changing toolbar visibility $from -> 0f, $to -> 1f")
        animator?.cancel()
        val outAnimator =
            ValueAnimator.ofFloat(fromView.alpha, 0f).apply {
                duration = outDuration
                interpolator = outInterpolator
                addUpdateListener {
                    val ratio = it.animatedValue as Float
                    fromView.apply {
                        scaleX = 1 - 0.05f * (1 - ratio)
                        scaleY = 1 - 0.05f * (1 - ratio)
                        alpha = ratio
                        isInvisible = alpha == 0f
                    }
                }
            }
        val inAnimator =
            ValueAnimator.ofFloat(toView.alpha, 1f).apply {
                duration = inDuration
                interpolator = inInterpolator
                startDelay = outDuration
                addUpdateListener {
                    val ratio = it.animatedValue as Float
                    toView.apply {
                        scaleX = 1 - 0.05f * (1 - ratio)
                        scaleY = 1 - 0.05f * (1 - ratio)
                        alpha = ratio
                        isInvisible = alpha == 0f
                    }
                }
            }
        animator =
            AnimatorSet().apply {
                playTogether(outAnimator, inAnimator)
                start()
            }

        return true
    }
}
