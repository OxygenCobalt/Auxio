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
                !compatModeEnabled || !widgetMetadataPublishable || !shortcutCompatReady || !sessionCompatReady,
            widgetMetadataPublishable = widgetMetadataPublishable,
            shortcutCompatReady = shortcutCompatReady,
            sessionCompatReady = sessionCompatReady,
            nativePrivateIntegrationStatus = NativePrivateIntegrationStatus.NOT_ENABLED_REQUIRES_VALIDATION,
        )

    fun onEvent(event: HeadUnitCompatEvent): HeadUnitCompatResult =
        when (event) {
            HeadUnitCompatEvent.SESSION_CHANGED,
            HeadUnitCompatEvent.WIDGET_REFRESH -> HeadUnitCompatResult.ENABLED_WITH_FALLBACK
            HeadUnitCompatEvent.AUDIO_FOCUS_CHANGED -> HeadUnitCompatResult.REQUIRES_VALIDATION
        }
}
