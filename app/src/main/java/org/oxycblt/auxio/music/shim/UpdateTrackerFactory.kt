/*
 * Copyright (c) 2025 Auxio Project
 * UpdateTrackerFactory.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.shim

import android.content.Context
import org.oxycblt.musikr.track.UpdateTracker

interface UpdateTrackerFactory {
    fun create(callback: UpdateTracker.Callback): UpdateTracker
}

class UpdateTrackerFactoryImpl(private val context: Context) : UpdateTrackerFactory {
    override fun create(callback: UpdateTracker.Callback) = UpdateTracker.from(context, callback)
}
