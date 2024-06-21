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
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.media.utils.MediaConstants
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import java.io.ByteArrayOutputStream
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.device.DeviceLibrary
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.util.getPlural

fun MediaSessionUID.Category.toMediaItem(context: Context): MediaItem {
    // TODO: Make custom overflow menu for compat
    val style =
        Bundle().apply {
            putInt(
                MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_SINGLE_ITEM,
                MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM)
        }
    val metadata =
        MediaMetadata.Builder()
            .setTitle(context.getString(nameRes))
            .setIsPlayable(false)
            .setIsBrowsable(true)
            .setMediaType(mediaType)
            .setExtras(style)
    if (bitmapRes != null) {
        val data = ByteArrayOutputStream()
        BitmapFactory.decodeResource(context.resources, bitmapRes)
            .compress(Bitmap.CompressFormat.PNG, 100, data)
        metadata.setArtworkData(data.toByteArray(), MediaMetadata.PICTURE_TYPE_FILE_ICON)
    }
    return MediaItem.Builder().setMediaId(toString()).setMediaMetadata(metadata.build()).build()
}

fun Song.toMediaItem(context: Context, parent: MusicParent?): MediaItem {
    val mediaSessionUID =
        if (parent == null) {
            MediaSessionUID.Single(uid)
        } else {
            MediaSessionUID.Joined(parent.uid, uid)
        }
    val metadata =
        MediaMetadata.Builder()
            .setTitle(name.resolve(context))
            .setArtist(artists.resolveNames(context))
            .setAlbumTitle(album.name.resolve(context))
            .setAlbumArtist(album.artists.resolveNames(context))
            .setTrackNumber(track)
            .setDiscNumber(disc?.number)
            .setGenre(genres.resolveNames(context))
            .setDisplayTitle(name.resolve(context))
            .setSubtitle(artists.resolveNames(context))
            .setRecordingYear(album.dates?.min?.year)
            .setRecordingMonth(album.dates?.min?.month)
            .setRecordingDay(album.dates?.min?.day)
            .setReleaseYear(album.dates?.min?.year)
            .setReleaseMonth(album.dates?.min?.month)
            .setReleaseDay(album.dates?.min?.day)
            .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
            .setIsPlayable(true)
            .setIsBrowsable(false)
            .setArtworkUri(cover.mediaStoreCoverUri)
            .setExtras(
                Bundle().apply {
                    putString("uid", mediaSessionUID.toString())
                    putLong("durationMs", durationMs)
                })
            .build()
    return MediaItem.Builder()
        .setUri(uri)
        .setMediaId(mediaSessionUID.toString())
        .setMediaMetadata(metadata)
        .build()
}

fun Album.toMediaItem(context: Context): MediaItem {
    val mediaSessionUID = MediaSessionUID.Single(uid)
    val metadata =
        MediaMetadata.Builder()
            .setTitle(name.resolve(context))
            .setArtist(artists.resolveNames(context))
            .setAlbumTitle(name.resolve(context))
            .setAlbumArtist(artists.resolveNames(context))
            .setRecordingYear(dates?.min?.year)
            .setRecordingMonth(dates?.min?.month)
            .setRecordingDay(dates?.min?.day)
            .setReleaseYear(dates?.min?.year)
            .setReleaseMonth(dates?.min?.month)
            .setReleaseDay(dates?.min?.day)
            .setMediaType(MediaMetadata.MEDIA_TYPE_ALBUM)
            .setIsPlayable(false)
            .setIsBrowsable(true)
            .setArtworkUri(cover.single.mediaStoreCoverUri)
            .setExtras(Bundle().apply { putString("uid", mediaSessionUID.toString()) })
            .build()
    return MediaItem.Builder()
        .setMediaId(mediaSessionUID.toString())
        .setMediaMetadata(metadata)
        .build()
}

fun Artist.toMediaItem(context: Context): MediaItem {
    val mediaSessionUID = MediaSessionUID.Single(uid)
    val metadata =
        MediaMetadata.Builder()
            .setTitle(name.resolve(context))
            .setSubtitle(
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
                    }))
            .setMediaType(MediaMetadata.MEDIA_TYPE_ARTIST)
            .setIsPlayable(false)
            .setIsBrowsable(true)
            .setGenre(genres.resolveNames(context))
            .setArtworkUri(cover.single.mediaStoreCoverUri)
            .setExtras(Bundle().apply { putString("uid", mediaSessionUID.toString()) })
            .build()
    return MediaItem.Builder()
        .setMediaId(mediaSessionUID.toString())
        .setMediaMetadata(metadata)
        .build()
}

