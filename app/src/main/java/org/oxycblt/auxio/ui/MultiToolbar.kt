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
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getInteger
import org.oxycblt.auxio.util.logD

class MultiToolbar
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    private var fadeThroughAnimator: ValueAnimator? = null
    private var currentlyVisible = 0

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
        val targetDuration =
            // Since this view starts with the lowest toolbar index,
            if (from < to) {
                logD("Moving higher, use an entrance animation")
                context.getInteger(R.integer.anim_fade_enter_duration).toLong()
            } else {
                logD("Moving lower, use an exit animation")
                context.getInteger(R.integer.anim_fade_exit_duration).toLong()
            }

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
                duration = targetDuration
                addUpdateListener { setToolbarsAlpha(fromView, toView, it.animatedValue as Float) }
                start()
            }

        return true
    }

    private fun setToolbarsAlpha(from: Toolbar, to: Toolbar, innerAlpha: Float) {
        from.apply {
            alpha = innerAlpha
            isInvisible = innerAlpha == 0f
        }

        to.apply {
            alpha = 1 - innerAlpha
            isInvisible = innerAlpha == 1f
        }
    }
}
