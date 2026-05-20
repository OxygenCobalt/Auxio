package org.oxycblt.auxio.headunit

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.drawable.IconCompat
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.MainActivity
import org.oxycblt.auxio.R

object HeadUnitEntryPoints {
    private const val ACTION_PREFIX = BuildConfig.APPLICATION_ID + ".action."

    const val ACTION_OPEN_NOW_PLAYING = ACTION_PREFIX + "OPEN_NOW_PLAYING"
    const val ACTION_SHUFFLE_ALL = ACTION_PREFIX + "SHUFFLE_ALL"
    const val ACTION_OPEN_QUEUE = ACTION_PREFIX + "OPEN_QUEUE"
    const val ACTION_OPEN_RECENTLY_ADDED = ACTION_PREFIX + "OPEN_RECENTLY_ADDED"
    const val ACTION_OPEN_GENRES = ACTION_PREFIX + "OPEN_GENRES"
    const val ACTION_OPEN_ARTISTS = ACTION_PREFIX + "OPEN_ARTISTS"
    const val ACTION_OPEN_ALBUMS = ACTION_PREFIX + "OPEN_ALBUMS"
    const val ACTION_OPEN_FAVOURITES = ACTION_PREFIX + "OPEN_FAVOURITES"
    const val ACTION_OPEN_HEAD_UNIT_SETTINGS = ACTION_PREFIX + "OPEN_HEAD_UNIT_SETTINGS"

    const val EXTRA_ENTRY_DESTINATION = ACTION_PREFIX + "ENTRY_DESTINATION"

    enum class EntryDestination {
        NOW_PLAYING,
        QUEUE,
        RECENTLY_ADDED,
        GENRES,
        ARTISTS,
        ALBUMS,
        FAVOURITES,
        HEAD_UNIT_SETTINGS,
    }

    fun destinationForAction(action: String?): EntryDestination? =
        when (action) {
            ACTION_OPEN_NOW_PLAYING -> EntryDestination.NOW_PLAYING
            ACTION_OPEN_QUEUE -> EntryDestination.QUEUE
            ACTION_OPEN_RECENTLY_ADDED -> EntryDestination.RECENTLY_ADDED
            ACTION_OPEN_GENRES -> EntryDestination.GENRES
            ACTION_OPEN_ARTISTS -> EntryDestination.ARTISTS
            ACTION_OPEN_ALBUMS -> EntryDestination.ALBUMS
            ACTION_OPEN_FAVOURITES -> EntryDestination.FAVOURITES
            ACTION_OPEN_HEAD_UNIT_SETTINGS -> EntryDestination.HEAD_UNIT_SETTINGS
            else -> null
        }

    fun createDynamicShortcuts(context: Context): List<ShortcutInfoCompat> {
        val icon = IconCompat.createWithResource(context, R.drawable.ic_auxio_24)
        return listOf(
            shortcut(context, "shortcut_now_playing", R.string.lbl_playback, ACTION_OPEN_NOW_PLAYING, icon),
            shortcut(context, "shortcut_shuffle", R.string.lbl_shuffle_shortcut_long, ACTION_SHUFFLE_ALL, icon),
            shortcut(context, "shortcut_queue", R.string.lbl_queue, ACTION_OPEN_QUEUE, icon),
            shortcut(context, "shortcut_recently_added", R.string.lbl_recently_added, ACTION_OPEN_RECENTLY_ADDED, icon),
            shortcut(context, "shortcut_genres", R.string.lbl_genres, ACTION_OPEN_GENRES, icon),
            shortcut(context, "shortcut_artists", R.string.lbl_artists, ACTION_OPEN_ARTISTS, icon),
            shortcut(context, "shortcut_albums", R.string.lbl_albums, ACTION_OPEN_ALBUMS, icon),
            shortcut(context, "shortcut_favourites", R.string.lbl_favourites, ACTION_OPEN_FAVOURITES, icon),
            shortcut(context, "shortcut_head_unit_settings", R.string.set_head_unit_title, ACTION_OPEN_HEAD_UNIT_SETTINGS, icon),
        )
    }

    private fun shortcut(
        context: Context,
        id: String,
        labelRes: Int,
        action: String,
        icon: IconCompat,
    ): ShortcutInfoCompat =
        ShortcutInfoCompat.Builder(context, id)
            .setShortLabel(context.getString(labelRes))
            .setLongLabel(context.getString(labelRes))
            .setIcon(icon)
            .setIntent(Intent(context, MainActivity::class.java).setAction(action))
            .build()
}
