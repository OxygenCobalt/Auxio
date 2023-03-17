/*
 * Copyright (c) 2021 Auxio Project
 * FrameworkUtil.kt is part of Auxio.
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
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.Insets
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Get if this [View] contains the given [PointF], with optional leeway.
 *
 * @param x The x value of the point to check.
 * @param y The y value of the point to check.
 * @param minTouchTargetSize A minimum size to use when checking the value. This can be used to
 *   extend the range where a point is considered "contained" by the [View] beyond it's actual size.
 * @return true if the [PointF] is contained by the view, false otherwise. Adapted from
 *   AndroidFastScroll: https://github.com/zhanghai/AndroidFastScroll
 */
fun View.isUnder(x: Float, y: Float, minTouchTargetSize: Int = 0) =
    isUnderImpl(x, left, right, (parent as View).width, minTouchTargetSize) &&
        isUnderImpl(y, top, bottom, (parent as View).height, minTouchTargetSize)

/**
 * Internal implementation of [isUnder].
 *
 * @param position The position to check.
 * @param viewStart The start of the view bounds, on the same axis as [position].
 * @param viewEnd The end of the view bounds, on the same axis as [position]
 * @param parentEnd The end of the parent bounds, on the same axis as [position].
 * @param minTouchTargetSize The minimum size to use when checking if the value is in range.
 */
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

/** Whether this [View] is using an RTL layout direction. */
val View.isRtl: Boolean
    get() = layoutDirection == View.LAYOUT_DIRECTION_RTL

/** Whether this [Drawable] is using an RTL layout direction. */
val Drawable.isRtl: Boolean
    get() = DrawableCompat.getLayoutDirection(this) == View.LAYOUT_DIRECTION_RTL

/** Get a [Context] from a [ViewBinding]'s root [View]. */
val ViewBinding.context: Context
    get() = root.context

/**
 * Compute if this [RecyclerView] can scroll through their items, or if the items can all fit on one
 * screen.
 */
fun RecyclerView.canScroll() = computeVerticalScrollRange() > height

/**
 * Fix the double ripple that appears in MaterialButton instances due to an issue with AppCompat 1.5
 * or higher.
 */
fun AppCompatButton.fixDoubleRipple() {
    AppCompatButton::class.java.getDeclaredField("mBackgroundTintHelper").apply {
        isAccessible = true
        set(this@fixDoubleRipple, null)
    }
}

/**
 * Crash-safe wrapped around [NavController.navigate] that will not crash if multiple destinations
 * are selected at once.
 *
 * @param directions The [NavDirections] to navigate with.
 */
fun NavController.navigateSafe(directions: NavDirections) =
    try {
        navigate(directions)
    } catch (e: IllegalStateException) {
        // Nothing to do.
    }

/**
 * Get the [CoordinatorLayout.Behavior] of a [View], or null if the [View] is not part of a
 * [CoordinatorLayout] or does not have a [CoordinatorLayout.Behavior].
 */
val View.coordinatorLayoutBehavior: CoordinatorLayout.Behavior<View>?
    get() = (layoutParams as? CoordinatorLayout.LayoutParams)?.behavior

/**
 * Get the "System Bar" [Insets] in this [WindowInsets] instance in a version-compatible manner This
 * can be used to prevent [View] elements from intersecting with the navigation bars.
 */
val WindowInsets.systemBarInsetsCompat: Insets
    get() =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                // API 30+, use window inset map.
                getCompatInsets(WindowInsets.Type.systemBars())
            }
            // API 21+, use window inset fields.
            else -> getSystemWindowCompatInsets()
        }

/**
 * Get the "System Gesture" [Insets] in this [WindowInsets] instance in a version-compatible manner
 * This can be used to prevent [View] elements from intersecting with the navigation bars and their
 * extended gesture hit-boxes. Note that "System Bar" insets will be used if the system does not
 * provide gesture insets.
 */
val WindowInsets.systemGestureInsetsCompat: Insets
    get() =
        // Some android versions seemingly don't provide gesture insets, setting them to zero.
        // To resolve this, we take the maximum between the system bar and system gesture
        // insets. Since system gesture insets should extend further than system bar insets,
        // this should allow this code to fall back to system bar insets easily if the system
        // does not provide system gesture insets. This does require androidx Insets to allow
        // us to use the max method on all versions however, so we will want to convert the
        // system-provided insets to such.
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                // API 30+, use window inset map.
                Insets.max(
                    getCompatInsets(WindowInsets.Type.systemGestures()),
                    getCompatInsets(WindowInsets.Type.systemBars()))
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // API 29, use window inset fields.
                Insets.max(getSystemGestureCompatInsets(), getSystemWindowCompatInsets())
            }
            // API 21+ do not support gesture insets, as they don't have gesture navigation.
            // Just use system bar insets.
            else -> getSystemWindowCompatInsets()
        }

/**
 * Returns the given [Insets] based on the to the API 30+ [WindowInsets] convention.
 *
 * @param typeMask The type of [Insets] to obtain.
 * @return Compat [Insets] corresponding to the given type.
 * @see WindowInsets.getInsets
 */
@RequiresApi(Build.VERSION_CODES.R)
private fun WindowInsets.getCompatInsets(typeMask: Int) = Insets.toCompatInsets(getInsets(typeMask))

/**
 * Returns "System Bar" [Insets] based on the API 21+ [WindowInsets] convention.
 *
 * @return Compat [Insets] consisting of the [WindowInsets] "System Bar" [Insets] field.
 * @see WindowInsets.getSystemWindowInsets
 */
@Suppress("DEPRECATION")
private fun WindowInsets.getSystemWindowCompatInsets() =
    Insets.of(
        systemWindowInsetLeft,
        systemWindowInsetTop,
        systemWindowInsetRight,
        systemWindowInsetBottom)

/**
 * Returns "System Bar" [Insets] based on the API 29 [WindowInsets] convention.
 *
 * @return Compat [Insets] consisting of the [WindowInsets] "System Gesture" [Insets] fields.
 * @see WindowInsets.getSystemGestureInsets
 */
@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.Q)
private fun WindowInsets.getSystemGestureCompatInsets() = Insets.toCompatInsets(systemGestureInsets)

/**
 * Replace the "System Bar" [Insets] in [WindowInsets] with a new set of [Insets].
 *
 * @param left The new left inset.
 * @param top The new top inset.
 * @param right The new right inset.
 * @param bottom The new bottom inset.
 * @return A new [WindowInsets] with the given "System Bar" inset values applied.
 */
fun WindowInsets.replaceSystemBarInsetsCompat(
    left: Int,
    top: Int,
    right: Int,
    bottom: Int
): WindowInsets {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            // API 30+, use a Builder to create a new instance.
            WindowInsets.Builder(this)
                .setInsets(
                    WindowInsets.Type.systemBars(),
                    Insets.of(left, top, right, bottom).toPlatformInsets())
                .build()
        }
        else -> {
            // API 21+, replace the system bar inset fields.
            @Suppress("DEPRECATION") replaceSystemWindowInsets(left, top, right, bottom)
        }
    }
}
