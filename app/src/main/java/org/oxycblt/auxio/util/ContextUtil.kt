/*
 * Copyright (c) 2021 Auxio Project
 * ContextUtil.kt is part of Auxio.
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

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.PluralsRes
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import kotlin.reflect.KClass
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.MainActivity
import org.oxycblt.auxio.R

/**
 * Get a [LayoutInflater] instance from this [Context].
 *
 * @see LayoutInflater.from
 */
val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)

/** Whether the device is in night mode or not. */
val Context.isNight
    get() =
        resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES

/** Whether the device is in landscape mode or not. */
val Context.isLandscape
    get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

/**
 * Concatenate values in a list together in a localized manner.
 *
 * @param context [Context] require.d
 * @param map Function to map the [T] values to a string value to be concatenated.
 */
inline fun <T> List<T>.concatLocalized(context: Context, map: (T) -> String): String {
    if (isEmpty()) {
        // Nothing to do.
        return ""
    }

    var joined = map(first())
    for (i in 1..lastIndex) {
        // Chain all previous values with the next value in the list with another delimiter.
        joined = context.getString(R.string.fmt_list, joined, map(get(i)))
    }
    return joined
}

/**
 * @param pluralRes A plural resource ID.
 * @param value Int value for the plural.
 * @return The formatted string requested.
 * @brief Get a plural resource.
 */
fun Context.getPlural(@PluralsRes pluralRes: Int, value: Int) =
    resources.getQuantityString(pluralRes, value, value)

/**
 * @param integerRes An integer resource ID.
 * @return The integer resource requested.
 * @brief Get an integer resource.
 */
fun Context.getInteger(@IntegerRes integerRes: Int) = resources.getInteger(integerRes)

/**
 * Get a [ColorStateList] resource.
 *
 * @param colorRes A color resource ID.
 * @return The [ColorStateList] requested.
 */
fun Context.getColorCompat(@ColorRes colorRes: Int) =
    requireNotNull(ContextCompat.getColorStateList(this, colorRes)) {
        "Invalid resource: State list was null"
    }

/**
 * Get a [ColorStateList] pointed to by an attribute.
 *
 * @param attrRes An attribute resource ID.
 * @return The [ColorStateList] the requested attribute points to.
 */
fun Context.getAttrColorCompat(@AttrRes attrRes: Int): ColorStateList {
    // First resolve the attribute into its ID
    val resolvedAttr = TypedValue()
    theme.resolveAttribute(attrRes, resolvedAttr, true)

    // Then convert it to a proper color
    val color =
        if (resolvedAttr.resourceId != 0) {
            resolvedAttr.resourceId
        } else {
            resolvedAttr.data
        }

    return getColorCompat(color)
}

/**
 * Get a Drawable.
 *
 * @param drawableRes The Drawable resource ID.
 * @return The Drawable requested.
 */
fun Context.getDrawableCompat(@DrawableRes drawableRes: Int) =
    requireNotNull(ContextCompat.getDrawable(this, drawableRes)) {
        "Invalid resource: Drawable was null"
    }

/**
 * Get the complex (i.e DP) size of a dimension.
 *
 * @param dimenRes The dimension resource.
 * @return The size of the dimension requested, in complex units.
 */
@Dimension fun Context.getDimen(@DimenRes dimenRes: Int) = resources.getDimension(dimenRes)

/**
 * Get the pixel size of a dimension.
 *
 * @param dimenRes The dimension resource
 * @return The size of the dimension requested, in pixels
 */
@Px fun Context.getDimenPixels(@DimenRes dimenRes: Int) = resources.getDimensionPixelSize(dimenRes)

/**
 * Get an instance of the requested system service.
 *
 * @param T The system service in question.
 * @param serviceClass The service's kotlin class [Java class will be used in function call]
 * @return The system service
 * @throws IllegalArgumentException If the system service cannot be retrieved.
 */
fun <T : Any> Context.getSystemServiceCompat(serviceClass: KClass<T>) =
    requireNotNull(ContextCompat.getSystemService(this, serviceClass.java)) {
        "System service ${serviceClass.simpleName} could not be instantiated"
    }

/**
 * Create a short-length [Toast] with text from the specified string resource.
 *
 * @param stringRes The resource to the string to use in the toast.
 */
fun Context.showToast(@StringRes stringRes: Int) {
    Toast.makeText(applicationContext, getString(stringRes), Toast.LENGTH_SHORT).show()
}

/** Create a [PendingIntent] that will launch the app activity when launched. */
fun Context.newMainPendingIntent(): PendingIntent =
    PendingIntent.getActivity(
        this,
        IntegerTable.REQUEST_CODE,
        Intent(this, MainActivity::class.java).setAction(Intent.ACTION_MAIN),
        PendingIntent.FLAG_IMMUTABLE)

/**
 * Create a [PendingIntent] that will broadcast the specified command when launched.
 *
 * @param action The action to broadcast when the [PendingIntent] is launched.
 */
fun Context.newBroadcastPendingIntent(action: String): PendingIntent =
    PendingIntent.getBroadcast(
        this,
        IntegerTable.REQUEST_CODE,
        Intent(action).setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY),
        PendingIntent.FLAG_IMMUTABLE)
