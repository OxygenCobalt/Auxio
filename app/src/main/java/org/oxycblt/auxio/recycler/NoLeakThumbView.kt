@file:Suppress("MemberVisibilityCanBePrivate")

package org.oxycblt.auxio.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.reddit.indicatorfastscroll.FastScrollerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.ui.accent
import org.oxycblt.auxio.ui.toColor

/**
 * A semi-copy, semi-custom implementation of [com.reddit.indicatorfastscroll.FastScrollerThumbView]
 * that fixes a memory leak that occurs from a bug fix they added. All credit goes to the authors of
 * the fast scroll library.
 * <a href="https://github.com/reddit/IndicatorFastScroll"> Link to repo </a>
 * @author Reddit, OxygenCobalt
 */
class NoLeakThumbView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.indicatorFastScrollerThumbStyle
) : ConstraintLayout(context, attrs, defStyleAttr),
    FastScrollerView.ItemIndicatorSelectedCallback {

    private val thumbColor = ColorStateList.valueOf(accent.first.toColor(context))
    private val iconColor = R.color.background.toColor(context)
    private val textAppearanceRes = R.style.TextAppearance_ThumbIndicator
    private val textColor = R.color.background.toColor(context)

    private val thumbView: ViewGroup
    private val textView: TextView
    private val iconView: ImageView

    private val isSetup: Boolean get() = (fastScrollerView != null)
    private var fastScrollerView: FastScrollerView? = null

    private val thumbAnimation: SpringAnimation

    init {
        LayoutInflater.from(context).inflate(
            R.layout.fast_scroller_thumb_view, this, true
        )

        thumbView = findViewById(R.id.fast_scroller_thumb)
        textView = thumbView.findViewById(R.id.fast_scroller_thumb_text)
        iconView = thumbView.findViewById(R.id.fast_scroller_thumb_icon)

        isActivated = false
        isVisible = false

        applyStyle()

        thumbAnimation = SpringAnimation(thumbView, DynamicAnimation.TRANSLATION_Y).apply {
            spring = SpringForce().apply {
                dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setupWithFastScroller(fastScrollerView: FastScrollerView) {
        check(!isSetup) { "Only set this view's FastScrollerView once!" }

        this.fastScrollerView = fastScrollerView

        fastScrollerView.itemIndicatorSelectedCallbacks += this

        // FastScrollerView's "onItemIndicatorTouched" [Which I would've used here] is internal,
        // so instead I just use a setOnTouchListener to get the same-ish effect.
        fastScrollerView.setOnTouchListener { _, event ->
            fastScrollerView.onTouchEvent(event)
            fastScrollerView.performClick()

            isVisible = true

            if (event.actionMasked == MotionEvent.ACTION_UP ||
                event.actionMasked == MotionEvent.ACTION_CANCEL
            ) {
                isActivated = false
                return@setOnTouchListener true
            }

            isActivated = isPointerOnItem(fastScrollerView, event.y.toInt())

            true
        }
    }

    /**
     * Hack so that I can detect when the pointer is on the FastScrollerView's items
     * without using onItemIndicatorTouched (Which is internal)
     * @author OxygenCobalt
     */
    private fun isPointerOnItem(fastScrollerView: FastScrollerView, touchY: Int): Boolean {
        fun View.containsY(y: Int) = y in (top until bottom)

        var consumed = false

        fastScrollerView.apply {
            children.forEach { view ->
                if (view.containsY(touchY)) {
                    consumed = true
                    return@forEach
                }
            }
        }

        return consumed
    }

    private fun applyStyle() {
        thumbView.backgroundTintList = thumbColor

        if (Build.VERSION.SDK_INT == 21) {
            // Workaround for 21 background tint bug
            (thumbView.background as GradientDrawable).apply {
                mutate()
                color = thumbColor
            }
        }

        TextViewCompat.setTextAppearance(textView, textAppearanceRes)
        textView.setTextColor(textColor)
        iconView.imageTintList = ColorStateList.valueOf(iconColor)
    }

    override fun onItemIndicatorSelected(
        indicator: FastScrollItemIndicator,
        indicatorCenterY: Int,
        itemPosition: Int
    ) {
        val thumbTargetY = indicatorCenterY.toFloat() - (thumbView.measuredHeight / 2)
        thumbAnimation.animateToFinalPosition(thumbTargetY)

        when (indicator) {
            is FastScrollItemIndicator.Text -> {
                textView.isVisible = true
                iconView.isVisible = false

                textView.text = indicator.text
            }
            is FastScrollItemIndicator.Icon -> {
                textView.isVisible = false
                iconView.isVisible = true

                iconView.setImageResource(indicator.iconRes)
            }
        }
    }
}
