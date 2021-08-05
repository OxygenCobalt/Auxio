package org.oxycblt.auxio.widgets

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.SizeF
import android.widget.RemoteViews
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.coil.loadBitmap
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.ui.isLandscape
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
        applyDefaultViews(context, appWidgetManager)
        requestUpdate(context)
    }

    /*
     * Update the widget based on the playback state.
     */
    fun update(context: Context, playbackManager: PlaybackStateManager) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val song = playbackManager.song

        if (song == null) {
            applyDefaultViews(context, appWidgetManager)
            return
        }

        // FIXME: Fix the race conditions with the bitmap loading.

        loadBitmap(context, song) { bitmap ->
            val state = WidgetState(
                song,
                bitmap,
                playbackManager.isPlaying,
                playbackManager.isShuffling,
                playbackManager.loopMode
            )

            // Map each widget form to the rough dimensions where it would look at least okay.
            val views = mapOf(
                SizeF(110f, 110f) to SmallWidgetForm().createViews(context, state),
                SizeF(272f, 110f) to FullWidgetForm().createViews(context, state)
            )

            appWidgetManager.applyViewsCompat(context, views)
        }
    }

    /*
     * Revert this widget to its default view
     */
    fun reset(context: Context) {
        logD("Resetting widget")

        applyDefaultViews(context, AppWidgetManager.getInstance(context))
    }

    @SuppressLint("RemoteViewLayout")
    private fun applyDefaultViews(context: Context, manager: AppWidgetManager) {
        val views = RemoteViews(context.packageName, R.layout.widget_default)

        views.setOnClickPendingIntent(
            android.R.id.background,
            context.newMainIntent()
        )

        manager.updateAppWidget(ComponentName(context, this::class.java), views)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            // We can't resize the widget until we can generate the views, request an update
            // from PlaybackService.
            requestUpdate(context)
        }
    }

    private fun requestUpdate(context: Context) {
        logD("Sending update intent to PlaybackService")

        val intent = Intent(ACTION_WIDGET_UPDATE)
            .addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY)

        context.sendBroadcast(intent)
    }

    private fun AppWidgetManager.applyViewsCompat(
        context: Context,
        views: Map<SizeF, RemoteViews>
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Widgets are automatically responsive on Android 12, no need to do anything.
            updateAppWidget(
                ComponentName(context, WidgetProvider::class.java),
                RemoteViews(views)
            )
        } else {
            // Otherwise, we try our best to backport the responsive behavior to older versions.
            // This is mostly a guess based on RemoteView's documentation, and it has some
            // problems [most notably UI jittering when resizing]. Its better than just using
            // one layout though. It may be improved once Android 12's source is released.

            // Theres a non-zero likelihood that this code ends up being copy-pasted all over
            // by other apps that are trying to refresh their widgets for Android 12. So, if
            // you're doing that than uh...hi.

            // Each widget has independent dimensions, so we iterate through them all
            // and do this for each.
            val ids = getAppWidgetIds(ComponentName(context, WidgetProvider::class.java))

            for (id in ids) {
                val options = getAppWidgetOptions(id)

                if (options != null) {
                    var width: Int
                    var height: Int

                    // AFAIK, Landscape mode uses MAX_WIDTH and MIN_HEIGHT, while Portrait
                    // uses MIN_WIDTH and MAX_HEIGHT
                    if (isLandscape(context.resources)) {
                        height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
                        width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
                    } else {
                        width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
                        height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
                    }

                    logD("Assuming widget dimens are ${width}x$height")

                    // Find layouts that fit into the widget
                    val candidates = mutableListOf<SizeF>()

                    for (size in views.keys) {
                        if (size.width < width && size.height < height) {
                            candidates.add(size)
                        }
                    }

                    // Find the layout with the greatest area. This is what we use.
                    val layout = candidates.maxByOrNull { it.height * it.width }

                    if (layout != null) {
                        logD("Using widget layout $layout")

                        updateAppWidget(id, views[layout])

                        continue
                    }
                }

                // No layout fits. Just use the smallest view.

                logD("No widget layout found")

                val minimum = requireNotNull(
                    views.minByOrNull { it.key.width * it.key.height }?.value
                )

                updateAppWidget(id, minimum)
            }
        }
    }

    companion object {
        const val ACTION_WIDGET_UPDATE = BuildConfig.APPLICATION_ID + ".action.WIDGET_UPDATE"
    }
}
