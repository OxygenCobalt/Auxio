/*
 * Copyright (c) 2021 Auxio Project
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
 
package org.oxycblt.auxio.widgets

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.RoundedCornersTransformation
import org.oxycblt.auxio.image.BitmapProvider
import org.oxycblt.auxio.image.SquareFrameTransform
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.util.getDimenSizeSafe
import org.oxycblt.auxio.util.logD

/**
 * A wrapper around each [WidgetProvider] that plugs into the main Auxio process and updates the
 * widget state based off of that. This cannot be rolled into [WidgetProvider] directly, as it may
 * result in memory leaks if [PlaybackStateManager]/[SettingsManager] gets created and bound to
 * without being released.
 * @author OxygenCobalt
 */
class WidgetComponent(private val context: Context) :
    PlaybackStateManager.Callback, SettingsManager.Callback {
    private val playbackManager = PlaybackStateManager.getInstance()
    private val settingsManager = SettingsManager.getInstance()
    private val widget = WidgetProvider()
    private val provider = BitmapProvider(context)

    init {
        playbackManager.addCallback(this)
        settingsManager.addCallback(this)

        if (playbackManager.isInitialized) {
            update()
        }
    }

    /*
     * Force-update the widget.
     */
    fun update() {
        // Updating Auxio's widget is unlike the rest of Auxio for a few reasons:
        // 1. We can't use the typical primitives like ViewModels
        // 2. The component range is far smaller, so we have to do some odd hacks to get
        // the same UX.
        // 3. RemoteView memory is limited, so we want to batch updates as much as physically
        // possible.
        val song = playbackManager.song
        if (song == null) {
            logD("No song, resetting widget")
            widget.update(context, null)
            return
        }

        // Note: Store these values here so they remain consistent once the bitmap is loaded.
        val isPlaying = playbackManager.isPlaying
        val repeatMode = playbackManager.repeatMode
        val isShuffled = playbackManager.isShuffled

        provider.load(
            song,
            object : BitmapProvider.Target {
                override fun onConfigRequest(builder: ImageRequest.Builder): ImageRequest.Builder {
                    // The widget has two distinct styles that we must transform the album art to
                    // accommodate:
                    // - Before Android 12, the widget has hard edges, so we don't need to round
                    // out the album art.
                    // - After Android 12, the widget has round edges, so we need to round out
                    // the album art. I dislike this, but it's mainly for stylistic cohesion.
                    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        this@WidgetComponent.logD("Doing API 31 cover load")

                        val metrics = context.resources.displayMetrics

                        // Use RoundedCornersTransformation. This is because our hack to get a 1:1
                        // aspect ratio on widget ImageViews doesn't actually result in a square
                        // ImageView, so clipToOutline won't work.
                        builder
                            .transformations(
                                SquareFrameTransform.INSTANCE,
                                RoundedCornersTransformation(
                                    context
                                        .getDimenSizeSafe(
                                            android.R.dimen.system_app_widget_inner_radius)
                                        .toFloat()))
                            // The output of RoundedCornersTransformation is dimension-dependent,
                            // so scale up the image to the screen size to ensure consistent radii.
                            // Make sure we stop at 1024, so we don't accidentally make a massive
                            // bitmap on very large screens.
                            .size(minOf(metrics.widthPixels, metrics.heightPixels, 1024))
                    } else {
                        this@WidgetComponent.logD("Doing API 21 cover load")
                        // Note: Explicitly use the "original" size as without it the scaling logic
                        // in coil breaks down and results in an error.
                        builder.size(Size.ORIGINAL)
                    }
                }

                override fun onCompleted(bitmap: Bitmap?) {
                    val state = WidgetState(song, bitmap, isPlaying, repeatMode, isShuffled)
                    widget.update(context, state)
                }
            })
    }

    /*
     * Release this instance, removing the callbacks and resetting all widgets
     */
    fun release() {
        provider.release()
        widget.reset(context)
        playbackManager.removeCallback(this)
        settingsManager.removeCallback(this)
    }

    // --- CALLBACKS ---

    override fun onIndexMoved(index: Int) = update()
    override fun onNewPlayback(index: Int, queue: List<Song>, parent: MusicParent?) = update()
    override fun onPlayingChanged(isPlaying: Boolean) = update()
    override fun onShuffledChanged(isShuffled: Boolean) = update()
    override fun onRepeatChanged(repeatMode: RepeatMode) = update()
    override fun onCoverSettingsChanged() = update()

    /*
     * An immutable condensed variant of the current playback state, used so that PlaybackStateManager
     * does not need to be queried directly.
     */
    data class WidgetState(
        val song: Song,
        val cover: Bitmap?,
        val isPlaying: Boolean,
        val repeatMode: RepeatMode,
        val isShuffled: Boolean,
    )
}
