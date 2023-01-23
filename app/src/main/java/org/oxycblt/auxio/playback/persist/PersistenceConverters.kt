/*
 * Copyright (c) 2023 Auxio Project
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
 
package org.oxycblt.auxio.playback.persist

import androidx.room.TypeConverter
import org.oxycblt.auxio.music.Music

/**
 * Defines conversions used in the persistence table.
 * @author Alexander Capehart (OxygenCobalt)
 */
object PersistenceConverters {
    /** @see [Music.UID.toString] */
    @TypeConverter fun fromMusicUID(uid: Music.UID?) = uid?.toString()

    /** @see [Music.UID.fromString] */
    @TypeConverter fun toMusicUid(string: String?) = string?.let(Music.UID::fromString)
}
