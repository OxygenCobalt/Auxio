/*
 * Copyright (c) 2026 Auxio Project
 * AuxioUiAutomator.kt is part of Auxio.
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
 
package org.oxycblt.auxio.testutil

import androidx.annotation.IdRes
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Configurator
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

/**
 * Drives the app under test via UiAutomator instead of Espresso's `onView(...).perform(...)`.
 *
 * Espresso's `perform()`/`check()` wait for the app's main-thread Looper to go idle before acting.
 * Auxio's playback screens continuously repost position updates to the main thread while something
 * is playing (`PlaybackViewModel.positionDs`, collected in `PlaybackBarFragment.kt`), so that wait
 * never completes and every Espresso interaction hangs indefinitely once playback starts -- which
 * is most of the app, most of the time, since the mini-player bar is visible app-wide. UiAutomator
 * injects input at the system/ AccessibilityService level and never waits on the app's own main
 * thread, so it works regardless of what the app is doing.
 */
class AuxioUiAutomator(private val device: UiDevice, private val targetPackage: String) {
    private val resources = InstrumentationRegistry.getInstrumentation().targetContext.resources

    init {
        // UiAutomator has its OWN internal "wait for idle" step (distinct from Espresso's, but
        // the same underlying problem) that most read/interaction calls perform before acting --
        // by default up to ~10s. The screen never goes idle while something is playing (the same
        // continuously-reposting positionDs Flow that breaks Espresso), so every single
        // interaction was eating the full default timeout before proceeding, even though it
        // still eventually succeeded (confirmed: one 30-80 cycle scenario took ~700s instead of
        // the expected ~1 minute). UiAutomator doesn't actually need idle to read the
        // accessibility tree correctly, so cut this down to near-zero.
        Configurator.getInstance().waitForIdleTimeout = 100
    }

    private fun resSelector(@IdRes id: Int) =
        By.res(targetPackage, resources.getResourceEntryName(id))

    fun waitForRes(@IdRes id: Int, timeoutMs: Long = 10_000): UiObject2 =
        device.wait(Until.findObject(resSelector(id)), timeoutMs)
            ?: error(
                "Timed out waiting for resource-id ${resources.getResourceEntryName(id)}. " +
                    dumpDiagnostics()
            )

    fun waitForText(text: String, timeoutMs: Long = 10_000): UiObject2 =
        device.wait(Until.findObject(By.text(text)), timeoutMs)
            ?: error("Timed out waiting for text \"$text\". " + dumpDiagnostics())

    fun waitForDesc(desc: String, timeoutMs: Long = 10_000): UiObject2 =
        device.wait(Until.findObject(By.desc(desc)), timeoutMs)
            ?: error("Timed out waiting for content-description \"$desc\". " + dumpDiagnostics())

    /**
     * A short summary of what's actually on screen, appended to timeout errors so a failure is
     * diagnosable from the test-run log alone, without needing a live follow-up `uiautomator dump`
     * (which can't be taken after the fact -- by the time a run finishes and its UiAutomation
     * connection tears down, the system has often already returned to the launcher, erasing exactly
     * the state that mattered).
     */
    private fun dumpDiagnostics(): String {
        val topActivity =
            device
                .executeShellCommand("dumpsys activity activities")
                .lineSequence()
                .firstOrNull { it.contains("topResumedActivity") }
                ?.trim()
        val onScreenTexts =
            device.findObjects(By.pkg(targetPackage)).mapNotNull { it.text }.distinct().take(15)
        return "Top activity: $topActivity. Visible text in $targetPackage: $onScreenTexts"
    }

    fun clickRes(@IdRes id: Int, timeoutMs: Long = 10_000) {
        waitForRes(id, timeoutMs).click()
    }

    fun clickText(text: String, timeoutMs: Long = 10_000) {
        waitForText(text, timeoutMs).click()
    }

    fun clickDesc(desc: String, timeoutMs: Long = 10_000) {
        waitForDesc(desc, timeoutMs).click()
    }

    /** A real swipe gesture within the target's own bounds, not a programmatic value set. */
    fun swipeLeftOnRes(@IdRes id: Int, percent: Float = 0.75f, timeoutMs: Long = 10_000) {
        waitForRes(id, timeoutMs).swipe(Direction.LEFT, percent)
    }

    fun swipeRightOnRes(@IdRes id: Int, percent: Float = 0.75f, timeoutMs: Long = 10_000) {
        waitForRes(id, timeoutMs).swipe(Direction.RIGHT, percent)
    }

    fun typeTextInRes(@IdRes id: Int, text: String, timeoutMs: Long = 10_000) {
        waitForRes(id, timeoutMs).setText(text)
    }

    /** Scrolls the given scrollable container until [text] is visible, if it isn't already. */
    fun scrollToText(@IdRes scrollableId: Int, text: String, timeoutMs: Long = 10_000): Boolean {
        val container = waitForRes(scrollableId, timeoutMs)
        return container.scrollUntil(Direction.DOWN, Until.findObject(By.text(text))) != null ||
            device.hasObject(By.text(text))
    }

    fun existsRes(@IdRes id: Int): Boolean = device.hasObject(resSelector(id))

    /**
     * Presses Back until [targetRes] is visible, up to [maxPresses] times. Used to recover a known
     * starting screen between tests -- launching the app only brings it to the foreground, it
     * doesn't reset whatever screen/dialog/panel a previous test left it on.
     */
    fun pressBackUntilRes(@IdRes targetRes: Int, maxPresses: Int = 5) {
        repeat(maxPresses) {
            if (existsRes(targetRes)) return
            device.pressBack()
            Thread.sleep(300)
        }
        check(existsRes(targetRes)) {
            "Still didn't find ${resources.getResourceEntryName(targetRes)} after $maxPresses" +
                " back presses"
        }
    }

    fun existsText(text: String): Boolean = device.hasObject(By.text(text))
}
