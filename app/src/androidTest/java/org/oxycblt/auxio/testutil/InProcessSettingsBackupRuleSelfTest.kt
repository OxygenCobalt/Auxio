/*
 * Copyright (c) 2026 Auxio Project
 * InProcessSettingsBackupRuleSelfTest.kt is part of Auxio.
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

import androidx.preference.PreferenceManager
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test

/**
 * Drives [InProcessSettingsBackupRule]'s capture/restore directly (rather than applying it as a
 * `@Rule`) to round-trip a known value within one test run, the same way
 * [SettingsSnapshot.selfTestRoundTrip] verifies its own mechanism.
 */
class InProcessSettingsBackupRuleSelfTest {
    @Test
    fun selfTestRoundTrip() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putString("in_process_rule_self_test_marker", "hello_rule").commit()

        val rule = InProcessSettingsBackupRule()
        rule.captureSnapshot()
        prefs
            .edit()
            .remove("in_process_rule_self_test_marker")
            .putInt("in_process_rule_self_test_marker", 12345)
            .putBoolean("in_process_rule_self_test_extra_key", true)
            .commit()
        rule.restoreSnapshot()

        val restored = PreferenceManager.getDefaultSharedPreferences(context).all
        check(restored["in_process_rule_self_test_marker"] == "hello_rule") {
            "Round-trip failed: expected 'hello_rule', got " +
                "${restored["in_process_rule_self_test_marker"]}"
        }
        check(!restored.containsKey("in_process_rule_self_test_extra_key")) {
            "Round-trip failed: restore should have removed a key absent from the snapshot"
        }
        prefs.edit().remove("in_process_rule_self_test_marker").commit()
    }
}
