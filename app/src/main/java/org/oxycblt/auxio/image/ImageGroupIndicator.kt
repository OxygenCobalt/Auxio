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
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getColorStateListSafe

/**
 * Represents the animated indicator that is shown when [ImageGroup] is active.
 *
 * AnimationDrawable, the drawable that this view is backed by, is really finicky. Basically, it has
 * to be set as the drawable of an ImageView to work correctly, and will just not draw anywhere
 * else. As a result, we have to create a custom view that emulates [StyledImageView] and
 * [StyledDrawable] simultaneously while also managing the animation state.
 *
 * @author OxygenCobalt
 */
class ImageGroupIndicator
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr) {
    private val centerMatrix = Matrix()
    private val matrixSrc = RectF()
    private val matrixDst = RectF()

    init {
        scaleType = ScaleType.MATRIX
        setImageResource(R.drawable.ic_animated_equalizer)
        imageTintList = context.getColorStateListSafe(R.color.sel_on_cover_bg)
        background =
            MaterialShapeDrawable().apply {
                fillColor = context.getColorStateListSafe(R.color.sel_cover_bg)
            }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // Instead of using StyledDrawable (which would break the animation), we scale
        // up the animated icon using a matrix. This is okay, as it won't fall victim to
        // the same issues that come with using a matrix in StyledImageView
        imageMatrix =
            centerMatrix.apply {
                reset()
                drawable?.let { drawable ->
                    // Android is too good to allow us to set a fixed image size, so we instead need
                    // to define a matrix to scale an image directly.

                    val iconWidth = measuredWidth / 2f
                    val iconHeight = measuredHeight / 2f

                    // First scale the icon up to the desired size.
                    matrixSrc.set(
                        0f,
                        0f,
                        drawable.intrinsicWidth.toFloat(),
                        drawable.intrinsicHeight.toFloat())
                    matrixDst.set(0f, 0f, iconWidth, iconHeight)
                    centerMatrix.setRectToRect(matrixSrc, matrixDst, Matrix.ScaleToFit.CENTER)

                    // Then actually center it into the icon, which the previous call does not
                    // actually do.
                    centerMatrix.postTranslate(
                        (measuredWidth - iconWidth) / 2f, (measuredHeight - iconHeight) / 2f)
                }
            }
    }

    override fun setActivated(activated: Boolean) {
        super.setActivated(activated)
        val icon = drawable as AnimationDrawable
        if (activated) {
            icon.start()
        } else {
            icon.stop()
        }
    }
}
