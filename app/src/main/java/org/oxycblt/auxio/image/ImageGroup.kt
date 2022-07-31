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
import org.oxycblt.auxio.util.getColorStateListSafe
import org.oxycblt.auxio.util.getDrawableSafe

/**
 * Effectively a super-charged [StyledImageView].
 *
 * This class enables the following features alongside the base features pf [StyledImageView]:
 * - Activation indicator
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
    private val inner: StyledImageView
    private var customView: View? = null
    private val indicator: StyledImageView

    init {
        // Android wants you to make separate attributes for each view type, but will
        // then throw an error if you do because of duplicate attribute names.
        @SuppressLint("CustomViewStyleable")
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.StyledImageView)
        cornerRadius = styledAttrs.getDimension(R.styleable.StyledImageView_cornerRadius, 0f)
        styledAttrs.recycle()

        inner = StyledImageView(context, attrs)
        indicator =
            StyledImageView(context).apply {
                cornerRadius = this@ImageGroup.cornerRadius
                staticIcon = context.getDrawableSafe(R.drawable.ic_currently_playing_24)
            }

        addView(inner)
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
                        setCornerSize(cornerRadius)
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

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        invalidateIndicator()
    }

    private fun invalidateIndicator() {
        if (isActivated) {
            alpha = 1f
            indicator.alpha = 1f
            customView?.alpha = 0f
            inner.alpha = 0f
        } else {
            alpha = if (isEnabled) 1f else 0.5f
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
