package org.oxycblt.auxio.image.stack.cache

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

interface AppFiles {
    suspend fun read(file: String): InputStream?
    suspend fun write(file: String, inputStream: InputStream): Boolean
}

class AppFilesImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AppFiles {
    override suspend fun read(file: String): InputStream? =
        withContext(context = Dispatchers.IO) {
            try {
                context.openFileInput(file)
            } catch (e: IOException) {
                null
            }
        }

    override suspend fun write(file: String, inputStream: InputStream) =
        withContext(context = Dispatchers.IO) {
            try {
                context.openFileOutput(file, Context.MODE_PRIVATE).use { fileOutputStream ->
                    inputStream.copyTo(fileOutputStream)
                }
                true
            } catch (e: IOException) {
                false
            } finally {
                inputStream.close()
            }
        }

}