/*
 * Copyright (c) 2024 Auxio Project
 * ReplayGainPreAmp.kt is part of Auxio.
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

/**
 * The current ReplayGain pre-amp configuration.
 *
 * @param with The pre-amp (in dB) to use when ReplayGain tags are present.
 * @param without The pre-amp (in dB) to use when ReplayGain tags are not present.
 * @author Alexander Capehart (OxygenCobalt)
 */
data class ReplayGainPreAmp(val with: Float, val without: Float)
