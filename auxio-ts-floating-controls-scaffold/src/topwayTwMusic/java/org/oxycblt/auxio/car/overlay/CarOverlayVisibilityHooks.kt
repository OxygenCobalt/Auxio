package org.oxycblt.auxio.car.overlay

import android.app.Activity

/**
 * Optional helper for wiring Auxio activity foreground state.
 *
 * Codex should replace this with the repo's actual base-activity/application lifecycle pattern.
 */
object CarOverlayVisibilityHooks {
    fun onAuxioActivityResumed(activity: Activity) {
        CarFloatingControlsService.setAuxioForeground(activity, true)
    }

    fun onAuxioActivityPaused(activity: Activity) {
        CarFloatingControlsService.setAuxioForeground(activity, false)
    }
}
