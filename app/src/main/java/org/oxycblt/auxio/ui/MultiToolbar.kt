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

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.IdRes
import androidx.core.view.children
import timber.log.Timber as L

class MultiToolbar
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    private var animator: Animator? = null
    private var currentlyVisible = 0
    private val flipper = MaterialFlipper(context)

    override fun onFinishInflate() {
        super.onFinishInflate()
        for (i in 1 until childCount) {
            getChildAt(i).apply { flipper.jump(this) }
        }
    }

    fun setVisible(@IdRes viewId: Int): Boolean {
        val index = children.indexOfFirst { it.id == viewId }
        if (index == currentlyVisible) return false
        L.d("Switching toolbar visibility from $currentlyVisible -> $index")
        return animateToolbarsVisibility(currentlyVisible, index).also { currentlyVisible = index }
    }

    private fun animateToolbarsVisibility(from: Int, to: Int): Boolean {
        // TODO: Animate nicer Material Fade transitions using animators (Normal transitions
        //  don't work due to translation)
        // Set up the target transitions for both the inner and selection toolbars.
        L.d("Changing toolbar visibility $from -> 0f, $to -> 1f")
        animator?.cancel()
        animator = flipper.flip(getChildAt(from), getChildAt(to)).also { it.start() }

        return true
    }
}
