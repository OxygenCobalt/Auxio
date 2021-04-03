package org.oxycblt.auxio.database

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Looper

/**
 * Shortcut for querying all items in a database and running [block] with the cursor returned.
 * Will not run if the cursor is null.
 */
fun <R> SQLiteDatabase.queryAll(tableName: String, block: (Cursor) -> R) =
    query(tableName, null, null, null, null, null, null)?.use(block)

/**
 * Assert that we are on a background thread.
 */
fun SQLiteOpenHelper.assertBackgroundThread() {
    check(Looper.myLooper() != Looper.getMainLooper()) {
        "Database operations must be ran on a background thread."
    }
}
