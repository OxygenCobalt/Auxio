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

import android.app.Application
import android.content.Context
import android.content.res.ColorStateList
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Insets
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.oxycblt.auxio.R

/**
 * Disables drop shadows on a view programmatically in a version-compatible manner. This only works
 * on Android 9 and above. Below that version, shadows will remain visible.
 */
fun View.disableDropShadowCompat() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val transparent = context.getColorSafe(android.R.color.transparent)
        outlineAmbientShadowColor = transparent
        outlineSpotShadowColor = transparent
    }
}

/**
 * Determines if the point given by [x] and [y] falls within this view.
 * @param minTouchTargetSize The minimum touch size, independent of the view's size (Optional)
 */
fun View.isUnder(x: Float, y: Float, minTouchTargetSize: Int = 0) =
    isUnderImpl(x, left, right, (parent as View).width, minTouchTargetSize) &&
        isUnderImpl(y, top, bottom, (parent as View).height, minTouchTargetSize)

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

/** Returns if this view is RTL in a compatible manner. */
val View.isRtl: Boolean
    get() = layoutDirection == View.LAYOUT_DIRECTION_RTL

/** Returns if this drawable is RTL in a compatible manner.] */
val Drawable.isRtl: Boolean
    get() = DrawableCompat.getLayoutDirection(this) == View.LAYOUT_DIRECTION_RTL

/** Shortcut to get a context from a ViewBinding */
val ViewBinding.context: Context
    get() = root.context

/**
 * A variation of [TextView.setText] that automatically relayouts the view when updated. Helps with
 * getting ellipsize functionality to work.
 */
var TextView.textSafe: CharSequence
    get() = text
    set(value) {
        text = value
        requestLayout()
    }

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
val RecyclerView.canScroll: Boolean
    get() = computeVerticalScrollRange() > height

val View.coordinatorLayoutBehavior: CoordinatorLayout.Behavior<*>?
    get() = (layoutParams as? CoordinatorLayout.LayoutParams)?.behavior

/** Converts this color to a single-color [ColorStateList]. */
val @receiver:ColorRes Int.stateList
    get() = ColorStateList.valueOf(this)

/**
 * Collect a [stateFlow] into [block] eventually.
 *
 * This does have an initializing call, but it usually occurs ~100ms into draw-time, which might not
 * be ideal for some views. This should be used in cases where the state only needs to be updated
 * during runtime.
 */
fun <T> Fragment.collect(stateFlow: StateFlow<T>, block: (T) -> Unit) {
    launch { stateFlow.collect(block) }
}

/**
 * Collect a [stateFlow] into [block] immediately.
 *
 * This method automatically calls [block] when initially starting to ensure UI state consistency at
 * soon as the view is visible. This does nominally mean that there are two initializing
 * collections, but this is considered okay. [block] should be a function pointer in order to ensure
 * lifecycle consistency.
 *
 * This should be used if the state absolutely needs to be shown at draw-time.
 */
fun <T> Fragment.collectImmediately(stateFlow: StateFlow<T>, block: (T) -> Unit) {
    block(stateFlow.value)
    launch { stateFlow.collect(block) }
}

/** Like [collectImmediately], but with two [StateFlow] values. */
fun <T1, T2> Fragment.collectImmediately(
    a: StateFlow<T1>,
    b: StateFlow<T2>,
    block: (T1, T2) -> Unit
) {
    block(a.value, b.value)
    val combine = a.combine(b) { first, second -> Pair(first, second) }
    launch { combine.collect { block(it.first, it.second) } }
}

/**
 * Launches [block] in a lifecycle-aware coroutine once [state] is reached. This is primarily a
 * shortcut intended to correctly launch a co-routine on a fragment in a way that won't cause
 * miscellaneous coroutine insanity.
 */
private fun Fragment.launch(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch { viewLifecycleOwner.repeatOnLifecycle(state, block) }
}

/**
 * Shortcut to generate a AndroidViewModel [T] without having to specify the bloated factory syntax.
 */
inline fun <reified T : AndroidViewModel> Fragment.androidViewModels() =
    viewModels<T> { ViewModelProvider.AndroidViewModelFactory(requireActivity().application) }

/**
 * Shortcut to generate a AndroidViewModel [T] without having to specify the bloated factory syntax.
 */
inline fun <reified T : AndroidViewModel> AppCompatActivity.androidViewModels() =
    viewModels<T> { ViewModelProvider.AndroidViewModelFactory(application) }

/**
 * Shortcut to generate a AndroidViewModel [T] without having to specify the bloated factory syntax.
 */
inline fun <reified T : AndroidViewModel> Fragment.androidActivityViewModels() =
    activityViewModels<T> {
        ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
    }

/** Shortcut to get the [Application] from an [AndroidViewModel] */
val AndroidViewModel.application: Application
    get() = getApplication()

/**
 * Shortcut for querying all items in a database and running [block] with the cursor returned. Will
 * not run if the cursor is null.
 */
fun <R> SQLiteDatabase.queryAll(tableName: String, block: (Cursor) -> R) =
    query(tableName, null, null, null, null, null, null)?.use(block)

// Note: WindowInsetsCompat and it's related methods are an over-engineered mess that does not
// work for Auxio's use-case. Use our own compat methods instead.

/**
 * Resolve system bar insets in a version-aware manner. This can be used to apply padding to a view
 * that properly follows all the frustrating changes that were made between Android 8-11.
 */
val WindowInsets.systemBarInsetsCompat: Insets
    get() =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                getInsets(WindowInsets.Type.systemBars())
            }
            else -> systemWindowInsetsCompat
        }

/**
 * Resolve gesture insets in a version-aware manner. This can be used to apply padding to a view
 * that properly follows all the frustrating changes that were made between Android 8-11. Note that
 * if the gesture insets are not present (i.e zeroed), the bar insets will be used instead.
 */
val WindowInsets.systemGestureInsetsCompat: Insets
    get() =
        // The reasoning for why we take a larger inset is because gesture insets are seemingly
        // not present on some android versions. To prevent the app from appearing below the
        // system bars, we fall back to the bar insets. This is guaranteed not to fire in any
        // context but the gesture insets being invalid, as gesture insets are intended to
        // be larger than bar insets.
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                Insets.max(
                    getInsets(WindowInsets.Type.systemGestures()),
                    getInsets(WindowInsets.Type.systemBars()))
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                @Suppress("DEPRECATION") Insets.max(systemGestureInsets, systemBarInsetsCompat)
            }
            else -> systemWindowInsetsCompat
        }

@Suppress("DEPRECATION")
val WindowInsets.systemWindowInsetsCompat: Insets
    get() =
        Insets.of(
            systemWindowInsetLeft,
            systemWindowInsetTop,
            systemWindowInsetRight,
            systemWindowInsetBottom)

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
