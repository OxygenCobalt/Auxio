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

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Layout
import android.util.AttributeSet
import android.util.TypedValue
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
import androidx.dynamicanimation.animation.FloatValueHolder
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R as MR
import com.google.android.material.motion.MotionUtils
import com.google.android.material.textview.MaterialTextView
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import org.oxycblt.auxio.R
import org.oxycblt.auxio.ui.Effect
import org.oxycblt.auxio.ui.ExpressiveShapes
import org.oxycblt.auxio.ui.Spatial
import org.oxycblt.auxio.util.getAttrColorCompat
import org.oxycblt.auxio.util.getAttrResourceId
import org.oxycblt.auxio.util.getDimenPixels
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.isRtl
import org.oxycblt.auxio.util.isUnder
import org.oxycblt.auxio.util.scale
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
 * - M3 (Expressive) Redesign
 * - Dynamic popups
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
    private var thumbAnimator: SpringAnimation? = null

    @SuppressLint("InflateParams")
    private val thumbView =
        context.inflater.inflate(R.layout.view_scroll_thumb, null).apply {
            translationX = thumbWidth.toFloat()
        }
    private val thumbPadding = Rect(0, 0, 0, 0)
    private var thumbOffset = 0

    private val thumbTranslateInSpring = Spatial.DEFAULT
    private val thumbTranslateOutSpring = Spatial.FAST

    private val popupScaleSpring = Spatial.DEFAULT
    private val popupAlphaSpring = Effect.DEFAULT

    private var showingThumb = false
    private val hideThumbRunnable = Runnable {
        if (!dragging) {
            hideThumb()
        }
    }
    private val popupContainerSize = context.getDimenPixels(R.dimen.size_fast_scroll_popup)
    private val popupTextAutoScaleMinSize =
        context.getDimenPixels(R.dimen.text_size_fast_scroll_popup_min)
    private val popupTextAutoScaleMaxSize =
        context.getDimenPixels(R.dimen.text_size_fast_scroll_popup_max)
    private val popupTextAutoScaleStepGranularity =
        context.getDimenPixels(R.dimen.text_size_fast_scroll_popup_step)
    private val popupTextAutoScaleMinSizePx = popupTextAutoScaleMinSize.coerceAtLeast(1)
    private val popupTextAutoScaleMaxSizePx =
        popupTextAutoScaleMaxSize.coerceAtLeast(popupTextAutoScaleMinSizePx)
    private val popupTextAutoScaleStepGranularityPx =
        popupTextAutoScaleStepGranularity.coerceAtLeast(1)
    private var popupTextAutoScaleCeilingPx = popupTextAutoScaleMaxSizePx
    private var popupTextAutoScaleKey: PopupTextAutoScaleKey? = null

    private val popupTextView =
        createPopupTextView(context).apply {
            setTextColor(context.getAttrColorCompat(MR.attr.colorOnSecondary))
            maxLines = POPUP_TEXT_SINGLE_LINE_COUNT
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                this,
                popupTextAutoScaleMinSizePx,
                popupTextAutoScaleCeilingPx,
                popupTextAutoScaleStepGranularityPx,
                TypedValue.COMPLEX_UNIT_PX,
            )
        }
    private val popupTextMeasurementView = createPopupTextView(context)
    private val popupTextWidestDigit: Char = run {
        val paint = popupTextMeasurementView.paint
        ('0'..'9').maxByOrNull { paint.measureText(it.toString()) } ?: '0'
    }
    private val popupBackgroundDrawable = SoftBurstPopupDrawable(context)
    private val popupView =
        FrameLayout(context).apply {
            elevation = context.getDimenPixels(MR.dimen.m3_sys_elevation_level1).toFloat()
            background = popupBackgroundDrawable
            minimumWidth = popupContainerSize
            minimumHeight = popupContainerSize

            val paddingHorizontal =
                context.getDimenPixels(R.dimen.padding_fast_scroll_popup_horizontal)
            val paddingVertical = context.getDimenPixels(R.dimen.padding_fast_scroll_popup_vertical)
            updatePaddingRelative(
                start = paddingHorizontal,
                top = paddingVertical,
                end = paddingHorizontal,
                bottom = paddingVertical,
            )
            addView(
                popupTextView,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER,
                ),
            )
            layoutParams =
                FrameLayout.LayoutParams(popupContainerSize, popupContainerSize).apply {
                    marginEnd = context.getDimenPixels(R.dimen.size_touchable_small)
                    gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                }
            scale = 0.5f
            alpha = 0.0f
        }
    private val popupTextStaggerDelayMillis =
        MotionUtils.resolveThemeDuration(context, MR.attr.motionDurationShort2, 100).toLong()
    private val popupSpatialSpring =
        MotionUtils.resolveThemeSpringForce(
            context,
            MR.attr.motionSpringFastSpatial,
            MR.style.Motion_Material3_Spring_Standard_Fast_Spatial,
        )
    private val popupEffectsSpring =
        MotionUtils.resolveThemeSpringForce(
            context,
            MR.attr.motionSpringDefaultEffects,
            MR.style.Motion_Material3_Spring_Standard_Default_Effects,
        )
    private var popupShapeScaleAnimation: SpringAnimation? = null
    private var popupShapeAlphaAnimation: SpringAnimation? = null
    private var popupTextScaleAnimation: SpringAnimation? = null
    private var popupTextAlphaAnimation: SpringAnimation? = null
    private val popupTextRevealRunnable = Runnable {
        if (showingPopup) {
            popupShapeScaleAnimation?.cancel()
            popupShapeScaleAnimation = popupScaleSpring.scale(popupView, 0f)
        }
    }
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
            }
        )

        // We use a listener instead of overriding onTouchEvent so that we don't conflict with
        // RecyclerView touch events.
        addOnItemTouchListener(
            object : SimpleOnItemTouchListener() {
                override fun onTouchEvent(recyclerView: RecyclerView, event: MotionEvent) {
                    onItemTouch(event)
                }

                override fun onInterceptTouchEvent(
                    recyclerView: RecyclerView,
                    event: MotionEvent,
                ): Boolean {
                    return onItemTouch(event)
                }
            }
        )
    }

    // --- RECYCLERVIEW EVENT MANAGEMENT ---

    private fun onPreDraw() {
        updateThumbState()

        val thumbRange = thumbOffsetRange
        val scrollFraction =
            if (thumbRange > 0) {
                (thumbOffset / thumbRange.toFloat()).coerceIn(0f, 1f)
            } else {
                0f
            }
        popupBackgroundDrawable.scrollRotationDegrees = scrollFraction * 360f

        thumbView.layoutDirection = layoutDirection
        thumbView.measure(
            MeasureSpec.makeMeasureSpec(thumbWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(thumbHeight, MeasureSpec.EXACTLY),
        )
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

        val provider = popupProvider
        val hasPopupProvider = firstAdapterPos != NO_POSITION && provider != null
        val popupData =
            if (hasPopupProvider) {
                provider.getPopupData(firstAdapterPos)
            } else {
                null
            }
        val popupText: String
        if (hasPopupProvider) {
            popupView.isInvisible = false
            // Get the popup text. If there is none, we default to "?".
            popupText = popupData?.text ?: "?"
        } else {
            // No valid position or provider, do not show the popup.
            popupView.isInvisible = true
            popupText = ""
        }
        val popupLayoutParams = popupView.layoutParams as FrameLayout.LayoutParams

        val popupMaxWidth =
            width -
                thumbPadding.left -
                thumbPadding.right -
                thumbWidth -
                popupLayoutParams.leftMargin -
                popupLayoutParams.rightMargin
        val popupMaxHeight =
            height -
                thumbPadding.top -
                thumbPadding.bottom -
                popupLayoutParams.topMargin -
                popupLayoutParams.bottomMargin

        val maxPopupSize = min(popupMaxWidth, popupMaxHeight).coerceAtLeast(0)
        val popupSize =
            if (maxPopupSize > 0) {
                popupContainerSize.coerceAtMost(maxPopupSize)
            } else {
                0
            }
        updatePopupTextAutoScaleConfigFor(popupText, popupSize)
        if (popupTextView.text != popupText) {
            popupTextView.text = popupText
            if (showingPopup) {
                doPopupVibration()
            }
        }
        popupView.measure(
            MeasureSpec.makeMeasureSpec(popupSize, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(popupSize, MeasureSpec.EXACTLY),
        )

        val popupWidth = popupSize
        val popupHeight = popupSize
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
                    height - thumbPadding.bottom - popupLayoutParams.bottomMargin - popupHeight
                )

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
                if (
                    !dragging &&
                        thumbView.isUnder(downX, thumbView.top.toFloat(), minTouchTargetSize) &&
                        abs(eventY - downY) > touchSlop
                ) {
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
        thumbAnimator = thumbTranslateInSpring.translateX(thumbView, 0f)
    }

    private fun hideThumb() {
        if (!showingThumb) {
            return
        }

        showingThumb = false
        thumbAnimator?.cancel()
        thumbAnimator = thumbTranslateOutSpring.translateX(thumbView, thumbWidth.toFloat())
    }

    private fun showPopup() {
        if (!fastScrollingEnabled || !fastScrollingPossible) {
            return
        }
        if (showingPopup) {
            return
        }

        showingPopup = true
        resetPopupTextAutoScaleConfig()
        popupShapeScaleAnimation?.cancel()
        popupShapeAlphaAnimation?.cancel()
        popupShapeScaleAnimation = popupScaleSpring.scale(popupView, 1f)
        popupShapeAlphaAnimation = popupAlphaSpring.alpha(popupView, 1f)
    }

    private fun hidePopup() {
        if (!showingPopup) {
            return
        }

        showingPopup = false
        popupShapeScaleAnimation?.cancel()
        popupShapeAlphaAnimation?.cancel()
        popupShapeScaleAnimation = popupScaleSpring.scale(popupView, 0.5f)
        popupShapeAlphaAnimation = popupAlphaSpring.alpha(popupView, 0f)
    }

    private fun createPopupTextView(context: Context): MaterialTextView =
        MaterialTextView(context).apply {
            TextViewCompat.setTextAppearance(
                this,
                context.getAttrResourceId(MR.attr.textAppearanceHeadlineMediumEmphasized),
            )
            ellipsize = null
            gravity = Gravity.CENTER
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            includeFontPadding = false
            setHorizontallyScrolling(false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                breakStrategy = Layout.BREAK_STRATEGY_SIMPLE
                hyphenationFrequency = Layout.HYPHENATION_FREQUENCY_NONE
            }
        }

    private fun resetPopupTextAutoScaleConfig() {
        popupTextAutoScaleKey = null
        applyPopupTextAutoScaleConfig(popupTextAutoScaleMaxSizePx)
    }

    private fun updatePopupTextAutoScaleConfigFor(text: String, popupSize: Int) {
        if (popupSize <= 0) {
            resetPopupTextAutoScaleConfig()
            return
        }

        val exemplar = normalizeToExemplar(text)
        val key = PopupTextAutoScaleKey(exemplar = exemplar, popupSize = popupSize)
        if (popupTextAutoScaleKey == key) {
            return
        }
        popupTextAutoScaleKey = key

        if (exemplar == null) {
            applyPopupTextAutoScaleConfig(popupTextAutoScaleMaxSizePx)
            return
        }

        val textWidth =
            (popupSize - popupView.paddingLeft - popupView.paddingRight).coerceAtLeast(1)
        val textHeight =
            (popupSize - popupView.paddingTop - popupView.paddingBottom).coerceAtLeast(1)
        val ceilingPx = measurePopupTextCeiling(exemplar, textWidth, textHeight)
        applyPopupTextAutoScaleConfig(ceilingPx)
    }

    private fun normalizeToExemplar(text: String): String? {
        if (text.length <= 1) return null
        return buildString(text.length) {
            for (c in text) {
                append(if (c.isDigit()) popupTextWidestDigit else c)
            }
        }
    }

    private fun measurePopupTextCeiling(
        exemplar: String,
        availableWidth: Int,
        availableHeight: Int,
    ): Int {
        val paint = popupTextMeasurementView.paint
        val savedSize = paint.textSize
        var bestSize = popupTextAutoScaleMinSizePx
        for (sizePx in
            popupTextAutoScaleMaxSizePx downTo
                popupTextAutoScaleMinSizePx step
                popupTextAutoScaleStepGranularityPx) {
            paint.textSize = sizePx.toFloat()
            val w = paint.measureText(exemplar)
            val metrics = paint.fontMetrics
            val h = metrics.descent - metrics.ascent
            if (w <= availableWidth && h <= availableHeight) {
                bestSize = sizePx
                break
            }
        }
        paint.textSize = savedSize
        return bestSize
    }

    private fun applyPopupTextAutoScaleConfig(maxSizePx: Int) {
        popupTextAutoScaleCeilingPx =
            maxSizePx.coerceIn(popupTextAutoScaleMinSizePx, popupTextAutoScaleMaxSizePx)
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            popupTextView,
            popupTextAutoScaleMinSizePx,
            popupTextAutoScaleCeilingPx,
            popupTextAutoScaleStepGranularityPx,
            TypedValue.COMPLEX_UNIT_PX,
        )
    }

    private fun springTo(
        startValue: Float,
        finalValue: Float,
        springTemplate: SpringForce,
        minimumVisibleChange: Float,
        dampingOverride: Float? = null,
        update: (Float) -> Unit,
    ): SpringAnimation {
        val animation =
            SpringAnimation(FloatValueHolder(startValue)).apply {
                spring =
                    SpringForce().apply {
                        dampingRatio = dampingOverride ?: springTemplate.dampingRatio
                        stiffness = springTemplate.stiffness
                        finalPosition = finalValue
                    }
                setStartValue(startValue)
                setMinimumVisibleChange(minimumVisibleChange)
                addUpdateListener { _, value, _ -> update(value) }
                addEndListener { _, canceled, value, _ ->
                    update(if (canceled) value else finalValue)
                }
            }
        animation.animateToFinalPosition(finalValue)
        return animation
    }

    private fun doPopupVibration() {
        performHapticFeedback(
            if (Build.VERSION.SDK_INT >= 27) {
                HapticFeedbackConstants.TEXT_HANDLE_MOVE
            } else {
                HapticFeedbackConstants.KEYBOARD_TAP
            }
        )
    }

    // --- LAYOUT STATE ---

    private val thumbOffsetRange: Int
        get() {
            return height - thumbPadding.top - thumbPadding.bottom - thumbHeight
        }

    /** An interface to provide text to use in the popup when fast-scrolling. */
    interface PopupProvider {
        /**
         * Get popup metadata at the specified position.
         *
         * @param pos The position in the list.
         * @return A [PopupData] to use in the popup. Null if there is no applicable text for the
         *   popup at [pos].
         */
        fun getPopupData(pos: Int): PopupData?

        data class PopupData(val text: String)
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
        const val POPUP_SHAPE_HIDDEN_SCALE = 0.62f
        const val POPUP_TEXT_HIDDEN_SCALE = 0.78f
        const val POPUP_TEXT_SINGLE_LINE_COUNT = 1
    }

    private data class PopupTextAutoScaleKey(val exemplar: String?, val popupSize: Int)
}

