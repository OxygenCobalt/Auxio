/*
 * Copyright (c) 2026 Auxio Project
 * UsbDacStressTest.kt is part of Auxio.
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
 
package org.oxycblt.auxio.usb

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import kotlin.random.Random
import org.junit.Before
import org.junit.Test
import org.oxycblt.auxio.R
import org.oxycblt.auxio.testutil.AuxioLaunch
import org.oxycblt.auxio.testutil.AuxioUiAutomator

/**
 * Repeats the real-hardware playback-stress scenarios that have historically surfaced bugs in this
 * project (format switching, pause/resume, seeking, flipping the USB routing settings mid- track)
 * against the actually-running app, with random cycle counts so repeat runs vary timing instead of
 * hammering the exact same sequence every time.
 *
 * All interaction goes through [AuxioUiAutomator] (UiDevice/By), never Espresso's `onView()` --
 * Espresso's `.perform()`/`.check()` wait for the main-thread Looper to go idle first, and
 * `PlaybackViewModel.positionDs` continuously reposts to the main thread while something plays, so
 * that wait never completes. Confirmed via `dumpsys input` showing zero new touch events dispatched
 * for 5+ minutes when this test was still Espresso-based.
 *
 * Launch is done manually via [AuxioLaunch], not `ActivityScenarioRule` -- the app process may
 * genuinely die mid-test (that's the point of the MTE instrumentation this pairs with), and
 * `ActivityScenarioRule`'s teardown does instrumentation IPC back to the activity it launched, an
 * unnecessary extra failure mode on top of the crash itself once that activity's process is gone.
 *
 * This is a developer stress tool, not a from-scratch CI suite: it assumes a real USB DAC is
 * already attached and activated, and that a queue is already playing when the suite starts (it
 * stress-tests an *active* session; it doesn't set one up from an empty library). Build and run it
 * with the native libraries instrumented for Arm MTE (see libusbdac/src/main/cpp/CMakeLists.txt and
 * media/libraries/decoder_ffmpeg/src/main/jni/CMakeLists.txt's debug-only sanitize=memtag flags,
 * plus app/src/debug/AndroidManifest.xml's memtagMode) on a real MTE-capable device (e.g. Pixel 8+)
 * with Settings > Developer options > Memory Tagging Extension enabled and the device rebooted, for
 * this to actually be able to catch a native memory-safety bug rather than just a Kotlin-level
 * crash. On a non-MTE device (e.g. the x86_64 emulator), this still validates the harness itself --
 * clicks land, cycles complete, nothing hangs -- just not real ARM memory tagging.
 *
 * Run with: ./gradlew :app:connectedDebugAndroidTest
 * -Pandroid.testInstrumentationRunnerArguments.class=org.oxycblt.auxio.usb.UsbDacStressTest
 *
 * Deliberately NOT covered here: physically unplugging/replugging the DAC. There's no software hook
 * to simulate a real USB disconnect on an attached device without extra relay hardware -- that
 * stays a manual step.
 */
class UsbDacStressTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device: UiDevice = UiDevice.getInstance(instrumentation)
    private val targetPackage = instrumentation.targetContext.packageName
    private val ui = AuxioUiAutomator(device, targetPackage)

    @Before
    fun launch() {
        AuxioLaunch.launchAndWaitForForeground(device, instrumentation, targetPackage)
        // A previous test may have left the panel expanded or a dialog/settings screen open --
        // get back to a known starting point (Home, panel collapsed) before this test's own
        // assumptions about what's on screen take over.
        ui.pressBackUntilRes(R.id.home_toolbar)
    }

    private fun currentPid(): String = device.executeShellCommand("pidof $targetPackage").trim()

    private fun clearCrashBuffer() {
        device.executeShellCommand("logcat -b crash -c")
    }

    /**
     * Clicks the mini player bar to expand it into the full panel (skip/seek controls live there).
     */
    private fun expandToPanel() {
        ui.clickRes(R.id.playback_bar_fragment)
        Thread.sleep(500)
    }

    private fun assertNoCrash(pidBefore: String, scenario: String) {
        val pidAfter = currentPid()
        check(pidAfter.isNotEmpty()) {
            "$scenario: app process is gone (was pid=$pidBefore) -- it crashed"
        }
        check(pidAfter == pidBefore) {
            "$scenario: app process restarted (pid $pidBefore -> $pidAfter) -- treat as a crash" +
                " even though a new process is now up"
        }
        val crashLog = device.executeShellCommand("logcat -d -b crash")
        check(crashLog.isBlank()) { "$scenario: new entries in the crash log buffer:\n$crashLog" }
    }

    @Test
    fun stressFormatSwitching() {
        expandToPanel()
        val pid = currentPid()
        clearCrashBuffer()
        val cycles = Random.nextInt(30, 81)
        repeat(cycles) {
            ui.clickRes(R.id.playback_skip_next)
            Thread.sleep(Random.nextLong(200, 600))
        }
        assertNoCrash(pid, "stressFormatSwitching ($cycles cycles)")
    }

    @Test
    fun stressPauseResume() {
        expandToPanel()
        val pid = currentPid()
        clearCrashBuffer()
        val cycles = Random.nextInt(50, 151)
        repeat(cycles) {
            ui.clickRes(R.id.playback_play_pause)
            Thread.sleep(Random.nextLong(100, 400))
        }
        assertNoCrash(pid, "stressPauseResume ($cycles cycles)")
    }

    @Test
    fun stressSeeking() {
        expandToPanel()
        val pid = currentPid()
        clearCrashBuffer()
        val cycles = Random.nextInt(20, 51)
        repeat(cycles) {
            // A real finger-drag on the seek bar -- this is what actually fires
            // StyledSeekBar.onStopTrackingTouch() -> listener.onSeekConfirmed() ->
            // playbackModel.seekTo(); setting .positionDs programmatically would NOT, since that
            // only moves the visual thumb without going through the touch-listener dispatch path.
            // Swipes target playback_seek_bar (the StyledSeekBar container), not the internal
            // Slider child (R.id.seek_bar_slider) -- Material's Slider exposes its thumb/track
            // to accessibility as virtual nodes on its own view rather than as a separately
            // addressable child, so seek_bar_slider never appears in the accessibility tree
            // UiAutomator reads (confirmed via a live `uiautomator dump` while the panel was
            // open: seek_bar_position/seek_bar_duration showed up as siblings, seek_bar_slider
            // never did).
            if (Random.nextBoolean()) {
                ui.swipeLeftOnRes(R.id.playback_seek_bar)
            } else {
                ui.swipeRightOnRes(R.id.playback_seek_bar)
            }
            Thread.sleep(Random.nextLong(150, 450))
        }
        assertNoCrash(pid, "stressSeeking ($cycles cycles)")
    }

    @Test
    fun stressRoutingToggle() {
        // No need to expand the panel -- Settings is reached directly from Home, and playback
        // (already active per this suite's precondition) keeps running underneath it.
        val pid = currentPid()
        clearCrashBuffer()
        ui.clickDesc("More")
        ui.clickText(instrumentation.targetContext.getString(R.string.set_root_title))
        ui.clickText(instrumentation.targetContext.getString(R.string.set_usb))
        val cycles = Random.nextInt(10, 26)
        repeat(cycles) {
            ui.clickText(instrumentation.targetContext.getString(R.string.set_usb_enabled))
            Thread.sleep(Random.nextLong(400, 1000))
            ui.clickText(instrumentation.targetContext.getString(R.string.set_usb_bit_perfect))
            Thread.sleep(Random.nextLong(400, 1000))
        }
        assertNoCrash(pid, "stressRoutingToggle ($cycles cycles)")
    }
}
