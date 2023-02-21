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

import android.content.Context
import android.net.Uri
import java.text.CollationKey
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.music.metadata.Date
import org.oxycblt.auxio.music.metadata.Disc
import org.oxycblt.auxio.music.metadata.ReleaseType
import org.oxycblt.auxio.music.storage.MimeType
import org.oxycblt.auxio.music.storage.Path

open class FakeSong : Song {
    override val rawName: String?
        get() = throw NotImplementedError()
    override val rawSortName: String?
        get() = throw NotImplementedError()
    override val date: Date?
        get() = throw NotImplementedError()
    override val dateAdded: Long
        get() = throw NotImplementedError()
    override val disc: Disc?
        get() = throw NotImplementedError()
    override val genres: List<Genre>
        get() = throw NotImplementedError()
    override val mimeType: MimeType
        get() = throw NotImplementedError()
    override val track: Int?
        get() = throw NotImplementedError()
    override val path: Path
        get() = throw NotImplementedError()
    override val size: Long
        get() = throw NotImplementedError()
    override val uri: Uri
        get() = throw NotImplementedError()
    override val album: Album
        get() = throw NotImplementedError()
    override val artists: List<Artist>
        get() = throw NotImplementedError()
    override val collationKey: CollationKey?
        get() = throw NotImplementedError()
    override val durationMs: Long
        get() = throw NotImplementedError()
    override val uid: Music.UID
        get() = throw NotImplementedError()

    override fun resolveName(context: Context): String {
        throw NotImplementedError()
    }
}

open class FakeAlbum : Album {
    override val rawName: String?
        get() = throw NotImplementedError()
    override val rawSortName: String?
        get() = throw NotImplementedError()
    override val coverUri: Uri
        get() = throw NotImplementedError()
    override val dateAdded: Long
        get() = throw NotImplementedError()
    override val dates: Date.Range?
        get() = throw NotImplementedError()
    override val releaseType: ReleaseType
        get() = throw NotImplementedError()
    override val artists: List<Artist>
        get() = throw NotImplementedError()
    override val collationKey: CollationKey?
        get() = throw NotImplementedError()
    override val durationMs: Long
        get() = throw NotImplementedError()
    override val songs: List<Song>
        get() = throw NotImplementedError()
    override val uid: Music.UID
        get() = throw NotImplementedError()

    override fun resolveName(context: Context): String {
        throw NotImplementedError()
    }
}

open class FakeArtist : Artist {
    override val rawName: String?
        get() = throw NotImplementedError()
    override val rawSortName: String?
        get() = throw NotImplementedError()
    override val albums: List<Album>
        get() = throw NotImplementedError()
    override val genres: List<Genre>
        get() = throw NotImplementedError()
    override val isCollaborator: Boolean
        get() = throw NotImplementedError()
    override val collationKey: CollationKey?
        get() = throw NotImplementedError()
    override val durationMs: Long
        get() = throw NotImplementedError()
    override val songs: List<Song>
        get() = throw NotImplementedError()
    override val uid: Music.UID
        get() = throw NotImplementedError()

    override fun resolveName(context: Context): String {
        throw NotImplementedError()
    }
}

open class FakeGenre : Genre {
    override val rawName: String?
        get() = throw NotImplementedError()
    override val rawSortName: String?
        get() = throw NotImplementedError()
    override val albums: List<Album>
        get() = throw NotImplementedError()
    override val artists: List<Artist>
        get() = throw NotImplementedError()
    override val collationKey: CollationKey?
        get() = throw NotImplementedError()
    override val durationMs: Long
        get() = throw NotImplementedError()
    override val songs: List<Song>
        get() = throw NotImplementedError()
    override val uid: Music.UID
        get() = throw NotImplementedError()

    override fun resolveName(context: Context): String {
        throw NotImplementedError()
    }
}
