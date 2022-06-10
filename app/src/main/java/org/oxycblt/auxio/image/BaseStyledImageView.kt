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
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.DrawableCompat
import coil.dispose
import coil.load
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.getColorStateListSafe
import org.oxycblt.auxio.util.getDrawableSafe

/**
 * The base class for Auxio's images. Do not use this class outside of this module.
 *
 * Default behavior includes the addition of a tonal background and automatic icon sizing. Other
 * behavior is implemented by [StyledImageView] and [ImageGroup].
 *
 * @author OxygenCobalt
 */
open class BaseStyledImageView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr) {
    private var staticIcon = 0

    init {
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.StyledImageView)
        staticIcon = styledAttrs.getResourceId(R.styleable.StyledImageView_staticIcon, -1)
        styledAttrs.recycle()

        background =
            MaterialShapeDrawable().apply {
                fillColor = context.getColorStateListSafe(R.color.sel_cover_bg)
            }
    }

    /** Bind the album cover for a [song]. */
    open fun bind(song: Song) = loadImpl(song, R.drawable.ic_song)

    /** Bind the album cover for an [album]. */
    open fun bind(album: Album) = loadImpl(album, R.drawable.ic_album)

    /** Bind the image for an [artist] */
    open fun bind(artist: Artist) = loadImpl(artist, R.drawable.ic_artist)

    /** Bind the image for a [genre] */
    open fun bind(genre: Genre) = loadImpl(genre, R.drawable.ic_genre)

    private fun <T : Music> loadImpl(music: T, @DrawableRes error: Int) {
        if (staticIcon > -1) {
            throw IllegalStateException("Static StyledImageViews cannot bind new images")
        }

        dispose()
        load(music) {
            error(StyledDrawable(context, error))
            transformations(SquareFrameTransform.INSTANCE)
        }
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
