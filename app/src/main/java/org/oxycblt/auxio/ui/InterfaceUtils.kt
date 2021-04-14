package org.oxycblt.auxio.ui

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Looper
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.logE
import kotlin.reflect.KClass

// --- VIEW CONFIGURATION ---

/**
 * Disable an image button.
 */
fun ImageButton.disable() {
    if (isEnabled) {
        imageTintList = R.color.inactive_color.toStateList(context)
        isEnabled = false
    }
}

/**
 * Set a [TextView] text color, without having to resolve the resource.
 */
fun TextView.setTextColorResource(@ColorRes color: Int) {
    setTextColor(color.toColor(context))
}

// --- CONVENIENCE ---

/**
 * Shortcut to get a [LayoutInflater] from a [Context]
 */
val Context.inflater: LayoutInflater get() = LayoutInflater.from(this)

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
 * Resolve a color.
 * @param context [Context] required
 * @return The resolved color, black if the resolving process failed.
 */
@ColorInt
fun Int.toColor(context: Context): Int {
    return try {
        ContextCompat.getColor(context, this)
    } catch (e: Resources.NotFoundException) {
        logE("Attempted color load failed.")

        // Default to the emergency color [Black] if the loading fails.
        ContextCompat.getColor(context, android.R.color.black)
    }
}

/**
 * Resolve a color and turn it into a [ColorStateList]
 * @param context [Context] required
 * @return The resolved color as a [ColorStateList]
 * @see toColor
 */
fun Int.toStateList(context: Context) = ColorStateList.valueOf(toColor(context))

/**
 * Resolve a drawable resource into a [Drawable]
 */
fun Int.toDrawable(context: Context) = ContextCompat.getDrawable(context, this)

/**
 * Resolve a drawable resource into an [AnimatedVectorDrawable]
 * @see toDrawable
 */
fun Int.toAnimDrawable(context: Context) = toDrawable(context) as AnimatedVectorDrawable

/**
 * Resolve this int into a color as if it was an attribute
 */
fun Int.resolveAttr(context: Context): Int {
    // Convert the attribute into its color
    val resolvedAttr = TypedValue()
    context.theme.resolveAttribute(this, resolvedAttr, true)

    // Then convert it to a proper color
    val color = if (resolvedAttr.resourceId != 0) {
        resolvedAttr.resourceId
    } else {
        resolvedAttr.data
    }

    return color.toColor(context)
}

/**
 * Create a toast using the provided string resource.
 */
fun Context.showToast(@StringRes str: Int) {
    Toast.makeText(applicationContext, getString(str), Toast.LENGTH_SHORT).show()
}

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
 * @param resources [Resources] required
 */
fun isLandscape(resources: Resources): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

/**
 * Determine if we are in tablet mode or not
 */
fun isTablet(resources: Resources): Boolean {
    val layout = resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

    return layout == Configuration.SCREENLAYOUT_SIZE_XLARGE ||
        layout == Configuration.SCREENLAYOUT_SIZE_LARGE
}

/**
 * Determine if the tablet is XLARGE, ignoring normal tablets.
 */
fun isXLTablet(resources: Resources): Boolean {
    val layout = resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

    return layout == Configuration.SCREENLAYOUT_SIZE_XLARGE
}

/**
 * Get the span count for most RecyclerViews. These probably work right on most displays. Trust me.
 */
fun RecyclerView.getSpans(): Int {
    return if (isLandscape(resources)) {
        if (isXLTablet(resources)) 3 else 2
    } else {
        if (isXLTablet(resources)) 2 else 1
    }
}

/**
 * Returns whether a recyclerview can scroll.
 */
fun RecyclerView.canScroll() = computeVerticalScrollRange() > height

/**
 * Check if we are in the "Irregular" landscape mode (e.g landscape, but nav bar is on the sides)
 * Used to disable most of edge-to-edge if that's the case, as I cant get it to work on this mode.
 * @return True if we are in the irregular landscape mode, false if not.
 */
fun Activity.isIrregularLandscape(): Boolean {
    return isLandscape(resources) && !isSystemBarOnBottom(this)
}

/**
 * Check if the system bars are on the bottom.
 * @return If the system bars are on the bottom, false if no.
 */
private fun isSystemBarOnBottom(activity: Activity): Boolean {
    val realPoint = Point()
    val metrics = DisplayMetrics()

    var width = 0
    var height = 0

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        activity.display?.let { display ->
            display.getRealSize(realPoint)

            activity.windowManager.currentWindowMetrics.bounds.also {
                width = it.width()
                height = it.height()
            }
        }
    } else {
        @Suppress("DEPRECATION")
        activity.getSystemServiceSafe(WindowManager::class).apply {
            defaultDisplay.getRealSize(realPoint)
            defaultDisplay.getMetrics(metrics)

            width = metrics.widthPixels
            height = metrics.heightPixels
        }
    }

    val config = activity.resources.configuration
    val canMove = (width != height && config.smallestScreenWidthDp < 600)

    return (!canMove || width < height)
}

// --- HACKY NIGHTMARES ---

/**
 * Use ***REFLECTION*** to fix a memory leak in the [Fragment] source code where the focused view
 * will never be cleared. I can't believe I have to do this.
 */
fun Fragment.fixAnimInfoLeak() {
    try {
        Fragment::class.java.getDeclaredMethod("setFocusedView", View::class.java).apply {
            isAccessible = true
            invoke(this@fixAnimInfoLeak, null)
        }
    } catch (e: Exception) {
        logE("mAnimationInfo leak fix failed.")
    }
}
