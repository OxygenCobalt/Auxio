package org.oxycblt.auxio.songs

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.ui.Accent
import org.oxycblt.auxio.ui.resolveAttr
import org.oxycblt.auxio.ui.toColor
import kotlin.math.ceil
import kotlin.math.min

/**
 * A view that allows for quick scrolling through a [RecyclerView] with many items. Unlike other
 * fast-scrollers, this one displays indicators instead of simply a scroll bar.
 * This code is fundamentally an adaptation of Reddit's IndicatorFastScroll, albeit specialized
 * towards Auxio. The original library is here: https://github.com/reddit/IndicatorFastScroll/
 * @author OxygenCobalt
 */
class FastScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : LinearLayout(context, attrs, defStyleAttr) {

    // --- BASIC SETUP ---

    private var mRecycler: RecyclerView? = null
    private var mThumb: FastScrollThumb? = null
    private var mGetItem: ((Int) -> Char)? = null

    // --- INDICATORS ---

    /** Representation of a single Indicator character in the view */
    data class Indicator(val char: Char, val pos: Int)

    private var indicators = listOf<Indicator>()

    private val indicatorText: TextView
    private val activeColor = Accent.get().color.toColor(context)
    private val inactiveColor = android.R.attr.textColorSecondary.resolveAttr(context)

    // --- STATE ---

    private var hasPostedItemUpdate = false
    private var lastPos = -1

    init {
        isFocusableInTouchMode = true
        isClickable = true
        gravity = Gravity.CENTER

        val textPadding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 4F, resources.displayMetrics
        )

        // Making this entire view a TextView will cause distortions due to the touch calculations
        // using a height that is not wrapped to the text.
        indicatorText = AppCompatTextView(context).apply {
            gravity = Gravity.CENTER
            includeFontPadding = false

            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_FastScroll)
            setLineSpacing(textPadding, lineSpacingMultiplier)
            setTextColor(inactiveColor)
        }

        addView(indicatorText)
    }

    /**
     * Set up this view with a [RecyclerView] and a corresponding [FastScrollThumb].
     */
    fun setup(recycler: RecyclerView, thumb: FastScrollThumb, getItem: (Int) -> Char) {
        check(mRecycler == null) { "Only set up this view once." }

        mRecycler = recycler
        mThumb = thumb
        mGetItem = getItem

        postIndicatorUpdate()
    }

    // --- INDICATOR UPDATES ---

    private fun postIndicatorUpdate() {
        if (!hasPostedItemUpdate) {
            hasPostedItemUpdate = true

            post {
                val recycler = requireNotNull(mRecycler)

                if (recycler.isAttachedToWindow && recycler.adapter != null) {
                    updateIndicators()
                }

                hasPostedItemUpdate = false
            }
        }
    }

    private fun updateIndicators() {
        val recycler = requireNotNull(mRecycler)
        val getItem = requireNotNull(mGetItem)

        indicators = 0.until(recycler.adapter!!.itemCount).mapNotNull { pos ->
            Indicator(getItem(pos), pos)
        }.distinctBy { it.char }

        val textHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 14F, resources.displayMetrics
        )

        // If the scroller size is too small to contain all the entries, truncate entries
        // so that the fast scroller entries fit.
        val maxEntries = height / textHeight

        if (indicators.size > maxEntries.toInt()) {
            val truncateInterval = ceil(indicators.size / maxEntries).toInt()

            check(truncateInterval > 1) {
                "Needed to truncate, but truncateInterval was 1 or lower anyway"
            }

            logD("More entries than screen space, truncating by $truncateInterval.")

            indicators = indicators.filterIndexed { index, _ ->
                index % truncateInterval == 0
            }
        }

        indicatorText.apply {
            tag = indicators
            text = indicators.joinToString("\n") { it.char.toString() }
        }
    }

    // --- TOUCH ---

    @Suppress("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        performClick()

        val success = handleTouch(event.action, event.y.toInt())

        // Depending on the results, update the visibility of the thumb and the pressed state of
        // this view.
        isPressed = success
        mThumb?.isActivated = success

        return success
    }

    @Suppress("UNCHECKED_CAST")
    private fun handleTouch(action: Int, touchY: Int): Boolean {
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            indicatorText.setTextColor(inactiveColor)
            lastPos = -1

            return false
        }

        if (touchY in (indicatorText.top until indicatorText.bottom)) {
            // Try to roughly caculate which indicator the user is currently touching [Since the
            val textHeight = indicatorText.height / indicators.size
            val indicatorIndex = min(
                (touchY - indicatorText.top) / textHeight, indicators.lastIndex
            )

            val centerY = y.toInt() + (textHeight / 2) + (indicatorIndex * textHeight)

            val touchedIndicator = indicators[indicatorIndex]

            selectIndicator(touchedIndicator, centerY)

            return true
        }

        return false
    }

    private fun selectIndicator(indicator: Indicator, indicatorCenterY: Int) {
        if (indicator.pos != lastPos) {
            lastPos = indicator.pos
            indicatorText.setTextColor(activeColor)

            // Stop any scroll momentum and snap-scroll to the position
            mRecycler?.apply {
                stopScroll()
                (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                    indicator.pos, 0
                )
            }

            mThumb?.jumpTo(indicator, indicatorCenterY)

            performHapticFeedback(
                if (Build.VERSION.SDK_INT >= 27) {
                    // Dragging across a scroller is closer to moving a text handle
                    HapticFeedbackConstants.TEXT_HANDLE_MOVE
                } else {
                    HapticFeedbackConstants.KEYBOARD_TAP
                }
            )
        }
    }
}
