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

import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.SizeF
import android.widget.RemoteViews
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.util.isLandscape
import org.oxycblt.auxio.util.logD
import kotlin.math.min

/**
 * Auxio's one and only appwidget. This widget follows a more unorthodox approach, effectively
 * packing what could be considered multiple widgets into a single responsive widget.
 *
 * This widget is also able to backport it's responsive behavior to android versions below 12,
 * albeit with some issues, such as UI jittering and a layout not being picked when the orientation
 * changes. This is tolerable.
 *
 * For more specific details about these sub-widgets, see Forms.kt.
 */
class WidgetProvider : AppWidgetProvider() {
    /*
     * Update the widget based on the playback state.
     */
    fun update(context: Context, playbackManager: PlaybackStateManager) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val song = playbackManager.song

        if (song == null) {
            reset(context)
            return
        }

        loadWidgetBitmap(context, song) { bitmap ->
            val state = WidgetState(
                song,
                bitmap,
                playbackManager.isPlaying,
                playbackManager.isShuffling,
                playbackManager.loopMode
            )

            // Map each widget form to the cells where it would look at least okay.
            val views = mapOf(
                SizeF(180f, 100f) to createTinyWidget(context, state),
                SizeF(180f, 152f) to createSmallWidget(context, state),
                SizeF(272f, 152f) to createWideWidget(context, state),
                SizeF(180f, 270f) to createMediumWidget(context, state),
                SizeF(272f, 270f) to createLargeWidget(context, state)
            )

            appWidgetManager.applyViewsCompat(context, views)
        }
    }

    private fun loadWidgetBitmap(context: Context, song: Song, onDone: (Bitmap?) -> Unit) {
        // Load our image so that it takes up the phone screen. This allows
        // us to get stable rounded corners for every single widget image. This probably
        // sacrifices quality in some way, but it's really the only good option.
        val metrics = context.resources.displayMetrics
        val imageSize = min(metrics.widthPixels, metrics.heightPixels)

        val coverRequest = ImageRequest.Builder(context)
            .data(song.album)
            .size(imageSize)
            .target(
                onError = { onDone(null) },
                onSuccess = { onDone(it.toBitmap()) }
            )

        // If we are on Android 12 or higher, round out the album cover.
        // This is simply to maintain stylistic cohesion with other widgets.
        // Here, we actually have to use RoundedCornersTransformation since the way
        // we get a 1:1 aspect ratio image results in clipToOutline not working well.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val transform = RoundedCornersTransformation(
                context.resources.getDimensionPixelSize(
                    android.R.dimen.system_app_widget_inner_radius
                ).toFloat()
            )

            coverRequest.transformations(transform)
        }

        context.imageLoader.enqueue(coverRequest.build())
    }

    /*
     * Revert this widget to its default view
     */
    fun reset(context: Context) {
        logD("Resetting widget")

        AppWidgetManager.getInstance(context).updateAppWidget(
            ComponentName(context, this::class.java), createDefaultWidget(context)
        )
    }

    // --- OVERRIDES ---

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        reset(context)
        requestUpdate(context)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            // We can't resize the widget until we can generate the views, so request an update
            // from PlaybackService.
            requestUpdate(context)
        }
    }

    // --- INTERNAL METHODS ---

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
        check(views.isNotEmpty()) { "Must provide a non-empty map" }

        val name = ComponentName(context, WidgetProvider::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Widgets are automatically responsive on Android 12, no need to do anything.
            updateAppWidget(name, RemoteViews(views))
        } else {
            // Otherwise, we try our best to backport the responsive behavior to older versions.
            // This seems to work well enough on most launchers.

            // Each widget has independent dimensions, so we iterate through them all
            // and do this for each.
            val ids = getAppWidgetIds(name)

            // Most of the major launchers seem to provide widget sizes that don't include
            // padding. Query for the padding here so we can get a sane result later on.
            val padding = AppWidgetHostView.getDefaultPaddingForWidget(context, name, null)

            val density = context.resources.displayMetrics.density
            val padW = ((padding.left + padding.right) / density).toInt()
            val padH = ((padding.top + padding.bottom) / density).toInt()

            for (id in ids) {
                val options = getAppWidgetOptions(id)

                var width: Int
                var height: Int

                // Landscape/Portrait modes use different dimen bounds
                if (context.isLandscape()) {
                    width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
                    height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
                } else {
                    width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
                    height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
                }

                height += padH
                width += padW

                logD("Assuming true widget dimens are ${width}x$height")

                // Find the layout with the greatest area that fits entirely within
                // the widget. This is what we will use.

                val candidates = mutableListOf<SizeF>()

                for (size in views.keys) {
                    if (size.width <= width && size.height <= height) {
                        candidates.add(size)
                    }
                }

                val layout = candidates.maxByOrNull { it.height * it.width }

                if (layout != null) {
                    logD("Using widget layout $layout")
                    updateAppWidget(id, views[layout])
                    continue
                } else {
                    // Default to the smallest view if no layout fits
                    logD("No widget layout found")

                    val minimum = requireNotNull(
                        views.minByOrNull { it.key.width * it.key.height }?.value
                    )

                    updateAppWidget(id, minimum)
                }
            }
        }
    }

    companion object {
        const val ACTION_WIDGET_UPDATE = BuildConfig.APPLICATION_ID + ".action.WIDGET_UPDATE"
    }
}
