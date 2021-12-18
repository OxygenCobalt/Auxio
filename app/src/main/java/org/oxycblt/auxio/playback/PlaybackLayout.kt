package org.oxycblt.auxio.playback

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Insets
import android.graphics.Rect
import android.graphics.drawable.LayerDrawable
import android.os.Build
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.customview.widget.ViewDragHelper
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.resolveAttr
import org.oxycblt.auxio.util.resolveDrawable
import org.oxycblt.auxio.util.systemBarsCompat
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * This layout handles pretty much every aspect of the playback UI flow, notably the playback
 * bar and it's ability to slide up into the playback view. It's a blend of Hai Zhang's
 * PersistentBarLayout and Umano's SlidingUpPanelLayout, albeit heavily minified to remove
 * extraneous use cases and updated to support the latest SDK level and androidx tools.
 *
 * **Note:** If you want to adapt this layout into your own app. Good luck. This layout has been
 * reduced to Auxio's use case in particular and is really hard to understand since it has a ton
 * of state and view magic. I tried my best to document it, but it's probably not the most friendly
 * or extendable. You have been warned.
 *
 * TODO: Add the queue view into this layout.
 *
 * @author OxygenCobalt (With help from Umano and Hai Zhang)
 */
class PlaybackLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {
    private enum class PanelState {
        EXPANDED, COLLAPSED, HIDDEN, DRAGGING
    }

    private lateinit var contentView: View
    private val playbackContainerView: FrameLayout
    private val playbackBarView: PlaybackBarView
    private val playbackPanelView: FrameLayout

    private val playbackContainerBg: MaterialShapeDrawable
    private val playbackFragment = PlaybackFragment()

    /**
     * The drag helper that animates and dispatches drag events to the panels.
     */
    private val dragHelper = ViewDragHelper.create(this, DragHelperCallback()).apply {
        minVelocity = MIN_FLING_VEL * resources.displayMetrics.density
    }

    /**
     * The current window insets.
     * Important since this layout must play a long with Auxio's edge-to-edge functionality.
     */
    private var lastInsets: WindowInsets? = null

    /** The current panel state. Can be [PanelState.DRAGGING]*/
    private var panelState = INIT_PANEL_STATE

    /** The last panel state before a drag event began. */
    private var lastIdlePanelState = INIT_PANEL_STATE

    /** The range of pixels that the panel can drag through */
    private var panelRange = 0

    /**
     * The relative offset of this panel as a percentage of [panelRange].
     * A value of 1 means a fully expanded panel.
     * A value of 0 means a collapsed panel.
     * A value below 0 means a hidden panel.
     */
    private var panelOffset = 0f

    // Miscellaneous view things
    private var initMotionX = 0f
    private var initMotionY = 0f
    private val tRect = Rect()

    /** See [isDragging] */
    private val dragStateField = ViewDragHelper::class.java.getDeclaredField("mDragState").apply {
        isAccessible = true
    }

