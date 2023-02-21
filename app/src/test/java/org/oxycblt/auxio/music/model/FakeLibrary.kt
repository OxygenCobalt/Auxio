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
 
package org.oxycblt.auxio.music.model

import android.content.Context
import android.net.Uri
import org.oxycblt.auxio.music.*

open class FakeLibrary : Library {
    override val songs: List<Song>
        get() = throw NotImplementedError()
    override val albums: List<Album>
        get() = throw NotImplementedError()
    override val artists: List<Artist>
        get() = throw NotImplementedError()
    override val genres: List<Genre>
        get() = throw NotImplementedError()

    override fun <T : Music> find(uid: Music.UID): T? {
        throw NotImplementedError()
    }

    override fun findSongForUri(context: Context, uri: Uri): Song? {
        throw NotImplementedError()
    }

    override fun <T : MusicParent> sanitize(parent: T): T? {
        throw NotImplementedError()
    }

    override fun sanitize(song: Song): Song? {
        throw NotImplementedError()
    }
}
