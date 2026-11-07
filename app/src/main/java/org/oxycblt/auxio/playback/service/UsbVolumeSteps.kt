/*
 * Copyright (c) 2026 Auxio Project
 * UsbVolumeSteps.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.service

/**
 * Pure step<->dB conversion math for [MediaSessionHolder]'s remote [UsbVolumeProvider][
 * MediaSessionHolder.UsbVolumeProvider], pulled out of that inner class so it can be unit tested
 * directly -- [androidx.media.VolumeProviderCompat]'s own methods touch Android framework internals
 * not available under a plain JVM unit test.
 */
internal object UsbVolumeSteps {
    /**
     * How many equal slices [percentPerStep]% of a range divides it into -- e.g. a 5% step means
     * the range is cut into 20 slices -- clamped so a 0 or negative percent can never divide by
     * zero.
     */
    fun divisions(percentPerStep: Int): Int = 100 / percentPerStep.coerceAtLeast(1)

    /**
     * The raw dB*256 value [divisions] equal slices of [range] away from its minimum, at [step].
     */
    fun stepToDb(step: Int, range: Pair<Int, Int>, divisions: Int): Int {
        val (min, max) = range
        return min + (max - min) * step / divisions
    }

    /**
     * The nearest step [db] falls on, rounding to the closest slice rather than truncating.
     * [stepToDb] already truncates when computing a dB delta from a step, so round-tripping a value
     * it just produced back through floor-division here would land one step short (floor of floor
     * rounds down twice) -- confirmed on real hardware: a device's reported volume silently drifted
     * down a step every time this ran on a periodic control-state refresh, with no corresponding
     * volume-key press.
     */
    fun dbToStep(db: Int, range: Pair<Int, Int>, divisions: Int): Int {
        val (min, max) = range
        if (max == min) return 0
        return (((db - min) * divisions + (max - min) / 2) / (max - min)).coerceIn(0, divisions)
    }
}
