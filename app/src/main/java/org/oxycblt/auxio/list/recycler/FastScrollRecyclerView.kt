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
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.core.view.isEmpty
import androidx.core.view.isInvisible
import androidx.core.view.updatePaddingRelative
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt
import org.oxycblt.auxio.R
import org.oxycblt.auxio.ui.MaterialFadingSlider
import org.oxycblt.auxio.ui.MaterialSlider
import org.oxycblt.auxio.util.getAttrColorCompat
import org.oxycblt.auxio.util.getDimenPixels
import org.oxycblt.auxio.util.getDrawableCompat
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
 * - New scroll position backend
 *
 * @author Hai Zhang, Alexander Capehart (OxygenCobalt)
 */
class FastScrollRecyclerView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    AuxioRecyclerView(context, attrs, defStyleAttr) {
    // Thumb
    private val thumbWidth = context.getDimenPixels(R.dimen.spacing_mid_medium)
    private val thumbHeight = context.getDimenPixels(R.dimen.size_touchable_medium)
    private val thumbSlider = MaterialSlider.small(context, thumbWidth)
    private var thumbAnimator: Animator? = null

    @SuppressLint("InflateParams")
    private val thumbView =
        context.inflater.inflate(R.layout.view_scroll_thumb, null).apply {
            thumbSlider.jumpOut(this)
        }
    private val thumbPadding = Rect(0, 0, 0, 0)
    private var thumbOffset = 0

    private var showingThumb = false
    private val hideThumbRunnable = Runnable {
        if (!dragging) {
            hideThumb()
        }
    }

    private val popupView =
        MaterialTextView(context).apply {
            minimumWidth = context.getDimenPixels(R.dimen.size_touchable_large)
            minimumHeight = context.getDimenPixels(R.dimen.size_touchable_small)

            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_Auxio_HeadlineMedium)
            setTextColor(
                context.getAttrColorCompat(com.google.android.material.R.attr.colorOnSecondary))
            ellipsize = TextUtils.TruncateAt.MIDDLE
            gravity = Gravity.CENTER
            includeFontPadding = false

