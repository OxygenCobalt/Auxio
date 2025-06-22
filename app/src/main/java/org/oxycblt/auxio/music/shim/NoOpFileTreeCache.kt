/*
 * Copyright (c) 2025 Auxio Project
 * NoOpFileTreeCache.kt is part of Auxio.
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

/**
 * A no-op implementation of [FileTreeCache] that doesn't cache anything. This implementation always
 * returns cache misses and discards all updates.
 */
class NoOpFileTreeCache : FileTreeCache {
    override fun read(): FileTree = NoOpFileTree()
}

/**
 * A no-op implementation of [FileTree] that doesn't cache anything. All queries return null (cache
 * miss) and all updates are discarded.
 */
class NoOpFileTree : FileTree {
    override suspend fun queryDirectory(uri: Uri): CachedDirectory? {
        // Always return null to simulate a cache miss
        return null
    }

    override suspend fun updateDirectory(uri: Uri, directory: CachedDirectory) {
        // Do nothing - discard the update
    }

    override suspend fun queryFile(uri: Uri): CachedFile? {
        // Always return null to simulate a cache miss
        return null
    }

    override suspend fun updateFile(uri: Uri, file: CachedFile) {
        // Do nothing - discard the update
    }

    override suspend fun write() {
        // Do nothing - no cache to write
    }
}
