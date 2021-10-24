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
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.math.MathUtils
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.resolveAttr
import org.oxycblt.auxio.util.resolveDrawable
import kotlin.math.abs

/**
 * A [RecyclerView] that enables better fast-scrolling. This is fundamentally a implementation of
 * Zhanghi's AndroidFastScroll but slimmed down for Auxio and with a couple of enhancements.
 *
 * Attributions as per the Apache 2.0 license:
 * ORIGINAL AUTHOR: Zhanghai [https://github.com/zhanghai]
 * PROJECT: Android Fast Scroll [https://github.com/zhanghai/AndroidFastScroll]
 * MODIFIER: OxygenCobalt [https://github.com/]
 *
 * !!! MODIFICATIONS !!!:
 * - Scroller will no longer show itself on startup, which looked unpleasent with multiple
 * views
 * - DefaultAnimationHelper and RecyclerViewHelper were merged into the class
 * - FastScroller overlay was merged into RecyclerView instance
 * - Removed FastScrollerBuilder
 * - Converted all code to kotlin
 * - Use modified Auxio resources instead of AFS resources
 * - Track view is now only used for touch bounds
 * - Redundant functions have been merged
 * - Variable names are no longer prefixed with m
 * - TODO: Added documentation
 * - TODO: Popup will center itself to the thumb when possible
 *
 * TODO: Debug this
 */
class FastScrollRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : RecyclerView(context, attrs, defStyleAttr) {
    private var popupProvider: ((Int) -> String)? = null

    private val minTouchTargetSize: Int = resources.getDimensionPixelSize(R.dimen.size_btn_small)
    private val touchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop

    private val trackView: View
    private val thumbView: View
    private val popupView: TextView

    private val thumbWidth: Int
    private val thumbHeight: Int
    private var thumbOffset = 0

    private var downX = 0f
    private var downY = 0f
    private var lastY = 0f
    private var dragStartY = 0f
    private var dragStartThumbOffset = 0

    private var dragging = false
    private var didRelayout = true
    private var showingScrollbar = false
    private var showingPopup = false

    private val childRect = Rect()

    private val hideScrollbarRunnable = Runnable {
        if (dragging) {
            return@Runnable
        }

        hideScrollbar()
    }

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

            val layoutParams = layoutParams as FrameLayout.LayoutParams
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
            layoutParams.marginEnd = resources.getDimensionPixelOffset(
                R.dimen.spacing_small
            )

            setLayoutParams(layoutParams)

            background = Md2PopupBackground(context)
            elevation = resources.getDimensionPixelOffset(R.dimen.elevation_normal).toFloat()
            ellipsize = TextUtils.TruncateAt.MIDDLE
            gravity = Gravity.CENTER
            includeFontPadding = false
            isSingleLine = true

