/*
 * Copyright (c) 2026 Auxio Project
 * TopwayWidgetProviderPolicy.kt is part of Auxio.
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

package org.oxycblt.auxio.headunit.topway

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.widgets.WidgetProvider

/**
 * Chooses widget-provider components for Topway/DoFun update gating.
 *
 * The standard Auxio widget provider is still valid in every variant. Topway-compatible variants
 * also expose the stock-name wrapper provider so fixed TS18/DoFun widget requests do not get
 * dropped merely because the wrapper component, rather than Auxio's normal component, owns the
 * launcher widget instance.
 */
object TopwayWidgetProviderPolicy {
    private const val STOCK_WIDGET_PROVIDER_CLASS = "com.tw.music.view.MusicWidgetProvider"

    fun shouldHandleTopwayUpdate(context: Context): Boolean {
        val topwayCompatFlavor = BuildConfig.TOPWAY_COMPAT_FLAVOR
        // DoFun/Topway launchers are not guaranteed to be normal Android AppWidget hosts. In the
        // dedicated compatibility APKs, serve explicit `cmd=update` requests even when
        // AppWidgetManager cannot report a widget instance for either provider component.
        return shouldHandleTopwayUpdate(
            topwayCompatFlavor = topwayCompatFlavor,
            hasAnyWidgetInstances = hasAnyWidgetInstances(context),
        )
    }

    fun hasAnyWidgetInstances(context: Context): Boolean {
        val awm = AppWidgetManager.getInstance(context)
        return providerComponents(context).any { component ->
            awm.getAppWidgetIds(component).isNotEmpty()
        }
    }

    fun providerComponents(context: Context): List<ComponentName> =
        providerClassNames(BuildConfig.TOPWAY_COMPAT_FLAVOR).map { className ->
            ComponentName(context.packageName, className)
        }

    internal fun shouldHandleTopwayUpdate(
        topwayCompatFlavor: Boolean,
        hasAnyWidgetInstances: Boolean,
    ): Boolean = topwayCompatFlavor || hasAnyWidgetInstances

    internal fun providerClassNames(topwayCompatFlavor: Boolean): List<String> =
        buildList {
            add(WidgetProvider::class.java.name)
            if (topwayCompatFlavor) {
                add(STOCK_WIDGET_PROVIDER_CLASS)
            }
        }.distinct()
}
