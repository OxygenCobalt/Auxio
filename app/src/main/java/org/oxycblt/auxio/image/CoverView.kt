/*
 * Copyright (c) 2023 Auxio Project
 * CoverView.kt is part of Auxio.
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
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.core.view.isEmpty
import androidx.core.view.updateMarginsRelative
import androidx.core.widget.ImageViewCompat
import androidx.dynamicanimation.animation.SpringAnimation
import coil3.ImageLoader
import coil3.asImage
import coil3.request.ImageRequest
import coil3.request.target
import coil3.request.transformations
import coil3.util.CoilUtils
import com.google.android.material.R as MR
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.ShapeAppearanceModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.min
import kotlin.random.Random
import org.oxycblt.auxio.R
import org.oxycblt.auxio.image.coil.GalleryCoverCollection
import org.oxycblt.auxio.image.coil.RoundedRectTransformation
import org.oxycblt.auxio.image.coil.SmatteringCoverCollection
import org.oxycblt.auxio.image.coil.SquareCropTransformation
import org.oxycblt.auxio.image.coil.StackCoverCollection
import org.oxycblt.auxio.ui.Effect
import org.oxycblt.auxio.ui.Spatial
import org.oxycblt.auxio.ui.UISettings
import org.oxycblt.auxio.util.getAttrColorCompat
import org.oxycblt.auxio.util.getColorCompat
import org.oxycblt.auxio.util.getDimenPixels
import org.oxycblt.auxio.util.getDrawableCompat
import org.oxycblt.musikr.Album
import org.oxycblt.musikr.Artist
import org.oxycblt.musikr.Folder
import org.oxycblt.musikr.Genre
import org.oxycblt.musikr.Playlist
import org.oxycblt.musikr.Song
import org.oxycblt.musikr.covers.CoverCollection

/**
 * Auxio's extension of [ImageView] that enables cover art loading and playing indicator and
 * selection badge. In practice, it's three [ImageView]'s in a [FrameLayout] trenchcoat. By default,
 * all of this functionality is enabled. The playback indicator and selection badge selectively
 * disabled with the "playbackIndicatorEnabled" and "selectionBadgeEnabled" attributes, and image
 * itself can be overridden if populated like a normal [FrameLayout].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
open class CoverView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    @Inject lateinit var imageLoader: ImageLoader
    @Inject lateinit var uiSettings: UISettings
    @Inject lateinit var imageSettings: ImageSettings

    private val image: ImageView

    private data class PlaybackIndicator(
        val view: ImageView,
        val playingDrawable: AnimationDrawable,
        val pausedDrawable: Drawable,
    )

    private val playbackIndicator: PlaybackIndicator?
    private val selectionBadge: ImageView?
    private val iconSize: Int?

    private val checkScaleSpring = Spatial.FAST
    private val checkAlphaSpring = Effect.FAST
    private var fadeAnimators: List<SpringAnimation>? = null
    private val indicatorMatrix = Matrix()
    private val indicatorMatrixSrc = RectF()
    private val indicatorMatrixDst = RectF()

    private val shapeAppearance: ShapeAppearanceModel
    private val circleShapeAppearance: ShapeAppearanceModel
    private var currentShapeAppearance: ShapeAppearanceModel
    private var circleCropEnabled = false

    init {
        // Obtain some StyledImageView attributes to use later when theming the custom view.
        @SuppressLint("CustomViewStyleable")
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.CoverView)

        val shapeAppearanceRes = styledAttrs.getResourceId(R.styleable.CoverView_shapeAppearance, 0)
        shapeAppearance =
            if (uiSettings.roundMode) {
                if (shapeAppearanceRes != 0) {
                    ShapeAppearanceModel.builder(context, shapeAppearanceRes, -1).build()
                } else {
                    ShapeAppearanceModel.builder(
                            context,
                            com.google.android.material.R.style
                                .ShapeAppearance_Material3_Corner_Medium,
                            -1,
                        )
                        .build()
                }
            } else {
                ShapeAppearanceModel.builder().build()
            }
        circleShapeAppearance =
            ShapeAppearanceModel.builder().setAllCornerSizes(RelativeCornerSize(0.5f)).build()
        currentShapeAppearance = shapeAppearance
        iconSize =
            styledAttrs.getDimensionPixelSize(R.styleable.CoverView_iconSize, -1).takeIf {
                it != -1
            }

        val playbackIndicatorEnabled =
            styledAttrs.getBoolean(R.styleable.CoverView_enablePlaybackIndicator, true)
        val selectionBadgeEnabled =
            styledAttrs.getBoolean(R.styleable.CoverView_enableSelectionBadge, true)

        styledAttrs.recycle()

        image = ImageView(context, attrs)

        // Initialize the playback indicator if enabled.
        playbackIndicator =
            if (playbackIndicatorEnabled) {
                PlaybackIndicator(
                    ImageView(context).apply {
                        scaleType = ImageView.ScaleType.MATRIX
                        ImageViewCompat.setImageTintList(
                            this,
                            context.getColorCompat(R.color.sel_on_cover_bg),
                        )
                    },
                    context.getDrawableCompat(R.drawable.ic_playing_indicator_24)
                        as AnimationDrawable,
                    context.getDrawableCompat(R.drawable.ic_paused_indicator_24),
                )
            } else {
                null
            }

        // Initialize the selection badge if enabled.
        selectionBadge =
            if (selectionBadgeEnabled) {
                ImageView(context).apply {
                    imageTintList = context.getAttrColorCompat(MR.attr.colorOnPrimary)
                    setImageResource(R.drawable.ic_check_20)
                    setBackgroundResource(R.drawable.ui_selection_badge_bg)
                }
            } else {
                null
            }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        // The image isn't added if other children have populated the body. This is by design.
        if (isEmpty()) {
            addView(image)
        }

        playbackIndicator?.run { addView(view) }

        applyBackgroundsToChildren()

        // The selection badge has it's own background we don't want overridden, add it after
        // all other elements.
        selectionBadge?.let {
            addView(
                it,
                // Position the selection badge to the bottom right.
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    // Override the layout params of the indicator so that it's in the
                    // bottom left corner.
                    gravity = Gravity.BOTTOM or Gravity.END
                    val spacing = context.getDimenPixels(R.dimen.spacing_tiny)
                    updateMarginsRelative(bottom = spacing, end = spacing)
                },
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // AnimatedVectorDrawable cannot be placed in a StyledDrawable, we must replicate the
        // behavior with a matrix.
        val playbackIndicator = (playbackIndicator ?: return).view
        val iconSize = iconSize ?: (measuredWidth / 2)
        playbackIndicator.apply {
            imageMatrix =
                indicatorMatrix.apply {
                    reset()
                    drawable?.let { drawable ->
                        // First scale the icon up to the desired size.
                        indicatorMatrixSrc.set(
                            0f,
                            0f,
                            drawable.intrinsicWidth.toFloat(),
                            drawable.intrinsicHeight.toFloat(),
                        )
                        indicatorMatrixDst.set(0f, 0f, iconSize.toFloat(), iconSize.toFloat())
                        indicatorMatrix.setRectToRect(
                            indicatorMatrixSrc,
                            indicatorMatrixDst,
                            Matrix.ScaleToFit.CENTER,
                        )

                        // Then actually center it into the icon.
                        indicatorMatrix.postTranslate(
                            (measuredWidth - iconSize) / 2f,
                            (measuredHeight - iconSize) / 2f,
                        )
                    }
                }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        invalidateRootAlpha()
        invalidatePlaybackIndicatorAlpha(playbackIndicator ?: return)
        invalidateSelectionIndicatorAlpha(selectionBadge ?: return)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        invalidateRootAlpha()
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        invalidateRootAlpha()
        invalidatePlaybackIndicatorAlpha(playbackIndicator ?: return)
    }

    override fun setActivated(activated: Boolean) {
        super.setActivated(activated)
        invalidateSelectionIndicatorAlpha(selectionBadge ?: return)
    }

    /**
     * Set if the playback indicator should be indicated ongoing or paused playback.
     *
     * @param isPlaying Whether playback is ongoing or paused.
     */
    fun setPlaying(isPlaying: Boolean) {
        playbackIndicator?.run {
            if (isPlaying) {
                playingDrawable.start()
                view.setImageDrawable(playingDrawable)
            } else {
                playingDrawable.stop()
                view.setImageDrawable(pausedDrawable)
            }
        }
    }

    private fun applyBackgroundsToChildren() {

        // Add backgrounds to each child for visual consistency
        for (child in children) {
            if (child == selectionBadge) {
                continue
            }
            child.apply {
                // If there are rounded corners, we want to make sure view content will be cropped
                // with it.
                clipToOutline = this != image
                background =
                    MaterialShapeDrawable().apply {
                        fillColor = context.getColorCompat(R.color.sel_cover_bg)
                        shapeAppearanceModel = currentShapeAppearance
                    }
            }
        }
    }

    private fun updateShapeAppearance(useCircleCrop: Boolean) {
        if (circleCropEnabled == useCircleCrop) {
            return
        }
        circleCropEnabled = useCircleCrop
        currentShapeAppearance = if (useCircleCrop) circleShapeAppearance else shapeAppearance
        applyBackgroundsToChildren()
    }

    private fun invalidateRootAlpha() {
        alpha = if (isEnabled || isSelected) 1f else 0.5f
    }

    private fun invalidatePlaybackIndicatorAlpha(playbackIndicator: PlaybackIndicator) {
        // Occasionally content can bleed through the rounded corners and result in a seam
        // on the playing indicator, prevent that from occurring by disabling the visibility of
        // all views below the playback indicator.
        for (child in children) {
            child.alpha =
                when (child) {
                    // Selection badge is above the playback indicator, do nothing
                    selectionBadge -> child.alpha
                    playbackIndicator.view -> if (isSelected) 1f else 0f
                    else -> if (isSelected) 0f else 1f
                }
        }
    }

    private fun invalidateSelectionIndicatorAlpha(selectionBadge: ImageView) {
        fadeAnimators?.forEach { it.cancel() }
        val scaleAnim: SpringAnimation
        val alphaAnim: SpringAnimation
        if (isActivated) {
            scaleAnim = checkScaleSpring.scale(selectionBadge, 1.0f)
            alphaAnim = checkAlphaSpring.alpha(selectionBadge, 1.0f)
        } else {
            scaleAnim = checkScaleSpring.scale(selectionBadge, 0.9f)
            alphaAnim = checkAlphaSpring.alpha(selectionBadge, 0.0f)
        }
        fadeAnimators = listOf(scaleAnim, alphaAnim)
    }

    /**
     * Bind a [Song]'s image to this view.
     *
     * @param song The [Song] to bind to the view.
     */
    fun bind(song: Song) =
        bindImpl(
            { song.cover },
            context.getString(R.string.desc_album_cover, song.album.name),
            R.drawable.ic_song_24,
        )

    /**
     * Bind an [Album]'s image to this view.
     *
     * @param album The [Album] to bind to the view.
     */
    fun bind(album: Album) {
        bindImpl(
            {
                // Generally it's not desirable for many albums to show all of their covers since
                // unlike artists/genres they don't really change. Therefore just pick the most
                // "prominent" cover (the one with the most instances) and load that like you
                // would a song instead.
                album.covers.covers
                    .groupBy { it.id }
                    .maxByOrNull { it.value.size }
                    ?.value
                    ?.firstOrNull()
            },
            context.getString(R.string.desc_album_cover, album.name),
            R.drawable.ic_song_24,
        )
    }

    /**
     * Bind an [Artist]'s image to this view.
     *
     * @param artist The [Artist] to bind to the view.
     */
    fun bind(artist: Artist) {
        // For artists we both engage in circle cropping but also arrange them
        // in a "smattering" of random rotation/tilt to give the feeling of a messy
        // stack of vinyl.
        val uidSeed = artist.uid.toString().hashCode()
        bindImpl(
            {
                SmatteringCoverCollection(
                    artist.covers,
                    coverCollectionCornerRatio(),
                    coverCollectionFanAngle(uidSeed),
                    coverCollectionTiltAngle(uidSeed),
                    coverCollectionZOrder(uidSeed),
                    coverCollectionBackgroundColor(),
                )
            },
            context.getString(R.string.desc_artist_image, artist.name),
            R.drawable.ic_artist_24,
            useCircleCrop = true,
        )
    }

    /**
     * Bind a [Genre]'s image to this view.
     *
     * @param genre The [Genre] to bind to the view.
     */
    fun bind(genre: Genre) =
        // Genres are organized like a "gallery" of various covers that overlap eachother,
        // as if they were framed on a wall.
        bindImpl(
            {
                GalleryCoverCollection(
                    genre.covers,
                    coverCollectionCornerRatio(),
                    coverCollectionZOrder(genre.uid.toString().hashCode()),
                    coverCollectionBackgroundColor(),
                )
            },
            context.getString(R.string.desc_genre_image, genre.name),
            R.drawable.ic_genre_24,
        )

    /**
     * Bind a [Playlist]'s image to this view.
     *
     * @param playlist the [Playlist] to bind.
     */
    fun bind(playlist: Playlist) =
        // Playlists are organized in a straight diagonal stack to give the appearance of an
        // "orderly" pile of covers.
        bindImpl(
            {
                StackCoverCollection(
                    playlist.covers,
                    coverCollectionCornerRatio(),
                    coverCollectionZOrder(playlist.uid.toString().hashCode()),
                    coverCollectionBackgroundColor(),
                )
            },
            context.getString(R.string.desc_playlist_image, playlist.name),
            R.drawable.ic_playlist_24,
        )

    fun bind(folder: Folder) =
        bindImpl(
            {
                StackCoverCollection(
                    folder.covers,
                    coverCollectionCornerRatio(),
                    coverCollectionZOrder(folder.uid.toString().hashCode()),
                    coverCollectionBackgroundColor(),
                )
            },
            "Folder image for {folder.name}",
            R.drawable.ic_folder_24,
        )

    /**
     * Bind the covers of a generic list of [Song]s.
     *
     * @param songs The [Song]s to bind.
     * @param desc The content description to describe the bound data.
     * @param errorRes The resource of the error drawable to use if the cover cannot be loaded.
     * @param uidSeed A seed for the z-order of the stack, to keep the visual consistent.
     */
    fun bind(
        songs: List<Song>,
        desc: String,
        @DrawableRes errorRes: Int,
        uidSeed: Int = songs.hashCode(),
    ) =
        bindImpl(
            {
                StackCoverCollection(
                    CoverCollection.from(songs.mapNotNull { it.cover }),
                    coverCollectionCornerRatio(),
                    coverCollectionZOrder(uidSeed),
                    coverCollectionBackgroundColor(),
                )
            },
            desc,
            errorRes,
        )

    private fun bindImpl(
        cover: () -> Any?,
        desc: String,
        @DrawableRes errorRes: Int,
        useCircleCrop: Boolean = false,
        configure: (ImageRequest.Builder) -> ImageRequest.Builder = { it },
        deferIfNeeded: Boolean = true,
    ) {
        updateShapeAppearance(useCircleCrop)
        val cornerBounds = resolveCornerBounds()
        if (deferIfNeeded && cornerBounds == null && (uiSettings.roundMode || useCircleCrop)) {
            doOnLayout {
                bindImpl(
                    cover = cover,
                    desc = desc,
                    errorRes = errorRes,
                    useCircleCrop = useCircleCrop,
                    configure = configure,
                    deferIfNeeded = false,
                )
            }
            return
        }
        val request =
            configure(
                ImageRequest.Builder(context)
                    .data(cover())
                    .error(
                        StyledDrawable(context, context.getDrawableCompat(errorRes), iconSize)
                            .asImage()
                    )
                    .target(image)
            )

        val cornerRadius =
            cornerBounds?.let { currentShapeAppearance.topLeftCornerSize.getCornerSize(it) } ?: 0f
        val cornersTransformation = RoundedRectTransformation(cornerRadius)
        if (useCircleCrop || imageSettings.forceSquareCovers) {
            request.transformations(SquareCropTransformation.INSTANCE, cornersTransformation)
        } else {
            request.transformations(cornersTransformation)
        }

        // Dispose of any previous image request and load a new image.
        CoilUtils.dispose(image)
        imageLoader.enqueue(request.build())
        contentDescription = desc
    }

    private fun coverCollectionCornerRatio(): Float {
        if (!uiSettings.roundMode) {
            return 0f
        }
        val bounds = resolveCornerBounds() ?: return 0f
        val cornerRadiusPx = shapeAppearance.topLeftCornerSize.getCornerSize(bounds)
        if (cornerRadiusPx <= 0f) {
            return 0f
        }
        val minSize = min(bounds.width(), bounds.height())
        if (minSize <= 0f) {
            return 0f
        }
        return cornerRadiusPx / minSize
    }

    private fun coverCollectionZOrder(uidSeed: Int): List<Int> {
        val order = mutableListOf(0, 1, 2, 3)
        order.shuffle(Random(uidSeed.toLong()))
        return order
    }

    private fun coverCollectionFanAngle(uidSeed: Int): Float {
        val random = Random(uidSeed.toLong())
        return random.nextFloat() * 10f + 5f // 5-15 degrees
    }

    private fun coverCollectionTiltAngle(uidSeed: Int): Float {
        val random = Random(uidSeed.toLong())
        return random.nextFloat() * 20f - 10f // -10 to +10 degrees
    }

    private fun coverCollectionBackgroundColor(): Int =
        context.getColorCompat(R.color.sel_cover_bg).defaultColor

    private fun resolveCornerBounds(): RectF? {
        val widthPx = resolveSizePx(width, measuredWidth, layoutParams?.width)
        val heightPx = resolveSizePx(height, measuredHeight, layoutParams?.height)
        if (widthPx <= 0 || heightPx <= 0) {
            return null
        }
        return RectF(0f, 0f, widthPx.toFloat(), heightPx.toFloat())
    }

    private fun resolveSizePx(vararg candidates: Int?): Int {
        for (candidate in candidates) {
            if (candidate != null && candidate > 0) {
                return candidate
            }
        }
        return 0
    }

    /**
     * Since the error drawable must also share a view with an image, any kind of transform or tint
     * must occur within a custom dialog, which is implemented here.
     */
    private class StyledDrawable(context: Context, inner: Drawable, @Px val iconSize: Int?) :
        Drawable() {
        // Never tint a shared drawable instance. These icons are also used elsewhere (e.g. the
        // About screen), and tinting without mutation can leak the modified tint state across
        // views in inconsistent ways depending on drawable caching/inflation order.
        private val inner: Drawable = (inner.constantState?.newDrawable() ?: inner).mutate()

        init {
            // Re-tint the drawable to use the analogous "on surface" color for
            // StyledImageView.
            DrawableCompat.setTintList(inner, context.getColorCompat(R.color.sel_on_cover_bg))
        }

        override fun draw(canvas: Canvas) {
            // Resize the drawable such that it's always 1/4 the size of the image and
            // centered in the middle of the canvas.
            val adj = iconSize?.let { (bounds.width() - it) / 2 } ?: (bounds.width() / 4)
            inner.bounds.set(adj, adj, bounds.width() - adj, bounds.height() - adj)
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
