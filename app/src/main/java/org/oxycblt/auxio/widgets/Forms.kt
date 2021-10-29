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

private fun RemoteViews.applyMeta(context: Context, state: WidgetState) {
    setTextViewText(R.id.widget_song, state.song.name)
    setTextViewText(R.id.widget_artist, state.song.album.artist.resolvedName)

    if (state.albumArt != null) {
        setImageViewBitmap(R.id.widget_cover, state.albumArt)
        setContentDescription(
            R.id.widget_cover, context.getString(R.string.desc_album_cover, state.song.album.name)
        )
    } else {
        setImageViewResource(R.id.widget_cover, R.drawable.ic_song)
        setContentDescription(R.id.widget_cover, context.getString(R.string.desc_no_cover))
    }
}

private fun RemoteViews.applyControls(context: Context, state: WidgetState) {
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
}

fun createDefaultWidget(context: Context): RemoteViews {
    return createViews(context, R.layout.widget_default)
}

fun createTinyWidget(context: Context, state: WidgetState): RemoteViews {
    val views = createViews(context, R.layout.widget_tiny)
    views.applyMeta(context, state)
    views.applyControls(context, state)
    return views
}

fun createWideWidget(context: Context, state: WidgetState): RemoteViews {
    val views = createViews(context, R.layout.widget_wide)
    views.applyMeta(context, state)
    views.applyControls(context, state)
    return views
}

fun createSmallWidget(context: Context, state: WidgetState): RemoteViews {
    val views = createViews(context, R.layout.widget_small)
    views.applyMeta(context, state)
    views.applyControls(context, state)
    return views
}

fun createMediumWidget(context: Context, state: WidgetState): RemoteViews {
    val views = createViews(context, R.layout.widget_medium)
    views.applyMeta(context, state)
    views.applyControls(context, state)
    return views
}

fun createLargeWidget(context: Context, state: WidgetState): RemoteViews {
    val views = createViews(context, R.layout.widget_large)
    views.applyMeta(context, state)
    views.applyControls(context, state)

    views.setOnClickPendingIntent(
        R.id.widget_loop,
        context.newBroadcastIntent(
            PlaybackService.ACTION_LOOP
        )
    )

    views.setOnClickPendingIntent(
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

    views.setImageViewResource(R.id.widget_shuffle, shuffleRes)
    views.setImageViewResource(R.id.widget_loop, loopRes)

    return views
}
