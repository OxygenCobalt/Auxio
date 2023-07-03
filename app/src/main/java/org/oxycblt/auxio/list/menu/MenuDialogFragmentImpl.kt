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
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogMenuBinding
import org.oxycblt.auxio.detail.DetailViewModel
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
    private val detailModel: DetailViewModel by activityViewModels()
    private val musicModel: MusicViewModel by activityViewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val args: SongMenuDialogFragmentArgs by navArgs()

    override val menuRes: Int
        get() = args.menuRes
    override val uid: Music.UID
        get() = args.songUid

    override fun updateMusic(binding: DialogMenuBinding, music: Song) {
        val context = requireContext()
        binding.menuCover.bind(music)
        binding.menuType.text = getString(R.string.lbl_song)
        binding.menuName.text = music.name.resolve(context)
        binding.menuInfo.text = music.artists.resolveNames(context)
    }

    override fun onClick(music: Song, item: MenuItem) {
        when (item.itemId) {
            R.id.action_play_next -> {
                playbackModel.playNext(music)
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_queue_add -> {
                playbackModel.addToQueue(music)
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_go_artist -> detailModel.showArtist(music)
            R.id.action_go_album -> detailModel.showAlbum(music)
            R.id.action_share -> requireContext().share(music)
            R.id.action_playlist_add -> musicModel.addToPlaylist(music)
            R.id.action_song_detail -> detailModel.showSong(music)
            else -> error("Unexpected menu item selected $item")
        }
    }
}

@AndroidEntryPoint
class AlbumMenuDialogFragment : MenuDialogFragment<Album>() {
    override val menuModel: MenuViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()
    private val musicModel: MusicViewModel by activityViewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val args: AlbumMenuDialogFragmentArgs by navArgs()

    override val menuRes: Int
        get() = args.menuRes
    override val uid: Music.UID
        get() = args.albumUid

    override fun updateMusic(binding: DialogMenuBinding, music: Album) {
        val context = requireContext()
        binding.menuCover.bind(music)
        binding.menuType.text = getString(music.releaseType.stringRes)
        binding.menuName.text = music.name.resolve(context)
        binding.menuInfo.text = music.artists.resolveNames(context)
    }

    override fun onClick(music: Album, item: MenuItem) {
        when (item.itemId) {
            R.id.action_play -> playbackModel.play(music)
            R.id.action_shuffle -> playbackModel.shuffle(music)
            R.id.action_play_next -> {
                playbackModel.playNext(music)
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_queue_add -> {
                playbackModel.addToQueue(music)
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_go_artist -> detailModel.showArtist(music)
            R.id.action_playlist_add -> musicModel.addToPlaylist(music)
            R.id.action_share -> requireContext().share(music)
            else -> error("Unexpected menu item selected $item")
        }
    }
}

@AndroidEntryPoint
class ArtistMenuDialogFragment : MenuDialogFragment<Artist>() {
    override val menuModel: MenuViewModel by activityViewModels()
    private val musicModel: MusicViewModel by activityViewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val args: ArtistMenuDialogFragmentArgs by navArgs()

    override val menuRes: Int
        get() = args.menuRes
    override val uid: Music.UID
        get() = args.artistUid

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

    override fun onClick(music: Artist, item: MenuItem) {
        when (item.itemId) {
            R.id.action_play -> playbackModel.play(music)
            R.id.action_shuffle -> playbackModel.shuffle(music)
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
    override val menuModel: MenuViewModel by activityViewModels()
    private val musicModel: MusicViewModel by activityViewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val args: GenreMenuDialogFragmentArgs by navArgs()

    override val menuRes: Int
        get() = args.menuRes
    override val uid: Music.UID
        get() = args.genreUid

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

    override fun onClick(music: Genre, item: MenuItem) {
        when (item.itemId) {
            R.id.action_play -> playbackModel.play(music)
            R.id.action_shuffle -> playbackModel.shuffle(music)
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
    override val menuModel: MenuViewModel by activityViewModels()
    private val musicModel: MusicViewModel by activityViewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val args: PlaylistMenuDialogFragmentArgs by navArgs()

    override val menuRes: Int
        get() = args.menuRes
    override val uid: Music.UID
        get() = args.playlistUid

    override fun updateMusic(binding: DialogMenuBinding, music: Playlist) {
        val context = requireContext()
        binding.menuCover.bind(music)
        binding.menuType.text = getString(R.string.lbl_playlist)
        binding.menuName.text = music.name.resolve(context)
        binding.menuInfo.text = context.getPlural(R.plurals.fmt_song_count, music.songs.size)
    }

    override fun onClick(music: Playlist, item: MenuItem) {
        when (item.itemId) {
            R.id.action_play -> playbackModel.play(music)
            R.id.action_shuffle -> playbackModel.shuffle(music)
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
