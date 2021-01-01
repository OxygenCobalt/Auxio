package org.oxycblt.auxio

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import coil.Coil
import org.oxycblt.auxio.coil.createImageLoader
import org.oxycblt.auxio.databinding.ActivityMainBinding
import org.oxycblt.auxio.playback.PlaybackService
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.accent
import org.oxycblt.auxio.ui.isEdgeOn

/**
 * The single [AppCompatActivity] for Auxio.
 */
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

        if (isEdgeOn()) {
            doEdgeToEdgeSetup(binding)
        }

        Coil.setImageLoader(createImageLoader(applicationContext))
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
