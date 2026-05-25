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

    override fun transformPage(page: View, position: Float) {
        // Implements a Material3-style carousel over a standard ViewPager2.
        //
        // ViewPager2 normally lays out pages side-by-side via LinearLayoutManager.
        // To create a carousel effect, all pages are pinned to the same X position
        // using translationX, and then MaskableFrameLayout's mask is adjusted per
        // page to reveal the correct portion of each cover.
        val maskable = page as? MaskableFrameLayout ?: return
        val width = page.width.toFloat()
        val height = page.height.toFloat()

        if (width <= 0f || height <= 0f) {
            return
        }

        // Pin the page to its natural position so LinearLayoutManager's side-by-side
        // placement does not affect the visual arrangement.
        page.translationX = -position * width

        // Offscreen pages (-1 or beyond) are made invisible so their touch areas
        // do not interfere with the playback stepper gesture detection.
        page.isInvisible = position <= -1f || position >= 1f

        page.alpha = 1f

        // Normalise position to p in [0, 1] where 0 = fully on-screen and 1 = fully off-screen.
        // Prev page (position < 0): p = |position|.
        // Next page (position > 0): p = 1 - position (maps 0=fully visible to 1=fully hidden).
        val p =
            when {
                position < 0f -> -position
                position > 0f -> 1f - position
                else -> 0f
            }.coerceIn(0f, 1f)

        // The gap between adjacent covers is eased in/out over the first and last GAP_BREAKPOINT
        // fraction of transition progress. A constant gap would appear or disappear instantly;
        // a linear gap would morph throughout the transition. The piecewise linear approach below
        // keeps the gap at maxGap for most of the transition while smoothly ramping at the edges.
        val gapEnterBreakpoint = GAP_BREAKPOINT
        val gapExitBreakpoint = 1f - GAP_BREAKPOINT
        val maxGap = width * 0.08f

        val gap =
            when {
                p < gapEnterBreakpoint -> maxGap * (p / gapEnterBreakpoint)
                p > gapExitBreakpoint -> maxGap * ((1f - p) / (1f - gapExitBreakpoint))
                else -> maxGap
            }

        // The available width for covers after the gap is subtracted. The reveal fraction
        // determines how much of the incoming cover is exposed. Because gap is already factored
        // into `available`, the left boundary of the incoming mask automatically incorporates
        // the gap without needing an explicit gap term.
        val reveal =
            ((p - gapEnterBreakpoint) / (gapExitBreakpoint - gapEnterBreakpoint)).coerceIn(0f, 1f)

        val available = width - gap
        val incomingWidth = available * reveal
        val outgoingWidth = available - incomingWidth

        val rect =
            when {
                // Previous cover: fill from left up to outgoingWidth.
                position < 0f -> RectF(0f, 0f, outgoingWidth, height)
                // Incoming (next) cover: fill from right down to incomingWidth.
                position > 0f -> RectF(width - incomingWidth, 0f, width, height)
                // Current cover: full extent.
                else -> RectF(0f, 0f, width, height)
            }

        maskable.setMaskRectF(rect)

        // Subtle parallax: shift the cover image inside the mask so that the visible
        // portion tracks slightly ahead of the mask boundary, giving a sense of depth.
        val content = page.getChildAt(0)
        val maskCenter = (rect.left + rect.right) / 2f
        val pageCenter = width / 2f
        content.translationX = (maskCenter - pageCenter) * PARALLAX
    }

    private companion object {
        const val GAP_BREAKPOINT = 0.18f
        const val PARALLAX = 0.2f
    }
}
