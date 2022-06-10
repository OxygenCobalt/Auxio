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
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getColorStateListSafe

/**
 * The internal drawable used by Auxio's images. Do not use this outside of this module.
 *
 * This enables a few features:
 * - Automatic tinting to the correct image tint
 * - Automatic sizing to HALF of the canvas.
 *
 * @author OxygenCobalt
 */
class StyledDrawable(context: Context, private val src: Drawable) : Drawable() {
    init {
        // Re-tint the drawable to something that will play along with the background.
        // Done here because this call (and nothing else) miraculously works on Lollipop devices
        DrawableCompat.setTintList(src, context.getColorStateListSafe(R.color.sel_on_cover_bg))
    }

    override fun draw(canvas: Canvas) {
        src.bounds.set(canvas.clipBounds)
        val adjustWidth = src.bounds.width() / 4
        val adjustHeight = src.bounds.height() / 4
        src.bounds.set(
            adjustWidth,
            adjustHeight,
            src.bounds.width() - adjustWidth,
            src.bounds.height() - adjustHeight)
        src.draw(canvas)
    }

    override fun setAlpha(alpha: Int) {
        src.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        src.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}
