/*
 * Copyright (c) 2024 Auxio Project
 * DetailGenerator.kt is part of Auxio.
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
 
package org.oxycblt.auxio.detail

import androidx.annotation.StringRes
import javax.inject.Inject
import org.oxycblt.auxio.R
import org.oxycblt.auxio.list.ListSettings
import org.oxycblt.auxio.list.sort.Sort
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.MusicType
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.musikr.tag.Disc
import org.oxycblt.auxio.musikr.tag.ReleaseType
import timber.log.Timber as L

interface DetailGenerator {
    fun any(uid: Music.UID): Detail<out MusicParent>?

    fun album(uid: Music.UID): Detail<Album>?

    fun artist(uid: Music.UID): Detail<Artist>?

    fun genre(uid: Music.UID): Detail<Genre>?

    fun playlist(uid: Music.UID): Detail<Playlist>?

    fun attach()

    fun release()

    interface Factory {
        fun create(invalidator: Invalidator): DetailGenerator
    }

    interface Invalidator {
        fun invalidate(type: MusicType, replace: Int?)
    }
}

class DetailGeneratorFactoryImpl
@Inject
constructor(private val listSettings: ListSettings, private val musicRepository: MusicRepository) :
    DetailGenerator.Factory {
    override fun create(invalidator: DetailGenerator.Invalidator): DetailGenerator =
        DetailGeneratorImpl(invalidator, listSettings, musicRepository)
}

private class DetailGeneratorImpl(
    private val invalidator: DetailGenerator.Invalidator,
    private val listSettings: ListSettings,
    private val musicRepository: MusicRepository
) : DetailGenerator, MusicRepository.UpdateListener, ListSettings.Listener {
    override fun attach() {
        listSettings.registerListener(this)
        musicRepository.addUpdateListener(this)
    }

    override fun onAlbumSongSortChanged() {
        super.onAlbumSongSortChanged()
        invalidator.invalidate(MusicType.ALBUMS, -1)
    }

    override fun onArtistSongSortChanged() {
        super.onArtistSongSortChanged()
        invalidator.invalidate(MusicType.ARTISTS, -1)
    }

    override fun onGenreSongSortChanged() {
        super.onGenreSongSortChanged()
        invalidator.invalidate(MusicType.GENRES, -1)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        if (changes.deviceLibrary) {
            invalidator.invalidate(MusicType.ALBUMS, null)
            invalidator.invalidate(MusicType.ARTISTS, null)
            invalidator.invalidate(MusicType.GENRES, null)
        }
        if (changes.userLibrary) {
            invalidator.invalidate(MusicType.PLAYLISTS, null)
        }
    }

    override fun release() {
        listSettings.unregisterListener(this)
        musicRepository.removeUpdateListener(this)
    }

    override fun any(uid: Music.UID): Detail<out MusicParent>? {
        val music = musicRepository.find(uid) ?: return null
        return when (music) {
            is Album -> album(uid)
            is Artist -> artist(uid)
            is Genre -> genre(uid)
            is Playlist -> playlist(uid)
            else -> null
        }
    }

    override fun album(uid: Music.UID): Detail<Album>? {
        val album = musicRepository.library?.findAlbum(uid) ?: return null
        val songs = listSettings.albumSongSort.songs(album.songs)
        val discs = songs.groupBy { it.disc }
        val section =
            if (discs.size > 1) {
                DetailSection.Discs(discs)
            } else {
                DetailSection.Songs(songs)
            }
        return Detail(album, listOf(section))
    }

    override fun artist(uid: Music.UID): Detail<Artist>? {
        val artist = musicRepository.library?.findArtist(uid) ?: return null
        val grouping =
            artist.explicitAlbums.groupByTo(sortedMapOf()) {
                // Remap the complicated ReleaseType data structure into detail sections
                when (it.releaseType.refinement) {
                    ReleaseType.Refinement.LIVE -> DetailSection.Albums.Category.LIVE
                    ReleaseType.Refinement.REMIX -> DetailSection.Albums.Category.REMIXES
                    null ->
                        when (it.releaseType) {
                            is ReleaseType.Album -> DetailSection.Albums.Category.ALBUMS
                            is ReleaseType.EP -> DetailSection.Albums.Category.EPS
                            is ReleaseType.Single -> DetailSection.Albums.Category.SINGLES
                            is ReleaseType.Compilation -> DetailSection.Albums.Category.COMPILATIONS
                            is ReleaseType.Soundtrack -> DetailSection.Albums.Category.SOUNDTRACKS
                            is ReleaseType.Mix -> DetailSection.Albums.Category.DJ_MIXES
                            is ReleaseType.Mixtape -> DetailSection.Albums.Category.MIXTAPES
                            is ReleaseType.Demo -> DetailSection.Albums.Category.DEMOS
                        }
                }
            }

        if (artist.implicitAlbums.isNotEmpty()) {
            L.d("Implicit albums present, adding to list")
            grouping[DetailSection.Albums.Category.APPEARANCES] =
                artist.implicitAlbums.toMutableList()
        }

        val sections =
            grouping.mapTo(mutableListOf<DetailSection>()) { (category, albums) ->
                DetailSection.Albums(category, ARTIST_ALBUM_SORT.albums(albums))
            }
        if (artist.songs.isNotEmpty()) {
            val songs = DetailSection.Songs(listSettings.artistSongSort.songs(artist.songs))
            sections.add(songs)
        }
        return Detail(artist, sections)
    }

    override fun genre(uid: Music.UID): Detail<Genre>? {
        val genre = musicRepository.library?.findGenre(uid) ?: return null
        val artists = DetailSection.Artists(GENRE_ARTIST_SORT.artists(genre.artists))
        val songs = DetailSection.Songs(listSettings.genreSongSort.songs(genre.songs))
        return Detail(genre, listOf(artists, songs))
    }

    override fun playlist(uid: Music.UID): Detail<Playlist>? {
        val playlist = musicRepository.library?.findPlaylist(uid) ?: return null
        if (playlist.songs.isNotEmpty()) {
            val songs = DetailSection.Songs(playlist.songs)
            return Detail(playlist, listOf(songs))
        }
        return Detail(playlist, listOf())
    }

    private companion object {
        val ARTIST_ALBUM_SORT = Sort(Sort.Mode.ByDate, Sort.Direction.DESCENDING)
        val GENRE_ARTIST_SORT = Sort(Sort.Mode.ByName, Sort.Direction.ASCENDING)
    }
}

data class Detail<P : MusicParent>(val parent: P, val sections: List<DetailSection>)

sealed interface DetailSection {
    val order: Int
    val stringRes: Int

    abstract class PlainSection<T : Music> : DetailSection {
        abstract val items: List<T>
    }

    data class Artists(override val items: List<Artist>) : PlainSection<Artist>() {
        override val order = 0
        override val stringRes = R.string.lbl_artists
    }

    data class Albums(val category: Category, override val items: List<Album>) :
        PlainSection<Album>() {
        override val order = 1 + category.ordinal
        override val stringRes = category.stringRes

        enum class Category(@StringRes val stringRes: Int) {
            ALBUMS(R.string.lbl_albums),
            EPS(R.string.lbl_eps),
            SINGLES(R.string.lbl_singles),
            COMPILATIONS(R.string.lbl_compilations),
            SOUNDTRACKS(R.string.lbl_soundtracks),
            DJ_MIXES(R.string.lbl_mixes),
            MIXTAPES(R.string.lbl_mixtapes),
            DEMOS(R.string.lbl_demos),
            APPEARANCES(R.string.lbl_appears_on),
            LIVE(R.string.lbl_live_group),
            REMIXES(R.string.lbl_remix_group)
        }
    }

    data class Songs(override val items: List<Song>) : PlainSection<Song>() {
        override val order = 12
        override val stringRes = R.string.lbl_songs
    }

    data class Discs(val discs: Map<Disc?, List<Song>>) : DetailSection {
        override val order = 13
        override val stringRes = R.string.lbl_songs
    }
}
