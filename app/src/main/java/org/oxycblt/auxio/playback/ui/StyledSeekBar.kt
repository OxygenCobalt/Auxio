/*
 * Copyright (c) 2022 Auxio Project
 * StyledSeekBar.kt is part of Auxio.
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

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.slider.Slider
import kotlin.math.max
import org.oxycblt.auxio.databinding.ViewSeekBarBinding
import org.oxycblt.auxio.playback.formatDurationDs
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.logD

/**
 * A wrapper around [Slider] that shows position and duration values and sanitizes input to reduce
 * crashes from invalid values.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class StyledSeekBar
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ForcedLTRFrameLayout(context, attrs, defStyleAttr),
    Slider.OnSliderTouchListener,
    Slider.OnChangeListener {
    private val binding = ViewSeekBarBinding.inflate(context.inflater, this, true)

    init {
        binding.seekBarSlider.addOnSliderTouchListener(this)
        binding.seekBarSlider.addOnChangeListener(this)
    }

    /** The current [Listener] attached to this instance. */
    var listener: Listener? = null

    /**
     * The current position, in deci-seconds(1/10th of a second). This is the current value of the
     * SeekBar and is indicated by the start TextView in the layout.
     */
    var positionDs: Long
        get() = binding.seekBarSlider.value.toLong()
        set(value) {
            // Sanity check 1: Ensure that no negative values are sneaking their way into
            // this component.
            val from = max(value, 0)
            // Sanity check 2: Ensure that this value is within the duration and will not crash
            // the app, and that the user is not currently seeking (which would cause the SeekBar
            // to jump around).
            if (from <= durationDs && !isActivated) {
                binding.seekBarSlider.value = from.toFloat()
                // We would want to keep this in the listener, but the listener only fires when
                // a value changes completely, and sometimes that does not happen with this view.
                binding.seekBarPosition.text = from.formatDurationDs(true)
            }
        }

    /**
     * The current duration, in deci-seconds (1/10th of a second). This is the end value of the
     * SeekBar and is indicated by the end TextView in the layout.
     */
    var durationDs: Long
        get() = binding.seekBarSlider.valueTo.toLong()
        set(value) {
            // Sanity check 1: If this is a value so low that it effectively rounds down to
            // zero, use 1 instead and disable the SeekBar.
            val to = max(value, 1)
            isEnabled = value > 0
            // Sanity check 2: If the current value exceeds the new duration value, clamp it
            // down so that we don't crash and instead have an annoying visual flicker.
            if (positionDs > to) {
                logD("Clamping invalid position [current: $positionDs new max: $to]")
                binding.seekBarSlider.value = to.toFloat()
            }
            binding.seekBarSlider.valueTo = to.toFloat()
            binding.seekBarDuration.text = value.formatDurationDs(false)
        }

    override fun onStartTrackingTouch(slider: Slider) {
        logD("Starting seek mode")
        // User has begun seeking, place the SeekBar into a "Suspended" mode in which no
        // position updates are sent and is indicated by the position value turning accented.
        isActivated = true
    }

    override fun onStopTrackingTouch(slider: Slider) {
        logD("Confirming seek")
        // End of seek event, send off new value to listener.
        isActivated = false
        listener?.onSeekConfirmed(slider.value.toLong())
    }

    override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
        binding.seekBarPosition.text = value.toLong().formatDurationDs(true)
    }

    /** A listener for SeekBar interactions. */
    interface Listener {
        /**
         * Called when the internal [Slider] was scrubbed to a new position, requesting that a seek
         * be performed.
         *
         * @param positionDs The position to seek to, in deci-seconds (1/10th of a second).
         */
        fun onSeekConfirmed(positionDs: Long)
    }
}
