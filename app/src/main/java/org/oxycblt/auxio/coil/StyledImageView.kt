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
 
package org.oxycblt.auxio.coil

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import coil.dispose
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.size.Size
import com.google.android.material.shape.MaterialShapeDrawable
import kotlin.math.min
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.util.getColorStateListSafe

/**
 * An [AppCompatImageView] that applies many of the stylistic choices that Auxio uses regarding
 * images.
 */
class StyledImageView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr) {
    private val centerMatrix = Matrix()
    private val matrixSrc = RectF()
    private val matrixDst = RectF()
    private var cornerRadius = 0f

    init {
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.StyledImageView)
        cornerRadius = styledAttrs.getDimension(R.styleable.StyledImageView_cornerRadius, 0f)
        styledAttrs.recycle()

        clipToOutline = true
        background =
            MaterialShapeDrawable().apply {
                fillColor = context.getColorStateListSafe(R.color.sel_cover_bg)
            }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // Use clipToOutline and a background drawable to crop images. While Coil's transformation
        // could theoretically be used to round corners, the corner radius is dependent on the
        // dimensions of the image, which will result in inconsistent corners across different
        // album covers unless we resize all covers to be the same size. clipToOutline is both
        // cheaper and more elegant.
        if (!isInEditMode) {
            val settingsManager = SettingsManager.getInstance()
            if (settingsManager.roundCovers) {
                (background as MaterialShapeDrawable).setCornerSize(cornerRadius)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // Scale the image down to half-size
        imageMatrix =
            centerMatrix.apply {
                reset()
                drawable?.let { drawable ->
                    // Android is too good to allow us to set a fixed image size, so we instead need
                    // to define a matrix to scale an image directly.

                    // First scale the icon up to the desired size.
                    val iconSize = min(measuredWidth, measuredHeight) / 2f
                    matrixSrc.set(
                        0f,
                        0f,
                        drawable.intrinsicWidth.toFloat(),
                        drawable.intrinsicHeight.toFloat())
                    matrixDst.set(0f, 0f, iconSize, iconSize)
                    centerMatrix.setRectToRect(matrixSrc, matrixDst, Matrix.ScaleToFit.CENTER)

                    // Then actually center it into the icon, which the previous call does not
                    // actually do.
                    centerMatrix.postTranslate(
                        (measuredWidth - iconSize) / 2f, (measuredHeight - iconSize) / 2f)
                }
            }
    }
}

// TODO: Borg the extension methods into the view, move the loadBitmap call to the service
// eventually

/** Bind the album cover for a [song]. */
fun StyledImageView.bindAlbumCover(song: Song) =
    load(song, R.drawable.ic_song, R.string.desc_album_cover)

/** Bind the album cover for an [album]. */
fun StyledImageView.bindAlbumCover(album: Album) =
    load(album, R.drawable.ic_album, R.string.desc_album_cover)

/** Bind the image for an [artist] */
fun StyledImageView.bindArtistImage(artist: Artist) =
    load(artist, R.drawable.ic_artist, R.string.desc_artist_image)

/** Bind the image for a [genre] */
fun StyledImageView.bindGenreImage(genre: Genre) =
    load(genre, R.drawable.ic_genre, R.string.desc_genre_image)

fun <T : Music> StyledImageView.load(music: T, @DrawableRes error: Int, @StringRes desc: Int) {
    contentDescription = context.getString(desc, music.resolveName(context))
    dispose()
    load(music) {
        error(error)
        transformations(SquareFrameTransform.INSTANCE)
        listener(
            onSuccess = { _, _ ->
                // Using the matrix scale type will shrink the cover images, so set it back to
                // the default scale type.
                scaleType = ImageView.ScaleType.FIT_CENTER
            },
            onError = { _, _ ->
                // Error icons need to be scaled correctly, so set it to the custom matrix
                // that the ImageView applies
                scaleType = ImageView.ScaleType.MATRIX
            })
    }
}

// --- OTHER FUNCTIONS ---

/**
 * Get a bitmap for a [song]. [onDone] will be called with the loaded bitmap, or null if loading
 * failed/shouldn't occur. **This not meant for UIs, instead use the Binding Adapters.**
 */
fun loadBitmap(context: Context, song: Song, onDone: (Bitmap?) -> Unit) {
    context.imageLoader.enqueue(
        ImageRequest.Builder(context)
            .data(song.album)
            .size(Size.ORIGINAL)
            .transformations(SquareFrameTransform())
            .target(onError = { onDone(null) }, onSuccess = { onDone(it.toBitmap()) })
            .build())
}
