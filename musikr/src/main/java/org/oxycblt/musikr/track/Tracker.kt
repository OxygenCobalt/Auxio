/*
 * Copyright (c) 2024 Auxio Project
 * Tracker.kt is part of Auxio.
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
import org.oxycblt.musikr.tag.interpret.PreSong

abstract class Tracker {
    internal abstract suspend fun track(preSong: PreSong): TrackedSong

    companion object {
        fun from(context: Context): Tracker =
            TrackerImpl(TrackerDatabase.from(context).trackedSongsDao())
    }
}

internal data class TrackedSong(val preSong: PreSong, val dateAdded: Long)

private class TrackerImpl(private val dao: TrackedSongsDao) : Tracker() {
    override suspend fun track(preSong: PreSong): TrackedSong {
        val currentTime = System.currentTimeMillis()
        val entity = TrackedSongEntity(uid = preSong.uid.toString(), dateAdded = currentTime)
        dao.insertSong(entity)
        val trackedEntity = dao.selectSong(preSong.uid.toString())
        return TrackedSong(preSong = preSong, dateAdded = trackedEntity?.dateAdded ?: currentTime)
    }
}
