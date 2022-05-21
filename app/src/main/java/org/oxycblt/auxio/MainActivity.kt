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
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.updatePadding
import org.oxycblt.auxio.databinding.ActivityMainBinding
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.system.PlaybackService
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.util.isNight
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.replaceSystemBarInsetsCompat
import org.oxycblt.auxio.util.systemBarInsetsCompat

/**
 * The single [AppCompatActivity] for Auxio.
 *
 * TODO: Add a new view for crashes with a stack trace
 *
 * TODO: Custom language support
 *
 * TODO: Rework menus [perhaps add multi-select]
 *
 * TODO: Rework some fragments to use listeners *even more*
 *
 * TODO: Fix how selection works in the RecyclerViews (doing it poorly right now)
 *
 * TODO: Rework padding ethos
 */
class MainActivity : AppCompatActivity() {
    private val playbackModel: PlaybackViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTheme()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyEdgeToEdgeWindow(binding.root)

        logD("Activity created")
    }

    override fun onStart() {
        super.onStart()

        startService(Intent(this, PlaybackService::class.java))

        // onNewIntent doesn't automatically call on startup, so call it here.
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // If this intent is a valid view intent that has not been used already, give it
        // to PlaybackViewModel to be used later.
        if (intent != null) {
            val action = intent.action
            val isConsumed = intent.getBooleanExtra(KEY_INTENT_USED, false)

            if (action == Intent.ACTION_VIEW && !isConsumed) {
                // Mark the intent as used so this does not fire again
                intent.putExtra(KEY_INTENT_USED, true)
                intent.data?.let { fileUri -> playbackModel.play(fileUri, this) }
            }
        }
    }

    private fun setupTheme() {
        val settingsManager = SettingsManager.getInstance()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12, let dynamic colors be our accent and only enable the black theme option
            if (isNight && settingsManager.useBlackTheme) {
                logD("Applying black theme [dynamic colors]")
                setTheme(R.style.Theme_Auxio_Black)
            }
        } else {
            // Below android 12, load the accent and enable theme customization
            AppCompatDelegate.setDefaultNightMode(settingsManager.theme)
            val accent = settingsManager.accent

            // The black theme has a completely separate set of styles since style attributes cannot
            // be modified at runtime.
            if (isNight && settingsManager.useBlackTheme) {
                logD("Applying black theme [accent $accent]")
                setTheme(accent.blackTheme)
            } else {
                logD("Applying normal theme [accent $accent]")
                setTheme(accent.theme)
            }
        }
    }

    private fun applyEdgeToEdgeWindow(contentView: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            logD("Doing R+ edge-to-edge")

            window?.setDecorFitsSystemWindows(false)

            // Instead of automatically fetching these insets and exposing them,
            // the R+ SDK decides to make you specify the insets yourself with a barely
            // documented API that isn't even mentioned in any of the edge-to-edge
            // tutorials. Thanks android, very cool!
            contentView.setOnApplyWindowInsetsListener { view, insets ->
                WindowInsets.Builder()
                    .setInsets(
                        WindowInsets.Type.systemBars(),
                        insets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars()))
                    .setInsets(
                        WindowInsets.Type.systemGestures(),
                        insets.getInsetsIgnoringVisibility(WindowInsets.Type.systemGestures()))
                    .build()
                    .applyLeftRightInsets(view)
            }
        } else {
            // Do old edge-to-edge otherwise.
            logD("Doing legacy edge-to-edge")

            @Suppress("DEPRECATION")
            contentView.apply {
                systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

                setOnApplyWindowInsetsListener { view, insets -> insets.applyLeftRightInsets(view) }
            }
        }
    }

    private fun WindowInsets.applyLeftRightInsets(contentView: View): WindowInsets {
        val bars = systemBarInsetsCompat
        contentView.updatePadding(left = bars.left, right = bars.right)
        return replaceSystemBarInsetsCompat(0, bars.top, 0, bars.bottom)
    }

    companion object {
        private const val KEY_INTENT_USED = BuildConfig.APPLICATION_ID + ".key.FILE_INTENT_USED"
    }
}
