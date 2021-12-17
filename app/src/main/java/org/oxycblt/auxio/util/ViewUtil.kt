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

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Insets
import android.graphics.Rect
import android.os.Build
import android.util.TypedValue
import android.view.WindowInsets
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
    val spans = resources.getInteger(R.integer.recycler_spans)

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
    ContextCompat.getColorStateList(context, this)

/*
 * Resolve a color and turn it into a [ColorStateList]
 * @param context [Context] required
 * @return The resolved color as a [ColorStateList]
 * @see resolveColor
 */
fun @receiver:DrawableRes Int.resolveDrawable(context: Context) =
    requireNotNull(ContextCompat.getDrawable(context, this))

/**
 * Resolve this int into a color as if it was an attribute
 */
@ColorInt
fun @receiver:AttrRes Int.resolveAttr(context: Context): Int {
    // First resolve the attribute into its ID
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
 * Resolve window insets in a version-aware manner. This can be used to apply padding to
 * a view that properly follows all the frustrating changes that were made between 8-11.
 */
val WindowInsets.systemBarsCompat: Rect get() {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            getInsets(WindowInsets.Type.systemBars()).run {
                Rect(left, top, right, bottom)
            }
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
            @Suppress("DEPRECATION")
            Rect(
                systemWindowInsetLeft,
                systemWindowInsetTop,
                systemWindowInsetRight,
                systemWindowInsetBottom
            )
        }

        else -> Rect(0, 0, 0, 0)
    }
}

fun WindowInsets.replaceInsetsCompat(left: Int, top: Int, right: Int, bottom: Int): WindowInsets {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            WindowInsets.Builder(this)
                .setInsets(
                    WindowInsets.Type.systemBars(),
                    Insets.of(left, top, right, bottom)
                )
                .build()
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
            @Suppress("DEPRECATION")
            replaceSystemWindowInsets(
                left, top, right, bottom
            )
        }

        else -> this
    }
}
