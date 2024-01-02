/*
 * Copyright (c) 2023 Auxio Project
 * DocumentPathFactory.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.fs

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

/**
 * A factory for parsing the reverse-engineered format of the URIs obtained from document picker.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface DocumentPathFactory {
    /**
     * Unpacks a document URI into a [Path] instance, using [fromDocumentId].
     *
     * @param uri The document URI to unpack.
     * @return The [Path] instance, or null if the URI could not be unpacked.
     */
    fun unpackDocumentUri(uri: Uri): Path?

    /**
     * Unpacks a document tree URI into a [Path] instance, using [fromDocumentId].
     *
     * @param uri The document tree URI to unpack.
     * @return The [Path] instance, or null if the URI could not be unpacked.
     */
    fun unpackDocumentTreeUri(uri: Uri): Path?

    /**
     * Serializes a [Path] instance into a document tree URI format path.
     *
     * @param path The [Path] instance to serialize.
     * @return The serialized path.
     */
    fun toDocumentId(path: Path): String

    /**
     * Deserializes a document tree URI format path into a [Path] instance.
     *
     * @param path The path to deserialize.
     * @return The [Path] instance, or null if the path could not be deserialized.
     */
    fun fromDocumentId(path: String): Path?
}

class DocumentPathFactoryImpl
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val volumeManager: VolumeManager,
    private val mediaStorePathInterpreterFactory: MediaStorePathInterpreter.Factory
) : DocumentPathFactory {
    override fun unpackDocumentUri(uri: Uri): Path? {
        val id = DocumentsContract.getDocumentId(uri)
        val numericId = id.toLongOrNull()
        return if (numericId != null) {
            // The document URI is special and points to an entry only accessible via
            // ContentResolver. In this case, we have to manually query MediaStore.
            for (prefix in POSSIBLE_CONTENT_URI_PREFIXES) {
                val contentUri = ContentUris.withAppendedId(prefix, numericId)

                val path =
                    context.contentResolverSafe.useQuery(
                        contentUri, mediaStorePathInterpreterFactory.projection) {
                            it.moveToFirst()
                            mediaStorePathInterpreterFactory.wrap(it).extract()
                        }

                if (path != null) {
                    return path
                }
            }

            null
        } else {
            fromDocumentId(id)
        }
    }

    override fun unpackDocumentTreeUri(uri: Uri): Path? {
        // Convert the document tree URI into it's relative path form, which can then be
        // parsed into a Directory instance.
        val docUri =
            DocumentsContract.buildDocumentUriUsingTree(
                uri, DocumentsContract.getTreeDocumentId(uri))
        val treeUri = DocumentsContract.getTreeDocumentId(docUri)
        return fromDocumentId(treeUri)
    }

    override fun toDocumentId(path: Path): String =
        when (val volume = path.volume) {
            // The primary storage has a volume prefix of "primary", regardless
            // of if it's internal or not.
            is Volume.Internal -> "$DOCUMENT_URI_PRIMARY_NAME:${path.components}"
            // Document tree URIs consist of a prefixed volume name followed by a relative path.
            is Volume.External -> "${volume.id}:${path.components}"
        }

    override fun fromDocumentId(path: String): Path? {
        // Document tree URIs consist of a prefixed volume name followed by a relative path,
        // delimited with a colon.
        val split = path.split(File.pathSeparator, limit = 2)
        val volume =
            when (split[0]) {
                // The primary storage has a volume prefix of "primary", regardless
                // of if it's internal or not.
                DOCUMENT_URI_PRIMARY_NAME -> volumeManager.getInternalVolume()
                // Removable storage has a volume prefix of it's UUID, try to find it
                // within StorageManager's volume list.
                else ->
                    volumeManager.getVolumes().find { it is Volume.External && it.id == split[0] }
            }
        val relativePath = split.getOrNull(1) ?: return null
        return Path(volume ?: return null, Components.parseUnix(relativePath))
    }

    private companion object {
        const val DOCUMENT_URI_PRIMARY_NAME = "primary"

        private val POSSIBLE_CONTENT_URI_PREFIXES =
            arrayOf(
                Uri.parse("content://downloads/public_downloads"),
                Uri.parse("content://downloads/my_downloads"))
    }
}
