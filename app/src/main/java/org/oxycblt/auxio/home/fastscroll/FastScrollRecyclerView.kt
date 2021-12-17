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

package org.oxycblt.auxio.home.fastscroll

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.math.MathUtils
import androidx.core.view.isInvisible
import androidx.core.view.updatePadding
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.canScroll
import org.oxycblt.auxio.util.resolveAttr
import org.oxycblt.auxio.util.resolveDrawable
import org.oxycblt.auxio.util.systemBarsCompat
import kotlin.math.abs

/**
 * A [RecyclerView] that enables better fast-scrolling. This is fundamentally a implementation of
 * Hai Zhang's AndroidFastScroll but slimmed down for Auxio and with a couple of enhancements.
 *
 * Attributions as per the Apache 2.0 license:
 * ORIGINAL AUTHOR: Hai Zhang [https://github.com/zhanghai]
 * PROJECT: Android Fast Scroll [https://github.com/zhanghai/AndroidFastScroll]
 * MODIFIER: OxygenCobalt [https://github.com/]
 *
 * !!! MODIFICATIONS !!!:
 * - Scroller will no longer show itself on startup or relayouts, which looked unpleasant
 * with multiple views
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
 *
 * @author Hai Zhang, OxygenCobalt
 */
class FastScrollRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : RecyclerView(context, attrs, defStyleAttr) {
    /** Callback to provide a string to be shown on the popup when an item is passed */
    var popupProvider: ((Int) -> String)? = null

    /**
     * A listener for when a drag event occurs. The value will be true if a drag has begun,
     * and false if a drag ended.
     */
    var onDragListener: ((Boolean) -> Unit)? = null

    private val minTouchTargetSize: Int = resources.getDimensionPixelSize(R.dimen.size_btn_small)
    private val touchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop

    // Views for the track, thumb, and popup. Note that the track view is mostly vestigial
    // and is only for bounds checking.
    private val trackView: View
    private val thumbView: View
    private val popupView: TextView

    // Touch values
    private val thumbWidth: Int
    private val thumbHeight: Int
    private var thumbOffset = 0
    private var downX = 0f
    private var downY = 0f
    private var lastY = 0f
    private var dragStartY = 0f
    private var dragStartThumbOffset = 0

    // State
    private var dragging = false
    private var showingScrollbar = false
    private var showingPopup = false

    private val childRect = Rect()

    private val hideScrollbarRunnable = Runnable {
        if (!dragging) {
            hideScrollbar()
        }
    }

    private val initialPadding = Rect(paddingLeft, paddingTop, paddingRight, paddingBottom)
    private val scrollerPadding = Rect(0, 0, 0, 0)

    init {
        val thumbDrawable = R.drawable.ui_scroll_thumb.resolveDrawable(context)

        trackView = View(context)
        thumbView = View(context).apply {
            alpha = 0f
            background = thumbDrawable
        }

        popupView = AppCompatTextView(context).apply {
            alpha = 0f
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )

            minimumWidth = resources.getDimensionPixelSize(
                R.dimen.popup_min_width
            )
            minimumHeight = resources.getDimensionPixelSize(
                R.dimen.size_btn_large
            )

            (layoutParams as FrameLayout.LayoutParams).apply {
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                marginEnd = resources.getDimensionPixelOffset(
                    R.dimen.spacing_small
                )
            }

            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_Auxio_HeadlineLarge)
            setTextColor(R.attr.colorOnSecondary.resolveAttr(context))

