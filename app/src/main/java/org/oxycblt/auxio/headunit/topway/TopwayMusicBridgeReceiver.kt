package org.oxycblt.auxio.headunit.topway

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import org.oxycblt.auxio.AuxioService
import org.oxycblt.auxio.IntegerTable

/**
 * Narrow exported bridge receiver for Topway-compatible actions when Auxio is cold.
 */
class TopwayMusicBridgeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val serviceIntent =
            Intent(context, AuxioService::class.java)
                .setAction(action)
                .putExtra(AuxioService.INTENT_KEY_START_ID, IntegerTable.START_ID_TOPWAY)
        intent.extras?.let { serviceIntent.putExtras(it) }
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
