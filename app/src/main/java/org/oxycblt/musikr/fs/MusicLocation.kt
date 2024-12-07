package org.oxycblt.musikr.fs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import dagger.hilt.android.qualifiers.ApplicationContext
import org.oxycblt.musikr.fs.path.DocumentPathFactory
import javax.inject.Inject

class MusicLocation internal constructor(val uri: Uri, val path: Path) {
    override fun equals(other: Any?) =
        other is MusicLocation && uri == other.uri && path == other.path

    override fun hashCode() = 31 * uri.hashCode() + path.hashCode()
    override fun toString() = "src:$uri=$path"

    interface Factory {
        fun create(uri: Uri): MusicLocation?
    }
}

class MusicLocationFactoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val documentPathFactory: DocumentPathFactory
) : MusicLocation.Factory {
    override fun create(uri: Uri): MusicLocation? {
        check(DocumentsContract.isTreeUri(uri)) { "URI $uri is not a document tree URI" }
        val path = documentPathFactory.unpackDocumentTreeUri(uri) ?: return null
        context.contentResolverSafe.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
        return MusicLocation(uri, path)
    }
}