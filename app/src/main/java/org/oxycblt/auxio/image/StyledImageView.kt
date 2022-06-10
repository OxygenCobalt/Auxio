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
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.settings.SettingsManager

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
    BaseStyledImageView(context, attrs, defStyleAttr) {
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

        if (!isInEditMode) {
            val settingsManager = SettingsManager.getInstance()
            if (settingsManager.roundCovers) {
                (background as MaterialShapeDrawable).setCornerSize(cornerRadius)
            }
        }
    }

    override fun bind(song: Song) {
        super.bind(song)
        contentDescription =
            context.getString(R.string.desc_album_cover, song.album.resolveName(context))
    }

    override fun bind(album: Album) {
        super.bind(album)
        contentDescription =
            context.getString(R.string.desc_album_cover, album.resolveName(context))
    }

    override fun bind(artist: Artist) {
        super.bind(artist)
        contentDescription =
            context.getString(R.string.desc_artist_image, artist.resolveName(context))
    }

    override fun bind(genre: Genre) {
        super.bind(genre)
        contentDescription =
            context.getString(R.string.desc_genre_image, genre.resolveName(context))
    }
}
