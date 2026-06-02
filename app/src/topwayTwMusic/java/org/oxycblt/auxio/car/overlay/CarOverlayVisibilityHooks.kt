/*
 * Copyright (c) 2024 Auxio Project
 * CarOverlayVisibilityHooks.kt is part of Auxio.
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

package org.oxycblt.auxio.car.overlay

import android.app.Activity
import android.app.Application
import android.os.Bundle
import timber.log.Timber as L

/**
 * Application-level activity lifecycle callbacks that automatically hide/show the car floating
 * overlay when Auxio's activities transition between foreground/background. Uses a started-activity
 * counter to detect app-level foreground/background transitions (0→1 and 1→0) rather than
 * individual activity pauses/resumes, avoiding brief overlay flickers during in-app navigation.
 */
class CarOverlayVisibilityHooks : Application.ActivityLifecycleCallbacks {

    private var startedActivityCount = 0

    override fun onActivityStarted(activity: Activity) {
        val previous = startedActivityCount
        startedActivityCount++
        if (previous == 0) {
            // App entered foreground (0→1 transition).
            val prefs = CarOverlayPrefs.from(activity)
            if (prefs.enabled) {
                L.d("Auxio entered foreground, signalling overlay to hide")
                CarFloatingControlsService.setAuxioForeground(activity, true)
            }
        }
    }

    override fun onActivityStopped(activity: Activity) {
        startedActivityCount = (startedActivityCount - 1).coerceAtLeast(0)
        if (startedActivityCount == 0) {
            // App entered background (1→0 transition).
            val prefs = CarOverlayPrefs.from(activity)
            if (prefs.enabled) {
                L.d("Auxio entered background, signalling overlay to show")
                CarFloatingControlsService.setAuxioForeground(activity, false)
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    companion object {
        /**
         * Registers the visibility hooks on the given application. Should be called once from the
         * Application's onCreate or from a variant-specific initializer.
         */
        fun register(application: Application) {
            application.registerActivityLifecycleCallbacks(CarOverlayVisibilityHooks())
            L.d("Car overlay visibility hooks registered")
        }
    }
}
