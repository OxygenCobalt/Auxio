/*
 * Copyright (c) 2021 Auxio Project
 * CompactPlaybackView.kt is part of Auxio.
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
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.view.WindowInsets
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updatePadding
import com.google.android.material.color.MaterialColors
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ViewCompactPlaybackBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.resolveAttr
import org.oxycblt.auxio.util.resolveDrawable
import org.oxycblt.auxio.util.systemBarsCompat

/**
 * A view displaying the playback state in a compact manner. This is only meant to be used
 * by [PlaybackBarLayout].
 */
class CompactPlaybackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding = ViewCompactPlaybackBinding.inflate(context.inflater, this, true)
    private var mCallback: PlaybackBarLayout.ActionCallback? = null

    init {
        id = R.id.playback_bar

        elevation = resources.getDimensionPixelSize(R.dimen.elevation_normal).toFloat()

        // To get a MaterialShapeDrawable to co-exist with a ripple drawable, we need to layer
        // this drawable on top of the existing ripple drawable. RippleDrawable actually inherits
        // LayerDrawable though, so we can do this. However, adding a new drawable layer directly
        // is only available on API 23+, but we're on API 21. So we create a drawable resource with
        // an empty drawable with a hard-coded ID, filling the drawable in with a
        // MaterialShapeDrawable at runtime and allowing this code to work on API 21.
        background = R.drawable.ui_shape_ripple.resolveDrawable(context).apply {
            val backgroundDrawable = MaterialShapeDrawable.createWithElevationOverlay(context).apply {
                elevation = this@CompactPlaybackView.elevation
                fillColor = ColorStateList.valueOf(R.attr.colorSurface.resolveAttr(context))
            }

            (this as RippleDrawable).setDrawableByLayerId(
                android.R.id.background, backgroundDrawable
            )
        }

        isClickable = true
        isFocusable = true

        setOnClickListener {
            mCallback?.onNavToPlayback()
        }

        setOnLongClickListener {
            mCallback?.onNavToItem()
            true
        }

        binding.playbackPlayPause.setOnClickListener {
            mCallback?.onPlayPauseClick()
        }

        // By default, LinearProgressIndicator will not actually color the track with the proper
        // opacity. Why? Who knows!
        binding.playbackProgressBar.trackColor = MaterialColors.compositeARGBWithAlpha(
            binding.playbackProgressBar.trackColor, (255 * 0.2).toInt()
        )
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        updatePadding(bottom = insets.systemBarsCompat.bottom)
        return insets
    }

    fun setSong(song: Song) {
        binding.song = song
        binding.executePendingBindings()
    }

    fun setPlaying(isPlaying: Boolean) {
        binding.playbackPlayPause.isActivated = isPlaying
    }

    fun setPosition(position: Long) {
        binding.playbackProgressBar.progress = position.toInt()
    }

    fun setCallback(callback: PlaybackBarLayout.ActionCallback) {
        mCallback = callback
    }

    fun clearCallback() {
        mCallback = null
    }
}
