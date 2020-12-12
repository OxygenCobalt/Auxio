package org.oxycblt.auxio

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import org.oxycblt.auxio.databinding.ActivityMainBinding
import org.oxycblt.auxio.playback.PlaybackService
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.accent
import org.oxycblt.auxio.ui.handleTransparentSystemBars
import org.oxycblt.auxio.ui.toColor

// FIXME: Fix bug where fast navigation will break the animations and
//  lead to nothing being displayed [Possibly Un-fixable]
// TODO: Landscape UI layouts
// FIXME: Compat issue with Versions 5 that leads to progress bar looking off
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- UI SETUP ---

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this, R.layout.activity_main
        )

        val settingsManager = SettingsManager.init(applicationContext)

        AppCompatDelegate.setDefaultNightMode(settingsManager.theme)

        accent = settingsManager.accent

        // Apply the theme
        setTheme(accent.second)

        if (settingsManager.edgeEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            doEdgeToEdgeSetup(binding)
        }
    }

    override fun onStart() {
        super.onStart()

        Intent(this, PlaybackService::class.java).also {
            startService(it)
        }
    }

    @Suppress("DEPRECATION")
    private fun doEdgeToEdgeSetup(binding: ActivityMainBinding) {
        window?.apply {
            statusBarColor = Color.TRANSPARENT

            // Use a heavily transparent scrim on the nav bar as full transparency is borked
            navigationBarColor = R.color.nav_color.toColor(this@MainActivity)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Do modern edge to edge [Which is really a shot in the dark tbh]
                Log.d(this::class.simpleName, "Doing R+ edge-to-edge.")

                setDecorFitsSystemWindows(false)

                binding.root.setOnApplyWindowInsetsListener { _, insets ->
                    WindowInsets.Builder()
                        .setInsets(
                            WindowInsets.Type.systemBars(),
                            insets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                        )
                        .build()
                }
            } else {
                // Do old edge-to-edge otherwise
                Log.d(this::class.simpleName, "Doing legacy edge-to-edge.")

                binding.root.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }

            handleTransparentSystemBars(resources.configuration)
        }
    }
}
