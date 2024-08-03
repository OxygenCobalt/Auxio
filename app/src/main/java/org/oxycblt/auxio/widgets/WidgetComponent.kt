/*
 * Copyright (c) 2021 Auxio Project
 * WidgetComponent.kt is part of Auxio.
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
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.oxycblt.auxio.R
import org.oxycblt.auxio.image.BitmapProvider
import org.oxycblt.auxio.image.ImageSettings
import org.oxycblt.auxio.image.extractor.RoundedRectTransformation
import org.oxycblt.auxio.image.extractor.SquareCropTransformation
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.Progression
import org.oxycblt.auxio.playback.state.QueueChange
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.ui.UISettings
import org.oxycblt.auxio.util.getDimenPixels
import org.oxycblt.auxio.util.logD

/**
 * A component that manages the "Now Playing" state. This is kept separate from the [WidgetProvider]
 * itself to prevent possible memory leaks and enable extension to more widgets in the future.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class WidgetComponent
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val imageSettings: ImageSettings,
    private val bitmapProvider: BitmapProvider,
    private val playbackManager: PlaybackStateManager,
    private val uiSettings: UISettings
) : PlaybackStateManager.Listener, UISettings.Listener, ImageSettings.Listener {
    private val widgetProvider = WidgetProvider()

    init {
        playbackManager.addListener(this)
        uiSettings.registerListener(this)
        imageSettings.registerListener(this)
    }

    /** Update [WidgetProvider] with the current playback state. */
    fun update() {
        val song = playbackManager.currentSong
        if (song == null) {
            logD("No song, resetting widget")
            widgetProvider.update(context, uiSettings, null)
            return
        }

        // Note: Store these values here so they remain consistent once the bitmap is loaded.
        val isPlaying = playbackManager.progression.isPlaying
        val repeatMode = playbackManager.repeatMode
        val isShuffled = playbackManager.isShuffled

        logD("Updating widget with new playback state")
        bitmapProvider.load(
            song,
            object : BitmapProvider.Target {
                override fun onConfigRequest(builder: ImageRequest.Builder): ImageRequest.Builder {
                    val cornerRadius =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            // Android 12, always round the cover with the widget's inner radius
                            logD("Using android 12 corner radius")
                            context.getDimenPixels(android.R.dimen.system_app_widget_inner_radius)
                        } else if (uiSettings.roundMode) {
                            // < Android 12, but the user still enabled round mode.
                            logD("Using default corner radius")
                            context.getDimenPixels(R.dimen.m3_shape_corners_large)
                        } else {
                            // User did not enable round mode.
                            logD("Using no corner radius")
                            0
                        }

                    return if (cornerRadius > 0) {
                        // If rounded, reduce the bitmap size further to obtain more pronounced
                        // rounded corners.
                        builder.size(getSafeRemoteViewsImageSize(context, 10f))
                        val cornersTransformation =
                            RoundedRectTransformation(cornerRadius.toFloat())
                        if (imageSettings.forceSquareCovers) {
                            builder.transformations(
                                SquareCropTransformation.INSTANCE, cornersTransformation)
                        } else {
                            builder.transformations(cornersTransformation)
                        }
                    } else {
                        if (imageSettings.forceSquareCovers) {
                            builder.transformations(SquareCropTransformation.INSTANCE)
                        }
                        builder.size(getSafeRemoteViewsImageSize(context))
                    }
                }

                override fun onCompleted(bitmap: Bitmap?) {
                    val state = PlaybackState(song, bitmap, isPlaying, repeatMode, isShuffled)
                    logD("Bitmap loaded, uploading state $state")
                    widgetProvider.update(context, uiSettings, state)
                }
            })
    }

    /** Release this instance, preventing any further events from updating the widget instances. */
    fun release() {
        bitmapProvider.release()
        imageSettings.unregisterListener(this)
        playbackManager.removeListener(this)
        uiSettings.unregisterListener(this)
        widgetProvider.reset(context, uiSettings)
    }

    // --- CALLBACKS ---

    // Respond to all major song or player changes that will affect the widget
    override fun onIndexMoved(index: Int) = update()

    override fun onQueueChanged(queue: List<Song>, index: Int, change: QueueChange) {
        if (change.type == QueueChange.Type.SONG) {
            update()
        }
    }

    override fun onQueueReordered(queue: List<Song>, index: Int, isShuffled: Boolean) = update()

    override fun onNewPlayback(
        parent: MusicParent?,
        queue: List<Song>,
        index: Int,
        isShuffled: Boolean
    ) = update()

    override fun onProgressionChanged(progression: Progression) = update()

    override fun onRepeatModeChanged(repeatMode: RepeatMode) = update()

    // Respond to settings changes that will affect the widget
    override fun onRoundModeChanged() = update()

    override fun onImageSettingsChanged() = update()

    /**
     * A condensed form of the playback state that is safe to use in AppWidgets.
     *
     * @param song [PlaybackStateManager.currentSong]
     * @param cover A pre-loaded album cover [Bitmap] for [song].
     * @param cover A pre-loaded album cover [Bitmap] for [song], with rounded corners.
     * @param isPlaying [PlaybackStateManager.progression]
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
