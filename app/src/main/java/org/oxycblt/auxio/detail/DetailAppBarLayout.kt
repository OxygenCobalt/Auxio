/*
 * Copyright (c) 2022 Auxio Project
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
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import java.lang.Exception
import java.lang.reflect.Field
import org.oxycblt.auxio.R
import org.oxycblt.auxio.ui.coordinator.EdgeAppBarLayout
import org.oxycblt.auxio.util.lazyReflectedField
import org.oxycblt.auxio.util.logE
import org.oxycblt.auxio.util.logTraceOrThrow

/**
 * An [EdgeAppBarLayout] variant that also shows the name of the toolbar whenever the detail
 * recyclerview is scrolled beyond it's first item (a.k.a the header). This is used instead of
 * CollapsingToolbarLayout since that thing is a mess with crippling bugs and state issues. This
 * just works.
 * @author OxygenCobalt
 */
class DetailAppBarLayout
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    EdgeAppBarLayout(context, attrs, defStyleAttr) {
    private var titleView: AppCompatTextView? = null
    private var recycler: RecyclerView? = null

    private var titleShown: Boolean? = null
    private var titleAnimator: ValueAnimator? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (layoutParams as CoordinatorLayout.LayoutParams).behavior = Behavior(context)
    }

    private fun findTitleView(): AppCompatTextView? {
        val titleView = titleView
        if (titleView != null) {
            return titleView
        }

        val toolbar = findViewById<Toolbar>(R.id.detail_toolbar)

        // Reflect to get the actual title view to do transformations on
        val newTitleView =
            try {
                TOOLBAR_TITLE_TEXT_FIELD.get(toolbar) as AppCompatTextView
            } catch (e: Exception) {
                logE("Could not get toolbar title view (likely an internal code change)")
                e.logTraceOrThrow()
                return null
            }

        newTitleView.alpha = 0f
        this.titleView = newTitleView
        return newTitleView
    }

    private fun findRecyclerView(): RecyclerView {
        val recycler = recycler
        if (recycler != null) {
            return recycler
        }

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

        if (titleView?.alpha == to) return

        this.titleAnimator =
            ValueAnimator.ofFloat(from, to).apply {
                addUpdateListener { titleView?.alpha = it.animatedValue as Float }
                duration =
                    resources.getInteger(R.integer.detail_app_bar_title_anim_duration).toLong()
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

            val appBar = child as DetailAppBarLayout
            val recycler = appBar.findRecyclerView()

            val showTitle =
                (recycler.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() > 0

            appBar.setTitleVisibility(showTitle)
        }
    }

    companion object {
        private val TOOLBAR_TITLE_TEXT_FIELD: Field by
            lazyReflectedField(Toolbar::class, "mTitleTextView")
    }
}
