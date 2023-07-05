/*
 * Copyright (c) 2023 Auxio Project
 * MenuDialogFragmentImpl.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list.menu

import android.view.MenuItem
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogMenuBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.list.ListViewModel
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.share
import org.oxycblt.auxio.util.showToast

@AndroidEntryPoint
class SongMenuDialogFragment : MenuDialogFragment<Song>() {
    override val menuModel: MenuViewModel by activityViewModels()
    override val listModel: ListViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()
    private val musicModel: MusicViewModel by activityViewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val args: SongMenuDialogFragmentArgs by navArgs()

    override val menuRes: Int
        get() = args.menuRes
    override val uid: Music.UID
        get() = args.songUid

    override fun getDisabledItemIds(music: Song) = setOf<Int>()

    override fun updateMusic(binding: DialogMenuBinding, music: Song) {
        val context = requireContext()
        binding.menuCover.bind(music)
        binding.menuType.text = getString(R.string.lbl_song)
        binding.menuName.text = music.name.resolve(context)
        binding.menuInfo.text = music.artists.resolveNames(context)
    }

    override fun onClick(item: MenuItem, music: Song) {
        when (item.itemId) {
            R.id.action_play_next -> {
                playbackModel.playNext(music)
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_queue_add -> {
                playbackModel.addToQueue(music)
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_artist_details -> detailModel.showArtist(music)
            R.id.action_album_details -> detailModel.showAlbum(music)
            R.id.action_share -> requireContext().share(music)
            R.id.action_playlist_add -> musicModel.addToPlaylist(music)
            R.id.action_detail -> detailModel.showSong(music)
            else -> error("Unexpected menu item selected $item")
        }
    }
}

@AndroidEntryPoint
class AlbumMenuDialogFragment : MenuDialogFragment<Album>() {
    override val menuModel: MenuViewModel by viewModels()
    override val listModel: ListViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()
    private val musicModel: MusicViewModel by activityViewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val args: AlbumMenuDialogFragmentArgs by navArgs()

    override val menuRes: Int
        get() = args.menuRes
    override val uid: Music.UID
        get() = args.albumUid

    override fun getDisabledItemIds(music: Album) = setOf<Int>()

    override fun updateMusic(binding: DialogMenuBinding, music: Album) {
        val context = requireContext()
        binding.menuCover.bind(music)
        binding.menuType.text = getString(music.releaseType.stringRes)
        binding.menuName.text = music.name.resolve(context)
        binding.menuInfo.text = music.artists.resolveNames(context)
    }

    override fun onClick(item: MenuItem, music: Album) {
        when (item.itemId) {
            R.id.action_play -> playbackModel.play(music)
            R.id.action_shuffle -> playbackModel.shuffle(music)
            R.id.action_detail -> detailModel.showAlbum(music)
            R.id.action_play_next -> {
                playbackModel.playNext(music)
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_queue_add -> {
                playbackModel.addToQueue(music)
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_artist_details -> detailModel.showArtist(music)
            R.id.action_playlist_add -> musicModel.addToPlaylist(music)
            R.id.action_share -> requireContext().share(music)
            else -> error("Unexpected menu item selected $item")
        }
    }
}

@AndroidEntryPoint
class ArtistMenuDialogFragment : MenuDialogFragment<Artist>() {
    override val menuModel: MenuViewModel by viewModels()
    override val listModel: ListViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()
    private val musicModel: MusicViewModel by activityViewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val args: ArtistMenuDialogFragmentArgs by navArgs()

    override val menuRes: Int
        get() = args.menuRes
    override val uid: Music.UID
        get() = args.artistUid

    override fun getDisabledItemIds(music: Artist) =
        if (music.songs.isEmpty()) {
            setOf(
                R.id.action_play,
                R.id.action_shuffle,
                R.id.action_play_next,
                R.id.action_queue_add,
                R.id.action_playlist_add,
                R.id.action_share)
        } else {
            setOf()
        }

    override fun updateMusic(binding: DialogMenuBinding, music: Artist) {
        val context = requireContext()
        binding.menuCover.bind(music)
        binding.menuType.text = getString(R.string.lbl_artist)
        binding.menuName.text = music.name.resolve(context)
        binding.menuInfo.text =
            getString(
                R.string.fmt_two,
                context.getPlural(R.plurals.fmt_album_count, music.albums.size),
                if (music.songs.isNotEmpty()) {
                    context.getPlural(R.plurals.fmt_song_count, music.songs.size)
                } else {
                    getString(R.string.def_song_count)
                })
    }

    override fun onClick(item: MenuItem, music: Artist) {
        when (item.itemId) {
            R.id.action_play -> playbackModel.play(music)
            R.id.action_shuffle -> playbackModel.shuffle(music)
            R.id.action_detail -> detailModel.showArtist(music)
            R.id.action_play_next -> {
                playbackModel.playNext(music)
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_queue_add -> {
                playbackModel.addToQueue(music)
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_playlist_add -> musicModel.addToPlaylist(music)
            R.id.action_share -> requireContext().share(music)
            else -> error("Unexpected menu item $item")
        }
    }
}

@AndroidEntryPoint
class GenreMenuDialogFragment : MenuDialogFragment<Genre>() {
    override val menuModel: MenuViewModel by viewModels()
    override val listModel: ListViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()
    private val musicModel: MusicViewModel by activityViewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val args: GenreMenuDialogFragmentArgs by navArgs()

    override val menuRes: Int
        get() = args.menuRes
    override val uid: Music.UID
        get() = args.genreUid

    override fun getDisabledItemIds(music: Genre) = setOf<Int>()

    override fun updateMusic(binding: DialogMenuBinding, music: Genre) {
        val context = requireContext()
        binding.menuCover.bind(music)
        binding.menuType.text = getString(R.string.lbl_genre)
        binding.menuName.text = music.name.resolve(context)
        binding.menuInfo.text =
            getString(
                R.string.fmt_two,
                context.getPlural(R.plurals.fmt_artist_count, music.artists.size),
                context.getPlural(R.plurals.fmt_song_count, music.songs.size))
    }

    override fun onClick(item: MenuItem, music: Genre) {
        when (item.itemId) {
            R.id.action_play -> playbackModel.play(music)
            R.id.action_shuffle -> playbackModel.shuffle(music)
            R.id.action_detail -> detailModel.showGenre(music)
            R.id.action_play_next -> {
                playbackModel.playNext(music)
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_queue_add -> {
                playbackModel.addToQueue(music)
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_playlist_add -> musicModel.addToPlaylist(music)
            R.id.action_share -> requireContext().share(music)
            else -> error("Unexpected menu item $item")
        }
    }
}

@AndroidEntryPoint
class PlaylistMenuDialogFragment : MenuDialogFragment<Playlist>() {
    override val menuModel: MenuViewModel by viewModels()
    override val listModel: ListViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()
    private val musicModel: MusicViewModel by activityViewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val args: PlaylistMenuDialogFragmentArgs by navArgs()

    override val menuRes: Int
        get() = args.menuRes
    override val uid: Music.UID
        get() = args.playlistUid

    override fun getDisabledItemIds(music: Playlist) =
        if (music.songs.isEmpty()) {
            setOf(
                R.id.action_play,
                R.id.action_shuffle,
                R.id.action_play_next,
                R.id.action_queue_add,
                R.id.action_playlist_add,
                R.id.action_share)
        } else {
            setOf()
        }

    override fun updateMusic(binding: DialogMenuBinding, music: Playlist) {
        val context = requireContext()
        binding.menuCover.bind(music)
        binding.menuType.text = getString(R.string.lbl_playlist)
        binding.menuName.text = music.name.resolve(context)
        binding.menuInfo.text =
            if (music.songs.isNotEmpty()) {
                context.getPlural(R.plurals.fmt_song_count, music.songs.size)
            } else {
                getString(R.string.def_song_count)
            }
    }

    override fun onClick(item: MenuItem, music: Playlist) {
        when (item.itemId) {
            R.id.action_play -> playbackModel.play(music)
            R.id.action_shuffle -> playbackModel.shuffle(music)
            R.id.action_detail -> detailModel.showPlaylist(music)
            R.id.action_play_next -> {
                playbackModel.playNext(music)
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_queue_add -> {
                playbackModel.addToQueue(music)
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_rename -> musicModel.renamePlaylist(music)
            R.id.action_delete -> musicModel.deletePlaylist(music)
            R.id.action_share -> requireContext().share(music)
            else -> error("Unexpected menu item $item")
        }
    }
}
