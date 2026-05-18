package org.oxycblt.auxio.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class UISettingsDriverSideTest {
    @Test
    fun from_unknown_defaultsToRight() {
        assertEquals(UISettings.DriverSide.RIGHT, UISettings.DriverSide.from(0))
    }

    @Test
    fun from_right_constant_isRight() {
        assertEquals(UISettings.DriverSide.RIGHT, UISettings.DriverSide.from(1))
    }

    @Test
    fun from_left_constant_isLeft() {
        assertEquals(UISettings.DriverSide.LEFT, UISettings.DriverSide.from(2))
    }
}
