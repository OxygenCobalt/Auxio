package org.oxycblt.auxio.ui

import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.text.HtmlCompat
import org.oxycblt.auxio.R
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.state.PlaybackMode

// Functions for managing UI elements [Not Colors]

/**
 * Apply a text color to a [MenuItem]
 * @param color The text color that should be applied.
 */
fun MenuItem.applyColor(@ColorInt color: Int) {
    SpannableString(title).apply {
        setSpan(ForegroundColorSpan(color), 0, length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        title = this
    }
}

/**
 * Disable an image button.
 * @param context [Context] required to change the [ImageButton]s color.
 */
fun ImageButton.disable(context: Context) {
    if (isEnabled) {
        imageTintList = ColorStateList.valueOf(
            R.color.inactive_color.toColor(context)
        )

        isEnabled = false
    }
}

/**
 * Create a [Toast] from a [String]
 * @param context [Context] required to create the toast
 */
fun String.createToast(context: Context) {
    Toast.makeText(context.applicationContext, this, Toast.LENGTH_SHORT).show()
}

/**
 * "Render" a [Spanned] using [HtmlCompat].
 * @return A [Spanned] that actually works.
 */
fun Spanned.render(): Spanned {
    return HtmlCompat.fromHtml(
        this.toString(), HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS
    )
}

/**
 * Handle the new transparent system bars on light mode. Adapted from Music Player GO
 * (https://github.com/enricocid/Music-Player-GO)
 */
@TargetApi(Build.VERSION_CODES.O_MR1)
@Suppress("DEPRECATION")
fun Window.handleTransparentSystemBars(config: Configuration) {
    fun isNight() = config.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.let { controller ->
            val appearance = WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS or
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS

            val mask = if (isNight()) 0 else appearance

            controller.setSystemBarsAppearance(appearance, mask)
        }
    } else {
        val flags = decorView.systemUiVisibility

        decorView.systemUiVisibility =
            if (isNight()) {
                flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() and
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            } else {
                flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
    }
}

/**
 * Show actions for a song item, such as the ones found in [org.oxycblt.auxio.songs.SongsFragment]
 */
fun PopupMenu.setupSongActions(song: Song, context: Context, playbackModel: PlaybackViewModel) {
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
    inflateAndShow(R.menu.menu_song_actions)
}

/**
 * Show actions for a song item, such as the ones found in [org.oxycblt.auxio.songs.SongsFragment]
 */
fun PopupMenu.setupAlbumSongActions(
    song: Song,
    context: Context,
    detailModel: DetailViewModel,
    playbackModel: PlaybackViewModel
) {
    setOnMenuItemClickListener {
        when (it.itemId) {
            R.id.action_queue_add -> {
                playbackModel.addToUserQueue(song)
                context.getString(R.string.label_queue_added).createToast(context)

                true
            }

            R.id.action_go_artist -> {
                detailModel.doNavToParent()
                true
            }

            R.id.action_play_artist -> {
                playbackModel.playSong(song, PlaybackMode.IN_ARTIST)
                true
            }

            else -> false
        }
    }
    inflateAndShow(R.menu.menu_album_song_actions)
}

/**
 * Show actions for an [Album]
 */
fun PopupMenu.setupAlbumActions(
    album: Album,
    context: Context,
    playbackModel: PlaybackViewModel
) {
    setOnMenuItemClickListener {
        when (it.itemId) {
            R.id.action_queue_add -> {
                playbackModel.addToUserQueue(album)
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
    inflateAndShow(R.menu.menu_album_actions)
}

/**
 * Show actions for an [Artist]
 */
fun PopupMenu.setupArtistActions(
    artist: Artist,
    playbackModel: PlaybackViewModel
) {
    setOnMenuItemClickListener {
        when (it.itemId) {
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
    inflateAndShow(R.menu.menu_detail)
}

/**
 * Show actions for a [Genre]
 */
fun PopupMenu.setupGenreActions(
    genre: Genre,
    playbackModel: PlaybackViewModel
) {
    setOnMenuItemClickListener {
        when (it.itemId) {
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
    inflateAndShow(R.menu.menu_detail)
}

/**
 * Shortcut method that inflates a menu and shows the action menu.
 */
private fun PopupMenu.inflateAndShow(@MenuRes menuRes: Int) {
    inflate(menuRes)
    show()
}
