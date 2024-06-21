/*
 * Copyright (c) 2024 Auxio Project
 * MediaItemBrowser.kt is part of Auxio.
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
import androidx.annotation.StringRes
import androidx.media.utils.MediaConstants
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaSession.ControllerInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import org.oxycblt.auxio.R
import org.oxycblt.auxio.list.ListSettings
import org.oxycblt.auxio.list.sort.Sort
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.device.DeviceLibrary
import org.oxycblt.auxio.music.user.UserLibrary
import org.oxycblt.auxio.search.SearchEngine

class MediaItemBrowser
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val musicRepository: MusicRepository,
    private val listSettings: ListSettings,
    private val searchEngine: SearchEngine
) : MusicRepository.UpdateListener {
    private val browserJob = Job()
    private val searchScope = CoroutineScope(browserJob + Dispatchers.Default)
    private val searchSubscribers = mutableMapOf<ControllerInfo, String>()
    private val searchResults = mutableMapOf<String, Deferred<SearchEngine.Items>>()
    private var invalidator: Invalidator? = null

    interface Invalidator {
        fun invalidate(ids: Map<String, Int>)

        fun invalidate(controller: ControllerInfo, query: String, itemCount: Int)
    }

    fun attach(invalidator: Invalidator) {
        this.invalidator = invalidator
        musicRepository.addUpdateListener(this)
    }

    fun release() {
        browserJob.cancel()
        invalidator = null
        musicRepository.removeUpdateListener(this)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        val deviceLibrary = musicRepository.deviceLibrary
        var invalidateSearch = false
        val invalidate = mutableMapOf<String, Int>()
        if (changes.deviceLibrary && deviceLibrary != null) {
            MediaSessionUID.Category.DEVICE_MUSIC.forEach {
                invalidate[it.toString()] = getCategorySize(it, musicRepository)
            }

            deviceLibrary.albums.forEach {
                val id = MediaSessionUID.Single(it.uid).toString()
                invalidate[id] = it.songs.size
            }

            deviceLibrary.artists.forEach {
                val id = MediaSessionUID.Single(it.uid).toString()
                invalidate[id] = it.songs.size + it.explicitAlbums.size + it.implicitAlbums.size
            }

            deviceLibrary.genres.forEach {
                val id = MediaSessionUID.Single(it.uid).toString()
                invalidate[id] = it.songs.size + it.artists.size
            }

            invalidateSearch = true
        }
        val userLibrary = musicRepository.userLibrary
        if (changes.userLibrary && userLibrary != null) {
            MediaSessionUID.Category.USER_MUSIC.forEach {
                invalidate[it.toString()] = getCategorySize(it, musicRepository)
            }
            userLibrary.playlists.forEach {
                val id = MediaSessionUID.Single(it.uid).toString()
                invalidate[id] = it.songs.size
            }
            invalidateSearch = true
        }

        if (invalidate.isNotEmpty()) {
            invalidator?.invalidate(invalidate)
        }

        if (invalidateSearch) {
            for (entry in searchResults.entries) {
                searchResults[entry.key]?.cancel()
            }
            searchResults.clear()

            for (entry in searchSubscribers.entries) {
                if (searchResults[entry.value] != null) {
                    continue
                }
                searchResults[entry.value] = searchTo(entry.value)
            }
        }
    }

    val root: MediaItem
        get() = MediaSessionUID.Category.ROOT.toMediaItem(context)

    fun getItem(mediaId: String): MediaItem? {
        val music =
            when (val uid = MediaSessionUID.fromString(mediaId)) {
                is MediaSessionUID.Category -> return uid.toMediaItem(context)
                is MediaSessionUID.Single ->
                    musicRepository.find(uid.uid)?.let { musicRepository.find(it.uid) }
                is MediaSessionUID.Joined ->
                    musicRepository.find(uid.childUid)?.let { musicRepository.find(it.uid) }
                null -> null
            }
                ?: return null

        return when (music) {
            is Album -> music.toMediaItem(context)
            is Artist -> music.toMediaItem(context)
            is Genre -> music.toMediaItem(context)
            is Playlist -> music.toMediaItem(context)
            is Song -> music.toMediaItem(context, null)
        }
    }

    fun getChildren(parentId: String, page: Int, pageSize: Int): List<MediaItem>? {
        val deviceLibrary = musicRepository.deviceLibrary
        val userLibrary = musicRepository.userLibrary
        if (deviceLibrary == null || userLibrary == null) {
            return listOf()
        }

        val items = getMediaItemList(parentId, deviceLibrary, userLibrary) ?: return null
        return items.paginate(page, pageSize)
    }

    private fun getMediaItemList(
        id: String,
        deviceLibrary: DeviceLibrary,
        userLibrary: UserLibrary
    ): List<MediaItem>? {
        return when (val mediaSessionUID = MediaSessionUID.fromString(id)) {
            is MediaSessionUID.Category -> {
                when (mediaSessionUID) {
                    MediaSessionUID.Category.ROOT ->
                        MediaSessionUID.Category.IMPORTANT.map { it.toMediaItem(context) }
                    MediaSessionUID.Category.SONGS ->
                        listSettings.songSort.songs(deviceLibrary.songs).map {
                            it.toMediaItem(context, null)
                        }
                    MediaSessionUID.Category.ALBUMS ->
                        listSettings.albumSort.albums(deviceLibrary.albums).map {
                            it.toMediaItem(context)
                        }
                    MediaSessionUID.Category.ARTISTS ->
                        listSettings.artistSort.artists(deviceLibrary.artists).map {
                            it.toMediaItem(context)
                        }
                    MediaSessionUID.Category.GENRES ->
                        listSettings.genreSort.genres(deviceLibrary.genres).map {
                            it.toMediaItem(context)
                        }
                    MediaSessionUID.Category.PLAYLISTS ->
                        userLibrary.playlists.map { it.toMediaItem(context) }
                }
            }
            is MediaSessionUID.Single -> {
                getChildMediaItems(mediaSessionUID.uid)
            }
            is MediaSessionUID.Joined -> {
                getChildMediaItems(mediaSessionUID.childUid)
            }
            null -> {
                return null
            }
        }
    }

    private fun getChildMediaItems(uid: Music.UID): List<MediaItem>? {
        return when (val item = musicRepository.find(uid)) {
            is Album -> {
                val songs = listSettings.albumSongSort.songs(item.songs)
                songs.map { it.toMediaItem(context, item).withHeader(R.string.lbl_songs) }
            }
            is Artist -> {
                val albums = ARTIST_ALBUMS_SORT.albums(item.explicitAlbums + item.implicitAlbums)
                val songs = listSettings.artistSongSort.songs(item.songs)
                albums.map { it.toMediaItem(context).withHeader(R.string.lbl_albums) } +
                    songs.map { it.toMediaItem(context, item).withHeader(R.string.lbl_songs) }
            }
            is Genre -> {
                val artists = GENRE_ARTISTS_SORT.artists(item.artists)
                val songs = listSettings.genreSongSort.songs(item.songs)
                artists.map { it.toMediaItem(context).withHeader(R.string.lbl_artists) } +
                    songs.map { it.toMediaItem(context, null).withHeader(R.string.lbl_songs) }
            }
            is Playlist -> {
                item.songs.map { it.toMediaItem(context, item).withHeader(R.string.lbl_songs) }
            }
            is Song,
            null -> return null
        }
    }

    private fun MediaItem.withHeader(@StringRes res: Int): MediaItem {
        val oldExtras = mediaMetadata.extras ?: Bundle()
        val newExtras =
            Bundle(oldExtras).apply {
                putString(
                    MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_GROUP_TITLE,
                    context.getString(res))
            }
        return buildUpon()
            .setMediaMetadata(mediaMetadata.buildUpon().setExtras(newExtras).build())
            .build()
    }

    private fun getCategorySize(
        category: MediaSessionUID.Category,
        musicRepository: MusicRepository
    ): Int {
        val deviceLibrary = musicRepository.deviceLibrary ?: return 0
        val userLibrary = musicRepository.userLibrary ?: return 0
        return when (category) {
            MediaSessionUID.Category.ROOT -> MediaSessionUID.Category.IMPORTANT.size
            MediaSessionUID.Category.SONGS -> deviceLibrary.songs.size
            MediaSessionUID.Category.ALBUMS -> deviceLibrary.albums.size
            MediaSessionUID.Category.ARTISTS -> deviceLibrary.artists.size
            MediaSessionUID.Category.GENRES -> deviceLibrary.genres.size
            MediaSessionUID.Category.PLAYLISTS -> userLibrary.playlists.size
        }
    }

    suspend fun prepareSearch(query: String, controller: ControllerInfo) {
        searchSubscribers[controller] = query
        val existing = searchResults[query]
        if (existing == null) {
            val new = searchTo(query)
            searchResults[query] = new
            new.await()
        } else {
            val items = existing.await()
            invalidator?.invalidate(controller, query, items.count())
        }
    }

    suspend fun getSearchResult(
        query: String,
        page: Int,
        pageSize: Int,
    ): List<MediaItem>? {
        val deferred = searchResults[query] ?: searchTo(query).also { searchResults[query] = it }
        return deferred.await().concat().paginate(page, pageSize)
    }

    private fun SearchEngine.Items.concat(): MutableList<MediaItem> {
        val music = mutableListOf<MediaItem>()
        if (songs != null) {
            music.addAll(songs.map { it.toMediaItem(context, null) })
        }
        if (albums != null) {
            music.addAll(albums.map { it.toMediaItem(context) })
        }
        if (artists != null) {
            music.addAll(artists.map { it.toMediaItem(context) })
        }
        if (genres != null) {
            music.addAll(genres.map { it.toMediaItem(context) })
        }
        if (playlists != null) {
            music.addAll(playlists.map { it.toMediaItem(context) })
        }
        return music
    }

    private fun SearchEngine.Items.count(): Int {
        var count = 0
        if (songs != null) {
            count += songs.size
        }
        if (albums != null) {
            count += albums.size
        }
        if (artists != null) {
            count += artists.size
        }
        if (genres != null) {
            count += genres.size
        }
        if (playlists != null) {
            count += playlists.size
        }
        return count
    }

    private fun searchTo(query: String) =
        searchScope.async {
            if (query.isEmpty()) {
                return@async SearchEngine.Items()
            }
            val deviceLibrary = musicRepository.deviceLibrary ?: return@async SearchEngine.Items()
            val userLibrary = musicRepository.userLibrary ?: return@async SearchEngine.Items()
            val items =
                SearchEngine.Items(
                    deviceLibrary.songs,
                    deviceLibrary.albums,
                    deviceLibrary.artists,
                    deviceLibrary.genres,
                    userLibrary.playlists)
            val results = searchEngine.search(items, query)
            for (entry in searchSubscribers.entries) {
                if (entry.value == query) {
                    invalidator?.invalidate(entry.key, query, results.count())
                }
            }
            results
        }

    private fun List<MediaItem>.paginate(page: Int, pageSize: Int): List<MediaItem>? {
        if (page == Int.MAX_VALUE) {
            // I think if someone requests this page it more or less implies that I should
            // return all of the pages.
            return this
        }
        val start = page * pageSize
        val end = min((page + 1) * pageSize, size) // Tolerate partial page queries
        if (pageSize == 0 || start !in indices) {
            // These pages are probably invalid. Hopefully this won't backfire.
            return null
        }
        return subList(start, end).toMutableList()
    }

    private companion object {
        // TODO: Rely on detail item gen logic?
        val ARTIST_ALBUMS_SORT = Sort(Sort.Mode.ByDate, Sort.Direction.DESCENDING)
        val GENRE_ARTISTS_SORT = Sort(Sort.Mode.ByName, Sort.Direction.ASCENDING)
    }
}
