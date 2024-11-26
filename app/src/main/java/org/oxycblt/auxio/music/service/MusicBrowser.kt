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
import javax.inject.Inject
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.detail.DetailGenerator
import org.oxycblt.auxio.detail.DetailSection
import org.oxycblt.auxio.home.HomeGenerator
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.MusicType
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.info.resolveNumber
import org.oxycblt.auxio.search.SearchEngine

class MusicBrowser
private constructor(
    private val context: Context,
    private val invalidator: Invalidator,
    private val musicRepository: MusicRepository,
    private val searchEngine: SearchEngine,
    homeGeneratorFactory: HomeGenerator.Factory,
    detailGeneratorFactory: DetailGenerator.Factory
) : HomeGenerator.Invalidator, DetailGenerator.Invalidator {

    class Factory
    @Inject
    constructor(
        private val musicRepository: MusicRepository,
        private val searchEngine: SearchEngine,
        private val homeGeneratorFactory: HomeGenerator.Factory,
        private val detailGeneratorFactory: DetailGenerator.Factory
    ) {
        fun create(context: Context, invalidator: Invalidator): MusicBrowser =
            MusicBrowser(
                context,
                invalidator,
                musicRepository,
                searchEngine,
                homeGeneratorFactory,
                detailGeneratorFactory)
    }

    interface Invalidator {
        fun invalidateMusic(ids: Set<String>)
    }

    private val homeGenerator = homeGeneratorFactory.create(this)
    private val detailGenerator = detailGeneratorFactory.create(this)

    fun attach() {
        homeGenerator.attach()
        detailGenerator.attach()
    }

    fun release() {
        homeGenerator.release()
        detailGenerator.release()
    }

    override fun invalidateMusic(type: MusicType, instructions: UpdateInstructions) {
        val id = MediaSessionUID.Tab(TabNode.Home(type)).toString()
        invalidator.invalidateMusic(setOf(id))
    }

    override fun invalidateTabs() {
        val rootId = MediaSessionUID.Tab(TabNode.Root).toString()
        val moreId = MediaSessionUID.Tab(TabNode.More).toString()
        invalidator.invalidateMusic(setOf(rootId, moreId))
    }

    override fun invalidate(type: MusicType, replace: Int?) {
        val library = musicRepository.library ?: return
        val music =
            when (type) {
                MusicType.ALBUMS -> library.albums
                MusicType.ARTISTS -> library.artists
                MusicType.GENRES -> library.genres
                MusicType.PLAYLISTS -> library.playlists
                else -> return
            }
        if (music.isEmpty()) {
            return
        }
        val ids = music.map { MediaSessionUID.SingleItem(it.uid).toString() }.toSet()
        invalidator.invalidateMusic(ids)
    }

    fun getItem(mediaId: String): MediaItem? {
        val music =
            when (val uid = MediaSessionUID.fromString(mediaId)) {
                is MediaSessionUID.Tab -> return uid.node.toMediaItem(context)
                is MediaSessionUID.SingleItem ->
                    musicRepository.find(uid.uid)?.let { musicRepository.find(it.uid) }
                null -> null
            } ?: return null

        return when (music) {
            is Album -> music.toMediaItem(context)
            is Artist -> music.toMediaItem(context)
            is Genre -> music.toMediaItem(context)
            is Playlist -> music.toMediaItem(context)
            is Song -> music.toMediaItem(context)
        }
    }

    fun getChildren(parentId: String, maxTabs: Int): List<MediaItem>? {
        if (musicRepository.library == null) {
            return listOf()
        }
        return getMediaItemList(parentId, maxTabs)
    }

    suspend fun search(query: String): MutableList<MediaItem> {
        if (query.isEmpty()) {
            return mutableListOf()
        }
        val library = musicRepository.library ?: return mutableListOf()
        val items =
            SearchEngine.Items(
                library.songs, library.albums, library.artists, library.genres, library.playlists)
        return searchEngine.search(items, query).toMediaItems()
    }

    private fun SearchEngine.Items.toMediaItems(): MutableList<MediaItem> {
        val music = mutableListOf<MediaItem>()
        if (songs != null) {
            music.addAll(songs.map { it.toMediaItem(context, header(R.string.lbl_songs)) })
        }
        if (albums != null) {
            music.addAll(albums.map { it.toMediaItem(context, header(R.string.lbl_albums)) })
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

    private fun getMediaItemList(id: String, maxTabs: Int): List<MediaItem>? {
        return when (val mediaSessionUID = MediaSessionUID.fromString(id)) {
            is MediaSessionUID.Tab -> {
                getCategoryMediaItems(mediaSessionUID.node, maxTabs)
            }
            is MediaSessionUID.SingleItem -> {
                getChildMediaItems(mediaSessionUID.uid)
            }
            null -> {
                return null
            }
        }
    }

    private fun getCategoryMediaItems(node: TabNode, maxTabs: Int) =
        when (node) {
            is TabNode.Root -> {
                val tabs = homeGenerator.tabs()
                if (maxTabs < tabs.size) {
                    tabs.take(maxTabs - 1).map { TabNode.Home(it).toMediaItem(context) } +
                        TabNode.More.toMediaItem(context)
                } else {
                    tabs.map { TabNode.Home(it).toMediaItem(context) }
                }
            }
            is TabNode.More -> {
                homeGenerator.tabs().drop(maxTabs - 1).map { TabNode.Home(it).toMediaItem(context) }
            }
            is TabNode.Home ->
                when (node.type) {
                    MusicType.SONGS -> homeGenerator.songs().map { it.toMediaItem(context) }
                    MusicType.ALBUMS -> homeGenerator.albums().map { it.toMediaItem(context) }
                    MusicType.ARTISTS -> homeGenerator.artists().map { it.toMediaItem(context) }
                    MusicType.GENRES -> homeGenerator.genres().map { it.toMediaItem(context) }
                    MusicType.PLAYLISTS -> homeGenerator.playlists().map { it.toMediaItem(context) }
                }
        }

    private fun getChildMediaItems(uid: Music.UID): List<MediaItem>? {
        val detail = detailGenerator.any(uid) ?: return null
        return detail.sections.flatMap { section ->
            when (section) {
                is DetailSection.Songs ->
                    section.items.map {
                        it.toMediaItem(context, header(section.stringRes), child(detail.parent))
                    }
                is DetailSection.Albums ->
                    section.items.map { it.toMediaItem(context, header(section.stringRes)) }
                is DetailSection.Artists ->
                    section.items.map { it.toMediaItem(context, header(section.stringRes)) }
                is DetailSection.Discs ->
                    section.discs.flatMap { (disc, songs) ->
                        val discString = disc.resolveNumber(context)
                        songs.map { it.toMediaItem(context, header(discString)) }
                    }
                else -> error("Unknown section type: $section")
            }
        }
    }

    companion object {
        const val KEY_CHILD_OF = BuildConfig.APPLICATION_ID + ".key.CHILD_OF"
    }
}