    init {
        setWillNotDraw(false)

        // Set up our playback views. Doing this allows us to abstract away the implementation
        // of these views from the user of this layout [MainFragment].
        playbackContainerView = FrameLayout(context).apply {
            id = R.id.playback_container

            isClickable = true
            isFocusable = false
            isFocusableInTouchMode = false

            playbackContainerBg = MaterialShapeDrawable.createWithElevationOverlay(context).apply {
                fillColor = ColorStateList.valueOf(R.attr.colorSurface.resolveAttr(context))
                elevation = resources.getDimensionPixelSize(R.dimen.elevation_normal).toFloat()
            }

            // The way we fade out the elevation overlay is not by actually reducing the elevation
            // but by fading out the background drawable itself. To be safe, we apply this
            // background drawable to a layer list with another colorSurface shape drawable, just
            // in case weird things happen if background drawable is completely transparent.
            background = (R.drawable.ui_panel_bg.resolveDrawable(context) as LayerDrawable).apply {
                setDrawableByLayerId(R.id.panel_overlay, playbackContainerBg)
            }
        }

        playbackBarView = PlaybackBarView(context).apply {
            id = R.id.playback_bar

            playbackContainerView.addView(this)

            (layoutParams as FrameLayout.LayoutParams).apply {
                width = LayoutParams.MATCH_PARENT
                height = LayoutParams.WRAP_CONTENT
                gravity = Gravity.TOP
            }

            // The bar view if clicked will expand into the full panel
            setOnClickListener {
                if (canSlide && panelState != PanelState.EXPANDED) {
                    applyState(PanelState.EXPANDED)
                }
            }
        }

        playbackPanelView = FrameLayout(context).apply {
            playbackContainerView.addView(this)

            (layoutParams as FrameLayout.LayoutParams).apply {
                width = LayoutParams.MATCH_PARENT
                height = LayoutParams.MATCH_PARENT
                gravity = Gravity.CENTER
            }

            id = R.id.playback_panel

            // Make sure we add our fragment to this view. This is actually a replace operation
            // since we don't want to stack fragments but we can't ensure that this view doesn't
            // already have a fragment attached.
            try {
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.playback_panel, playbackFragment)
                    .commit()
            } catch (e: Exception) {
                // Band-aid to stop the app crashing if we have to swap out the content view
                // without warning (which we have to do sometimes because android is the worst
                // thing ever
            }
        }
    }

    // / --- CONTROL METHODS ---

    /**
     * Update the song that this layout is showing. This will be reflected in the compact view
     * at the bottom of the screen.
     */
    fun setup(
        playbackModel: PlaybackViewModel,
        detailModel: DetailViewModel,
        viewLifecycleOwner: LifecycleOwner
    ) {
        setSong(playbackModel.song.value)

        playbackModel.song.observe(viewLifecycleOwner) { song ->
            setSong(song)
        }

        playbackBarView.setup(playbackModel, detailModel, viewLifecycleOwner)
    }

    private fun setSong(song: Song?) {
        if (song != null) {
            playbackBarView.setSong(song)

            // Make sure the bar is shown
            if (panelState == PanelState.HIDDEN) {
                applyState(PanelState.COLLAPSED)
            }
        } else {
            applyState(PanelState.HIDDEN)
        }
    }

    /**
     * Collapse the panel if it is currently expanded.
     * @return If the panel was collapsed or not.
     */
    fun collapse(): Boolean {
        if (panelState == PanelState.EXPANDED) {
            applyState(PanelState.COLLAPSED)
            return true
        }

        return false
    }

    private fun applyState(state: PanelState) {
        // Dragging events are really complex and we don't want to mess up the state
        // while we are in one.
        if (state == panelState || panelState == PanelState.DRAGGING) {
            return
        }

        // Disallow events that aren't showing the bar when disabled
        if (state != PanelState.HIDDEN && state != PanelState.COLLAPSED && !isEnabled) {
            return
        }

        if (!isLaidOut) {
            // Not laid out, just apply the state and let the measure + layout steps apply it for us.
            setPanelStateInternal(state)
        } else {
            // We are laid out. In this case we actually animate to our desired target.
            when (state) {
                PanelState.COLLAPSED -> smoothSlideTo(0f)
                PanelState.EXPANDED -> smoothSlideTo(1.0f)
                PanelState.HIDDEN -> smoothSlideTo(computePanelOffset(measuredHeight))
                else -> {}
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        check(childCount == 1) { "There must only be one view in this layout" }

        // Grab our content view [asserting that there is nothing else] and then add our panel.
        // I would add our panel in our init, but that messes things up for some reason.
        contentView = getChildAt(0)
        addView(playbackContainerView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // Sanity check. The last thing I want to deal with is this view being WRAP_CONTENT.
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        check(widthMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.EXACTLY) {
            "This view must be MATCH_PARENT"
        }

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)

        // First measure our actual container. We need to do this first to determine our
        // range and offset values.
        val panelWidthSpec = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY)
        val panelHeightSpec = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
        playbackContainerView.measure(panelWidthSpec, panelHeightSpec)

        panelRange = measuredHeight - playbackBarView.measuredHeight

        if (!isLaidOut) {
            // This is our first layout, so make sure we know what offset we should work with
            // before we measure our content
            panelOffset = when (panelState) {
                PanelState.EXPANDED -> 1.0f
                PanelState.HIDDEN -> computePanelOffset(measuredHeight)
                else -> 0f
            }

            updatePanelTransition()
        }

        applyContentWindowInsets()
        measureContent()
    }

    private fun measureContent() {
        // We need to find out how much the panel should affect the view.
        // When the panel is in it's bar form, we shorten the content view. If it's being expanded,
        // we keep the same height and just overlay the panel.
        val barHeightAdjusted = measuredHeight - computePanelTopPosition(min(panelOffset, 0f))

        // Note that these views will always be a fixed MATCH_PARENT. This is intentional,
        // as it reduces the logic we have to deal with regarding WRAP_CONTENT views.
        val contentWidthSpec = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY)
        val contentHeightSpec = MeasureSpec.makeMeasureSpec(
            measuredHeight - barHeightAdjusted, MeasureSpec.EXACTLY
        )

        contentView.measure(contentWidthSpec, contentHeightSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // Figure out where our panel should be and lay it out there.
        val panelTop = computePanelTopPosition(panelOffset)

        playbackContainerView.layout(
            0,
            panelTop,
            playbackContainerView.measuredWidth,
            playbackContainerView.measuredHeight + panelTop
        )

        layoutContent()
    }

    private fun layoutContent() {
        // We already did our magic while measuring. No need to do anything here.
        contentView.layout(0, 0, contentView.measuredWidth, contentView.measuredHeight)
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val save = canvas.save()

        // Drawing views that are under the panel is inefficient, clip the canvas
        // so that doesn't occur.
        if (child == contentView) {
            canvas.getClipBounds(tRect)
            tRect.bottom = tRect.bottom.coerceAtMost(playbackContainerView.top)
            canvas.clipRect(tRect)
        }

        return super.drawChild(canvas, child, drawingTime).also {
            canvas.restoreToCount(save)
        }
    }

    override fun dispatchApplyWindowInsets(insets: WindowInsets): WindowInsets {
        // One issue with handling a bottom bar with edge-to-edge is that if you want to
        // apply window insets to a view, those insets will cause incorrect spacing if the
        // bottom navigation is consumed by a bar. To fix this, we modify the bottom insets
        // to reflect the presence of the panel [at least in it's collapsed state]
        playbackContainerView.dispatchApplyWindowInsets(insets)

        lastInsets = insets
        applyContentWindowInsets()

        return insets
    }

    /**
     * Apply window insets to the content views in this layouts. This is done separately as at
     * times we want to re-inset the content views but not re-inset the bar view.
     */
    private fun applyContentWindowInsets() {
        val insets = lastInsets

        if (insets != null) {
            contentView.dispatchApplyWindowInsets(adjustInsets(insets))
        }
    }

    /**
     * Adjust window insets to line up with the panel
     */
    private fun adjustInsets(insets: WindowInsets): WindowInsets {
        // We kind to do a reverse-measure to figure out how we should inset this view.
        // Find how much space is lost by the panel and then combine that with the
        // bottom inset to find how much space we should apply.
        val bars = insets.systemBarsCompat
        val consumedByPanel = computePanelTopPosition(panelOffset) - measuredHeight
        val adjustedBottomInset = (consumedByPanel + bars.bottom).coerceAtLeast(0)

        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                WindowInsets.Builder(insets)
                    .setInsets(
                        WindowInsets.Type.systemBars(),
                        Insets.of(bars.left, bars.top, bars.right, adjustedBottomInset)
                    )
                    .build()
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
                @Suppress("DEPRECATION")
                insets.replaceSystemWindowInsets(
                    bars.left, bars.top, bars.right, adjustedBottomInset
                )
            }

            else -> insets
        }
    }

    override fun onSaveInstanceState(): Parcelable = Bundle().apply {
        putParcelable("superState", super.onSaveInstanceState())
        putSerializable(
            KEY_PANEL_STATE,
            if (panelState != PanelState.DRAGGING) {
                panelState
            } else {
                lastIdlePanelState
            }
        )
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            panelState = state.getSerializable(KEY_PANEL_STATE) as? PanelState ?: INIT_PANEL_STATE
            super.onRestoreInstanceState(state.getParcelable("superState"))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    @Suppress("Redundant")
    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        performClick()

        return if (!canSlide) {
            super.onTouchEvent(ev)
        } else try {
            dragHelper.processTouchEvent(ev)
            true
        } catch (ex: Exception) {
            // Ignore the pointer out of range exception
            false
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!canSlide) {
            return super.onInterceptTouchEvent(ev)
        }

        val adx = abs(ev.x - initMotionX)
        val ady = abs(ev.y - initMotionY)
        val dragSlop = dragHelper.touchSlop

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                initMotionX = ev.x
                initMotionY = ev.y

                if (!playbackContainerView.isUnder(ev.x.toInt(), ev.y.toInt())) {
                    // Pointer is not on our view, do not intercept this event
                    dragHelper.cancel()
                    return false
                }
            }

            MotionEvent.ACTION_MOVE -> {
                val pointerUnder = playbackContainerView.isUnder(ev.x.toInt(), ev.y.toInt())
                val motionUnder = playbackContainerView.isUnder(initMotionX.toInt(), initMotionY.toInt())

                if (!(pointerUnder || motionUnder) || ady > dragSlop && adx > ady) {
                    // Pointer has moved beyond our control, do not intercept this event
                    dragHelper.cancel()
                    return false
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP ->
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

    private fun View.isUnder(x: Int, y: Int): Boolean {
        val viewLocation = IntArray(2)
        getLocationOnScreen(viewLocation)

        val parentLocation = IntArray(2)
        (parent as View).getLocationOnScreen(parentLocation)

        val screenX = parentLocation[0] + x
        val screenY = parentLocation[1] + y

        val inX = screenX >= viewLocation[0] && screenX < viewLocation[0] + width
        val inY = screenY >= viewLocation[1] && screenY < viewLocation[1] + height

        return inX && inY
    }

    private val ViewDragHelper.isDragging: Boolean
        get() {
            // We can't grab the drag state outside of a callback, but that's stupid and I don't
            // want to vendor ViewDragHelper so I just do reflection instead.
            val state = try {
                dragStateField.get(this)
            } catch (e: Exception) {
                ViewDragHelper.STATE_IDLE
            }

            return state == ViewDragHelper.STATE_DRAGGING
        }

    private fun setPanelStateInternal(state: PanelState) {
        if (panelState == state) {
            return
        }

        panelState = state

        sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
    }

    /**
     * Do the nice view animations that occur whenever we slide up the playback panel.
     * The way I transition is largely inspired by Android 12's notification panel, with the
     * compact view fading out completely before the panel view fades in. We don't fade out the
     * content though so we have cohesion between the other sliding transitions.
     */
    private fun updatePanelTransition() {
        val ratio = max(panelOffset, 0f)

        val outRatio = 1 - ratio
        val halfOutRatio = min(ratio / 0.5f, 1f)
        val halfInRatio = max(ratio - 0.5f, 0f) / 0.5f

        // Optimize out drawing for this view completely
        contentView.apply {
            alpha = outRatio
            isInvisible = alpha == 0f
        }

        // Slowly reduce the elevation of the container as we slide up, eventually resulting in a
        // neutral color instead of an elevated one when fully expanded.
        playbackContainerBg.alpha = (outRatio * 255).toInt()

        // Fade out our bar view as we slide up
        playbackBarView.apply {
            alpha = min(1 - halfOutRatio, 1f)
            isInvisible = alpha == 0f

            // When edge-to-edge is enabled, the playback bar will not fade out into the
            // playback menu's toolbar properly as PlaybackFragment will apply it's window insets.
            // Therefore, we slowly increase the bar view's margins so that it fully disappears
            // near the toolbar instead of in the system bars, which just looks nicer.
            // The reason why we can't pad the bar is that it might result in the padding desyncing
            // [reminder that this view also applies the bottom window inset] and we can't
            // apply padding to the whole container layout since that would adjust the size
            // of the playback view. This seems to be the least obtrusive way to do this.
            lastInsets?.systemBarsCompat?.let { bars ->
                val params = layoutParams as FrameLayout.LayoutParams
                val oldTopMargin = params.topMargin

                params.setMargins(
                    params.leftMargin,
                    (bars.top * halfOutRatio).toInt(),
                    params.rightMargin,
                    params.bottomMargin
                )

                // Poke the layout only when we changed something
                if (params.topMargin != oldTopMargin) {
                    playbackContainerView.requestLayout()
                }
            }
        }

        // Fade in our panel as we slide up
        playbackPanelView.apply {
            alpha = halfInRatio
            isInvisible = alpha == 0f
        }
    }

    private fun computePanelTopPosition(panelOffset: Float): Int =
        measuredHeight - playbackBarView.measuredHeight - (panelOffset * panelRange).toInt()

    private fun computePanelOffset(topPosition: Int): Float =
        (computePanelTopPosition(0f) - topPosition).toFloat() / panelRange

    private fun smoothSlideTo(offset: Float) {
        val okay = dragHelper.smoothSlideViewTo(
            playbackContainerView, playbackContainerView.left, computePanelTopPosition(offset)
        )

        if (okay) {
            postInvalidateOnAnimation()
        }
    }

    private val canSlide: Boolean
        get() = panelState != PanelState.HIDDEN && isEnabled

    private inner class DragHelperCallback : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            // Only capture on a fully expanded panel view
            return child === playbackContainerView && panelOffset >= 0
        }

        override fun onViewDragStateChanged(state: Int) {
            if (state == ViewDragHelper.STATE_IDLE) {
                panelOffset = computePanelOffset(playbackContainerView.top)

                when {
                    panelOffset == 1f -> setPanelStateInternal(PanelState.EXPANDED)
                    panelOffset == 0f -> setPanelStateInternal(PanelState.COLLAPSED)
                    panelOffset < 0f -> {
                        setPanelStateInternal(PanelState.HIDDEN)
                        playbackContainerView.visibility = INVISIBLE
                    }

                    else -> setPanelStateInternal(PanelState.EXPANDED)
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
            // We're dragging, so we need to update our state accordingly
            if (panelState != PanelState.DRAGGING) {
                lastIdlePanelState = panelState
            }

            setPanelStateInternal(PanelState.DRAGGING)

            // Update our panel offset using the new top value
            panelOffset = computePanelOffset(top)

            if (panelOffset < 0) {
                // If we are hiding the panel, make sure we relayout our content too.
                applyContentWindowInsets()
                measureContent()
                layoutContent()
            }

            updatePanelTransition()
            invalidate()
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val newOffset = when {
                // Swipe Up -> Expand to top
                yvel < 0 -> 1f
                // Swipe down -> Collapse to bottom
                yvel > 0 -> 0f
                // No velocity, far enough from middle to expand to top
                panelOffset >= 0.5f -> 1f
                // Collapse to bottom
                else -> 0f
            }

            dragHelper.settleCapturedViewAt(releasedChild.left, computePanelTopPosition(newOffset))

            invalidate()
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return panelRange
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            val collapsedTop = computePanelTopPosition(0f)
            val expandedTop = computePanelTopPosition(1.0f)
            return top.coerceAtLeast(expandedTop).coerceAtMost(collapsedTop)
        }
    }

    companion object {
        private val INIT_PANEL_STATE = PanelState.HIDDEN
        private const val MIN_FLING_VEL = 400
        private const val KEY_PANEL_STATE = BuildConfig.APPLICATION_ID + ".key.panel_state"
    }
}
