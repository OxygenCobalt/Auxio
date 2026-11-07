/*
 * Copyright (c) 2026 Auxio Project
 * AuxioLaunch.kt is part of Auxio.
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

import android.app.Instrumentation
import android.content.Intent
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

/**
 * Launches the app under test and waits for it to reach the foreground, without
 * `ActivityScenarioRule`. For tests where the app process may legitimately die mid-run (a real
 * native crash is the thing under test) -- `ActivityScenarioRule`'s teardown does instrumentation
 * IPC back to the activity it launched, which is an extra failure mode layered on top of the crash
 * itself once that activity's process is gone.
 */
object AuxioLaunch {
    fun launchAndWaitForForeground(
        device: UiDevice,
        instrumentation: Instrumentation,
        targetPackage: String,
        timeoutMs: Long = 15_000,
    ) {
        val context = instrumentation.targetContext
        val launchIntent =
            context.packageManager.getLaunchIntentForPackage(targetPackage)
                ?: error("No launch intent for $targetPackage")
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchIntent)
        check(device.wait(Until.hasObject(By.pkg(targetPackage).depth(0)), timeoutMs)) {
            "Timed out waiting for $targetPackage to reach the foreground"
        }
    }
}
