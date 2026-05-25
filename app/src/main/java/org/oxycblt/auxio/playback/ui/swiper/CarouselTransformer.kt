/*
 * Copyright (c) 2026 Auxio Project
 * CarouselTransformer.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.ui.swiper

import android.graphics.RectF
import android.view.View
import androidx.core.view.isInvisible
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.carousel.MaskableFrameLayout

class CarouselTransformer : ViewPager2.PageTransformer {
    private val hiddenRect = RectF()

    override fun transformPage(page: View, position: Float) {
        // drafted by codex mostly due to the insane complexity of abusing a viewpager2
        // into a m3 carousel like thing
        // cleaned/rewritten/documented by me for cognitive ownership
        val maskable = page as MaskableFrameLayout
        val width = page.width.toFloat()
        val height = page.height.toFloat()

        if (width <= 0f || height <= 0f) {
            //
            return
        }

        // pin the page
        // normally the page's layout is controlled by linearlayoutmanager which we dont
        // like since it means we cant arrange them in the carousel
        // so we use translationX to fix them to the same pos so we can remask them however
        // we want to create our effect
        page.translationX = -position * width

        // Make sure despite the pinning offscreen pages are noninteractable
        // Otherwise funky android touch logic kicks in and breaks the playback stepper
        page.isInvisible = position <= -1f || position >= 1f

        page.alpha = 1f

        val p =
            when {
                // prev page, just abs
                // so in this case p will be 1.0 -> 0.0, so 1.0 when fully outset and 0.0 when inset
                // we must conform to this!
                position < 0f -> -position
                // next page, take the inverse actually
                // remember, this must also be normalized to outset = 1.0, inset = 0.0
                // so take the opposite end (1f - pos)
                position > 0f -> 1f - position
                else -> 0f
            }

        // breakpoints for the gap
        // the thing is that really we have to encode the "gap" an equation such that
        // the masking effect looks like a contiguous transform of adjacent covers.
        // if we just add a fixed gap it'll appear instantly, and any p * gap or similar
        // logic will morph over time. even polynomials will morph as well. hence this
        // really funky piecewise function

        // basically at the first and last 18% of progress we should grow and shrink the gap
        // respectively
        val gapEnterBreakpoint = GAP_BREAKPOINT // when growth ends
        val gapExitBreakpoint = 1f - GAP_BREAKPOINT // symmetric, when shrink starts
        val maxGap = width * 0.08f

        val gap =
            when {
                // gap is not fully grown yet, so we do the gap size * how close we are to a fully
                // grown gap
                p < gapEnterBreakpoint -> maxGap * (p / gapEnterBreakpoint)

                // gap should be shrinking, so we need to downscale maxgap by how close we are
                // actually to the end
                p > gapExitBreakpoint -> maxGap * ((1f - p) / (1f - gapExitBreakpoint))

                // not growing or shrinking
                else -> maxGap
            }

        // then we need to get how much we actually want to reveal the incoming covers post-gap
        // so first normalize p past gapEnterBreakpoint, then normalize it to the range
        // between the gap transform breakpoints, so this range:
        // [enter] - [reveal] - [exit]
        // if p is inside [enter] or [exit] we snap to 0f/1f respectively, so i.e
        // dont reveal more/less than we need to if the gap being phased out already
        // handles it
        val reveal =
            ((p - gapEnterBreakpoint) / ((gapExitBreakpoint) - gapEnterBreakpoint)).coerceIn(0f, 1f)

        // alloc space for incoming cover, gap, outgoing cover
        val available = width - gap
        val incomingWidth = available * reveal
        val outgoingWidth = available - incomingWidth

        val rect =
            when {
                // previous cover -> fit to outgoing width to the right
                // gap is already factored into the available calculation outgoingWidth depends
                // on
                position < 0f -> {
                    RectF(0f, 0f, outgoingWidth, height)
                }

                // incoming cover -> mask to incoming width towards the left
                // note:
                // left = width - incomingWidth = width - (width - **gap**) * reveal
                // this means that we automatically inset for the gap without including
                // it as an explicit term
                // and since gap itself is scaled via the piecewise function this makes
                // it so that the gap automatically appears
                position > 0f -> {
                    RectF(width - incomingWidth, 0f, width, height)
                }

                // current cover, mask to normal
                else -> {
                    RectF(0f, 0f, width, height)
                }
            }

        maskable.setMaskRectF(rect)

        // internal page parallax
        // this does assume the cover item setup but thats fine
        val content = page.getChildAt(0)
        val maskCenter = (rect.left + rect.right) / 2f
        val pageCenter = width / 2f
        // easy parallax where left content moves more left,
        // right content moves more to the right
        content.translationX = (maskCenter - pageCenter) * PARALLAX
    }

    private companion object {
        const val GAP_BREAKPOINT = 0.18f
        const val PARALLAX = 0.2f
    }
}
