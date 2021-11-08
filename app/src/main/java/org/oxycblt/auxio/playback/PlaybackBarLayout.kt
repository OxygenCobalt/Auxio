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
import androidx.customview.widget.ViewDragHelper
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.systemBarsCompat

/**
 * A layout that manages the bottom playback bar while still enabling edge-to-edge to work
 * properly. The mechanism is mostly inspired by Material Files' PersistentBarLayout, however
 * this class was primarily written by me.
 *
 * TODO: Add a swipe-up behavior a la Phonograph. I think that would improve UX.
 * TODO: Leverage this layout to make more tablet-friendly UIs
 *
 * @author OxygenCobalt
 */
class PlaybackBarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {
    private val playbackView = CompactPlaybackView(context)
    private var barDragHelper = ViewDragHelper.create(this, BarDragCallback())
    private var lastInsets: WindowInsets? = null

    init {
        addView(playbackView)

        // playbackView is special as it's the view we treat as a bottom bar.
        // Mark it as such.
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

        // Measure the bar view so that it fills the whole screen and takes up the bottom views.
        val barParams = playbackView.layoutParams as LayoutParams

        val barWidthSpec = getChildMeasureSpec(widthMeasureSpec, 0, barParams.width)
        val barHeightSpec = getChildMeasureSpec(heightMeasureSpec, 0, barParams.height)
        playbackView.measure(barWidthSpec, barHeightSpec)

        applyContentWindowInsets()
        measureContent()
    }

    /**
     * Measure the content views in this layout. This is done separately as at times we want
     * to relayout the content views but not relayout the bar view.
     */
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
        val barParams = (playbackView.layoutParams as LayoutParams)
        val barHeightAdjusted = (barHeight * barParams.offset).toInt()

        // Again, lay out our view like we measured it.
        playbackView.layout(
            0, height - barHeightAdjusted,
            width, height + (barHeight - barHeightAdjusted)
        )

