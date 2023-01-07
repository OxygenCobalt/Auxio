/*
 * Copyright (c) 2021 Auxio Project
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
 
package org.oxycblt.auxio.image.extractor

import android.content.Context
import coil.ImageLoader
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.key.Keyer
import coil.request.Options
import coil.size.Size
import kotlin.math.min
import okio.buffer
import okio.source
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.library.Sort

/**
 * A [Keyer] implementation for [Music] data.
 * @author Alexander Capehart (OxygenCobalt)
 */
class MusicKeyer : Keyer<Music> {
    override fun key(data: Music, options: Options) =
        if (data is Song) {
            // Group up song covers with album covers for better caching
            data.album.uid.toString()
        } else {
            data.uid.toString()
        }
}

/**
 * Generic [Fetcher] for [Album] covers. Works with both [Album] and [Song]. Use [SongFactory] or
 * [AlbumFactory] for instantiation.
 * @author Alexander Capehart (OxygenCobalt)
 */
class AlbumCoverFetcher
private constructor(private val context: Context, private val album: Album) : Fetcher {
    override suspend fun fetch(): FetchResult? =
        Covers.fetch(context, album)?.run {
            SourceResult(
                source = ImageSource(source().buffer(), context),
                mimeType = null,
                dataSource = DataSource.DISK)
        }

    /** A [Fetcher.Factory] implementation that works with [Song]s. */
    class SongFactory : Fetcher.Factory<Song> {
        override fun create(data: Song, options: Options, imageLoader: ImageLoader) =
            AlbumCoverFetcher(options.context, data.album)
    }

    /** A [Fetcher.Factory] implementation that works with [Album]s. */
    class AlbumFactory : Fetcher.Factory<Album> {
        override fun create(data: Album, options: Options, imageLoader: ImageLoader) =
            AlbumCoverFetcher(options.context, data)
    }
}

/**
 * [Fetcher] for [Artist] images. Use [Factory] for instantiation.
 * @author Alexander Capehart (OxygenCobalt)
 */
class ArtistImageFetcher
private constructor(
    private val context: Context,
    private val size: Size,
    private val artist: Artist
) : Fetcher {
    override suspend fun fetch(): FetchResult? {
        // Pick the "most prominent" albums (i.e albums with the most songs) to show in the image.
        val albums = Sort(Sort.Mode.ByCount, false).albums(artist.albums)
        val results = albums.mapAtMostNotNull(4) { album -> Covers.fetch(context, album) }
        return Images.createMosaic(context, results, size)
    }

    /** [Fetcher.Factory] implementation. */
    class Factory : Fetcher.Factory<Artist> {
        override fun create(data: Artist, options: Options, imageLoader: ImageLoader) =
            ArtistImageFetcher(options.context, options.size, data)
    }
}

/**
 * [Fetcher] for [Genre] images. Use [Factory] for instantiation.
 * @author Alexander Capehart (OxygenCobalt)
 */
class GenreImageFetcher
private constructor(
    private val context: Context,
    private val size: Size,
    private val genre: Genre
) : Fetcher {
    override suspend fun fetch(): FetchResult? {
        val results = genre.albums.mapAtMostNotNull(4) { Covers.fetch(context, it) }
        return Images.createMosaic(context, results, size)
    }

    /** [Fetcher.Factory] implementation. */
    class Factory : Fetcher.Factory<Genre> {
        override fun create(data: Genre, options: Options, imageLoader: ImageLoader) =
            GenreImageFetcher(options.context, options.size, data)
    }
}

/**
 * Map at most N [T] items a collection into a collection of [R], ignoring [T] that cannot be
 * transformed into [R].
 * @param n The maximum amount of items to map.
 * @param transform The function that transforms data [T] from the original list into data [R] in
 * the new list. Can return null if the [T] cannot be transformed into an [R].
 * @return A new list of at most N non-null [R] items.
 */
private inline fun <T : Any, R : Any> Collection<T>.mapAtMostNotNull(
    n: Int,
    transform: (T) -> R?
): List<R> {
    val until = min(size, n)
    val out = mutableListOf<R>()

    for (item in this) {
        if (out.size >= until) {
            break
        }

        // Still have more data we can transform.
        transform(item)?.let(out::add)
    }

    return out
}
