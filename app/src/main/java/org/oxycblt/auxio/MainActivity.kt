package org.oxycblt.auxio

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import org.oxycblt.auxio.databinding.ActivityMainBinding
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.system.PlaybackService
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.Accent
import org.oxycblt.auxio.ui.isEdgeOn

/**
 * The single [AppCompatActivity] for Auxio.
 */
class MainActivity : AppCompatActivity() {
    private val playbackModel: PlaybackViewModel by viewModels()

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

        // onNewIntent doesnt automatically call on startup, so call it here.
        onNewIntent(intent)

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

        // If this intent is a valid view intent that has not been used already, give it
        // to PlaybackViewModel to be used later.
        if (intent != null) {
            val action = intent.action
            val isConsumed = intent.getBooleanExtra(KEY_INTENT_USED, false)

            if (action == Intent.ACTION_VIEW && !isConsumed) {
                // Mark the intent as used so this does not fire again
                intent.putExtra(KEY_INTENT_USED, true)

                intent.data?.let { fileUri ->
                    playbackModel.playWithUri(fileUri, this)
                }
            }
        }
    }

    private fun doEdgeToEdgeSetup(binding: ActivityMainBinding) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Do modern edge to edge, which happens to be around twice the size of the
            // old way of doing things. Thanks android, very cool!
            logD("Doing R+ edge-to-edge.")

            window?.setDecorFitsSystemWindows(false)

            binding.root.setOnApplyWindowInsetsListener { _, insets ->
                WindowInsets.Builder()
                    .setInsets(
                        WindowInsets.Type.systemBars(),
                        insets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                    )
                    .build()
            }
        } else {
            // Do old edge-to-edge otherwise.
            logD("Doing legacy edge-to-edge.")

            @Suppress("DEPRECATION")
            binding.root.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    companion object {
        private const val KEY_INTENT_USED = "KEY_FILE_INTENT_USED"
    }
}
