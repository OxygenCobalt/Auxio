package org.oxycblt.auxio.music.covers

import android.content.Context
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.cover.CoverIdentifier
import org.oxycblt.musikr.cover.Covers
import org.oxycblt.musikr.cover.MutableCovers
import org.oxycblt.musikr.cover.ObtainResult
import java.io.InputStream

class NullCovers(private val context: Context, private val identifier: CoverIdentifier) : MutableCovers {
    override suspend fun obtain(id: String) = ObtainResult.Hit(NullCover(id))

    override suspend fun write(data: ByteArray): Cover =
        NullCover(identifier.identify(data))

    override suspend fun cleanup(excluding: Collection<Cover>) {
        context.coversDir().listFiles()?.forEach { it.deleteRecursively() }
    }
}

private class NullCover(override val id: String) : Cover {
    override suspend fun open() = null
}