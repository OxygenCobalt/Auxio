/*
 * Copyright (c) 2026 Auxio Project
 * DeviceFileDeleter.kt is part of Auxio.
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

import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import org.oxycblt.musikr.Song
import timber.log.Timber as L

/**
 * Deletes the underlying audio files behind a set of [Song]s from device storage.
 *
 * [Song.uri] (see its own doc comment) is scoped-storage-safe to access, but which authority it
 * actually belongs to depends on [org.oxycblt.auxio.music.locations.LocationMode]:
 * [org.oxycblt.auxio.music.locations.LocationMode.SAF] songs carry a SAF *document* URI (from
 * [org.oxycblt.musikr.fs.saf.SAF]'s own directory walk), while
 * [org.oxycblt.auxio.music.locations.LocationMode.MEDIA_STORE] songs carry a genuine MediaStore
 * item URI. These need two entirely different deletion mechanisms -- confirmed, reproduced: passing
 * a SAF document URI into [MediaStore.createDeleteRequest] crashes outright with "All requested
 * items must be referenced by specific ID", since that call only ever accepts a real
 * `content://media/...` item URI, never a document URI from another provider entirely.
 *
 * A SAF document URI deletes via [DocumentsContract.deleteDocument], authorized by the persistable
 * write grant already taken on its tree when the user picked that folder (see
 * [org.oxycblt.musikr.fs.Location]) -- this needs no further system confirmation, since the user
 * already consented to this app managing that folder's contents. A MediaStore URI for a file *not
 * created by this app*, on the other hand, needs the system's own explicit, per-version consent: a
 * single batched confirmation dialog on Android 11+ ([MediaStore.createDeleteRequest]), a one-shot
 * recovery intent carried by [RecoverableSecurityException] on Android 10 specifically, or (pre-10,
 * before scoped storage existed) a direct [ContentResolver.delete] with no extra prompt at all.
 */
object DeviceFileDeleter {
    sealed interface Result {
        /** The deletion already ran to completion; no further action needed. */
        data class Done(val success: Boolean) : Result

        /** The system needs explicit consent first -- launch this and report back the outcome. */
        data class NeedsConfirmation(val intentSender: IntentSender) : Result
    }

    /**
     * Attempts to delete every [Song] in [songs]. Call this off the main thread -- every path here
     * makes at least one blocking call per song.
     */
    fun delete(resolver: ContentResolver, songs: List<Song>): Result {
        val (documentSongs, mediaStoreSongs) =
            songs.partition { it.uri.authority != MediaStore.AUTHORITY }

        var documentsOk = true
        for (song in documentSongs) {
            try {
                if (!DocumentsContract.deleteDocument(resolver, song.uri)) documentsOk = false
            } catch (e: Exception) {
                L.e("Could not delete document ${song.uri}: $e")
                documentsOk = false
            }
        }

        if (mediaStoreSongs.isEmpty()) {
            return Result.Done(documentsOk)
        }

        val mediaStoreUris = mediaStoreSongs.map { it.uri }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Handles any number of songs, including ones from multiple apps/owners, behind a
            // single system confirmation -- the only mechanism that can do that at all from
            // API 30 onward. Any document-URI songs above are already done by this point
            // regardless of what happens to this request, since they go through a completely
            // separate path.
            val pending = MediaStore.createDeleteRequest(resolver, mediaStoreUris)
            Result.NeedsConfirmation(pending.intentSender)
        } else {
            when (val result = deleteDirectly(resolver, mediaStoreUris)) {
                is Result.Done -> Result.Done(documentsOk && result.success)
                is Result.NeedsConfirmation -> result
            }
        }
    }

    private fun deleteDirectly(resolver: ContentResolver, uris: List<Uri>): Result {
        for (uri in uris) {
            try {
                resolver.delete(uri, null, null)
            } catch (e: RecoverableSecurityException) {
                // Only ever thrown on API 29 (Android 10, the one release with scoped storage
                // but not yet createDeleteRequest) -- its recovery intent only ever covers the
                // single Uri that triggered it, so if more than one Uri in this batch needs
                // recovery, only the first is surfaced here; the rest stay undeleted until the
                // user retries. A narrow, documented limitation of this one OS version, not of
                // every pre-R device -- API 24-28 has no scoped storage at all, so the plain
                // delete() call two lines up already succeeds there without reaching this catch.
                L.w("Delete needs recovery for $uri")
                return Result.NeedsConfirmation(e.userAction.actionIntent.intentSender)
            } catch (e: SecurityException) {
                L.e("Could not delete $uri: $e")
                return Result.Done(success = false)
            }
        }
        return Result.Done(success = true)
    }
}
