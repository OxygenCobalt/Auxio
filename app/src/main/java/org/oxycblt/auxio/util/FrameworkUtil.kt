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

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Build
import android.os.TransactionTooLargeException
import android.view.View
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ShareCompat
import androidx.core.graphics.Insets
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import java.lang.IllegalArgumentException
import java.lang.reflect.Field
import org.oxycblt.auxio.R
import org.oxycblt.musikr.MusicParent
import org.oxycblt.musikr.Song
import timber.log.Timber as L

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
    minTouchTargetSize: Int,
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

/** A single scale value **assuming that the View is always center-scaled** */
var View.scale: Float
    get() = scaleX
    set(it) {
        scaleX = it
        scaleY = it
    }

/** Get a [Context] from a [ViewBinding]'s root [View]. */
val ViewBinding.context: Context
    get() = root.context

/**
 * Shortcut to easily set up a [GridLayoutManager.SpanSizeLookup].
 *
 * @param isItemFullWidth Mapping expression that returns true if the item should take up all spans
 *   or just one.
 */
fun GridLayoutManager.setFullWidthLookup(isItemFullWidth: (Int) -> Boolean) {
    spanSizeLookup =
        object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) =
                if (isItemFullWidth(position)) spanCount else 1
        }
}

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

private val VP_RECYCLER_FIELD: Field by lazyReflectedField(ViewPager2::class, "mRecyclerView")
private val RV_TOUCH_SLOP_FIELD: Field by lazyReflectedField(RecyclerView::class, "mTouchSlop")

/**
 * Dampen a [ViewPager2] so that vertical scrolls can still easily occur.
 *
 * By default, ViewPager2's sensitivity is high enough to result in vertical scroll events being
 * registered as horizontal scroll events. Reflect into the internal RecyclerView and change the
 * touch slope so that touch actions will act more as a scroll than as a swipe. Derived from:
 * https://al-e-shevelev.medium.com/how-to-reduce-scroll-sensitivity-of-viewpager2-widget-87797ad02414
 */
fun ViewPager2.dampen() {
    val recycler = recycler()
    val slop = RV_TOUCH_SLOP_FIELD.get(recycler) as Int
    RV_TOUCH_SLOP_FIELD.set(recycler, slop * 3)
}

fun ViewPager2.recycler() = (VP_RECYCLER_FIELD.get(this) as RecyclerView)

/**
 * Crash-safe wrapped around [NavController.navigate] that will not crash if multiple destinations
 * are selected at once.
 *
 * @param directions The [NavDirections] to navigate with.
 */
fun NavController.navigateSafe(directions: NavDirections) =
    try {
        navigate(directions)
    } catch (e: IllegalArgumentException) {
        // Nothing to do.
        L.e("Could not navigate from this destination.")
        L.e(e.stackTraceToString())
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
                    getCompatInsets(WindowInsets.Type.systemBars()),
                )
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
        systemWindowInsetBottom,
    )

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
    bottom: Int,
): WindowInsets {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            // API 30+, use a Builder to create a new instance.
            WindowInsets.Builder(this)
                .setInsets(
                    WindowInsets.Type.systemBars(),
                    Insets.of(left, top, right, bottom).toPlatformInsets(),
                )
                .build()
        }
        else -> {
            // API 21+, replace the system bar inset fields.
            @Suppress("DEPRECATION") replaceSystemWindowInsets(left, top, right, bottom)
        }
    }
}

/**
 * Share a single [Song].
 *
 * @param song
 */
fun Context.share(song: Song) = share(listOf(song))

/**
 * Share all songs in a [MusicParent].
 *
 * @param parent The [MusicParent] to share.
 */
fun Context.share(parent: MusicParent) = share(parent.songs)

/**
 * Share an arbitrary list of [Song]s.
 *
 * @param songs The [Song]s to share.
 */
fun Context.share(songs: Collection<Song>) {
    if (songs.isEmpty()) return
    L.d("Showing sharesheet for ${songs.size} songs")
    val builder = ShareCompat.IntentBuilder(this)
    val mimeTypes = mutableSetOf<String>()
    for (song in songs) {
        builder.addStream(song.uri)
        mimeTypes.add(song.format.mimeType)
    }

    try {
        builder.setType(mimeTypes.singleOrNull() ?: "audio/*").startChooser()
    } catch (e: TransactionTooLargeException) {
        L.e("Failed to share ${songs.size} songs: Too large")
        showToast(R.string.err_share_too_large)
    } catch (e: Exception) {
        L.e("Failed to share ${songs.size} songs: $e")
        showToast(R.string.err_share_failed)
    }
}

/**
 * Open the given URI in a web browser.
 *
 * @param uri The URL to open.
 */
fun Context.openInBrowser(uri: String) {
    L.d("Opening $uri")
    startIntent(Intent(Intent.ACTION_VIEW, uri.toUri()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

fun Context.startIntent(intent: Intent) {
    fun openAppChooser(intent: Intent) {
        L.d("Opening app chooser for ${intent.action}")
        val chooserIntent =
            Intent(Intent.ACTION_CHOOSER)
                .putExtra(Intent.EXTRA_INTENT, intent)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(chooserIntent)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // Android 11 seems to now handle the app chooser situations on its own now
        // [along with adding a new permission that breaks the old manual code], so
        // we just do a typical activity launch.
        L.d("Using API 30+ chooser")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // No app installed to open the link
            showToast(R.string.err_no_app)
        }
    } else {
        // On older versions of android, opening links from an ACTION_VIEW intent might
        // not work in all cases, especially when no default app was set. If that is the
        // case, we will try to manually handle these cases before we try to launch the
        // browser.
        L.d("Resolving browser activity for chooser")
        val pkgName =
            packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)?.run {
                activityInfo.packageName
            }

        if (pkgName != null) {
            if (pkgName == "android") {
                // No default browser [Must open app chooser, may not be supported]
                L.d("No default browser found")
                openAppChooser(intent)
            } else L.d("Opening browser intent")
            try {
                intent.setPackage(pkgName)
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Not a browser but an app chooser
                intent.setPackage(null)
                openAppChooser(intent)
            }
        } else {
            // No app installed to open the link
            showToast(R.string.err_no_app)
        }
    }
}
