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
 
package org.oxycblt.auxio.coil

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.util.getColorSafe
import org.oxycblt.auxio.util.stateList

/**
 * An [AppCompatImageView] that applies the specified cornerRadius attribute if the user has enabled
 * the "Round album covers" option. We don't round album covers by default as it desecrates album
 * artwork, but if the user desires it we do have an option to enable it.
 */
class RoundableImageView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr) {
    init {
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.RoundableImageView)
        val cornerRadius = styledAttrs.getDimension(R.styleable.RoundableImageView_cornerRadius, 0f)
        styledAttrs.recycle()

        background =
            MaterialShapeDrawable().apply {
                setCornerSize(cornerRadius)
                fillColor = context.getColorSafe(android.R.color.transparent).stateList
            }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // Use clipToOutline and a background drawable to crop images. While Coil's transformation
        // could theoretically be used to round corners, the corner radius is dependent on the
        // dimensions of the image, which will result in inconsistent corners across different
        // album covers unless we resize all covers to be the same size. clipToOutline is both
        // cheaper and more elegant.
        if (!isInEditMode) {
            val settingsManager = SettingsManager.getInstance()
            clipToOutline = settingsManager.roundCovers
        }
    }
}
