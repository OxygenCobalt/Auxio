package org.oxycblt.musikr.track

import android.content.Context
import org.oxycblt.musikr.fs.MusicLocation

interface UpdateTracker {
    fun track(locations: List<MusicLocation>)

    fun release()

    interface Callback {
        fun onUpdate(location: MusicLocation)
    }

    companion object {
        fun from(context: Context, callback: Callback): UpdateTracker = UpdateTrackerImpl(context, callback)
    }
}

private class UpdateTrackerImpl(
    private val context: Context,
    private val callback: UpdateTracker.Callback
) : UpdateTracker {
    private val observers = mutableListOf<LocationObserver>()

    override fun track(locations: List<MusicLocation>) {
        release()
        observers.addAll(locations.map { LocationObserver(context, it, callback) })
    }

    override fun release() {
        observers.forEach { it.release() }
        observers.clear()
    }
}