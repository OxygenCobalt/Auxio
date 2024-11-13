package org.oxycblt.auxio.music.fs


import android.content.ContentResolver
import android.content.Context
import android.media.MediaFormat
import android.net.Uri
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.provider.DocumentsContract
import android.webkit.MimeTypeMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface DeviceFiles {
    fun explore(uris: Flow<Uri>): Flow<DeviceFile>
}

@OptIn(ExperimentalCoroutinesApi::class)
class DeviceFilesImpl @Inject constructor(private val contentResolver: ContentResolver) :
    DeviceFiles {
    override fun explore(uris: Flow<Uri>): Flow<DeviceFile> =
        uris.flatMapMerge { rootUri ->
            exploreImpl(contentResolver, rootUri)
        }

    private fun exploreImpl(
        contentResolver: ContentResolver,
        uri: Uri
    ): Flow<DeviceFile> =
        flow {
            contentResolver.useQuery(uri, PROJECTION) { cursor ->
                val childUriIndex =
                    cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                val mimeTypeIndex =
                    cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)
                // Recurse into sub-directories as another flowO
                val recursions = mutableListOf<Flow<DeviceFile>>()
                while (cursor.moveToNext()) {
                    val childId = cursor.getString(childUriIndex)
                    val childUri = DocumentsContract.buildDocumentUriUsingTree(uri, childId)
                    val mimeType = cursor.getString(mimeTypeIndex)
                    if (mimeType == DocumentsContract.Document.MIME_TYPE_DIR) {
                        // This does NOT block the current coroutine. Instead, we will
                        // evaluate this flow in parallel later to maximize throughput.
                        recursions.add(exploreImpl(contentResolver, childUri))
                    } else {
                        // Immediately emit all files given that it's just an O(1) op.
                        // This also just makes sure the outer flow has a reason to exist
                        // rather than just being a glorified async.
                        emit(DeviceFile(childUri, mimeType))
                    }
                }
                // Hypothetically, we could just emitAll as we recurse into a new directory,
                // but this will block the flow and force the tree search to be sequential.
                // Instead, try to leverage flow parallelism  and do all recursive calls in parallel.
                // Kotlin coroutines can handle doing possibly thousands of parallel calls, it'll
                // be fine. I hope.
                emitAll(recursions.asFlow().flattenMerge())
            }
        }

    private companion object {
        val PROJECTION = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_MIME_TYPE
        )
    }
}

data class DeviceFile(val uri: Uri, val mimeType: String)