            setTextColor(android.R.attr.textColorPrimaryInverse.resolveAttr(context))
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimensionPixelSize(
                    R.dimen.text_size_insane
                ).toFloat()
            )
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
            override fun onInterceptTouchEvent(
                recyclerView: RecyclerView,
                event: MotionEvent
            ): Boolean {
                return onItemTouch(event)
            }

            override fun onTouchEvent(
                recyclerView: RecyclerView,
                event: MotionEvent
            ) {
                onItemTouch(event)
            }
        })
    }

    fun setup(provider: (Int) -> String) {
        popupProvider = provider
    }

    // --- RECYCLERVIEW EVENT MANAGEMENT ---

    private fun onPreDraw() {
        updateScrollbarState()

        trackView.layoutDirection = layoutDirection
        thumbView.layoutDirection = layoutDirection
        popupView.layoutDirection = layoutDirection

        val trackLeft = if (isRtl) {
            paddingLeft
        } else {
            width - paddingRight - thumbWidth
        }

        trackView.layout(trackLeft, paddingTop, trackLeft + thumbWidth, height - paddingBottom)

        val thumbLeft = if (isRtl) {
            paddingLeft
        } else {
            width - paddingRight - thumbWidth
        }

        val thumbTop = paddingTop + thumbOffset

        thumbView.layout(thumbLeft, thumbTop, thumbLeft + thumbWidth, thumbTop + thumbHeight)

        val firstPos = firstAdapterPos
        val popupText = if (firstPos != NO_POSITION) {
            popupProvider?.invoke(firstPos) ?: ""
        } else {
            ""
        }

        val hasPopup = !TextUtils.isEmpty(popupText)
        popupView.visibility = if (hasPopup) View.VISIBLE else View.INVISIBLE

        if (hasPopup) {
            val popupLayoutParams = popupView.layoutParams as FrameLayout.LayoutParams

            if (popupView.text != popupText) {
                popupView.text = popupText

                val widthMeasureSpec = ViewGroup.getChildMeasureSpec(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    paddingLeft + paddingRight + thumbWidth + popupLayoutParams.leftMargin +
                        popupLayoutParams.rightMargin,
                    popupLayoutParams.width
                )

                val heightMeasureSpec = ViewGroup.getChildMeasureSpec(
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
                    paddingTop + paddingBottom + popupLayoutParams.topMargin +
                        popupLayoutParams.bottomMargin,
                    popupLayoutParams.height
                )

                popupView.measure(widthMeasureSpec, heightMeasureSpec)
            }

            val popupWidth = popupView.measuredWidth
            val popupHeight = popupView.measuredHeight
            val popupLeft = if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                paddingLeft + thumbWidth + popupLayoutParams.leftMargin
            } else {
                width - paddingRight - thumbWidth - popupLayoutParams.rightMargin - popupWidth
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
                paddingTop + popupLayoutParams.topMargin,
                height - paddingBottom - popupLayoutParams.bottomMargin - popupHeight
            )

            popupView.layout(
                popupLeft, popupTop, popupLeft + popupWidth, popupTop + popupHeight
            )
        }
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)

        updateScrollbarState()

        if (didRelayout) {
            didRelayout = false
            return
        }

        showScrollbar()
        postAutoHideScrollbar()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        didRelayout = changed
    }

    private fun updateScrollbarState() {
        // Getting a pixel-perfect scroll position from a recyclerview is a bit of an involved
        // process. It's kind of expected given how RecyclerView well...recycles views, but it's
        // still very annoying how many hoops one has to jump through.

        // First, we need to get the first visible child. We will use this to extrapolate a rough
        // scroll range/position for the view.
        // Doing this does mean that the fast scroller will break if you have a header view that's
        // a different height, but Auxio's home UI doesn't have something like that so we're okay.
        val firstChild = getChildAt(0)

        val itemPos = firstAdapterPos
        val itemCount = itemCount

        // Now get the bounds of the first child. These are the dimensions we use to extrapolate
        // information for the whole recyclerview.
        getDecoratedBoundsWithMargins(firstChild, childRect)
        val itemHeight = childRect.height()
        val itemTop = childRect.top

        // This is where things get messy. We have to take everything we just calculated and
        // do some arithmetic to get it into a working thumb position.

        // The total scroll range based on the initial item
        val scrollRange = paddingTop + (itemCount * itemHeight) + paddingBottom

        // The scroll range where the items aren't visible
        val scrollOffsetRange = scrollRange - height

        // The scroll offset, or basically the y of the current item + the height of all
        // the previous items
        val scrollOffset = paddingTop + (itemPos * itemHeight) - itemTop

        // The range of pixels where the thumb is not present
        val thumbOffsetRange = height - paddingTop - paddingBottom - thumbHeight

        // Finally, we can calculate the thumb position, which is just:
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
                val isInScrollbar = (
                    eventX >= thumbView.left - scrollX && eventX < thumbView.right - scrollX
                    )

                if (trackView.alpha > 0 && isInScrollbar) {
                    dragStartY = eventY

                    if (isInViewTouchTarget(thumbView, eventX, eventY)) {
                        dragStartThumbOffset = thumbOffset
                    } else {
                        dragStartThumbOffset = (eventY - paddingTop - thumbHeight / 2f).toInt()
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
                        dragStartThumbOffset = (eventY - paddingTop - thumbHeight / 2f).toInt()
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
        val scrollX = scrollX
        val scrollY = scrollY
        return (
            isInTouchTarget(
                x, view.left - scrollX, view.right - scrollX, 0,
                width
            ) &&
                isInTouchTarget(
                    y, view.top - scrollY, view.bottom - scrollY, 0,
                    height
                )
            )
    }

    private fun isInTouchTarget(
        position: Float,
        viewStart: Int,
        viewEnd: Int,
        parentStart: Int,
        parentEnd: Int
    ): Boolean {
        val viewSize = viewEnd - viewStart

        if (viewSize >= minTouchTargetSize) {
            return position >= viewStart && position < viewEnd
        }

        var touchTargetStart = viewStart - (minTouchTargetSize - viewSize) / 2
        if (touchTargetStart < parentStart) {
            touchTargetStart = parentStart
        }

        var touchTargetEnd = touchTargetStart + minTouchTargetSize
        if (touchTargetEnd > parentEnd) {
            touchTargetEnd = parentEnd
            touchTargetStart = touchTargetEnd - minTouchTargetSize
            if (touchTargetStart < parentStart) {
                touchTargetStart = parentStart
            }
        }

        return position >= touchTargetStart && position < touchTargetEnd
    }

    private fun scrollToThumbOffset(thumbOffset: Int) {
        var newThumbOffset = thumbOffset
        newThumbOffset = MathUtils.clamp(newThumbOffset, 0, thumbOffsetRange)
        var scrollOffset = (scrollOffsetRange.toLong() * newThumbOffset / thumbOffsetRange).toInt()
        scrollOffset -= paddingTop

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

    private fun setDragging(dragging: Boolean) {
        if (this.dragging == dragging) {
            return
        }

        this.dragging = dragging
        if (this.dragging) {
            parent.requestDisallowInterceptTouchEvent(true)
        }

        trackView.isPressed = this.dragging
        thumbView.isPressed = this.dragging

        if (this.dragging) {
            removeCallbacks(hideScrollbarRunnable)
            showScrollbar()
            showPopup()
        } else {
            postAutoHideScrollbar()
            hidePopup()
        }
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

    private val scrollOffsetRange: Int
        get() = scrollRange - height

    private val thumbOffsetRange: Int
        get() {
            return height - paddingTop - paddingBottom - thumbHeight
        }

    private val itemCount: Int
        get() = when (val mgr = layoutManager) {
            is GridLayoutManager -> (mgr.itemCount - 1) / mgr.spanCount + 1
            is LinearLayoutManager -> mgr.itemCount
            else -> 0
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

    companion object {
        private const val ANIM_MILLIS = 150L
        private const val AUTO_HIDE_SCROLLBAR_DELAY_MILLIS = 1500
    }
}
