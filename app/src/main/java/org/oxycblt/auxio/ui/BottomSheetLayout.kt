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
 
package org.oxycblt.auxio.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import androidx.core.view.isInvisible
import androidx.customview.widget.ViewDragHelper
import com.google.android.material.shape.MaterialShapeDrawable
import java.lang.reflect.Field
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.disableDropShadowCompat
import org.oxycblt.auxio.util.getAttrColorSafe
import org.oxycblt.auxio.util.getDimenSafe
import org.oxycblt.auxio.util.getDimenSizeSafe
import org.oxycblt.auxio.util.isUnder
import org.oxycblt.auxio.util.lazyReflectedField
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.pxOfDp
import org.oxycblt.auxio.util.replaceSystemBarInsetsCompat
import org.oxycblt.auxio.util.stateList
import org.oxycblt.auxio.util.systemBarInsetsCompat

/**
 * A layout that *properly* handles bottom sheet functionality.
 *
 * BottomSheetBehavior has a multitude of shortcomings based that make it a non-starter for Auxio,
 * such as:
 * - God-awful edge-to-edge support
 * - Does not allow other content to adapt
 * - Extreme jank
 * - Terrible APIs that you have to use just to make the UX tolerable
 * - Inexplicable layout and measuring inconsistencies
 * - Reliance on CoordinatorLayout, which is just a terrible component in general and everyone
 * responsible for creating it should be publicly shamed
 *
 * So, I decided to make my own implementation. With blackjack, and referential humor.
 *
 * The actual internals of this view are based off of a blend of Hai Zhang's PersistentBarLayout and
 * Umano's SlidingUpPanelLayout, albeit heavily minified to remove extraneous use cases and updated
 * to support the latest SDK level and androidx tools.
 *
 * What is hilarious is that Google now hates CoordinatorLayout and it's behavior implementations as
 * much as I do. Just look at all the new boring layout implementations they are introducing like
 * SlidingPaneLayout. It's almost like re-inventing the layout process but buggier and without
 * access to other children in the ViewGroup was a bad idea. Whoa.
 *
 * **Note:** If you want to adapt this layout into your own app. Good luck. This layout has been
 * reduced to Auxio's use case in particular and is really hard to understand since it has a ton of
 * state and view magic. I tried my best to document it, but it's probably not the most friendly or
 * extendable. You have been warned.
 *
 * @author OxygenCobalt (With help from Umano and Hai Zhang)
 */
