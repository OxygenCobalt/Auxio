package org.oxycblt.auxio.widgets

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.ui.newMainIntent

/**
 * The base widget class for all widget implementations in Auxio.
 */
abstract class BaseWidget : AppWidgetProvider() {
    abstract val type: Int

    protected open fun createViews(context: Context, @LayoutRes layout: Int): RemoteViews {
        val views = RemoteViews(context.packageName, layout)

        views.setOnClickPendingIntent(
            android.R.id.background,
            context.newMainIntent()
        )

        return views
    }

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
        val manager = AppWidgetManager.getInstance(context)

        // View updates are often async due to image loading, so only push the views
        // when the callback is called.
        updateViews(context, playbackManager) { views ->
            manager.applyViews(context, views)
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

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        logD("Sending update intent to PlaybackService")

        appWidgetManager.applyViews(context, defaultViews(context))

        val intent = Intent(ACTION_WIDGET_UPDATE)
            .putExtra(KEY_WIDGET_TYPE, type)
            .addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY)

        context.sendBroadcast(intent)
    }

    @SuppressLint("RemoteViewLayout")
    protected fun defaultViews(context: Context): RemoteViews {
        return RemoteViews(
            context.packageName,
            R.layout.widget_default
        )
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
