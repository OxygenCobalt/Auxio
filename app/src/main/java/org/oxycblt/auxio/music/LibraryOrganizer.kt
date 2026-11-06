/*
 * Copyright (c) 2026 Auxio Project
 * LibraryOrganizer.kt is part of Auxio.
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

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import org.oxycblt.musikr.Song
import timber.log.Timber as L

/**
 * Moves the underlying audio files behind a set of [Song]s into a new on-disk folder structure
 * (e.g. "Album (Year)/01 Title.mp3"), based on a user-configurable pair of templates.
 *
 * Only [org.oxycblt.auxio.music.locations.LocationMode.SAF] songs are eligible: those carry a SAF
 * document [Song.uri] that this app already has a persistent write grant on (see
 * [org.oxycblt.musikr.fs.Location]), so a move can happen outright via [DocumentsContract] with no
 * further system confirmation -- the same reasoning [DeviceFileDeleter] uses for its own SAF path.
 * [org.oxycblt.auxio.music.locations.LocationMode.MEDIA_STORE] songs carry a real
 * `content://media/...` URI this app doesn't own, which has no equivalent move primitive at all
 * (only per-field [android.content.ContentResolver] updates with the same tiered consent model
 * [DeviceFileDeleter] has to juggle for deletes) -- those are reported back as skipped rather than
 * attempted.
 */
object LibraryOrganizer {
    /** A single planned relocation, computed but not yet applied. */
    data class PlannedMove(
        val song: Song,
        /** Human-readable current location, for display only. */
        val oldDisplayPath: String,
        val folderSegments: List<String>,
        val fileName: String,
    ) {
        /** Human-readable destination, for display only. */
        val newDisplayPath: String
            get() = (folderSegments + fileName).joinToString("/")
    }

    enum class SkipReason {
        /** The song isn't backed by a SAF document this app can move without extra consent. */
        NOT_SAF,
        /** A token required by the configured templates had no value for this song. */
        MISSING_METADATA,
    }

    data class SkippedSong(val song: Song, val reason: SkipReason)

    data class Plan(val moves: List<PlannedMove>, val skipped: List<SkippedSong>)

    data class ExecuteResult(val succeeded: Int, val failed: Int)

    private val ILLEGAL_CHARS = Regex("""[/\\:*?"<>|\x00-\x1F]""")

    private fun String.sanitizeSegment() = replace(ILLEGAL_CHARS, "_").trim().ifEmpty { "_" }

    private fun applyTemplate(template: String, tokens: Map<String, String>): String {
        var result = template
        for ((key, value) in tokens) {
            result = result.replace("{$key}", value)
        }
        return result
    }

    /**
     * Computes what [execute] would do, without touching any files. Call this off the main
     * thread -- it resolves display names for every song in [songs].
     */
    fun plan(
        context: Context,
        songs: Collection<Song>,
        folderFormat: String,
        fileFormat: String,
    ): Plan {
        val moves = mutableListOf<PlannedMove>()
        val skipped = mutableListOf<SkippedSong>()
        val usedPaths = mutableSetOf<String>()

        for (song in songs) {
            if (song.uri.authority == MediaStore.AUTHORITY) {
                skipped.add(SkippedSong(song, SkipReason.NOT_SAF))
                continue
            }

            val album = song.album
            val year = album.dates?.min?.year
            if (folderFormat.contains("{year}") && year == null) {
                skipped.add(SkippedSong(song, SkipReason.MISSING_METADATA))
                continue
            }

            val tokens =
                mapOf(
                    "album" to album.name.resolve(context).sanitizeSegment(),
                    "artist" to song.artists.resolveNames(context).sanitizeSegment(),
                    "albumArtist" to album.artists.resolveNames(context).sanitizeSegment(),
                    "year" to (year?.toString() ?: ""),
                    "track" to (song.track?.toString()?.padStart(2, '0') ?: "00"),
                    "disc" to (song.disc?.number?.toString() ?: ""),
                    "title" to song.name.resolve(context).sanitizeSegment(),
                )

            val folderPath = applyTemplate(folderFormat, tokens)
            val folderSegments =
                folderPath.split('/').map { it.sanitizeSegment() }.filter { it != "_" }
            val fileBaseName = applyTemplate(fileFormat, tokens).replace("/", " ").sanitizeSegment()
            if (folderSegments.isEmpty() || fileBaseName == "_") {
                skipped.add(SkippedSong(song, SkipReason.MISSING_METADATA))
                continue
            }

            val extension = song.path.components.name?.substringAfterLast('.', "").orEmpty()
            val fileNameFor = { base: String -> if (extension.isEmpty()) base else "$base.$extension" }

            var fileName = fileNameFor(fileBaseName)
            var candidateKey = (folderSegments + fileName).joinToString("/").lowercase()
            var suffix = 2
            while (candidateKey in usedPaths) {
                fileName = fileNameFor("$fileBaseName ($suffix)")
                candidateKey = (folderSegments + fileName).joinToString("/").lowercase()
                suffix++
            }
            usedPaths.add(candidateKey)

            moves.add(PlannedMove(song, song.path.resolve(context), folderSegments, fileName))
        }

        return Plan(moves, skipped)
    }

