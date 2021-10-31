/*
 * Copyright (c) 2021 Auxio Project
 * PlaybackBarLayout.kt is part of Auxio.
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

package org.oxycblt.auxio.playback

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Insets
import android.os.Build
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.resolveAttr
import org.oxycblt.auxio.util.systemBarsCompat

/**
 * A layout that manages the bottom playback bar while still enabling edge-to-edge to work
 * properly. The mechanism is mostly inspired by Material Files' PersistentBarLayout, however
 * this class was primarily written by me and I plan to expand this layout to become part of
 * the playback navigation process.
 *
 * TODO: Migrate CompactPlaybackFragment to a view. This is okay, as updates can be delivered
 *  via MainFragment and it would fix the issue where the actual layout won't measure until
 *  the fragment is shown.
 * TODO: Implement animation
 * TODO: Implement the swipe-up behavior. This needs to occur, as the way the main fragment
 *  saves state results in'
 */
class PlaybackBarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {
    private val barLayout = FrameLayout(context)
    private val playbackFragment = CompactPlaybackFragment()
    private var lastInsets: WindowInsets? = null

    init {
        addView(barLayout)

        barLayout.apply {
            id = R.id.main_playback

            elevation = resources.getDimensionPixelSize(R.dimen.elevation_normal).toFloat()

            (layoutParams as LayoutParams).apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                isBar = true
            }

            background = MaterialShapeDrawable.createWithElevationOverlay(context).apply {
                elevation = barLayout.elevation
                fillColor = ColorStateList.valueOf(R.attr.colorSurface.resolveAttr(context))
            }
        }

        if (!isInEditMode) {
            (context as AppCompatActivity).supportFragmentManager.apply {
                this
                    .beginTransaction()
                    .replace(R.id.main_playback, playbackFragment)
                    .commit()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(widthSize, heightSize)

        val barParams = barLayout.layoutParams as LayoutParams

        val barWidthSpec = getChildMeasureSpec(widthMeasureSpec, 0, barParams.width)
        val barHeightSpec = getChildMeasureSpec(heightMeasureSpec, 0, barParams.height)
        barLayout.measure(barWidthSpec, barHeightSpec)

        updateWindowInsets()

        val barHeightAdjusted = (barLayout.measuredHeight * barParams.offset).toInt()

        val contentWidth = measuredWidth
        val contentHeight = measuredHeight - barHeightAdjusted

        for (child in children) {
            if (child.visibility == View.GONE) continue

            val childParams = child.layoutParams as LayoutParams

            if (!childParams.isBar) {
                val childWidthSpec = MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.EXACTLY)
                val childHeightSpec = MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY)

                child.measure(childWidthSpec, childHeightSpec)
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val barHeight = if (barLayout.isVisible) {
            barLayout.measuredHeight
        } else {
            0
        }

        val barHeightAdjusted = (barHeight * (barLayout.layoutParams as LayoutParams).offset).toInt()

        for (child in children) {
            if (child.visibility == View.GONE) continue

            val childParams = child.layoutParams as LayoutParams

            if (childParams.isBar) {
                child.layout(
                    0, height - barHeightAdjusted,
                    width, height + (barHeight - barHeightAdjusted)
                )
            } else {
                child.layout(0, 0, child.measuredWidth, child.measuredHeight)
            }
        }
    }

    override fun dispatchApplyWindowInsets(insets: WindowInsets): WindowInsets {
        barLayout.updatePadding(bottom = insets.systemBarsCompat.bottom)

        lastInsets = insets
        updateWindowInsets()

        return insets
    }

    private fun updateWindowInsets() {
        val insets = lastInsets

        if (insets != null) {
            super.dispatchApplyWindowInsets(mutateInsets(insets))
        }
    }

    private fun mutateInsets(insets: WindowInsets): WindowInsets {
        val barParams = barLayout.layoutParams as LayoutParams
        val childConsumedInset = (barLayout.measuredHeight * barParams.offset).toInt()

        val bars = insets.systemBarsCompat

        // TODO: Q support
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                return WindowInsets.Builder(insets)
                    .setInsets(
                        WindowInsets.Type.systemBars(),
                        Insets.of(
                            bars.left, bars.top,
                            bars.right, (bars.bottom - childConsumedInset).coerceAtLeast(0)
                        )
                    )
                    .build()
            }
        }

        return insets
    }

    fun showBar() {
        val barParams = barLayout.layoutParams as LayoutParams

        if (barParams.offset == 1f) {
            return
        }

        barParams.offset = 1f

        if (isLaidOut) {
            updateWindowInsets()
        }

        invalidate()
    }

    fun hideBar() {
        val barParams = barLayout.layoutParams as LayoutParams

        if (barParams.offset == 0f) {
            return
        }

        barParams.offset = 0f

        if (isLaidOut) {
            updateWindowInsets()
        }

        invalidate()
    }

    // --- LAYOUT PARAMS ---

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(
        layoutParams: ViewGroup.LayoutParams
    ): ViewGroup.LayoutParams =
        when (layoutParams) {
            is LayoutParams -> LayoutParams(layoutParams)
            is MarginLayoutParams -> LayoutParams(layoutParams)
            else -> LayoutParams(layoutParams)
        }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams =
        LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

    override fun checkLayoutParams(layoutParams: ViewGroup.LayoutParams): Boolean =
        layoutParams is LayoutParams && super.checkLayoutParams(layoutParams)

    @Suppress("UNUSED")
    class LayoutParams : ViewGroup.LayoutParams {
        var isBar = false
        var offset = 0f

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

        constructor(width: Int, height: Int) : super(width, height)

        constructor(source: LayoutParams) : super(source)

        constructor(source: ViewGroup.LayoutParams) : super(source)
    }
}
