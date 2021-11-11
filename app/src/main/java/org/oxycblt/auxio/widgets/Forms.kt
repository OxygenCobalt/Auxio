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

private fun RemoteViews.applyMeta(state: WidgetState): RemoteViews {
    setTextViewText(R.id.widget_song, state.song.name)
    setTextViewText(R.id.widget_artist, state.song.album.artist.resolvedName)

    return this
}

private fun RemoteViews.applyCover(context: Context, state: WidgetState): RemoteViews {
    if (state.albumArt != null) {
        setImageViewBitmap(R.id.widget_cover, state.albumArt)
        setContentDescription(
            R.id.widget_cover, context.getString(R.string.desc_album_cover, state.song.album.name)
        )
    } else {
        setImageViewResource(R.id.widget_cover, R.drawable.ic_song)
        setContentDescription(R.id.widget_cover, context.getString(R.string.desc_no_cover))
    }

    return this
}
private fun RemoteViews.applyControls(context: Context, state: WidgetState): RemoteViews {
    setOnClickPendingIntent(
        R.id.widget_skip_prev,
        context.newBroadcastIntent(
            PlaybackService.ACTION_SKIP_PREV
        )
    )

    setOnClickPendingIntent(
        R.id.widget_play_pause,
        context.newBroadcastIntent(
            PlaybackService.ACTION_PLAY_PAUSE
        )
    )

    setOnClickPendingIntent(
        R.id.widget_skip_next,
        context.newBroadcastIntent(
            PlaybackService.ACTION_SKIP_NEXT
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

private fun RemoteViews.applyFullControls(context: Context, state: WidgetState): RemoteViews {
    applyControls(context, state)

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

    // While it is technically possible to use the setColorFilter to tint these buttons, its
    // actually less efficent than using duplicate drawables.
    // And no, we can't control state drawables with RemoteViews. Because of course we can't.

    val shuffleRes = when {
        state.isShuffled -> R.drawable.ic_shuffle_on
        else -> R.drawable.ic_shuffle
    }

    val loopRes = when (state.loopMode) {
        LoopMode.NONE -> R.drawable.ic_loop
        LoopMode.ALL -> R.drawable.ic_loop_on
        LoopMode.TRACK -> R.drawable.ic_loop_one
    }

    setImageViewResource(R.id.widget_shuffle, shuffleRes)
    setImageViewResource(R.id.widget_loop, loopRes)

    return this
}

fun createDefaultWidget(context: Context): RemoteViews {
    return createViews(context, R.layout.widget_default)
}

fun createTinyWidget(context: Context, state: WidgetState): RemoteViews {
    return createViews(context, R.layout.widget_tiny)
        .applyMeta(state)
        .applyCover(context, state)
        .applyControls(context, state)
}

fun createWideWidget(context: Context, state: WidgetState): RemoteViews {
    return createViews(context, R.layout.widget_wide)
        .applyMeta(state)
        .applyCover(context, state)
        .applyFullControls(context, state)
}

fun createSmallWidget(context: Context, state: WidgetState): RemoteViews {
    return createViews(context, R.layout.widget_small)
        .applyMeta(state)
        .applyCover(context, state)
        .applyControls(context, state)
}

fun createMediumWidget(context: Context, state: WidgetState): RemoteViews {
    return createViews(context, R.layout.widget_medium)
        .applyMeta(state)
        .applyCover(context, state)
        .applyControls(context, state)
}

fun createLargeWidget(context: Context, state: WidgetState): RemoteViews {
    return createViews(context, R.layout.widget_large)
        .applyMeta(state)
        .applyCover(context, state)
        .applyFullControls(context, state)
}
