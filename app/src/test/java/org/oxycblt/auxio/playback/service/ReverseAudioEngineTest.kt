/*
 * Copyright (c) 2026 Auxio Project
 * ReverseAudioEngineTest.kt is part of Auxio.
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

import org.junit.Assert.assertArrayEquals
import org.junit.Test

class ReverseAudioEngineTest {
    @Test
    fun reversePcmFrames_preservesInterleavedChannels() {
        val pcm = byteArrayOf(1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0)

        reversePcmFrames(pcm, 4)

        assertArrayEquals(byteArrayOf(5, 0, 6, 0, 3, 0, 4, 0, 1, 0, 2, 0), pcm)
    }

    @Test
    fun amplifyPcm_clampsSamples() {
        val pcm = byteArrayOf(0xff.toByte(), 0x7f, 0x00, 0x80.toByte(), 0xe8.toByte(), 0x03)

        amplifyPcm(pcm, 2f)

        assertArrayEquals(
            byteArrayOf(0xff.toByte(), 0x7f, 0x00, 0x80.toByte(), 0xd0.toByte(), 0x07),
            pcm,
        )
    }
}
