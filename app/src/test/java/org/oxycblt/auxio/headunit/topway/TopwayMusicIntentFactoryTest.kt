package org.oxycblt.auxio.headunit.topway

import kotlin.test.Test
import kotlin.test.assertEquals
import org.oxycblt.auxio.headunit.compat.HeadUnitMetadataSnapshot

class TopwayMusicIntentFactoryTest {
    @Test
    fun `metadata intent uses Topway action and keys`() {
        val snapshot = HeadUnitMetadataSnapshot("t", "s", "a", "aa", "al", "d", 1000L, "id", "uri", false, null)
        val intent = TopwayMusicIntentFactory.metadataIntent(snapshot)
        assertEquals(TopwayMusicContract.ACTION_MUSIC_INFO, intent.action)
        assertEquals("t", intent.getStringExtra(TopwayMusicContract.EXTRA_MUSIC_TITLE))
        assertEquals("a", intent.getStringExtra(TopwayMusicContract.EXTRA_MUSIC_ARTIST))
    }
}
