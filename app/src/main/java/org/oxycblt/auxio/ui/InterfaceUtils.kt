package org.oxycblt.auxio.ui

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
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
 */
fun ImageButton.disable() {
    if (isEnabled) {
        imageTintList = ColorStateList.valueOf(
            R.color.inactive_color.toColor(context)
        )

        isEnabled = false
    }
}

/**
 * Determine if the device is currently in landscape.
 * @param resources [Resources] required
 */
fun isLandscape(resources: Resources): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
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
 * Show actions for a song item, such as the ones found in [org.oxycblt.auxio.songs.SongsFragment]
 * @param context [Context] required
 * @param song [Song] The menu should correspond to
 * @param playbackModel The [PlaybackViewModel] the menu should dispatch actions to.
 * @param detailModel The [DetailViewModel] the menu should dispatch actions to.
 */
fun PopupMenu.setupSongActions(
    context: Context,
    song: Song,
    playbackModel: PlaybackViewModel,
    detailModel: DetailViewModel
) {
    setOnMenuItemClickListener {
        when (it.itemId) {
            R.id.action_queue_add -> {
                playbackModel.addToUserQueue(song)
                context.getString(R.string.label_queue_added).createToast(context)
                true
            }

            R.id.action_go_artist -> {
                detailModel.navToItem(song.album.artist)
                true
            }

            R.id.action_go_album -> {
                detailModel.navToItem(song.album)
                true
            }

            else -> false
        }
    }

    inflateAndShow(R.menu.menu_song_actions)
}

/**
 * Show actions for a album song item, such as the ones found in
 * [org.oxycblt.auxio.detail.AlbumDetailFragment]
 * @param context [Context] required
 * @param song [Song] The menu should correspond to
 * @param detailModel The [DetailViewModel] the menu should dispatch some actions to.
 * @param playbackModel The [PlaybackViewModel] the menu should dispatch actions to.
 */
fun PopupMenu.setupAlbumSongActions(
    context: Context,
    song: Song,
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
                detailModel.navToParent()
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
 * Show actions for an [Album].
 * @param context [Context] required
 * @param album [Album] The menu should correspond to
 * @param playbackModel The [PlaybackViewModel] the menu should dispatch actions to.
 */
fun PopupMenu.setupAlbumActions(
    context: Context,
    album: Album,
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
 * Show actions for an [Artist].
 * @param artist The [Artist] The menu should correspond to
 * @param playbackModel The [PlaybackViewModel] the menu should dispatch actions to.
 */
fun PopupMenu.setupArtistActions(artist: Artist, playbackModel: PlaybackViewModel) {
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
    inflateAndShow(R.menu.menu_artist_actions)
}

/**
 * Show actions for a [Genre].
 * @param genre The [Genre] The menu should correspond to
 * @param playbackModel The [PlaybackViewModel] the menu should dispatch actions to.
 */
fun PopupMenu.setupGenreActions(genre: Genre, playbackModel: PlaybackViewModel) {
    setOnMenuItemClickListener {
        when (it.itemId) {
            R.id.action_shuffle -> {
                playbackModel.playGenre(genre, true)
                true
            }

            else -> false
        }
    }
    inflateAndShow(R.menu.menu_genre_actions)
}

/**
 * Show actions for a [Genre] song. Mostly identical to [setupSongActions] aside from a different
 * flag being used for navigation.
 * @param context [Context] required
 * @param song [Song] The menu should correspond to
 * @param playbackModel The [PlaybackViewModel] the menu should dispatch actions to.
 * @param detailModel The [DetailViewModel] the menu should dispatch actions to.
 */
fun PopupMenu.setupGenreSongActions(
    context: Context,
    song: Song,
    playbackModel: PlaybackViewModel,
    detailModel: DetailViewModel
) {
    setOnMenuItemClickListener {
        when (it.itemId) {
            R.id.action_queue_add -> {
                playbackModel.addToUserQueue(song)
                context.getString(R.string.label_queue_added).createToast(context)
                true
            }

            R.id.action_go_artist -> {
                detailModel.navToChild(song.album.artist)
                true
            }

            R.id.action_go_album -> {
                detailModel.navToChild(song.album)
                true
            }

            else -> false
        }
    }

    inflateAndShow(R.menu.menu_song_actions)
}

/**
 * Shortcut method that inflates a menu and shows the action menu.
 * @param menuRes the menu that should be shown.
 */
private fun PopupMenu.inflateAndShow(@MenuRes menuRes: Int) {
    inflate(menuRes)
    show()
}
