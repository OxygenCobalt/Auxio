/*
 * Copyright (c) 2022 Auxio Project
 * ListFragment.kt is part of Auxio.
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

import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.MenuCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import org.oxycblt.auxio.MainFragmentDirections
import org.oxycblt.auxio.R
import org.oxycblt.auxio.list.selection.SelectionFragment
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.navigation.MainNavigationAction
import org.oxycblt.auxio.navigation.NavigationViewModel
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.showToast

/**
 * A Fragment containing a selectable list.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
abstract class ListFragment<in T : Music, VB : ViewBinding> :
    SelectionFragment<VB>(), SelectableListListener<T> {
    protected abstract val navModel: NavigationViewModel
    private var currentMenu: PopupMenu? = null

    override fun onDestroyBinding(binding: VB) {
        super.onDestroyBinding(binding)
        currentMenu?.dismiss()
        currentMenu = null
    }

    /**
     * Called when [onClick] is called, but does not result in the item being selected. This more or
     * less corresponds to an [onClick] implementation in a non-[ListFragment].
     *
     * @param item The [T] data of the item that was clicked.
     */
    abstract fun onRealClick(item: T)

    final override fun onClick(item: T, viewHolder: RecyclerView.ViewHolder) {
        if (selectionModel.selected.value.isNotEmpty()) {
            // Map clicking an item to selecting an item when items are already selected.
            selectionModel.select(item)
        } else {
            // Delegate to the concrete implementation when we don't select the item.
            onRealClick(item)
        }
    }

    final override fun onSelect(item: T) {
        selectionModel.select(item)
    }

    /**
     * Opens a menu in the context of a [Song]. This menu will be managed by the Fragment and closed
     * when the view is destroyed. If a menu is already opened, this call is ignored.
     *
     * @param anchor The [View] to anchor the menu to.
     * @param menuRes The resource of the menu to load.
     * @param song The [Song] to create the menu for.
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
                    navModel.exploreNavigateToParentArtist(song)
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
     * Opens a menu in the context of a [Album]. This menu will be managed by the Fragment and
     * closed when the view is destroyed. If a menu is already opened, this call is ignored.
     *
     * @param anchor The [View] to anchor the menu to.
     * @param menuRes The resource of the menu to load.
     * @param album The [Album] to create the menu for.
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
                    navModel.exploreNavigateToParentArtist(album)
                }
                else -> {
                    error("Unexpected menu item selected")
                }
            }
        }
    }

    /**
     * Opens a menu in the context of a [Artist]. This menu will be managed by the Fragment and
     * closed when the view is destroyed. If a menu is already opened, this call is ignored.
     *
     * @param anchor The [View] to anchor the menu to.
     * @param menuRes The resource of the menu to load.
     * @param artist The [Artist] to create the menu for.
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
     * Opens a menu in the context of a [Genre]. This menu will be managed by the Fragment and
     * closed when the view is destroyed. If a menu is already opened, this call is ignored.
     *
     * @param anchor The [View] to anchor the menu to.
     * @param menuRes The resource of the menu to load.
     * @param genre The [Genre] to create the menu for.
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

    /**
     * Opens a menu in the context of a [Playlist]. This menu will be managed by the Fragment and
     * closed when the view is destroyed. If a menu is already opened, this call is ignored.
     *
     * @param anchor The [View] to anchor the menu to.
     * @param menuRes The resource of the menu to load.
     * @param playlist The [Playlist] to create the menu for.
     */
    protected fun openMusicMenu(anchor: View, @MenuRes menuRes: Int, playlist: Playlist) {
        logD("Launching new playlist menu: ${playlist.rawName}")

        openMusicMenuImpl(anchor, menuRes) {
            when (it.itemId) {
                R.id.action_play -> {
                    playbackModel.play(playlist)
                }
                R.id.action_shuffle -> {
                    playbackModel.shuffle(playlist)
                }
                R.id.action_play_next -> {
                    playbackModel.playNext(playlist)
                    requireContext().showToast(R.string.lng_queue_added)
                }
                R.id.action_queue_add -> {
                    playbackModel.addToQueue(playlist)
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
        onMenuItemClick: (MenuItem) -> Unit
    ) {
        openMenu(anchor, menuRes) {
            setOnMenuItemClickListener { item ->
                onMenuItemClick(item)
                true
            }
        }
    }

    /**
     * Open a menu. This menu will be managed by the Fragment and closed when the view is destroyed.
     * If a menu is already opened, this call is ignored.
     *
     * @param anchor The [View] to anchor the menu to.
     * @param menuRes The resource of the menu to load.
     * @param block A block that is ran within [PopupMenu] that allows further configuration.
     */
    protected fun openMenu(anchor: View, @MenuRes menuRes: Int, block: PopupMenu.() -> Unit) {
        if (currentMenu != null) {
            logD("Menu already present, not launching")
            return
        }

        currentMenu =
            PopupMenu(requireContext(), anchor).apply {
                inflate(menuRes)
                MenuCompat.setGroupDividerEnabled(menu, true)
                block()
                setOnDismissListener { currentMenu = null }
                show()
            }
    }
}
