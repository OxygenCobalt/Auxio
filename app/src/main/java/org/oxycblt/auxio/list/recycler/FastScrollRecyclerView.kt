/*
 * Copyright (c) 2021 Auxio Project
 * FastScrollRecyclerView.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list.recycler

import android.animation.Animator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.WindowInsets
import androidx.annotation.AttrRes
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt
import org.oxycblt.auxio.R
import org.oxycblt.auxio.ui.MaterialSlider
import org.oxycblt.auxio.util.getDimenPixels
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.isRtl
import org.oxycblt.auxio.util.isUnder
import org.oxycblt.auxio.util.systemBarInsetsCompat

/**
 * A [RecyclerView] that enables better fast-scrolling. This is fundamentally a implementation of
 * Hai Zhang's AndroidFastScroll but slimmed down for Auxio and with a couple of enhancements.
 *
 * Attributions as per the Apache 2.0 license:
 * - ORIGINAL AUTHOR: Hai Zhang [https://github.com/zhanghai]
 * - PROJECT: Android Fast Scroll [https://github.com/zhanghai/AndroidFastScroll]
 * - MODIFIER: OxygenCobalt [https://github.com/oxygencobalt]
 *
 * !!! MODIFICATIONS !!!:
 * - Scroller will no longer show itself on startup or relayouts, which looked unpleasant with
 *   multiple views
 * - DefaultAnimationHelper and RecyclerViewHelper were merged into the class
 * - FastScroller overlay was merged into RecyclerView instance
 * - Removed FastScrollerBuilder
 * - Converted all code to kotlin
 * - Use modified Auxio resources instead of AFS resources
 * - Track view is now only used for touch bounds
 * - Redundant functions have been merged
 * - Variable names are no longer prefixed with m
 * - Added drag listener
 * - Added documentation
 * - Completely new design
 *
 * @author Hai Zhang, Alexander Capehart (OxygenCobalt)
 *
 * TODO: Add vibration when popup changes
 */
class FastScrollRecyclerView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    AuxioRecyclerView(context, attrs, defStyleAttr) {
    // Thumb
    private val thumbWidth = context.getDimenPixels(R.dimen.spacing_mid_medium)
    private val thumbHeight = context.getDimenPixels(R.dimen.size_touchable_medium)
    private val slider = MaterialSlider(context, thumbWidth)
    private var thumbAnimator: Animator? = null

    private val thumbView =
        context.inflater.inflate(R.layout.view_scroll_thumb, null).apply { slider.jumpOut(this) }
    private val thumbPadding = Rect(0, 0, 0, 0)
    private var thumbOffset = 0

    private var showingThumb = false
    private val hideThumbRunnable = Runnable {
        if (!dragging) {
            hideScrollbar()
        }
    }

    // Touch
    private val minTouchTargetSize = context.getDimenPixels(R.dimen.size_touchable_small)
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    private var downX = 0f
    private var downY = 0f
    private var lastY = 0f
    private var dragStartY = 0f
    private var dragStartThumbOffset = 0

    private var thumbEnabled = false
        set(value) {
            if (field == value) {
                return
            }

            field = value
            if (value) {
                removeCallbacks(hideThumbRunnable)
                hideScrollbar()
            }

            listener?.onFastScrollingChanged(field)
        }

    private var dragging = false
        set(value) {
            if (field == value) {
                return
            }

            field = value

            if (value) {
                parent.requestDisallowInterceptTouchEvent(true)
            }

            thumbView.isPressed = value

            if (field) {
                removeCallbacks(hideThumbRunnable)
                showScrollbar()
            } else {
                postAutoHideScrollbar()
            }

            listener?.onFastScrollingChanged(field)
        }

    var popupProvider: PopupProvider? = null
    var listener: Listener? = null

    init {
        overlay.add(thumbView)

        addItemDecoration(
            object : ItemDecoration() {
                override fun onDraw(canvas: Canvas, parent: RecyclerView, state: State) {
                    onPreDraw()
                }
            })

        // We use a listener instead of overriding onTouchEvent so that we don't conflict with
        // RecyclerView touch events.
        addOnItemTouchListener(
            object : SimpleOnItemTouchListener() {
                override fun onTouchEvent(recyclerView: RecyclerView, event: MotionEvent) {
                    onItemTouch(event)
                }

                override fun onInterceptTouchEvent(
                    recyclerView: RecyclerView,
                    event: MotionEvent
                ): Boolean {
                    return onItemTouch(event)
                }
            })
    }

    // --- RECYCLERVIEW EVENT MANAGEMENT ---

    private fun onPreDraw() {
        updateScrollbarState()

        thumbView.layoutDirection = layoutDirection
        thumbView.measure(
            MeasureSpec.makeMeasureSpec(thumbWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(thumbHeight, MeasureSpec.EXACTLY))
        val thumbTop = thumbPadding.top + thumbOffset
        val thumbLeft =
            if (isRtl) {
                thumbPadding.left
            } else {
                width - thumbPadding.right - thumbWidth
            }
        thumbView.layout(thumbLeft, thumbTop, thumbLeft + thumbWidth, thumbTop + thumbHeight)
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)

        updateScrollbarState()

        // Measure or layout events result in a fake onScrolled call. Ignore those.
        if (dx == 0 && dy == 0) {
            return
        }

        showScrollbar()
        postAutoHideScrollbar()
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        super.onApplyWindowInsets(insets)
        val bars = insets.systemBarInsetsCompat
        thumbPadding.bottom = bars.bottom
        return insets
    }

