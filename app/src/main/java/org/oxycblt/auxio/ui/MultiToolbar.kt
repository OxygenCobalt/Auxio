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

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.IdRes
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.dynamicanimation.animation.SpringAnimation
import org.oxycblt.auxio.util.scale
import timber.log.Timber as L

class MultiToolbar
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    private var outAnimators: List<SpringAnimation>? = null
    private var inAnimators: List<SpringAnimation>? = null
    private var currentlyVisible = 0
    private val outScaleSpring = Spatial.FAST
    private val outAlphaSpring = Effect.FAST
    private val inScaleSpring = Spatial.DEFAULT
    private val inAlphaSpring = Effect.DEFAULT

    override fun onFinishInflate() {
        super.onFinishInflate()
        for (i in 1 until childCount) {
            getChildAt(i).apply {
                scale = 0.9f
                alpha = 0f
                isInvisible = true
            }
        }
    }

    fun setVisible(@IdRes viewId: Int): Boolean {
        val index = children.indexOfFirst { it.id == viewId }
        if (index == currentlyVisible) return false
        L.d("Switching toolbar visibility from $currentlyVisible -> $index")
        return animateToolbarsVisibility(currentlyVisible, index).also { currentlyVisible = index }
    }

    private fun animateToolbarsVisibility(from: Int, to: Int): Boolean {
        // Set up the target transitions for both the inner and selection toolbars.
        L.d("Changing toolbar visibility $from -> 0f, $to -> 1f")
        inAnimators?.forEach { it.cancel() }
        outAnimators?.forEach { it.cancel() }
        val from = getChildAt(from)
        val to = getChildAt(to)
        // since we will lose track of the going-out view if we suddenly cancel it's probably better
        // to just jump to the end rather than have overlapping views or a stuck toolbar
        val outScaleAnimation = outScaleSpring.scale(from, 0.9f, jumpOnCancellation = true)
        val outAlphaAnimation =
            outAlphaSpring.alpha(from, 0.0f, jumpOnCancellation = true).addEndListener {
                _,
                cancelled,
                _,
                _ ->
                if (!cancelled) {
                    val inScaleAnimation = inScaleSpring.scale(to, 1.0f)
                    val inAlphaAnimation = inAlphaSpring.alpha(to, 1.0f)
                    inAnimators = listOf(inScaleAnimation, inAlphaAnimation)
                }
            }
        outAnimators = listOf(outScaleAnimation, outAlphaAnimation)

        return true
    }
}
