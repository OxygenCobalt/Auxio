@file:Suppress("DEPRECATION")
@file:TargetApi(Build.VERSION_CODES.O_MR1)

package org.oxycblt.auxio.ui

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import org.oxycblt.auxio.settings.SettingsManager

/**
 * Check if we are in the "Irregular" landscape mode [e.g landscape, but nav bar is on the sides]
 * Used to disable most of edge-to-edge if that's the case, as I cant get it to work on this mode yet.
 * TODO: Make edge-to-edge work in irregular mode
 * @return True if we are in the irregular landscape mode, false if not.
 */
fun Activity.isInIrregularLandscapeMode(): Boolean {
    return SettingsManager.getInstance().edgeEnabled &&
        isLandscape(resources) &&
        !isSystemBarOnBottom(this)
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
        (activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager).apply {
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

/**
 * Handle transparent system bars. Adapted from Music Player GO
 * (https://github.com/enricocid/Music-Player-GO)
 */
fun Window.handleTransparentSystemBars(config: Configuration) {
    fun isNight() = config.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.let { controller ->
            val appearance = WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS or
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS

            val mask = if (isNight()) 0 else appearance

            controller.setSystemBarsAppearance(appearance, mask)
        }
    } else {
        val flags = decorView.systemUiVisibility

        decorView.systemUiVisibility =
            if (isNight()) {
                flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() and
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            } else {
                flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
    }
}
