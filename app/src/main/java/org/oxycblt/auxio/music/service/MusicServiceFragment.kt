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
import android.support.v4.media.MediaBrowserCompat.MediaItem
import androidx.media.MediaBrowserServiceCompat
import androidx.media.MediaBrowserServiceCompat.BrowserRoot
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.oxycblt.auxio.ForegroundListener
import org.oxycblt.auxio.ForegroundServiceNotification
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.IndexingState
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.search.SearchEngine
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

class MusicServiceFragment
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val indexer: Indexer,
    private val musicBrowser: MusicBrowser,
    private val musicRepository: MusicRepository,
    private val musicSettings: MusicSettings,
    private val searchEngine: SearchEngine,
    private val contentObserver: SystemContentObserver,
) : MusicBrowser.Invalidator, MusicSettings.Listener {
    private val indexingNotification = IndexingNotification(context)
    private val observingNotification = ObservingNotification(context)
    private var invalidator: Invalidator? = null
    private var foregroundListener: ForegroundListener? = null
    private val dispatchJob = Job()
    private val dispatchScope = CoroutineScope(dispatchJob + Dispatchers.Default)

    interface Invalidator {
        fun invalidateMusic(mediaId: String)
    }

    fun attach(foregroundListener: ForegroundListener, invalidator: Invalidator) {
        this.invalidator = invalidator
        indexer.attach(foregroundListener)
        musicBrowser.attach(this)
        contentObserver.attach()
        musicSettings.registerListener(this)
    }

    fun release() {
        musicSettings.unregisterListener(this)
        contentObserver.release()
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

    override fun onObservingChanged() {
        super.onObservingChanged()
        // Make sure we don't override the service state with the observing
        // notification if we were actively loading when the automatic rescanning
        // setting changed. In such a case, the state will still be updated when
        // the music loading process ends.
        if (musicRepository.indexingState == null) {
            logD("Not loading, updating idle session")
            foregroundListener?.updateForeground(ForegroundListener.Change.INDEXER)
        }
    }

    fun start() {
        if (musicRepository.indexingState == null) {
            musicRepository.requestIndex(true)
        }
    }

    fun createNotification(post: (ForegroundServiceNotification?) -> Unit) {
        val state = musicRepository.indexingState
        if (state is IndexingState.Indexing) {
            // There are a few reasons why we stay in the foreground with automatic rescanning:
            // 1. Newer versions of Android have become more and more restrictive regarding
            // how a foreground service starts. Thus, it's best to go foreground now so that
            // we can go foreground later.
            // 2. If a non-foreground service is killed, the app will probably still be alive,
            // and thus the music library will not be updated at all.
            val changed = indexingNotification.updateIndexingState(state.progress)
            if (changed) {
                post(indexingNotification)
            }
        } else if (musicSettings.shouldBeObserving) {
            // Not observing and done loading, exit foreground.
            logD("Exiting foreground")
            post(observingNotification)
        } else {
            post(null)
        }
    }

    fun getRoot() = BrowserRoot(Category.ROOT.id, null)

    fun getItem(mediaId: String, result: MediaBrowserServiceCompat.Result<MediaItem>) =
        result.dispatch { musicBrowser.getItem(mediaId) }

    fun getChildren(
        mediaId: String,
        result: MediaBrowserServiceCompat.Result<MutableList<MediaItem>>
    ) = result.dispatch { musicBrowser.getChildren(mediaId)?.toMutableList() }

    fun search(query: String, result: MediaBrowserServiceCompat.Result<MutableList<MediaItem>>) =
        result.dispatchAsync {
            if (query.isEmpty()) {
                return@dispatchAsync mutableListOf()
            }
            val deviceLibrary =
                musicRepository.deviceLibrary ?: return@dispatchAsync mutableListOf()
            val userLibrary = musicRepository.userLibrary ?: return@dispatchAsync mutableListOf()
            val items =
                SearchEngine.Items(
                    deviceLibrary.songs,
                    deviceLibrary.albums,
                    deviceLibrary.artists,
                    deviceLibrary.genres,
                    userLibrary.playlists)
            searchEngine.search(items, query).toMediaItems()
        }

    private fun SearchEngine.Items.toMediaItems(): MutableList<MediaItem> {
        val music = mutableListOf<MediaItem>()
        if (songs != null) {
            music.addAll(songs.map { it.toMediaItem(context, null, header(R.string.lbl_songs)) })
        }
        if (albums != null) {
            music.addAll(albums.map { it.toMediaItem(context, null, header(R.string.lbl_albums)) })
        }
        if (artists != null) {
            music.addAll(artists.map { it.toMediaItem(context, header(R.string.lbl_artists)) })
        }
        if (genres != null) {
            music.addAll(genres.map { it.toMediaItem(context, header(R.string.lbl_genres)) })
        }
        if (playlists != null) {
            music.addAll(playlists.map { it.toMediaItem(context, header(R.string.lbl_playlists)) })
        }
        return music
    }

    private fun <T> MediaBrowserServiceCompat.Result<T>.dispatch(body: () -> T?) {
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

    private fun <T> MediaBrowserServiceCompat.Result<T>.dispatchAsync(body: suspend () -> T?) {
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
