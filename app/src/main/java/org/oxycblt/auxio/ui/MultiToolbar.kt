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
import kotlin.math.max
import kotlin.math.min
import org.oxycblt.auxio.util.logD

class MultiToolbar
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    private var fadeThroughAnimator: ValueAnimator? = null
    private var currentlyVisible = 0
    private val matInterpolator =
        MotionUtils.resolveThemeInterpolator(
            context, MR.attr.motionEasingStandardInterpolator, FastOutSlowInInterpolator())
    private val matDuration =
        MotionUtils.resolveThemeDuration(context, MR.attr.motionDurationMedium3, 300).toLong()

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
            setToolbarsAlpha(fromView, toView, targetFromAlpha)
            return false
        }

        logD("Changing toolbar visibility $from -> 0f, $to -> 1f")
        fadeThroughAnimator?.cancel()
        fadeThroughAnimator =
            ValueAnimator.ofFloat(fromView.alpha, targetFromAlpha).apply {
                duration = matDuration
                interpolator = matInterpolator
                addUpdateListener { setToolbarsAlpha(fromView, toView, it.animatedValue as Float) }
                start()
            }

        return true
    }

    private fun setToolbarsAlpha(from: Toolbar, to: Toolbar, ratio: Float) {
        val outRatio = min(ratio * 2, 1f)
        to.apply {
            scaleX = 1 - 0.05f * outRatio
            scaleY = 1 - 0.05f * outRatio
            alpha = 1 - outRatio
            isInvisible = alpha == 0f
        }

        val inRatio = max(ratio - 0.5f, 0f) * 2
        from.apply {
            scaleX = 1 - 0.05f * (1 - inRatio)
            scaleY = 1 - 0.05f * (1 - inRatio)
            alpha = inRatio
            isInvisible = alpha == 0f
        }
    }
}
