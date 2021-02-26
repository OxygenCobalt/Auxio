package org.oxycblt.auxio.playback

import android.content.Context
import android.graphics.drawable.Animatable2
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageButton
import org.oxycblt.auxio.R
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.ui.toAnimDrawable

/**
 * Custom [AppCompatImageButton] that handles the animated play/pause icons.
 * @author OxygenCobalt
 */
class PlayPauseButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : AppCompatImageButton(context, attrs, defStyleAttr) {
    private val iconPauseToPlay = R.drawable.ic_pause_to_play.toAnimDrawable(context)
    private val iconPlayToPause = R.drawable.ic_play_to_pause.toAnimDrawable(context)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fixSeams()
        }
    }

    /**
     * Update the play/pause icon to reflect [isPlaying]
     * @param animate Whether the icon change should be animated or not.
     */
    fun setPlaying(isPlaying: Boolean, animate: Boolean) {
        if (isPlaying) {
            if (animate) {
                setImageDrawable(iconPlayToPause)
                iconPlayToPause.start()
            } else {
                setImageResource(R.drawable.ic_pause_large)
            }
        } else {
            if (animate) {
                setImageDrawable(iconPauseToPlay)
                iconPauseToPlay.start()
            } else {
                logD("what the FUCK WHY ARENT YOU DOING THIS")
                setImageResource(R.drawable.ic_play_large)
            }
        }
    }

    /**
     * Hack that fixes an issue where a seam would display in the middle of the play button,
     * probably as a result of floating point precision errors. Gotta love IEEE 754.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun fixSeams() {
        iconPauseToPlay.registerAnimationCallback(object : Animatable2.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                setImageResource(R.drawable.ic_play_large)
            }
        })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            iconPauseToPlay.clearAnimationCallbacks()
        }
    }
}
