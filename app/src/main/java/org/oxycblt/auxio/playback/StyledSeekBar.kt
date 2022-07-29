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
 
package org.oxycblt.auxio.playback

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.slider.Slider
import kotlin.math.max
import org.oxycblt.auxio.databinding.ViewSeekBarBinding
import org.oxycblt.auxio.util.formatDuration
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.textSafe

/**
 * A wrapper around [Slider] that shows not only position and duration values, but also basically
 * hacks in behavior consistent with a normal SeekBar in a way that will not crash the app.
 *
 * SeekBar, like most android OS components, is a version-specific mess that requires constant hacks
 * on older versions. Instead, we use the more "modern" [Slider] component, but it is not designed
 * for the job that Auxio's progress bar has. It does not gracefully degrade when positions don't
 * make sense (which happens incredibly often), it just crashes the entire app, which is insane but
 * also checks out for something more meant for configuration than seeking.
 *
 * Instead, we wrap it in a safe class that hopefully implements enough sanity checks to not crash
 * the app or result in blatantly janky behavior. Mostly.
 *
 * TODO: Add smooth seeking
 *
 * @author OxygenCobalt
 */
class StyledSeekBar
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) :
    ForcedLTRFrameLayout(context, attrs, defStyleAttr),
    Slider.OnSliderTouchListener,
    Slider.OnChangeListener {
    private val binding = ViewSeekBarBinding.inflate(context.inflater, this, true)

    init {
        binding.seekBarSlider.addOnSliderTouchListener(this)
        binding.seekBarSlider.addOnChangeListener(this)
    }

    var callback: Callback? = null

    /**
     * The current position, in seconds. This is the current value of the SeekBar and is indicated
     * by the start TextView in the layout.
     */
    var positionSecs: Long
        get() = binding.seekBarSlider.value.toLong()
        set(value) {
            // Sanity check: Ensure that this value is within the duration and will not crash
            // the app, and that the user is not currently seeking (which would cause the SeekBar
            // to jump around).
            if (value <= durationSecs && !isActivated) {
                binding.seekBarSlider.value = value.toFloat()

                // We would want to keep this in the callback, but the callback only fires when
                // a value changes completely, and sometimes that does not happen with this view.
                binding.seekBarPosition.textSafe = value.formatDuration(true)
            }
        }

    /**
     * The current duration, in seconds. This is the end value of the SeekBar and is indicated by
     * the end TextView in the layout.
     */
    var durationSecs: Long
        get() = binding.seekBarSlider.valueTo.toLong()
        set(value) {
            // Sanity check 1: If this is a value so low that it effectively rounds down to
            // zero, use 1 instead and disable the SeekBar.
            val to = max(value, 1)
            isEnabled = value > 0

            // Sanity check 2: If the current value exceeds the new duration value, clamp it
            // down so that we don't crash and instead have an annoying visual flicker.
            if (positionSecs > to) {
                logD("Clamping invalid position [current: $positionSecs new max: $to]")
                binding.seekBarSlider.value = to.toFloat()
            }

            binding.seekBarSlider.valueTo = to.toFloat()
            binding.seekBarDuration.textSafe = value.formatDuration(false)
        }

    override fun onStartTrackingTouch(slider: Slider) {
        logD("Starting seek mode")
        // User has begun seeking, place the SeekBar into a "Suspended" mode in which no
        // position updates are sent and is indicated by the position value turning accented.
        isActivated = true
    }

    override fun onStopTrackingTouch(slider: Slider) {
        logD("Confirming seek")
        // End of seek event, send off new value to callback.
        isActivated = false
        callback?.seekTo(slider.value.toLong())
    }

    override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
        binding.seekBarPosition.textSafe = value.toLong().formatDuration(true)
    }

    interface Callback {
        /**
         * Called when a seek event was completed and the new position must be seeked to by the app.
         */
        fun seekTo(positionSecs: Long)
    }
}