    /**
     * Actually performs a reorganization the user has already confirmed via [plan]. Call this off
     * the main thread -- every move makes several blocking [DocumentsContract] calls.
     */
    fun execute(resolver: ContentResolver, moves: List<PlannedMove>): ExecuteResult {
        var succeeded = 0
        var failed = 0
        for (move in moves) {
            if (moveOne(resolver, move)) succeeded++ else failed++
        }
        return ExecuteResult(succeeded, failed)
    }

    private fun moveOne(resolver: ContentResolver, move: PlannedMove): Boolean {
        val song = move.song
        return try {
            // The ancestor chain from the SAF tree root down to this document -- the only
            // standard way to recover a SAF document's current parent, since Song carries no
            // parent reference once indexing is done (Song.path is display-only, see its doc
            // comment, and can't be trusted to still match the real on-disk layout).
            val ids = DocumentsContract.findDocumentPath(resolver, song.uri)?.path
            if (ids == null || ids.size < 2) {
                L.w("Cannot organize ${song.uri}: no parent directory in SAF tree")
                return false
            }
            var currentUri = song.uri
            val currentParentUri =
                DocumentsContract.buildDocumentUriUsingTree(song.uri, ids[ids.size - 2])

            val currentName = move.oldDisplayPath.substringAfterLast('/')
            if (currentName != move.fileName) {
                DocumentsContract.renameDocument(resolver, currentUri, move.fileName)?.let {
                    currentUri = it
                }
            }

            var targetDirUri = DocumentsContract.buildDocumentUriUsingTree(song.uri, ids.first())
            for (segment in move.folderSegments) {
                targetDirUri = findOrCreateChildDirectory(resolver, targetDirUri, segment) ?: return false
            }

            if (DocumentsContract.getDocumentId(targetDirUri) !=
                DocumentsContract.getDocumentId(currentParentUri)) {
                currentUri =
                    try {
                        DocumentsContract.moveDocument(resolver, currentUri, currentParentUri, targetDirUri)
                            ?: currentUri
                    } catch (e: Exception) {
                        L.w("moveDocument failed for ${song.uri}, falling back to copy+delete: $e")
                        copyAndDelete(resolver, currentUri, targetDirUri, move.fileName) ?: return false
                    }
            }
            true
        } catch (e: Exception) {
            L.e("Failed to organize ${song.uri}: $e")
            false
        }
    }

    private fun findOrCreateChildDirectory(
        resolver: ContentResolver,
        parentUri: Uri,
        name: String,
    ): Uri? {
        val childrenUri =
            DocumentsContract.buildChildDocumentsUriUsingTree(
                parentUri, DocumentsContract.getDocumentId(parentUri))
        val projection =
            arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
            )
        try {
            resolver.query(childrenUri, projection, null, null, null)?.use { cursor ->
                val idIdx = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                val nameIdx =
                    cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                val mimeIdx = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)
                while (cursor.moveToNext()) {
                    if (cursor.getString(mimeIdx) == DocumentsContract.Document.MIME_TYPE_DIR &&
                        cursor.getString(nameIdx) == name) {
                        return DocumentsContract.buildDocumentUriUsingTree(
                            parentUri, cursor.getString(idIdx))
                    }
                }
            }
        } catch (e: Exception) {
            L.e("Could not query children of $parentUri: $e")
            return null
        }
        return try {
            DocumentsContract.createDocument(
                resolver, parentUri, DocumentsContract.Document.MIME_TYPE_DIR, name)
        } catch (e: Exception) {
            L.e("Could not create directory $name under $parentUri: $e")
            null
        }
    }

    private fun copyAndDelete(
        resolver: ContentResolver,
        sourceUri: Uri,
        targetDirUri: Uri,
        fileName: String,
    ): Uri? {
        val mimeType = resolver.getType(sourceUri) ?: "application/octet-stream"
        val newUri =
            try {
                DocumentsContract.createDocument(resolver, targetDirUri, mimeType, fileName)
            } catch (e: Exception) {
                L.e("Could not create destination file for copy fallback: $e")
                return null
            } ?: return null
        return try {
            val copied =
                resolver.openInputStream(sourceUri)?.use { input ->
                    resolver.openOutputStream(newUri)?.use { output ->
                        input.copyTo(output)
                        true
                    }
                }
            if (copied != true) {
                DocumentsContract.deleteDocument(resolver, newUri)
                return null
            }
            DocumentsContract.deleteDocument(resolver, sourceUri)
            newUri
        } catch (e: Exception) {
            L.e("Copy fallback failed, cleaning up partial destination file: $e")
            try {
                DocumentsContract.deleteDocument(resolver, newUri)
            } catch (_: Exception) {}
            null
        }
    }
}
