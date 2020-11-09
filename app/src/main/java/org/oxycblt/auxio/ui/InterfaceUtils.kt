package org.oxycblt.auxio.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.state.PlaybackMode

// Functions for managing UI elements [Not Colors]

fun showActionMenuForSong(
    context: Context,
    song: Song,
    view: View,
    playbackModel: PlaybackViewModel
) {
    PopupMenu(context, view).apply {
        inflate(R.menu.menu_song_actions)
        setOnMenuItemClickListener {
            return@setOnMenuItemClickListener when (it.itemId) {
                R.id.action_queue_add -> {
                    playbackModel.addToUserQueue(song)

                    Toast.makeText(
                        context,
                        context.getString(R.string.label_queue_added),
                        Toast.LENGTH_SHORT
                    ).show()

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
}

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
