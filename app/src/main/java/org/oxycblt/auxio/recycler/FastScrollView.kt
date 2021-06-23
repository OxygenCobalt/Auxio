package org.oxycblt.auxio.recycler

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ViewFastScrollBinding
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.ui.Accent
import org.oxycblt.auxio.ui.canScroll
import org.oxycblt.auxio.ui.inflater
import org.oxycblt.auxio.ui.resolveAttr
import org.oxycblt.auxio.ui.toColor
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * A view that allows for quick scrolling through a [RecyclerView] with many items. Unlike other
 * fast-scrollers, this one displays indicators and a thumb instead of simply a scroll bar.
 * This code is fundamentally an adaptation of Reddit's IndicatorFastScroll, albeit specialized
 * towards Auxio. The original library is here: https://github.com/reddit/IndicatorFastScroll/
 * @author OxygenCobalt
 */
class FastScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : ConstraintLayout(context, attrs, defStyleAttr) {

    // --- UI ---

    private val binding = ViewFastScrollBinding.inflate(context.inflater, this, true)
    private val thumbAnim: SpringAnimation

    // --- RECYCLER ---

    private var mRecycler: RecyclerView? = null
    private var mGetItem: ((Int) -> Char)? = null
    private val mObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() = postIndicatorUpdate()

        override fun onItemRangeChanged(
            positionStart: Int,
            itemCount: Int,
            payload: Any?
        ) = postIndicatorUpdate()

        override fun onItemRangeInserted(
            positionStart: Int,
            itemCount: Int
        ) = onChanged()

        override fun onItemRangeMoved(
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int
        ) = onChanged()

        override fun onItemRangeRemoved(
            positionStart: Int,
            itemCount: Int
        ) = onChanged()
    }

    // --- INDICATORS ---

    private data class Indicator(val char: Char, val pos: Int)

    private var indicators = listOf<Indicator>()
    private val activeColor = Accent.get().color.toColor(context)
    private val inactiveColor = android.R.attr.textColorSecondary.resolveAttr(context)

    // --- STATE ---

    private var hasPostedItemUpdate = false
    private var wasValidTouch = false
    private var lastPos = -1

    init {
        isFocusableInTouchMode = true
        isClickable = true

        thumbAnim = SpringAnimation(binding.scrollThumb, DynamicAnimation.TRANSLATION_Y).apply {
            spring = SpringForce().also {
                it.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
            }
        }

        // Prevent the disappear animation from being displayed on startup by making the thumb
        // invisible, it will be made visible once the animation ends
        binding.scrollThumb.visibility = View.INVISIBLE

        postDelayed(200) {
            binding.scrollThumb.visibility = View.VISIBLE
        }
    }

    /**
     * Set up this view with a [RecyclerView]. [getItem] is called when the first character
     * of a piece of data is needed.
     */
    fun setup(recycler: RecyclerView, getItem: (Int) -> Char) {
        check(mRecycler == null) { "Only set up this view once." }

        mRecycler = recycler
        mGetItem = getItem

        recycler.adapter?.registerAdapterDataObserver(mObserver)

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
                    binding.scrollIndicatorText.requestLayout()
                }

                // Hide this view if there is nothing to scroll
                isVisible = recycler.canScroll()

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

        // Then set it as the unified TextView text, for efficiency purposes.
        binding.scrollIndicatorText.text = indicators.joinToString("\n") { indicator ->
            indicator.char.toString()
        }
    }

    // --- TOUCH ---

    @Suppress("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        performClick()

        val success = handleTouch(event.action, event.x.roundToInt(), event.y.roundToInt())

        // Depending on the results, update the visibility of the thumb and the pressed state of
        // this view.
        binding.scrollThumb.isActivated = success
        binding.scrollIndicatorText.isPressed = success

        return success
    }

    private fun handleTouch(action: Int, touchX: Int, touchY: Int): Boolean {
        when (action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                binding.scrollIndicatorText.setTextColor(inactiveColor)
                wasValidTouch = false
                lastPos = -1

                return false
            }

            // Since this view is unified between the thumb and the indicators, we have
            // to check if the initial pointer position was in the indicators to prevent the
            // scroll from being triggered outside its bounds.
            MotionEvent.ACTION_DOWN -> {
                wasValidTouch = binding.scrollIndicatorText.contains(touchX, touchY)
            }
        }

        // Try to figure out which indicator the pointer has landed on
        if (binding.scrollIndicatorText.containsY(touchY) && wasValidTouch) {
            // Get the touch position in regards to the TextView and the rough text height
            val indicatorTouchY = touchY - binding.scrollIndicatorText.top
            val textHeight = binding.scrollIndicatorText.height / indicators.size

            // Use that to calculate the indicator index, if the calculation is
            // invalid just ignore it.
            val index = min(indicatorTouchY / textHeight, indicators.lastIndex)

            // Also calculate the rough center position of the indicator for the scroll thumb
            val centerY = binding.scrollIndicatorText.y + (textHeight / 2) + (index * textHeight)

            selectIndicator(indicators[index], centerY)

            return true
        }

        return false
    }

    private fun selectIndicator(indicator: Indicator, centerY: Float) {
        if (indicator.pos != lastPos) {
            lastPos = indicator.pos
            binding.scrollIndicatorText.setTextColor(activeColor)

            // Stop any scroll momentum and snap-scroll to the position
            mRecycler?.apply {
                stopScroll()
                (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(indicator.pos, 0)
            }

            // Update the thumb position/text
            binding.scrollThumbText.text = indicator.char.toString()
            thumbAnim.animateToFinalPosition(centerY - (binding.scrollThumb.measuredHeight / 2))

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

    private fun View.contains(x: Int, y: Int): Boolean {
        return x in (left until right) && containsY(y)
    }

    private fun View.containsY(y: Int): Boolean {
        return y in (top until bottom)
    }
}
