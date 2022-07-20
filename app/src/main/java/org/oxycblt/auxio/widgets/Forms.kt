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
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import org.oxycblt.auxio.R
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.playback.system.PlaybackService
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.newBroadcastPendingIntent
import org.oxycblt.auxio.util.newMainPendingIntent

/**
 * The default widget is displayed whenever there is no music playing. It just shows the message "No
 * music playing".
 */
fun createDefaultWidget(context: Context) = createViews(context, R.layout.widget_default)

/**
 * The thin widget is a weird outlier widget intended to work well on strange launchers or landscape
 * grid launchers that allow really thin widget sizing.
 */
fun createThinWidget(context: Context, state: WidgetComponent.WidgetState) =
    createViews(context, R.layout.widget_thin)
        .applyRoundingToBackground(context)
        .applyMeta(context, state)
        .applyBasicControls(context, state)
/**
 * The small widget is for 2x2 widgets and just shows the cover art and playback controls. This is
 * generally because a Medium widget is too large for this widget size and a text-only widget is too
 * small for this widget size.
 */
fun createSmallWidget(context: Context, state: WidgetComponent.WidgetState) =
    createViews(context, R.layout.widget_small)
        .applyRoundingToBar(context)
        .applyCover(context, state)
        .applyBasicControls(context, state)

/**
 * The medium widget is for 2x3 widgets and shows the cover art, title/artist, and three controls.
 * This is the default widget configuration.
 */
fun createMediumWidget(context: Context, state: WidgetComponent.WidgetState) =
    createViews(context, R.layout.widget_medium)
        .applyRoundingToBackground(context)
        .applyMeta(context, state)
        .applyBasicControls(context, state)

/** The wide widget is for Nx2 widgets and is like the small widget but with more controls. */
fun createWideWidget(context: Context, state: WidgetComponent.WidgetState) =
    createViews(context, R.layout.widget_wide)
        .applyRoundingToBar(context)
        .applyCover(context, state)
        .applyFullControls(context, state)

/** The large widget is for 3x4 widgets and shows all metadata and controls. */
fun createLargeWidget(context: Context, state: WidgetComponent.WidgetState): RemoteViews =
    createViews(context, R.layout.widget_large)
        .applyRoundingToBackground(context)
        .applyMeta(context, state)
        .applyFullControls(context, state)

private fun createViews(context: Context, @LayoutRes layout: Int): RemoteViews {
    val views = RemoteViews(context.packageName, layout)
    views.setOnClickPendingIntent(android.R.id.background, context.newMainPendingIntent())
    return views
}

private fun RemoteViews.applyRoundingToBackground(context: Context): RemoteViews {
    if (Settings(context).roundMode && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        setInt(android.R.id.background, "setBackgroundResource", R.drawable.ui_widget_bg_round)
    } else {
        setInt(android.R.id.background, "setBackgroundResource", R.drawable.ui_widget_bg)
    }

    return this
}

private fun RemoteViews.applyRoundingToBar(context: Context): RemoteViews {
    if (Settings(context).roundMode && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        setInt(R.id.widget_controls, "setBackgroundResource", R.drawable.ui_widget_bar_round)
    } else {
        setInt(R.id.widget_controls, "setBackgroundResource", R.drawable.ui_widget_bar)
    }

    return this
}

private fun RemoteViews.applyMeta(
    context: Context,
    state: WidgetComponent.WidgetState
): RemoteViews {
    applyCover(context, state)

    setTextViewText(R.id.widget_song, state.song.resolveName(context))
    setTextViewText(R.id.widget_artist, state.song.resolveIndividualArtistName(context))

    return this
}

private fun RemoteViews.applyCover(
    context: Context,
    state: WidgetComponent.WidgetState
): RemoteViews {
    if (state.cover != null) {
        setImageViewBitmap(R.id.widget_cover, state.cover)
        setContentDescription(
            R.id.widget_cover,
            context.getString(R.string.desc_album_cover, state.song.album.resolveName(context)))
    } else {
        setImageViewResource(R.id.widget_cover, R.drawable.ic_remote_default_cover_24)
        setContentDescription(R.id.widget_cover, context.getString(R.string.desc_no_cover))
    }

    return this
}

private fun RemoteViews.applyPlayPauseControls(
    context: Context,
    state: WidgetComponent.WidgetState
): RemoteViews {
    // Controls are timeline elements, override the layout direction to RTL
    setInt(R.id.widget_controls, "setLayoutDirection", View.LAYOUT_DIRECTION_LTR)

    setOnClickPendingIntent(
        R.id.widget_play_pause,
        context.newBroadcastPendingIntent(PlaybackService.ACTION_PLAY_PAUSE))

    setImageViewResource(
        R.id.widget_play_pause,
        if (state.isPlaying) {
            R.drawable.ic_pause_24
        } else {
            R.drawable.ic_play_24
        })

    return this
}

private fun RemoteViews.applyBasicControls(
    context: Context,
    state: WidgetComponent.WidgetState
): RemoteViews {
    applyPlayPauseControls(context, state)

    setOnClickPendingIntent(
        R.id.widget_skip_prev, context.newBroadcastPendingIntent(PlaybackService.ACTION_SKIP_PREV))

    setOnClickPendingIntent(
        R.id.widget_skip_next, context.newBroadcastPendingIntent(PlaybackService.ACTION_SKIP_NEXT))

    return this
}

private fun RemoteViews.applyFullControls(
    context: Context,
    state: WidgetComponent.WidgetState
): RemoteViews {
    applyBasicControls(context, state)

    setOnClickPendingIntent(
        R.id.widget_repeat,
        context.newBroadcastPendingIntent(PlaybackService.ACTION_INC_REPEAT_MODE))

    setOnClickPendingIntent(
        R.id.widget_shuffle,
        context.newBroadcastPendingIntent(PlaybackService.ACTION_INVERT_SHUFFLE))

    val shuffleRes =
        when {
            state.isShuffled -> R.drawable.ic_shuffle_on_24
            else -> R.drawable.ic_shuffle_off_24
        }

    val repeatRes =
        when (state.repeatMode) {
            RepeatMode.NONE -> R.drawable.ic_repeat_off_24
            RepeatMode.ALL -> R.drawable.ic_repeat_on_24
            RepeatMode.TRACK -> R.drawable.ic_repeat_one_24
        }

    setImageViewResource(R.id.widget_shuffle, shuffleRes)
    setImageViewResource(R.id.widget_repeat, repeatRes)

    return this
}
