package org.oxycblt.auxio.playback.service

import org.oxycblt.auxio.widgets.WidgetProvider

/** Pure policy for actions handled by [SystemPlaybackReceiver]. */
object PlaybackActionPolicy {
    val supportedActions: Set<String> =
        setOf(
            PlaybackActions.ACTION_PLAY_PAUSE,
            PlaybackActions.ACTION_INC_REPEAT_MODE,
            PlaybackActions.ACTION_INVERT_SHUFFLE,
            PlaybackActions.ACTION_SKIP_PREV,
            PlaybackActions.ACTION_SKIP_NEXT,
            PlaybackActions.ACTION_EXIT,
            WidgetProvider.ACTION_WIDGET_UPDATE,
        )

    fun isSupportedAction(action: String?): Boolean = action != null && action in supportedActions
}
