/*
 * Copyright (c) 2025 Auxio Project
 * UpdateTracker.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
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
        fun from(context: Context, callback: Callback): UpdateTracker =
            UpdateTrackerImpl(context, callback)
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
