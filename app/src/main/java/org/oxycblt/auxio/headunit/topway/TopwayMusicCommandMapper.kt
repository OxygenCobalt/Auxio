package org.oxycblt.auxio.headunit.topway

enum class TopwayMappedCommand { PREV, NEXT, PLAY_PAUSE, UPDATE, UNKNOWN }

object TopwayMusicCommandMapper {
    fun map(action: String?, cmd: String?): TopwayMappedCommand =
        when (action) {
            TopwayMusicContract.ACTION_PREV -> TopwayMappedCommand.PREV
            TopwayMusicContract.ACTION_NEXT -> TopwayMappedCommand.NEXT
            TopwayMusicContract.ACTION_PLAY_PAUSE -> TopwayMappedCommand.PLAY_PAUSE
            TopwayMusicContract.ACTION_CMD ->
                when (cmd) {
                    TopwayMusicContract.CMD_PREV -> TopwayMappedCommand.PREV
                    TopwayMusicContract.CMD_NEXT -> TopwayMappedCommand.NEXT
                    TopwayMusicContract.CMD_PLAY_PAUSE -> TopwayMappedCommand.PLAY_PAUSE
                    TopwayMusicContract.CMD_UPDATE -> TopwayMappedCommand.UPDATE
                    else -> TopwayMappedCommand.UNKNOWN
                }
            else -> TopwayMappedCommand.UNKNOWN
        }
}
