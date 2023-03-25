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
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import org.oxycblt.auxio.image.ImageSettings
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.ui.UISettings

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

    override fun onCreate() {
        super.onCreate()
        // Migrate any settings that may have changed in an app update.
        imageSettings.migrate()
        playbackSettings.migrate()
        uiSettings.migrate()
        // Adding static shortcuts in a dynamic manner is better than declaring them
        // manually, as it will properly handle the difference between debug and release
        // Auxio instances.
        // TODO: Switch to static shortcuts
        ShortcutManagerCompat.addDynamicShortcuts(
            this,
            listOf(
                ShortcutInfoCompat.Builder(this, SHORTCUT_SHUFFLE_ID)
                    .setShortLabel(getString(R.string.lbl_shuffle_shortcut_short))
                    .setLongLabel(getString(R.string.lbl_shuffle_shortcut_long))
                    .setIcon(IconCompat.createWithResource(this, R.drawable.ic_shortcut_shuffle_24))
                    .setIntent(
                        Intent(this, MainActivity::class.java)
                            .setAction(INTENT_KEY_SHORTCUT_SHUFFLE))
                    .build()))
    }

    companion object {
        /** The [Intent] name for the "Shuffle All" shortcut. */
        const val INTENT_KEY_SHORTCUT_SHUFFLE = BuildConfig.APPLICATION_ID + ".action.SHUFFLE_ALL"
        /** The ID of the "Shuffle All" shortcut. */
        private const val SHORTCUT_SHUFFLE_ID = "shortcut_shuffle"
    }
}
