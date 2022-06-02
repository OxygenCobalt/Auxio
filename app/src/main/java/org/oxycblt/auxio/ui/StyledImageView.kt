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
 
package org.oxycblt.auxio.ui

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
import androidx.core.graphics.drawable.DrawableCompat
import coil.dispose
import coil.load
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.image.SquareFrameTransform
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.util.getColorStateListSafe
import org.oxycblt.auxio.util.getDrawableSafe

/**
 * An [AppCompatImageView] that applies many of the stylistic choices that Auxio uses regarding
 * images.
 *
 * Default behavior includes the addition of a tonal background, automatic sizing of icons to half
 * of the view size, and corner radius application depending on user preference.
 * @author OxygenCobalt
 *
 * TODO: Add an activation indicator to this view too
 */
class StyledImageView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr) {
    private var cornerRadius = 0f

    init {
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.StyledImageView)

        cornerRadius = styledAttrs.getDimension(R.styleable.StyledImageView_cornerRadius, 0f)

        styledAttrs.recycle()

        // Use clipToOutline and a background drawable to crop images. While Coil's transformation
        // could theoretically be used to round corners, the corner radius is dependent on the
        // dimensions of the image, which will result in inconsistent corners across different
        // album covers unless we resize all covers to be the same size. clipToOutline is both
        // cheaper and more elegant. As a side-note, this also allows us to re-use the same
        // background for both the tonal background color and the corner rounding.
        clipToOutline = true
        background =
            MaterialShapeDrawable().apply {
                fillColor = context.getColorStateListSafe(R.color.sel_cover_bg)
            }

        // If we have a pre-set drawable, ensure that it's half-size.
        val drawable = drawable
        if (drawable != null) {
            setImageDrawable(StyledDrawable(context, drawable))
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (!isInEditMode) {
            val settingsManager = SettingsManager.getInstance()
            if (settingsManager.roundCovers) {
                (background as MaterialShapeDrawable).setCornerSize(cornerRadius)
            }
        }
    }

    /** Bind the album cover for a [song]. */
    fun bind(song: Song) = load(song, R.drawable.ic_song, R.string.desc_album_cover)

    /** Bind the album cover for an [album]. */
    fun bind(album: Album) = load(album, R.drawable.ic_album, R.string.desc_album_cover)

    /** Bind the image for an [artist] */
    fun bind(artist: Artist) = load(artist, R.drawable.ic_artist, R.string.desc_artist_image)

    /** Bind the image for a [genre] */
    fun bind(genre: Genre) = load(genre, R.drawable.ic_genre, R.string.desc_genre_image)

    private fun <T : Music> load(music: T, @DrawableRes error: Int, @StringRes desc: Int) {
        dispose()
        load(music) {
            error(StyledDrawable(context, error))
            transformations(SquareFrameTransform.INSTANCE)
        }
        contentDescription = context.getString(desc, music.resolveName(context))
    }

    /**
     * A companion drawable that can be used with the style that [StyledImageView] provides.
     * @author OxygenCobalt
     */
    class StyledDrawable(context: Context, private val src: Drawable) : Drawable() {
        constructor(
            context: Context,
            @DrawableRes res: Int
        ) : this(context, context.getDrawableSafe(res))

        init {
            // Re-tint the drawable to something that will play along with the background
            DrawableCompat.setTintList(src, context.getColorStateListSafe(R.color.sel_on_cover_bg))
        }

        override fun draw(canvas: Canvas) {
            src.bounds.set(canvas.clipBounds)
            val adjustWidth = src.bounds.width() / 4
            val adjustHeight = src.bounds.height() / 4
            src.bounds.set(
                adjustWidth,
                adjustHeight,
                src.bounds.width() - adjustWidth,
                src.bounds.height() - adjustHeight)
            src.draw(canvas)
        }

        override fun setAlpha(alpha: Int) {
            src.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            src.colorFilter = colorFilter
        }

        override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
    }
}
