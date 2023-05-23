/*
 * Copyright (c) 2023 Auxio Project
 * Indexing.kt is part of Auxio.
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

import android.os.Build

/** Version-aware permission identifier for reading audio files. */
val PERMISSION_READ_AUDIO =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_AUDIO
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

/**
 * Represents the current state of the music loader.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed interface IndexingState {
    /**
     * Music loading is on-going.
     *
     * @param progress The current progress of the music loading.
     */
    data class Indexing(val progress: IndexingProgress) : IndexingState

    /**
     * Music loading has completed.
     *
     * @param error If music loading has failed, the error that occurred will be here. Otherwise, it
     *   will be null.
     */
    data class Completed(val error: Throwable?) : IndexingState
}

/**
 * Represents the current progress of music loading.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed interface IndexingProgress {
    /** Other work is being done that does not have a defined progress. */
    object Indeterminate : IndexingProgress

    /**
     * Songs are currently being loaded.
     *
     * @param current The current amount of songs loaded.
     * @param total The projected total amount of songs.
     */
    data class Songs(val current: Int, val total: Int) : IndexingProgress
}

/**
 * Thrown by the music loader when [PERMISSION_READ_AUDIO] was not granted.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class NoAudioPermissionException : Exception() {
    override val message = "Storage permissions are required to load music"
}

/**
 * Thrown when no music was found.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class NoMusicException : Exception() {
    override val message = "No music was found on the device"
}
