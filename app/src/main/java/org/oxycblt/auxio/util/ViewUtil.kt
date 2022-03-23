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

import android.content.res.ColorStateList
import android.graphics.Insets
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.WindowInsets
import androidx.annotation.ColorRes
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R

/** Converts this color to a single-color [ColorStateList]. */
val @receiver:ColorRes Int.stateList
    get() = ColorStateList.valueOf(this)

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
            mgr.spanSizeLookup =
                object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (shouldBeFullWidth(position)) spans else 1
                    }
                }
        }

        layoutManager = mgr
    }
}

/** Returns whether a recyclerview can scroll. */
fun RecyclerView.canScroll(): Boolean = computeVerticalScrollRange() > height

/**
 * Disables drop shadows on a view programmatically in a version-compatible manner. This only works
 * on Android 9 and above. Below that version, shadows will remain visible.
 */
fun View.disableDropShadowCompat() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        logD("Disabling drop shadows")
        val transparent = context.getColorSafe(android.R.color.transparent)
        outlineAmbientShadowColor = transparent
        outlineSpotShadowColor = transparent
    }
}

fun View.isUnder(x: Float, y: Float, minTouchTargetSize: Int = 0): Boolean {
    return isUnderImpl(x, left, right, (parent as View).width, minTouchTargetSize) &&
        isUnderImpl(y, top, bottom, (parent as View).height, minTouchTargetSize)
}

private fun isUnderImpl(
    position: Float,
    viewStart: Int,
    viewEnd: Int,
    parentEnd: Int,
    minTouchTargetSize: Int
): Boolean {
    val viewSize = viewEnd - viewStart

    if (viewSize >= minTouchTargetSize) {
        return position >= viewStart && position < viewEnd
    }
    var touchTargetStart = viewStart - (minTouchTargetSize - viewSize) / 2

    if (touchTargetStart < 0) {
        touchTargetStart = 0
    }

    var touchTargetEnd = touchTargetStart + minTouchTargetSize
    if (touchTargetEnd > parentEnd) {
        touchTargetEnd = parentEnd
        touchTargetStart = touchTargetEnd - minTouchTargetSize

        if (touchTargetStart < 0) {
            touchTargetStart = 0
        }
    }

    return position >= touchTargetStart && position < touchTargetEnd
}

val View.isRtl: Boolean
    get() = layoutDirection == View.LAYOUT_DIRECTION_RTL
val Drawable.isRtl: Boolean
    get() = DrawableCompat.getLayoutDirection(this) == View.LAYOUT_DIRECTION_RTL

/**
 * Resolve system bar insets in a version-aware manner. This can be used to apply padding to a view
 * that properly follows all the frustrating changes that were made between 8-11.
 */
val WindowInsets.systemBarInsetsCompat: Rect
    get() {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                getInsets(WindowInsets.Type.systemBars()).run { Rect(left, top, right, bottom) }
            }
            else -> {
                @Suppress("DEPRECATION")
                Rect(
                    systemWindowInsetLeft,
                    systemWindowInsetTop,
                    systemWindowInsetRight,
                    systemWindowInsetBottom)
            }
        }
    }

/**
 * Replaces the system bar insets in a version-aware manner. This can be used to modify the insets
 * for child views in a way that follows all of the frustrating changes that were made between 8-11.
 */
fun WindowInsets.replaceSystemBarInsetsCompat(
    left: Int,
    top: Int,
    right: Int,
    bottom: Int
): WindowInsets {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            WindowInsets.Builder(this)
                .setInsets(WindowInsets.Type.systemBars(), Insets.of(left, top, right, bottom))
                .build()
        }
        else -> {
            @Suppress("DEPRECATION") replaceSystemWindowInsets(left, top, right, bottom)
        }
    }
}
