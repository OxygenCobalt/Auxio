/*
 * Copyright (c) 2021 Auxio Project
 * CobaltCoordinatorLayout.kt is part of Auxio.
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
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.StyleRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout

/**
 * An [AppBarLayout] that fixes a bug with the default implementation where the lifted state
 * will not properly respond to RecyclerView events.
 * TODO: Find a way to get the lift animation to not animate on startup.
 */
class LiftAppBarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @StyleRes defStyleAttr: Int = -1
) : AppBarLayout(context, attrs, defStyleAttr) {
    private var recycler: RecyclerView? = null
    private val tConsumed = IntArray(2)

    private val onPreDraw = ViewTreeObserver.OnPreDrawListener {
        recycler?.let { rec ->
            val coordinator = (parent as CoordinatorLayout)

            (layoutParams as CoordinatorLayout.LayoutParams).behavior?.onNestedPreScroll(
                coordinator, this, rec, 0, 0, tConsumed, 0
            )
        }

        true
    }

    init {
        viewTreeObserver.addOnPreDrawListener(onPreDraw)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // Assume there is one RecyclerView [Because there is]
        recycler = (parent as ViewGroup).children.firstOrNull { it is RecyclerView }
            as RecyclerView?
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        viewTreeObserver.removeOnPreDrawListener(onPreDraw)
    }
}
