/*
 * Copyright (c) 2024 Auxio Project
 * MusicBrowser.kt is part of Auxio.
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
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
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

class MusicBrowser
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val musicRepository: MusicRepository,
    private val searchEngine: SearchEngine,
    private val listSettings: ListSettings
) : MusicRepository.UpdateListener {
    interface Invalidator {
        fun invalidateMusic(ids: Set<String>)
    }

    private var invalidator: Invalidator? = null

    fun attach(invalidator: Invalidator) {
        this.invalidator = invalidator
        musicRepository.addUpdateListener(this)
    }

    fun release() {
        musicRepository.removeUpdateListener(this)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        val deviceLibrary = musicRepository.deviceLibrary
        val invalidate = mutableSetOf<String>()
        if (changes.deviceLibrary && deviceLibrary != null) {
            Category.DEVICE_MUSIC.forEach {
                invalidate.add(MediaSessionUID.CategoryItem(it).toString())
            }

            deviceLibrary.albums.forEach {
                val id = MediaSessionUID.SingleItem(it.uid).toString()
                invalidate.add(id)
            }

            deviceLibrary.artists.forEach {
                val id = MediaSessionUID.SingleItem(it.uid).toString()
                invalidate.add(id)
            }

            deviceLibrary.genres.forEach {
                val id = MediaSessionUID.SingleItem(it.uid).toString()
                invalidate.add(id)
            }
        }
        val userLibrary = musicRepository.userLibrary
        if (changes.userLibrary && userLibrary != null) {
            Category.USER_MUSIC.forEach {
                invalidate.add(MediaSessionUID.CategoryItem(it).toString())
            }
            userLibrary.playlists.forEach {
                val id = MediaSessionUID.SingleItem(it.uid).toString()
                invalidate.add(id)
            }
        }

        if (invalidate.isNotEmpty()) {
            invalidator?.invalidateMusic(invalidate)
        }
    }

    fun getItem(mediaId: String): MediaItem? {
        val music =
            when (val uid = MediaSessionUID.fromString(mediaId)) {
                is MediaSessionUID.CategoryItem -> return uid.category.toMediaItem(context)
                is MediaSessionUID.SingleItem ->
                    musicRepository.find(uid.uid)?.let { musicRepository.find(it.uid) }
                is MediaSessionUID.ChildItem ->
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

    fun getChildren(parentId: String): List<MediaItem>? {
        val deviceLibrary = musicRepository.deviceLibrary
        val userLibrary = musicRepository.userLibrary
        if (deviceLibrary == null || userLibrary == null) {
            return listOf()
        }

        return getMediaItemList(parentId, deviceLibrary, userLibrary)
    }

    suspend fun search(query: String): MutableList<MediaItem> {
        if (query.isEmpty()) {
            return mutableListOf()
        }
        val deviceLibrary = musicRepository.deviceLibrary ?: return mutableListOf()
        val userLibrary = musicRepository.userLibrary ?: return mutableListOf()
        val items =
            SearchEngine.Items(
                deviceLibrary.songs,
                deviceLibrary.albums,
                deviceLibrary.artists,
                deviceLibrary.genres,
                userLibrary.playlists)
        return searchEngine.search(items, query).toMediaItems()
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

    private fun getMediaItemList(
        id: String,
        deviceLibrary: DeviceLibrary,
        userLibrary: UserLibrary
    ): List<MediaItem>? {
        return when (val mediaSessionUID = MediaSessionUID.fromString(id)) {
            is MediaSessionUID.CategoryItem -> {
                when (mediaSessionUID.category) {
                    Category.ROOT -> Category.IMPORTANT.map { it.toMediaItem(context) }
                    Category.MORE -> TODO()
                    Category.SONGS ->
                        listSettings.songSort.songs(deviceLibrary.songs).map {
                            it.toMediaItem(context, null)
                        }
                    Category.ALBUMS ->
                        listSettings.albumSort.albums(deviceLibrary.albums).map {
                            it.toMediaItem(context)
                        }
                    Category.ARTISTS ->
                        listSettings.artistSort.artists(deviceLibrary.artists).map {
                            it.toMediaItem(context)
                        }
                    Category.GENRES ->
                        listSettings.genreSort.genres(deviceLibrary.genres).map {
                            it.toMediaItem(context)
                        }
                    Category.PLAYLISTS -> userLibrary.playlists.map { it.toMediaItem(context) }
                }
            }
            is MediaSessionUID.SingleItem -> {
                getChildMediaItems(mediaSessionUID.uid)
            }
            is MediaSessionUID.ChildItem -> {
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
                songs.map { it.toMediaItem(context, item, header(R.string.lbl_songs)) }
            }
            is Artist -> {
                val albums = ARTIST_ALBUMS_SORT.albums(item.explicitAlbums + item.implicitAlbums)
                val songs = listSettings.artistSongSort.songs(item.songs)
                albums.map { it.toMediaItem(context, null, header(R.string.lbl_songs)) } +
                    songs.map { it.toMediaItem(context, item, header(R.string.lbl_songs)) }
            }
            is Genre -> {
                val artists = GENRE_ARTISTS_SORT.artists(item.artists)
                val songs = listSettings.genreSongSort.songs(item.songs)
                artists.map { it.toMediaItem(context, header(R.string.lbl_songs)) } +
                    songs.map { it.toMediaItem(context, null, header(R.string.lbl_songs)) }
            }
            is Playlist -> {
                item.songs.map { it.toMediaItem(context, null, header(R.string.lbl_songs)) }
            }
            is Song,
            null -> return null
        }
    }

    private companion object {
        // TODO: Rely on detail item gen logic?
        val ARTIST_ALBUMS_SORT = Sort(Sort.Mode.ByDate, Sort.Direction.DESCENDING)
        val GENRE_ARTISTS_SORT = Sort(Sort.Mode.ByName, Sort.Direction.ASCENDING)
    }
}
