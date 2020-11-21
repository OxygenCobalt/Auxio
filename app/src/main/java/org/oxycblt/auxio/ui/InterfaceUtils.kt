package org.oxycblt.auxio.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.state.PlaybackMode

// Functions for managing UI elements [Not Colors]

// Apply a color to a Menu Item
fun MenuItem.applyColor(@ColorInt color: Int) {
    SpannableString(title).apply {
        setSpan(ForegroundColorSpan(color), 0, length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        title = this
    }
}

// Disable an ImageButton
fun ImageButton.disable(context: Context) {
    if (isEnabled) {
        imageTintList = ColorStateList.valueOf(
            R.color.inactive_color.toColor(context)
        )

        isEnabled = false
    }
}

fun String.createToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

// Apply a custom vertical divider
fun RecyclerView.applyDivider() {
    val div = DividerItemDecoration(
        context,
        DividerItemDecoration.VERTICAL
    )

    div.setDrawable(
        ColorDrawable(
            R.color.divider_color.toColor(context)
        )
    )

    addItemDecoration(div)
}

fun PopupMenu.setupSongActions(song: Song, context: Context, playbackModel: PlaybackViewModel) {
    inflate(R.menu.menu_song_actions)
    setOnMenuItemClickListener {
        when (it.itemId) {
            R.id.action_queue_add -> {
                playbackModel.addToUserQueue(song)
                context.getString(R.string.label_queue_added).createToast(context)
                true
            }

            R.id.action_play_artist -> {
                playbackModel.playSong(song, PlaybackMode.IN_ARTIST)
                true
            }

            R.id.action_play_album -> {
                playbackModel.playSong(song, PlaybackMode.IN_ALBUM)
                true
            }

            else -> false
        }
    }
    show()
}

fun PopupMenu.setupAlbumSongActions(
    song: Song,
    context: Context,
    detailViewModel: DetailViewModel,
    playbackModel: PlaybackViewModel
) {
    inflate(R.menu.menu_album_song_actions)
    setOnMenuItemClickListener {
        when (it.itemId) {
            R.id.action_queue_add -> {
                playbackModel.addToUserQueue(song)
                context.getString(R.string.label_queue_added).createToast(context)

                true
            }

            R.id.action_go_artist -> {
                detailViewModel.doNavToParent()
                true
            }

            R.id.action_play_artist -> {
                playbackModel.playSong(song, PlaybackMode.IN_ARTIST)
                true
            }

            else -> false
        }
    }
    show()
}

fun PopupMenu.setupAlbumActions(
    album: Album,
    context: Context,
    playbackModel: PlaybackViewModel
) {
    inflate(R.menu.menu_album_actions)
    setOnMenuItemClickListener {
        when (it.itemId) {
            R.id.action_queue_add -> {
                playbackModel.addToUserQueue(album.songs)
                context.getString(R.string.label_queue_added).createToast(context)

                true
            }

            R.id.action_play -> {
                playbackModel.playAlbum(album, false)
                true
            }

            R.id.action_shuffle -> {
                playbackModel.playAlbum(album, true)
                true
            }

            else -> false
        }
    }
    show()
}

fun PopupMenu.setupArtistActions(
    artist: Artist,
    context: Context,
    playbackModel: PlaybackViewModel
) {
    inflate(R.menu.menu_detail)
    setOnMenuItemClickListener {
        when (it.itemId) {
            R.id.action_queue_add -> {
                playbackModel.addToUserQueue(artist.songs)
                context.getString(R.string.label_queue_added).createToast(context)

                true
            }

            R.id.action_play -> {
                playbackModel.playArtist(artist, false)
                true
            }

            R.id.action_shuffle -> {
                playbackModel.playArtist(artist, true)
                true
            }

            else -> false
        }
    }
    show()
}

fun PopupMenu.setupGenreActions(
    genre: Genre,
    context: Context,
    playbackModel: PlaybackViewModel
) {
    inflate(R.menu.menu_detail)
    setOnMenuItemClickListener {
        when (it.itemId) {
            R.id.action_queue_add -> {
                playbackModel.addToUserQueue(genre.songs)
                context.getString(R.string.label_queue_added).createToast(context)

                true
            }

            R.id.action_play -> {
                playbackModel.playGenre(genre, false)
                true
            }

            R.id.action_shuffle -> {
                playbackModel.playGenre(genre, true)
                true
            }

            else -> false
        }
    }
    show()
}
