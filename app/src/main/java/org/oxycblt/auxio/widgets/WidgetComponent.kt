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
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Build
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
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
 * A component that manages the "Now Playing" state. This is kept separate from the [WidgetProvider]
 * itself to prevent possible memory leaks and enable extension to more widgets in the future.
 * @param context [Context] required to manage AppWidgetProviders.
 * @author Alexander Capehart (OxygenCobalt)
 */
class WidgetComponent(private val context: Context) :
    PlaybackStateManager.Listener, SharedPreferences.OnSharedPreferenceChangeListener {
    private val playbackManager = PlaybackStateManager.getInstance()
    private val settings = Settings(context)
    private val widgetProvider = WidgetProvider()
    private val provider = BitmapProvider(context)

    init {
        playbackManager.addListener(this)
        settings.addListener(this)
    }

    /** Update [WidgetProvider] with the current playback state. */
    fun update() {
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
        settings.removeListener(this)
        widgetProvider.reset(context)
        playbackManager.removeListener(this)
    }

    // --- CALLBACKS ---

    // Hook all the major song-changing updates + the major player state updates
    // to updating the "Now Playing" widget.
    override fun onIndexMoved(index: Int) = update()
    override fun onNewPlayback(index: Int, queue: List<Song>, parent: MusicParent?) = update()
    override fun onStateChanged(state: InternalPlayer.State) = update()
    override fun onShuffledChanged(isShuffled: Boolean) = update()
    override fun onRepeatChanged(repeatMode: RepeatMode) = update()

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == context.getString(R.string.set_key_cover_mode) ||
            key == context.getString(R.string.set_key_round_mode)) {
            update()
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
