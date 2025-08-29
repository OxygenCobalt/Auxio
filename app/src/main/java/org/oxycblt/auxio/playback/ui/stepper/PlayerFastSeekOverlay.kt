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
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.END
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.START
import androidx.constraintlayout.widget.ConstraintSet
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R

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

class PlayerFastSeekOverlay(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs), DoubleTapListener, GestureDetector.OnDoubleTapListener {

    private var secondsView: SecondsView
    private var circleClipTapView: CircleClipTapView
    private var rootConstraintLayout: ConstraintLayout

    private var wasForwarding: Boolean = false
    private val gestureDetector: GestureDetector

    init {
        LayoutInflater.from(context).inflate(R.layout.player_fast_seek_overlay, this, true)

        secondsView = findViewById(R.id.seconds_view)
        circleClipTapView = findViewById(R.id.circle_clip_tap_view)
        rootConstraintLayout = findViewById(R.id.root_constraint_layout)

        // Hide overlay elements by default
        secondsView.alpha = 0f
        circleClipTapView.alpha = 0f
        secondsView.visibility = INVISIBLE
        circleClipTapView.visibility = INVISIBLE

        // Set up gesture detection
        gestureDetector =
            GestureDetector(
                context,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onDown(e: MotionEvent) = true
                })
        gestureDetector.setOnDoubleTapListener(this)

        addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
            circleClipTapView.updateArcSize(view)
        }
    }

    private var performListener: PerformListener? = null

    fun performListener(listener: PerformListener?) = apply { performListener = listener }

    private var seekSecondsSupplier: () -> Int = { 0 }

    fun seekSecondsSupplier(supplier: (() -> Int)?) = apply {
        seekSecondsSupplier = supplier ?: { 0 }
    }

    // Indicates whether this (double) tap is the first of a series
    // Decides whether to call performListener.onAnimationStart or not
    private var initTap: Boolean = false

    // Track if we're currently animating to avoid redundant animations
    private var isAnimatingIn: Boolean = false
    private var fadeOutRunnable: Runnable? = null

    override fun onDoubleTapStarted(portion: DisplayPortion) {
        if (DEBUG) Log.d(TAG, "onDoubleTapStarted called with portion = [$portion]")

        initTap = false

        secondsView.stopAnimation()
    }

    override fun onDoubleTapProgressDown(portion: DisplayPortion) {
        val shouldForward: Boolean =
            performListener?.getFastSeekDirection(portion)?.directionAsBoolean ?: return

        if (DEBUG)
            Log.d(
                TAG,
                "onDoubleTapProgressDown called with " +
                    "shouldForward = [$shouldForward], " +
                    "wasForwarding = [$wasForwarding], " +
                    "initTap = [$initTap], ")

        // Cancel any pending fade out
        fadeOutRunnable?.let {
            removeCallbacks(it)
            fadeOutRunnable = null
        }

        /*
         * Check if a initial tap occurred or if direction was switched
         */
        if (!initTap || wasForwarding != shouldForward) {
            // Reset seconds and update position
            secondsView.seconds = 0
            changeConstraints(shouldForward)
            circleClipTapView.updatePosition(!shouldForward)
            secondsView.setForwarding(shouldForward)

            wasForwarding = shouldForward

            if (!initTap) {
                initTap = true
                // Only fade in if we're not already visible and animating
                if (!isAnimatingIn && secondsView.alpha < 1f) {
                    isAnimatingIn = true
                    secondsView.visibility = VISIBLE
                    circleClipTapView.visibility = VISIBLE
                    secondsView
                        .animate()
                        .alpha(1f)
                        .setDuration(200)
                        .withEndAction { isAnimatingIn = false }
                        .start()
                    circleClipTapView.animate().alpha(1f).setDuration(200).start()
                }
            }
        } else {
            // For rapid taps, ensure we're fully visible without re-animating
            if (secondsView.alpha < 1f && !isAnimatingIn) {
                secondsView.alpha = 1f
                circleClipTapView.alpha = 1f
                secondsView.visibility = VISIBLE
                circleClipTapView.visibility = VISIBLE
            }
        }

        performListener?.onDoubleTap()

        secondsView.seconds += seekSecondsSupplier.invoke()
        performListener?.seek(forward = shouldForward)
    }

    override fun onDoubleTapFinished() {
        if (DEBUG) Log.d(TAG, "onDoubleTapFinished called with initTap = [$initTap]")

        if (initTap) performListener?.onDoubleTapEnd()
        initTap = false

        secondsView.stopAnimation()

        // Cancel any existing fade out
        fadeOutRunnable?.let { removeCallbacks(it) }

        // Schedule fade out overlay after a delay
        fadeOutRunnable = Runnable {
            isAnimatingIn = false
            secondsView
                .animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction { secondsView.visibility = INVISIBLE }
                .start()
            circleClipTapView
                .animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction { circleClipTapView.visibility = INVISIBLE }
                .start()
            fadeOutRunnable = null
        }
        postDelayed(fadeOutRunnable, 400)
    }

    private fun changeConstraints(forward: Boolean) {
        val constraintSet = ConstraintSet()
        with(constraintSet) {
            clone(rootConstraintLayout)
            clear(secondsView.id, if (forward) START else END)
            connect(
                secondsView.id, if (forward) END else START, PARENT_ID, if (forward) END else START)
            secondsView.startAnimation()
            applyTo(rootConstraintLayout)
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
