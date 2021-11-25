package org.oxycblt.auxio.detail

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import org.oxycblt.auxio.R
import org.oxycblt.auxio.ui.EdgeAppBarLayout

/**
 * An [EdgeAppBarLayout] variant that also shows the name of the toolbar whenever the detail
 * recyclerview is scrolled beyond it's first item (a.k.a the header). This is used instead of
 * CollapsingToolbarLayout since that thing is a mess with crippling bugs and state issues.
 * This just works.
 * @author OxygenCobalt
 */
class DetailAppBarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @StyleRes defStyleAttr: Int = -1
) : EdgeAppBarLayout(context, attrs, defStyleAttr) {
    private var mTitleView: AppCompatTextView? = null
    private var mRecycler: RecyclerView? = null

    private var titleShown: Boolean? = null
    private var mTitleAnimator: ValueAnimator? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        (layoutParams as CoordinatorLayout.LayoutParams).behavior = Behavior(context)
    }

    private fun findTitleView(): AppCompatTextView {
        val titleView = mTitleView

        if (titleView != null) {
            return titleView
        }

        val toolbar = findViewById<Toolbar>(R.id.detail_toolbar)

        // Reflect to get the actual title view to do transformations on
        val newTitleView = Toolbar::class.java.getDeclaredField("mTitleTextView").run {
            isAccessible = true
            get(toolbar) as AppCompatTextView
        }

        newTitleView.alpha = 0f

        mTitleView = newTitleView
        return newTitleView
    }

    private fun findRecyclerView(): RecyclerView {
        val recycler = mRecycler

        if (recycler != null) {
            return recycler
        }

        val newRecycler = (parent as ViewGroup).findViewById<RecyclerView>(liftOnScrollTargetViewId)

        mRecycler = newRecycler
        return newRecycler
    }

    private fun setTitleVisibility(visible: Boolean) {
        if (titleShown == visible) return

        titleShown = visible

        if (mTitleAnimator != null) {
            mTitleAnimator!!.cancel()
            mTitleAnimator = null
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

        if (titleView.alpha == to) return

        mTitleAnimator = ValueAnimator.ofFloat(from, to).apply {
            addUpdateListener {
                titleView.alpha = it.animatedValue as Float
            }

            duration = resources.getInteger(R.integer.app_bar_elevation_anim_duration).toLong()

            start()
        }
    }

    class Behavior @JvmOverloads constructor(
        context: Context? = null,
        attrs: AttributeSet? = null
    ) : AppBarLayout.Behavior(context, attrs) {
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

            val showTitle = (recycler.layoutManager as LinearLayoutManager)
                .findFirstVisibleItemPosition() > 0

            appBar.setTitleVisibility(showTitle)
        }
    }
}
