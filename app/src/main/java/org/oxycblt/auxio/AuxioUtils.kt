/*
 * Copyright (c) 2021 Auxio Project
 * AuxioUtils.kt is part of Auxio.
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

package org.oxycblt.auxio

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Looper
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KClass

const val INTENT_REQUEST_CODE = 0xA0A0

// --- VIEW CONFIGURATION ---

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
 * Get the span count for most RecyclerViews. These probably work right on most displays. Trust me.
 */
val RecyclerView.spans: Int get() =
    if (context.isLandscape()) {
        if (context.isXLTablet()) 3 else 2
    } else {
        if (context.isXLTablet()) 2 else 1
    }

/**
 * Returns whether a recyclerview can scroll.
 */
fun RecyclerView.canScroll(): Boolean = computeVerticalScrollRange() > height

// --- CONVENIENCE ---

/**
 * Shortcut to get a [LayoutInflater] from a [Context]
 */
val Context.inflater: LayoutInflater get() = LayoutInflater.from(this)

/**
 * Returns whether the current UI is in night mode or not. This will work if the theme is
 * automatic as well.
 */
val Context.isNight: Boolean get() =
    resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
        Configuration.UI_MODE_NIGHT_YES

/**
 * Convenience method for getting a system service without nullability issues.
 * @param T The system service in question.
 * @param serviceClass The service's kotlin class [Java class will be used in function call]
 * @return The system service
 * @throws IllegalArgumentException If the system service cannot be retrieved.
 */
fun <T : Any> Context.getSystemServiceSafe(serviceClass: KClass<T>): T {
    return requireNotNull(ContextCompat.getSystemService(this, serviceClass.java)) {
        "System service ${serviceClass.simpleName} could not be instantiated"
    }
}

/**
 * Create a broadcast [PendingIntent]
 */
fun Context.newBroadcastIntent(what: String): PendingIntent {
    return PendingIntent.getBroadcast(
        this, INTENT_REQUEST_CODE, Intent(what),
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.FLAG_IMMUTABLE
        else 0
    )
}

/**
 * Create a [PendingIntent] that leads to Auxio's [MainActivity]
 */
fun Context.newMainIntent(): PendingIntent {
    return PendingIntent.getActivity(
        this, INTENT_REQUEST_CODE, Intent(this, MainActivity::class.java),
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.FLAG_IMMUTABLE
        else 0
    )
}

/**
 * Create a toast using the provided string resource.
 */
fun Context.showToast(@StringRes str: Int) {
    Toast.makeText(applicationContext, getString(str), Toast.LENGTH_SHORT).show()
}

/**
 * Convenience method for getting a plural.
 * @param pluralsRes Resource for the plural
 * @param value Int value for the plural.
 * @return The formatted string requested
 */
fun Context.getPlural(@PluralsRes pluralsRes: Int, value: Int): String {
    return resources.getQuantityString(pluralsRes, value, value)
}

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
 * Resolve a drawable resource into an [AnimatedVectorDrawable]
 * @see resolveDrawable
 */
fun @receiver:DrawableRes Int.toAnimDrawable(context: Context) =
    resolveDrawable(context) as AnimatedVectorDrawable

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
 * Shortcut for querying all items in a database and running [block] with the cursor returned.
 * Will not run if the cursor is null.
 */
fun <R> SQLiteDatabase.queryAll(tableName: String, block: (Cursor) -> R) =
    query(tableName, null, null, null, null, null, null)?.use(block)

/**
 * Assert that we are on a background thread.
 */
fun assertBackgroundThread() {
    check(Looper.myLooper() != Looper.getMainLooper()) {
        "This operation must be ran on a background thread."
    }
}

/**
 * Assert that we are on a foreground thread.
 */
fun assertMainThread() {
    check(Looper.myLooper() == Looper.getMainLooper()) {
        "This operation must be ran on the main thread"
    }
}

// --- CONFIGURATION ---

/**
 * Check if edge is on. Really a glorified version check.
 * @return Whether edge is on.
 */
fun isEdgeOn(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

/**
 * Determine if the device is currently in landscape.
 */
fun Context.isLandscape(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

/**
 * Determine if we are in tablet mode or not
 */
fun Context.isTablet(): Boolean {
    val layout = resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

    return layout == Configuration.SCREENLAYOUT_SIZE_XLARGE ||
        layout == Configuration.SCREENLAYOUT_SIZE_LARGE
}

/**
 * Determine if the tablet is XLARGE, ignoring normal tablets.
 */
fun Context.isXLTablet(): Boolean {
    val layout = resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

    return layout == Configuration.SCREENLAYOUT_SIZE_XLARGE
}

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
