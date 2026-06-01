package org.oxycblt.auxio.car.overlay

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.roundToInt

internal class CarFloatingControlsView(
    context: Context,
    private val prefs: CarOverlayPrefs,
    private val callbacks: Callbacks,
) : LinearLayout(context) {

    interface Callbacks {
        fun onDrag(dx: Int, dy: Int)
        fun onDragFinished()
        fun onPrevious()
        fun onPlayPause()
        fun onNext()
        fun onOpenAuxio()
        fun onStopRequested()
    }

    private var lastRawX = 0f
    private var lastRawY = 0f
    private var dragging = false
    private var tapCount = 0
    private var lastTapAt = 0L

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        alpha = prefs.opacityPercent / 100f
        setBackgroundColor(Color.argb(220, 18, 18, 18))
        minimumHeight = dp(prefs.buttonDp)
        contentDescription = "Auxio floating media controls"
        elevation = dp(8).toFloat()
        setPadding(dp(6), dp(4), dp(6), dp(4))

        addView(button("⠿", "Drag floating controls") { callbacks.onStopRequested() }.also { handle ->
            handle.setOnTouchListener { _, event -> handleTouch(event) }
        })
        addView(button("⏮", "Previous") { callbacks.onPrevious() })
        addView(button("⏯", "Play or pause") { callbacks.onPlayPause() })
        addView(button("⏭", "Next") { callbacks.onNext() })
        addView(button("♪", "Open Auxio") { callbacks.onOpenAuxio() })
    }

    private fun button(label: String, description: String, onClick: () -> Unit): TextView {
        return TextView(context).apply {
            text = label
            textSize = 28f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            contentDescription = description
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
            layoutParams = ViewGroup.LayoutParams(dp(prefs.buttonDp), dp(prefs.buttonDp))
        }
    }

    private fun handleTouch(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastRawX = event.rawX
                lastRawY = event.rawY
                dragging = false
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (event.rawX - lastRawX).roundToInt()
                val dy = (event.rawY - lastRawY).roundToInt()
                if (dx != 0 || dy != 0) {
                    dragging = true
                    callbacks.onDrag(dx, dy)
                    lastRawX = event.rawX
                    lastRawY = event.rawY
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (dragging) {
                    callbacks.onDragFinished()
                } else {
                    val now = System.currentTimeMillis()
                    tapCount = if (now - lastTapAt <= TRIPLE_TAP_WINDOW_MS) tapCount + 1 else 1
                    lastTapAt = now
                    if (tapCount >= 3) {
                        tapCount = 0
                        callbacks.onStopRequested()
                    }
                }
                return true
            }
        }
        return false
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).roundToInt()

    companion object {
        private const val TRIPLE_TAP_WINDOW_MS = 900L
    }
}
