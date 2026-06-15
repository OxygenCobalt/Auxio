/*
 * Copyright (c) 2024 Auxio Project
 * TagLibJNI.kt is part of Auxio.
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
 
package org.oxycblt.musikr.metadata

import java.io.FileInputStream
import org.oxycblt.musikr.fs.File

internal object TagLibJNI {
    init {
        System.loadLibrary("tagJNI")
    }

    /**
     * Open a file and extract a tag.
     *
     * Note: This method is blocking and should be handled as such if calling from a coroutine.
     */
    fun open(deviceFile: File, fis: FileInputStream): Metadata? {
        val inputStream = NativeInputStream(deviceFile, fis)
        val tag = openNative(inputStream)
        inputStream.close()
        return tag
    }

    private external fun openNative(inputStream: NativeInputStream): Metadata?
}
