/*
 * Copyright (c) 2021 Auxio Project
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
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.AttrRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.appbar.AppBarLayout
import org.oxycblt.auxio.util.coordinatorLayoutBehavior

/**
 * An [AppBarLayout] that fixes a bug with the default implementation where the lifted state will
 * not properly respond to RecyclerView events.
 *
 * **Note:** This layout relies on [AppBarLayout.liftOnScrollTargetViewId] to figure out what
 * scrolling view to use. Failure to specify this will result in the layout not working.
 */
open class AuxioAppBarLayout
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    AppBarLayout(context, attrs, defStyleAttr) {
    private var scrollingChild: View? = null
    private val tConsumed = IntArray(2)

    private val onPreDraw =
        ViewTreeObserver.OnPreDrawListener {
            val child = findScrollingChild()

            if (child != null) {
                val coordinator = parent as CoordinatorLayout
                coordinatorLayoutBehavior?.onNestedPreScroll(
                    coordinator,
                    this,
                    coordinator,
                    0,
                    0,
                    tConsumed,
                    0
                )
            }

            true
        }

    init {
        fitsSystemWindows = true
        viewTreeObserver.addOnPreDrawListener(onPreDraw)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewTreeObserver.removeOnPreDrawListener(onPreDraw)
        scrollingChild = null
    }

    override fun setLiftOnScrollTargetViewId(liftOnScrollTargetViewId: Int) {
        super.setLiftOnScrollTargetViewId(liftOnScrollTargetViewId)

        // Sometimes we dynamically set the scrolling child [such as in HomeFragment], so clear it
        // and re-draw when that occurs.
        scrollingChild = null
        onPreDraw.onPreDraw()
    }

    private fun findScrollingChild(): View? {
        // Roll some custom code for finding our scrolling view. This can be anything as long as
        // it updates this layout in it's onNestedPreScroll call.
        if (scrollingChild == null) {
            if (liftOnScrollTargetViewId != ResourcesCompat.ID_NULL) {
                scrollingChild = (parent as ViewGroup).findViewById(liftOnScrollTargetViewId)
            } else {
                error("liftOnScrollTargetViewId was not specified")
            }
        }

        return scrollingChild
    }
}
