/*
 * Copyright (c) 2026 Auxio Project
 * InProcessSettingsBackupRule.kt is part of Auxio.
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
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Snapshots the app's SharedPreferences before a test and restores them after, in-process via a
 * plain JUnit rule. For tests where the app process may itself die mid-run (so this rule's
 * `finished()` would never get a chance to execute), use [SettingsSnapshot] instead -- its heavier
 * two-process, file-based mechanism is built specifically to survive that.
 */
class InProcessSettingsBackupRule : TestWatcher() {
    private val prefs by lazy {
        PreferenceManager.getDefaultSharedPreferences(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
    }
    private var snapshot: Map<String, Any?> = emptyMap()

    /** Exposed (not just `starting()`) so [InProcessSettingsBackupRuleSelfTest] can drive it. */
    fun captureSnapshot() {
        snapshot = prefs.all.toMap()
    }

    /** Exposed (not just `finished()`) so [InProcessSettingsBackupRuleSelfTest] can drive it. */
    fun restoreSnapshot() {
        val editor = prefs.edit()
        // Never clear() -- it notifies listeners with key=null, which crashes
        // Settings.Impl.onSharedPreferenceChanged's unlikelyToBeNull(key) (a real, separate,
        // pre-existing bug; see SettingsSnapshot.restore for the same note).
        for (key in prefs.all.keys - snapshot.keys) {
            editor.remove(key)
        }
        for ((key, value) in snapshot) {
            when (value) {
                is Boolean -> editor.putBoolean(key, value)
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is Float -> editor.putFloat(key, value)
                is String -> editor.putString(key, value)
                is Set<*> -> {
                    @Suppress("UNCHECKED_CAST") val strings = value as Set<String>
                    editor.putStringSet(key, strings)
                }
                else -> error("Unexpected SharedPreferences value type: ${value?.javaClass}")
            }
        }
        editor.commit()
    }

    override fun starting(description: Description) = captureSnapshot()

    override fun finished(description: Description) = restoreSnapshot()
}
