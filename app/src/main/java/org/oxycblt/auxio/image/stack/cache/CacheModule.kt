/*
 * Copyright (c) 2024 Auxio Project
 * CacheModule.kt is part of Auxio.
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
 
package org.oxycblt.auxio.image.stack.cache

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface StackModule {
    @Singleton @Binds fun appFiles(impl: AppFilesImpl): AppFiles

    @Binds fun perceptualHash(perceptualHash: PerceptualHashImpl): PerceptualHash

    @Binds fun coverCache(cache: CoverCacheImpl): CoverCache
}

@Module
@InstallIn(SingletonComponent::class)
class StoredCoversDatabaseModule {
    @Provides fun storedCoversDao(database: StoredCoversDatabase) = database.storedCoversDao()

    @Singleton
    @Provides
    fun database(@ApplicationContext context: Context) =
        Room.databaseBuilder(
                context.applicationContext, StoredCoversDatabase::class.java, "stored_covers.db")
            .fallbackToDestructiveMigration()
            .build()
}
