package org.oxycblt.auxio.headunit.topway

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TopwayProgressStatePolicyTest {
    @Test
    fun `active progress clamps values and rejects unknown duration`() {
        assertEquals(TopwayProgressSnapshot(0L, 5_000L), TopwayProgressStatePolicy.active(-1L, 5_000L))
        assertEquals(TopwayProgressSnapshot(5_000L, 5_000L), TopwayProgressStatePolicy.active(9_000L, 5_000L))
        assertNull(TopwayProgressStatePolicy.active(1_000L, 0L))
        assertNull(TopwayProgressStatePolicy.active(1_000L, -1L))
    }

    @Test
    fun `publish policy allows changes or elapsed interval`() {
        val a = TopwayProgressSnapshot(1_000L, 5_000L)
        val b = TopwayProgressSnapshot(2_000L, 5_000L)
        assertTrue(TopwayProgressStatePolicy.shouldPublish(a, null, 0L, 0L, 1_000L))
        assertTrue(TopwayProgressStatePolicy.shouldPublish(b, a, 100L, 0L, 1_000L))
        assertFalse(TopwayProgressStatePolicy.shouldPublish(a, a, 100L, 0L, 1_000L))
        assertTrue(TopwayProgressStatePolicy.shouldPublish(a, a, 1_000L, 0L, 1_000L))
    }
}
