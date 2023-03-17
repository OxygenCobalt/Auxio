/*
 * Copyright (c) 2022 Auxio Project
 * DetailAppBarLayout.kt is part of Auxio.
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
 
package org.oxycblt.auxio.detail

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import java.lang.reflect.Field
import org.oxycblt.auxio.R
import org.oxycblt.auxio.ui.CoordinatorAppBarLayout
import org.oxycblt.auxio.util.getInteger
import org.oxycblt.auxio.util.lazyReflectedField

/**
 * An [CoordinatorAppBarLayout] that displays the title of a hidden [Toolbar] when the scrolling
 * view goes beyond it's first item.
 *
 * This is intended for the detail views, in which the first item is the album/artist/genre header,
 * and thus scrolling past them should make the toolbar show the name in order to give context on
 * where the user currently is.
 *
 * This task should nominally be accomplished with CollapsingToolbarLayout, but I have not figured
 * out how to get that working sensibly yet.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class DetailAppBarLayout
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    CoordinatorAppBarLayout(context, attrs, defStyleAttr) {
    private var titleView: TextView? = null
    private var recycler: RecyclerView? = null

    private var titleShown: Boolean? = null
    private var titleAnimator: ValueAnimator? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (layoutParams as CoordinatorLayout.LayoutParams).behavior = Behavior(context)
    }

    private fun findTitleView(): TextView {
        val titleView = titleView
        if (titleView != null) {
            return titleView
        }

        // Assume that we have a Toolbar with a detail_toolbar ID, as this view is only
        // used within the detail layouts.
        val toolbar = findViewById<Toolbar>(R.id.detail_toolbar)

        // The Toolbar's title view is actually hidden. To avoid having to create our own
        // title view, we just reflect into Toolbar and grab the hidden field.
        val newTitleView =
            (TOOLBAR_TITLE_TEXT_FIELD.get(toolbar) as TextView).apply {
                // We can never properly initialize the title view's state before draw time,
                // so we just set it's alpha to 0f to produce a less jarring initialization
                // animation..
                alpha = 0f
            }

        this.titleView = newTitleView
        return newTitleView
    }

    private fun findRecyclerView(): RecyclerView {
        val recycler = recycler
        if (recycler != null) {
            return recycler
        }

        // Use the scrolling view in order to find a RecyclerView to use.
        val newRecycler = (parent as ViewGroup).findViewById<RecyclerView>(liftOnScrollTargetViewId)
        this.recycler = newRecycler
        return newRecycler
    }

    private fun setTitleVisibility(visible: Boolean) {
        if (titleShown == visible) return
        titleShown = visible

        val titleAnimator = titleAnimator
        if (titleAnimator != null) {
            titleAnimator.cancel()
            this.titleAnimator = null
        }

        // Emulate the AppBarLayout lift animation (Linear, alpha 0f -> 1f), but now with
        // the title view's alpha instead of the AppBarLayout's elevation.
        val titleView = findTitleView()
        val from: Float
        val to: Float

        if (visible) {
            from = 0f
            to = 1f
        } else {
            from = 1f
            to = 0f
        }

        if (titleView.alpha == to) {
            // Nothing to do
            return
        }

        this.titleAnimator =
            ValueAnimator.ofFloat(from, to).apply {
                addUpdateListener { titleView.alpha = it.animatedValue as Float }
                duration =
                    if (titleShown == true) {
                        context.getInteger(R.integer.anim_fade_enter_duration).toLong()
                    } else {
                        context.getInteger(R.integer.anim_fade_exit_duration).toLong()
                    }
                start()
            }
    }

    class Behavior
    @JvmOverloads
    constructor(context: Context? = null, attrs: AttributeSet? = null) :
        AppBarLayout.Behavior(context, attrs) {
        override fun onNestedPreScroll(
            coordinatorLayout: CoordinatorLayout,
            child: AppBarLayout,
            target: View,
            dx: Int,
            dy: Int,
            consumed: IntArray,
            type: Int
        ) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)

            val appBarLayout = child as DetailAppBarLayout
            val recycler = appBarLayout.findRecyclerView()

            // Title should be visible if we are no longer showing the top item
            // (i.e the header)
            appBarLayout.setTitleVisibility(
                (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() > 0)
        }
    }

    private companion object {
        val TOOLBAR_TITLE_TEXT_FIELD: Field by lazyReflectedField(Toolbar::class, "mTitleTextView")
    }
}
