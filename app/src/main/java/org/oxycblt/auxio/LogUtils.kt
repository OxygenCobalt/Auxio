package org.oxycblt.auxio

import android.util.Log

// Shortcut functions for logging.
// Yes, I know timber exists but this does what I need.

/**
 * Shortcut method for logging a non-string [obj] to debug. Should only be used for debug preferably.
 */
fun Any.logD(obj: Any) {
    logD(obj.toString())
}

/**
 * Shortcut method for logging [msg] to the debug console., handles debug builds and anonymous objects
 */
fun Any.logD(msg: String) {
    if (BuildConfig.DEBUG) {
        Log.d(getName(), msg)
    }
}

/**
 * Shortcut method for logging [msg] as an error to the console. Handles anonymous objects
 */
fun Any.logE(msg: String) {
    Log.e(getName(), msg)
}

/**
 * Get a non-nullable name, used so that logs will always show up in the console.
 * This also applies a special "Auxio" prefix so that messages can be filtered to just from the main codebase.
 * @return The name of the object, otherwise "Anonymous Object"
 */
private fun Any.getName(): String = "Auxio.${this::class.simpleName ?: "Anonymous Object"}"
