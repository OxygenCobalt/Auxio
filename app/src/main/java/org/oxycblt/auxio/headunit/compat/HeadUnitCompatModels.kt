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
    val nativePrivateIntegrationStatus: String,
)
