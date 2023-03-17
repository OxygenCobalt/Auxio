/*
 * Copyright (c) 2021 Auxio Project
 * WidgetProvider.kt is part of Auxio.
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

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.SizeF
import android.view.View
import android.widget.RemoteViews
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.playback.system.PlaybackService
import org.oxycblt.auxio.ui.UISettings
import org.oxycblt.auxio.util.*

/**
 * The [AppWidgetProvider] for the "Now Playing" widget. This widget shows the current playback
 * state alongside actions to control it.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class WidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        requestUpdate(context)
        // Revert to the default layout for now until we get a response from WidgetComponent.
        // If we don't, then we will stick with the default widget layout.
        reset(context)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        // Another adaptive layout backport for API 21+: We are unable to immediately update
        // the layout ourselves when the widget dimensions change, so we need to request
        // an update from WidgetComponent first.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            requestUpdate(context)
        }
    }

    /**
     * Update the currently shown layout based on the given [WidgetComponent.PlaybackState]
     *
     * @param context [Context] required to update the widget layout.
     * @param uiSettings [UISettings] to obtain round mode configuration
     * @param state [WidgetComponent.PlaybackState] to show, or null if no playback is going on.
     */
    fun update(context: Context, uiSettings: UISettings, state: WidgetComponent.PlaybackState?) {
        if (state == null) {
            // No state, use the default widget.
            reset(context)
            return
        }

        // Create and configure each possible layout for the widget. These dimensions seem
        // arbitrary, but they are actually the minimum dimensions required to fit all of
        // the widget elements, plus some leeway for text sizing.
        val views =
            mapOf(
                SizeF(180f, 100f) to newThinLayout(context, uiSettings, state),
                SizeF(180f, 152f) to newSmallLayout(context, uiSettings, state),
                SizeF(272f, 152f) to newWideLayout(context, uiSettings, state),
                SizeF(180f, 272f) to newMediumLayout(context, uiSettings, state),
                SizeF(272f, 272f) to newLargeLayout(context, uiSettings, state))

        // Manually update AppWidgetManager with the new views.
        val awm = AppWidgetManager.getInstance(context)
        val component = ComponentName(context, this::class.java)
        try {
            awm.updateAppWidgetCompat(context, component, views)
        } catch (e: Exception) {
            // Layout update failed, gracefully degrade to the default widget.
            logW("Unable to update widget: $e")
            reset(context)
        }
    }

    /**
     * Revert to the default layout that displays "No music playing".
     *
     * @param context [Context] required to update the widget layout.
     */
    fun reset(context: Context) {
        logD("Using default layout")
        AppWidgetManager.getInstance(context)
            .updateAppWidget(ComponentName(context, this::class.java), newDefaultLayout(context))
    }

    // --- INTERNAL METHODS ---

    /**
     * Request an update from [WidgetComponent].
     *
     * @param context [Context] required to send update request broadcast.
     */
    private fun requestUpdate(context: Context) {
        logD("Sending update intent to PlaybackService")
        val intent = Intent(ACTION_WIDGET_UPDATE).addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY)
        context.sendBroadcast(intent)
    }

    // --- LAYOUTS ---

    private fun newDefaultLayout(context: Context) =
        newRemoteViews(context, R.layout.widget_default)

    private fun newThinLayout(
        context: Context,
        uiSettings: UISettings,
        state: WidgetComponent.PlaybackState
    ) =
        newRemoteViews(context, R.layout.widget_thin)
            .setupBackground(
                uiSettings,
            )
            .setupPlaybackState(context, state)
            .setupTimelineControls(context, state)

    private fun newSmallLayout(
        context: Context,
        uiSettings: UISettings,
        state: WidgetComponent.PlaybackState
    ) =
        newRemoteViews(context, R.layout.widget_small)
            .setupBar(
                uiSettings,
            )
            .setupCover(context, state)
            .setupTimelineControls(context, state)

    private fun newMediumLayout(
        context: Context,
        uiSettings: UISettings,
        state: WidgetComponent.PlaybackState
    ) =
        newRemoteViews(context, R.layout.widget_medium)
            .setupBackground(
                uiSettings,
            )
            .setupPlaybackState(context, state)
            .setupTimelineControls(context, state)

    private fun newWideLayout(
        context: Context,
        uiSettings: UISettings,
        state: WidgetComponent.PlaybackState
    ) =
        newRemoteViews(context, R.layout.widget_wide)
            .setupBar(
                uiSettings,
            )
            .setupCover(context, state)
            .setupFullControls(context, state)

    private fun newLargeLayout(
        context: Context,
        uiSettings: UISettings,
        state: WidgetComponent.PlaybackState
    ) =
        newRemoteViews(context, R.layout.widget_large)
            .setupBackground(
                uiSettings,
            )
            .setupPlaybackState(context, state)
            .setupFullControls(context, state)

    /**
     * Set up the control bar in a [RemoteViews] layout that contains one. This is a kind of
     * "floating" drawable that sits in front of the cover and contains the controls.
     */
    private fun RemoteViews.setupBar(
        uiSettings: UISettings,
    ): RemoteViews {
        // Below API 31, enable a rounded bar only if round mode is enabled.
        // On API 31+, the bar should always be round in order to fit in with other widgets.
        val background =
            if (useRoundedRemoteViews(uiSettings)) {
                R.drawable.ui_widget_bar_round
            } else {
                R.drawable.ui_widget_bar_system
            }
        setBackgroundResource(R.id.widget_controls, background)
        return this
    }

    /**
     * Set up the background in a [RemoteViews] layout that contains one. This is largely
     * self-explanatory, being a solid-color background that sits behind the cover and controls.
     */
    private fun RemoteViews.setupBackground(
        uiSettings: UISettings,
    ): RemoteViews {
        // Below API 31, enable a rounded background only if round mode is enabled.
        // On API 31+, the background should always be round in order to fit in with other
        // widgets.
        val background =
            if (useRoundedRemoteViews(uiSettings)) {
                R.drawable.ui_widget_bg_round
            } else {
                R.drawable.ui_widget_bg_system
            }
        setBackgroundResource(android.R.id.background, background)
        return this
    }

    /**
     * Set up the album cover in a [RemoteViews] layout that contains one.
     *
     * @param context [Context] required to set up the view.
     * @param state Current [WidgetComponent.PlaybackState] to display.
     */
    private fun RemoteViews.setupCover(
        context: Context,
        state: WidgetComponent.PlaybackState
    ): RemoteViews {
        if (state.cover != null) {
            setImageViewBitmap(R.id.widget_cover, state.cover)
            setContentDescription(
                R.id.widget_cover,
                context.getString(R.string.desc_album_cover, state.song.album.resolveName(context)))
        } else {
            // We are unable to use the typical placeholder cover with the song item due to
            // limitations with the corner radius. Instead use a custom-made album icon as the
            // placeholder.
            setImageViewResource(R.id.widget_cover, R.drawable.ic_remote_default_cover_24)
            setContentDescription(R.id.widget_cover, context.getString(R.string.desc_no_cover))
        }

        return this
    }

    /**
     * Set up the album cover, song title, and artist name in a [RemoteViews] layout that contains
     * them.
     *
     * @param context [Context] required to set up the view.
     * @param state Current [WidgetComponent.PlaybackState] to display.
     */
    private fun RemoteViews.setupPlaybackState(
        context: Context,
        state: WidgetComponent.PlaybackState
    ): RemoteViews {
        setupCover(context, state)
        setTextViewText(R.id.widget_song, state.song.resolveName(context))
        setTextViewText(R.id.widget_artist, state.song.artists.resolveNames(context))
        return this
    }

    /**
     * Set up the play/pause button in a [RemoteViews] layout that contains one.
     *
     * @param context [Context] required to set up the view.
     * @param state Current [WidgetComponent.PlaybackState] to display.
     */
    private fun RemoteViews.setupBasicControls(
        context: Context,
        state: WidgetComponent.PlaybackState
    ): RemoteViews {
        // Hook the play/pause button to the play/pause broadcast that will be recognized
        // by PlaybackService.
        setOnClickPendingIntent(
            R.id.widget_play_pause,
            context.newBroadcastPendingIntent(PlaybackService.ACTION_PLAY_PAUSE))

        // Set up the play/pause button appearance. Like the Android 13 media controls, use
        // a circular FAB when paused, and a squircle FAB when playing. This does require us
        // to disable the ripple animation sadly, as  it will glitch when this is used. The
        // shape change should act as a similar signal.
        val icon: Int
        val background: Int
        if (state.isPlaying) {
            icon = R.drawable.ic_pause_24
            background = R.drawable.ui_remote_fab_container_playing
        } else {
            icon = R.drawable.ic_play_24
            background = R.drawable.ui_remote_fab_container_paused
        }

        setImageViewResource(R.id.widget_play_pause, icon)
        setBackgroundResource(R.id.widget_play_pause, background)

        return this
    }

    /**
     * Set up the play/pause and skip previous/next button in a [RemoteViews] layout that contains
     * them.
     *
     * @param context [Context] required to set up the view.
     * @param state Current [WidgetComponent.PlaybackState] to display.
     */
    private fun RemoteViews.setupTimelineControls(
        context: Context,
        state: WidgetComponent.PlaybackState
    ): RemoteViews {
        // Timeline controls contain the basic controls, set those up
        setupBasicControls(context, state)
        // Timeline elements should always be left-to-right.
        setLayoutDirection(R.id.widget_controls, View.LAYOUT_DIRECTION_LTR)
        // Hook the skip buttons to the respective broadcasts that can be recognized
        // by PlaybackService.
        setOnClickPendingIntent(
            R.id.widget_skip_prev,
            context.newBroadcastPendingIntent(PlaybackService.ACTION_SKIP_PREV))
        setOnClickPendingIntent(
            R.id.widget_skip_next,
            context.newBroadcastPendingIntent(PlaybackService.ACTION_SKIP_NEXT))
        return this
    }

    /**
     * Set up the play/pause, skip previous/next, and repeat/shuffle buttons in a [RemoteViews] that
     * contains them.
     *
     * @param context [Context] required to set up the view.
     * @param state Current [WidgetComponent.PlaybackState] to display.
     */
    private fun RemoteViews.setupFullControls(
        context: Context,
        state: WidgetComponent.PlaybackState
    ): RemoteViews {
        // Full controls contain timeline controls, make are set those.
        setupTimelineControls(context, state)

        // Hook the repeat/shuffle buttons to the respective broadcasts that can
        // be recognized by PlaybackService.
        setOnClickPendingIntent(
            R.id.widget_repeat,
            context.newBroadcastPendingIntent(PlaybackService.ACTION_INC_REPEAT_MODE))
        setOnClickPendingIntent(
            R.id.widget_shuffle,
            context.newBroadcastPendingIntent(PlaybackService.ACTION_INVERT_SHUFFLE))

        // Set up the repeat/shuffle buttons. When working with RemoteViews, we will
        // need to hard-code different accent tinting configurations, as stateful drawables
        // are unsupported.
        val repeatRes =
            when (state.repeatMode) {
                RepeatMode.NONE -> R.drawable.ic_repeat_off_24
                RepeatMode.ALL -> R.drawable.ic_repeat_on_24
                RepeatMode.TRACK -> R.drawable.ic_repeat_one_24
            }
        setImageViewResource(R.id.widget_repeat, repeatRes)

        val shuffleRes =
            when {
                state.isShuffled -> R.drawable.ic_shuffle_on_24
                else -> R.drawable.ic_shuffle_off_24
            }
        setImageViewResource(R.id.widget_shuffle, shuffleRes)

        return this
    }

    companion object {
        /**
         * Broadcast when [WidgetProvider] desires to update it's widget with new information.
         * Responsible background tasks should intercept this and relay the message to
         * [WidgetComponent].
         */
        const val ACTION_WIDGET_UPDATE = BuildConfig.APPLICATION_ID + ".action.WIDGET_UPDATE"
    }
}
