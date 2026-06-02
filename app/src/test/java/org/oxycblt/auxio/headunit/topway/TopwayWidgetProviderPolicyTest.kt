/*
 * Copyright (c) 2026 Auxio Project
 * TopwayWidgetProviderPolicyTest.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package org.oxycblt.auxio.headunit.topway

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.oxycblt.auxio.widgets.WidgetProvider

class TopwayWidgetProviderPolicyTest {
    @Test
    fun standardVariantChecksOnlyNormalAuxioProvider() {
        assertEquals(
            listOf(WidgetProvider::class.java.name),
            TopwayWidgetProviderPolicy.providerClassNames(topwayCompatFlavor = false),
        )
    }

    @Test
    fun topwayVariantsCheckNormalAndStockNameWrapperProviders() {
        assertEquals(
            listOf(WidgetProvider::class.java.name, "com.tw.music.view.MusicWidgetProvider"),
            TopwayWidgetProviderPolicy.providerClassNames(topwayCompatFlavor = true),
        )
    }

    @Test
    fun standardVariantRequiresWidgetInstanceForUpdate() {
        assertFalse(
            TopwayWidgetProviderPolicy.shouldHandleTopwayUpdate(
                topwayCompatFlavor = false,
                hasAnyWidgetInstances = false,
            )
        )
        assertTrue(
            TopwayWidgetProviderPolicy.shouldHandleTopwayUpdate(
                topwayCompatFlavor = false,
                hasAnyWidgetInstances = true,
            )
        )
    }

    @Test
    fun topwayVariantsServeUpdateEvenWithoutNormalAppWidgetInstance() {
        assertTrue(
            TopwayWidgetProviderPolicy.shouldHandleTopwayUpdate(
                topwayCompatFlavor = true,
                hasAnyWidgetInstances = false,
            )
        )
    }
}
