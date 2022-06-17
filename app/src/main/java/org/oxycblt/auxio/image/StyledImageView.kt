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
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import coil.dispose
import coil.load
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.R
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
 *
 * @author OxygenCobalt
 */
class StyledImageView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr) {
    var cornerRadius = 0f
        set(value) {
            field = value
            (background as? MaterialShapeDrawable)?.let { bg ->
                if (!isInEditMode && SettingsManager.getInstance().roundCovers) {
                    bg.setCornerSize(value)
                } else {
                    bg.setCornerSize(0f)
                }
            }
        }

    var staticIcon: Drawable? = null
        set(value) {
            val wrapped = value?.let { StyledDrawable(context, it) }
            field = wrapped
            setImageDrawable(field)
        }

    init {
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
                setCornerSize(cornerRadius)
            }

        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.StyledImageView)
        val staticIconRes =
            styledAttrs.getResourceId(
                R.styleable.StyledImageView_staticIcon, ResourcesCompat.ID_NULL)
        if (staticIconRes != ResourcesCompat.ID_NULL) {
            staticIcon = context.getDrawableSafe(staticIconRes)
        }

        cornerRadius = styledAttrs.getDimension(R.styleable.StyledImageView_cornerRadius, 0f)
        styledAttrs.recycle()
    }

    /** Bind the album cover for a [song]. */
    fun bind(song: Song) = loadImpl(song, R.drawable.ic_song, R.string.desc_album_cover)

    /** Bind the album cover for an [album]. */
    fun bind(album: Album) = loadImpl(album, R.drawable.ic_album, R.string.desc_album_cover)

    /** Bind the image for an [artist] */
    fun bind(artist: Artist) = loadImpl(artist, R.drawable.ic_artist, R.string.desc_artist_image)

    /** Bind the image for a [genre] */
    fun bind(genre: Genre) = loadImpl(genre, R.drawable.ic_genre, R.string.desc_genre_image)

    private fun <T : Music> loadImpl(music: T, @DrawableRes error: Int, @StringRes desc: Int) {
        if (staticIcon != null) {
            error("Static StyledImageViews cannot bind new images")
        }

        contentDescription = context.getString(desc, music.resolveName(context))

        dispose()
        load(music) {
            error(StyledDrawable(context, context.getDrawableSafe(error)))
            transformations(SquareFrameTransform.INSTANCE)
        }
    }
}
