/*
 * Copyright (c) 2021 Auxio Project
 * Components.kt is part of Auxio.
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
import javax.inject.Inject
import kotlin.math.min
import okio.buffer
import okio.source
import org.oxycblt.auxio.list.Sort
import org.oxycblt.auxio.music.*

/**
 * A [Keyer] implementation for [Music] data.
 *
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
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class AlbumCoverFetcher
private constructor(
    private val context: Context,
    private val extractor: CoverExtractor,
    private val album: Album
) : Fetcher {
    override suspend fun fetch(): FetchResult? =
        extractor.extract(album)?.run {
            SourceResult(
                source = ImageSource(source().buffer(), context),
                mimeType = null,
                dataSource = DataSource.DISK)
        }

    class SongFactory @Inject constructor(private val coverExtractor: CoverExtractor) :
        Fetcher.Factory<Song> {
        override fun create(data: Song, options: Options, imageLoader: ImageLoader) =
            AlbumCoverFetcher(options.context, coverExtractor, data.album)
    }

    class AlbumFactory @Inject constructor(private val coverExtractor: CoverExtractor) :
        Fetcher.Factory<Album> {
        override fun create(data: Album, options: Options, imageLoader: ImageLoader) =
            AlbumCoverFetcher(options.context, coverExtractor, data)
    }
}

/**
 * [Fetcher] for [Artist] images. Use [Factory] for instantiation.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class ArtistImageFetcher
private constructor(
    private val context: Context,
    private val extractor: CoverExtractor,
    private val size: Size,
    private val artist: Artist
) : Fetcher {
    override suspend fun fetch(): FetchResult? {
        // Pick the "most prominent" albums (i.e albums with the most songs) to show in the image.
        val albums = Sort(Sort.Mode.ByCount, Sort.Direction.DESCENDING).albums(artist.albums)
        val results = albums.mapAtMostNotNull(4) { album -> extractor.extract(album) }
        return Images.createMosaic(context, results, size)
    }

    class Factory @Inject constructor(private val extractor: CoverExtractor) :
        Fetcher.Factory<Artist> {
        override fun create(data: Artist, options: Options, imageLoader: ImageLoader) =
            ArtistImageFetcher(options.context, extractor, options.size, data)
    }
}

/**
 * [Fetcher] for [Genre] images. Use [Factory] for instantiation.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class GenreImageFetcher
private constructor(
    private val context: Context,
    private val extractor: CoverExtractor,
    private val size: Size,
    private val genre: Genre
) : Fetcher {
    override suspend fun fetch(): FetchResult? {
        val results = genre.albums.mapAtMostNotNull(4) { album -> extractor.extract(album) }
        return Images.createMosaic(context, results, size)
    }

    class Factory @Inject constructor(private val extractor: CoverExtractor) :
        Fetcher.Factory<Genre> {
        override fun create(data: Genre, options: Options, imageLoader: ImageLoader) =
            GenreImageFetcher(options.context, extractor, options.size, data)
    }
}

/**
 * [Fetcher] for [Playlist] images. Use [Factory] for instantiation.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class PlaylistImageFetcher
private constructor(
    private val context: Context,
    private val extractor: CoverExtractor,
    private val size: Size,
    private val playlist: Playlist
) : Fetcher {
    override suspend fun fetch(): FetchResult? {
        val results = playlist.albums.mapAtMostNotNull(4) { album -> extractor.extract(album) }
        return Images.createMosaic(context, results, size)
    }

    class Factory @Inject constructor(private val extractor: CoverExtractor) :
        Fetcher.Factory<Playlist> {
        override fun create(data: Playlist, options: Options, imageLoader: ImageLoader) =
            PlaylistImageFetcher(options.context, extractor, options.size, data)
    }
}

/**
 * Map at most N [T] items a collection into a collection of [R], ignoring [T] that cannot be
 * transformed into [R].
 *
 * @param n The maximum amount of items to map.
 * @param transform The function that transforms data [T] from the original list into data [R] in
 *   the new list. Can return null if the [T] cannot be transformed into an [R].
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
