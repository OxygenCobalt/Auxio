package org.oxycblt.auxio.util

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.sqlite.transaction


/**
 * Query all columns in the given [SQLiteDatabase] table, running the block when the [Cursor] is
 * loaded. The block will be called with [use], allowing for automatic cleanup of [Cursor]
 * resources.
 * @param tableName The name of the table to query all columns in.
 * @param block The code block to run with the loaded [Cursor].
 */
inline fun <R> SQLiteDatabase.queryAll(tableName: String, block: (Cursor) -> R) =
    query(tableName, null, null, null, null, null, null)?.use(block)

/**
 * Create a table in an [SQLiteDatabase], if it does not already exist.
 * @param name The name of the table to create.
 * @param schema A block that adds a comma-separated list of SQL column declarations.
 */
inline fun SQLiteDatabase.createTable(name: String, schema: StringBuilder.() -> StringBuilder) {
    val command = StringBuilder()
        .append("CREATE TABLE IF NOT EXISTS $name(")
        .schema()
        .append(")")
    execSQL(command.toString())
}

/**
 * Safely write a list of items to an [SQLiteDatabase]. This will clear the prior list and write
 * as much of the new list as possible.
 * @param list The list of items to write.
 * @param tableName The name of the table to write the items to.
 * @param transform Code to transform an item into a corresponding [ContentValues] to the given
 * table.
 */
inline fun <reified T> SQLiteDatabase.writeList(list: List<T>, tableName: String, transform: (Int, T) -> ContentValues) {
    // Clear any prior items in the table.
    transaction { delete(tableName, null, null) }

    var transactionPosition = 0
    while (transactionPosition < list.size) {
        // Start at the current transaction position, if a transaction failed at any point,
        // this value can be used to immediately start at the next item and continue writing
        // the list without error.
        var i = transactionPosition
        transaction {
            while (i < list.size) {
                val values = transform(i, list[i])
                // Increment forward now so that if this insert fails, the transactionPosition
                // will still start at the next i.
                i++
                insert(tableName, null, values)
            }
        }
        transactionPosition = i
        logD("Wrote batch of ${T::class.simpleName} instances. " +
                    "Position is now at $transactionPosition")
    }
}