    private fun updateScrollbarState() {
        // Then calculate the thumb position, which is just:
        // [proportion of scroll position to scroll range] * [total thumb range]
        val offsetY = computeVerticalScrollOffset()
        if (computeVerticalScrollRange() < height || childCount == 0) {
            return
        }
        val extentY = computeVerticalScrollExtent()
        val fraction = (offsetY).toFloat() / (computeVerticalScrollRange() - extentY)
        thumbOffset = (thumbOffsetRange * fraction).toInt()
    }

    private fun onItemTouch(event: MotionEvent): Boolean {
        val eventX = event.x
        val eventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = eventX
                downY = eventY

                if (eventX >= thumbView.left && eventX < thumbView.right) {
                    dragStartY = eventY

                    if (thumbView.isUnder(eventX, eventY, minTouchTargetSize)) {
                        dragStartThumbOffset = thumbOffset
                    } else if (eventX > thumbView.right - thumbWidth / 4) {
                        dragStartThumbOffset =
                            (eventY - thumbPadding.top - thumbHeight / 2f).toInt()
                        scrollToThumbOffset(dragStartThumbOffset)
                    } else {
                        return false
                    }

                    dragging = true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (!dragging &&
                    thumbView.isUnder(downX, thumbView.top.toFloat(), minTouchTargetSize) &&
                    abs(eventY - downY) > touchSlop) {
                    if (thumbView.isUnder(downX, downY, minTouchTargetSize)) {
                        dragStartY = lastY
                        dragStartThumbOffset = thumbOffset
                    } else {
                        dragStartY = eventY
                        dragStartThumbOffset =
                            (eventY - thumbPadding.top - thumbHeight / 2f).toInt()
                        scrollToThumbOffset(dragStartThumbOffset)
                    }

                    dragging = true
                }

                if (dragging) {
                    val thumbOffset = dragStartThumbOffset + (eventY - dragStartY).toInt()
                    scrollToThumbOffset(thumbOffset)
                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> dragging = false
        }

        lastY = eventY
        return dragging
    }

    private fun scrollToThumbOffset(thumbOffset: Int) {
        val rangeY = computeVerticalScrollRange() - computeVerticalScrollExtent()
        val previousThumbOffset = this.thumbOffset.coerceAtLeast(0).coerceAtMost(thumbOffsetRange)
        val previousOffsetY = rangeY * (previousThumbOffset / thumbOffsetRange.toFloat())
        val newThumbOffset = thumbOffset.coerceAtLeast(0).coerceAtMost(thumbOffsetRange)
        val newOffsetY = rangeY * (newThumbOffset / thumbOffsetRange.toFloat())
        if (newOffsetY == 0f) {
            // Hacky workaround to drift in vertical scroll offset where we just snap
            // to the top if the thumb offset hit zero.
            scrollToPosition(0)
            return
        }
        val dy = newOffsetY - previousOffsetY
        scrollBy(0, max(dy.roundToInt(), -computeVerticalScrollOffset()))
    }

    // --- SCROLLBAR APPEARANCE ---

    private fun postAutoHideScrollbar() {
        removeCallbacks(hideThumbRunnable)
        postDelayed(hideThumbRunnable, AUTO_HIDE_SCROLLBAR_DELAY_MILLIS.toLong())
    }

    private fun showScrollbar() {
        if (showingThumb) {
            return
        }

        showingThumb = true
        thumbAnimator?.cancel()
        thumbAnimator = slider.slideIn(thumbView).also { it.start() }
    }

    private fun hideScrollbar() {
        if (!showingThumb) {
            return
        }

        showingThumb = false
        thumbAnimator?.cancel()
        thumbAnimator = slider.slideOut(thumbView).also { it.start() }
    }

    // --- LAYOUT STATE ---

    private val thumbOffsetRange: Int
        get() {
            return height - thumbPadding.top - thumbPadding.bottom - thumbHeight
        }

    /** An interface to provide text to use in the popup when fast-scrolling. */
    interface PopupProvider {
        /**
         * Get text to use in the popup at the specified position.
         *
         * @param pos The position in the list.
         * @return A [String] to use in the popup. Null if there is no applicable text for the popup
         *   at [pos].
         */
        fun getPopup(pos: Int): String?
    }

    /** A listener for fast scroller interactions. */
    interface Listener {
        /**
         * Called when the fast scrolling state changes.
         *
         * @param isFastScrolling true if the user is currently fast scrolling, false otherwise.
         */
        fun onFastScrollingChanged(isFastScrolling: Boolean)
    }

    private companion object {
        const val AUTO_HIDE_SCROLLBAR_DELAY_MILLIS = 500
    }
}
