package org.oxycblt.auxio.songs

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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
import org.oxycblt.auxio.ui.Accent
import org.oxycblt.auxio.ui.addIndicatorCallback
import org.oxycblt.auxio.ui.inflater

/**
 * A slimmed-down variant of [com.reddit.indicatorfastscroll.FastScrollerThumbView] designed
 * specifically for Auxio. Also fixes a memory leak that occurs from a bug fix they added.
 * @author OxygenCobalt
 */
class CobaltScrollThumb @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val thumbView: ViewGroup
    private val textView: TextView
    private val thumbAnim: SpringAnimation

    init {
        context.inflater.inflate(R.layout.fast_scroller_thumb_view, this, true)

        val accent = Accent.get().getStateList(context)

        thumbView = findViewById<ViewGroup>(R.id.fast_scroller_thumb).apply {
            textView = findViewById(R.id.fast_scroller_thumb_text)

            backgroundTintList = accent

            // Workaround for API 21 tint bug
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                (background as GradientDrawable).apply {
                    mutate()
                    color = accent
                }
            }
        }

        textView.apply {
            isVisible = true
            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_ThumbIndicator)
        }

        thumbAnim = SpringAnimation(thumbView, DynamicAnimation.TRANSLATION_Y).apply {
            spring = SpringForce().also {
                it.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
            }
        }

        visibility = View.INVISIBLE
        isActivated = false

        post {
            visibility = View.VISIBLE
        }
    }

    /**
     * Set up this view with a [FastScrollerView]. Should only be called once.
     */
    fun setup(scrollView: FastScrollerView) {
        scrollView.addIndicatorCallback { indicator, centerY, _ ->
            thumbAnim.animateToFinalPosition(centerY.toFloat() - (thumbView.measuredHeight / 2))

            if (indicator is FastScrollItemIndicator.Text) {
                textView.text = indicator.text
            }
        }

        @Suppress("ClickableViewAccessibility")
        scrollView.setOnTouchListener { _, event ->
            scrollView.onTouchEvent(event)
            scrollView.performClick()

            val action = event.actionMasked
            val actionValid = action != MotionEvent.ACTION_UP && action != MotionEvent.ACTION_CANCEL

            isActivated = if (actionValid) {
                isPointerOnItem(scrollView, event.y.toInt())
            } else {
                false
            }

            true
        }
    }

    /**
     * Hack that determines whether the pointer is currently on the [scrollView] or not.
     */
    private fun isPointerOnItem(scrollView: FastScrollerView, touchY: Int): Boolean {
        scrollView.children.forEach { child ->
            if (touchY in (child.top until child.bottom)) {
                return true
            }
        }

        return false
    }
}
