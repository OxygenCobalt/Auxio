/*
 * Copyright (c) 2025 Auxio Project
 * SecondsView.kt is part of Auxio.
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

package org.oxycblt.auxio.playback.ui.stepper

import android.content.Context
import android.provider.Settings
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.PlayerFastSeekSecondsViewBinding
import org.oxycblt.auxio.ui.Effect

class SecondsView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    var seconds: Int = 0
        set(value) {
            binding.tvSeconds.text =
                context.resources.getQuantityString(R.plurals.fmt_seconds, value, value)
            field = value
        }

    // Done as a field so that we don't have to compute on each tab if animations are enabled
    private val animationsEnabled =
        Settings.System.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1F,
        ) != 0F

    private val alphaSpring = Effect.FAST
    private var animations: List<SpringAnimation> = listOf()

    val binding = PlayerFastSeekSecondsViewBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = VERTICAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        reset()
    }

    fun setDirection(direction: Direction) {
        // easy flip if in the other direction, dont have to change icons or anim order
        binding.triangleContainer.rotation = if (direction == Direction.FORWARDS) 0f else 180f
    }

    fun startAnimation() {
        if (animations.any { it.isRunning }) {
            // animations ongoing
            // we never try to stop ongoing animations when this is
            // called again, as that will likely result in the indicator always
            // resetting
            // TODO: might want to change this
            return
        }
        reset()
        if (animationsEnabled) {
            cycle()
        } else {
            // If no animations are enable show the arrow(s) without animation
            showWithoutAnimation()
        }
    }

    fun stopAnimation() {
        animations.forEach { it.cancel() }
        reset()
    }

    private fun reset() {
        binding.icon1.alpha = 0f
        binding.icon2.alpha = 0f
        binding.icon3.alpha = 0f
    }

    private fun showWithoutAnimation() {
        binding.icon1.alpha = 1f
        binding.icon2.alpha = 1f
        binding.icon3.alpha = 1f
    }

    // cycle animation for the 3 icons
    private val cyclicAlpha =
        floatArrayOf(
            1.00f,
            0.25f,
            0.25f,
            1.00f,
            1.00f,
            0.25f,
            0.25f,
            1.00f,
            1.00f,
            0.25f,
            0.25f,
            1.00f,
            0.25f,
            0.25f,
            0.25f,
        )

    private fun cycle(at: Int = 0) {
        animations.forEach { it.cancel() }
        val row = at * 3
        val one = alphaSpring.alpha(binding.icon1, cyclicAlpha[row])
        val two = alphaSpring.alpha(binding.icon2, cyclicAlpha[row + 1])
        val three = alphaSpring.alpha(binding.icon3, cyclicAlpha[row + 2])
        val endListener =
            DynamicAnimation.OnAnimationEndListener { _, cancelled, _, _ ->
                if (!cancelled) {
                    cycle((at + 1) % 5)
                }
            }
        when {
            cyclicAlpha[row] != binding.icon1.alpha -> {
                one.addEndListener(endListener)
            }
            cyclicAlpha[row + 1] != binding.icon2.alpha -> {
                two.addEndListener(endListener)
            }
            cyclicAlpha[row + 2] != binding.icon3.alpha -> {
                three.addEndListener(endListener)
            }
        }
        animations = listOf(one, two, three)
    }
}