            elevation =
                context
                    .getDimenPixels(com.google.android.material.R.dimen.m3_sys_elevation_level1)
                    .toFloat()
            background = context.getDrawableCompat(R.drawable.ui_popup)
            val paddingStart = context.getDimenPixels(R.dimen.spacing_medium)
            val paddingEnd = paddingStart + context.getDimenPixels(R.dimen.spacing_tiny) / 2
            updatePaddingRelative(start = paddingStart, end = paddingEnd)
            layoutParams =
                FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    .apply {
                        marginEnd = context.getDimenPixels(R.dimen.size_touchable_small)
                        gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                    }
        }
    private val popupSlider =
        MaterialFadingSlider(MaterialSlider.large(context, popupView.minimumWidth / 2)).apply {
            jumpOut(popupView)
        }
    private var popupAnimator: Animator? = null
    private var showingPopup = false

    // Touch
    private val minTouchTargetSize = context.getDimenPixels(R.dimen.size_touchable_small)
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    private var downX = 0f
    private var downY = 0f
    private var lastY = 0f
    private var dragStartY = 0f
    private var dragStartThumbOffset = 0

    private var fastScrollingPossible = true

    var fastScrollingEnabled = true
        set(value) {
            if (field == value) {
                return
            }

            field = value
            if (!value) {
                removeCallbacks(hideThumbRunnable)
                hideThumb()
                hidePopup()
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
                showPopup()
            } else {
                hidePopup()
                postAutoHideScrollbar()
            }

            listener?.onFastScrollingChanged(field)
        }

    var popupProvider: PopupProvider? = null
    var listener: Listener? = null

    init {
        overlay.add(thumbView)
        overlay.add(popupView)

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
        updateThumbState()

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

        popupView.layoutDirection = layoutDirection
        val child = getChildAt(0)
        val firstAdapterPos =
            if (child != null) {
                layoutManager?.getPosition(child) ?: NO_POSITION
            } else {
                NO_POSITION
            }

        val popupText: String
        val provider = popupProvider
        if (firstAdapterPos != NO_POSITION && provider != null) {
            popupView.isInvisible = false
            // Get the popup text. If there is none, we default to "?".
            popupText = provider.getPopup(firstAdapterPos) ?: "?"
        } else {
            // No valid position or provider, do not show the popup.
            popupView.isInvisible = false
            popupText = ""
        }
        val popupLayoutParams = popupView.layoutParams as FrameLayout.LayoutParams

        if (popupView.text != popupText) {
            popupView.text = popupText

            val widthMeasureSpec =
                ViewGroup.getChildMeasureSpec(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    thumbPadding.left +
                        thumbPadding.right +
                        thumbWidth +
                        popupLayoutParams.leftMargin +
                        popupLayoutParams.rightMargin,
                    popupLayoutParams.width)

            val heightMeasureSpec =
                ViewGroup.getChildMeasureSpec(
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
                    thumbPadding.top +
                        thumbPadding.bottom +
                        popupLayoutParams.topMargin +
                        popupLayoutParams.bottomMargin,
                    popupLayoutParams.height)

            popupView.measure(widthMeasureSpec, heightMeasureSpec)
            if (showingPopup) {
                doPopupVibration()
            }
        }

        val popupWidth = popupView.measuredWidth
        val popupHeight = popupView.measuredHeight
        val popupLeft =
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                thumbPadding.left + thumbWidth + popupLayoutParams.leftMargin
            } else {
                width - thumbPadding.right - thumbWidth - popupLayoutParams.rightMargin - popupWidth
            }

        val popupAnchorY = popupHeight / 2
        val thumbAnchorY = thumbView.height / 2

        val popupTop =
            (thumbTop + thumbAnchorY - popupAnchorY)
                .coerceAtLeast(thumbPadding.top + popupLayoutParams.topMargin)
                .coerceAtMost(
                    height - thumbPadding.bottom - popupLayoutParams.bottomMargin - popupHeight)

        popupView.layout(popupLeft, popupTop, popupLeft + popupWidth, popupTop + popupHeight)
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)

        updateThumbState()

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

    private fun updateThumbState() {
        // Then calculate the thumb position, which is just:
        // [proportion of scroll position to scroll range] * [total thumb range]
        // This is somewhat adapted from the androidx RecyclerView FastScroller implementation.
        val offsetY = computeVerticalScrollOffset()
        if (computeVerticalScrollRange() < height || isEmpty()) {
            fastScrollingPossible = false
            hideThumb()
            hidePopup()
            return
        }
        val extentY = computeVerticalScrollExtent()
        val fraction = (offsetY).toFloat() / (computeVerticalScrollRange() - extentY)
        thumbOffset = (thumbOffsetRange * fraction).toInt()
    }

    private fun onItemTouch(event: MotionEvent): Boolean {
        if (!fastScrollingEnabled || !fastScrollingPossible) {
            dragging = false
            return false
        }
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
        if (!fastScrollingEnabled || !fastScrollingPossible) {
            return
        }
        if (showingThumb) {
            return
        }

        showingThumb = true
        thumbAnimator?.cancel()
        thumbAnimator = thumbSlider.slideIn(thumbView).also { it.start() }
    }

    private fun hideThumb() {
        if (!showingThumb) {
            return
        }

        showingThumb = false
        thumbAnimator?.cancel()
        thumbAnimator = thumbSlider.slideOut(thumbView).also { it.start() }
    }

    private fun showPopup() {
        if (!fastScrollingEnabled || !fastScrollingPossible) {
            return
        }
        if (showingPopup) {
            return
        }

        showingPopup = true
        popupAnimator?.cancel()
        popupAnimator = popupSlider.slideIn(popupView).also { it.start() }
    }

    private fun hidePopup() {
        if (!showingPopup) {
            return
        }

        showingPopup = false
        popupAnimator?.cancel()
        popupAnimator = popupSlider.slideOut(popupView).also { it.start() }
    }

    private fun doPopupVibration() {
        performHapticFeedback(
            if (Build.VERSION.SDK_INT >= 27) {
                HapticFeedbackConstants.TEXT_HANDLE_MOVE
            } else {
                HapticFeedbackConstants.KEYBOARD_TAP
            })
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
