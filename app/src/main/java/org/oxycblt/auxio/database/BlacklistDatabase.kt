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
 * Note that the paths stored here will not work with MediaStore unless you append a "%" at the
 * end.
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
        val path = file.mediaStorePath

        logD("Adding path $path to blacklist")

        if (hasFile(path)) {
            logD("Path already exists. Ignoring.")

            return false
        }

        val database = writableDatabase
        database.beginTransaction()

        try {
            val values = ContentValues(1)
            values.put(COLUMN_PATH, path)

            database.insert(TABLE_NAME, null, values)
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()

            return true
        }
    }

    /**
     * Remove a [File] from this blacklist.
     */
    fun removePath(file: File) {
        val database = writableDatabase
        val path = file.mediaStorePath

        database.beginTransaction()
        database.delete(TABLE_NAME, "$COLUMN_PATH=?", arrayOf(path))
        database.setTransactionSuccessful()
        database.endTransaction()
    }

    fun getPaths(): List<String> {
        val paths = mutableListOf<String>()

        val pathsCursor = readableDatabase.query(
            TABLE_NAME, arrayOf(COLUMN_PATH), null, null, null, null, null
        )

        pathsCursor?.use { cursor ->
            while (cursor.moveToNext()) {
                paths.add(cursor.getString(0))
            }
        }

        return paths
    }

    private fun hasFile(path: String): Boolean {
        val pathsCursor = readableDatabase.query(
            TABLE_NAME,
            arrayOf(COLUMN_PATH),
            "$COLUMN_PATH=?",
            arrayOf(path),
            null, null, null, null
        )

        pathsCursor?.use { cursor ->
            return cursor.moveToFirst()
        }

        return false
    }

    private val File.mediaStorePath: String get() {
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
