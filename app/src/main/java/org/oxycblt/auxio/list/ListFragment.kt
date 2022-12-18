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
 
package org.oxycblt.auxio.list

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import org.oxycblt.auxio.MainFragmentDirections
import org.oxycblt.auxio.R
import org.oxycblt.auxio.list.selection.SelectionToolbarOverlay
import org.oxycblt.auxio.list.selection.SelectionViewModel
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.shared.MainNavigationAction
import org.oxycblt.auxio.shared.NavigationViewModel
import org.oxycblt.auxio.shared.ViewBindingFragment
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.showToast

abstract class ListFragment<VB : ViewBinding> : ViewBindingFragment<VB>() {
    protected val selectionModel: SelectionViewModel by activityViewModels()
    private var currentMenu: PopupMenu? = null

    protected val playbackModel: PlaybackViewModel by androidActivityViewModels()
    protected val navModel: NavigationViewModel by activityViewModels()

    override fun onDestroyBinding(binding: VB) {
        super.onDestroyBinding(binding)
        currentMenu?.dismiss()
        currentMenu = null
    }

    fun setupSelectionToolbar(toolbar: SelectionToolbarOverlay) {
        toolbar.apply {
            setOnSelectionCancelListener { selectionModel.consume() }
            setOnMenuItemClickListener {
                handleSelectionMenuItem(it)
                true
            }
        }
    }

    /** Handle a media item with a selection. */
    private fun handleSelectionMenuItem(item: MenuItem) {
        when (item.itemId) {
            R.id.action_play_next -> {
                playbackModel.playNext(selectionModel.consume())
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_queue_add -> {
                playbackModel.addToQueue(selectionModel.consume())
                requireContext().showToast(R.string.lng_queue_added)
            }
        }
    }

    /**
     * Called when an item is clicked by the user and was not selected by [handleClick]. This can be
     * optionally implemented if [handleClick] is used.
     */
    open fun onRealClick(music: Music) {
        throw NotImplementedError()
    }

    /** Provided implementation of an item click callback that handles selection. */
    protected fun handleClick(item: Item) {
        check(item is Music) { "Unexpected datatype: ${item::class.simpleName}" }
        if (selectionModel.selected.value.isNotEmpty()) {
            selectionModel.select(item)
        } else {
            onRealClick(item)
        }
    }

    /** Provided implementation of an item selection callback. */
    protected fun handleSelect(item: Item) {
        check(item is Music) { "Unexpected datatype: ${item::class.simpleName}" }
        selectionModel.select(item)
    }

    /**
     * Opens the given menu in context of [song]. Assumes that the menu is only composed of common
     * [Song] options.
     */
    protected fun openMusicMenu(anchor: View, @MenuRes menuRes: Int, song: Song) {
        logD("Launching new song menu: ${song.rawName}")

        openMusicMenuImpl(anchor, menuRes) {
            when (it.itemId) {
                R.id.action_play_next -> {
                    playbackModel.playNext(song)
                    requireContext().showToast(R.string.lng_queue_added)
                }
                R.id.action_queue_add -> {
                    playbackModel.addToQueue(song)
                    requireContext().showToast(R.string.lng_queue_added)
                }
                R.id.action_go_artist -> {
                    navModel.exploreNavigateTo(song.artists)
                }
                R.id.action_go_album -> {
                    navModel.exploreNavigateTo(song.album)
                }
                R.id.action_song_detail -> {
                    navModel.mainNavigateTo(
                        MainNavigationAction.Directions(
                            MainFragmentDirections.actionShowDetails(song.uid)))
                }
                else -> {
                    error("Unexpected menu item selected")
                }
            }
        }
    }

    /**
     * Opens the given menu in context of [album]. Assumes that the menu is only composed of common
     * [Album] options.
     */
    protected fun openMusicMenu(anchor: View, @MenuRes menuRes: Int, album: Album) {
        logD("Launching new album menu: ${album.rawName}")

        openMusicMenuImpl(anchor, menuRes) {
            when (it.itemId) {
                R.id.action_play -> {
                    playbackModel.play(album)
                }
                R.id.action_shuffle -> {
                    playbackModel.shuffle(album)
                }
                R.id.action_play_next -> {
                    playbackModel.playNext(album)
                    requireContext().showToast(R.string.lng_queue_added)
                }
                R.id.action_queue_add -> {
                    playbackModel.addToQueue(album)
                    requireContext().showToast(R.string.lng_queue_added)
                }
                R.id.action_go_artist -> {
                    navModel.exploreNavigateTo(album.artists)
                }
                else -> {
                    error("Unexpected menu item selected")
                }
            }
        }
    }

    /**
     * Opens the given menu in context of [artist]. Assumes that the menu is only composed of common
     * [Artist] options.
     */
    protected fun openMusicMenu(anchor: View, @MenuRes menuRes: Int, artist: Artist) {
        logD("Launching new artist menu: ${artist.rawName}")

        openMusicMenuImpl(anchor, menuRes) {
            when (it.itemId) {
                R.id.action_play -> {
                    playbackModel.play(artist)
                }
                R.id.action_shuffle -> {
                    playbackModel.shuffle(artist)
                }
                R.id.action_play_next -> {
                    playbackModel.playNext(artist)
                    requireContext().showToast(R.string.lng_queue_added)
                }
                R.id.action_queue_add -> {
                    playbackModel.addToQueue(artist)
                    requireContext().showToast(R.string.lng_queue_added)
                }
                else -> {
                    error("Unexpected menu item selected")
                }
            }
        }
    }

    /**
     * Opens the given menu in context of [genre]. Assumes that the menu is only composed of common
     * [Genre] options.
     */
    protected fun openMusicMenu(anchor: View, @MenuRes menuRes: Int, genre: Genre) {
        logD("Launching new genre menu: ${genre.rawName}")

        openMusicMenuImpl(anchor, menuRes) {
            when (it.itemId) {
                R.id.action_play -> {
                    playbackModel.play(genre)
                }
                R.id.action_shuffle -> {
                    playbackModel.shuffle(genre)
                }
                R.id.action_play_next -> {
                    playbackModel.playNext(genre)
                    requireContext().showToast(R.string.lng_queue_added)
                }
                R.id.action_queue_add -> {
                    playbackModel.addToQueue(genre)
                    requireContext().showToast(R.string.lng_queue_added)
                }
                else -> {
                    error("Unexpected menu item selected")
                }
            }
        }
    }

    private fun openMusicMenuImpl(
        anchor: View,
        @MenuRes menuRes: Int,
        onClick: (MenuItem) -> Unit
    ) {
        openMenu(anchor, menuRes) {
            setOnMenuItemClickListener { item ->
                onClick(item)
                true
            }
        }
    }

    /**
     * Open a generic menu with configuration in [block]. If a menu is already opened, then this
     * function is a no-op.
     */
    protected fun openMenu(anchor: View, @MenuRes menuRes: Int, block: PopupMenu.() -> Unit) {
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
}
