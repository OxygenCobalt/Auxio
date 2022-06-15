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

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.util.getColorStateListSafe

/**
 * Effectively a super-charged [StyledImageView].
 *
 * This class enables the following features alongside the base features pf [StyledImageView]:
 * - Activation indicator with an animated icon
 * - (Eventually) selection indicator
 * - Support for ONE custom view
 *
 * This class is primarily intended for list items. For most uses, the simpler [StyledImageView] is
 * more efficient and suitable.
 *
 * @author OxygenCobalt
 */
class ImageGroup
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    private val cornerRadius: Float

    private val inner = BaseStyledImageView(context, attrs)
    private var customView: View? = null
    private val indicator = ImageGroupIndicator(context)

    init {
        // Android wants you to make separate attributes for each view type, but will
        // then throw an error if you do because of duplicate attribute names.
        @SuppressLint("CustomViewStyleable")
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.StyledImageView)
        cornerRadius = styledAttrs.getDimension(R.styleable.StyledImageView_cornerRadius, 0f)
        styledAttrs.recycle()

        addView(inner)

        // Use clipToOutline and a background drawable to crop images. While Coil's transformation
        // could theoretically be used to round corners, the corner radius is dependent on the
        // dimensions of the image, which will result in inconsistent corners across different
        // album covers unless we resize all covers to be the same size. clipToOutline is both
        // cheaper and more elegant. As a side-note, this also allows us to re-use the same
        // background for both the tonal background color and the corner rounding.
        background = MaterialShapeDrawable()
        clipToOutline = true

        if (!isInEditMode) {
            val settingsManager = SettingsManager.getInstance()
            if (settingsManager.roundCovers) {
                (background as MaterialShapeDrawable).setCornerSize(cornerRadius)
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (childCount > 2) {
            error("Only one custom view is allowed")
        }

        customView =
            getChildAt(1)?.apply {
                background =
                    MaterialShapeDrawable().apply {
                        fillColor = context.getColorStateListSafe(R.color.sel_cover_bg)
                    }
            }

        addView(indicator)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        invalidateIndicator()
    }

    override fun setActivated(activated: Boolean) {
        super.setActivated(activated)
        invalidateIndicator()
    }

    private fun invalidateIndicator() {
        if (isActivated) {
            indicator.alpha = 1f
            customView?.alpha = 0f
            inner.alpha = 0f
        } else {
            indicator.alpha = 0f
            customView?.alpha = 1f
            inner.alpha = 1f
        }
    }

    fun bind(song: Song) {
        inner.bind(song)
        contentDescription =
            context.getString(R.string.desc_album_cover, song.album.resolveName(context))
    }

    fun bind(album: Album) {
        inner.bind(album)
        contentDescription =
            context.getString(R.string.desc_album_cover, album.resolveName(context))
    }

    fun bind(artist: Artist) {
        inner.bind(artist)
        contentDescription =
            context.getString(R.string.desc_artist_image, artist.resolveName(context))
    }

    fun bind(genre: Genre) {
        inner.bind(genre)
        contentDescription =
            context.getString(R.string.desc_genre_image, genre.resolveName(context))
    }
}
