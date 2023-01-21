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
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.core.view.updatePadding
import org.oxycblt.auxio.databinding.ActivityMainBinding
import org.oxycblt.auxio.music.system.IndexerService
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.state.InternalPlayer
import org.oxycblt.auxio.playback.system.PlaybackService
import org.oxycblt.auxio.ui.UISettings
import org.oxycblt.auxio.util.androidViewModels
import org.oxycblt.auxio.util.isNight
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.systemBarInsetsCompat

/**
 * Auxio's single [AppCompatActivity].
 *
 * TODO: Add error screens
 *
 * TODO: Custom language support
 *
 * TODO: Use proper material attributes (Not the weird dimen attributes I currently have)
 *
 * TODO: Migrate to material animation system
 *
 * TODO: Unit testing
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class MainActivity : AppCompatActivity() {
    private val playbackModel: PlaybackViewModel by androidViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTheme()
        // Inflate the views after setting up the theme so that the theme attributes are applied.
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupEdgeToEdge(binding.root)
        logD("Activity created")
    }

    override fun onStart() {
        super.onStart()

        startService(Intent(this, IndexerService::class.java))
        startService(Intent(this, PlaybackService::class.java))

        if (!startIntentAction(intent)) {
            // No intent action to do, just restore the previously saved state.
            playbackModel.startAction(InternalPlayer.Action.RestoreState)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        startIntentAction(intent)
    }

    private fun setupTheme() {
        val settings = UISettings.from(this)
        // Apply the theme configuration.
        AppCompatDelegate.setDefaultNightMode(settings.theme)
        // Apply the color scheme. The black theme requires it's own set of themes since
        // it's not possible to modify the themes at run-time.
        if (isNight && settings.useBlackTheme) {
            logD("Applying black theme [accent ${settings.accent}]")
            setTheme(settings.accent.blackTheme)
        } else {
            logD("Applying normal theme [accent ${settings.accent}]")
            setTheme(settings.accent.theme)
        }
    }

    private fun setupEdgeToEdge(contentView: View) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        contentView.setOnApplyWindowInsetsListener { view, insets ->
            // Automatically inset the view to the left/right, as component support for
            // these insets are highly lacking.
            val bars = insets.systemBarInsetsCompat
            view.updatePadding(left = bars.left, right = bars.right)
            insets
        }
    }

    /**
     * Transform an [Intent] given to [MainActivity] into a [InternalPlayer.Action] that can be used
     * in the playback system.
     * @param intent The (new) [Intent] given to this [MainActivity], or null if there is no intent.
     * @return true If the analogous [InternalPlayer.Action] to the given [Intent] was started,
     * false otherwise.
     */
    private fun startIntentAction(intent: Intent?): Boolean {
        if (intent == null) {
            // Nothing to do.
            return false
        }

        if (intent.getBooleanExtra(KEY_INTENT_USED, false)) {
            // Don't commit the action, but also return that the intent was applied.
            // This is because onStart can run multiple times, and thus we really don't
            // want to return false and override the original delayed action with a
            // RestoreState action.
            return true
        }
        intent.putExtra(KEY_INTENT_USED, true)

        val action =
            when (intent.action) {
                Intent.ACTION_VIEW -> InternalPlayer.Action.Open(intent.data ?: return false)
                Auxio.INTENT_KEY_SHORTCUT_SHUFFLE -> InternalPlayer.Action.ShuffleAll
                else -> return false
            }
        playbackModel.startAction(action)
        return true
    }

    private companion object {
        const val KEY_INTENT_USED = BuildConfig.APPLICATION_ID + ".key.FILE_INTENT_USED"
    }
}
