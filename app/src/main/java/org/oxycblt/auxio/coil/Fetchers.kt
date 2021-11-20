/*
 * Copyright (c) 2021 Auxio Project
 * Fetchers.kt is part of Auxio.
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

package org.oxycblt.auxio.coil

import android.content.Context
import coil.ImageLoader
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import okio.buffer
import okio.source
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import kotlin.math.min

/**
 * Fetcher that returns the album art for a given [Album]. Handles settings on whether to use
 * quality covers or not.
 * @author OxygenCobalt
 */
class AlbumArtFetcher private constructor(
    private val context: Context,
    private val album: Album
) : AuxioFetcher() {
    override suspend fun fetch(): FetchResult? {
        return fetchArt(context, album)?.let { stream ->
            SourceResult(
                source = ImageSource(stream.source().buffer(), context),
                mimeType = null,
                dataSource = DataSource.DISK
            )
        }
    }

    class SongFactory : Fetcher.Factory<Song> {
        override fun create(data: Song, options: Options, imageLoader: ImageLoader): Fetcher? {
            return AlbumArtFetcher(options.context, data.album)
        }
    }

    class AlbumFactory : Fetcher.Factory<Album> {
        override fun create(data: Album, options: Options, imageLoader: ImageLoader): Fetcher? {
            return AlbumArtFetcher(options.context, data)
        }
    }
}

class ArtistImageFetcher private constructor(
    private val context: Context,
    private val artist: Artist
) : AuxioFetcher() {
    override suspend fun fetch(): FetchResult? {
        val end = min(4, artist.albums.size)
        val results = artist.albums.mapN(end) { album ->
            fetchArt(context, album)
        }

        return createMosaic(context, results)
    }

    class Factory : Fetcher.Factory<Artist> {
        override fun create(data: Artist, options: Options, imageLoader: ImageLoader): Fetcher? {
            return ArtistImageFetcher(options.context, data)
        }
    }
}

class GenreImageFetcher private constructor(
    private val context: Context,
    private val genre: Genre
) : AuxioFetcher() {
    override suspend fun fetch(): FetchResult? {
        val albums = genre.songs.groupBy { it.album }.keys
        val end = min(4, albums.size)
        val results = albums.mapN(end) { album ->
            fetchArt(context, album)
        }

        return createMosaic(context, results)
    }

    class Factory : Fetcher.Factory<Genre> {
        override fun create(data: Genre, options: Options, imageLoader: ImageLoader): Fetcher? {
            return GenreImageFetcher(options.context, data)
        }
    }
}

/**
 * Map only [n] items from a collection. [transform] is called for each item that is eligible.
 * If null is returned, then that item will be skipped.
 */
private inline fun <T : Any, R : Any> Iterable<T>.mapN(n: Int, transform: (T) -> R?): List<R> {
    val out = mutableListOf<R>()

    for (item in this) {
        if (out.size >= n) {
            break
        }

        transform(item)?.let {
            out.add(it)
        }
    }

    return out
}
