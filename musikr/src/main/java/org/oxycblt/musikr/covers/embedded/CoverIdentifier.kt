/*
 * Copyright (c) 2024 Auxio Project
 * CoverIdentifier.kt is part of Auxio.
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
 
package org.oxycblt.musikr.covers.embedded

import java.security.MessageDigest

/** An interface to transform embedded cover data into cover IDs, used for [EmbeddedCovers]. */
interface CoverIdentifier {
    /**
     * Identify the cover data and return a unique identifier for it. This should use a strong
     * hashing algorithm to ensure uniqueness and minimize memory use.
     *
     * @param data the cover data to identify
     * @return a unique identifier for the cover data, such as a hash
     */
    suspend fun identify(data: ByteArray): String

    companion object {
        /**
         * Returns a default implementation of [CoverIdentifier] that uses the MD5 hashing
         * algorithm. Reasonably efficient for most default use-cases, but not secure if any
         * extensions could be brought down by ID collisions.
         *
         * @return a [CoverIdentifier] that uses the MD5 hashing algorithm
         */
        fun md5(): CoverIdentifier = MD5CoverIdentifier()
    }
}

private class MD5CoverIdentifier() : CoverIdentifier {
    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun identify(data: ByteArray): String {
        val digest =
            MessageDigest.getInstance("MD5").run {
                update(data)
                digest()
            }
        return digest.toHexString()
    }
}
