package org.oxycblt.auxio.playback.ui.swiper

import androidx.viewpager2.widget.ViewPager2

/**
 * A [ViewPager2.OnPageChangeCallback] that only fires [userCallback] when a user actually
 * enagages in a swipe action. [ViewPager2] doesn't normally supply this so we have to do it
 * via a stateful object.
 *
 * @param viewPager [ViewPager2] that this will be attached to (must do this separately)
 * @param userCallback The callback to run on a user-driven swipe
 *
 * @author Alexander Capehart (OxygenCobalt), Idea from GPT-5.5 (Rewritten)
 */
class UserAwarePagerCallback(private val viewPager: ViewPager2, private val userCallback: (Int) -> Unit) : ViewPager2.OnPageChangeCallback() {
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

    /**
     * Attach this to the passed [viewPager].
     */
    fun attach() {
        viewPager.registerOnPageChangeCallback(this)
    }

    /**
     * Detach this from the passed [viewPager].
     */
    fun release() {
        viewPager.unregisterOnPageChangeCallback(this)
    }
}