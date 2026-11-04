/*
 * Copyright (c) 2026 Auxio Project
 * MediaStoreLibrarySeeder.kt is part of Auxio.
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
import org.junit.Test

/**
 * Emulator-only test-setup helper: switches music indexing to MediaStore mode with no filtering,
 * bypassing the SAF folder-picker system dialog entirely (that dialog can't be driven headlessly).
 * Lets emulator runs index whatever MediaStore already sees without any manual folder-selection
 * step. Not meant for the real device, which already has a working SAF-based "Music sources"
 * location configured by hand.
 *
 * This only flips Auxio's own settings -- it doesn't touch MediaStore itself. Whichever fixture set
 * should be indexed (the flat USB/MTE set under `/sdcard/Music/MTE_Test/`, or a richer breadth-test
 * set under a different path) must already be pushed to the device and scanned into MediaStore (adb
 * push + a MEDIA_SCANNER_SCAN_FILE broadcast per file) before this runs, since MediaStore mode
 * indexes everything MediaStore currently knows about, regardless of path.
 */
class MediaStoreLibrarySeeder {
    private val context: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun seedMediaStoreLibrary() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs
            .edit()
            .putInt("auxio_locations_mode", 0xA128) // IntegerTable.LOCATION_MODE_MEDIA_STORE
            .putInt("auxio_filter_mode", 0xA12A) // IntegerTable.FILTER_MODE_EXCLUDE
            .putString("auxio_filtered_locations", "") // exclude nothing -> include everything
            .putBoolean("auxio_exclude_non_music", false)
            .putInt("auxio_force_reload", prefs.getInt("auxio_force_reload", 0) + 1)
            .commit()
    }
}
