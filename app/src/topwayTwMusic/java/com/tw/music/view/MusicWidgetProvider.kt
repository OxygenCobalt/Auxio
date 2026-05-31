/*
 * Topway/DoFun stock-component compatibility shim.
 *
 * Stock twmusic exposes com.tw.music.view.MusicWidgetProvider. Some Topway/DoFun builds may
 * address that component explicitly. This wrapper does not implement private vendor APIs; it
 * forwards safe observed Topway widget/control broadcasts into Auxio's existing Topway bridge.
 */

package com.tw.music.view

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.tw.music.MusicService
import org.oxycblt.auxio.AuxioService
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.headunit.topway.TopwayBridgeExtrasPolicy
import org.oxycblt.auxio.headunit.topway.TopwayMusicContract
import timber.log.Timber as L

class MusicWidgetProvider : AppWidgetProvider() {
    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        forwardTopwayIntent(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        forwardTopwayIntent(context, null)
    }

    private fun forwardTopwayIntent(context: Context, intent: Intent?) {
        val incomingAction = intent?.action
        val action =
            incomingAction?.takeIf { TopwayMusicContract.isIncomingAction(it) }
                ?: TopwayMusicContract.ACTION_CMD

        val incoming =
            intent?.extras?.keySet()?.associateWith { key -> intent.extras?.get(key) } ?: emptyMap()
        val extras = TopwayBridgeExtrasPolicy.sanitizeIncomingExtras(incoming)

        val serviceIntent =
            Intent(context, MusicService::class.java)
                .setAction(action)
                .putExtra(AuxioService.INTENT_KEY_START_ID, IntegerTable.START_ID_TOPWAY)

        when {
            extras.cmd != null -> serviceIntent.putExtra(TopwayMusicContract.EXTRA_CMD, extras.cmd)
            action == TopwayMusicContract.ACTION_CMD ->
                serviceIntent.putExtra(
                    TopwayMusicContract.EXTRA_CMD,
                    TopwayMusicContract.CMD_UPDATE,
                )
        }

        extras.widgetProgress?.let {
            serviceIntent.putExtra(TopwayMusicContract.EXTRA_WIDGET_PROGRESS, it)
        }

        try {
            ContextCompat.startForegroundService(context, serviceIntent)
        } catch (e: Exception) {
            L.w("Unable to forward Topway widget/provider intent: $e")
        }
    }
}
