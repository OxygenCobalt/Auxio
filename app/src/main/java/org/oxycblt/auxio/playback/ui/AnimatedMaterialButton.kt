/*
 * Copyright (c) 2022 Auxio Project
 * AnimatedMaterialButton.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.ui

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import org.oxycblt.auxio.ui.MaterialCornerAnim
import org.oxycblt.auxio.ui.RippleFixMaterialButton
import timber.log.Timber as L

/**
 * A [MaterialButton] that automatically morphs from a circle to a squircle shape appearance when
 * [isActivated] changes.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class AnimatedMaterialButton : RippleFixMaterialButton {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    private var animator: Animator? = null
    private val anim = MaterialCornerAnim(context)

    override fun setActivated(activated: Boolean) {
        super.setActivated(activated)

        // Activated -> Squircle (30% Radius), Inactive -> Circle (50% Radius)
        val targetRadius = if (activated) 0.3f else 0.5f
        if (!isLaidOut) {
            // Not laid out, initialize it without animation before drawing.
            L.d("Not laid out, immediately updating corner radius")
            shapeAppearanceModel = shapeAppearanceModel.withCornerSize { it.width() * targetRadius }
            return
        }

        L.d("Starting corner radius animation")
        animator?.cancel()
        animator = anim.animate(this, width * targetRadius).also { it.start() }
    }
}
