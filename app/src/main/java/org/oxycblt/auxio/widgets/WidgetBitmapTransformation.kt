/*
 * Copyright (c) 2024 Auxio Project
 * WidgetBitmapTransformation.kt is part of Auxio.
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
 
package org.oxycblt.auxio.widgets

import android.content.res.Resources
import android.graphics.Bitmap
import coil.size.Size
import coil.transform.Transformation
import kotlin.math.sqrt

class WidgetBitmapTransformation(reduce: Float) : Transformation {
    private val metrics = Resources.getSystem().displayMetrics
    private val sw = metrics.widthPixels
    private val sh = metrics.heightPixels
    // Cap memory usage at 1.5 times the size of the display
    // 1.5 * 4 bytes/pixel * w * h ==> 6 * w * h
    // https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/services/appwidget/java/com/android/server/appwidget/AppWidgetServiceImpl.java
    // Of course since OEMs randomly patch this check, we give a lot of slack.
    private val maxBitmapArea = (1.5 * sw * sh / reduce).toInt()

    override val cacheKey: String
        get() = "WidgetBitmapTransformation:${maxBitmapArea}"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        if (size !== Size.ORIGINAL) {
            // The widget loading stack basically discards the size parameter since there's no
            // sane value from the get-go, all this transform does is actually dynamically apply
            // the size cap so this transform must always be zero.
            throw IllegalArgumentException("WidgetBitmapTransformation requires original size.")
        }
        val inputArea = input.width * input.height
        if (inputArea != maxBitmapArea) {
            val scale = sqrt(maxBitmapArea / inputArea.toDouble())
            val newWidth = (input.width * scale).toInt()
            val newHeight = (input.height * scale).toInt()
            return Bitmap.createScaledBitmap(input, newWidth, newHeight, true)
        }
        return input
    }
}
