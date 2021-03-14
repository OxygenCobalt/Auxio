package org.oxycblt.auxio.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.oxycblt.auxio.logD
import java.io.File
import java.io.IOException

/**
 * Database for storing blacklisted paths.
 * Note that the paths stored here will not work with MediaStore unless you append a "%" at the end.
 * Yes. I know Room exists. But that would needlessly bloat my app and has crippling bugs.
 * @author OxygenCobalt
 */
class BlacklistDatabase(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME ($COLUMN_PATH TEXT NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    /**
     * Add a [File] to the the blacklist.
     * @return Whether this file has been added to the database or not.
     */
    fun addPath(file: File): Boolean {
        assertBackgroundThread()

        val path = file.canonicalPathSafe

        logD("Adding path $path to blacklist")

        if (hasFile(path)) {
            logD("Path already exists. Ignoring.")

            return false
        }

        writableDatabase.execute {
            val values = ContentValues(1)
            values.put(COLUMN_PATH, path)

            insert(TABLE_NAME, null, values)
        }

        return true
    }

    /**
     * Remove a [File] from this blacklist.
     */
    fun removePath(file: File) {
        assertBackgroundThread()

        writableDatabase.execute {
            delete(TABLE_NAME, "$COLUMN_PATH=?", arrayOf(file.canonicalPathSafe))
        }
    }

    fun getPaths(): List<String> {
        assertBackgroundThread()

        val paths = mutableListOf<String>()

        readableDatabase.queryAll(TABLE_NAME) { cursor ->
            while (cursor.moveToNext()) {
                paths.add(cursor.getString(0))
            }
        }

        return paths
    }

    private fun hasFile(path: String): Boolean {
        val exists = readableDatabase.queryUse(TABLE_NAME, null, "$COLUMN_PATH=?", path) { cursor ->
            cursor.moveToFirst()
        }

        return exists ?: false
    }

    private val File.canonicalPathSafe: String get() {
        return try {
            canonicalPath
        } catch (e: IOException) {
            absolutePath
        }
    }

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "auxio_blacklist_database.db"

        const val TABLE_NAME = "blacklist_dirs_table"
        const val COLUMN_PATH = "COLUMN_PATH"

        @Volatile
        private var INSTANCE: BlacklistDatabase? = null

        /**
         * Get/Instantiate the single instance of [PlaybackStateDatabase].
         */
        fun getInstance(context: Context): BlacklistDatabase {
            val currentInstance = INSTANCE

            if (currentInstance != null) {
                return currentInstance
            }

            synchronized(this) {
                val newInstance = BlacklistDatabase(context.applicationContext)
                INSTANCE = newInstance
                return newInstance
            }
        }
    }
}
