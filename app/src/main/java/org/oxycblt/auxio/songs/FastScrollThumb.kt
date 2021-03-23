package org.oxycblt.auxio.songs

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ViewScrollThumbBinding
import org.oxycblt.auxio.ui.Accent
import org.oxycblt.auxio.ui.inflater

/**
 * The companion thumb for [FastScrollView]. This does not need any setup, instead pass it as an
 * argument to [FastScrollView.setup].
 * This code is fundamentally an adaptation of Reddit's IndicatorFastScroll, albeit specialized
 * towards Auxio. The original library is here: https://github.com/reddit/IndicatorFastScroll/
 * @author OxygenCobalt
 */
class FastScrollThumb @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val thumbAnim: SpringAnimation
    private val binding = ViewScrollThumbBinding.inflate(context.inflater, this, true)

    init {
        val accent = Accent.get().getStateList(context)

        binding.thumbLayout.apply {
            backgroundTintList = accent

            // Workaround for API 21 tint bug
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                (background as GradientDrawable).apply {
                    mutate()
                    color = accent
                }
            }
        }

        binding.thumbText.apply {
            isVisible = true
            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_ThumbIndicator)
        }

        thumbAnim = SpringAnimation(binding.thumbLayout, DynamicAnimation.TRANSLATION_Y).apply {
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
     * Make the thumb jump to a new position and update its text to the given [indicator].
     * This is not meant for use outside of the main [FastScrollView] code. Do not use it.
     */
    fun jumpTo(indicator: FastScrollView.Indicator, centerY: Int) {
        binding.thumbText.text = indicator.char.toString()
        thumbAnim.animateToFinalPosition(
            centerY.toFloat() - (binding.thumbLayout.measuredHeight / 2)
        )
    }
}
