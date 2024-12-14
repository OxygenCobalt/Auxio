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
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.playback.formatDurationDs
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.musikr.Album
import org.oxycblt.musikr.Artist
import org.oxycblt.musikr.Genre
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.MusicParent
import org.oxycblt.musikr.Playlist
import org.oxycblt.musikr.Song

sealed interface MediaSessionUID {
    data class Tab(val node: TabNode) : MediaSessionUID {
        override fun toString() = "$ID_CATEGORY:${node.id}"
    }

    data class SingleItem(val uid: Music.UID) : MediaSessionUID {
        override fun toString() = "$ID_ITEM:$uid"
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
                ID_CATEGORY -> Tab(TabNode.fromString(parts[1]) ?: return null)
                ID_ITEM -> SingleItem(Music.UID.fromString(parts[1]) ?: return null)
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

fun header(name: String): Sugar = {
    putString(MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_GROUP_TITLE, name)
}

fun child(of: MusicParent): Sugar = {
    putString(MusicBrowser.KEY_CHILD_OF, MediaSessionUID.SingleItem(of.uid).toString())
}

private fun style(style: Int): Sugar = {
    putInt(MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_SINGLE_ITEM, style)
}

private fun makeExtras(context: Context, vararg sugars: Sugar): Bundle {
    return Bundle().apply { sugars.forEach { this.it(context) } }
}

fun TabNode.toMediaItem(context: Context): MediaItem {
    val extras =
        makeExtras(
            context,
            style(MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM))
    val mediaSessionUID = MediaSessionUID.Tab(this)
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

fun Song.toMediaDescription(context: Context, vararg sugar: Sugar): MediaDescriptionCompat {
    val mediaSessionUID = MediaSessionUID.SingleItem(uid)
    val extras = makeExtras(context, *sugar)
    return MediaDescriptionCompat.Builder()
        .setMediaId(mediaSessionUID.toString())
        .setTitle(name.resolve(context))
        .setSubtitle(artists.resolveNames(context))
        .setDescription(album.name.resolve(context))
        //        .setIconUri(cover.mediaStoreCoverUri)
        .setMediaUri(uri)
        .setExtras(extras)
        .build()
}

fun Song.toMediaItem(context: Context, vararg sugar: Sugar): MediaItem {
    return MediaItem(toMediaDescription(context, *sugar), MediaItem.FLAG_PLAYABLE)
}

fun Album.toMediaItem(context: Context, vararg sugar: Sugar): MediaItem {
    val mediaSessionUID = MediaSessionUID.SingleItem(uid)
    val extras = makeExtras(context, *sugar)
    val counts = context.getPlural(R.plurals.fmt_song_count, songs.size)
    val description =
        MediaDescriptionCompat.Builder()
            .setMediaId(mediaSessionUID.toString())
            .setTitle(name.resolve(context))
            .setSubtitle(artists.resolveNames(context))
            .setDescription(counts)
            //            .setIconUri(cover.single.mediaStoreCoverUri)
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
    val extras = makeExtras(context, *sugar)
    val description =
        MediaDescriptionCompat.Builder()
            .setMediaId(mediaSessionUID.toString())
            .setTitle(name.resolve(context))
            .setSubtitle(counts)
            .setDescription(genres.resolveNames(context))
            //            .setIconUri(cover.single.mediaStoreCoverUri)
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
    val extras = makeExtras(context, *sugar)
    val description =
        MediaDescriptionCompat.Builder()
            .setMediaId(mediaSessionUID.toString())
            .setTitle(name.resolve(context))
            .setSubtitle(counts)
            //            .setIconUri(cover.single.mediaStoreCoverUri)
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
    val extras = makeExtras(context, *sugar)
    val description =
        MediaDescriptionCompat.Builder()
            .setMediaId(mediaSessionUID.toString())
            .setTitle(name.resolve(context))
            .setSubtitle(counts)
            .setDescription(durationMs.formatDurationDs(true))
            //            .setIconUri(cover?.single?.mediaStoreCoverUri)
            .setExtras(extras)
            .build()
    return MediaItem(description, MediaItem.FLAG_BROWSABLE)
}
