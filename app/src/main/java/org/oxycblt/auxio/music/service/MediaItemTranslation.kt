/*
 * Copyright (c) 2024 Auxio Project
 * MediaItemTranslation.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.service

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaDescriptionCompat
import android.view.MenuInflater
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.children
import androidx.media.utils.MediaConstants
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.playback.formatDurationDs
import org.oxycblt.auxio.util.getPlural

sealed interface MediaSessionUID {
    data class CategoryItem(val category: Category) : MediaSessionUID {
        override fun toString() = "$ID_CATEGORY:${category.id}"
    }

    data class SingleItem(val uid: Music.UID) : MediaSessionUID {
        override fun toString() = "$ID_ITEM:$uid"
    }

    data class ChildItem(val parentUid: Music.UID, val childUid: Music.UID) : MediaSessionUID {
        override fun toString() = "$ID_ITEM:$parentUid>$childUid"
    }

    companion object {
        const val ID_CATEGORY = BuildConfig.APPLICATION_ID + ".category"
        const val ID_ITEM = BuildConfig.APPLICATION_ID + ".item"

        fun fromString(str: String): MediaSessionUID? {
            val parts = str.split(":", limit = 2)
            if (parts.size != 2) {
                return null
            }
            return when (parts[0]) {
                ID_CATEGORY -> CategoryItem(Category.fromString(parts[1]) ?: return null)
                ID_ITEM -> {
                    val uids = parts[1].split(">", limit = 2)
                    if (uids.size == 1) {
                        Music.UID.fromString(uids[0])?.let { SingleItem(it) }
                    } else {
                        Music.UID.fromString(uids[0])?.let { parent ->
                            Music.UID.fromString(uids[1])?.let { child -> ChildItem(parent, child) }
                        }
                    }
                }
                else -> return null
            }
        }
    }
}

enum class BrowserOption(val actionId: String, val labelRes: Int, val iconRes: Int) {
    PLAY(BuildConfig.APPLICATION_ID + ".menu.PLAY", R.string.lbl_play, R.drawable.ic_play_24),
    SHUFFLE(
        BuildConfig.APPLICATION_ID + ".menu.SHUFFLE",
        R.string.lbl_shuffle,
        R.drawable.ic_shuffle_off_24),
    PLAY_NEXT(
        BuildConfig.APPLICATION_ID + ".menu.PLAY_NEXT",
        R.string.lbl_play_next,
        R.drawable.ic_play_next_24),
    ADD_TO_QUEUE(
        BuildConfig.APPLICATION_ID + ".menu.ADD_TO_QUEUE",
        R.string.lbl_queue_add,
        R.drawable.ic_queue_add_24),
    DETAILS(
        BuildConfig.APPLICATION_ID + ".menu.DETAILS",
        R.string.lbl_parent_detail,
        R.drawable.ic_details_24),
    ALBUM_DETAILS(
        BuildConfig.APPLICATION_ID + ".menu.ALBUM_DETAILS",
        R.string.lbl_album_details,
        R.drawable.ic_album_24),
    ARTIST_DETAILS(
        BuildConfig.APPLICATION_ID + ".menu.ARTIST_DETAILS",
        R.string.lbl_artist_details,
        R.drawable.ic_artist_24);

    companion object {
        val ITEM_ID_MAP =
            mapOf(
                R.id.action_play to PLAY,
                R.id.action_shuffle to SHUFFLE,
                R.id.action_play_next to PLAY_NEXT,
                R.id.action_queue_add to ADD_TO_QUEUE,
                R.id.action_detail to DETAILS,
                R.id.action_album_details to ALBUM_DETAILS,
                R.id.action_artist_details to ARTIST_DETAILS)
    }
}

typealias Sugar = Bundle.(Context) -> Unit

fun header(@StringRes nameRes: Int): Sugar = {
    putString(
        MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_GROUP_TITLE, it.getString(nameRes))
}

private fun style(style: Int): Sugar = {
    putInt(MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_SINGLE_ITEM, style)
}

private fun menu(@MenuRes res: Int): Sugar = { context ->
    @SuppressLint("RestrictedApi") val builder = MenuBuilder(context)
    MenuInflater(context).inflate(res, builder)
    val menuIds =
        builder.children.mapNotNullTo(ArrayList()) {
            BrowserOption.ITEM_ID_MAP[it.itemId]?.actionId
        }
    putStringArrayList(MediaConstants.DESCRIPTION_EXTRAS_KEY_CUSTOM_BROWSER_ACTION_ID_LIST, menuIds)
}

private fun makeExtras(context: Context, vararg sugars: Sugar): Bundle {
    return Bundle().apply { sugars.forEach { this.it(context) } }
}

fun Category.toMediaItem(context: Context): MediaItem {
    val extras =
        makeExtras(
            context,
            style(MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM))
    val mediaSessionUID = MediaSessionUID.CategoryItem(this)
    val description =
        MediaDescriptionCompat.Builder()
            .setMediaId(mediaSessionUID.toString())
            .setTitle(context.getString(nameRes))
            .setExtras(extras)
    bitmapRes?.let { res ->
        val bitmap = BitmapFactory.decodeResource(context.resources, res)
        description.setIconBitmap(bitmap)
    }
    return MediaItem(description.build(), MediaItem.FLAG_BROWSABLE)
}

fun Song.toMediaDescription(
    context: Context,
    parent: MusicParent? = null,
    vararg sugar: Sugar
): MediaDescriptionCompat {
    val mediaSessionUID =
        if (parent == null) {
            MediaSessionUID.SingleItem(uid)
        } else {
            MediaSessionUID.ChildItem(parent.uid, uid)
        }
    val extras = makeExtras(context, *sugar, menu(R.menu.song))
    return MediaDescriptionCompat.Builder()
        .setMediaId(mediaSessionUID.toString())
        .setTitle(name.resolve(context))
        .setSubtitle(artists.resolveNames(context))
        .setDescription(album.name.resolve(context))
        .setIconUri(cover.mediaStoreCoverUri)
        .setMediaUri(uri)
        .setExtras(extras)
        .build()
}

fun Song.toMediaItem(
    context: Context,
    parent: MusicParent? = null,
    vararg sugar: Sugar
): MediaItem {
    return MediaItem(toMediaDescription(context, parent, *sugar), MediaItem.FLAG_PLAYABLE)
}

fun Album.toMediaItem(
    context: Context,
    parent: MusicParent? = null,
    vararg sugar: Sugar
): MediaItem {
    val mediaSessionUID =
        if (parent == null) {
            MediaSessionUID.SingleItem(uid)
        } else {
            MediaSessionUID.ChildItem(parent.uid, uid)
        }
    val extras = makeExtras(context, *sugar, menu(R.menu.album))
    val counts = context.getPlural(R.plurals.fmt_song_count, songs.size)
    val description =
        MediaDescriptionCompat.Builder()
            .setMediaId(mediaSessionUID.toString())
            .setTitle(name.resolve(context))
            .setSubtitle(artists.resolveNames(context))
            .setDescription(counts)
            .setIconUri(cover.single.mediaStoreCoverUri)
            .setExtras(extras)
            .build()
    return MediaItem(description, MediaItem.FLAG_BROWSABLE)
}

fun Artist.toMediaItem(context: Context, vararg sugar: Sugar): MediaItem {
    val mediaSessionUID = MediaSessionUID.SingleItem(uid)
    val counts =
        context.getString(
            R.string.fmt_two,
            if (explicitAlbums.isNotEmpty()) {
                context.getPlural(R.plurals.fmt_album_count, explicitAlbums.size)
            } else {
                context.getString(R.string.def_album_count)
            },
            if (songs.isNotEmpty()) {
                context.getPlural(R.plurals.fmt_song_count, songs.size)
            } else {
                context.getString(R.string.def_song_count)
            })
    val extras = makeExtras(context, *sugar, menu(R.menu.parent))
    val description =
        MediaDescriptionCompat.Builder()
            .setMediaId(mediaSessionUID.toString())
            .setTitle(name.resolve(context))
            .setSubtitle(counts)
            .setDescription(genres.resolveNames(context))
            .setIconUri(cover.single.mediaStoreCoverUri)
            .setExtras(extras)
            .build()
    return MediaItem(description, MediaItem.FLAG_BROWSABLE)
}

fun Genre.toMediaItem(context: Context, vararg sugar: Sugar): MediaItem {
    val mediaSessionUID = MediaSessionUID.SingleItem(uid)
    val counts =
        if (songs.isNotEmpty()) {
            context.getPlural(R.plurals.fmt_song_count, songs.size)
        } else {
            context.getString(R.string.def_song_count)
        }
    val extras = makeExtras(context, *sugar, menu(R.menu.parent))
    val description =
        MediaDescriptionCompat.Builder()
            .setMediaId(mediaSessionUID.toString())
            .setTitle(name.resolve(context))
            .setSubtitle(counts)
            .setIconUri(cover.single.mediaStoreCoverUri)
            .setExtras(extras)
            .build()
    return MediaItem(description, MediaItem.FLAG_BROWSABLE)
}

fun Playlist.toMediaItem(context: Context, vararg sugar: Sugar): MediaItem {
    val mediaSessionUID = MediaSessionUID.SingleItem(uid)
    val counts =
        if (songs.isNotEmpty()) {
            context.getPlural(R.plurals.fmt_song_count, songs.size)
        } else {
            context.getString(R.string.def_song_count)
        }
    val extras = makeExtras(context, *sugar, menu(R.menu.playlist))
    val description =
        MediaDescriptionCompat.Builder()
            .setMediaId(mediaSessionUID.toString())
            .setTitle(name.resolve(context))
            .setSubtitle(counts)
            .setDescription(durationMs.formatDurationDs(true))
            .setIconUri(cover?.single?.mediaStoreCoverUri)
            .setExtras(extras)
            .build()
    return MediaItem(description, MediaItem.FLAG_BROWSABLE)
}
