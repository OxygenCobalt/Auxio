/*
 * Copyright (c) 2023 Auxio Project
 * FsModule.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.fs

import android.content.ContentResolver
import android.content.Context
import android.os.storage.StorageManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.oxycblt.auxio.util.getSystemServiceCompat

@Module
@InstallIn(SingletonComponent::class)
class FsModule {
    @Provides
    fun volumeManager(@ApplicationContext context: Context): VolumeManager =
        VolumeManagerImpl(context.getSystemServiceCompat(StorageManager::class))

    @Provides
    fun mediaStoreExtractor(
        @ApplicationContext context: Context,
        mediaStorePathInterpreterFactory: MediaStorePathInterpreter.Factory
    ) = MediaStoreExtractor.from(context, mediaStorePathInterpreterFactory)

    @Provides
    fun mediaStorePathInterpreterFactory(
        volumeManager: VolumeManager
    ): MediaStorePathInterpreter.Factory = MediaStorePathInterpreter.Factory.from(volumeManager)

    @Provides
    fun contentResolver(@ApplicationContext context: Context): ContentResolver =
        context.contentResolverSafe
}

@Module
@InstallIn(SingletonComponent::class)
interface FsBindsModule {
    @Binds
    fun documentPathFactory(documentTreePathFactory: DocumentPathFactoryImpl): DocumentPathFactory
}
