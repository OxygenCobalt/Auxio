package org.oxycblt.auxio.headunit.topway

enum class TopwayServiceAction { PREVIOUS, NEXT, PLAY_PAUSE, WIDGET_UPDATE, SEEK, IGNORE }

data class TopwayServiceDecision(
    val action: TopwayServiceAction,
    val seekTargetMs: Long? = null,
)

/** Pure routing policy used by both the cold manifest bridge and active playback receiver. */
object TopwayStartRoutingPolicy {
    fun decide(
        action: String?,
        cmd: String?,
        rawSeek: Any?,
        durationMs: Long?,
        hasCurrentSong: Boolean,
    ): TopwayServiceDecision {
        if (action == TopwayMusicContract.ACTION_LAUNCHER_WIDGET_SEEK) {
            val target = TopwayMusicSeekMapper.mapSeekTargetMs(rawSeek, durationMs)
            return if (hasCurrentSong && target != null) {
                TopwayServiceDecision(TopwayServiceAction.SEEK, target)
            } else {
                TopwayServiceDecision(TopwayServiceAction.IGNORE)
            }
        }

        return when (TopwayMusicCommandMapper.map(action, cmd)) {
            TopwayMappedCommand.PREV ->
                if (hasCurrentSong) TopwayServiceDecision(TopwayServiceAction.PREVIOUS)
                else TopwayServiceDecision(TopwayServiceAction.IGNORE)
            TopwayMappedCommand.NEXT ->
                if (hasCurrentSong) TopwayServiceDecision(TopwayServiceAction.NEXT)
                else TopwayServiceDecision(TopwayServiceAction.IGNORE)
            TopwayMappedCommand.PLAY_PAUSE ->
                if (hasCurrentSong) TopwayServiceDecision(TopwayServiceAction.PLAY_PAUSE)
                else TopwayServiceDecision(TopwayServiceAction.IGNORE)
            TopwayMappedCommand.UPDATE -> TopwayServiceDecision(TopwayServiceAction.WIDGET_UPDATE)
            TopwayMappedCommand.UNKNOWN -> TopwayServiceDecision(TopwayServiceAction.IGNORE)
        }
    }
}
