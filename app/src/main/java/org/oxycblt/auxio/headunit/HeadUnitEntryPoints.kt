package org.oxycblt.auxio.headunit

import org.oxycblt.auxio.BuildConfig

object HeadUnitEntryPoints {
    const val ACTION_SHUFFLE_ALL = BuildConfig.APPLICATION_ID + ".action.SHUFFLE_ALL"
    const val ACTION_OPEN_NOW_PLAYING = BuildConfig.APPLICATION_ID + ".action.OPEN_NOW_PLAYING"
    const val ACTION_OPEN_QUEUE = BuildConfig.APPLICATION_ID + ".action.OPEN_QUEUE"

    enum class EntryDestination {
        NOW_PLAYING,
        QUEUE,
    }

    fun destinationForAction(action: String?): EntryDestination? =
        when (action) {
            ACTION_OPEN_NOW_PLAYING -> EntryDestination.NOW_PLAYING
            ACTION_OPEN_QUEUE -> EntryDestination.QUEUE
            else -> null
        }
}
