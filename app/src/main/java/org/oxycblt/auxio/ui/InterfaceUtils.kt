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
                doUserQueueAdd(context, song, playbackModel)

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
                doUserQueueAdd(context, song, playbackModel)

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

private fun doUserQueueAdd(context: Context, song: Song, playbackModel: PlaybackViewModel) {
    // If the song was already added to the user queue, then don't add it again.
    // This is just to prevent a bug with DiffCallback that creates strange
    // behavior when duplicate user queue items are added.
    // FIXME: Fix the duplicate item DiffCallback issue
    if (!playbackModel.userQueue.value!!.contains(song)) {
        playbackModel.addToUserQueue(song)

        Toast.makeText(
            context,
            context.getString(R.string.label_queue_added),
            Toast.LENGTH_SHORT
        ).show()
    } else {
        Toast.makeText(
            context,
            context.getString(R.string.label_queue_already_added),
            Toast.LENGTH_SHORT
        ).show()
    }
}
