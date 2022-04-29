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
 
package org.oxycblt.auxio.playback

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatImageButton
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getDimenSizeSafe
import org.oxycblt.auxio.util.getDrawableSafe

/**
 * An [AppCompatImageButton] designed for the buttons used in the playback display.
 *
 * Auxio's playback buttons have never followed the typical 24dp icon size that all other UI
 * elements do, mostly because those icons just look bad at that size with all the gobs of
 * whitespace surrounding them. So, this view resizes the icons to a fixed 32dp in a way that
 * doesn't require a whole new icon set.
 *
 * This view also enables use of an "indicator", which is a dot that can denote when a button is
 * active. This is useful for the shuffle/repeat buttons, as at times highlighting them is not
 * enough to differentiate them.
 */
class PlaybackButton
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    AppCompatImageButton(context, attrs, defStyleAttr) {
    private val iconSize = context.getDimenSizeSafe(R.dimen.size_playback_icon)
    private val centerMatrix = Matrix()
    private val matrixSrc = RectF()
    private val matrixDst = RectF()

    private val indicatorDrawable = context.getDrawableSafe(R.drawable.ui_indicator)
    private var hasIndicator = false
        set(value) {
            field = value
            invalidate()
        }

    init {
        val size = context.getDimenSizeSafe(R.dimen.size_btn_small)
        minimumWidth = size
        minimumHeight = size
        scaleType = ScaleType.MATRIX
        setBackgroundResource(R.drawable.ui_large_unbounded_ripple)

        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.PlaybackButton)
        hasIndicator = styledAttrs.getBoolean(R.styleable.PlaybackButton_hasIndicator, false)
        styledAttrs.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        imageMatrix =
            centerMatrix.apply {
                reset()
                drawable?.let { drawable ->
                    // Android is too good to allow us to set a fixed image size, so we instead need
                    // to define a matrix to scale an image directly.

                    // First scale the icon up to the desired size.
                    matrixSrc.set(
                        0f,
                        0f,
                        drawable.intrinsicWidth.toFloat(),
                        drawable.intrinsicHeight.toFloat())
                    matrixDst.set(0f, 0f, iconSize.toFloat(), iconSize.toFloat())
                    centerMatrix.setRectToRect(matrixSrc, matrixDst, Matrix.ScaleToFit.CENTER)

                    // Then actually center it into the icon, which the previous call does not
                    // actually do.
                    centerMatrix.postTranslate(
                        (measuredWidth - iconSize) / 2f, (measuredHeight - iconSize) / 2f)
                }
            }

        // Put the indicator right below the icon.
        val x = (measuredWidth - indicatorDrawable.intrinsicWidth) / 2
        val y = ((measuredHeight - iconSize) / 2) + iconSize

        indicatorDrawable.bounds.set(
            x, y, x + indicatorDrawable.intrinsicWidth, y + indicatorDrawable.intrinsicHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // I would use onDrawForeground but apparently that isn't called by Lollipop devices.
        // This is not referenced in the documentation at all.
        if (hasIndicator && isActivated) {
            indicatorDrawable.draw(canvas)
        }
    }
}
