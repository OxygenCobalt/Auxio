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
 
package org.oxycblt.musikr.covers.internal

import java.security.MessageDigest

interface CoverIdentifier {
    suspend fun identify(data: ByteArray): String

    companion object {
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
