package org.oxycblt.auxio.ui

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import org.oxycblt.auxio.R
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.state.PlaybackMode

/**
 * A wrapper around [PopupMenu] that automates a ton of things across all the menus in Auxio
 * @param activity [AppCompatActivity] required as both a context and ViewModelStore owner.
 * @param anchor [View] This should be centered around
 * @param data [BaseModel] this menu corresponds to
 * @param flag Any extra flags to accompany the data.
 * See [FLAG_NONE], [FLAG_IN_ALBUM], [FLAG_IN_ARTIST] and [FLAG_IN_GENRE] for more details.
 */
class ActionMenu(
    activity: AppCompatActivity,
    anchor: View,
    private val data: BaseModel,
    private val flag: Int = FLAG_NONE
) : PopupMenu(activity, anchor) {
    private val context = activity.applicationContext

    private val detailModel: DetailViewModel by lazy {
        ViewModelProvider(activity).get(DetailViewModel::class.java)
    }

    private val playbackModel: PlaybackViewModel by lazy {
        ViewModelProvider(activity).get(PlaybackViewModel::class.java)
    }

    init {
        val menuRes = determineMenu()

        check(menuRes != -1) { "There is no menu associated with this configuration." }

        inflate(menuRes)
        setOnMenuItemClickListener {
            onMenuClick(it.itemId)
            true
        }
        show()
    }

    /**
     * Figure out what menu to use here, based on the data & flags
     */
    @MenuRes
    private fun determineMenu(): Int {
        return when (data) {
            is Song -> {
                when (flag) {
                    FLAG_NONE, FLAG_IN_GENRE -> R.menu.menu_song_actions
                    FLAG_IN_ALBUM -> R.menu.menu_album_song_actions

                    else -> -1
                }
            }

            is Album -> {
                when (flag) {
                    FLAG_NONE -> R.menu.menu_album_actions
                    FLAG_IN_ARTIST -> R.menu.menu_artist_album_actions

                    else -> -1
                }
            }

            is Artist -> R.menu.menu_artist_actions

            is Genre -> R.menu.menu_genre_actions

            else -> -1
        }
    }

    /**
     * Determine what to do when a MenuItem is clicked.
     */
    private fun onMenuClick(@IdRes id: Int) {
        when (id) {
            R.id.action_play -> {
                when (data) {
                    is Album -> playbackModel.playAlbum(data, false)
                    is Artist -> playbackModel.playArtist(data, false)
                    is Genre -> playbackModel.playGenre(data, false)

                    else -> {}
                }
            }

            R.id.action_shuffle -> {
                when (data) {
                    is Album -> playbackModel.playAlbum(data, true)
                    is Artist -> playbackModel.playArtist(data, true)
                    is Genre -> playbackModel.playGenre(data, true)

                    else -> {}
                }
            }

            R.id.action_play_artist -> {
                if (flag == FLAG_IN_ALBUM && data is Song) {
                    playbackModel.playSong(data, PlaybackMode.IN_ARTIST)
                }
            }

            R.id.action_queue_add -> {
                when (data) {
                    is Song -> {
                        playbackModel.addToUserQueue(data)
                        context.getString(R.string.label_queue_added).createToast(context)
                    }
                    is Album -> {
                        playbackModel.addToUserQueue(data)
                        context.getString(R.string.label_queue_added).createToast(context)
                    }

                    else -> {}
                }
            }

            R.id.action_go_album -> {
                if (data is Song) {
                    determineWhereToNavWithSong(data.album)
                }
            }

            R.id.action_go_artist -> {
                if (data is Song) {
                    determineWhereToNavWithSong(data.album.artist)
                } else if (data is Album) {
                    detailModel.navToItem(data.artist)
                }
            }
        }
    }

    private fun determineWhereToNavWithSong(parent: BaseModel) {
        when (flag) {
            FLAG_NONE -> detailModel.navToItem(parent)
            FLAG_IN_ALBUM -> detailModel.navToParent()
            FLAG_IN_GENRE -> detailModel.navToChild(parent)
        }
    }

    companion object {
        /** No Flags **/
        const val FLAG_NONE = -1
        /** Flag for when an item is accessed from an artist **/
        const val FLAG_IN_ARTIST = 0
        /** Flag for when an item is accessed from an album **/
        const val FLAG_IN_ALBUM = 1
        /** Flag or when an item is accessed from a genre **/
        const val FLAG_IN_GENRE = 2
    }
}
