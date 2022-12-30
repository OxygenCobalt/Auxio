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

import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.WindowInsets
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.Insets
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Get if this [View] contains the given [PointF], with optional leeway.
 * @param x The x value of the point to check.
 * @param y The y value of the point to check.
 * @param minTouchTargetSize A minimum size to use when checking the value. This can be used to
 * extend the range where a point is considered "contained" by the [View] beyond it's actual size.
 * @return true if the [PointF] is contained by the view, false otherwise. Adapted from
 * AndroidFastScroll: https://github.com/zhanghai/AndroidFastScroll
 */
fun View.isUnder(x: Float, y: Float, minTouchTargetSize: Int = 0) =
    isUnderImpl(x, left, right, (parent as View).width, minTouchTargetSize) &&
        isUnderImpl(y, top, bottom, (parent as View).height, minTouchTargetSize)

/**
 * Internal implementation of [isUnder].
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
 * Get the [CoordinatorLayout.Behavior] of a [View], or null if the [View] is not part of a
 * [CoordinatorLayout] or does not have a [CoordinatorLayout.Behavior].
 */
val View.coordinatorLayoutBehavior: CoordinatorLayout.Behavior<View>?
    get() = (layoutParams as? CoordinatorLayout.LayoutParams)?.behavior

/**
 * Collect a [StateFlow] into [block] in a lifecycle-aware manner *eventually.* Due to co-routine
 * launching, the initializing call will occur ~100ms after draw time. If this is not desirable, use
 * [collectImmediately].
 * @param stateFlow The [StateFlow] to collect.
 * @param block The code to run when the [StateFlow] updates.
 */
fun <T> Fragment.collect(stateFlow: StateFlow<T>, block: (T) -> Unit) {
    launch { stateFlow.collect(block) }
}

/**
 * Collect a [StateFlow] into a [block] in a lifecycle-aware manner *immediately.* This will
 * immediately run an initializing call to ensure the UI is set up before draw-time. Note that this
 * will result in two initializing calls.
 * @param stateFlow The [StateFlow] to collect.
 * @param block The code to run when the [StateFlow] updates.
 */
fun <T> Fragment.collectImmediately(stateFlow: StateFlow<T>, block: (T) -> Unit) {
    block(stateFlow.value)
    launch { stateFlow.collect(block) }
}

/**
 * Like [collectImmediately], but with two [StateFlow] instances that are collected with the same
 * block.
 * @param a The first [StateFlow] to collect.
 * @param b The second [StateFlow] to collect.
 * @param block The code to run when either [StateFlow] updates.
 */
fun <T1, T2> Fragment.collectImmediately(
    a: StateFlow<T1>,
    b: StateFlow<T2>,
    block: (T1, T2) -> Unit
) {
    block(a.value, b.value)
    // We can combine flows, but only if we transform them into one flow output.
    // Thus, we have to first combine the two flow values into a Pair, and then
    // decompose it when we collect the values.
    val combine = a.combine(b) { first, second -> Pair(first, second) }
    launch { combine.collect { block(it.first, it.second) } }
}

/**
 * Like [collectImmediately], but with three [StateFlow] instances that are collected with the same
 * block.
 * @param a The first [StateFlow] to collect.
 * @param b The second [StateFlow] to collect.
 * @param c The third [StateFlow] to collect.
 * @param block The code to run when any of the [StateFlow]s update.
 */
fun <T1, T2, T3> Fragment.collectImmediately(
    a: StateFlow<T1>,
    b: StateFlow<T2>,
    c: StateFlow<T3>,
    block: (T1, T2, T3) -> Unit
) {
    block(a.value, b.value, c.value)
    val combine = combine(a, b, c) { a1, b2, c3 -> Triple(a1, b2, c3) }
    launch { combine.collect { block(it.first, it.second, it.third) } }
}

/**
 * Launch a [Fragment] co-routine whenever the [Lifecycle] hits the given [Lifecycle.State]. This
 * should always been used when launching [Fragment] co-routines was it will not result in
 * unexpected behavior.
 * @param state The [Lifecycle.State] to launch the co-routine in.
 * @param block The block to run in the co-routine.
 * @see repeatOnLifecycle
 */
private fun Fragment.launch(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch { viewLifecycleOwner.repeatOnLifecycle(state, block) }
}

/**
 * An extension to [viewModels] that automatically provides an
 * [ViewModelProvider.AndroidViewModelFactory]. Use whenever an [AndroidViewModel] is used.
 */
inline fun <reified T : AndroidViewModel> Fragment.androidViewModels() =
    viewModels<T> { ViewModelProvider.AndroidViewModelFactory(requireActivity().application) }

/**
 * An extension to [viewModels] that automatically provides an
 * [ViewModelProvider.AndroidViewModelFactory]. Use whenever an [AndroidViewModel] is used. Note
 * that this implementation is for an [AppCompatActivity], and thus makes this functionally
 * equivalent in scope to [androidActivityViewModels].
 */
inline fun <reified T : AndroidViewModel> AppCompatActivity.androidViewModels() =
    viewModels<T> { ViewModelProvider.AndroidViewModelFactory(application) }

/**
 * An extension to [activityViewModels] that automatically provides an
 * [ViewModelProvider.AndroidViewModelFactory]. Use whenever an [AndroidViewModel] is used.
 */
inline fun <reified T : AndroidViewModel> Fragment.androidActivityViewModels() =
    activityViewModels<T> {
        ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
    }

/** The [Context] provided to an [AndroidViewModel]. */
inline val AndroidViewModel.context: Context
    get() = getApplication()

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
        // system-provided insets to such..
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
 * @param typeMask The type of [Insets] to obtain.
 * @return Compat [Insets] corresponding to the given type.
 * @see WindowInsets.getInsets
 */
@RequiresApi(Build.VERSION_CODES.R)
private fun WindowInsets.getCompatInsets(typeMask: Int) = Insets.toCompatInsets(getInsets(typeMask))

/**
 * Returns "System Bar" [Insets] based on the API 21+ [WindowInsets] convention.
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
 * @return Compat [Insets] consisting of the [WindowInsets] "System Gesture" [Insets] fields.
 * @see WindowInsets.getSystemGestureInsets
 */
@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.Q)
private fun WindowInsets.getSystemGestureCompatInsets() = Insets.toCompatInsets(systemGestureInsets)

/**
 * Replace the "System Bar" [Insets] in [WindowInsets] with a new set of [Insets].
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
