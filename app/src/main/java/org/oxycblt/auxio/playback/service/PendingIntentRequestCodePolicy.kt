package org.oxycblt.auxio.playback.service

import org.oxycblt.auxio.IntegerTable

/** Shared deterministic request-code policy for playback/entry intents. */
object PendingIntentRequestCodePolicy {
    fun forAction(action: String): Int = IntegerTable.REQUEST_CODE + action.hashCode()
}
