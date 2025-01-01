package org.oxycblt.musikr.track

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import org.oxycblt.musikr.fs.MusicLocation

internal class LocationObserver(
    private val context: Context,
    private val location: MusicLocation,
    private val listener: UpdateTracker.Callback
) : ContentObserver(Handler(Looper.getMainLooper())), Runnable {
    private val handler = Handler(Looper.getMainLooper())

    init {
        context.applicationContext.contentResolver.registerContentObserver(
            location.uri,
            true,
            this
        )
    }

    fun release() {
        handler.removeCallbacks(this)
        context.applicationContext.contentResolver.unregisterContentObserver(this)
    }

    override fun onChange(selfChange: Boolean) {
        // Batch rapid-fire updates into a single callback after delay
        handler.removeCallbacks(this)
        handler.postDelayed(this, REINDEX_DELAY_MS)
    }

    override fun run() {
        listener.onUpdate(location)
    }

    private companion object {
        const val REINDEX_DELAY_MS = 500L
    }
}