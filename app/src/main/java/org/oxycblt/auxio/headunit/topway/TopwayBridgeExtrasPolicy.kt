package org.oxycblt.auxio.headunit.topway

data class TopwayBridgeExtras(
    val cmd: String?,
    val widgetProgress: Int?,
)

/** Allowlist-only bridge payload sanitizer for exported Topway receiver intents. */
object TopwayBridgeExtrasPolicy {
    private val ALLOWED_COMMANDS: Set<String> =
        setOf(
            TopwayMusicContract.CMD_PREV,
            TopwayMusicContract.CMD_NEXT,
            TopwayMusicContract.CMD_PLAY_PAUSE,
            TopwayMusicContract.CMD_UPDATE,
        )

    fun sanitizeIncomingExtras(incoming: Map<String, Any?>): TopwayBridgeExtras {
        val cmd =
            (incoming[TopwayMusicContract.EXTRA_CMD] as? String)
                ?.takeIf { it.length <= 16 && it in ALLOWED_COMMANDS }
        val widgetProgress =
            parseWidgetProgress(incoming[TopwayMusicContract.EXTRA_WIDGET_PROGRESS])
        return TopwayBridgeExtras(cmd = cmd, widgetProgress = widgetProgress)
    }

    private fun parseWidgetProgress(raw: Any?): Int? =
        when (raw) {
            is Int -> raw
            is Long ->
                raw.takeIf { it in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong() }?.toInt()
            is String -> raw.takeIf { it.length <= 10 }?.toIntOrNull()
            else -> null
        }
}
