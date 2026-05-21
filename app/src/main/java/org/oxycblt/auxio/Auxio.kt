/*
 * Copyright (c) 2021 Auxio Project
 * Auxio.kt is part of Auxio.
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
 
package org.oxycblt.auxio

import android.app.Application
import android.content.Intent
import androidx.core.content.pm.ShortcutManagerCompat
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import org.oxycblt.auxio.home.HomeSettings
import org.oxycblt.auxio.image.ImageSettings
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.headunit.HeadUnitEntryPoints
import org.oxycblt.auxio.ui.UISettings
import org.oxycblt.auxio.util.CopyleftNoticeTree
import timber.log.Timber

/**
 * A simple, rational music player for android.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@HiltAndroidApp
class Auxio : Application() {
    @Inject lateinit var imageSettings: ImageSettings
    @Inject lateinit var playbackSettings: PlaybackSettings
    @Inject lateinit var uiSettings: UISettings
    @Inject lateinit var homeSettings: HomeSettings

    override fun onCreate() {
        super.onCreate()
        @Suppress("KotlinConstantConditions")
        if (
            BuildConfig.APPLICATION_ID != "org.oxycblt.auxio" &&
                BuildConfig.APPLICATION_ID != "org.oxycblt.auxio.debug"
        ) {
            Timber.plant(CopyleftNoticeTree())
        } else if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Migrate any settings that may have changed in an app update.
        imageSettings.migrate()
        playbackSettings.migrate()
        uiSettings.migrate()
        homeSettings.migrate()
        ShortcutManagerCompat.setDynamicShortcuts(this, HeadUnitEntryPoints.createDynamicShortcuts(this))
    }

}
