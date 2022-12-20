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
 
package org.oxycblt.auxio.image

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.core.view.updateMarginsRelative
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.getAttrColorCompat
import org.oxycblt.auxio.util.getColorCompat
import org.oxycblt.auxio.util.getDimenSize

/**
 * A super-charged [StyledImageView]. This class enables the following features in addition
 * to [StyledImageView]:
 * - A selection indicator
 * - An activation (playback) indicator
 * - Support for ONE custom view
 *
 * This class is primarily intended for list items. For other uses, [StyledImageView] is more
 * suitable.
 *
 * TODO: Rework content descriptions here
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class ImageGroup
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    // Most attributes are simply handled by StyledImageView.
    private val innerImageView: StyledImageView
    // The custom view is populated when the layout inflates.
    private var customView: View? = null
    // PlaybackIndicatorView overlays on top of the StyledImageView and custom view.
    private val playbackIndicatorView: PlaybackIndicatorView
    // The selection indicator view overlays all previous views.
    private val selectionIndicatorView: ImageView
    // Animator to handle selection visibility animations
    private var fadeAnimator: ValueAnimator? = null
    // Keep track of our corner radius so that we can apply the same attributes to the custom view.
    private val cornerRadius: Float

    init {
        // Android wants you to make separate attributes for each view type, but will
        // then throw an error if you do because of duplicate attribute names.
        @SuppressLint("CustomViewStyleable")
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.StyledImageView)
        cornerRadius = styledAttrs.getDimension(R.styleable.StyledImageView_cornerRadius, 0f)
        styledAttrs.recycle()

        // Initialize what views we can here.
        innerImageView = StyledImageView(context, attrs)
        playbackIndicatorView =
            PlaybackIndicatorView(context).apply { cornerRadius = this@ImageGroup.cornerRadius }
        selectionIndicatorView =
            ImageView(context).apply {
                imageTintList = context.getAttrColorCompat(R.attr.colorOnPrimary)
                setImageResource(R.drawable.ic_check_20)
                setBackgroundResource(R.drawable.ui_selection_badge_bg)
            }

        addView(innerImageView)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        // Due to innerImageView, the max child count is actually 2 and not 1.
        check(childCount < 3) { "Only one custom view is allowed" }

        // Get the second inflated child, if it exists, and then customize it to
        // act like the other components in this view.
        customView =
            getChildAt(1)?.apply {
                background =
                    MaterialShapeDrawable().apply {
                        fillColor = context.getColorCompat(R.color.sel_cover_bg)
                        setCornerSize(cornerRadius)
                    }
            }

        // Add the other two views to complete the layering.
        addView(playbackIndicatorView)
        addView(
            selectionIndicatorView,
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                // Override the layout params of the indicator so that it's in the
                // bottom left corner.
                gravity = Gravity.BOTTOM or Gravity.END
                val spacing = context.getDimenSize(R.dimen.spacing_tiny)
                updateMarginsRelative(bottom = spacing, end = spacing)
            })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Initialize each component before this view is drawn.
        invalidateAlpha()
        invalidatePlayingIndicator()
        invalidateSelectionIndicator()
    }

    override fun setActivated(activated: Boolean) {
        super.setActivated(activated)
        invalidateSelectionIndicator()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        invalidateAlpha()
        invalidatePlayingIndicator()
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        invalidateAlpha()
        invalidatePlayingIndicator()
    }

    /**
     * Bind a [Song] to the internal [StyledImageView].
     * @param song The [Song] to bind to the view.
     * @see StyledImageView.bind
     */
    fun bind(song: Song) = innerImageView.bind(song)

    /**
     * Bind a [Album] to the internal [StyledImageView].
     * @param album The [Album] to bind to the view.
     * @see StyledImageView.bind
     */
    fun bind(album: Album) = innerImageView.bind(album)

    /**
     * Bind a [Genre] to the internal [StyledImageView].
     * @param artist The [Artist] to bind to the view.
     * @see StyledImageView.bind
     */
    fun bind(artist: Artist) = innerImageView.bind(artist)

    /**
     * Bind a [Genre] to the internal [StyledImageView].
     * @param genre The [Genre] to bind to the view.
     * @see StyledImageView.bind
     */
    fun bind(genre: Genre) = innerImageView.bind(genre)

    /**
     * Whether this view should be indicated to have ongoing playback or not. See
     * PlaybackIndicatorView for more information on what occurs here.
     * Note: It's expected for this view to already be marked as playing with setSelected
     * (not the same thing) before this is set to true.
     */
    var isPlaying: Boolean
        get() = playbackIndicatorView.isPlaying
        set(value) {
            playbackIndicatorView.isPlaying = value
        }

    /**
     * Invalidate the overall opacity of this view.
     */
    private fun invalidateAlpha() {
        // If this view is disabled, show it at half-opacity, *unless* it is also marked
        // as playing, in which we still want to show it at full-opacity.
        alpha = if (isSelected || isEnabled) 1f else 0.5f
    }

    /**
     * Invalidate the view's playing ([isSelected]) indicator.
     */
    private fun invalidatePlayingIndicator() {
        if (isSelected) {
            // View is "selected" (actually marked as playing), so show the playing indicator
            // and hide all other elements except for the selection indicator.
            // TODO: Animate the other indicators?
            customView?.alpha = 0f
            innerImageView.alpha = 0f
            playbackIndicatorView.alpha = 1f
        } else {
            // View is not "selected", hide the playing indicator.
            customView?.alpha = 1f
            innerImageView.alpha = 1f
            playbackIndicatorView.alpha = 0f
        }
    }

    /**
     * Invalidate the view's selection ([isActivated]) indicator, animating it from invisible
     * to visible (or vice versa).
     */
    private fun invalidateSelectionIndicator() {
        // Set up a target transition for the selection indicator.
        val targetAlpha: Float
        val targetDuration: Long

        if (isActivated) {
            // Activated -> Show selection indicator
            targetAlpha = 1f
            targetDuration =
                context.resources.getInteger(R.integer.anim_fade_enter_duration).toLong()
        } else {
            // Activated -> Hide selection indicator.
            targetAlpha = 0f
            targetDuration =
                context.resources.getInteger(R.integer.anim_fade_exit_duration).toLong()
        }

        if (selectionIndicatorView.alpha == targetAlpha) {
            // Nothing to do.
            return
        }

        if (!isLaidOut) {
            // Not laid out, initialize it without animation before drawing.
            selectionIndicatorView.alpha = targetAlpha
            return
        }

        if (fadeAnimator != null) {
            // Cancel any previous animation.
            fadeAnimator?.cancel()
            fadeAnimator = null
        }

        fadeAnimator =
            ValueAnimator.ofFloat(selectionIndicatorView.alpha, targetAlpha).apply {
                duration = targetDuration
                addUpdateListener { selectionIndicatorView.alpha = it.animatedValue as Float }
                start()
            }
    }
}
