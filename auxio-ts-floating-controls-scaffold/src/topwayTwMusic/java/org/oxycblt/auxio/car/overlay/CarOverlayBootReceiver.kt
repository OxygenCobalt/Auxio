package org.oxycblt.auxio.car.overlay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CarOverlayBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val prefs = CarOverlayPrefs(context)
        if (prefs.enabled && prefs.alwaysShow) {
            CarFloatingControlsService.start(context)
        }
    }
}
