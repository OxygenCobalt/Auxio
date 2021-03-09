package org.oxycblt.auxio.database

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Looper
import org.oxycblt.auxio.logE
import java.lang.Exception

/**
 * Shortcut for running a series of [commands] on an [SQLiteDatabase].
 * @return true if the transaction was successful, false if not.
 */
fun SQLiteDatabase.execute(commands: SQLiteDatabase.() -> Unit): Boolean {
    beginTransaction()

    val success = try {
        commands()
        setTransactionSuccessful()
        true
    } catch (e: Exception) {
        logE("An error occurred when trying to execute commands.")
        logE(e.stackTraceToString())

        false
    }

    endTransaction()

    return success
}

/**
 * Shortcut for running a query on this database and then running [block] with the cursor returned.
 * Will not run if the cursor is null.
 */
fun <R> SQLiteDatabase.queryUse(
    tableName: String,
    columns: Array<String>?,
    selection: String?,
    vararg args: String,
    block: (Cursor) -> R
) = query(tableName, columns, selection, args, null, null, null, null)?.use(block)

/**
 * Shortcut for querying all items in a database and running [block] with the cursor returned.
 * Will not run if the cursor is null.
 */
fun <R> SQLiteDatabase.queryAll(tableName: String, block: (Cursor) -> R) =
    query(tableName, null, null, null, null, null, null)?.use(block)

/**
 * Assert that we are on a background thread.
 */
fun assertBackgroundThread() {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        error("Not on a background thread.")
    }
}
