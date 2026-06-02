/*
 * Copyright (c) 2026 Auxio Project
 * MusicWidgetProvider.kt is part of Auxio.
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

package com.tw.music.view

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.BadParcelableException
import androidx.core.content.ContextCompat
import com.tw.music.MusicService
import org.oxycblt.auxio.AuxioService
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.headunit.topway.TopwayBridgeExtrasPolicy
import org.oxycblt.auxio.headunit.topway.TopwayMusicContract
import timber.log.Timber as L

class MusicWidgetProvider : AppWidgetProvider() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) {
            L.d("Ignoring null Topway widget/provider intent")
            return
        }

        val action = intent.action
        if (action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            // AppWidgetProvider.super.onReceive() dispatches this to onUpdate(). Keep forwarding in
            // exactly one place to avoid duplicate foreground-service starts for the same update.
            super.onReceive(context, intent)
            return
        }

        super.onReceive(context, intent)

        if (TopwayMusicContract.isIncomingAction(action)) {
            forwardTopwayIntent(context, intent)
        } else {
            L.d("Ignoring unsupported Topway widget/provider action: $action")
        }
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

        val extras =
            TopwayBridgeExtrasPolicy.sanitizeIncomingExtras(safelyExtractIncomingExtras(intent))

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
        } catch (e: IllegalStateException) {
            L.w(e, "Unable to forward Topway widget/provider intent due to service state")
        } catch (e: SecurityException) {
            L.w(e, "Unable to forward Topway widget/provider intent due to security policy")
        }
    }

    private fun safelyExtractIncomingExtras(intent: Intent?): Map<String, Any?> {
        val extras = intent?.extras ?: return emptyMap()
        return try {
            extras.classLoader = javaClass.classLoader
            extras.keySet().associateWith { key -> extras.get(key) }
        } catch (e: BadParcelableException) {
            L.w(e, "Ignoring malformed extras from untrusted Topway widget/provider intent")
            emptyMap()
        } catch (e: RuntimeException) {
            L.w(e, "Ignoring unreadable extras from untrusted Topway widget/provider intent")
            emptyMap()
        }
    }
}
