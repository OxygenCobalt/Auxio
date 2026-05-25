/*
 * Copyright (c) 2024 Auxio Project
 * TopwayMusicIntentFactory.kt is part of Auxio.
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

package org.oxycblt.auxio.headunit.topway

import android.content.Intent
import kotlin.math.max
import org.oxycblt.auxio.headunit.compat.HeadUnitMetadataSnapshot

object TopwayMusicIntentFactory {
    fun metadataIntent(snapshot: HeadUnitMetadataSnapshot): Intent =
        Intent(TopwayMusicContract.ACTION_MUSIC_INFO).apply {
            metadataExtras(snapshot).forEach { (key, value) -> putExtra(key, value) }
        }

    fun progressIntent(progressMs: Long, durationMs: Long): Intent =
        Intent(TopwayMusicContract.ACTION_PROGRESS_DURATION).apply {
            progressExtras(progressMs, durationMs).forEach { (key, value) -> putExtra(key, value) }
        }

    internal fun metadataExtras(snapshot: HeadUnitMetadataSnapshot): Map<String, String?> =
        mapOf(
            TopwayMusicContract.EXTRA_MUSIC_TITLE to snapshot.displayTitle,
            TopwayMusicContract.EXTRA_MUSIC_ARTIST to snapshot.artist,
            TopwayMusicContract.EXTRA_MUSIC_ALBUM to snapshot.albumTitle,
            TopwayMusicContract.EXTRA_MUSIC_PATH to snapshot.mediaUri,
        )

    internal fun progressExtras(progressMs: Long, durationMs: Long): Map<String, Long> =
        mapOf(
            TopwayMusicContract.EXTRA_PROGRESS to max(0L, progressMs),
            TopwayMusicContract.EXTRA_DURATION to max(0L, durationMs),
        )
}
