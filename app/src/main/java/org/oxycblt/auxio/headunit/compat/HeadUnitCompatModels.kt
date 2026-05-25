/*
 * Copyright (c) 2024 Auxio Project
 * HeadUnitCompatModels.kt is part of Auxio.
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

enum class HeadUnitCompatFeature {
    TWTHEME_WIDGET_METADATA,
    STOCK_TMUSIC_PARITY,
    MEDIA_RICH_METADATA,
    ENTRYPOINT_DEEPLINK_STABILITY,
    ZLINK_TLINK_COEXISTENCE,
    HARDWARE_MEDIA_KEYS,
}

enum class HeadUnitCompatEvent {
    SESSION_CHANGED,
    WIDGET_REFRESH,
    AUDIO_FOCUS_CHANGED,
}

enum class HeadUnitCompatResult {
    ENABLED_WITH_FALLBACK,
    FALLBACK_ONLY,
    REQUIRES_VALIDATION,
}

data class HeadUnitCompatStatus(
    val compatModeEnabled: Boolean,
    val androidFallbackActive: Boolean,
    val widgetMetadataPublishable: Boolean,
    val shortcutCompatReady: Boolean,
    val sessionCompatReady: Boolean,
    val nativePrivateIntegrationStatus: NativePrivateIntegrationStatus,
)

enum class NativePrivateIntegrationStatus {
    NOT_ENABLED_REQUIRES_VALIDATION,
}