            background = FastScrollPopupDrawable(context)
            elevation = resources.getDimensionPixelOffset(R.dimen.elevation_normal).toFloat()
            ellipsize = TextUtils.TruncateAt.MIDDLE
            gravity = Gravity.CENTER
            includeFontPadding = false
            isSingleLine = true
        }

        thumbWidth = thumbDrawable.intrinsicWidth
        thumbHeight = thumbDrawable.intrinsicHeight

        check(thumbWidth >= 0)
        check(thumbHeight >= 0)

        overlay.add(trackView)
        overlay.add(thumbView)
        overlay.add(popupView)

        addItemDecoration(object : ItemDecoration() {
            override fun onDraw(
                canvas: Canvas,
                parent: RecyclerView,
                state: State
            ) {
                onPreDraw()
            }
        })

        // We use a listener instead of overriding onTouchEvent so that we don't conflict with
        // RecyclerView touch events.
        addOnItemTouchListener(object : SimpleOnItemTouchListener() {
            override fun onTouchEvent(
                recyclerView: RecyclerView,
                event: MotionEvent
            ) {
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

        trackView.layoutDirection = layoutDirection
        thumbView.layoutDirection = layoutDirection
        popupView.layoutDirection = layoutDirection

        val trackLeft = if (isRtl) {
            scrollerPadding.left
        } else {
            width - scrollerPadding.right - thumbWidth
        }

        trackView.layout(
            trackLeft, scrollerPadding.top, trackLeft + thumbWidth,
            height - scrollerPadding.bottom
        )

        val thumbLeft = if (isRtl) {
            scrollerPadding.left
        } else {
            width - scrollerPadding.right - thumbWidth
        }

        val thumbTop = scrollerPadding.top + thumbOffset

        thumbView.layout(thumbLeft, thumbTop, thumbLeft + thumbWidth, thumbTop + thumbHeight)

        val firstPos = firstAdapterPos
        val popupText = if (firstPos != NO_POSITION) {
            popupProvider?.invoke(firstPos) ?: ""
        } else {
            ""
        }

        popupView.isInvisible = popupText.isEmpty()

        if (popupText.isNotEmpty()) {
            val popupLayoutParams = popupView.layoutParams as FrameLayout.LayoutParams

            if (popupView.text != popupText) {
                popupView.text = popupText

                val widthMeasureSpec = ViewGroup.getChildMeasureSpec(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    scrollerPadding.left + scrollerPadding.right + thumbWidth +
                        popupLayoutParams.leftMargin + popupLayoutParams.rightMargin,
                    popupLayoutParams.width
                )

                val heightMeasureSpec = ViewGroup.getChildMeasureSpec(
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
                    scrollerPadding.top + scrollerPadding.bottom + popupLayoutParams.topMargin +
                        popupLayoutParams.bottomMargin,
                    popupLayoutParams.height
                )

                popupView.measure(widthMeasureSpec, heightMeasureSpec)
            }

            val popupWidth = popupView.measuredWidth
            val popupHeight = popupView.measuredHeight
            val popupLeft = if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                scrollerPadding.left + thumbWidth + popupLayoutParams.leftMargin
            } else {
                width - scrollerPadding.right - thumbWidth - popupLayoutParams.rightMargin - popupWidth
            }

            // We handle RTL separately, so it's okay if Gravity.RIGHT is used here
            @SuppressLint("RtlHardcoded")
            val popupAnchorY = when (popupLayoutParams.gravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                Gravity.CENTER_HORIZONTAL -> popupHeight / 2
                Gravity.RIGHT -> popupHeight
                else -> 0
            }

            val thumbAnchorY = when (popupLayoutParams.gravity and Gravity.VERTICAL_GRAVITY_MASK) {
                Gravity.CENTER_VERTICAL -> {
                    thumbView.paddingTop + (
                        thumbHeight - thumbView.paddingTop - thumbView.paddingBottom
                        ) / 2
                }
                Gravity.BOTTOM -> thumbHeight - thumbView.paddingBottom
                else -> thumbView.paddingTop
            }

            val popupTop = MathUtils.clamp(
                thumbTop + thumbAnchorY - popupAnchorY,
                scrollerPadding.top + popupLayoutParams.topMargin,
                height - scrollerPadding.bottom - popupLayoutParams.bottomMargin - popupHeight
            )

            popupView.layout(
                popupLeft, popupTop, popupLeft + popupWidth, popupTop + popupHeight
            )
        }
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
        val bars = insets.systemBarsCompat

        updatePadding(
            initialPadding.left, initialPadding.top, initialPadding.right,
            initialPadding.bottom + bars.bottom
        )

        scrollerPadding.bottom = bars.bottom

        return insets
    }

    private fun updateScrollbarState() {
        if (!canScroll() || childCount == 0) {
            return
        }

        // Combine the previous item dimensions with the current item top to find our scroll
        // position
        getDecoratedBoundsWithMargins(getChildAt(0), childRect)
        val scrollOffset = paddingTop + (firstAdapterPos * itemHeight) - childRect.top

        // Then calculate the thumb position, which is just:
        // [proportion of scroll position to scroll range] * [total thumb range]
        thumbOffset = (thumbOffsetRange.toLong() * scrollOffset / scrollOffsetRange).toInt()
    }

    private fun onItemTouch(event: MotionEvent): Boolean {
        val eventX = event.x
        val eventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = eventX
                downY = eventY

                val scrollX = trackView.scrollX
                val isInScrollbar =
                    eventX >= thumbView.left - scrollX && eventX < thumbView.right - scrollX

                if (trackView.alpha > 0 && isInScrollbar) {
                    dragStartY = eventY

                    if (isInViewTouchTarget(thumbView, eventX, eventY)) {
                        dragStartThumbOffset = thumbOffset
                    } else {
                        dragStartThumbOffset = (eventY - scrollerPadding.top - thumbHeight / 2f).toInt()
                        scrollToThumbOffset(dragStartThumbOffset)
                    }

                    setDragging(true)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (!dragging && isInViewTouchTarget(trackView, downX, downY) &&
                    abs(eventY - downY) > touchSlop
                ) {
                    if (isInViewTouchTarget(thumbView, downX, downY)) {
                        dragStartY = lastY
                        dragStartThumbOffset = thumbOffset
                    } else {
                        dragStartY = eventY
                        dragStartThumbOffset = (eventY - scrollerPadding.top - thumbHeight / 2f).toInt()
                        scrollToThumbOffset(dragStartThumbOffset)
                    }
                    setDragging(true)
                }

                if (dragging) {
                    val thumbOffset = dragStartThumbOffset + (eventY - dragStartY).toInt()
                    scrollToThumbOffset(thumbOffset)
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> setDragging(false)
        }

        lastY = eventY
        return dragging
    }

    private fun isInViewTouchTarget(view: View, x: Float, y: Float): Boolean {
        return isInTouchTarget(x, view.left - scrollX, view.right - scrollX, width) &&
            isInTouchTarget(y, view.top - scrollY, view.bottom - scrollY, height)
    }

    private fun isInTouchTarget(
        position: Float,
        viewStart: Int,
        viewEnd: Int,
        parentEnd: Int
    ): Boolean {
        val viewSize = viewEnd - viewStart

        if (viewSize >= minTouchTargetSize) {
            return position >= viewStart && position < viewEnd
        }

        var touchTargetStart = viewStart - (minTouchTargetSize - viewSize) / 2

        if (touchTargetStart < 0) {
            touchTargetStart = 0
        }

        var touchTargetEnd = touchTargetStart + minTouchTargetSize
        if (touchTargetEnd > parentEnd) {
            touchTargetEnd = parentEnd
            touchTargetStart = touchTargetEnd - minTouchTargetSize

            if (touchTargetStart < 0) {
                touchTargetStart = 0
            }
        }

        return position >= touchTargetStart && position < touchTargetEnd
    }

    private fun scrollToThumbOffset(thumbOffset: Int) {
        val clampedThumbOffset = MathUtils.clamp(thumbOffset, 0, thumbOffsetRange)

        val scrollOffset = (
            scrollOffsetRange.toLong() * clampedThumbOffset / thumbOffsetRange
            ).toInt() - paddingTop

        scrollTo(scrollOffset)
    }

    private fun scrollTo(offset: Int) {
        stopScroll()

        val trueOffset = offset - paddingTop
        val itemHeight = itemHeight

        val firstItemPosition = 0.coerceAtLeast(trueOffset / itemHeight)
        val firstItemTop = firstItemPosition * itemHeight - trueOffset

        scrollToPositionWithOffset(firstItemPosition, firstItemTop)
    }

    private fun scrollToPositionWithOffset(position: Int, offset: Int) {
        var targetPosition = position
        val trueOffset = offset - paddingTop

        when (val mgr = layoutManager) {
            is GridLayoutManager -> {
                targetPosition *= mgr.spanCount
                mgr.scrollToPositionWithOffset(targetPosition, trueOffset)
            }

            is LinearLayoutManager -> {
                mgr.scrollToPositionWithOffset(targetPosition, trueOffset)
            }
        }
    }

    private fun setDragging(isDragging: Boolean) {
        if (dragging == isDragging) {
            return
        }

        dragging = isDragging

        if (dragging) {
            parent.requestDisallowInterceptTouchEvent(true)
        }

        trackView.isPressed = dragging
        thumbView.isPressed = dragging

        if (dragging) {
            removeCallbacks(hideScrollbarRunnable)
            showScrollbar()
            showPopup()
        } else {
            postAutoHideScrollbar()
            hidePopup()
        }

        onDragListener?.invoke(isDragging)
    }

    // --- SCROLLBAR APPEARANCE ---

    private fun postAutoHideScrollbar() {
        removeCallbacks(hideScrollbarRunnable)
        postDelayed(hideScrollbarRunnable, AUTO_HIDE_SCROLLBAR_DELAY_MILLIS.toLong())
    }

    private fun showScrollbar() {
        if (showingScrollbar) {
            return
        }

        showingScrollbar = true
        animateView(thumbView, 1f)
    }

    private fun hideScrollbar() {
        if (!showingScrollbar) {
            return
        }

        showingScrollbar = false
        animateView(thumbView, 0f)
    }

    private fun showPopup() {
        if (showingPopup) {
            return
        }

        showingPopup = true
        animateView(popupView, 1f)
    }

    private fun hidePopup() {
        if (!showingPopup) {
            return
        }

        showingPopup = false
        animateView(popupView, 0f)
    }

    private fun animateView(view: View, alpha: Float) {
        view.animate()
            .alpha(alpha)
            .setDuration(ANIM_MILLIS)
            .start()
    }

    // --- LAYOUT STATE ---

    private val isRtl: Boolean
        get() = layoutDirection == LAYOUT_DIRECTION_RTL

    private val thumbOffsetRange: Int
        get() {
            return height - scrollerPadding.top - scrollerPadding.bottom - thumbHeight
        }

    private val scrollRange: Int
        get() {
            val itemCount = itemCount

            if (itemCount == 0) {
                return 0
            }

            val itemHeight = itemHeight

            return if (itemHeight != 0) {
                paddingTop + itemCount * itemHeight + paddingBottom
            } else {
                0
            }
        }

    private val scrollOffsetRange: Int
        get() = scrollRange - height

    private val firstAdapterPos: Int
        get() {
            if (childCount == 0) {
                return NO_POSITION
            }

            val child = getChildAt(0)

            return when (val mgr = layoutManager) {
                is GridLayoutManager -> mgr.getPosition(child) / mgr.spanCount
                is LinearLayoutManager -> mgr.getPosition(child)
                else -> 0
            }
        }

    private val itemHeight: Int
        get() {
            if (childCount == 0) {
                return 0
            }

            val itemView = getChildAt(0)
            getDecoratedBoundsWithMargins(itemView, childRect)
            return childRect.height()
        }

    private val itemCount: Int
        get() = when (val mgr = layoutManager) {
            is GridLayoutManager -> (mgr.itemCount - 1) / mgr.spanCount + 1
            is LinearLayoutManager -> mgr.itemCount
            else -> 0
        }

    companion object {
        private const val ANIM_MILLIS = 150L
        private const val AUTO_HIDE_SCROLLBAR_DELAY_MILLIS = 1500
    }
}
