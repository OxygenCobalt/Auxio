package org.oxycblt.auxio.car.overlay

import android.content.Context
import android.provider.Settings

/**
 * Thin facade intended to be called from Auxio's existing settings UI.
 *
 * Replace this with real settings-state integration once Codex maps Auxio's settings architecture.
 */
object CarOverlaySettingsFacade {
    fun isEnabled(context: Context): Boolean = CarOverlayPrefs(context).enabled

    fun setEnabled(context: Context, enabled: Boolean) {
        val prefs = CarOverlayPrefs(context)
        prefs.enabled = enabled
        if (enabled) {
            if (Settings.canDrawOverlays(context)) {
                CarFloatingControlsService.start(context)
            } else {
                context.startActivity(
                    CarFloatingControlsService.permissionIntent(context)
                        .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK),
                )
            }
        } else {
            CarFloatingControlsService.stop(context)
        }
    }

    fun resetPosition(context: Context) {
        CarOverlayPrefs(context).resetPosition()
        context.startService(android.content.Intent(context, CarFloatingControlsService::class.java).apply {
            action = CarOverlayActions.ACTION_RESET_POSITION
        })
    }
}