fun Genre.toMediaItem(context: Context): MediaItem {
    val mediaSessionUID = MediaSessionUID.Single(uid)
    val metadata =
        MediaMetadata.Builder()
            .setTitle(name.resolve(context))
            .setSubtitle(
                if (songs.isNotEmpty()) {
                    context.getPlural(R.plurals.fmt_song_count, songs.size)
                } else {
                    context.getString(R.string.def_song_count)
                })
            .setMediaType(MediaMetadata.MEDIA_TYPE_GENRE)
            .setIsPlayable(false)
            .setIsBrowsable(true)
            .setArtworkUri(cover.single.mediaStoreCoverUri)
            .setExtras(Bundle().apply { putString("uid", mediaSessionUID.toString()) })
            .build()
    return MediaItem.Builder()
        .setMediaId(mediaSessionUID.toString())
        .setMediaMetadata(metadata)
        .build()
}

fun Playlist.toMediaItem(context: Context): MediaItem {
    val mediaSessionUID = MediaSessionUID.Single(uid)
    val metadata =
        MediaMetadata.Builder()
            .setTitle(name.resolve(context))
            .setSubtitle(
                if (songs.isNotEmpty()) {
                    context.getPlural(R.plurals.fmt_song_count, songs.size)
                } else {
                    context.getString(R.string.def_song_count)
                })
            .setMediaType(MediaMetadata.MEDIA_TYPE_PLAYLIST)
            .setIsPlayable(false)
            .setIsBrowsable(true)
            .setArtworkUri(cover?.single?.mediaStoreCoverUri)
            .setExtras(Bundle().apply { putString("uid", mediaSessionUID.toString()) })
            .build()
    return MediaItem.Builder()
        .setMediaId(mediaSessionUID.toString())
        .setMediaMetadata(metadata)
        .build()
}

fun MediaItem.toSong(deviceLibrary: DeviceLibrary): Song? {
    val uid = MediaSessionUID.fromString(mediaId) ?: return null
    return when (uid) {
        is MediaSessionUID.Single -> {
            deviceLibrary.findSong(uid.uid)
        }
        is MediaSessionUID.Joined -> {
            deviceLibrary.findSong(uid.childUid)
        }
        is MediaSessionUID.Category -> null
    }
}

sealed interface MediaSessionUID {
    enum class Category(
        val id: String,
        @StringRes val nameRes: Int,
        @DrawableRes val bitmapRes: Int?,
        val mediaType: Int?
    ) : MediaSessionUID {
        ROOT("root", R.string.info_app_name, null, null),
        SONGS(
            "songs",
            R.string.lbl_songs,
            R.drawable.ic_song_bitmap_24,
            MediaMetadata.MEDIA_TYPE_MUSIC),
        ALBUMS(
            "albums",
            R.string.lbl_albums,
            R.drawable.ic_album_bitmap_24,
            MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS),
        ARTISTS(
            "artists",
            R.string.lbl_artists,
            R.drawable.ic_artist_bitmap_24,
            MediaMetadata.MEDIA_TYPE_FOLDER_ARTISTS),
        GENRES(
            "genres",
            R.string.lbl_genres,
            R.drawable.ic_genre_bitmap_24,
            MediaMetadata.MEDIA_TYPE_FOLDER_GENRES),
        PLAYLISTS(
            "playlists",
            R.string.lbl_playlists,
            R.drawable.ic_playlist_bitmap_24,
            MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS);

        override fun toString() = "$ID_CATEGORY:$id"

        companion object {
            val DEVICE_MUSIC = listOf(ROOT, SONGS, ALBUMS, ARTISTS, GENRES)
            val USER_MUSIC = listOf(ROOT, PLAYLISTS)
            val IMPORTANT = listOf(SONGS, ALBUMS, ARTISTS, GENRES, PLAYLISTS)
        }
    }

    data class Single(val uid: Music.UID) : MediaSessionUID {
        override fun toString() = "$ID_ITEM:$uid"
    }

    data class Joined(val parentUid: Music.UID, val childUid: Music.UID) : MediaSessionUID {
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
                    when (parts[1]) {
                        Category.ROOT.id -> Category.ROOT
                        Category.SONGS.id -> Category.SONGS
                        Category.ALBUMS.id -> Category.ALBUMS
                        Category.ARTISTS.id -> Category.ARTISTS
                        Category.GENRES.id -> Category.GENRES
                        Category.PLAYLISTS.id -> Category.PLAYLISTS
                        else -> null
                    }
                ID_ITEM -> {
                    val uids = parts[1].split(">", limit = 2)
                    if (uids.size == 1) {
                        Music.UID.fromString(uids[0])?.let { Single(it) }
                    } else {
                        Music.UID.fromString(uids[0])?.let { parent ->
                            Music.UID.fromString(uids[1])?.let { child -> Joined(parent, child) }
                        }
                    }
                }
                else -> return null
            }
        }
    }
}
