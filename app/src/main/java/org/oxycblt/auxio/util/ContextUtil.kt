/*
 * Copyright (c) 2021 Auxio Project
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
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import kotlin.reflect.KClass
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.MainActivity

/** Shortcut to get a [LayoutInflater] from a [Context] */
val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)

/**
 * Returns whether the current UI is in night mode or not. This will work if the theme is automatic
 * as well.
 */
val Context.isNight: Boolean
    get() =
        resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES

/** Returns if this device is in landscape. */
val Context.isLandscape
    get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

/**
 * Gets a content resolver in a way that does not mangle metadata on certain OEM skins. See
 * https://github.com/OxygenCobalt/Auxio/issues/50 for more info.
 */
val Context.contentResolverSafe: ContentResolver
    get() = applicationContext.contentResolver

/**
 * Convenience method for getting a plural.
 * @param pluralsRes Resource for the plural
 * @param value Int value for the plural.
 * @return The formatted string requested
 */
fun Context.getPluralSafe(@PluralsRes pluralsRes: Int, value: Int): String {
    return try {
        resources.getQuantityString(pluralsRes, value, value)
    } catch (e: Exception) {
        handleResourceFailure(e, "plural", "<plural error>")
    }
}

/**
 * Convenience method for getting a color safely.
 * @param color The color resource
 * @return The color integer requested, or black if an error occurred.
 */
@ColorInt
fun Context.getColorSafe(@ColorRes color: Int): Int {
    return try {
        ContextCompat.getColor(this, color)
    } catch (e: Exception) {
        handleResourceFailure(e, "color", getColorSafe(android.R.color.black))
    }
}

/**
 * Convenience method for getting a [ColorStateList] resource safely.
 * @param color The color resource
 * @return The [ColorStateList] requested, or black if an error occurred.
 */
fun Context.getColorStateListSafe(@ColorRes color: Int): ColorStateList {
    return try {
        unlikelyToBeNull(ContextCompat.getColorStateList(this, color))
    } catch (e: Exception) {
        handleResourceFailure(e, "color state list", getColorSafe(android.R.color.black).stateList)
    }
}

/**
 * Convenience method for getting a color attribute safely.
 * @param attr The color attribute
 * @return The attribute requested, or black if an error occurred.
 */
@ColorInt
fun Context.getAttrColorSafe(@AttrRes attr: Int): Int {
    // First resolve the attribute into its ID
    val resolvedAttr = TypedValue()
    theme.resolveAttribute(attr, resolvedAttr, true)

    // Then convert it to a proper color
    val color =
        if (resolvedAttr.resourceId != 0) {
            resolvedAttr.resourceId
        } else {
            resolvedAttr.data
        }

    return getColorSafe(color)
}

/**
 * Convenience method for getting a [Drawable] safely.
 * @param drawable The drawable resource
 * @return The drawable requested, or black if an error occurred.
 */
fun Context.getDrawableSafe(@DrawableRes drawable: Int): Drawable {
    return try {
        requireNotNull(ContextCompat.getDrawable(this, drawable))
    } catch (e: Exception) {
        handleResourceFailure(e, "drawable", ColorDrawable(getColorSafe(android.R.color.black)))
    }
}

/**
 * Convenience method for getting a dimension safely.
 * @param dimen The dimension resource
 * @return The dimension requested, or 0 if an error occurred.
 */
@Dimension
fun Context.getDimenSafe(@DimenRes dimen: Int): Float {
    return try {
        resources.getDimension(dimen)
    } catch (e: Exception) {
        handleResourceFailure(e, "dimen", 0f)
    }
}

/**
 * Convenience method for getting a dimension pixel size safely.
 * @param dimen The dimension resource
 * @return The dimension requested, in pixels, or 0 if an error occurred.
 */
@Px
fun Context.getDimenSizeSafe(@DimenRes dimen: Int): Int {
    return try {
        resources.getDimensionPixelSize(dimen)
    } catch (e: Exception) {
        handleResourceFailure(e, "dimen", 0)
    }
}

/**
 * Convenience method for getting a dimension pixel offset safely.
 * @param dimen The dimension resource
 * @return The dimension requested, in pixels, or 0 if an error occurred.
 */
@Px
fun Context.getDimenOffsetSafe(@DimenRes dimen: Int): Int {
    return try {
        resources.getDimensionPixelOffset(dimen)
    } catch (e: Exception) {
        handleResourceFailure(e, "dimen", 0)
    }
}

/**
 * Calculates the pixels of the given dimension [dp].
 * @param dp the dimension value
 * @return The equivalent amount of pixels for [dp].
 */
@Px
fun Context.pxOfDp(@Dimension dp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
        .toInt()
}

private fun <T> Context.handleResourceFailure(e: Exception, what: String, default: T): T {
    logE("$what load failed")
    e.logTraceOrThrow()
    return default
}

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

/** Create a toast using the provided string resource. */
fun Context.showToast(@StringRes str: Int) {
    Toast.makeText(applicationContext, getString(str), Toast.LENGTH_SHORT).show()
}

/** Create a [PendingIntent] that leads to Auxio's [MainActivity] */
fun Context.newMainPendingIntent(): PendingIntent =
    PendingIntent.getActivity(
        this,
        IntegerTable.REQUEST_CODE,
        Intent(this, MainActivity::class.java),
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)

/** Create a broadcast [PendingIntent] */
fun Context.newBroadcastPendingIntent(what: String): PendingIntent =
    PendingIntent.getBroadcast(
        this,
        IntegerTable.REQUEST_CODE,
        Intent(what).setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY),
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
