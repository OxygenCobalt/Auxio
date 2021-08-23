/*
 * Copyright (c) 2021 Auxio Project
 * BlacklistDatabase.kt is part of Auxio.
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

package org.oxycblt.auxio.excluded

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.sqlite.transaction
import org.oxycblt.auxio.util.assertBackgroundThread
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.queryAll

/**
 * Database for storing excluded directories.
 * Note that the paths stored here will not work with MediaStore unless you append a "%" at the end.
 * Yes. I know Room exists. But that would needlessly bloat my app and has crippling bugs.
 * @author OxygenCobalt
 */
class ExcludedDatabase(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
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

    /**
     * Write a list of [paths] to the database.
     */
    fun writePaths(paths: List<String>) {
        assertBackgroundThread()

        writableDatabase.transaction {
            delete(TABLE_NAME, null, null)

            logD("Deleted paths db")

            for (path in paths) {
                insert(
                    TABLE_NAME, null,
                    ContentValues(1).apply {
                        put(COLUMN_PATH, path)
                    }
                )
            }
        }
    }

    /**
     * Get the current list of paths from the database.
     */
    fun readPaths(): List<String> {
        assertBackgroundThread()

        val paths = mutableListOf<String>()

        readableDatabase.queryAll(TABLE_NAME) { cursor ->
            while (cursor.moveToNext()) {
                paths.add(cursor.getString(0))
            }
        }

        return paths
    }

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "auxio_blacklist_database.db"

        const val TABLE_NAME = "blacklist_dirs_table"
        const val COLUMN_PATH = "COLUMN_PATH"

        @Volatile
        private var INSTANCE: ExcludedDatabase? = null

        /**
         * Get/Instantiate the single instance of [ExcludedDatabase].
         */
        fun getInstance(context: Context): ExcludedDatabase {
            val currentInstance = INSTANCE

            if (currentInstance != null) {
                return currentInstance
            }

            synchronized(this) {
                val newInstance = ExcludedDatabase(context.applicationContext)
                INSTANCE = newInstance
                return newInstance
            }
        }
    }
}
