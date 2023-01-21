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
import org.oxycblt.auxio.R
import org.oxycblt.auxio.image.BitmapProvider
import org.oxycblt.auxio.image.ImageSettings
import org.oxycblt.auxio.image.extractor.SquareFrameTransform
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.InternalPlayer
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.Queue
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.ui.UISettings
import org.oxycblt.auxio.util.getDimenPixels
import org.oxycblt.auxio.util.logD

/**
 * A component that manages the "Now Playing" state. This is kept separate from the [WidgetProvider]
 * itself to prevent possible memory leaks and enable extension to more widgets in the future.
 * @param context [Context] required to manage AppWidgetProviders.
 * @author Alexander Capehart (OxygenCobalt)
 */
class WidgetComponent(private val context: Context) :
    PlaybackStateManager.Listener, UISettings.Listener, ImageSettings.Listener {
    private val playbackManager = PlaybackStateManager.getInstance()
    private val uiSettings = UISettings.from(context)
    private val imageSettings = ImageSettings.from(context)
    private val widgetProvider = WidgetProvider()
    private val provider = BitmapProvider(context)

    init {
        playbackManager.addListener(this)
        uiSettings.registerListener(this)
        imageSettings.registerListener(this)
    }

    /** Update [WidgetProvider] with the current playback state. */
    fun update() {
        val song = playbackManager.queue.currentSong
        if (song == null) {
            logD("No song, resetting widget")
            widgetProvider.update(context, null)
            return
        }

        // Note: Store these values here so they remain consistent once the bitmap is loaded.
        val isPlaying = playbackManager.playerState.isPlaying
        val repeatMode = playbackManager.repeatMode
        val isShuffled = playbackManager.queue.isShuffled

        provider.load(
            song,
            object : BitmapProvider.Target {
                override fun onConfigRequest(builder: ImageRequest.Builder): ImageRequest.Builder {
                    val cornerRadius =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            // Android 12, always round the cover with the widget's inner radius
                            context.getDimenPixels(android.R.dimen.system_app_widget_inner_radius)
                        } else if (uiSettings.roundMode) {
                            // < Android 12, but the user still enabled round mode.
                            context.getDimenPixels(R.dimen.size_corners_medium)
                        } else {
                            // User did not enable round mode.
                            0
                        }

                    return if (cornerRadius > 0) {
                        // If rounded, educe the bitmap size further to obtain more pronounced
                        // rounded corners.
                        builder
                            .size(getSafeRemoteViewsImageSize(context, 10f))
                            .transformations(
                                SquareFrameTransform.INSTANCE,
                                RoundedCornersTransformation(cornerRadius.toFloat()))
                    } else {
                        builder.size(getSafeRemoteViewsImageSize(context))
                    }
                }

                override fun onCompleted(bitmap: Bitmap?) {
                    val state = PlaybackState(song, bitmap, isPlaying, repeatMode, isShuffled)
                    widgetProvider.update(context, state)
                }
            })
    }

    /** Release this instance, preventing any further events from updating the widget instances. */
    fun release() {
        provider.release()
        uiSettings.unregisterListener(this)
        widgetProvider.reset(context)
        playbackManager.removeListener(this)
    }

    // --- CALLBACKS ---

    // Respond to all major song or player changes that will affect the widget
    override fun onIndexMoved(queue: Queue) = update()
    override fun onQueueReordered(queue: Queue) = update()
    override fun onNewPlayback(queue: Queue, parent: MusicParent?) = update()
    override fun onStateChanged(state: InternalPlayer.State) = update()
    override fun onRepeatChanged(repeatMode: RepeatMode) = update()

    // Respond to settings changes that will affect the widget
    override fun onRoundModeChanged() = update()
    override fun onCoverModeChanged() = update()

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
