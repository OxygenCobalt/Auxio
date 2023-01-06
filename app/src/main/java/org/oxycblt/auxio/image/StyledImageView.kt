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

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import coil.dispose
import coil.load
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.image.extractor.SquareFrameTransform
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.UISettings
import org.oxycblt.auxio.util.getColorCompat
import org.oxycblt.auxio.util.getDrawableCompat

/**
 * An [AppCompatImageView] with some additional styling, including:
 *
 * - Tonal background
 * - Rounded corners based on user preferences
 * - Built-in support for binding image data or using a static icon with the same styling as
 * placeholder drawables.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class StyledImageView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr) {
    init {
        // Load view attributes
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.StyledImageView)
        val staticIcon =
            styledAttrs.getResourceId(
                R.styleable.StyledImageView_staticIcon, ResourcesCompat.ID_NULL)
        val cornerRadius = styledAttrs.getDimension(R.styleable.StyledImageView_cornerRadius, 0f)
        styledAttrs.recycle()

        if (staticIcon != ResourcesCompat.ID_NULL) {
            // Use the static icon if specified for this image.
            setImageDrawable(StyledDrawable(context, context.getDrawableCompat(staticIcon)))
        }

        // Use clipToOutline and a background drawable to crop images. While Coil's transformation
        // could theoretically be used to round corners, the corner radius is dependent on the
        // dimensions of the image, which will result in inconsistent corners across different
        // album covers unless we resize all covers to be the same size. clipToOutline is both
        // cheaper and more elegant. As a side-note, this also allows us to re-use the same
        // background for both the tonal background color and the corner rounding.
        clipToOutline = true
        background =
            MaterialShapeDrawable().apply {
                fillColor = context.getColorCompat(R.color.sel_cover_bg)
                if (UISettings.from(context).roundMode) {
                    // Only use the specified corner radius when round mode is enabled.
                    setCornerSize(cornerRadius)
                }
            }
    }

    /**
     * Bind a [Song]'s album cover to this view, also updating the content description.
     * @param song The [Song] to bind.
     */
    fun bind(song: Song) = bindImpl(song, R.drawable.ic_song_24, R.string.desc_album_cover)

    /**
     * Bind an [Album]'s cover to this view, also updating the content description.
     * @param album the [Album] to bind.
     */
    fun bind(album: Album) = bindImpl(album, R.drawable.ic_album_24, R.string.desc_album_cover)

    /**
     * Bind an [Artist]'s image to this view, also updating the content description.
     * @param artist the [Artist] to bind.
     */
    fun bind(artist: Artist) = bindImpl(artist, R.drawable.ic_artist_24, R.string.desc_artist_image)

    /**
     * Bind an [Genre]'s image to this view, also updating the content description.
     * @param genre the [Genre] to bind.
     */
    fun bind(genre: Genre) = bindImpl(genre, R.drawable.ic_genre_24, R.string.desc_genre_image)

    /**
     * Internally bind a [Music]'s image to this view.
     * @param music The music to find.
     * @param errorRes The error drawable resource to use if the music cannot be loaded.
     * @param descRes The content description string resource to use. The resource must have one
     * field for the name of the [Music].
     */
    private fun bindImpl(music: Music, @DrawableRes errorRes: Int, @StringRes descRes: Int) {
        // Dispose of any previous image request and load a new image.
        dispose()
        load(music) {
            error(StyledDrawable(context, context.getDrawableCompat(errorRes)))
            transformations(SquareFrameTransform.INSTANCE)
        }

        // Update the content description to the specified resource.
        contentDescription = context.getString(descRes, music.resolveName(context))
    }

    /**
     * A [Drawable] wrapper that re-styles the drawable to better align with the style of
     * [StyledImageView].
     * @param context [Context] required for initialization.
     * @param inner The [Drawable] to wrap.
     */
    private class StyledDrawable(context: Context, private val inner: Drawable) : Drawable() {
        init {
            // Re-tint the drawable to use the analogous "on surface" color for
            // StyledImageView.
            DrawableCompat.setTintList(inner, context.getColorCompat(R.color.sel_on_cover_bg))
        }

        override fun draw(canvas: Canvas) {
            // Resize the drawable such that it's always 1/4 the size of the image and
            // centered in the middle of the canvas.
            val adjustWidth = bounds.width() / 4
            val adjustHeight = bounds.height() / 4
            inner.bounds.set(
                adjustWidth,
                adjustHeight,
                bounds.width() - adjustWidth,
                bounds.height() - adjustHeight)
            inner.draw(canvas)
        }

        // Required drawable overrides. Just forward to the wrapped drawable.

        override fun setAlpha(alpha: Int) {
            inner.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            inner.colorFilter = colorFilter
        }

        override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    }
}
