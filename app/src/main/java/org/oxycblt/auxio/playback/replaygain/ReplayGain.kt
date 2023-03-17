/*
 * Copyright (c) 2022 Auxio Project
 * ReplayGain.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.replaygain

import org.oxycblt.auxio.IntegerTable

/**
 * The current ReplayGain configuration.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
enum class ReplayGainMode {
    /** Apply the track gain, falling back to the album gain if the track gain is not found. */
    TRACK,
    /** Apply the album gain, falling back to the track gain if the album gain is not found. */
    ALBUM,
    /** Apply the album gain only when playing from an album, defaulting to track gain otherwise. */
    DYNAMIC;

    companion object {
        /**
         * Convert a [ReplayGainMode] integer representation into an instance.
         *
         * @param intCode An integer representation of a [ReplayGainMode]
         * @return The corresponding [ReplayGainMode], or null if the [ReplayGainMode] is invalid.
         */
        fun fromIntCode(intCode: Int) =
            when (intCode) {
                IntegerTable.REPLAY_GAIN_MODE_TRACK -> TRACK
                IntegerTable.REPLAY_GAIN_MODE_ALBUM -> ALBUM
                IntegerTable.REPLAY_GAIN_MODE_DYNAMIC -> DYNAMIC
                else -> null
            }
    }
}

/**
 * The current ReplayGain pre-amp configuration.
 *
 * @param with The pre-amp (in dB) to use when ReplayGain tags are present.
 * @param without The pre-amp (in dB) to use when ReplayGain tags are not present.
 * @author Alexander Capehart (OxygenCobalt)
 */
data class ReplayGainPreAmp(val with: Float, val without: Float)
