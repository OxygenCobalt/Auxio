package org.oxycblt.auxio.playback

import android.content.Context
import android.graphics.drawable.Animatable2
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageButton
import org.oxycblt.auxio.R
import org.oxycblt.auxio.ui.getAnimatedDrawable

/**
 * Custom [AppCompatImageButton] that handles the animated play/pause icons.
 */
class PlayPauseButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : AppCompatImageButton(context, attrs, defStyleAttr) {
    private val iconPauseToPlay = context.getAnimatedDrawable(R.drawable.ic_pause_to_play)
    private val iconPlayToPause = context.getAnimatedDrawable(R.drawable.ic_play_to_pause)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fixSeams()
        }
    }

    fun showPlay(animated: Boolean) {
        if (animated) {
            setImageDrawable(iconPauseToPlay)
            iconPauseToPlay.start()
        } else {
            setImageResource(R.drawable.ic_play_large)
        }
    }

    fun showPause(animated: Boolean) {
        if (animated) {
            setImageDrawable(iconPlayToPause)
            iconPlayToPause.start()
        } else {
            setImageResource(R.drawable.ic_pause_large)
        }
    }

    /**
     * Hack that fixes an issue where a seam will display on the play button on certain display
     * sizes due to floating point precision problems (Gotta love IEEE 754)
     * This is done by detecting when the animation has ended and then reverting this
     * view to the normal static image. Not possible below API 23 though.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun fixSeams() {
        iconPauseToPlay.registerAnimationCallback(object : Animatable2.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                setImageResource(R.drawable.ic_play_large)
            }
        })
    }
}
