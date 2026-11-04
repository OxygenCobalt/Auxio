/*
 * Copyright (c) 2026 Auxio Project
 * SettingsSnapshot.kt is part of Auxio.
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

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import org.junit.Test

/**
 * Two independent instrumentation entry points -- deliberately NOT part of any test class's own
 * JUnit lifecycle -- for snapshotting and restoring every app-level setting (the single default
 * SharedPreferences file) around a risky test run.
 *
 * These are meant to be invoked as two SEPARATE `am instrument` calls from a shell wrapper, with
 * [backup] run before the risky run and [restore] run after in a `trap`/`finally`, specifically so
 * restore still happens even if the run crashes the app process outright and never gets a chance to
 * run its own in-process JUnit teardown. SharedPreferences are backed by a file on disk that
 * survives a process death untouched, so a fresh instrumentation process can always read it back
 * regardless of what killed the previous one.
 *
 * For tests that don't crash the app process on purpose, use [InProcessSettingsBackupRule] instead
 * -- it's a plain JUnit rule and avoids the overhead of two extra `am instrument` calls.
 */
class SettingsSnapshot {
    private val context: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    private val snapshotFile: File
        get() = File(context.getExternalFilesDir(null), "settings_snapshot.tsv")

    // type tag + key + value, tab-separated, one entry per line. Deliberately not JSON -- this
    // avoids relying on org.json's implicit Int/Long/Float/Double coercion rules on the Android
    // version under test, which aren't worth being precise about for a throwaway snapshot format.
    private fun escape(s: String) =
        s.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n")

    private fun unescape(s: String) =
        s.replace("\\n", "\n").replace("\\t", "\t").replace("\\\\", "\\")

    /**
     * Not part of the backup/restore flow itself -- round-trips a known value through
     * backup()/restore() within one test run to confirm the mechanism actually works, since the
     * real on-device SharedPreferences may legitimately be empty right now and wouldn't otherwise
     * exercise this at all.
     */
    @Test
    fun selfTestRoundTrip() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putString("settings_snapshot_self_test_marker", "hello_snapshot").commit()
        backup()
        // Mutate (wrong type, plus a key that didn't exist at backup time) the way a real run
        // would, then confirm restore() undoes exactly that -- per-key, never via clear(), which
        // notifies listeners with key=null and crashes Settings.Impl's own
        // onSharedPreferenceChanged (a real, separate, pre-existing bug this surfaced; not
        // something to work around here beyond simply not calling clear() ourselves).
        prefs
            .edit()
            .remove("settings_snapshot_self_test_marker")
            .putInt("settings_snapshot_self_test_marker", 12345)
            .putBoolean("settings_snapshot_self_test_extra_key", true)
            .commit()
        restore()
        val restored = PreferenceManager.getDefaultSharedPreferences(context).all
        check(restored["settings_snapshot_self_test_marker"] == "hello_snapshot") {
            "Round-trip failed: expected 'hello_snapshot', got " +
                "${restored["settings_snapshot_self_test_marker"]}"
        }
        check(!restored.containsKey("settings_snapshot_self_test_extra_key")) {
            "Round-trip failed: restore() should have removed a key absent from the snapshot"
        }
        prefs.edit().remove("settings_snapshot_self_test_marker").commit()
    }

    @Test
    fun backup() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val lines =
            prefs.all.entries.map { (key, value) ->
                val (tag, text) =
                    when (value) {
                        is Boolean -> "BOOLEAN" to value.toString()
                        is Int -> "INT" to value.toString()
                        is Long -> "LONG" to value.toString()
                        is Float -> "FLOAT" to value.toString()
                        is String -> "STRING" to value
                        is Set<*> ->
                            "STRINGSET" to value.joinToString("\u0001") { escape(it as String) }
                        else ->
                            error("Unexpected SharedPreferences value type: ${value?.javaClass}")
                    }
                "$tag\t${escape(key)}\t${escape(text)}"
            }
        snapshotFile.parentFile?.mkdirs()
        snapshotFile.writeText(lines.joinToString("\n"))
        check(snapshotFile.exists() && snapshotFile.length() >= 0) {
            "Failed to write settings snapshot to $snapshotFile"
        }
    }

    @Test
    fun restore() {
        check(snapshotFile.exists()) {
            "No settings snapshot at $snapshotFile -- run backup() before the risky run first"
        }
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        val snapshotKeys = mutableSetOf<String>()
        snapshotFile
            .readText()
            .lineSequence()
            .filter { it.isNotEmpty() }
            .forEach { line ->
                val parts = line.split("\t", limit = 3)
                val tag = parts[0]
                val key = unescape(parts[1])
                val text = unescape(parts[2])
                snapshotKeys += key
                when (tag) {
                    "BOOLEAN" -> editor.putBoolean(key, text.toBoolean())
                    "INT" -> editor.putInt(key, text.toInt())
                    "LONG" -> editor.putLong(key, text.toLong())
                    "FLOAT" -> editor.putFloat(key, text.toFloat())
                    "STRING" -> editor.putString(key, text)
                    "STRINGSET" ->
                        editor.putStringSet(
                            key,
                            if (text.isEmpty()) emptySet()
                            else text.split("\u0001").map { unescape(it) }.toSet(),
                        )
                    else -> error("Unknown snapshot type tag: $tag")
                }
            }
        // Deliberately never clear() -- it notifies listeners with key=null, which crashes
        // Settings.Impl.onSharedPreferenceChanged's unlikelyToBeNull(key) (a real, separate,
        // pre-existing bug this suite's own setup surfaced). Remove only keys the test run added
        // that weren't in the snapshot; every removal/put below still notifies with a real key.
        for (key in prefs.all.keys - snapshotKeys) {
            editor.remove(key)
        }
        // commit(), not apply() -- this process may be torn down by the test harness immediately
        // after this instrumentation call returns, so the write needs to be synchronously durable.
        check(editor.commit()) { "SharedPreferences restore failed to commit" }
    }
}
