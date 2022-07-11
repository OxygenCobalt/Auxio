/*
 * Copyright (c) 2021 Auxio Project
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
 
package org.oxycblt.auxio.settings

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.os.storage.StorageManager
import android.util.Log
import androidx.core.content.edit
import java.io.File
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Directory
import org.oxycblt.auxio.music.directoryCompat
import org.oxycblt.auxio.music.isInternalCompat
import org.oxycblt.auxio.music.storageVolumesCompat
import org.oxycblt.auxio.ui.accent.Accent
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.queryAll

// A couple of utils for migrating from old settings values to the new formats.
// Usually, these will last for 6 months before being removed.

fun handleAccentCompat(context: Context, prefs: SharedPreferences): Accent {
    val currentKey = context.getString(R.string.set_key_accent)

    if (prefs.contains(OldKeys.KEY_ACCENT3)) {
        Log.d("Auxio.SettingsCompat", "Migrating ${OldKeys.KEY_ACCENT3}")

        var accent = prefs.getInt(OldKeys.KEY_ACCENT3, 5)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Accents were previously frozen as soon as the OS was updated to android twelve,
            // as dynamic colors were enabled by default. This is no longer the case, so we need
            // to re-update the setting to dynamic colors here.
            accent = 16
        }

        prefs.edit {
            putInt(currentKey, accent)
            remove(OldKeys.KEY_ACCENT3)
            apply()
        }
    }

    return Accent.from(prefs.getInt(currentKey, Accent.DEFAULT))
}

/**
 * Converts paths from the old excluded directory database to a list of modern [Directory]
 * instances.
 *
 * Historically, Auxio used an excluded directory database shamelessly ripped from Phonograph. This
 * was a dumb idea, as the choice of a full-blown database for a few paths was overkill, version
 * boundaries were not respected, and the data format limited us to grokking DATA.
 *
 * In 2.4.0, Auxio switched to a system based on SharedPreferences, also switching from a path-based
 * excluded system to a volume-based excluded system at the same time. These are both rolled into
 * this conversion.
 */
fun handleExcludedCompat(context: Context, storageManager: StorageManager): List<Directory> {
    Log.d("Auxio.SettingsCompat", "Migrating old excluded database")

    // /storage/emulated/0 (the old path prefix) should correspond to primary *emulated* storage.
    val primaryVolume =
        storageManager.storageVolumesCompat.find { it.isInternalCompat } ?: return emptyList()

    val primaryDirectory =
        (primaryVolume.directoryCompat ?: return emptyList()) + File.separatorChar

    return LegacyExcludedDatabase(context).readPaths().map { path ->
        val relativePath = path.removePrefix(primaryDirectory)
        Log.d("Auxio.SettingsCompat", "Migrate $path -> $relativePath")
        Directory(primaryVolume, relativePath)
    }
}

class LegacyExcludedDatabase(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME ($COLUMN_PATH TEXT NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, newVersion, oldVersion)
    }

    /** Get the current list of paths from the database. */
    fun readPaths(): List<String> {
        val paths = mutableListOf<String>()
        readableDatabase.queryAll(TABLE_NAME) { cursor ->
            while (cursor.moveToNext()) {
                paths.add(cursor.getString(0))
            }
        }

        logD("Successfully read ${paths.size} paths from db")

        return paths
    }

    companion object {
        // Blacklist is still used here for compatibility reasons, please don't get
        // your pants in a twist about it.
        const val DB_VERSION = 1
        const val DB_NAME = "auxio_blacklist_database.db"

        const val TABLE_NAME = "blacklist_dirs_table"
        const val COLUMN_PATH = "COLUMN_PATH"
    }
}

/** Cache of the old keys used in Auxio. */
private object OldKeys {
    const val KEY_ACCENT3 = "auxio_accent"
}
