/*
 * Copyright (c) 2026 Auxio Project
 * UserAwarePagerCallback.kt is part of Auxio.
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

import androidx.viewpager2.widget.ViewPager2

/**
 * A [ViewPager2.OnPageChangeCallback] that only fires [userCallback] when a user actually enagages
 * in a swipe action. [ViewPager2] doesn't normally supply this so we have to do it via a stateful
 * object.
 *
 * @param viewPager [ViewPager2] that this will be attached to (must do this separately)
 * @param userCallback The callback to run on a user-driven swipe
 * @author Alexander Capehart (OxygenCobalt), Idea from GPT-5.5 (Rewritten)
 */
class UserAwarePagerCallback(
    private val viewPager: ViewPager2,
    private val userCallback: (Int) -> Unit,
) : ViewPager2.OnPageChangeCallback() {
    private var user = false

    override fun onPageScrollStateChanged(state: Int) {
        when (state) {
            ViewPager2.SCROLL_STATE_DRAGGING -> {
                user = !viewPager.isFakeDragging
            }

            ViewPager2.SCROLL_STATE_IDLE -> {
                user = false
            }

            else -> {
                // ignore
            }
        }
    }

    override fun onPageSelected(position: Int) {
        if (user) {
            userCallback(position)
        }
    }

    /** Attach this to the passed [viewPager]. */
    fun attach() {
        viewPager.registerOnPageChangeCallback(this)
    }

    /** Detach this from the passed [viewPager]. */
    fun release() {
        viewPager.unregisterOnPageChangeCallback(this)
    }
}
