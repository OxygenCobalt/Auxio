/*
 * Copyright (c) 2025 Auxio Project
 * WriteOnlyFileTreeCache.kt is part of Auxio.
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

package org.oxycblt.auxio.music.shim

import android.net.Uri
import org.oxycblt.musikr.fs.device.CachedDirectory
import org.oxycblt.musikr.fs.device.CachedFile
import org.oxycblt.musikr.fs.device.FileTree
import org.oxycblt.musikr.fs.device.FileTreeCache

class WriteOnlyFileTreeCache(private val inner: FileTreeCache) : FileTreeCache {
    override fun read(): FileTree {
        val innerTree = inner.read()
        return WriteOnlyFileTree(innerTree)
    }
}

class WriteOnlyFileTree(private val innerTree: FileTree) : FileTree {
    override suspend fun queryDirectory(uri: Uri): CachedDirectory? {
        // Always return null to simulate a cache miss
        return null
    }

    override suspend fun updateDirectory(uri: Uri, directory: CachedDirectory) {
        // Forward update operations to the inner tree
        innerTree.updateDirectory(uri, directory)
    }

    override suspend fun queryFile(uri: Uri): CachedFile? {
        // Always return null to simulate a cache miss
        return null
    }

    override suspend fun updateFile(uri: Uri, file: CachedFile) {
        // Forward update operations to the inner tree
        innerTree.updateFile(uri, file)
    }

    override suspend fun write() {
        // Forward write operations to the inner tree
        innerTree.write()
    }
}