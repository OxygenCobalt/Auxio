package org.oxycblt.auxio

import android.util.Log

// Shortcut functions for logging.
// Yes, I know timber exists but this does what I need.

/**
 * Shortcut method for logging a debug statement, handles debug builds and anonymous objects
 * @param msg The message to log
 */
fun Any.logD(msg: String) {
    if (BuildConfig.DEBUG) {
        Log.d(getName(), msg)
    }
}

/**
 * Shortcut method for logging an error. Handles anonymous objects
 * @param msg The message to log
 */
fun Any.logE(msg: String) {
    Log.e(getName(), msg)
}

/**
 * Get a non-nullable name, used so that logs will always show up in the console.
 * @return The name of the object, otherwise "Anonymous Object"
 */
private fun Any.getName(): String = this::class.simpleName ?: "Anonymous Object"
