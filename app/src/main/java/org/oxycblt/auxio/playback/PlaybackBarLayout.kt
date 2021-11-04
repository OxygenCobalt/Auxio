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
import android.graphics.Insets
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.view.children
import androidx.core.view.updatePadding
import androidx.customview.widget.ViewDragHelper
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.systemBarsCompat

/**
 * A layout that manages the bottom playback bar while still enabling edge-to-edge to work
 * properly. The mechanism is mostly inspired by Material Files' PersistentBarLayout, however
 * this class was primarily written by me and I plan to expand this layout to become part of
 * the playback navigation process.
 *
 * TODO: Explain how this thing works so that others can be spared the pain of deciphering
 *  this custom viewgroup
 */
class PlaybackBarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {
    private val playbackView = CompactPlaybackView(context)
    private var barDragHelper = ViewDragHelper.create(this, ViewDragCallback())
    private var lastInsets: WindowInsets? = null

    init {
        addView(playbackView)

        (playbackView.layoutParams as LayoutParams).apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            isBar = true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(widthSize, heightSize)

        val barParams = playbackView.layoutParams as LayoutParams

        val barWidthSpec = getChildMeasureSpec(widthMeasureSpec, 0, barParams.width)
        val barHeightSpec = getChildMeasureSpec(heightMeasureSpec, 0, barParams.height)
        playbackView.measure(barWidthSpec, barHeightSpec)

        updateWindowInsets()
        measureContent()
    }

    private fun measureContent() {
        val barParams = playbackView.layoutParams as LayoutParams
        val barHeightAdjusted = (playbackView.measuredHeight * barParams.offset).toInt()

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
        val barHeight = playbackView.measuredHeight
        val barHeightAdjusted = (barHeight * (playbackView.layoutParams as LayoutParams).offset).toInt()

        for (child in children) {
            if (child.visibility == View.GONE) continue

            val childParams = child.layoutParams as LayoutParams

            if (childParams.isBar) {
                child.layout(
                    0, height - barHeightAdjusted,
                    width, height + (barHeight - barHeightAdjusted)
                )
            }
        }

        layoutContent()
    }

    private fun layoutContent() {
        for (child in children) {
            val childParams = child.layoutParams as LayoutParams

            if (!childParams.isBar) {
                child.layout(0, 0, child.measuredWidth, child.measuredHeight)
            }
        }
    }

    override fun dispatchApplyWindowInsets(insets: WindowInsets): WindowInsets {
        playbackView.updatePadding(bottom = insets.systemBarsCompat.bottom)

        lastInsets = insets
        updateWindowInsets()

        return insets
    }

    override fun computeScroll() {
        if (barDragHelper.continueSettling(true)) {
            postInvalidateOnAnimation()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        playbackView.clearCallback()
    }

    private fun updateWindowInsets() {
        val insets = lastInsets

        if (insets != null) {
            val adjustedInsets = adjustInsets(insets)

            for (child in children) {
                child.dispatchApplyWindowInsets(adjustedInsets)
            }
        }
    }

    private fun adjustInsets(insets: WindowInsets): WindowInsets {
        val barParams = playbackView.layoutParams as LayoutParams
        val childConsumedInset = (playbackView.measuredHeight * barParams.offset).toInt()

        val bars = insets.systemBarsCompat

        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                WindowInsets.Builder(insets)
                    .setInsets(
                        WindowInsets.Type.systemBars(),
                        Insets.of(
                            bars.left, bars.top,
                            bars.right, (bars.bottom - childConsumedInset).coerceAtLeast(0)
                        )
                    )
                    .build()
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
                @Suppress("DEPRECATION")
                insets.replaceSystemWindowInsets(
                    bars.left, bars.top,
                    bars.right, (bars.bottom - childConsumedInset).coerceAtLeast(0)
                )
            }

            else -> insets
        }
    }

    fun setSong(song: Song?, animate: Boolean = false) {
        if (song != null) {
            showBar(animate)
            playbackView.setSong(song)
        } else {
            hideBar(animate)
        }
    }

    fun setPlaying(isPlaying: Boolean) {
        playbackView.setPlaying(isPlaying)
    }

    fun setPosition(position: Long) {
        playbackView.setPosition(position)
    }

    fun setActionCallback(callback: ActionCallback) {
        playbackView.setCallback(callback)
    }

    private fun showBar(animate: Boolean) {
        val barParams = playbackView.layoutParams as LayoutParams

        if (barParams.shown || barParams.offset == 1f) {
            return
        }

        barParams.shown = true

        if (animate) {
            barDragHelper.smoothSlideViewTo(
                playbackView, playbackView.left, height - playbackView.height
            )
        } else {
            barParams.offset = 1f

            if (isLaidOut) {
                updateWindowInsets()
                measureContent()
                layoutContent()
            }
        }

        invalidate()
    }

    private fun hideBar(animate: Boolean) {
        val barParams = playbackView.layoutParams as LayoutParams

        if (barParams.shown || barParams.offset == 0f) {
            return
        }

        barParams.shown = false

        if (animate) {
            barDragHelper.smoothSlideViewTo(
                playbackView, playbackView.left, height
            )
        } else {
            barParams.offset = 0f

            if (isLaidOut) {
                updateWindowInsets()
                measureContent()
                layoutContent()
            }
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

    class LayoutParams : ViewGroup.LayoutParams {
        var isBar = false
        var shown = false
        var offset = 0f

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

        constructor(width: Int, height: Int) : super(width, height)

        constructor(source: LayoutParams) : super(source)

        constructor(source: ViewGroup.LayoutParams) : super(source)
    }

    interface ActionCallback {
        fun onPlayPauseClick()
        fun onNavToItem()
        fun onNavToPlayback()
    }

    private inner class ViewDragCallback : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean = false

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            val childRange = getViewVerticalDragRange(changedView)
            val childLayoutParams = changedView.layoutParams as LayoutParams

            val height = height
            childLayoutParams.offset = (height - top).toFloat() / childRange

            updateWindowInsets()
            measureContent()
            layoutContent()
        }

        override fun getViewVerticalDragRange(child: View): Int {
            val childParams = child.layoutParams as LayoutParams

            return if (childParams.isBar) {
                child.height
            } else {
                0
            }
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int = child.left

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return top.coerceIn(height - getViewVerticalDragRange(child)..height)
        }
    }
}
