/*
 * Copyright (c) 2022 Auxio Project
 * ImageGroup.kt is part of Auxio.
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
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.util.getAttrColorCompat
import org.oxycblt.auxio.util.getColorCompat
import org.oxycblt.auxio.util.getDimenPixels
import org.oxycblt.auxio.util.getInteger

/**
 * A super-charged [StyledImageView]. This class enables the following features in addition to
 * [StyledImageView]:
 * - A selection indicator
 * - An activation (playback) indicator
 * - Support for ONE custom view
 *
 * This class is primarily intended for list items. For other uses, [StyledImageView] is more
 * suitable.
 *
 * @author Alexander Capehart (OxygenCobalt)
 *
 * TODO: Rework content descriptions here
 */
class ImageGroup
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    private val innerImageView: StyledImageView
    private var customView: View? = null
    private val playbackIndicatorView: PlaybackIndicatorView
    private val selectionIndicatorView: ImageView

    private var fadeAnimator: ValueAnimator? = null
    private val cornerRadius: Float

    init {
        // Obtain some StyledImageView attributes to use later when theming the custom view.
        @SuppressLint("CustomViewStyleable")
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.StyledImageView)
        // Keep track of our corner radius so that we can apply the same attributes to the custom
        // view.
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

        // The inner StyledImageView should be at the bottom and hidden by any other elements
        // if they become visible.
        addView(innerImageView)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        // Due to innerImageView, the max child count is actually 2 and not 1.
        check(childCount < 3) { "Only one custom view is allowed" }

        // Get the second inflated child, making sure we customize it to align with
        // the rest of this view.
        customView =
            getChildAt(1)?.apply {
                background =
                    MaterialShapeDrawable().apply {
                        fillColor = context.getColorCompat(R.color.sel_cover_bg)
                        setCornerSize(cornerRadius)
                    }
            }

        // Playback indicator should sit above the inner StyledImageView and custom view/
        addView(playbackIndicatorView)
        // Selection indicator should never be obscured, so place it at the top.
        addView(
            selectionIndicatorView,
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                // Override the layout params of the indicator so that it's in the
                // bottom left corner.
                gravity = Gravity.BOTTOM or Gravity.END
                val spacing = context.getDimenPixels(R.dimen.spacing_tiny)
                updateMarginsRelative(bottom = spacing, end = spacing)
            })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Initialize each component before this view is drawn.
        invalidateImageAlpha()
        invalidatePlayingIndicator()
        invalidateSelectionIndicator()
    }

    override fun setActivated(activated: Boolean) {
        super.setActivated(activated)
        invalidateSelectionIndicator()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        invalidateImageAlpha()
        invalidatePlayingIndicator()
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        invalidateImageAlpha()
        invalidatePlayingIndicator()
    }

    /**
     * Bind a [Song] to the internal [StyledImageView].
     *
     * @param song The [Song] to bind to the view.
     * @see StyledImageView.bind
     */
    fun bind(song: Song) = innerImageView.bind(song)

    /**
     * Bind a [Album] to the internal [StyledImageView].
     *
     * @param album The [Album] to bind to the view.
     * @see StyledImageView.bind
     */
    fun bind(album: Album) = innerImageView.bind(album)

    /**
     * Bind a [Genre] to the internal [StyledImageView].
     *
     * @param artist The [Artist] to bind to the view.
     * @see StyledImageView.bind
     */
    fun bind(artist: Artist) = innerImageView.bind(artist)

    /**
     * Bind a [Genre] to the internal [StyledImageView].
     *
     * @param genre The [Genre] to bind to the view.
     * @see StyledImageView.bind
     */
    fun bind(genre: Genre) = innerImageView.bind(genre)

    /**
     * Bind a [Playlist]'s image to the internal [StyledImageView].
     *
     * @param playlist the [Playlist] to bind.
     * @see StyledImageView.bind
     */
    fun bind(playlist: Playlist) = innerImageView.bind(playlist)

    /**
     * Whether this view should be indicated to have ongoing playback or not. See
     * PlaybackIndicatorView for more information on what occurs here. Note: It's expected for this
     * view to already be marked as playing with setSelected (not the same thing) before this is set
     * to true.
     */
    var isPlaying: Boolean
        get() = playbackIndicatorView.isPlaying
        set(value) {
            playbackIndicatorView.isPlaying = value
        }

    private fun invalidateImageAlpha() {
        // If this view is disabled, show it at half-opacity, *unless* it is also marked
        // as playing, in which we still want to show it at full-opacity.
        alpha = if (isSelected || isEnabled) 1f else 0.5f
    }

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

    private fun invalidateSelectionIndicator() {
        // Set up a target transition for the selection indicator.
        val targetAlpha: Float
        val targetDuration: Long

        if (isActivated) {
            // View is "activated" (i.e marked as selected), so show the selection indicator.
            targetAlpha = 1f
            targetDuration = context.getInteger(R.integer.anim_fade_enter_duration).toLong()
        } else {
            // View is not "activated", hide the selection indicator.
            targetAlpha = 0f
            targetDuration = context.getInteger(R.integer.anim_fade_exit_duration).toLong()
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
