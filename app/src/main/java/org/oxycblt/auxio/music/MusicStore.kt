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
 
package org.oxycblt.auxio.music

import org.oxycblt.auxio.music.library.Library

/**
 * A repository granting access to the music library.
 *
 * This can be used to obtain certain music items, or await changes to the music library. It is
 * generally recommended to use this over Indexer to keep track of the library state, as the
 * interface will be less volatile.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class MusicStore private constructor() {
    private val listeners = mutableListOf<Listener>()

    /**
     * The current [Library]. May be null if a [Library] has not been successfully loaded yet. This
     * can change, so it's highly recommended to not access this directly and instead rely on
     * [Listener].
     */
    @Volatile
    var library: Library? = null
        set(value) {
            field = value
            for (callback in listeners) {
                callback.onLibraryChanged(library)
            }
        }

    /**
     * Add a [Listener] to this instance. This can be used to receive changes in the music library.
     * Will invoke all [Listener] methods to initialize the instance with the current state.
     * @param listener The [Listener] to add.
     * @see Listener
     */
    @Synchronized
    fun addListener(listener: Listener) {
        listener.onLibraryChanged(library)
        listeners.add(listener)
    }

    /**
     * Remove a [Listener] from this instance, preventing it from receiving any further updates.
     * @param listener The [Listener] to remove. Does nothing if the [Listener] was never added in
     * the first place.
     * @see Listener
     */
    @Synchronized
    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    /** A listener for changes in the music library. */
    interface Listener {
        /**
         * Called when the current [Library] has changed.
         * @param library The new [Library], or null if no [Library] has been loaded yet.
         */
        fun onLibraryChanged(library: Library?)
    }

    companion object {
        @Volatile private var INSTANCE: MusicStore? = null

        /**
         * Get a singleton instance.
         * @return The (possibly newly-created) singleton instance.
         */
        fun getInstance(): MusicStore {
            val currentInstance = INSTANCE
            if (currentInstance != null) {
                return currentInstance
            }

            synchronized(this) {
                val newInstance = MusicStore()
                INSTANCE = newInstance
                return newInstance
            }
        }
    }
}
