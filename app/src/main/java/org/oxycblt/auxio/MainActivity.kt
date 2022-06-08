/*
 * Copyright (c) 2021 Auxio Project
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

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.core.view.updatePadding
import org.oxycblt.auxio.databinding.ActivityMainBinding
import org.oxycblt.auxio.music.IndexerService
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.system.PlaybackService
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.accent.Accent
import org.oxycblt.auxio.util.getColorSafe
import org.oxycblt.auxio.util.getSystemBarInsetsCompat
import org.oxycblt.auxio.util.isNight
import org.oxycblt.auxio.util.logD

/**
 * The single [AppCompatActivity] for Auxio.
 *
 * TODO: Add crash reporting and error screens. This likely has to be an external activity, so it is
 * blocked by eliminating exitProcess from the app.
 *
 * TODO: Custom language support
 *
 * TODO: Rework padding ethos
 *
 * @author OxygenCobalt
 */
class MainActivity : AppCompatActivity() {
    private val playbackModel: PlaybackViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsManager = SettingsManager.getInstance()

        setupTheme(settingsManager.theme, settingsManager.accent, settingsManager.useBlackTheme)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupEdgeToEdge(binding.root, settingsManager.edgeToEdge)

        logD("Activity created")
    }

    override fun onStart() {
        super.onStart()

        startService(Intent(this, IndexerService::class.java))
        startService(Intent(this, PlaybackService::class.java))

        // If we have a file URI already, open it. Otherwise, restore the playback state.
        val action =
            retrieveViewUri(intent)?.let { PlaybackViewModel.DelayedAction.Open(it) }
                ?: PlaybackViewModel.DelayedAction.RestoreState

        playbackModel.performAction(this, action)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // See if the new intent is a file intent. If so, open it.
        val uri = retrieveViewUri(intent)
        if (uri != null) {
            playbackModel.performAction(this, PlaybackViewModel.DelayedAction.Open(uri))
        }
    }

    /**
     * Extracts a [Uri] from an intent as long as:
     * - The intent is ACTION_VIEW
     * - The intent has not already been used.
     */
    private fun retrieveViewUri(intent: Intent?): Uri? {
        if (intent != null) {
            val action = intent.action
            val isConsumed = intent.getBooleanExtra(KEY_INTENT_USED, false)

            if (action == Intent.ACTION_VIEW && !isConsumed) {
                // Mark the intent as used so this does not fire again
                intent.putExtra(KEY_INTENT_USED, true)
                return intent.data
            }
        }

        return null
    }

    private fun setupTheme(theme: Int, accent: Accent, useBlackTheme: Boolean) {
        // Disable theme customization above Android 12, as it's far enough in as a version to
        // the point where most phones should have an automatic option for light/dark theming.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            AppCompatDelegate.setDefaultNightMode(theme)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        // The black theme has a completely separate set of styles since style attributes cannot
        // be modified at runtime.
        if (isNight && useBlackTheme) {
            logD("Applying black theme [accent $accent]")
            setTheme(accent.blackTheme)
        } else {
            logD("Applying normal theme [accent $accent]")
            setTheme(accent.theme)
        }
    }

    private fun setupEdgeToEdge(contentView: View, enabled: Boolean) {
        val fitsSystemWindows = !enabled
        WindowCompat.setDecorFitsSystemWindows(window, fitsSystemWindows)
        if (fitsSystemWindows) {
            // Auxio's theme is normally set up to anticipate edge to edge mode being
            // enabled. In the case that it is not, we have to update the values during
            // runtime.
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            val black = getColorSafe(android.R.color.black)

            window.statusBarColor = black
            controller.isAppearanceLightStatusBars = false
            window.navigationBarColor = black
            controller.isAppearanceLightNavigationBars = false
        }

        contentView.setOnApplyWindowInsetsListener { view, insets ->
            val bars = insets.getSystemBarInsetsCompat(view)
            view.updatePadding(left = bars.left, right = bars.right)
            insets
        }
    }

    companion object {
        private const val KEY_INTENT_USED = BuildConfig.APPLICATION_ID + ".key.FILE_INTENT_USED"
    }
}
