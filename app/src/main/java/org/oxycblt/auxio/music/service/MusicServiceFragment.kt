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

import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import androidx.media.MediaBrowserServiceCompat.BrowserRoot
import androidx.media.MediaBrowserServiceCompat.Result
import androidx.media.utils.MediaConstants
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
    private val context: Context,
    foregroundListener: ForegroundListener,
    private val invalidator: Invalidator,
    indexerFactory: Indexer.Factory,
    musicBrowserFactory: MusicBrowser.Factory,
    private val musicRepository: MusicRepository
) : MusicBrowser.Invalidator {
    private val indexer = indexerFactory.create(context, foregroundListener)
    private val musicBrowser = musicBrowserFactory.create(context, this)
    private val dispatchJob = Job()
    private val dispatchScope = CoroutineScope(dispatchJob + Dispatchers.Default)

    class Factory
    @Inject
    constructor(
        private val indexerFactory: Indexer.Factory,
        private val musicBrowserFactory: MusicBrowser.Factory,
        private val musicRepository: MusicRepository
    ) {
        fun create(
            context: Context,
            foregroundListener: ForegroundListener,
            invalidator: Invalidator
        ): MusicServiceFragment =
            MusicServiceFragment(
                context,
                foregroundListener,
                invalidator,
                indexerFactory,
                musicBrowserFactory,
                musicRepository)
    }

    interface Invalidator {
        fun invalidateMusic(mediaId: String)
    }

    fun release() {
        dispatchJob.cancel()
        musicBrowser.release()
        indexer.release()
    }

    override fun invalidateMusic(ids: Set<String>) {
        ids.forEach { mediaId -> invalidator.invalidateMusic(mediaId) }
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
        BrowserRoot(
            MediaSessionUID.Tab(TabNode.Root(maxItems)).toString(),
            Bundle())

    fun getItem(mediaId: String, result: Result<MediaItem>) =
        result.dispatch { musicBrowser.getItem(mediaId) }

    fun getChildren(mediaId: String, result: Result<MutableList<MediaItem>>) =
        result.dispatch { musicBrowser.getChildren(mediaId)?.toMutableList() }

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
        detach()
        dispatchScope.launch {
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
    }
}
