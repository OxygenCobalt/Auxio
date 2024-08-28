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

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaDescriptionCompat
import androidx.annotation.StringRes
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
import org.oxycblt.auxio.util.getPlural

sealed interface MediaSessionUID {
    data class CategoryItem(val category: Category) : MediaSessionUID {
        override fun toString() = "$ID_CATEGORY:$category"
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

typealias Sugar = Bundle.(Context) -> Unit

fun header(@StringRes nameRes: Int): Sugar = {
    putString(
        MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_GROUP_TITLE, it.getString(nameRes))
}

fun Category.toMediaItem(context: Context): MediaItem {
    val extras =
        Bundle().apply {
            putInt(
                MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_SINGLE_ITEM,
                MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM)
        }
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

fun Song.toMediaItem(
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
    val extras = Bundle().apply { sugar.forEach { this.it(context) } }
    val description =
        MediaDescriptionCompat.Builder()
            .setMediaId(mediaSessionUID.toString())
            .setTitle(name.resolve(context))
            .setSubtitle(artists.resolveNames(context))
            .setDescription(album.name.resolve(context))
            .setIconUri(album.cover.single.mediaStoreCoverUri)
            .setMediaUri(uri)
            .setExtras(extras)
            .build()
    return MediaItem(description, MediaItem.FLAG_PLAYABLE)
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
    val extras = Bundle().apply { sugar.forEach { this.it(context) } }
    val description =
        MediaDescriptionCompat.Builder()
            .setMediaId(mediaSessionUID.toString())
            .setTitle(name.resolve(context))
            .setSubtitle(artists.resolveNames(context))
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
    val extras = Bundle().apply { sugar.forEach { this.it(context) } }
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

fun Genre.toMediaItem(context: Context, vararg sugar: Sugar): MediaItem {
    val mediaSessionUID = MediaSessionUID.SingleItem(uid)
    val counts =
        if (songs.isNotEmpty()) {
            context.getPlural(R.plurals.fmt_song_count, songs.size)
        } else {
            context.getString(R.string.def_song_count)
        }
    val extras = Bundle().apply { sugar.forEach { this.it(context) } }
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
    val extras = Bundle().apply { sugar.forEach { this.it(context) } }
    val description =
        MediaDescriptionCompat.Builder()
            .setMediaId(mediaSessionUID.toString())
            .setTitle(name.resolve(context))
            .setSubtitle(counts)
            .setIconUri(cover?.single?.mediaStoreCoverUri)
            .setExtras(extras)
            .build()
    return MediaItem(description, MediaItem.FLAG_BROWSABLE)
}
