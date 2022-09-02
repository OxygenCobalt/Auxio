/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.image

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.ImageViewCompat
import com.google.android.material.shape.MaterialShapeDrawable
import kotlin.math.max
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.getColorCompat
import org.oxycblt.auxio.util.getDrawableCompat

/**
 * View that displays the playback indicator. Nominally emulates [StyledImageView], but is
 * much different internally as an animated icon can't be wrapped within StyledDrawable without
 * causing insane issues.
 * @author OxygenCobalt
 */
class IndicatorView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr) {
    private val playingIndicatorDrawable =
        context.getDrawableCompat(R.drawable.ic_playing_indicator_24) as AnimationDrawable

    private val pausedIndicatorDrawable = context.getDrawableCompat(R.drawable.ic_paused_indicator_24)

    private val indicatorMatrix = Matrix()
    private val indicatorMatrixSrc = RectF()
    private val indicatorMatrixDst = RectF()

    private val settings = Settings(context)

    var cornerRadius = 0f
        set(value) {
            field = value
            (background as? MaterialShapeDrawable)?.let { bg ->
                if (settings.roundMode) {
                    bg.setCornerSize(value)
                } else {
                    bg.setCornerSize(0f)
                }
            }
        }

    init {
        // Use clipToOutline and a background drawable to crop images. While Coil's transformation
        // could theoretically be used to round corners, the corner radius is dependent on the
        // dimensions of the image, which will result in inconsistent corners across different
        // album covers unless we resize all covers to be the same size. clipToOutline is both
        // cheaper and more elegant. As a side-note, this also allows us to re-use the same
        // background for both the tonal background color and the corner rounding.
        clipToOutline = true
        background =
            MaterialShapeDrawable().apply {
                fillColor = context.getColorCompat(R.color.sel_cover_bg)
                setCornerSize(cornerRadius)
            }

        scaleType = ScaleType.MATRIX
        ImageViewCompat.setImageTintList(this, context.getColorCompat(R.color.sel_on_cover_bg))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val iconSize = max(measuredWidth, measuredHeight) / 2

        imageMatrix =
            indicatorMatrix.apply {
                reset()
                drawable?.let { drawable ->
                    // Android is too good to allow us to set a fixed image size, so we instead need
                    // to define a matrix to scale an image directly.

                    // First scale the icon up to the desired size.
                    indicatorMatrixSrc.set(
                        0f,
                        0f,
                        drawable.intrinsicWidth.toFloat(),
                        drawable.intrinsicHeight.toFloat())
                    indicatorMatrixDst.set(0f, 0f, iconSize.toFloat(), iconSize.toFloat())
                    indicatorMatrix.setRectToRect(
                        indicatorMatrixSrc, indicatorMatrixDst, Matrix.ScaleToFit.CENTER)

                    // Then actually center it into the icon, which the previous call does not
                    // actually do.
                    indicatorMatrix.postTranslate(
                        (measuredWidth - iconSize) / 2f, (measuredHeight - iconSize) / 2f)
                }
            }
    }

    var isPlaying: Boolean
        get() = drawable == playingIndicatorDrawable
        set(value) {
            if (value) {
                playingIndicatorDrawable.start()
                setImageDrawable(playingIndicatorDrawable)
            } else {
                playingIndicatorDrawable.stop()
                setImageDrawable(pausedIndicatorDrawable)
            }
        }
}
