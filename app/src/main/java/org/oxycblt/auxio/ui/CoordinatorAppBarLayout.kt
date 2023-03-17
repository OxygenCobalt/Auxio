/*
 * Copyright (c) 2021 Auxio Project
 * CoordinatorAppBarLayout.kt is part of Auxio.
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
import android.view.animation.AnimationUtils
import androidx.annotation.AttrRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import org.oxycblt.auxio.util.coordinatorLayoutBehavior

/**
 * An [AppBarLayout] that resolves two issues with the default implementation:
 * 1. Lift state failing to update when list data changes.
 * 2. Expansion causing jumping in [RecyclerView] instances.
 *
 * Note: This layout relies on [AppBarLayout.liftOnScrollTargetViewId] to figure out what scrolling
 * view to use. Failure to specify this will result in the layout not working.
 *
 * Derived from Material Files: https://github.com/zhanghai/MaterialFiles
 *
 * @author Hai Zhang, Alexander Capehart (OxygenCobalt)
 */
open class CoordinatorAppBarLayout
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
                    coordinator, this, coordinator, 0, 0, tConsumed, 0)
            }

            true
        }

    init {
        fitsSystemWindows = true
        viewTreeObserver.addOnPreDrawListener(onPreDraw)
    }

    /**
     * Expand this [AppBarLayout] with respect to the current [RecyclerView] at
     * [liftOnScrollTargetViewId], preventing it from jumping around.
     */
    fun expandWithScrollingRecycler() {
        setExpanded(true)
        (findScrollingChild() as? RecyclerView)?.let {
            addOnOffsetChangedListener(ExpansionHackListener(it))
        }
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

    /**
     * An [AppBarLayout.OnOffsetChangedListener] that will automatically move the given
     * [RecyclerView] as the [AppBarLayout] expands. Should be added right when the view is
     * expanding. Will be removed automatically.
     *
     * @param recycler [RecyclerView] to scroll with the [AppBarLayout].
     */
    private class ExpansionHackListener(private val recycler: RecyclerView) :
        OnOffsetChangedListener {
        private val offsetAnimationMaxEndTime =
            (AnimationUtils.currentAnimationTimeMillis() +
                APP_BAR_LAYOUT_MAX_OFFSET_ANIMATION_DURATION)
        private var currentVerticalOffset: Int? = null

        override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
            if (verticalOffset == 0 ||
                AnimationUtils.currentAnimationTimeMillis() > offsetAnimationMaxEndTime) {
                // AppBarLayout crashes with IndexOutOfBoundsException when a non-last listener
                // removes itself, so we have to do the removal asynchronously.
                appBarLayout.postOnAnimation { appBarLayout.removeOnOffsetChangedListener(this) }
            }

            // If possible, scroll by the offset delta between this update and the last update.
            val oldVerticalOffset = currentVerticalOffset
            currentVerticalOffset = verticalOffset
            if (oldVerticalOffset != null) {
                recycler.scrollBy(0, verticalOffset - oldVerticalOffset)
            }
        }
    }

    private companion object {
        /** @see AppBarLayout.BaseBehavior.MAX_OFFSET_ANIMATION_DURATION */
        const val APP_BAR_LAYOUT_MAX_OFFSET_ANIMATION_DURATION = 600
    }
}
