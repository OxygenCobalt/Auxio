/*
 * Copyright (c) 2026 Auxio Project
 * SleepTimerWidgetProvider.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package org.oxycblt.auxio.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.preference.PreferenceManager
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.resolve
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.playback.SleepTimerState
import org.oxycblt.auxio.playback.service.PlaybackActions
import org.oxycblt.auxio.util.newBroadcastPendingIntent
import java.util.Locale

private const val DEFAULT_TIMER_MINUTES = 15

abstract class SleepTimerWidgetProvider : AppWidgetProvider() {
    protected abstract val layoutRes: Int

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        SleepTimerWidgetRenderer.reset(context, this::class.java, layoutRes)
        context.sendBroadcast(
            Intent(WidgetProvider.ACTION_WIDGET_UPDATE)
                .addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY)
        )
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: android.os.Bundle?,
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        context.sendBroadcast(
            Intent(WidgetProvider.ACTION_WIDGET_UPDATE)
                .addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY)
        )
    }
}

class ClearTimerWidgetProvider : SleepTimerWidgetProvider() {
    override val layoutRes = R.layout.widget_sleep_timer_clear
}

class PlaybackTimerWidgetProvider : SleepTimerWidgetProvider() {
    override val layoutRes = R.layout.widget_sleep_timer_player
}

class CompactTimerWidgetProvider : SleepTimerWidgetProvider() {
    override val layoutRes = R.layout.widget_sleep_timer_compact
}

/** Binds the three new widget families while leaving the existing adaptive widget unchanged. */
internal object SleepTimerWidgetRenderer {
    private data class WidgetSpec(
        val provider: Class<out SleepTimerWidgetProvider>,
        val layout: Int,
        val showArtist: Boolean = false,
        val showCover: Boolean = false,
    )

    private val widgetSpecs =
        listOf(
            WidgetSpec(ClearTimerWidgetProvider::class.java, R.layout.widget_sleep_timer_clear),
            WidgetSpec(
                PlaybackTimerWidgetProvider::class.java,
                R.layout.widget_sleep_timer_player,
                showArtist = true,
                showCover = true,
            ),
            WidgetSpec(CompactTimerWidgetProvider::class.java, R.layout.widget_sleep_timer_compact),
        )

    fun reset(context: Context, provider: Class<out SleepTimerWidgetProvider>, layout: Int) {
        val spec =
            widgetSpecs.firstOrNull { it.provider == provider }?.copy(layout = layout)
                ?: WidgetSpec(provider, layout)
        updateOne(
            context,
            spec,
            playback = null,
            timerState = SleepTimerState.Idle,
            defaultMinutes = defaultMinutes(context),
        )
    }

    fun updateAll(
        context: Context,
        playback: WidgetComponent.PlaybackState?,
        timerState: SleepTimerState,
        defaultMinutes: Int,
    ) {
        widgetSpecs.forEach { spec ->
            updateOne(context, spec, playback, timerState, defaultMinutes)
        }
    }

    private fun updateOne(
        context: Context,
        spec: WidgetSpec,
        playback: WidgetComponent.PlaybackState?,
        timerState: SleepTimerState,
        defaultMinutes: Int,
    ) {
        val views = RemoteViews(context.packageName, spec.layout)
        val manager = AppWidgetManager.getInstance(context)
        val component = ComponentName(context, spec.provider)

        views.setTextViewText(
            R.id.widget_song,
            playback?.song?.name?.resolve(context) ?: context.getString(R.string.def_playback),
        )
        if (spec.showArtist) {
            views.setTextViewText(
                R.id.widget_artist,
                playback?.song?.artists?.resolveNames(context).orEmpty(),
            )
            views.setViewVisibility(
                R.id.widget_artist,
                if (playback != null) android.view.View.VISIBLE else android.view.View.GONE,
            )
        }
        if (spec.showCover) {
            views.setImageViewResource(R.id.widget_cover, R.drawable.ic_remote_default_cover_24)
            playback?.cover?.let { views.setImageViewBitmap(R.id.widget_cover, it) }
        }

        views.setOnClickPendingIntent(
            R.id.widget_skip_prev,
            context.newBroadcastPendingIntent(PlaybackActions.ACTION_SKIP_PREV),
        )
        views.setOnClickPendingIntent(
            R.id.widget_skip_next,
            context.newBroadcastPendingIntent(PlaybackActions.ACTION_SKIP_NEXT),
        )
        views.setOnClickPendingIntent(
            R.id.widget_timer_toggle,
            context.newBroadcastPendingIntent(PlaybackActions.ACTION_SLEEP_TIMER_TOGGLE),
        )

        val isTimerActive = timerState !is SleepTimerState.Idle
        val timerDescription =
            if (isTimerActive) {
                context.getString(R.string.desc_sleep_timer_cancel)
            } else {
                context.getString(R.string.desc_sleep_timer_start, defaultMinutes)
            }
        views.setContentDescription(R.id.widget_timer_toggle, timerDescription)
        views.setImageViewResource(
            R.id.widget_timer_toggle,
            if (isTimerActive) R.drawable.ic_close_24 else R.drawable.ic_time_24,
        )

        when (timerState) {
            SleepTimerState.Idle -> {
                val time = String.format(Locale.getDefault(), "%02d:00", defaultMinutes)
                views.setChronometer(
                    R.id.widget_timer,
                    SystemClock.elapsedRealtime(),
                    null,
                    false,
                )
                views.setTextViewText(R.id.widget_timer, time)
                val status =
                    if (playback?.isPlaying == true) {
                        context.getString(R.string.widget_sleep_timer_idle, defaultMinutes)
                    } else {
                        context.getString(R.string.widget_sleep_timer_requires_playback)
                    }
                views.setTextViewText(R.id.widget_timer_status, status)
            }
            is SleepTimerState.CountingDown -> {
                val remaining = (timerState.deadlineMillis - System.currentTimeMillis()).coerceAtLeast(0L)
                views.setChronometer(
                    R.id.widget_timer,
                    SystemClock.elapsedRealtime() + remaining,
                    "%s",
                    true,
                )
                views.setChronometerCountDown(R.id.widget_timer, true)
                views.setTextViewText(
                    R.id.widget_timer_status,
                    context.getString(R.string.widget_sleep_timer_counting),
                )
            }
            SleepTimerState.StopAfterCurrentTrack -> {
                views.setChronometer(
                    R.id.widget_timer,
                    SystemClock.elapsedRealtime(),
                    null,
                    false,
                )
                views.setTextViewText(R.id.widget_timer, "00:00")
                views.setTextViewText(
                    R.id.widget_timer_status,
                    context.getString(R.string.widget_sleep_timer_pending),
                )
            }
        }

        manager.updateAppWidget(component, views)
    }

    private fun defaultMinutes(context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(context.getString(R.string.set_key_sleep_timer_minutes), DEFAULT_TIMER_MINUTES)
            .coerceIn(1, 180)
}
