/*
 * Reference-only contract skeleton extracted from stock Topway twmusic strings.
 * Do not compile this file directly. Keep production code under the isolated
 * Auxio-TS Topway bridge package and adapt to the app architecture.
 */

object TopwayMusicContractReference {
    const val ACTION_MUSIC_INFO = "com.tw.music.info"
    const val EXTRA_MUSIC_TITLE = "musicTitle"
    const val EXTRA_MUSIC_ARTIST = "musicaArtist"
    const val EXTRA_MUSIC_ALBUM = "musicAlbum"
    const val EXTRA_MUSIC_PATH = "musicPath"

    const val ACTION_PROGRESS_DURATION = "com.tw.launcher.music_progress_duration"
    const val EXTRA_PROGRESS = "msg_music_progress"
    const val EXTRA_DURATION = "msg_music_duration"

    const val ACTION_CMD = "com.tw.music.action.cmd"
    const val ACTION_PREV = "com.tw.music.action.prev"
    const val ACTION_NEXT = "com.tw.music.action.next"
    const val ACTION_PLAY_PAUSE = "com.tw.music.action.pp"
    const val ACTION_LAUNCHER_WIDGET_SEEK = "com.android.launcher.widget_music_progress"

    const val EXTRA_CMD = "cmd"
    const val EXTRA_APP_WIDGET_IDS = "appWidgetIds"
    const val EXTRA_WIDGET_PROGRESS = "music_progress"

    const val CMD_PREV = "prev"
    const val CMD_NEXT = "next"
    const val CMD_PLAY_PAUSE = "pp"
    const val CMD_UPDATE = "update"
}
