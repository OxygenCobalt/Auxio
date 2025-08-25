/*
 * Copyright (c) 2023 Auxio Project
 * ControlledCoverView.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.OnDoubleTapListener
import android.view.GestureDetector.OnGestureListener
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.animation.DecelerateInterpolator
import androidx.annotation.AttrRes
import kotlin.math.abs
import org.oxycblt.auxio.image.CoverView
import org.oxycblt.auxio.util.isRtl

class ControlledCoverView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    CoverView(context, attrs, defStyleAttr), OnGestureListener, OnDoubleTapListener {

    private val gestureDetector =
        GestureDetector(context, this).apply { setOnDoubleTapListener(this@ControlledCoverView) }
    private val viewConfig = ViewConfiguration.get(context)

    var onSwipeListener: OnSwipeListener? = null

    // Circle clip animation properties
    private val backgroundPaint =
        Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            color = 0x80000000.toInt() // Increased opacity for visibility
        }

    private val shapePath = Path()
    private var arcSize: Float = 80f
    private var isLeftSide = true
    private var animationProgress = 0f
    private var fadeAnimator: ValueAnimator? = null
    private var fadeOutAnimator: ValueAnimator? = null
    private var lastTapTime = 0L
    private var isAnimating = false

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        return gestureDetector.onGenericMotionEvent(event) || super.onGenericMotionEvent(event)
    }

    override fun onDown(e: MotionEvent): Boolean = true

    override fun onShowPress(e: MotionEvent) = Unit

    override fun onSingleTapUp(e: MotionEvent): Boolean = false

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean = false

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        e1 ?: return false
        val diffY = e2.y - e1.y
        val diffX = e2.x - e1.x
        if (abs(diffX) > abs(diffY) &&
            abs(diffX) > viewConfig.scaledTouchSlop &&
            abs(velocityX) > viewConfig.scaledMinimumFlingVelocity) {
            if (diffX > 0) {
                onSwipeRight()
            } else {
                onSwipeLeft()
            }
            return true
        }
        return false
    }

    override fun onLongPress(e: MotionEvent) = Unit

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean = false

    override fun onDoubleTap(e: MotionEvent): Boolean {
        val width = width
        val tapX = e.x

        when {
            tapX < width * 0.33f -> {
                showCircleAnimation(isLeft = true)
                if (isRtl) {
                    onSwipeListener?.onStepForward()
                } else {
                    onSwipeListener?.onStepBack()
                }
                return true
            }
            tapX > width * 0.67f -> {
                showCircleAnimation(isLeft = false)
                if (isRtl) {
                    onSwipeListener?.onStepBack()
                } else {
                    onSwipeListener?.onStepForward()
                }
                return true
            }
            else -> return false
        }
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean = false

    private fun onSwipeRight() {
        onSwipeListener?.run { if (isRtl) onSwipeNext() else onSwipePrevious() }
    }

    private fun onSwipeLeft() {
        onSwipeListener?.run { if (isRtl) onSwipePrevious() else onSwipeNext() }
    }

    private fun showCircleAnimation(isLeft: Boolean) {
        Log.d(
            "ControlledCoverView",
            "showCircleAnimation: isLeft = $isLeft, isAnimating = $isAnimating")

        val currentTime = System.currentTimeMillis()
        val timeSinceLastTap = currentTime - lastTapTime
        lastTapTime = currentTime

        val isSwitchingSides = isLeftSide != isLeft

        // Update the path if switching sides or initializing
        if (isSwitchingSides || !isAnimating) {
            isLeftSide = isLeft
            arcSize = height / 11.4f
            updatePathShape()
        }

        // If animation is already running and it's a rapid tap (within 600ms)
        if (isAnimating && timeSinceLastTap < 600 && !isSwitchingSides) {
            // Cancel any fade out that might be scheduled
            fadeOutAnimator?.cancel()
            removeCallbacks(fadeOutRunnable)

            // Keep the animation at full opacity or fade it back in if it was fading out
            if (animationProgress < 1f) {
                fadeAnimator?.cancel()
                fadeAnimator =
                    ValueAnimator.ofFloat(animationProgress, 1f).apply {
                        duration = ((1f - animationProgress) * 200).toLong()
                        interpolator = DecelerateInterpolator()
                        addUpdateListener { animation ->
                            animationProgress = animation.animatedValue as Float
                            invalidate()
                        }
                        start()
                    }
            }

            // Schedule fade out after a delay
            postDelayed(fadeOutRunnable, 600)
        } else {
            // Start fresh animation (either first tap, timeout, or side switch)
            isAnimating = true

            // Cancel any existing animations
            fadeAnimator?.cancel()
            fadeOutAnimator?.cancel()
            removeCallbacks(fadeOutRunnable)

            // If switching sides during rapid taps, do a quick crossfade
            if (isSwitchingSides && timeSinceLastTap < 600 && animationProgress > 0) {
                // Quick fade to new side
                fadeAnimator =
                    ValueAnimator.ofFloat(0f, 1f).apply {
                        duration = 150
                        interpolator = DecelerateInterpolator()
                        addUpdateListener { animation ->
                            animationProgress = animation.animatedValue as Float
                            invalidate()
                        }
                        start()
                    }
            } else {
                // Normal fade in
                fadeAnimator =
                    ValueAnimator.ofFloat(0f, 1f).apply {
                        duration = 250
                        interpolator = DecelerateInterpolator()
                        addUpdateListener { animation ->
                            animationProgress = animation.animatedValue as Float
                            invalidate()
                        }
                        start()
                    }
            }

            // Schedule fade out
            postDelayed(fadeOutRunnable, 600)
        }
    }

    private val fadeOutRunnable = Runnable {
        fadeOutAnimator?.cancel()
        fadeOutAnimator =
            ValueAnimator.ofFloat(animationProgress, 0f).apply {
                duration = 200
                addUpdateListener { animation ->
                    animationProgress = animation.animatedValue as Float
                    invalidate()

                    if (animationProgress == 0f) {
                        isAnimating = false
                    }
                }
                start()
            }
    }

    private fun updatePathShape() {
        val halfWidth = width * 0.5f

        shapePath.reset()

        val w = if (isLeftSide) 0f else width.toFloat()
        val f = if (isLeftSide) 1 else -1

        shapePath.moveTo(w, 0f)
        shapePath.lineTo(f * (halfWidth - arcSize) + w, 0f)
        shapePath.quadTo(
            f * (halfWidth + arcSize) + w,
            height.toFloat() / 2,
            f * (halfWidth - arcSize) + w,
            height.toFloat())
        shapePath.lineTo(w, height.toFloat())

        shapePath.close()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        arcSize = h / 11.4f
        updatePathShape()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        // Draw the animation overlay on top of everything
        if (animationProgress > 0f) {
            Log.d(
                "ControlledCoverView",
                "Drawing animation: progress = $animationProgress, arcSize = $arcSize")

            // Set alpha based on animation progress
            backgroundPaint.alpha = (0x80 * animationProgress).toInt()

            // Draw the curved shape without clipping
            canvas.drawPath(shapePath, backgroundPaint)
        }
    }

    interface OnSwipeListener {

        fun onSwipePrevious()

        fun onSwipeNext()

        fun onStepBack()

        fun onStepForward()
    }
}
