package org.oxycblt.auxio.music.import

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.device.DeviceLibrary
import org.oxycblt.auxio.music.fs.ContentPathResolver
import org.oxycblt.auxio.music.fs.Path
import org.oxycblt.auxio.music.fs.contentResolverSafe
import javax.inject.Inject

interface PlaylistImporter {
    suspend fun import(uri: Uri): ImportedPlaylist?
}

data class ImportedPlaylist(val name: String?, val paths: List<Path>)

class PlaylistImporterImpl @Inject constructor(
    @ApplicationContext private val contentResolver: ContentResolver,
    private val contentPathResolver: ContentPathResolver,
    private val m3u: M3U
) : PlaylistImporter {
    override suspend fun import(uri: Uri): ImportedPlaylist? {
        val workingDirectory = contentPathResolver.resolve(uri) ?: return null
        return contentResolver.openInputStream(uri)?.use {
            val paths = m3u.read(it, workingDirectory) ?: return null
            return ImportedPlaylist(null, paths)
        }
    }
}