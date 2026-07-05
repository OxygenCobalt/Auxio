package org.oxycblt.auxio.playback.ui.button

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.AttrRes
import com.google.android.material.R
import com.google.android.material.button.MaterialButton
import org.oxycblt.auxio.util.lazyReflectedMethod
import java.lang.reflect.Method
import kotlin.math.max

/**
 * Companion scalable button to [ScaledPlaybackButtonGroup], see that.
 *
 * @author Codex w/cleanup + cognitive ownership by Alexander Capehart (OxygenCobalt)
 */
class ScaledPlaybackButton
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.materialButtonStyle,
) : WidthFixMaterialButton(context, attrs, defStyleAttr) {
    private var baseMetrics: BaseMetrics
    private var lastScale: Float? = null

    init {
        // We need to capture the buttons original state w /o any transforms
        // so that our scaling applies to the correct dimens
        val contentWidth = paddingStart + paddingEnd + iconSize
        val contentHeight = paddingTop + paddingBottom + iconSize
        baseMetrics =  BaseMetrics(
                width = max(minimumWidth, contentWidth),
                height = max(minimumHeight, contentHeight),
                minimumWidth = minimumWidth,
                minimumHeight = minimumHeight,
                paddingStart = paddingStart,
                paddingTop = paddingTop,
                paddingEnd = paddingEnd,
                paddingBottom = paddingBottom,
                iconSize = iconSize,
                strokeWidth = strokeWidth,
            )
    }

    val baseWidth: Int
        get() = baseMetrics.width

    val baseHeight: Int
        get() = baseMetrics.height

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

    }

    fun applyPlaybackScale(scale: Float) {
        val scaledWidth = baseMetrics.width.scale(scale)
        val scaledHeight = baseMetrics.height.scale(scale)
        // MaterialButtonGroup mutates child widths while pressed so neighbors can shrink/grow.
        // Only rewrite the baseline when the scale itself changes.
        if (lastScale == scale) {
            // Nothing to change
            return
        }
        lastScale = scale

        METHOD_RECOVER_ORIG_PARAMS.invoke(this)
        val recoveredParams = layoutParams
        if (recoveredParams != null) {
            // Existing LayoutParams, update
            recoveredParams.width = scaledWidth
            recoveredParams.height = scaledHeight
        } else {
            // Otherwise make new LayoutParams
            layoutParams = ViewGroup.LayoutParams(scaledWidth, scaledHeight)
        }

        // Scale minimum width/height
        minimumWidth = minOf(baseMetrics.minimumWidth.scale(scale), scaledWidth)
        minimumHeight = minOf(baseMetrics.minimumHeight.scale(scale), scaledHeight)

        // Scale padding
        setPaddingRelative(
            baseMetrics.paddingStart.scale(scale),
            baseMetrics.paddingTop.scale(scale),
            baseMetrics.paddingEnd.scale(scale),
            baseMetrics.paddingBottom.scale(scale),
        )

        // Scale icon size
        iconSize = baseMetrics.iconSize.scale(scale)

        // Scale stroke width if necessary
        // A bit inaccurate (they actually step from 3, 2, 1) but better to interpolate honestly
        strokeWidth = if (baseMetrics.strokeWidth > 0) max(1, baseMetrics.strokeWidth.scale(scale)) else 0
    }

    private fun Int.scale(scale: Float) = (this * scale).toInt()

    private data class BaseMetrics(
        val width: Int,
        val height: Int,
        val minimumWidth: Int,
        val minimumHeight: Int,
        val paddingStart: Int,
        val paddingTop: Int,
        val paddingEnd: Int,
        val paddingBottom: Int,
        val iconSize: Int,
        val strokeWidth: Int,
    )

    private companion object {
        private val METHOD_RECOVER_ORIG_PARAMS: Method by lazyReflectedMethod(MaterialButton::class, "recoverOriginalLayoutParams")
    }
}
