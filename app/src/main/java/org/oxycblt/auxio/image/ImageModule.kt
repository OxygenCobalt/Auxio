/*
 * Copyright (c) 2023 Auxio Project
 * ImageModule.kt is part of Auxio.
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
 
package org.oxycblt.auxio.image

import android.content.Context
import coil.ImageLoader
import coil.request.CachePolicy
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import org.oxycblt.auxio.image.extractor.*

@Module
@InstallIn(SingletonComponent::class)
interface ImageModule {
    @Binds fun settings(imageSettings: ImageSettingsImpl): ImageSettings
}

@Module
@InstallIn(SingletonComponent::class)
class CoilModule {
    @Singleton
    @Provides
    fun imageLoader(
        @ApplicationContext context: Context,
        songFactory: AlbumCoverFetcher.SongFactory,
        albumFactory: AlbumCoverFetcher.AlbumFactory,
        artistFactory: ArtistImageFetcher.Factory,
        genreFactory: GenreImageFetcher.Factory,
        playlistFactory: PlaylistImageFetcher.Factory
    ) =
        ImageLoader.Builder(context)
            .components {
                // Add fetchers for Music components to make them usable with ImageRequest
                add(MusicKeyer())
                add(songFactory)
                add(albumFactory)
                add(artistFactory)
                add(genreFactory)
                add(playlistFactory)
            }
            // Use our own crossfade with error drawable support
            .transitionFactory(ErrorCrossfadeTransitionFactory())
            // Not downloading anything, so no disk-caching
            .diskCachePolicy(CachePolicy.DISABLED)
            .build()
}
