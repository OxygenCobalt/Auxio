package org.oxycblt.auxio.widgets

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.SizeF
import android.widget.RemoteViews
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.coil.loadBitmap
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.ui.newMainIntent
import org.oxycblt.auxio.widgets.forms.FullWidgetForm
import org.oxycblt.auxio.widgets.forms.SmallWidgetForm
import org.oxycblt.auxio.widgets.forms.WidgetForm

/**
 * Auxio's one and only appwidget. This widget follows a more unorthodox approach, effectively
 * packing what could be considered 3 or 4 widgets into a single responsive widget. More specifically:
 *
 * - TODO?: For widgets 3x1 or lower, show a text-only view with minimal controls
 * - TODO?: For widgets 4x1, show a minimized view with album art
 * - For widgets Wx2 or higher, show an expanded view with album art and basic controls
 * - For widgets 4x2 or higher, show a complete view with all playback controls
 *
 * For more specific details about these sub-widgets, see [WidgetForm].
 */
class WidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        logD("Sending update intent to PlaybackService")

        appWidgetManager.applyViews(context, defaultViews(context))

        val intent = Intent(ACTION_WIDGET_UPDATE)
            .addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY)

        context.sendBroadcast(intent)
    }

    /*
     * Update the widget based on the playback state.
     */
    fun update(context: Context, playbackManager: PlaybackStateManager) {
        val manager = AppWidgetManager.getInstance(context)

        val song = playbackManager.song

        if (song == null) {
            manager.applyViews(context, defaultViews(context))
            return
        }

        // What we do here depends on how we're responding to layout changes.
        // If we are on Android 11 or below, then we use the current widget form and default
        // to SmallWidgetForm if one couldn't be figured out.
        // If we are using Android S, we use the standard method of creating each RemoteView
        // instance and putting them into a Map with their size ranges. This isn't as nice
        // as it means we have to always load album art, even with the text only widget forms.
        // But it's still preferable than to the Pre-12 method.

        // FIXME: Fix the race conditions with the bitmap loading.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            loadBitmap(context, song) { bitmap ->
                val state = WidgetState(
                    song,
                    bitmap,
                    playbackManager.isPlaying,
                    playbackManager.isShuffling,
                    playbackManager.loopMode
                )

                // Map each widget form to the rough dimensions where it would look nice.
                // This might need to be adjusted.
                val views = mapOf(
                    SizeF(110f, 110f) to SmallWidgetForm().createViews(context, state),
                    SizeF(272f, 110f) to FullWidgetForm().createViews(context, state)
                )

                manager.applyViews(context, RemoteViews(views))
            }
        } else {
            loadBitmap(context, song) { bitmap ->
                val state = WidgetState(
                    song,
                    bitmap,
                    playbackManager.isPlaying,
                    playbackManager.isShuffling,
                    playbackManager.loopMode
                )

                // TODO: Make sub-12 widgets responsive.
                manager.applyViews(context, FullWidgetForm().createViews(context, state))
            }
        }
    }

    /*
     * Revert this widget to its default view
     */
    fun reset(context: Context) {
        logD("Resetting widget")

        val manager = AppWidgetManager.getInstance(context)
        manager.applyViews(context, defaultViews(context))
    }

    @SuppressLint("RemoteViewLayout")
    private fun defaultViews(context: Context): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_default)

        views.setOnClickPendingIntent(
            android.R.id.background,
            context.newMainIntent()
        )

        return views
    }

    private fun AppWidgetManager.applyViews(context: Context, views: RemoteViews) {
        val ids = getAppWidgetIds(ComponentName(context, this::class.java))

        if (ids.isNotEmpty()) {
            // Existing widgets found, update those
            ids.forEach { id ->
                updateAppWidget(id, views)
            }
        } else {
            // No existing widgets found. Fall back to the name of the widget class
            updateAppWidget(ComponentName(context, this@WidgetProvider::class.java), views)
        }
    }

    companion object {
        const val ACTION_WIDGET_UPDATE = BuildConfig.APPLICATION_ID + ".action.WIDGET_UPDATE"
    }
}
