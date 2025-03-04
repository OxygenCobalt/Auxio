/*
 * Copyright (c) 2024 Auxio Project
 * SettingCovers.kt is part of Auxio.
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

import android.content.Context
import java.util.UUID
import javax.inject.Inject
import org.oxycblt.auxio.image.CoverMode
import org.oxycblt.auxio.image.ImageSettings
import org.oxycblt.musikr.covers.Cover
import org.oxycblt.musikr.covers.Covers
import org.oxycblt.musikr.covers.MutableCovers
import org.oxycblt.musikr.covers.fs.FSCovers
import org.oxycblt.musikr.covers.fs.MutableFSCovers
import org.oxycblt.musikr.covers.internal.CoverIdentifier
import org.oxycblt.musikr.covers.internal.CoverParams
import org.oxycblt.musikr.covers.internal.FileCover

interface SettingCovers {
    suspend fun mutate(context: Context, revision: UUID): MutableCovers<out Cover>

    companion object {
        fun immutable(context: Context): Covers<FileCover> =
            Covers.chain(BaseSiloedCovers(context), FSCovers(context))
    }
}

class SettingCoversImpl
@Inject
constructor(private val imageSettings: ImageSettings, private val identifier: CoverIdentifier) :
    SettingCovers {
    override suspend fun mutate(context: Context, revision: UUID): MutableCovers<out Cover> =
        when (imageSettings.coverMode) {
            CoverMode.OFF -> NullCovers(context)
            CoverMode.SAVE_SPACE -> siloedCovers(context, revision, CoverParams.of(500, 70))
            CoverMode.BALANCED -> siloedCovers(context, revision, CoverParams.of(750, 85))
            CoverMode.HIGH_QUALITY -> siloedCovers(context, revision, CoverParams.of(1000, 100))
            CoverMode.AS_IS -> siloedCovers(context, revision, null)
        }

    private suspend fun siloedCovers(context: Context, revision: UUID, with: CoverParams?) =
        MutableCovers.chain(
            MutableSiloedCovers.from(context, CoverSilo(revision, with), identifier),
            MutableFSCovers(context))
}
