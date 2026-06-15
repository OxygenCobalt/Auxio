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
import android.graphics.Bitmap
import java.util.UUID
import javax.inject.Inject
import org.oxycblt.auxio.image.CoverMode
import org.oxycblt.auxio.image.ImageSettings
import org.oxycblt.musikr.covers.Cover
import org.oxycblt.musikr.covers.Covers
import org.oxycblt.musikr.covers.FDCover
import org.oxycblt.musikr.covers.MutableCovers
import org.oxycblt.musikr.covers.chained.ChainedCovers
import org.oxycblt.musikr.covers.chained.MutableChainedCovers
import org.oxycblt.musikr.covers.embedded.CoverIdentifier
import org.oxycblt.musikr.covers.embedded.EmbeddedCovers
import org.oxycblt.musikr.covers.fs.FSCovers
import org.oxycblt.musikr.covers.fs.MutableFSCovers
import org.oxycblt.musikr.covers.stored.Compress
import org.oxycblt.musikr.covers.stored.CoverStorage
import org.oxycblt.musikr.covers.stored.MutableStoredCovers
import org.oxycblt.musikr.covers.stored.NoTranscoding
import org.oxycblt.musikr.covers.stored.StoredCovers

interface SettingCovers {
    suspend fun mutate(context: Context, revision: UUID): MutableCovers<out Cover>

    companion object {
        suspend fun immutable(context: Context): Covers<FDCover> =
            ChainedCovers(StoredCovers(CoverStorage.at(context.coversDir())), FSCovers(context))
    }
}

class SettingCoversImpl @Inject constructor(private val imageSettings: ImageSettings) :
    SettingCovers {
    override suspend fun mutate(context: Context, revision: UUID): MutableCovers<out Cover> {
        val coverStorage = CoverStorage.at(context.coversDir())
        val transcoding =
            when (imageSettings.coverMode) {
                CoverMode.OFF -> return NullCovers(coverStorage)
                CoverMode.SAVE_SPACE -> Compress(Bitmap.CompressFormat.JPEG, 500, 70)
                CoverMode.BALANCED -> Compress(Bitmap.CompressFormat.JPEG, 750, 85)
                CoverMode.HIGH_QUALITY -> Compress(Bitmap.CompressFormat.JPEG, 1000, 100)
                CoverMode.AS_IS -> NoTranscoding
            }
        val revisionedTranscoding = RevisionedTranscoding(revision, transcoding)
        val storedCovers =
            MutableStoredCovers(
                EmbeddedCovers(CoverIdentifier.md5()), coverStorage, revisionedTranscoding)
        val fsCovers = MutableFSCovers(context)
        return MutableChainedCovers(storedCovers, fsCovers)
    }
}

private fun Context.coversDir() = filesDir.resolve("covers").apply { mkdirs() }
