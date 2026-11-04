/*
 * Copyright (c) 2026 Auxio Project
 * HomeBrowsingUiTest.kt is part of Auxio.
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
 
package org.oxycblt.auxio.home

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.oxycblt.auxio.R
import org.oxycblt.auxio.testutil.AuxioLaunch
import org.oxycblt.auxio.testutil.AuxioUiAutomator
import org.oxycblt.auxio.testutil.InProcessSettingsBackupRule

/**
 * Minimal smoke test proving the [AuxioUiAutomator] approach built for [org.oxycblt.auxio.usb]'s
 * stress suite generalizes to ordinary screens with no playback precondition -- this test never
 * starts playback, deliberately, to confirm the harness works outside the one screen it was first
 * built against.
 */
class HomeBrowsingUiTest {
    @get:Rule val settingsBackup = InProcessSettingsBackupRule()

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device: UiDevice = UiDevice.getInstance(instrumentation)
    private val targetPackage = instrumentation.targetContext.packageName
    private val ui = AuxioUiAutomator(device, targetPackage)

    @Before
    fun launch() {
        AuxioLaunch.launchAndWaitForForeground(device, instrumentation, targetPackage)
        ui.pressBackUntilRes(R.id.home_toolbar)
    }

    private fun currentPid(): String = device.executeShellCommand("pidof $targetPackage").trim()

    @Test
    fun switchThroughHomeTabs() {
        val pid = currentPid()
        ui.waitForRes(R.id.home_toolbar)

        for (tabText in listOf("Albums", "Artists", "Genres", "Playlists", "Songs")) {
            ui.clickText(tabText)
            Thread.sleep(400)
        }

        val pidAfter = currentPid()
        check(pidAfter.isNotEmpty() && pidAfter == pid) {
            "switchThroughHomeTabs: app process died or restarted (pid $pid -> $pidAfter)"
        }
    }
}
