/*
 * Copyright (c) 2023 Auxio Project
 * FakeMusicRepository.kt is part of Auxio.
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

import kotlinx.coroutines.Job
import org.oxycblt.auxio.music.device.DeviceLibrary
import org.oxycblt.auxio.music.user.UserLibrary

open class FakeMusicRepository : MusicRepository {
    override val indexingState: IndexingState?
        get() = throw NotImplementedError()
    override val deviceLibrary: DeviceLibrary?
        get() = throw NotImplementedError()
    override val userLibrary: UserLibrary?
        get() = throw NotImplementedError()

    override fun addUpdateListener(listener: MusicRepository.UpdateListener) {
        throw NotImplementedError()
    }

    override fun removeUpdateListener(listener: MusicRepository.UpdateListener) {
        throw NotImplementedError()
    }

    override fun addIndexingListener(listener: MusicRepository.IndexingListener) {
        throw NotImplementedError()
    }

    override fun removeIndexingListener(listener: MusicRepository.IndexingListener) {
        throw NotImplementedError()
    }

    override fun registerWorker(worker: MusicRepository.IndexingWorker) {
        throw NotImplementedError()
    }

    override fun unregisterWorker(worker: MusicRepository.IndexingWorker) {
        throw NotImplementedError()
    }

    override fun find(uid: Music.UID): Music? {
        throw NotImplementedError()
    }

    override fun requestIndex(withCache: Boolean) {
        throw NotImplementedError()
    }

    override fun index(worker: MusicRepository.IndexingWorker, withCache: Boolean): Job {
        throw NotImplementedError()
    }
}
