/*
 * Copyright (c) 2022 Auxio Project
 * FastScrollPopupView.kt is part of Auxio.
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
 
package org.oxycblt.auxio.home.fastscroll

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import androidx.core.widget.TextViewCompat
import com.google.android.material.textview.MaterialTextView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getAttrColorCompat
import org.oxycblt.auxio.util.getDimenPixels
import org.oxycblt.auxio.util.isRtl

/**
 * A [MaterialTextView] that displays the popup indicator used in FastScrollRecyclerView
 *
 * @author Alexander Capehart (OxygenCobalt), Hai Zhang
 */
class FastScrollPopupView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleRes: Int = 0) :
    MaterialTextView(context, attrs, defStyleRes) {
    init {
        minimumWidth = context.getDimenPixels(R.dimen.fast_scroll_popup_min_width)
        minimumHeight = context.getDimenPixels(R.dimen.fast_scroll_popup_min_height)

        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_Auxio_HeadlineLarge)
        setTextColor(context.getAttrColorCompat(R.attr.colorOnSecondary))
        ellipsize = TextUtils.TruncateAt.MIDDLE
        gravity = Gravity.CENTER
        includeFontPadding = false

        alpha = 0f
        elevation = context.getDimenPixels(R.dimen.elevation_normal).toFloat()
        background = FastScrollPopupDrawable(context)
    }

    private class FastScrollPopupDrawable(context: Context) : Drawable() {
        private val paint: Paint =
            Paint().apply {
                isAntiAlias = true
                color = context.getAttrColorCompat(R.attr.colorSecondary).defaultColor
                style = Paint.Style.FILL
            }

        private val path = Path()
        private val matrix = Matrix()

        private val paddingStart = context.getDimenPixels(R.dimen.fast_scroll_popup_padding_start)
        private val paddingEnd = context.getDimenPixels(R.dimen.fast_scroll_popup_padding_end)

        override fun draw(canvas: Canvas) {
            canvas.drawPath(path, paint)
        }

        override fun onBoundsChange(bounds: Rect) {
            updatePath()
        }

        override fun onLayoutDirectionChanged(layoutDirection: Int): Boolean {
            updatePath()
            return true
        }

        @Suppress("DEPRECATION")
        override fun getOutline(outline: Outline) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> outline.setPath(path)

                // Paths don't need to be convex on android Q, but the API was mislabeled and so
                // we still have to use this method.
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> outline.setConvexPath(path)
                else ->
                    if (!path.isConvex) {
                        // The outline path must be convex before Q, but we may run into floating
                        // point errors caused by calculations involving sqrt(2) or OEM differences,
                        // so in this case we just omit the shadow instead of crashing.
                        super.getOutline(outline)
                    }
            }
        }

        override fun getPadding(padding: Rect): Boolean {
            if (isRtl) {
                padding[paddingEnd, 0, paddingStart] = 0
            } else {
                padding[paddingStart, 0, paddingEnd] = 0
            }

            return true
        }

        override fun isAutoMirrored(): Boolean = true
        override fun setAlpha(alpha: Int) {}
        override fun setColorFilter(colorFilter: ColorFilter?) {}
        override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

        private fun updatePath() {
            val r = bounds.height().toFloat() / 2
            val w = (r + SQRT2 * r).coerceAtLeast(bounds.width().toFloat())

            path.apply {
                reset()

                // Draw the left pill shape
                val o1X = w - SQRT2 * r
                arcToSafe(r, r, r, 90f, 180f)
                arcToSafe(o1X, r, r, -90f, 45f)

                // Draw the right arrow shape
                val point = r / 5
                val o2X = w - SQRT2 * point
                arcToSafe(o2X, r, point, -45f, 90f)
                arcToSafe(o1X, r, r, 45f, 45f)

                close()
            }

            matrix.apply {
                reset()
                if (isRtl) setScale(-1f, 1f, w / 2, 0f)
                postTranslate(bounds.left.toFloat(), bounds.top.toFloat())
            }

            path.transform(matrix)
        }

        private fun Path.arcToSafe(
            centerX: Float,
            centerY: Float,
            radius: Float,
            startAngle: Float,
            sweepAngle: Float
        ) {
            arcTo(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
                startAngle,
                sweepAngle,
                false)
        }
    }

    private companion object {
        // Pre-calculate sqrt(2)
        const val SQRT2 = 1.4142135f
    }
}
