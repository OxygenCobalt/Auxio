/*
 * Copyright (c) 2021 Auxio Project
 * PlaybackSeeker.kt is part of Auxio.
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
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import org.oxycblt.auxio.databinding.ViewSeekBarBinding
import org.oxycblt.auxio.music.toDuration
import org.oxycblt.auxio.util.inflater

/**
 * A custom view that bundles together a seekbar with a current duration and a total duration.
 * The sub-views are specifically laid out so that the seekbar has an adequate touch height while
 * still not having gobs of whitespace everywhere.
 * @author OxygenCobalt
 */
class PlaybackSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleRes: Int = -1
) : ConstraintLayout(context, attrs, defStyleRes), SeekBar.OnSeekBarChangeListener {
    private val binding = ViewSeekBarBinding.inflate(context.inflater, this, true)
    private val isSeeking: Boolean get() = binding.playbackDurationCurrent.isActivated

    var onConfirmListener: ((Long) -> Unit)? = null

    init {
        binding.playbackSeekBar.setOnSeekBarChangeListener(this)
    }

    fun setProgress(seconds: Long) {
        // Don't update the progress while we are seeking, that will make the SeekBar jump around.
        if (!isSeeking) {
            binding.playbackSeekBar.progress = seconds.toInt()
            binding.playbackDurationCurrent.text = seconds.toDuration()
        }
    }

    fun setDuration(seconds: Long) {
        binding.playbackSeekBar.max = seconds.toInt()
        binding.playbackSongDuration.text = seconds.toDuration()
    }

    override fun onStartTrackingTouch(seekbar: SeekBar) {
        binding.playbackDurationCurrent.isActivated = true
    }

    override fun onStopTrackingTouch(seekbar: SeekBar) {
        binding.playbackDurationCurrent.isActivated = false
        onConfirmListener?.invoke(seekbar.progress.toLong())
    }

    override fun onProgressChanged(seekbar: SeekBar, value: Int, fromUser: Boolean) {
        if (fromUser) {
            // Don't actually seek yet when the user moves the progress bar, as to make our
            // player seek during every movement is both inefficient and weird.
            binding.playbackDurationCurrent.text = value.toLong().toDuration()
        }
    }
}
