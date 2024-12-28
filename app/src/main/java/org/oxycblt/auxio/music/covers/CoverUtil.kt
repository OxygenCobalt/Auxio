package org.oxycblt.auxio.music.covers

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun Context.coversDir() = withContext(Dispatchers.IO) {
    filesDir.resolve("covers").apply { mkdirs() }
}