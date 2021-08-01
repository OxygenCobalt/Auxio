package org.oxycblt.auxio.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.MainActivity
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.playback.state.PlaybackStateManager

/**
 * The base widget class for all widget implementations in Auxio.
 */
abstract class BaseWidget : AppWidgetProvider() {
    abstract val type: Int

    /*
     * Returns the default view for this widget. This should be the "No music playing" screen
     * in pretty much all cases.
     */
    protected abstract fun getDefaultViews(context: Context): RemoteViews

    /*
     * Update views based off of the playback state. This job is asynchronous and should be
     * notified as completed by calling [onDone]
     */
    protected abstract fun updateViews(
        context: Context,
        playbackManager: PlaybackStateManager,
        onDone: (RemoteViews) -> Unit
    )

    /*
     * Update the widget based on the playback state.
     */
    fun update(context: Context, playbackManager: PlaybackStateManager) {
        logD("Dispatching playback state update")

        val manager = AppWidgetManager.getInstance(context)

        // View updates are often async due to image loading, so only push the views
        // when the callback is called.
        updateViews(context, playbackManager) { views ->
            manager.applyViews(context, views)
        }
    }

    /*
     * Stop this widget, reverting it to its default state.
     */
    fun stop(context: Context) {
        logD("Stopping widget")

        val manager = AppWidgetManager.getInstance(context)
        manager.applyViews(context, getDefaultViews(context))
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetManager.applyViews(context, getDefaultViews(context))

        logD("Sending update intent to PlaybackService")

        val intent = Intent(ACTION_WIDGET_UPDATE)
            .putExtra(KEY_WIDGET_TYPE, type)
            .addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY)

        context.sendBroadcast(intent)
    }

    protected fun getRemoteViews(context: Context, @LayoutRes layout: Int): RemoteViews {
        val views = RemoteViews(context.packageName, layout)

        views.setOnClickPendingIntent(
            android.R.id.background,
            PendingIntent.getActivity(
                context, 0xA0A0, Intent(context, MainActivity::class.java),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
            )
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
            updateAppWidget(ComponentName(context, this@BaseWidget::class.java), views)
        }
    }

    companion object {
        const val ACTION_WIDGET_UPDATE = BuildConfig.APPLICATION_ID + ".action.WIDGET_UPDATE"
        const val KEY_WIDGET_TYPE = BuildConfig.APPLICATION_ID + ".key.WIDGET_TYPE"
    }
}

// PLAN:
// - Service calls methods on the widgets, think a subset of PlaybackStateManager.Callback
// - Widgets send BACK broadcasts to control playback, as other parts of the code do
// - Since widgets are broadcastrecievers, this is okay, shouldn't cause memory leaks
// - Can't use playbackstatemanager here since that would make the app unkillable
// - Callbacks also need to handle PlaybackService dying and being unable to send any
// more updates, and PlaybackService starting up as well
