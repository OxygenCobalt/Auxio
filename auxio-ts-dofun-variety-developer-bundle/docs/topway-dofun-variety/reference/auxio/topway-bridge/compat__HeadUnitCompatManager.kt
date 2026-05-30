/*
 * Copyright (c) 2024 Auxio Project
 * HeadUnitCompatManager.kt is part of Auxio.
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

package org.oxycblt.auxio.headunit.compat

object HeadUnitCompatManager {
    fun currentStatus(
        compatModeEnabled: Boolean,
        widgetMetadataPublishable: Boolean,
        shortcutCompatReady: Boolean,
        sessionCompatReady: Boolean,
    ): HeadUnitCompatStatus =
        HeadUnitCompatStatus(
            compatModeEnabled = compatModeEnabled,
            androidFallbackActive =
                !compatModeEnabled ||
                    !widgetMetadataPublishable ||
                    !shortcutCompatReady ||
                    !sessionCompatReady,
            widgetMetadataPublishable = widgetMetadataPublishable,
            shortcutCompatReady = shortcutCompatReady,
            sessionCompatReady = sessionCompatReady,
            nativePrivateIntegrationStatus =
                NativePrivateIntegrationStatus.NOT_ENABLED_REQUIRES_VALIDATION,
        )

    fun onEvent(event: HeadUnitCompatEvent): HeadUnitCompatResult =
        when (event) {
            HeadUnitCompatEvent.SESSION_CHANGED,
            HeadUnitCompatEvent.WIDGET_REFRESH -> HeadUnitCompatResult.ENABLED_WITH_FALLBACK
            HeadUnitCompatEvent.AUDIO_FOCUS_CHANGED -> HeadUnitCompatResult.REQUIRES_VALIDATION
        }
}
