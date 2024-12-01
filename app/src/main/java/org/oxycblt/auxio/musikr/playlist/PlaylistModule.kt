/*
 * Copyright (c) 2023 Auxio Project
 * ExternalModule.kt is part of Auxio.
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
 
package org.oxycblt.auxio.musikr.playlist

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.oxycblt.auxio.musikr.playlist.m3u.M3U
import org.oxycblt.auxio.musikr.playlist.m3u.M3UImpl

@Module
@InstallIn(SingletonComponent::class)
interface PlaylistModule {
    @Binds
    fun externalPlaylistManager(
        externalPlaylistManager: ExternalPlaylistManagerImpl
    ): ExternalPlaylistManager
}
