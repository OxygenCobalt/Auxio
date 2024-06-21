/*
 * Copyright (c) 2023 Auxio Project
 * ExtractorModule.kt is part of Auxio.
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
import coil.request.CachePolicy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ExtractorModule {
    @Singleton
    @Provides
    fun imageLoader(
        @ApplicationContext context: Context,
        keyer: CoverKeyer,
        factory: CoverFetcher.Factory
    ) =
        ImageLoader.Builder(context)
            .components {
                // Add fetchers for Music components to make them usable with ImageRequest
                add(keyer)
                add(factory)
            }
            // Use our own crossfade with error drawable support
            .transitionFactory(ErrorCrossfadeTransitionFactory())
            // Not downloading anything, so no disk-caching
            .diskCachePolicy(CachePolicy.DISABLED)
            .build()
}
