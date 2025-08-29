/*
 * Copyright (c) 2025 Auxio Project
 * PlayerFastSeekOverlay.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.ui.stepper

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.ui.UISettings

enum class DisplayPortion {
    LEFT,
    MIDDLE,
    RIGHT,
    LEFT_HALF,
    RIGHT_HALF
}

interface DoubleTapListener {
    fun onDoubleTapStarted(portion: DisplayPortion)

    fun onDoubleTapProgressDown(portion: DisplayPortion)

    fun onDoubleTapFinished()
}

@AndroidEntryPoint
class PlayerFastSeekOverlay(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs), DoubleTapListener, GestureDetector.OnDoubleTapListener {

    @Inject lateinit var uiSettings: UISettings

    private var leftSecondsView: SecondsView
    private var rightSecondsView: SecondsView
    private var leftCircleClipTapView: CircleClipTapView
    private var rightCircleClipTapView: CircleClipTapView
    private var rootConstraintLayout: ConstraintLayout

    private val gestureDetector: GestureDetector
    private val shapeAppearance: ShapeAppearanceModel

    init {
        LayoutInflater.from(context).inflate(R.layout.player_fast_seek_overlay, this, true)

        // Set up shape appearance based on UISettings similar to CoverView
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.PlayerFastSeekOverlay)
        val shapeAppearanceRes =
            styledAttrs.getResourceId(R.styleable.PlayerFastSeekOverlay_shapeAppearance, 0)

        shapeAppearance =
            if (uiSettings.roundMode) {
                if (shapeAppearanceRes != 0) {
                    ShapeAppearanceModel.builder(context, shapeAppearanceRes, -1).build()
                } else {
                    ShapeAppearanceModel.builder(
                            context,
                            com.google.android.material.R.style
                                .ShapeAppearance_Material3_Corner_Medium,
                            -1)
                        .build()
                }
            } else {
                ShapeAppearanceModel.builder().build()
            }

        styledAttrs.recycle()

        leftSecondsView = findViewById(R.id.left_seconds_view)
        rightSecondsView = findViewById(R.id.right_seconds_view)
        leftCircleClipTapView = findViewById(R.id.left_circle_clip_tap_view)
        rightCircleClipTapView = findViewById(R.id.right_circle_clip_tap_view)
        rootConstraintLayout = findViewById(R.id.root_constraint_layout)

        // Apply shape clipping to the overlay
        clipToOutline = true
        background =
            MaterialShapeDrawable().apply {
                shapeAppearanceModel = shapeAppearance
                // Set transparent background as we only want the clipping
                fillColor =
                    android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT)
            }

        // Hide overlay elements by default
        leftSecondsView.alpha = 0f
        rightSecondsView.alpha = 0f
        leftCircleClipTapView.alpha = 0f
        rightCircleClipTapView.alpha = 0f
        leftSecondsView.visibility = INVISIBLE
        rightSecondsView.visibility = INVISIBLE
        leftCircleClipTapView.visibility = INVISIBLE
        rightCircleClipTapView.visibility = INVISIBLE

        // Set up gesture detection
        gestureDetector =
            GestureDetector(
                context,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onDown(e: MotionEvent) = true
                })
        gestureDetector.setOnDoubleTapListener(this)

        addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
            leftCircleClipTapView.updateArcSize(view)
            rightCircleClipTapView.updateArcSize(view)
        }

        // Initialize directions for each side
        leftSecondsView.setForwarding(false)
        rightSecondsView.setForwarding(true)
        leftCircleClipTapView.updatePosition(true) // isLeft = true for left side
        rightCircleClipTapView.updatePosition(false) // isLeft = false for right side
    }

    private var performListener: PerformListener? = null

    fun performListener(listener: PerformListener?) = apply { performListener = listener }

    private var seekSecondsSupplier: () -> Int = { 0 }

    fun seekSecondsSupplier(supplier: (() -> Int)?) = apply {
        seekSecondsSupplier = supplier ?: { 0 }
    }

    // Track state for left overlay
    private var leftInitTap: Boolean = false
    private var leftIsAnimatingIn: Boolean = false
    private var leftFadeOutRunnable: Runnable? = null

    // Track state for right overlay
    private var rightInitTap: Boolean = false
    private var rightIsAnimatingIn: Boolean = false
    private var rightFadeOutRunnable: Runnable? = null

    override fun onDoubleTapStarted(portion: DisplayPortion) {
        if (DEBUG) Log.d(TAG, "onDoubleTapStarted called with portion = [$portion]")

        val shouldForward: Boolean =
            performListener?.getFastSeekDirection(portion)?.directionAsBoolean ?: return

        if (shouldForward) {
            rightInitTap = false
            rightSecondsView.stopAnimation()
        } else {
            leftInitTap = false
            leftSecondsView.stopAnimation()
        }
    }

    override fun onDoubleTapProgressDown(portion: DisplayPortion) {
        val shouldForward: Boolean =
            performListener?.getFastSeekDirection(portion)?.directionAsBoolean ?: return

        if (DEBUG)
            Log.d(TAG, "onDoubleTapProgressDown called with " + "shouldForward = [$shouldForward]")

        if (shouldForward) {
            handleRightOverlayTap()
        } else {
            handleLeftOverlayTap()
        }

        performListener?.onDoubleTap()
        performListener?.seek(forward = shouldForward)
    }

    private fun handleLeftOverlayTap() {
        // Cancel any pending fade out for left overlay
        leftFadeOutRunnable?.let {
            removeCallbacks(it)
            leftFadeOutRunnable = null
        }

        if (!leftInitTap) {
            // Reset seconds for new tap series
            leftSecondsView.seconds = 0
            leftInitTap = true

            // Only fade in if we're not already visible and animating
            if (!leftIsAnimatingIn && leftSecondsView.alpha < 1f) {
                leftIsAnimatingIn = true
                leftSecondsView.visibility = VISIBLE
                leftCircleClipTapView.visibility = VISIBLE
                leftSecondsView.startAnimation()
                leftSecondsView
                    .animate()
                    .alpha(1f)
                    .setDuration(200)
                    .withEndAction { leftIsAnimatingIn = false }
                    .start()
                leftCircleClipTapView.animate().alpha(1f).setDuration(200).start()
            }
        } else {
            // For rapid taps, ensure we're fully visible without re-animating
            if (leftSecondsView.alpha < 1f && !leftIsAnimatingIn) {
                leftSecondsView.alpha = 1f
                leftCircleClipTapView.alpha = 1f
                leftSecondsView.visibility = VISIBLE
                leftCircleClipTapView.visibility = VISIBLE
            }
        }

        leftSecondsView.seconds += seekSecondsSupplier.invoke()
    }

    private fun handleRightOverlayTap() {
        // Cancel any pending fade out for right overlay
        rightFadeOutRunnable?.let {
            removeCallbacks(it)
            rightFadeOutRunnable = null
        }

        if (!rightInitTap) {
            // Reset seconds for new tap series
            rightSecondsView.seconds = 0
            rightInitTap = true

            // Only fade in if we're not already visible and animating
            if (!rightIsAnimatingIn && rightSecondsView.alpha < 1f) {
                rightIsAnimatingIn = true
                rightSecondsView.visibility = VISIBLE
                rightCircleClipTapView.visibility = VISIBLE
                rightSecondsView.startAnimation()
                rightSecondsView
                    .animate()
                    .alpha(1f)
                    .setDuration(200)
                    .withEndAction { rightIsAnimatingIn = false }
                    .start()
                rightCircleClipTapView.animate().alpha(1f).setDuration(200).start()
            }
        } else {
            // For rapid taps, ensure we're fully visible without re-animating
            if (rightSecondsView.alpha < 1f && !rightIsAnimatingIn) {
                rightSecondsView.alpha = 1f
                rightCircleClipTapView.alpha = 1f
                rightSecondsView.visibility = VISIBLE
                rightCircleClipTapView.visibility = VISIBLE
            }
        }

        rightSecondsView.seconds += seekSecondsSupplier.invoke()
    }

    override fun onDoubleTapFinished() {
        if (DEBUG) Log.d(TAG, "onDoubleTapFinished called")

        // Handle left overlay fade out
        if (leftInitTap) {
            performListener?.onDoubleTapEnd()
            leftInitTap = false
            leftSecondsView.stopAnimation()

            // Cancel any existing fade out
            leftFadeOutRunnable?.let { removeCallbacks(it) }

            // Schedule fade out for left overlay after a delay
            leftFadeOutRunnable = Runnable {
                leftIsAnimatingIn = false
                leftSecondsView
                    .animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction { leftSecondsView.visibility = INVISIBLE }
                    .start()
                leftCircleClipTapView
                    .animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction { leftCircleClipTapView.visibility = INVISIBLE }
                    .start()
                leftFadeOutRunnable = null
            }
            postDelayed(leftFadeOutRunnable, 400)
        }

        // Handle right overlay fade out
        if (rightInitTap) {
            performListener?.onDoubleTapEnd()
            rightInitTap = false
            rightSecondsView.stopAnimation()

            // Cancel any existing fade out
            rightFadeOutRunnable?.let { removeCallbacks(it) }

            // Schedule fade out for right overlay after a delay
            rightFadeOutRunnable = Runnable {
                rightIsAnimatingIn = false
                rightSecondsView
                    .animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction { rightSecondsView.visibility = INVISIBLE }
                    .start()
                rightCircleClipTapView
                    .animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction { rightCircleClipTapView.visibility = INVISIBLE }
                    .start()
                rightFadeOutRunnable = null
            }
            postDelayed(rightFadeOutRunnable, 400)
        }
    }

    interface PerformListener {
        fun onDoubleTap()

        fun onDoubleTapEnd()
        /** Determines if the playback should forward/rewind or do nothing. */
        fun getFastSeekDirection(portion: DisplayPortion): FastSeekDirection

        fun seek(forward: Boolean)

        enum class FastSeekDirection(val directionAsBoolean: Boolean?) {
            NONE(null),
            FORWARD(true),
            BACKWARD(false)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    // GestureDetector.OnDoubleTapListener implementation
    override fun onSingleTapConfirmed(e: MotionEvent) = false

    override fun onDoubleTap(e: MotionEvent): Boolean {
        val width = width
        val tapX = e.x

        val portion =
            when {
                tapX < width * 0.33f -> DisplayPortion.LEFT_HALF
                tapX > width * 0.67f -> DisplayPortion.RIGHT_HALF
                else -> DisplayPortion.MIDDLE
            }

        if (portion != DisplayPortion.MIDDLE) {
            onDoubleTapStarted(portion)
            onDoubleTapProgressDown(portion)
            onDoubleTapFinished()
            return true
        }
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent) = false

    companion object {
        private const val TAG = "PlayerFastSeekOverlay"
        private val DEBUG = BuildConfig.DEBUG
    }
}
