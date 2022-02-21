/*
 * Copyright (c) 2021 Auxio Project
 * Forms.kt is part of Auxio.
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
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import org.oxycblt.auxio.R
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.playback.system.PlaybackService
import org.oxycblt.auxio.util.newBroadcastIntent
import org.oxycblt.auxio.util.newMainIntent

/**
 * The default widget is displayed whenever there is no music playing. It just shows the
 * message "No music playing".
 */
fun createDefaultWidget(context: Context): RemoteViews {
    return createViews(context, R.layout.widget_default)
}

/**
 * The tiny widget is for an edge-case situation where a 2xN widget happens to be smaller than
 * 100dp. It just shows the cover, titles, and a button.
 */
fun createTinyWidget(context: Context, state: WidgetState): RemoteViews {
    return createViews(context, R.layout.widget_tiny)
        .applyMeta(context, state)
        .applyPlayControls(context, state)
}

/**
 * The small widget is for 2x2 widgets and just shows the cover art and playback controls.
 * This is generally because a Medium widget is too large for this widget size and a text-only
 * widget is too small for this widget size.
 */
fun createSmallWidget(context: Context, state: WidgetState): RemoteViews {
    return createViews(context, R.layout.widget_small)
        .applyCover(context, state)
        .applyBasicControls(context, state)
}

/**
 * The medium widget is for 2x3 widgets and shows the cover art, title/artist, and three
 * controls. This is the default widget configuration.
 */
fun createMediumWidget(context: Context, state: WidgetState): RemoteViews {
    return createViews(context, R.layout.widget_medium)
        .applyMeta(context, state)
        .applyBasicControls(context, state)
}

/**
 * The wide widget is for Nx2 widgets and is like the small widget but with more controls.
 */
fun createWideWidget(context: Context, state: WidgetState): RemoteViews {
    return createViews(context, R.layout.widget_wide)
        .applyCover(context, state)
        .applyFullControls(context, state)
}

/**
 * The large widget is for 3x4 widgets and shows all metadata and controls.
 */
fun createLargeWidget(context: Context, state: WidgetState): RemoteViews {
    return createViews(context, R.layout.widget_large)
        .applyMeta(context, state)
        .applyFullControls(context, state)
}

private fun createViews(
    context: Context,
    @LayoutRes layout: Int
): RemoteViews {
    val views = RemoteViews(context.packageName, layout)

    views.setOnClickPendingIntent(
        android.R.id.background,
        context.newMainIntent()
    )

    return views
}

private fun RemoteViews.applyMeta(context: Context, state: WidgetState): RemoteViews {
    applyCover(context, state)

    setTextViewText(R.id.widget_song, state.song.name)
    setTextViewText(R.id.widget_artist, state.song.resolvedArtistName)

    return this
}

private fun RemoteViews.applyCover(context: Context, state: WidgetState): RemoteViews {
    if (state.albumArt != null) {
        setImageViewBitmap(R.id.widget_cover, state.albumArt)
        setContentDescription(
            R.id.widget_cover,
            context.getString(R.string.desc_album_cover, state.song.resolvedAlbumName)
        )
    } else {
        setImageViewResource(R.id.widget_cover, R.drawable.ic_widget_album)
        setContentDescription(R.id.widget_cover, context.getString(R.string.desc_no_cover))
    }

    return this
}

private fun RemoteViews.applyPlayControls(context: Context, state: WidgetState): RemoteViews {
    setOnClickPendingIntent(
        R.id.widget_play_pause,
        context.newBroadcastIntent(
            PlaybackService.ACTION_PLAY_PAUSE
        )
    )

    setImageViewResource(
        R.id.widget_play_pause,
        if (state.isPlaying) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play
        }
    )

    return this
}

private fun RemoteViews.applyBasicControls(context: Context, state: WidgetState): RemoteViews {
    applyPlayControls(context, state)

    setOnClickPendingIntent(
        R.id.widget_skip_prev,
        context.newBroadcastIntent(
            PlaybackService.ACTION_SKIP_PREV
        )
    )

    setOnClickPendingIntent(
        R.id.widget_skip_next,
        context.newBroadcastIntent(
            PlaybackService.ACTION_SKIP_NEXT
        )
    )

    return this
}

private fun RemoteViews.applyFullControls(context: Context, state: WidgetState): RemoteViews {
    applyBasicControls(context, state)

    setOnClickPendingIntent(
        R.id.widget_loop,
        context.newBroadcastIntent(
            PlaybackService.ACTION_LOOP
        )
    )

    setOnClickPendingIntent(
        R.id.widget_shuffle,
        context.newBroadcastIntent(
            PlaybackService.ACTION_SHUFFLE
        )
    )

    // RemoteView is so restrictive that emulating auxio's playback icons in a sensible way is
    // more or less impossible, including:
    // 1. Setting foreground drawables
    // 2. Applying custom image matrices
    // 3. Tinting icons at all
    //
    // So, we have to do the dumbest possible method of duplicating each drawable and hard-coding
    // indicators, tints, and icon sizes. And then google wonders why nobody uses widgets on
    // android.
    val shuffleRes = when {
        state.isShuffled -> R.drawable.ic_remote_shuffle_on
        else -> R.drawable.ic_remote_shuffle_off
    }

    val loopRes = when (state.loopMode) {
        LoopMode.NONE -> R.drawable.ic_remote_loop_off
        LoopMode.ALL -> R.drawable.ic_loop_on
        LoopMode.TRACK -> R.drawable.ic_loop_one
    }

    setImageViewResource(R.id.widget_shuffle, shuffleRes)
    setImageViewResource(R.id.widget_loop, loopRes)

    return this
}
