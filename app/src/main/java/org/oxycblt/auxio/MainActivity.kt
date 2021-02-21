package org.oxycblt.auxio

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import org.oxycblt.auxio.databinding.ActivityMainBinding
import org.oxycblt.auxio.playback.system.PlaybackService
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.Accent
import org.oxycblt.auxio.ui.isEdgeOn

/**
 * The single [AppCompatActivity] for Auxio.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this, R.layout.activity_main
        )

        val settingsManager = SettingsManager.getInstance()

        AppCompatDelegate.setDefaultNightMode(settingsManager.theme)

        val accent = Accent.set(settingsManager.accent)

        // Apply the theme
        setTheme(accent.theme)

        if (isEdgeOn()) {
            doEdgeToEdgeSetup(binding)
        }
    }

    override fun onStart() {
        super.onStart()

        // Start PlaybackService
        startService(Intent(this, PlaybackService::class.java))
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // Since the activity is set to singleInstance [Given that there's only MainActivity]
        // We have to manually push the intent whenever we get one so that the fragments
        // can catch any file intents
        setIntent(intent)
    }

    @Suppress("DEPRECATION")
    private fun doEdgeToEdgeSetup(binding: ActivityMainBinding) {
        window?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Do modern edge to edge [Which is really a shot in the dark tbh]
                this@MainActivity.logD("Doing R+ edge-to-edge.")

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
                this@MainActivity.logD("Doing legacy edge-to-edge.")

                binding.root.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }
    }
}
