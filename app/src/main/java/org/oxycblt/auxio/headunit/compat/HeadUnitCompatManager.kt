package org.oxycblt.auxio.headunit.compat

object HeadUnitCompatManager {
    fun currentStatus(widgetMetadataPublishable: Boolean, sessionCompatReady: Boolean): HeadUnitCompatStatus =
        HeadUnitCompatStatus(
            compatModeEnabled = true,
            androidFallbackActive = true,
            widgetMetadataPublishable = widgetMetadataPublishable,
            shortcutCompatReady = true,
            sessionCompatReady = sessionCompatReady,
            nativePrivateIntegrationStatus = "not enabled / requires validation",
        )

    fun onEvent(event: HeadUnitCompatEvent): HeadUnitCompatResult =
        when (event) {
            HeadUnitCompatEvent.SESSION_CHANGED,
            HeadUnitCompatEvent.WIDGET_REFRESH -> HeadUnitCompatResult.ENABLED_WITH_FALLBACK
            HeadUnitCompatEvent.AUDIO_FOCUS_CHANGED -> HeadUnitCompatResult.REQUIRES_VALIDATION
        }
}
