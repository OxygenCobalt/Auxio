/*
 * Copyright (c) 2023 Auxio Project
 * MusicModule.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import org.oxycblt.musikr.Musikr
import org.oxycblt.musikr.cache.CacheDatabase

@Module
@InstallIn(SingletonComponent::class)
interface MusicModule {
    @Singleton @Binds fun repository(musicRepository: MusicRepositoryImpl): MusicRepository

    @Binds fun settings(musicSettingsImpl: MusicSettingsImpl): MusicSettings
}

@Module
@InstallIn(SingletonComponent::class)
class MusikrShimModule {
    @Singleton
    @Provides
    fun tagDatabase(@ApplicationContext context: Context) = CacheDatabase.from(context)

    @Provides fun musikr(@ApplicationContext context: Context) = Musikr.new(context)
}
