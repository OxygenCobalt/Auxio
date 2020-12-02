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

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this, R.layout.activity_main
        )

        val settingsManager = SettingsManager.init(applicationContext)

        AppCompatDelegate.setDefaultNightMode(
            settingsManager.getTheme()
        )

        accent = settingsManager.getAccent()

        // Apply the theme
        setTheme(accent.second)

        // If enabled and possible, go through a stupidly long & complicated process
        // just to get edge-to-edge to work.
        // TODO: Make the navigation bar fully transparent
        if (settingsManager.getEdgeToEdge() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            window?.apply {
                statusBarColor = Color.TRANSPARENT
                navigationBarColor = R.color.nav_color.toColor(this@MainActivity)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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
                    Log.d(this::class.simpleName, "Doing legacy edge-to-edge.")

                    binding.root.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                }

                handleTransparentSystemBars(resources.configuration)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        Intent(this, PlaybackService::class.java).also {
            startService(it)
        }
    }

    fun doThemeRecreate(newTheme: Int) {
        AppCompatDelegate.setDefaultNightMode(newTheme)
    }
}
