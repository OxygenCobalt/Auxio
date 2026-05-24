package org.oxycblt.auxio.headunit.topway

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import org.oxycblt.auxio.AuxioService
import org.oxycblt.auxio.IntegerTable
import timber.log.Timber as L

/**
 * Narrow exported bridge receiver for Topway-compatible actions when Auxio is cold.
 *
 * It is exported so TS18/iLauncher can deliver the source-backed Topway actions, but it accepts
 * only the allowlisted actions from [TopwayMusicContract] and immediately delegates to Auxio's
 * service-specific Topway path. It must never route through generic media-button restore logic.
 */
class TopwayMusicBridgeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (!TopwayMusicContract.isIncomingAction(action)) {
            L.w("Ignoring unsupported Topway bridge action: $action")
            return
        }
        val serviceIntent =
            Intent(context, AuxioService::class.java)
                .setAction(action)
        val extras =
            TopwayBridgeExtrasPolicy.sanitizeIncomingExtras(
                intent.extras
                    ?.keySet()
                    ?.associateWith { key -> intent.extras?.get(key) }
                    ?: emptyMap(),
            )
        extras.cmd?.let { serviceIntent.putExtra(TopwayMusicContract.EXTRA_CMD, it) }
        extras.widgetProgress?.let {
            serviceIntent.putExtra(TopwayMusicContract.EXTRA_WIDGET_PROGRESS, it)
        }
        serviceIntent.putExtra(AuxioService.INTENT_KEY_START_ID, IntegerTable.START_ID_TOPWAY)
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
