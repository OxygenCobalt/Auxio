package org.oxycblt.auxio.headunit.compat

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HeadUnitCompatRegistryTest {
    @Test
    fun registry_has_expected_features() {
        assertEquals(6, HeadUnitCompatRegistry.candidates.size)
        assertTrue(HeadUnitCompatRegistry.candidates.any { it.feature == HeadUnitCompatFeature.TWTHEME_WIDGET_METADATA })
    }

    @Test
    fun stock_parity_has_no_private_action_dispatch() {
        assertFalse(HeadUnitStockMusicParity.hasUnexpectedActionLeak())
    }
}