        layoutContent()
    }

    /**
     * Layout the content views in this layout. This is done separately as at times we want
     * to relayout the content views but not relayout the bar view.
     */
    private fun layoutContent() {
        for (child in children) {
            val childParams = child.layoutParams as LayoutParams

            if (!childParams.isBar) {
                child.layout(0, 0, child.measuredWidth, child.measuredHeight)
            }
        }
    }

    override fun dispatchApplyWindowInsets(insets: WindowInsets): WindowInsets {
        // Applying window insets is the real special sauce of this layout. The problem with
        // having a bottom bar is that if you support edge-to-edge, applying insets to views
        // will result in spacing being incorrect whenever the bar is shown. If you cleverly
        // modify the insets however, you can make all content views remove their spacing as
        // the bar enters. This function itself is unimportant, so you should probably take
        // a look at applyContentWindowInsets and adjustInsets instead.
        playbackView.onApplyWindowInsets(insets)

        lastInsets = insets
        applyContentWindowInsets()

        return insets
    }

    /**
     * Apply window insets to the content views in this layouts. This is done separately as at
     * times we want to relayout the content views but not relayout the bar view.
     */
    private fun applyContentWindowInsets() {
        val insets = lastInsets

        if (insets != null) {
            val adjustedInsets = adjustInsets(insets)

            for (child in children) {
                val childParams = child.layoutParams as LayoutParams

                if (!childParams.isBar) {
                    child.dispatchApplyWindowInsets(adjustedInsets)
                }
            }
        }
    }

    /**
     * Adjust window insets to line up with the playback bar
     */
    private fun adjustInsets(insets: WindowInsets): WindowInsets {
        // Find how much space the bar is consuming right now. We use this to modify
        // the bottom window inset so that the spacing checks out, 0 if the bar is fully
        // shown and the original value if the bar is hidden.
        val barParams = playbackView.layoutParams as LayoutParams
        val barConsumedInset = (playbackView.measuredHeight * barParams.offset).toInt()

        val bars = insets.systemBarsCompat
        val adjustedBottomInset = (bars.bottom - barConsumedInset).coerceAtLeast(0)

        return when {
            // Android R. Modify insets using their new method that exists for no reason
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                WindowInsets.Builder(insets)
                    .setInsets(
                        WindowInsets.Type.systemBars(),
                        Insets.of(bars.left, bars.top, bars.right, adjustedBottomInset)
                    )
                    .build()
            }

            // Android O. Modify insets using the original method
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
                @Suppress("DEPRECATION")
                insets.replaceSystemWindowInsets(
                    bars.left, bars.top, bars.right, adjustedBottomInset
                )
            }

            else -> insets
        }
    }

    override fun computeScroll() {
        // Copied this from MaterialFiles.
        // Don't know what this does, but it seems important so I just keep it around.
        if (barDragHelper.continueSettling(true)) {
            postInvalidateOnAnimation()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        // Prevent memory leaks
        playbackView.clearCallback()
    }

    /**
     * Update the song that this layout is showing. This will be reflected in the compact view
     * at the bottom of the screen.
     * @param animate Whether to animate bar showing/hiding events.
     */
    fun setSong(song: Song?, animate: Boolean = false) {
        if (song != null) {
            showBar(animate)
            playbackView.setSong(song)
        } else {
            hideBar(animate)
        }
    }

    /**
     * Update the playing status on this layout. This will be reflected in the compact view
     * at the bottom of the screen.
     */
    fun setPlaying(isPlaying: Boolean) {
        playbackView.setPlaying(isPlaying)
    }

    /**
     * Update the playback positon on this layout. This will be reflected in the compact view
     * at the bottom of the screen.
     */
    fun setPosition(position: Long) {
        playbackView.setPosition(position)
    }

    /**
     * Add a callback for actions from the compact playback view in this layout.
     */
    fun setActionCallback(callback: ActionCallback) {
        playbackView.setCallback(callback)
    }

    private fun showBar(animate: Boolean) {
        val barParams = playbackView.layoutParams as LayoutParams

        if (barParams.shown || barParams.offset == 1f) {
            // Already showed the bar, don't do it again.
            return
        }

        barParams.shown = true

        if (animate) {
            // Animate, use our drag helper to slide the view upwards. All invalidation is done
            // in the callback.
            barDragHelper.smoothSlideViewTo(
                playbackView, playbackView.left, height - playbackView.height
            )
        } else {
            // Don't animate, snap the view and invalidate the content views if we are already
            // laid out. Otherwise we will do it later so don't waste time now.
            barParams.offset = 1f

            if (isLaidOut) {
                applyContentWindowInsets()
                measureContent()
                layoutContent()
            }
        }

        invalidate()
    }

    private fun hideBar(animate: Boolean) {
        val barParams = playbackView.layoutParams as LayoutParams

        if (barParams.shown || barParams.offset == 0f) {
            // Already hid the bar, don't do it again.
            return
        }

        barParams.shown = false

        if (animate) {
            // Animate, use our drag helper to slide the view upwards. All invalidation is done
            // in the callback.
            barDragHelper.smoothSlideViewTo(
                playbackView, playbackView.left, height
            )
        } else {
            // Don't animate, snap the view and invalidate the content views if we are already
            // laid out. Otherwise we will do it later so don't waste time now.
            barParams.offset = 0f

            if (isLaidOut) {
                applyContentWindowInsets()
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

    /**
     * A callback for actions done from this view. This fragment can inherit this and recieve
     * updates from the compact playback view in this layout that can then be sent to the
     * internal playback engine.
     *
     * There is no need to clear this callback when done, the view clears it itself when the
     * view is detached.
     */
    interface ActionCallback {
        fun onPlayPauseClick()
        fun onNavToItem()
        fun onNavToPlayback()
    }

    /**
     * Layout parameters for this layout. This layout is meant to be a black box with only two
     * types of views, so this implementation is kept private.
     */
    private class LayoutParams : ViewGroup.LayoutParams {
        var isBar = false
        var shown = false
        var offset = 0f

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

        constructor(width: Int, height: Int) : super(width, height)

        constructor(source: LayoutParams) : super(source)

        constructor(source: ViewGroup.LayoutParams) : super(source)
    }

    /**
     * Internal drag callback for animating the bar view showing/hiding.
     */
    private inner class BarDragCallback : ViewDragHelper.Callback() {
        // We aren't actually dragging things. Ignore this.
        override fun tryCaptureView(child: View, pointerId: Int): Boolean = false

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            val childRange = getViewVerticalDragRange(changedView)
            val childParams = changedView.layoutParams as LayoutParams

            // Find the new offset that this view takes up after an animation frame.
            childParams.offset = (height - top).toFloat() / childRange

            // Invalidate our content views so that they accurately reflect the bar now.
            applyContentWindowInsets()
            measureContent()
            layoutContent()
        }

        override fun getViewVerticalDragRange(child: View): Int {
            val childParams = child.layoutParams as LayoutParams

            // Sanity check
            check(childParams.isBar) { "This drag helper is only meant for content views" }

            return child.height
        }

        // Don't really know what these do but they're needed

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int = child.left

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return top.coerceIn(height - getViewVerticalDragRange(child)..height)
        }
    }
}
