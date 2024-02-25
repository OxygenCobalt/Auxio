/*
 * Copyright (c) 2024 Auxio Project
 * ServiceFragment.kt is part of Auxio.
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
 
package org.oxycblt.auxio.service

import android.content.Context
import android.content.Intent

abstract class ServiceFragment {
    private var handle: AuxioService? = null

    protected val context: Context
        get() = requireNotNull(handle)

    var notification: ForegroundServiceNotification? = null
        private set

    fun attach(handle: AuxioService) {
        this.handle = handle
        onCreate(handle)
    }

    fun release() {
        notification = null
        handle = null
        onDestroy()
    }

    fun handleIntent(intent: Intent) {
        onStartCommand(intent)
    }

    fun handleTaskRemoved() {
        onTaskRemoved()
    }

    protected open fun onCreate(context: Context) {}

    protected open fun onDestroy() {}

    protected open fun onStartCommand(intent: Intent) {}

    protected open fun onTaskRemoved() {}

    protected fun startForeground(notification: ForegroundServiceNotification) {
        this.notification = notification
        requireNotNull(handle).refreshForeground()
    }

    protected fun stopForeground() {
        this.notification = null
        requireNotNull(handle).refreshForeground()
    }
}
