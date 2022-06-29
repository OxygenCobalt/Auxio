/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.ui.fragment

import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.MainNavigationAction
import org.oxycblt.auxio.ui.NavigationViewModel
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logEOrThrow
import org.oxycblt.auxio.util.showToast

/**
 * A fragment capable of creating menus. Automatically keeps track of and disposes of menus,
 * preventing UI issues and memory leaks.
 * @author OxygenCobalt
 */
abstract class MenuFragment<T : ViewBinding> : ViewBindingFragment<T>() {
    private var currentMenu: PopupMenu? = null

    protected val playbackModel: PlaybackViewModel by androidActivityViewModels()
    protected val navModel: NavigationViewModel by activityViewModels()

    /**
     * Opens the given menu in context of [song]. Assumes that the menu is only composed of common
     * [Song] options.
     */
    protected fun musicMenu(anchor: View, @MenuRes menuRes: Int, song: Song) {
        logD("Launching new song menu: ${song.rawName}")

        musicMenuImpl(anchor, menuRes) { id ->
            when (id) {
                R.id.action_play_next -> {
                    playbackModel.playNext(song)
                    requireContext().showToast(R.string.lbl_queue_added)
                }
                R.id.action_queue_add -> {
                    playbackModel.addToQueue(song)
                    requireContext().showToast(R.string.lbl_queue_added)
                }
                R.id.action_go_artist -> {
                    navModel.exploreNavigateTo(song.album.artist)
                }
                R.id.action_go_album -> {
                    navModel.exploreNavigateTo(song.album)
                }
                R.id.action_song_detail -> {
                    navModel.mainNavigateTo(MainNavigationAction.SongDetails(song))
                }
                else -> {
                    logEOrThrow("Unexpected menu item selected")
                    return@musicMenuImpl false
                }
            }

            true
        }
    }

    /**
     * Opens the given menu in context of [album]. Assumes that the menu is only composed of common
     * [Album] options.
     */
    protected fun musicMenu(anchor: View, @MenuRes menuRes: Int, album: Album) {
        logD("Launching new album menu: ${album.rawName}")

        musicMenuImpl(anchor, menuRes) { id ->
            when (id) {
                R.id.action_play -> {
                    playbackModel.play(album, false)
                }
                R.id.action_shuffle -> {
                    playbackModel.play(album, true)
                }
                R.id.action_play_next -> {
                    playbackModel.playNext(album)
                    requireContext().showToast(R.string.lbl_queue_added)
                }
                R.id.action_queue_add -> {
                    playbackModel.addToQueue(album)
                    requireContext().showToast(R.string.lbl_queue_added)
                }
                R.id.action_go_artist -> {
                    navModel.exploreNavigateTo(album.artist)
                }
                else -> {
                    logEOrThrow("Unexpected menu item selected")
                    return@musicMenuImpl false
                }
            }

            true
        }
    }

    /**
     * Opens the given menu in context of [artist]. Assumes that the menu is only composed of common
     * [Artist] options.
     */
    protected fun musicMenu(anchor: View, @MenuRes menuRes: Int, artist: Artist) {
        logD("Launching new artist menu: ${artist.rawName}")

        musicMenuImpl(anchor, menuRes) { id ->
            when (id) {
                R.id.action_play -> {
                    playbackModel.play(artist, false)
                }
                R.id.action_shuffle -> {
                    playbackModel.play(artist, true)
                }
                R.id.action_play_next -> {
                    playbackModel.playNext(artist)
                    requireContext().showToast(R.string.lbl_queue_added)
                }
                R.id.action_queue_add -> {
                    playbackModel.addToQueue(artist)
                    requireContext().showToast(R.string.lbl_queue_added)
                }
                else -> {
                    logEOrThrow("Unexpected menu item selected")
                    return@musicMenuImpl false
                }
            }

            true
        }
    }

    /**
     * Opens the given menu in context of [genre]. Assumes that the menu is only composed of common
     * [Genre] options.
     */
    protected fun musicMenu(anchor: View, @MenuRes menuRes: Int, genre: Genre) {
        logD("Launching new genre menu: ${genre.rawName}")

        musicMenuImpl(anchor, menuRes) { id ->
            when (id) {
                R.id.action_play -> {
                    playbackModel.play(genre, false)
                }
                R.id.action_shuffle -> {
                    playbackModel.play(genre, true)
                }
                R.id.action_play_next -> {
                    playbackModel.playNext(genre)
                    requireContext().showToast(R.string.lbl_queue_added)
                }
                R.id.action_queue_add -> {
                    playbackModel.addToQueue(genre)
                    requireContext().showToast(R.string.lbl_queue_added)
                }
                else -> {
                    logEOrThrow("Unexpected menu item selected")
                    return@musicMenuImpl false
                }
            }

            true
        }
    }

    private fun musicMenuImpl(anchor: View, @MenuRes menuRes: Int, onSelect: (Int) -> Boolean) {
        menu(anchor, menuRes) {
            for (item in menu.children) {
                if (item.itemId == R.id.action_play_next || item.itemId == R.id.action_queue_add) {
                    item.isEnabled = playbackModel.song.value != null
                }
            }

            setOnMenuItemClickListener { item -> onSelect(item.itemId) }
        }
    }

    /**
     * Open a generic menu with configuration in [block]. If a menu is already opened, then this
     * function is a no-op.
     */
    protected fun menu(anchor: View, @MenuRes menuRes: Int, block: PopupMenu.() -> Unit) {
        if (currentMenu != null) {
            logD("Menu already present, not launching")
            return
        }

        currentMenu =
            PopupMenu(requireContext(), anchor).apply {
                inflate(menuRes)
                block()
                setOnDismissListener { currentMenu = null }
                show()
            }
    }

    override fun onDestroyBinding(binding: T) {
        super.onDestroyBinding(binding)
        currentMenu?.dismiss()
        currentMenu = null
    }
}
