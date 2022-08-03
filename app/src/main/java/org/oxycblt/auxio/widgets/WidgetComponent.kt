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
import coil.transform.RoundedCornersTransformation
import kotlin.math.sqrt
import org.oxycblt.auxio.R
import org.oxycblt.auxio.image.BitmapProvider
import org.oxycblt.auxio.image.SquareFrameTransform
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.getDimenSizeSafe
import org.oxycblt.auxio.util.logD

/**
 * A wrapper around each [WidgetProvider] that plugs into the main Auxio process and updates the
 * widget state based off of that. This cannot be rolled into [WidgetProvider] directly, as it may
 * result in memory leaks if [PlaybackStateManager]/[Settings] gets created and bound to without
 * being released.
 * @author OxygenCobalt
 */
class WidgetComponent(private val context: Context) :
    PlaybackStateManager.Callback, Settings.Callback {
    private val playbackManager = PlaybackStateManager.getInstance()
    private val settings = Settings(context, this)
    private val widget = WidgetProvider()
    private val provider = BitmapProvider(context)

    init {
        playbackManager.addCallback(this)

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
                    val cornerRadius =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            // Android 12, always round the cover with the app widget's inner radius
                            context.getDimenSizeSafe(android.R.dimen.system_app_widget_inner_radius)
                        } else if (settings.roundMode) {
                            // < Android 12, but the user still enabled round mode.
                            context.getDimenSizeSafe(R.dimen.size_corners_medium)
                        } else {
                            // User did not enable round mode.
                            0
                        }

                    // We resize the image in a such a way that we don't hit the RemoteView size
                    // limit, which is the size of an RGB_8888 bitmap 1.5x the screen size. When
                    // enabling rounded corners, we further reduce it by a factor of 8 to get 16-dp
                    // rounded corners, whereas we only downsize it by 2 when there is rounded
                    // corners just to ensure that we do not hit the memory limit.
                    val metrics = context.resources.displayMetrics
                    val sw = metrics.widthPixels
                    val sh = metrics.heightPixels

                    return if (cornerRadius > 0) {
                        this@WidgetComponent.logD("Loading round covers: $cornerRadius")

                        builder
                            .size(sqrt((6f / 4f / 8f) * sw * sh).toInt())
                            .transformations(
                                SquareFrameTransform.INSTANCE,
                                // RoundedCornersTransformation is used instead of clipToOutline
                                // since our hack to get a 1:1 cover on the widget actually does
                                // not result in a square view, making clipToOutline not work.
                                RoundedCornersTransformation(cornerRadius.toFloat()))
                    } else {
                        builder.size(sqrt((6f / 4f / 2f) * sw * sh).toInt())
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
        settings.release()
        widget.reset(context)
        playbackManager.removeCallback(this)
    }

    // --- CALLBACKS ---

    override fun onIndexMoved(index: Int) = update()
    override fun onNewPlayback(index: Int, queue: List<Song>, parent: MusicParent?) = update()
    override fun onPlayingChanged(isPlaying: Boolean) = update()
    override fun onShuffledChanged(isShuffled: Boolean) = update()
    override fun onRepeatChanged(repeatMode: RepeatMode) = update()
    override fun onSettingChanged(key: String) {
        if (key == context.getString(R.string.set_key_show_covers) ||
            key == context.getString(R.string.set_key_quality_covers) ||
            key == context.getString(R.string.set_key_round_mode)) {
            update()
        }
    }

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
