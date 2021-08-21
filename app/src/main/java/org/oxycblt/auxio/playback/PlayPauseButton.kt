/*
 * Copyright (c) 2021 Auxio Project
 * PlayPauseButton.kt is part of Auxio.
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
import androidx.appcompat.widget.AppCompatImageButton
import org.oxycblt.auxio.R

/**
 * Custom [AppCompatImageButton] that handles the animated play/pause icons.
 * @author OxygenCobalt
 * TODO: Replace this with a material icon that does the path manipulation manually.
 */
class PlayPauseButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : AppCompatImageButton(context, attrs, defStyleAttr) {
    /**
     * Update the play/pause icon to reflect [isPlaying]
     * @param animate Whether the icon change should be animated or not.
     */
    fun setPlaying(isPlaying: Boolean, animate: Boolean) {
        if (isPlaying) {
            setImageResource(R.drawable.ic_pause)
        } else {
            setImageResource(R.drawable.ic_play)
        }
    }
}
