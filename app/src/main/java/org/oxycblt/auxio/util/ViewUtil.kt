/*
 * Copyright (c) 2021 Auxio Project
 * ViewUtil.kt is part of Auxio.
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

package org.oxycblt.auxio.util

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R

/**
 * Apply the recommended spans for a [RecyclerView].
 *
 * @param shouldBeFullWidth Optional callback for determining whether an item should be full-width,
 * regardless of spans
 */
fun RecyclerView.applySpans(shouldBeFullWidth: ((Int) -> Boolean)? = null) {
    val spans = if (context.isLandscape()) {
        if (context.isXLTablet()) 3 else 2
    } else {
        if (context.isXLTablet()) 2 else 1
    }

    if (spans > 1) {
        val mgr = GridLayoutManager(context, spans)

        if (shouldBeFullWidth != null) {
            mgr.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (shouldBeFullWidth(position)) spans else 1
                }
            }
        }

        layoutManager = mgr
    }
}

/**
 * Disable an image button.
 */
fun ImageButton.disable() {
    if (isEnabled) {
        imageTintList = R.color.inactive.resolveStateList(context)
        isEnabled = false
    }
}

/**
 * Set a [TextView] text color, without having to resolve the resource.
 */
fun TextView.setTextColorResource(@ColorRes color: Int) {
    setTextColor(color.resolveColor(context))
}

/**
 * Returns whether a recyclerview can scroll.
 */
fun RecyclerView.canScroll(): Boolean = computeVerticalScrollRange() > height

/**
 * Resolve a color.
 * @param context [Context] required
 * @return The resolved color, black if the resolving process failed.
 */
@ColorInt
fun @receiver:ColorRes Int.resolveColor(context: Context): Int {
    return try {
        ContextCompat.getColor(context, this)
    } catch (e: Resources.NotFoundException) {
        logE("Attempted color load failed: ${e.stackTraceToString()}")

        // Default to the emergency color [Black] if the loading fails.
        ContextCompat.getColor(context, android.R.color.black)
    }
}

/**
 * Resolve a color and turn it into a [ColorStateList]
 * @param context [Context] required
 * @return The resolved color as a [ColorStateList]
 * @see resolveColor
 */
fun @receiver:ColorRes Int.resolveStateList(context: Context) =
    ColorStateList.valueOf(resolveColor(context))

/**
 * Resolve a drawable resource into a [Drawable]
 */
fun @receiver:DrawableRes Int.resolveDrawable(context: Context) =
    requireNotNull(ContextCompat.getDrawable(context, this))

/**
 * Resolve this int into a color as if it was an attribute
 */
@ColorInt
fun @receiver:AttrRes Int.resolveAttr(context: Context): Int {
    // Convert the attribute into its color
    val resolvedAttr = TypedValue()
    context.theme.resolveAttribute(this, resolvedAttr, true)

    // Then convert it to a proper color
    val color = if (resolvedAttr.resourceId != 0) {
        resolvedAttr.resourceId
    } else {
        resolvedAttr.data
    }

    return color.resolveColor(context)
}

/**
 * Check if edge-to-edge is on. Really a glorified version check.
 */
fun isEdgeOn(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

/**
 * Check if we are in the "Irregular" landscape mode (e.g landscape, but nav bar is on the sides)
 * Used to disable most of edge-to-edge if that's the case, as I cant get it to work on this mode.
 * @return True if we are in the irregular landscape mode, false if not.
 */
fun Activity.isIrregularLandscape(): Boolean {
    return isLandscape() && !isSystemBarOnBottom(this)
}

/**
 * Check if the system bars are on the bottom.
 * @return If the system bars are on the bottom, false if no.
 */
private fun isSystemBarOnBottom(activity: Activity): Boolean {
    val metrics = DisplayMetrics()

    var width: Int
    var height: Int

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        activity.windowManager.currentWindowMetrics.bounds.also {
            width = it.width()
            height = it.height()
        }
    } else {
        @Suppress("DEPRECATION")
        activity.getSystemServiceSafe(WindowManager::class).apply {
            defaultDisplay.getMetrics(metrics)

            width = metrics.widthPixels
            height = metrics.heightPixels
        }
    }

    val config = activity.resources.configuration
    val canMove = (width != height && config.smallestScreenWidthDp < 600)

    return (!canMove || width < height)
}
