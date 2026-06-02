/*
 * Copyright (c) 2024 Auxio Project
 * CarFloatingControlsView.kt is part of Auxio.
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

package org.oxycblt.auxio.car.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Programmatic overlay view for car floating media controls. Uses large, TS18/head-unit friendly
 * touch targets. No XML or Compose dependency for overlay-safe usage.
 */
@SuppressLint("ViewConstructor")
class CarFloatingControlsView(
    context: Context,
    private val callbacks: Callbacks,
) : LinearLayout(context) {

    interface Callbacks {
        fun onDrag(deltaX: Int, deltaY: Int)
        fun onDragFinished(x: Int, y: Int)
        fun onPrevious()
        fun onPlayPause()
        fun onNext()
        fun onOpenAuxio()
        fun onStopRequested()
    }

    private val buttonSizePx: Int
    private var dragStartX = 0f
    private var dragStartY = 0f
    private var dragging = false
    private var lastTapTime = 0L
    private var tapCount = 0

    init {
        val density = context.resources.displayMetrics.density
        buttonSizePx = (BUTTON_SIZE_DP * density).toInt()
        val paddingPx = (PADDING_DP * density).toInt()

        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(paddingPx, paddingPx, paddingPx, paddingPx)

        val bg = GradientDrawable()
        bg.setColor(BG_COLOR)
        bg.cornerRadius = CORNER_RADIUS_DP * density
        background = bg

        addView(createDragHandle(context))
        addView(createButton(context, LABEL_PREV, DESC_PREV) { callbacks.onPrevious() })
        addView(createButton(context, LABEL_PLAY_PAUSE, DESC_PLAY_PAUSE) { callbacks.onPlayPause() })
        addView(createButton(context, LABEL_NEXT, DESC_NEXT) { callbacks.onNext() })
        addView(createButton(context, LABEL_OPEN, DESC_OPEN) { callbacks.onOpenAuxio() })
    }

    fun applyOpacity(percent: Int) {
        alpha = percent.coerceIn(CarOverlayPrefs.MIN_OPACITY, CarOverlayPrefs.MAX_OPACITY) / 100f
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createDragHandle(context: Context): View {
        val tv = TextView(context)
        tv.text = LABEL_DRAG
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, BUTTON_TEXT_SP)
        tv.gravity = Gravity.CENTER
        tv.setTextColor(Color.WHITE)
        tv.layoutParams = LayoutParams(buttonSizePx, buttonSizePx)
        tv.contentDescription = DESC_DRAG

        tv.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dragStartX = event.rawX
                    dragStartY = event.rawY
                    dragging = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - dragStartX).toInt()
                    val dy = (event.rawY - dragStartY).toInt()
                    // Compare squared distance to avoid sqrt; threshold is 10px
                    if (!dragging && (dx * dx + dy * dy > DRAG_THRESHOLD_SQ)) {
                        dragging = true
                    }
                    if (dragging) {
                        callbacks.onDrag(dx, dy)
                        dragStartX = event.rawX
                        dragStartY = event.rawY
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (dragging) {
                        callbacks.onDragFinished(
                            event.rawX.toInt(),
                            event.rawY.toInt()
                        )
                    } else {
                        v.performClick()
                        handleDragHandleTap()
                    }
                    dragging = false
                    true
                }
                else -> false
            }
        }
        return tv
    }

    private fun handleDragHandleTap() {
        val now = System.currentTimeMillis()
        if (now - lastTapTime < TRIPLE_TAP_WINDOW_MS) {
            tapCount++
        } else {
            tapCount = 1
        }
        lastTapTime = now
        if (tapCount >= TRIPLE_TAP_COUNT) {
            tapCount = 0
            callbacks.onStopRequested()
        }
    }

    private fun createButton(
        context: Context,
        label: String,
        description: String,
        onClick: () -> Unit,
    ): View {
        val tv = TextView(context)
        tv.text = label
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, BUTTON_TEXT_SP)
        tv.gravity = Gravity.CENTER
        tv.setTextColor(Color.WHITE)
        tv.layoutParams = LayoutParams(buttonSizePx, buttonSizePx)
        tv.contentDescription = description
        tv.isFocusable = true
        tv.isClickable = true
        tv.setOnClickListener { onClick() }
        return tv
    }

    private companion object {
        const val BUTTON_SIZE_DP = 64f
        const val PADDING_DP = 8f
        const val CORNER_RADIUS_DP = 12f
        const val BUTTON_TEXT_SP = 24f
        const val BG_COLOR = 0xCC1B1B1B.toInt()
        const val DRAG_THRESHOLD_SQ = 100 // 10px squared
        const val TRIPLE_TAP_WINDOW_MS = 600L
        const val TRIPLE_TAP_COUNT = 3

        const val LABEL_DRAG = "\u2807" // Braille pattern dots-123
        const val LABEL_PREV = "\u23EE"
        const val LABEL_PLAY_PAUSE = "\u23EF"
        const val LABEL_NEXT = "\u23ED"
        const val LABEL_OPEN = "\u266A"

        const val DESC_DRAG = "Drag to move, triple-tap to dismiss"
        const val DESC_PREV = "Previous track"
        const val DESC_PLAY_PAUSE = "Play or pause"
        const val DESC_NEXT = "Next track"
        const val DESC_OPEN = "Open Auxio"
    }
}
