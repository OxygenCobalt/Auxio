/*
 * Copyright (c) 2023 Auxio Project
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
 
package org.oxycblt.auxio.music.system

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

open class FakeIndexer : Indexer {
    override val isIndeterminate: Boolean
        get() = throw NotImplementedError()
    override val isIndexing: Boolean
        get() = throw NotImplementedError()

    override fun index(context: Context, withCache: Boolean, scope: CoroutineScope): Job {
        throw NotImplementedError()
    }

    override fun registerController(controller: Indexer.Controller) {
        throw NotImplementedError()
    }

    override fun unregisterController(controller: Indexer.Controller) {
        throw NotImplementedError()
    }

    override fun registerListener(listener: Indexer.Listener) {
        throw NotImplementedError()
    }

    override fun unregisterListener(listener: Indexer.Listener) {
        throw NotImplementedError()
    }

    override fun requestReindex(withCache: Boolean) {
        throw NotImplementedError()
    }

    override fun reset() {
        throw NotImplementedError()
    }
}