private class SoftBurstPopupDrawable(context: Context) : Drawable() {
    private val baseRotationDegrees = 14f
    private val pathPosition = FloatArray(2)
    private val pathMatrix = Matrix()
    private val pathBounds = RectF()
    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color =
                context
                    .getAttrColorCompat(com.google.android.material.R.attr.colorSecondary)
                    .defaultColor
            style = Paint.Style.FILL
        }

    private var fittedPath = Path()
    private var shapeAlpha = 255
    var revealScale = 1f
        set(value) {
            val clampedValue = value.coerceAtLeast(0f)
            if (field == clampedValue) {
                return
            }

            field = clampedValue
            invalidateSelf()
        }

    var revealAlpha = 1f
        set(value) {
            val clampedValue = value.coerceIn(0f, 1f)
            if (field == clampedValue) {
                return
            }

            field = clampedValue
            updatePaintAlpha()
        }

    var scrollRotationDegrees = 0f
        set(value) {
            if (field == value) {
                return
            }

            field = value
            invalidateSelf()
        }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        rebuildPath()
    }

    private fun rebuildPath() {
        val drawableBounds = bounds
        if (drawableBounds.isEmpty) {
            fittedPath = Path()
            return
        }

        val size = min(drawableBounds.width(), drawableBounds.height()).toFloat()
        val left = drawableBounds.exactCenterX() - size / 2f
        val top = drawableBounds.exactCenterY() - size / 2f
        pathBounds.set(left, top, left + size, top + size)

        fittedPath = ExpressiveShapes.getSoftBurst(pathBounds)
        val maxRadius = computeMaxRadius(fittedPath, pathBounds.centerX(), pathBounds.centerY())
        if (maxRadius > 0f) {
            val targetRadius = size / 2f
            val uniformScale = targetRadius / maxRadius
            pathMatrix.reset()
            pathMatrix.setScale(
                uniformScale,
                uniformScale,
                pathBounds.centerX(),
                pathBounds.centerY(),
            )
            fittedPath.transform(pathMatrix)
        }
    }

    override fun draw(canvas: Canvas) {
        if (fittedPath.isEmpty) {
            return
        }

        val checkpoint = canvas.save()
        canvas.scale(revealScale, revealScale, pathBounds.centerX(), pathBounds.centerY())
        canvas.rotate(
            baseRotationDegrees + scrollRotationDegrees,
            pathBounds.centerX(),
            pathBounds.centerY(),
        )
        canvas.drawPath(fittedPath, paint)
        canvas.restoreToCount(checkpoint)
    }

    override fun setAlpha(alpha: Int) {
        shapeAlpha = alpha.coerceIn(0, 255)
        updatePaintAlpha()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    private fun computeMaxRadius(source: Path, centerX: Float, centerY: Float): Float {
        val measure = PathMeasure(source, false)
        var maxRadius = 0f

        do {
            val length = measure.length
            if (length <= 0f) {
                continue
            }

            val sampleCount = max((length / 1f).roundToInt(), 1)
            for (sample in 0..sampleCount) {
                val distance = length * sample / sampleCount
                if (measure.getPosTan(distance, pathPosition, null)) {
                    maxRadius =
                        max(maxRadius, hypot(pathPosition[0] - centerX, pathPosition[1] - centerY))
                }
            }
        } while (measure.nextContour())

        return maxRadius
    }

    private fun updatePaintAlpha() {
        paint.alpha = (shapeAlpha * revealAlpha).roundToInt().coerceIn(0, 255)
        invalidateSelf()
    }
}
