package org.oxycblt.auxio.playback

import android.content.Intent
import androidx.fragment.app.Fragment

object PlaybackUtils {
    const val KEY_INTENT_FIRED = "KEY_FILE_INTENT_FIRED"
}

/**
 * Check if the current activity intent is a file intent and needs to be dealt with.
 */
fun Fragment.shouldHandleFileIntent(): Boolean {
    val intent = requireActivity().intent

    return intent != null &&
        intent.action == Intent.ACTION_VIEW &&
        !intent.getBooleanExtra(PlaybackUtils.KEY_INTENT_FIRED, false)
}
