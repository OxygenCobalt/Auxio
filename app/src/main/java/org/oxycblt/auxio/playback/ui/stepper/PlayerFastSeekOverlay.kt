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
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import androidx.dynamicanimation.animation.SpringAnimation
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.oxycblt.auxio.R
import org.oxycblt.auxio.ui.Effect
import org.oxycblt.auxio.ui.UISettings

enum class Direction {
    FORWARDS,
    BACKWARDS,
}

@AndroidEntryPoint
class PlayerFastSeekOverlay(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs), GestureDetector.OnDoubleTapListener {
    private sealed interface OverlayState {
        data object Invisible : OverlayState

        data class Entering(val tap: SpringAnimation, val seconds: SpringAnimation) : OverlayState

        data class Wait(val runnable: Runnable) : OverlayState

        data class Exiting(val tap: SpringAnimation, val seconds: SpringAnimation) : OverlayState
    }

    @Inject lateinit var uiSettings: UISettings

    private var leftSecondsView: SecondsView
    private var rightSecondsView: SecondsView
    private var leftTapView: TapView
    private var rightTapView: TapView
    private val gestureDetector: GestureDetector

    var performListener: PerformListener? = null

    private val alphaSpring = Effect.FAST

    private var leftOverlayState: OverlayState = OverlayState.Invisible
    private var rightOverlayState: OverlayState = OverlayState.Invisible

    interface PerformListener {
        fun seek(direction: Direction)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.player_fast_seek_overlay, this, true)

        // Set up shape appearance based on UISettings similar to CoverView
        context.withStyledAttributes(attrs, R.styleable.PlayerFastSeekOverlay) {
            val shapeAppearanceRes =
                getResourceId(
                    R.styleable.PlayerFastSeekOverlay_shapeAppearance,
                    com.google.android.material.R.style.ShapeAppearance_Material3_Corner_Medium,
                )

            background =
                MaterialShapeDrawable().apply {
                    shapeAppearanceModel =
                        if (uiSettings.roundMode) {
                            ShapeAppearanceModel.builder(context, shapeAppearanceRes, -1).build()
                        } else {
                            ShapeAppearanceModel.builder().build()
                        }
                    // Set transparent background as we only want the clipping
                    fillColor = ColorStateList.valueOf(Color.TRANSPARENT)
                }
        }

        leftSecondsView = findViewById(R.id.left_seconds_view)
        rightSecondsView = findViewById(R.id.right_seconds_view)
        leftTapView = findViewById(R.id.left_circle_clip_tap_view)
        rightTapView = findViewById(R.id.right_circle_clip_tap_view)

        // Apply shape clipping to the overlay
        clipToOutline = true

        // Hide overlay elements by default
        leftSecondsView.alpha = 0f
        rightSecondsView.alpha = 0f
        leftTapView.alpha = 0f
        rightTapView.alpha = 0f

