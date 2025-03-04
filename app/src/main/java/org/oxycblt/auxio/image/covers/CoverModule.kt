/*
 * Copyright (c) 2024 Auxio Project
 * CoverModule.kt is part of Auxio.
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
 
package org.oxycblt.auxio.image.covers

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.oxycblt.musikr.covers.internal.CoverIdentifier

@Module
@InstallIn(SingletonComponent::class)
interface CoverModule {
    @Binds fun configCovers(impl: SettingCoversImpl): SettingCovers
}

@Module
@InstallIn(SingletonComponent::class)
class CoverProvidesModule {
    @Provides fun identifier(): CoverIdentifier = CoverIdentifier.md5()
}