class BottomSheetLayout
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    ViewGroup(context, attrs, defStyle) {
    private enum class State {
        EXPANDED,
        COLLAPSED,
        HIDDEN,
        DRAGGING
    }

    // Core views [obtained when layout is inflated]
    private lateinit var contentView: View
    private lateinit var barView: View
    private lateinit var panelView: View

    private val elevationNormal = context.getDimenSafe(R.dimen.elevation_normal)
    private val cornersLarge =
        if (Settings(context).roundMode) {
            // Since album covers are rounded, we can also round the bar too.
            context.getDimenSizeSafe(R.dimen.size_corners_large)
        } else {
            0
        }

    // We have to define the background before the bottom sheet declaration as otherwise it wont
    // work
    private val sheetBackground =
        MaterialShapeDrawable.createWithElevationOverlay(context).apply {
            fillColor = context.getAttrColorSafe(R.attr.colorSurface).stateList
            elevation = context.pxOfDp(elevationNormal).toFloat()
            setCornerSize(cornersLarge.toFloat())
        }

    private val sheetView =
        FrameLayout(context).apply {
            id = R.id.bottom_sheet_view

            isClickable = true
            isFocusable = false
            isFocusableInTouchMode = false

            // The way we fade out the elevation overlay is not by actually reducing the
            // elevation but by fading out the background drawable itself. To be safe,
            // we apply this background drawable to a layer list with another colorSurface
            // shape drawable, just in case weird things happen if background drawable is
            // completely transparent.
            val fallbackBackground =
                MaterialShapeDrawable().apply {
                    fillColor = context.getAttrColorSafe(R.attr.colorSurface).stateList
                    setCornerSize(cornersLarge.toFloat())
                }

            background = LayerDrawable(arrayOf(fallbackBackground, sheetBackground))

            disableDropShadowCompat()
        }

    /** The drag helper that animates and dispatches drag events to the bottom sheet. */
    private val dragHelper =
        ViewDragHelper.create(this, DragHelperCallback()).apply {
            minVelocity = MIN_FLING_VEL * resources.displayMetrics.density
        }

    /**
     * The current window insets. Important since this layout must play a long with Auxio's
     * edge-to-edge functionality.
     */
    private var lastInsets: WindowInsets? = null

    /** The current bottom sheet state. Can be [State.DRAGGING] */
    private var state = INIT_SHEET_STATE

    /** The last bottom sheet state before a drag event began. */
    private var lastIdleState = INIT_SHEET_STATE

    /** The range of pixels that the bottom sheet can drag through */
    private var sheetRange = 0

    /**
     * The relative offset of this bottom sheet as a percentage of [sheetRange]. A value of 1 means
     * a fully expanded sheet. A value of 0 means a collapsed sheet. A value below 0 means a hidden
     * sheet.
     */
    private var sheetOffset = 0f

    // Miscellaneous touch things
    private var initMotionX = 0f
    private var initMotionY = 0f
    private val tRect = Rect()

    var isDraggable = false

    init {
        setWillNotDraw(false)
    }

    // / --- CONTROL METHODS ---

    /**
     * Show the bottom sheet, only if it's hidden.
     * @return if the sheet was shown
     */
    fun show(): Boolean {
        if (state == State.HIDDEN) {
            applyState(State.COLLAPSED)
            return true
        }

        return false
    }

    /**
     * Expand the bottom sheet if it is currently collapsed.
     * @return If the sheet was expanded
     */
    fun expand(): Boolean {
        if (state == State.COLLAPSED) {
            applyState(State.EXPANDED)
            return true
        }

        return false
    }

    /**
     * Collapse the sheet if it is currently expanded.
     * @return If the sheet was collapsed
     */
    fun collapse(): Boolean {
        if (state == State.EXPANDED) {
            applyState(State.COLLAPSED)
            return true
        }

        return false
    }

    /**
     * Hide the sheet if it is not hidden.
     * @return If the sheet was hidden
     */
    fun hide(): Boolean {
        if (state != State.HIDDEN) {
            applyState(State.HIDDEN)
            return true
        }

        return false
    }

    private fun applyState(newState: State) {
        logD("Applying bottom sheet state $newState")

        // Dragging events are really complex and we don't want to mess up the state
        // while we are in one.
        if (newState == this.state) {
            return
        }

        if (!isLaidOut) {
            // Not laid out, just apply the state and let the measure + layout steps apply it for
            // us.
            setSheetStateInternal(newState)
        } else {
            // We are laid out. In this case we actually animate to our desired target.
            when (newState) {
                State.COLLAPSED -> smoothSlideTo(0f)
                State.EXPANDED -> smoothSlideTo(1.0f)
                State.HIDDEN -> smoothSlideTo(calculateSheetOffset(measuredHeight))
                else -> {}
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        contentView = getChildAt(0) // Child 1 is assumed to be the content
        barView = getChildAt(1) // Child 2 is assumed to be the bar used when collapsed
        panelView = getChildAt(2) // Child 3 is assumed to be the panel used when expanded

        // We actually move the bar and panel views into a container so that they have consistent
        // behavior when be manipulate layouts later.
        removeView(barView)
        removeView(panelView)

        sheetView.apply {
            addView(
                barView,
                FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                    .apply { gravity = Gravity.TOP })

            addView(
                panelView,
                FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    .apply { gravity = Gravity.CENTER })
        }

        addView(sheetView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // Sanity check. The last thing I want to deal with is this view being WRAP_CONTENT.
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        check(widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            "This view must be MATCH_PARENT"
        }

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)

        // First measure our actual bottom sheet. We need to do this first to determine our
        // range and offset values.
        val sheetWidthSpec = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY)
        val sheetHeightSpec = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
        sheetView.measure(sheetWidthSpec, sheetHeightSpec)

        sheetRange = measuredHeight - barView.measuredHeight

        if (!isLaidOut) {
            logD("Doing initial bottom sheet layout")

            // This is our first layout, so make sure we know what offset we should work with
            // before we measure our content
            sheetOffset =
                when (state) {
                    State.EXPANDED -> 1f
                    State.HIDDEN -> calculateSheetOffset(measuredHeight)
                    else -> 0f
                }

            updateBottomSheetTransition()
        }

        applyContentWindowInsets()

        // The content is always MATCH_PARENT, which nominally means that it will overlap
        // with the sheet. This is actually to ensure that when a rounded sheet is used,
        // the content will show in the gaps on each corner. To resolve the overlapping views,
        // we modify window insets later.
        val contentWidthSpec = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY)
        val contentHeightSpec = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
        contentView.measure(contentWidthSpec, contentHeightSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // Figure out where our bottom sheet should be and lay it out there.
        val sheetTop = calculateSheetTopPosition(sheetOffset)
        sheetView.layout(0, sheetTop, sheetView.measuredWidth, sheetView.measuredHeight + sheetTop)
        contentView.layout(0, 0, contentView.measuredWidth, contentView.measuredHeight)
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val save = canvas.save()

        // Drawing views that are under the bottom sheet is inefficient, clip the canvas
        // so that doesn't occur. Make sure we account for the corner radius when
        // doing this so that drawing still occurs in the gaps created by such.
        if (child == contentView) {
            canvas.getClipBounds(tRect)
            tRect.bottom = tRect.bottom.coerceAtMost(sheetView.top + cornersLarge)
            canvas.clipRect(tRect)
        }

        return super.drawChild(canvas, child, drawingTime).also { canvas.restoreToCount(save) }
    }

    override fun dispatchApplyWindowInsets(insets: WindowInsets): WindowInsets {
        // One issue with handling a bottom bar with edge-to-edge is that if you want to
        // apply window insets to a view, those insets will cause incorrect spacing if the
        // bottom navigation is consumed by a bar. To fix this, we modify the bottom insets
        // to reflect the presence of the bottom sheet [at least in it's collapsed state]
        sheetView.dispatchApplyWindowInsets(insets)
        lastInsets = insets
        applyContentWindowInsets()
        return insets
    }

    /**
     * Apply window insets to the content views in this layouts. This is done separately as at times
     * we want to re-inset the content views but not re-inset the bar view.
     */
    private fun applyContentWindowInsets() {
        val insets = lastInsets
        if (insets != null) {
            contentView.dispatchApplyWindowInsets(adjustInsets(insets))
        }
    }

    /** Adjust window insets to line up with the bottom sheet */
    private fun adjustInsets(insets: WindowInsets): WindowInsets {
        // While the content view spans the whole of the layout, we still want it to adapt to
        // the presence of the bottom sheet. WindowInsets is a great API for us to abuse in order
        // to achieve this. Basically, we do a kind of reverse-measure to figure out how much
        // space the sheet has consumed, and then combine that with the existing bottom inset to
        // see which one should be applied. Note that we do not include the expanded sheet into
        // this calculation, as it should be covered up by the bottom sheet.
        val bars = insets.systemBarInsetsCompat
        val consumedByNonExpandedSheet =
            measuredHeight - calculateSheetTopPosition(min(sheetOffset, 0f))
        val adjustedBottomInset = max(consumedByNonExpandedSheet, bars.bottom)
        return insets.replaceSystemBarInsetsCompat(
            bars.left, bars.top, bars.right, adjustedBottomInset)
    }

    override fun onSaveInstanceState(): Parcelable =
        Bundle().apply {
            putParcelable("superState", super.onSaveInstanceState())
            putSerializable(
                KEY_SHEET_STATE,
                if (state != State.DRAGGING) {
                    state
                } else {
                    lastIdleState
                })
        }

    override fun onRestoreInstanceState(savedState: Parcelable) {
        if (savedState is Bundle) {
            this.state = savedState.getSerializable(KEY_SHEET_STATE) as? State ?: INIT_SHEET_STATE
            super.onRestoreInstanceState(savedState.getParcelable("superState"))
        } else {
            super.onRestoreInstanceState(savedState)
        }
    }

    @Suppress("Redundant")
    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        performClick()

        return if (!isDraggable) {
            super.onTouchEvent(ev)
        } else
            try {
                dragHelper.processTouchEvent(ev)
                true
            } catch (ex: Exception) {
                // Ignore the pointer out of range exception
                false
            }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isDraggable) {
            return super.onInterceptTouchEvent(ev)
        }

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initMotionX = ev.x
                initMotionY = ev.y

                if (!sheetView.isUnder(ev.x, ev.y)) {
                    // Pointer is not on our view, do not intercept this event
                    dragHelper.cancel()
                    return false
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val adx = abs(ev.x - initMotionX)
                val ady = abs(ev.y - initMotionY)

                val pointerUnder = sheetView.isUnder(ev.x, ev.y)
                val motionUnder = sheetView.isUnder(initMotionX, initMotionY)

                if (!(pointerUnder || motionUnder) || ady > dragHelper.touchSlop && adx > ady) {
                    // Pointer has moved beyond our control, do not intercept this event
                    dragHelper.cancel()
                    return false
                }
            }
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP ->
                if (dragHelper.isDragging) {
                    // Stopped pressing while we were dragging, let the drag helper handle it
                    dragHelper.processTouchEvent(ev)
                    return true
                }
        }

        return dragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun computeScroll() {
        // I have no idea what this does but it seems important so I keep it around
        if (dragHelper.continueSettling(true)) {
            postInvalidateOnAnimation()
        }
    }

    private val ViewDragHelper.isDragging: Boolean
        get() {
            // We can't grab the drag state outside of a callback, but that's stupid and I don't
            // want to vendor ViewDragHelper so I just do reflection instead.
            val state =
                try {
                    VIEW_DRAG_HELPER_STATE_FIELD.get(this)
                } catch (e: Exception) {
                    ViewDragHelper.STATE_IDLE
                }

            return state == ViewDragHelper.STATE_DRAGGING
        }

    private fun setSheetStateInternal(newState: State) {
        if (this.state == newState) {
            return
        }

        logD("New state: $newState")
        this.state = newState

        // TODO: Improve accessibility by:
        // 1. Adding a (non-visible) handle. Material components now technically does have
        // this, but it relies on the busted BottomSheetBehavior.
        // 2. Adding the controls that BottomSheetBehavior defines in-app.
        sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
    }

    /**
     * Do the nice view animations that occur whenever we slide up the bottom sheet. The way I
     * transition is largely inspired by Android 12's notification panel, with the compact view
     * fading out completely before the panel view fades in.
     */
    private fun updateBottomSheetTransition() {
        val ratio = max(sheetOffset, 0f)

        val outRatio = 1 - ratio
        val halfOutRatio = min(ratio * 2, 1f)
        val halfInRatio = max(ratio - 0.5f, 0f) * 2

        contentView.apply {
            alpha = outRatio
            isInvisible = alpha == 0f
        }

        // Slowly reduce the elevation of the bottom sheet as we slide up, eventually resulting in a
        // neutral color instead of an elevated one when fully expanded.
        sheetBackground.alpha = (outRatio * 255).toInt()
        sheetView.translationZ = elevationNormal * outRatio

        // Fade out our bar view as we slide up
        barView.apply {
            alpha = min(1 - halfOutRatio, 1f)
            isInvisible = alpha == 0f

            // When edge-to-edge is enabled, we want to make the bar move along with the top
            // window insets as it goes upwards. Do this by progressively modifying the y
            // translation with a fraction of the said inset.
            lastInsets?.let { insets ->
                val bars = insets.systemBarInsetsCompat
                translationY = bars.top * halfOutRatio
            }
        }

        // Fade in our panel as we slide up
        panelView.apply {
            alpha = halfInRatio
            isInvisible = alpha == 0f
        }
    }

    private fun calculateSheetTopPosition(sheetOffset: Float): Int =
        measuredHeight - barView.measuredHeight - (sheetOffset * sheetRange).toInt()

    private fun calculateSheetOffset(top: Int): Float =
        (calculateSheetTopPosition(0f) - top).toFloat() / sheetRange

    private fun smoothSlideTo(offset: Float) {
        logD("Smooth sliding to $offset")

        if (dragHelper.smoothSlideViewTo(
            sheetView, sheetView.left, calculateSheetTopPosition(offset))) {
            postInvalidateOnAnimation()
        }
    }

    private inner class DragHelperCallback : ViewDragHelper.Callback() {
        // Only capture on a fully shown panel view
        override fun tryCaptureView(child: View, pointerId: Int) =
            child === sheetView && sheetOffset >= 0

        override fun onViewDragStateChanged(dragState: Int) {
            when (dragState) {
                ViewDragHelper.STATE_DRAGGING -> {
                    if (!isDraggable) {
                        return
                    }

                    // We're dragging, so we need to update our state accordingly
                    if (this@BottomSheetLayout.state != State.DRAGGING) {
                        lastIdleState = this@BottomSheetLayout.state
                    }

                    setSheetStateInternal(State.DRAGGING)
                }
                ViewDragHelper.STATE_IDLE -> {
                    sheetOffset = calculateSheetOffset(sheetView.top)

                    val newState =
                        when {
                            sheetOffset == 1f -> State.EXPANDED
                            sheetOffset == 0f -> State.COLLAPSED
                            sheetOffset < 0f -> State.HIDDEN
                            else -> State.EXPANDED
                        }

                    setSheetStateInternal(newState)
                }
            }
        }

        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {}

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            // Update our sheet offset using the new top value
            sheetOffset = calculateSheetOffset(top)
            if (sheetOffset < 0) {
                // If we are hiding/showing the sheet, see if we need to update the insets
                applyContentWindowInsets()
            }

            updateBottomSheetTransition()
            invalidate()
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val newOffset =
                when {
                    // Swipe Up -> Expand to top
                    yvel < 0 -> 1f
                    // Swipe down -> Collapse to bottom
                    yvel > 0 -> 0f
                    // No velocity, far enough from middle to expand to top
                    sheetOffset >= 0.5f -> 1f
                    // Collapse to bottom
                    else -> 0f
                }

            dragHelper.settleCapturedViewAt(
                releasedChild.left, calculateSheetTopPosition(newOffset))

            invalidate()
        }

        override fun getViewVerticalDragRange(child: View) = sheetRange

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            val collapsedTop = calculateSheetTopPosition(0f)
            val expandedTop = calculateSheetTopPosition(1.0f)
            return top.coerceAtLeast(expandedTop).coerceAtMost(collapsedTop)
        }
    }

    companion object {
        private val INIT_SHEET_STATE = State.HIDDEN
        private val VIEW_DRAG_HELPER_STATE_FIELD: Field by
            lazyReflectedField(ViewDragHelper::class, "mDragState")

        private const val MIN_FLING_VEL = 400
        private const val KEY_SHEET_STATE = BuildConfig.APPLICATION_ID + ".key.BOTTOM_SHEET_STATE"
    }
}
