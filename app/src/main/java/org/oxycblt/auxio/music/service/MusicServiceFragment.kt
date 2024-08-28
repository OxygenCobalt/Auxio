/*
 * Copyright (c) 2024 Auxio Project
 * MusicServiceFragment.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.service

import android.support.v4.media.MediaBrowserCompat.MediaItem
import androidx.media.MediaBrowserServiceCompat.Result
import androidx.media.MediaBrowserServiceCompat.BrowserRoot
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.oxycblt.auxio.ForegroundListener
import org.oxycblt.auxio.ForegroundServiceNotification
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

class MusicServiceFragment
@Inject
constructor(
    private val indexer: Indexer,
    private val musicBrowser: MusicBrowser,
    private val musicRepository: MusicRepository
) : MusicBrowser.Invalidator {
    private var invalidator: Invalidator? = null
    private val dispatchJob = Job()
    private val dispatchScope = CoroutineScope(dispatchJob + Dispatchers.Default)

    interface Invalidator {
        fun invalidateMusic(mediaId: String)
    }

    fun attach(foregroundListener: ForegroundListener, invalidator: Invalidator) {
        this.invalidator = invalidator
        indexer.attach(foregroundListener)
        musicBrowser.attach(this)
    }

    fun release() {
        dispatchJob.cancel()
        musicBrowser.release()
        indexer.release()
        invalidator = null
    }

    override fun invalidateMusic(ids: Set<String>) {
        ids.forEach { mediaId ->
            requireNotNull(invalidator) { "Invalidator not available" }.invalidateMusic(mediaId)
        }
    }

    fun start() {
        if (musicRepository.indexingState == null) {
            musicRepository.requestIndex(true)
        }
    }

    fun createNotification(post: (ForegroundServiceNotification?) -> Unit) {
        indexer.createNotification(post)
    }

    fun getRoot(maxItems: Int) =
        BrowserRoot(MediaSessionUID.CategoryItem(Category.Root(maxItems)).toString(), null)

    fun getItem(mediaId: String, result: Result<MediaItem>) =
        result.dispatch { musicBrowser.getItem(mediaId) }

    fun getChildren(
        mediaId: String,
        result: Result<MutableList<MediaItem>>
    ) = result.dispatch { musicBrowser.getChildren(mediaId)?.toMutableList() }

    fun search(query: String, result: Result<MutableList<MediaItem>>) =
        result.dispatchAsync { musicBrowser.search(query) }

    private fun <T> Result<T>.dispatch(body: () -> T?) {
        try {
            val result = body()
            if (result == null) {
                logW("Result is null")
            }
            sendResult(result)
        } catch (e: Exception) {
            logD("Error while dispatching: $e")
            sendResult(null)
        }
    }

    private fun <T> Result<T>.dispatchAsync(body: suspend () -> T?) {
        dispatchScope.launch {
            try {
                detach()
                val result = body()
                if (result == null) {
                    logW("Result is null")
                }
                sendResult(result)
            } catch (e: Exception) {
                logD("Error while dispatching: $e")
                sendResult(null)
            }
        }
    }
}
