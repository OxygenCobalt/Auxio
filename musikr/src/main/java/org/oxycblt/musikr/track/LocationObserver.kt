/*
 * Copyright (c) 2025 Auxio Project
 * LocationObserver.kt is part of Auxio.
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
        context.applicationContext.contentResolver.registerContentObserver(location.uri, true, this)
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
