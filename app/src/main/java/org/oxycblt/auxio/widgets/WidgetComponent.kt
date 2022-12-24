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
import org.oxycblt.auxio.image.extractor.SquareFrameTransform
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.InternalPlayer
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.getDimenPixels
import org.oxycblt.auxio.util.logD

/**
 * A component that manages the state of all of Auxio's widgets.
 * This is kept separate from the AppWidgetProviders themselves to prevent possible memory
 * leaks and enable the main functionality to be extended to more widgets in the future.
 * @param context [Context] required to manage AppWidgetProviders.
 * @author Alexander Capehart (OxygenCobalt)
 */
class WidgetComponent(private val context: Context) :
    PlaybackStateManager.Callback, Settings.Callback {
    private val playbackManager = PlaybackStateManager.getInstance()
    private val settings = Settings(context, this)
    private val widgetProvider = WidgetProvider()
    private val provider = BitmapProvider(context)

    init {
        playbackManager.addCallback(this)
    }

    /**
     * Update [WidgetProvider] with the current playback state.
     */
    fun updateNowPlaying() {
        val song = playbackManager.song
        if (song == null) {
            logD("No song, resetting widget")
            widgetProvider.update(context, null)
            return
        }

        // Note: Store these values here so they remain consistent once the bitmap is loaded.
        val isPlaying = playbackManager.playerState.isPlaying
        val repeatMode = playbackManager.repeatMode
        val isShuffled = playbackManager.isShuffled

        provider.load(
            song,
            object : BitmapProvider.Target {
                override fun onConfigRequest(builder: ImageRequest.Builder): ImageRequest.Builder {
                    val cornerRadius =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            // Android 12, always round the cover with the widget's inner radius
                            context.getDimenPixels(android.R.dimen.system_app_widget_inner_radius)
                        } else if (settings.roundMode) {
                            // < Android 12, but the user still enabled round mode.
                            context.getDimenPixels(R.dimen.size_corners_medium)
                        } else {
                            // User did not enable round mode.
                            0
                        }

                    val metrics = context.resources.displayMetrics
                    val sw = metrics.widthPixels
                    val sh = metrics.heightPixels

                    return if (cornerRadius > 0) {
                        // Reduce the size by 10x, not only to make 16dp-ish corners, but also
                        // to work around a bug in Android 13 where the bitmaps aren't pooled
                        // properly, massively reducing the memory size we can work with.
                        builder
                            .size(computeWidgetImageSize(sw, sh, 10f))
                            .transformations(
                                SquareFrameTransform.INSTANCE,
                                RoundedCornersTransformation(cornerRadius.toFloat()))
                    } else {
                        // Divide by two to really make sure we aren't hitting the memory limit.
                        builder.size(computeWidgetImageSize(sw, sh, 2f))
                    }
                }

                override fun onCompleted(bitmap: Bitmap?) {
                    val state = PlaybackState(song, bitmap, isPlaying, repeatMode, isShuffled)
                    widgetProvider.update(context, state)
                }
            })
    }

    /**
     * Get the recommended image size to load for use.
     * @param sw The current screen width
     * @param sh The current screen height
     * @param modifier Modifier to reduce the image size.
     * @return An image size that is guaranteed not to exceed the widget bitmap memory limit.
     */
    private fun computeWidgetImageSize(sw: Int, sh: Int, modifier: Float) =
        // Maximum size is 1/3 total screen area * 4 bytes per pixel. Reverse
        // that to obtain the image size.
        sqrt((6f / 4f / modifier) * sw * sh).toInt()

    /**
     * Release this instance, preventing any further events from updating the widget instances.
     */
    fun release() {
        provider.release()
        settings.release()
        widgetProvider.reset(context)
        playbackManager.removeCallback(this)
    }

    // --- CALLBACKS ---

    // Hook all the major song-changing updates + the major player state updates
    // to updating the "Now Playing" widget.
    override fun onIndexMoved(index: Int) = updateNowPlaying()
    override fun onNewPlayback(index: Int, queue: List<Song>, parent: MusicParent?) = updateNowPlaying()
    override fun onStateChanged(state: InternalPlayer.State) = updateNowPlaying()
    override fun onShuffledChanged(isShuffled: Boolean) = updateNowPlaying()
    override fun onRepeatChanged(repeatMode: RepeatMode) = updateNowPlaying()
    override fun onSettingChanged(key: String) {
        if (key == context.getString(R.string.set_key_cover_mode) ||
            key == context.getString(R.string.set_key_round_mode)) {
            updateNowPlaying()
        }
    }

    /**
     * A condensed form of the playback state that is safe to use in AppWidgets.
     * @param song [PlaybackStateManager.song]
     * @param cover A pre-loaded album cover [Bitmap] for [song].
     * @param isPlaying [PlaybackStateManager.playerState]
     * @param repeatMode [PlaybackStateManager.repeatMode]
     * @param isShuffled [PlaybackStateManager.isShuffled]
     */
    data class PlaybackState(
        val song: Song,
        val cover: Bitmap?,
        val isPlaying: Boolean,
        val repeatMode: RepeatMode,
        val isShuffled: Boolean
    )
}
