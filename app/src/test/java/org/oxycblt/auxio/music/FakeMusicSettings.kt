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
 
package org.oxycblt.auxio.music

import org.oxycblt.auxio.music.library.Sort
import org.oxycblt.auxio.music.storage.MusicDirectories

interface FakeMusicSettings : MusicSettings {
    override fun registerListener(listener: MusicSettings.Listener) = throw NotImplementedError()
    override fun unregisterListener(listener: MusicSettings.Listener) = throw NotImplementedError()
    override var musicDirs: MusicDirectories
        get() = throw NotImplementedError()
        set(_) = throw NotImplementedError()
    override val excludeNonMusic: Boolean
        get() = throw NotImplementedError()
    override val shouldBeObserving: Boolean
        get() = throw NotImplementedError()
    override var multiValueSeparators: String
        get() = throw NotImplementedError()
        set(_) = throw NotImplementedError()
    override var songSort: Sort
        get() = throw NotImplementedError()
        set(_) = throw NotImplementedError()
    override var albumSort: Sort
        get() = throw NotImplementedError()
        set(_) = throw NotImplementedError()
    override var artistSort: Sort
        get() = throw NotImplementedError()
        set(_) = throw NotImplementedError()
    override var genreSort: Sort
        get() = throw NotImplementedError()
        set(_) = throw NotImplementedError()
    override var albumSongSort: Sort
        get() = throw NotImplementedError()
        set(_) = throw NotImplementedError()
    override var artistSongSort: Sort
        get() = throw NotImplementedError()
        set(_) = throw NotImplementedError()
    override var genreSongSort: Sort
        get() = throw NotImplementedError()
        set(_) = throw NotImplementedError()
}
