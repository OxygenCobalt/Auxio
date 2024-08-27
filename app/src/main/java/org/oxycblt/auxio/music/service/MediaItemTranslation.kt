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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.media.utils.MediaConstants
import androidx.media3.common.MediaMetadata
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
import java.io.ByteArrayOutputStream
import kotlin.math.ceil

enum class Category(val id: String, @StringRes val nameRes: Int, @DrawableRes val bitmapRes: Int?) {
    ROOT("root", R.string.info_app_name, null),
    MORE("more", R.string.lbl_more, R.drawable.ic_more_24),
    SONGS("songs", R.string.lbl_songs, R.drawable.ic_song_bitmap_24),
    ALBUMS("albums", R.string.lbl_albums, R.drawable.ic_album_bitmap_24),
    ARTISTS("artists", R.string.lbl_artists, R.drawable.ic_artist_bitmap_24),
    GENRES("genres", R.string.lbl_genres, R.drawable.ic_genre_bitmap_24),
    PLAYLISTS("playlists", R.string.lbl_playlists, R.drawable.ic_playlist_bitmap_24);

    companion object {
        val DEVICE_MUSIC = listOf(ROOT, SONGS, ALBUMS, ARTISTS, GENRES)
        val USER_MUSIC = listOf(ROOT, PLAYLISTS)
        val IMPORTANT = listOf(SONGS, ALBUMS, ARTISTS, GENRES, PLAYLISTS)
    }
}

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
                ID_CATEGORY ->
                    CategoryItem(when (parts[1]) {
                        Category.ROOT.id -> Category.ROOT
                        Category.MORE.id -> Category.MORE
                        Category.SONGS.id -> Category.SONGS
                        Category.ALBUMS.id -> Category.ALBUMS
                        Category.ARTISTS.id -> Category.ARTISTS
                        Category.GENRES.id -> Category.GENRES
                        Category.PLAYLISTS.id -> Category.PLAYLISTS
                        else -> return null
                    })
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

fun Category.toMediaItem(context: Context): MediaItem {
    // TODO: Make custom overflow menu for compat
    val style =
        Bundle().apply {
            putInt(
                MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_SINGLE_ITEM,
                MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM)
        }
    val mediaSessionUID = MediaSessionUID.CategoryItem(this)
    val description = MediaDescriptionCompat.Builder()
        .setMediaId(mediaSessionUID.toString())
        .setTitle(context.getString(nameRes))
    if (bitmapRes != null) {
        val bitmap = BitmapFactory.decodeResource(context.resources, bitmapRes)
        description.setIconBitmap(bitmap)
    }
    return MediaItem(description.build(), MediaItem.FLAG_BROWSABLE)
}
fun Song.toMediaItem(context: Context, parent: MusicParent?): MediaItem {
    val mediaSessionUID =
        if (parent == null) {
            MediaSessionUID.SingleItem(uid)
        } else {
            MediaSessionUID.ChildItem(parent.uid, uid)
        }
    val description = MediaDescriptionCompat.Builder()
        .setMediaId(mediaSessionUID.toString())
        .setTitle(name.resolve(context))
        .setSubtitle(artists.resolveNames(context))
        .setDescription(album.name.resolve(context))
        .setIconUri(album.cover.single.mediaStoreCoverUri)
        .setMediaUri(uri)
        .build()
    return MediaItem(description, MediaItem.FLAG_PLAYABLE)
}

fun Album.toMediaItem(context: Context): MediaItem {
    val mediaSessionUID = MediaSessionUID.SingleItem(uid)
    val description = MediaDescriptionCompat.Builder()
        .setMediaId(mediaSessionUID.toString())
        .setTitle(name.resolve(context))
        .setSubtitle(artists.resolveNames(context))
        .setIconUri(cover.single.mediaStoreCoverUri)
        .build()
    return MediaItem(description, MediaItem.FLAG_BROWSABLE)
}

fun Artist.toMediaItem(context: Context): MediaItem {
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
    val description = MediaDescriptionCompat.Builder()
        .setMediaId(mediaSessionUID.toString())
        .setTitle(name.resolve(context))
        .setSubtitle(counts)
        .setIconUri(cover.single.mediaStoreCoverUri)
        .build()
    return MediaItem(description, MediaItem.FLAG_BROWSABLE)
}

fun Genre.toMediaItem(context: Context): MediaItem {
    val mediaSessionUID = MediaSessionUID.SingleItem(uid)
    val counts =
                if (songs.isNotEmpty()) {
                    context.getPlural(R.plurals.fmt_song_count, songs.size)
                } else {
                    context.getString(R.string.def_song_count)
                }
    val description = MediaDescriptionCompat.Builder()
        .setMediaId(mediaSessionUID.toString())
        .setTitle(name.resolve(context))
        .setSubtitle(counts)
        .setIconUri(cover.single.mediaStoreCoverUri)
        .build()
    return MediaItem(description, MediaItem.FLAG_BROWSABLE)
}

fun Playlist.toMediaItem(context: Context): MediaItem {
    val mediaSessionUID = MediaSessionUID.SingleItem(uid)
    val counts =
                if (songs.isNotEmpty()) {
                    context.getPlural(R.plurals.fmt_song_count, songs.size)
                } else {
                    context.getString(R.string.def_song_count)
                }
    val description = MediaDescriptionCompat.Builder()
        .setMediaId(mediaSessionUID.toString())
        .setTitle(name.resolve(context))
        .setSubtitle(counts)
        .setIconUri(cover?.single?.mediaStoreCoverUri)
        .build()
    return MediaItem(description, MediaItem.FLAG_BROWSABLE)
}
