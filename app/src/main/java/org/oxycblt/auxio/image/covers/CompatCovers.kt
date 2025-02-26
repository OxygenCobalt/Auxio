package org.oxycblt.auxio.image.covers


import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.cover.CoverResult
import org.oxycblt.musikr.cover.Covers
import org.oxycblt.musikr.cover.FileCover
import org.oxycblt.musikr.cover.MutableCovers
import org.oxycblt.musikr.fs.DeviceFile
import org.oxycblt.musikr.metadata.Metadata

open class CompatCovers(private val context: Context, private val inner: Covers<FileCover>) : Covers<FileCover> {
    override suspend fun obtain(id: String): CoverResult<FileCover> {
        when (val innerResult = inner.obtain(id)) {
            is CoverResult.Hit -> return CoverResult.Hit(innerResult.cover)
            is CoverResult.Miss -> {
                if (!id.startsWith("compat:")) return CoverResult.Miss()
                val uri = Uri.parse(id.substringAfter("compat:"))
                return CoverResult.Hit(CompatCover(context, uri))
            }
        }
    }
}

class MutableCompatCovers(private val context: Context, private val inner: MutableCovers<FileCover>) : CompatCovers(context, inner), MutableCovers<FileCover> {
    override suspend fun create(file: DeviceFile, metadata: Metadata): CoverResult<FileCover> {
        when (val innerResult = inner.create(file, metadata)) {
            is CoverResult.Hit -> return CoverResult.Hit(innerResult.cover)
            is CoverResult.Miss -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    return CoverResult.Miss()
                }
                val mediaStoreUri = MediaStore.getMediaUri(context, file.uri) ?: return CoverResult.Miss()
                val proj = arrayOf(MediaStore.MediaColumns._ID)
                val cursor = context.contentResolver.query(mediaStoreUri, proj, null, null, null)
                val uri = cursor.use {
                    if (it == null || !it.moveToFirst()) {
                        return CoverResult.Miss()
                    }
                    val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.buildUpon().run {
                        appendPath(id.toString())
                        appendPath("albumart")
                        build()
                    }
                }
                return CoverResult.Hit(CompatCover(context, uri))
            }
        }
    }

    override suspend fun cleanup(excluding: Collection<Cover>) {}
}

class CompatCover(private val context: Context, private val uri: Uri) : FileCover {
    override val id = "compat:$uri"

    override suspend fun fd(): ParcelFileDescriptor? {
        return context.contentResolver.openFileDescriptor(uri, "r")
    }

    override suspend fun open() = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)
    }
}