        // Set up gesture detection
        gestureDetector =
            GestureDetector(
                context,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onDown(e: MotionEvent) = true
                },
            )
        gestureDetector.setOnDoubleTapListener(this)

        addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
            leftTapView.updateArcSize(view)
            rightTapView.updateArcSize(view)
        }

        // Initialize directions for each side
        leftSecondsView.setDirection(Direction.BACKWARDS)
        rightSecondsView.setDirection(Direction.FORWARDS)
        leftTapView.setDirection(Direction.BACKWARDS) // isLeft = true for left side
        rightTapView.setDirection(Direction.FORWARDS) // isLeft = false for right side
    }

    override fun onTouchEvent(event: MotionEvent): Boolean =
        gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)

    override fun onSingleTapConfirmed(e: MotionEvent) = false

    private fun enter(
        secondsView: SecondsView,
        tapView: TapView,
        overlayState: (OverlayState) -> Unit,
    ) {
        // start animating the seconds icon
        secondsView.startAnimation()
        val secondsIn = alphaSpring.alpha(secondsView, 1.0f)
        val tapIn = alphaSpring.alpha(tapView, 1.0f)
        // will execute after a delay
        val out = Runnable { exit(secondsView, tapView, overlayState) }
        tapIn.addEndListener { _, cancelled, _, _ ->
            if (!cancelled) {
                postDelayed(out, EXIT_DELAY_MS)
                overlayState(OverlayState.Wait(out))
            }
        }
        overlayState(OverlayState.Entering(secondsIn, tapIn))
    }

    private fun exit(
        secondsView: SecondsView,
        tapView: TapView,
        overlayState: (OverlayState) -> Unit,
    ) {
        val secondsOut = alphaSpring.alpha(secondsView, 0.0f)
        val tapOut = alphaSpring.alpha(tapView, 0.0f)
        tapOut.addEndListener { _, cancelled, _, _ ->
            if (!cancelled) {
                secondsView.stopAnimation()
                secondsView.seconds = 0
                overlayState(OverlayState.Invisible)
            }
        }
        overlayState(OverlayState.Exiting(secondsOut, tapOut))
    }

    private fun tap(
        tappedSecondsView: SecondsView,
        tappedTapView: TapView,
        tappedOverlayState: OverlayState,
        setTappedOverlayState: (OverlayState) -> Unit,
        idleSecondsView: SecondsView,
        idleTapView: TapView,
        idleOverlayState: OverlayState,
        setIdleOverlayState: (OverlayState) -> Unit,
    ) {
        // (re)activate the side that was tapped on
        when (tappedOverlayState) {
            is OverlayState.Invisible -> {
                enter(tappedSecondsView, tappedTapView, setTappedOverlayState)
            }

            is OverlayState.Entering -> {
                // do nothing
            }

            is OverlayState.Wait -> {
                // refresh the exit timeout
                removeCallbacks(tappedOverlayState.runnable)
                postDelayed(tappedOverlayState.runnable, EXIT_DELAY_MS)
            }

            is OverlayState.Exiting -> {
                // cancel exit and re-enter
                tappedOverlayState.seconds.cancel()
                tappedOverlayState.tap.cancel()
                enter(tappedSecondsView, tappedTapView, setTappedOverlayState)
            }
        }

        // clear out the idle side if it's active
        when (idleOverlayState) {
            is OverlayState.Invisible -> {
                // nothing to clear
            }

            is OverlayState.Entering -> {
                // cancel enter and exit immediately
                idleOverlayState.seconds.cancel()
                idleOverlayState.tap.cancel()
                exit(idleSecondsView, idleTapView, setIdleOverlayState)
            }

            is OverlayState.Wait -> {
                // dont wait for an exit, exit immediately
                removeCallbacks(idleOverlayState.runnable)
                exit(idleSecondsView, idleTapView, setIdleOverlayState)
            }

            is OverlayState.Exiting -> {
                // nothing to clear
            }
        }

        // update seconds from tap - reset when exit anim clears
        // largely implicit shared understanding that seeking occurs by 10s
        // todo: probably share this constant
        tappedSecondsView.seconds += 10
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        val width = width
        val tapX = e.x

        val direction =
            when {
                tapX < width * 0.5 -> Direction.BACKWARDS
                tapX > width * 0.5 -> Direction.FORWARDS
                // dont care
                else -> return false
            }

        // a bit duplicated but already the generic `enter`/`exit` logic
        // requires a good amount of params and trying to share the helpe
        when (direction) {
            Direction.BACKWARDS -> {
                tap(
                    leftSecondsView,
                    leftTapView,
                    leftOverlayState,
                    { leftOverlayState = it },
                    rightSecondsView,
                    rightTapView,
                    rightOverlayState,
                    { rightOverlayState = it },
                )
            }

            Direction.FORWARDS -> {
                // forwards, we tap on the right
                tap(
                    rightSecondsView,
                    rightTapView,
                    rightOverlayState,
                    { rightOverlayState = it },
                    leftSecondsView,
                    leftTapView,
                    leftOverlayState,
                    { leftOverlayState = it },
                )
            }
        }

        performListener?.seek(direction)

        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent) = false

    private companion object {
        private const val EXIT_DELAY_MS = 500L
    }
}
