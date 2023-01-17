/*
 * Copyright (c) 2022 Auxio Project
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
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.SizeF
import android.widget.RemoteViews
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import kotlin.math.sqrt
import org.oxycblt.auxio.ui.UISettings
import org.oxycblt.auxio.util.isLandscape
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.newMainPendingIntent

/**
 * Create a [RemoteViews] instance with the specified layout and an automatic click handler to open
 * the Auxio activity.
 * @param context [Context] required to create [RemoteViews].
 * @param layoutRes Resource ID of the layout to use. Must be compatible with [RemoteViews].
 * @return A new [RemoteViews] instance with the specified configuration.
 */
fun newRemoteViews(context: Context, @LayoutRes layoutRes: Int): RemoteViews {
    val views = RemoteViews(context.packageName, layoutRes)
    views.setOnClickPendingIntent(android.R.id.background, context.newMainPendingIntent())
    return views
}

/**
 * Get an image size guaranteed to not exceed the [RemoteViews] bitmap memory limit, assuming that
 * there is only one image.
 * @param context [Context] required to perform calculation.
 * @param reduce Optional multiplier to reduce the image size. Recommended value is 2 to avoid
 * device-specific variations in memory limit.
 * @return The dimension of a bitmap that can be safely used in [RemoteViews].
 */
fun getSafeRemoteViewsImageSize(context: Context, reduce: Float = 2f): Int {
    val metrics = context.resources.displayMetrics
    val sw = metrics.widthPixels
    val sh = metrics.heightPixels
    // Maximum size is 1/3 total screen area * 4 bytes per pixel. Reverse
    // that to obtain the image size.
    return sqrt((6f / 4f / reduce) * sw * sh).toInt()
}

/**
 * Set the background resource of a [RemoteViews] View.
 * @param viewId The ID of the view to update.
 * @param drawableRes The resource ID of the drawable to set the background to.
 */
fun RemoteViews.setBackgroundResource(@IdRes viewId: Int, @DrawableRes drawableRes: Int) {
    setInt(viewId, "setBackgroundResource", drawableRes)
}

/**
 * Set the layout direction of a [RemoteViews] view.
 * @param viewId The ID of the view to update.
 * @param layoutDirection The layout direction to apply to the view,
 */
fun RemoteViews.setLayoutDirection(@IdRes viewId: Int, layoutDirection: Int) {
    setInt(viewId, "setLayoutDirection", layoutDirection)
}

/**
 * Update the app widget layouts corresponding to the given [WidgetProvider] [ComponentName] with an
 * adaptive layout, in a version-compatible manner.
 * @param context [Context] required to backport adaptive layout behavior.
 * @param component [ComponentName] of the app widget layout to update.
 * @param views Mapping between different size classes and [RemoteViews] instances.
 * @see RemoteViews
 */
fun AppWidgetManager.updateAppWidgetCompat(
    context: Context,
    component: ComponentName,
    views: Map<SizeF, RemoteViews>
) {
    check(views.isNotEmpty()) { "Must provide a non-empty map" }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Can use adaptive widgets from API 31+.
        updateAppWidget(component, RemoteViews(views))
    } else {
        // Backport adaptive widgets to API 21+.
        // Each app widget has independent dimensions, so we iterate through them all
        // and do this for each.
        for (id in getAppWidgetIds(component)) {
            val options = getAppWidgetOptions(id)

            // Depending on the device orientation, the size of the app widget will be
            // composed differently.
            val width: Int
            val height: Int
            if (context.isLandscape) {
                // Width will be larger in landscape, so use MAX_WIDTH.
                width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
                height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
            } else {
                // Height will be larger in portrait, so use MAX_HEIGHT.
                width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
                height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
            }
            logD("Assuming dimens are ${width}x$height")

            // Find the layout with the greatest area that fits entirely within
            // the app widget. This is what we will use. Fall back to the smallest layout
            // otherwise.
            val layout =
                views.keys
                    .filter { it.width <= width && it.height <= height }
                    .maxByOrNull { it.height * it.width }
                    ?: views.minBy { it.key.width * it.key.height }.key
            logD("Using layout $layout ${views.contains(layout)}")

            updateAppWidget(id, views[layout])
        }
    }
}

/**
 * Returns whether rounded UI elements are appropriate for the widget, either based on the current
 * settings or if the widget has to fit in aesthetically with other widgets.
 * @param context [Context] configuration to use.
 * @return true if to use round mode, false otherwise.
 */
fun useRoundedRemoteViews(context: Context) =
    UISettings.from(context).roundMode || Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
