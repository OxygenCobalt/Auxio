package org.oxycblt.auxio.headunit.topway

import android.content.Intent
import kotlin.math.max
import org.oxycblt.auxio.headunit.compat.HeadUnitMetadataSnapshot

object TopwayMusicIntentFactory {
    fun metadataIntent(snapshot: HeadUnitMetadataSnapshot): Intent =
        Intent(TopwayMusicContract.ACTION_MUSIC_INFO)
            .putExtra(TopwayMusicContract.EXTRA_MUSIC_TITLE, snapshot.displayTitle)
            .putExtra(TopwayMusicContract.EXTRA_MUSIC_ARTIST, snapshot.artist)
            .putExtra(TopwayMusicContract.EXTRA_MUSIC_ALBUM, snapshot.albumTitle)
            .putExtra(TopwayMusicContract.EXTRA_MUSIC_PATH, snapshot.mediaUri)

    fun progressIntent(progressMs: Long, durationMs: Long): Intent =
        Intent(TopwayMusicContract.ACTION_PROGRESS_DURATION)
            .putExtra(TopwayMusicContract.EXTRA_PROGRESS, max(0L, progressMs))
            .putExtra(TopwayMusicContract.EXTRA_DURATION, max(0L, durationMs))
}
