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
import org.oxycblt.auxio.ui.isNight

/**
 * The single [AppCompatActivity] for Auxio.
 * TODO: Port widgets to non-12 android
 */
class MainActivity : AppCompatActivity() {
    private val playbackModel: PlaybackViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTheme()

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this, R.layout.activity_main
        )

        if (isEdgeOn()) {
            setupEdgeToEdge(binding)
        }

        logD("Activity created.")
    }

    override fun onStart() {
        super.onStart()

        // Start PlaybackService
        startService(Intent(this, PlaybackService::class.java))

        // onNewIntent doesnt automatically call on startup, so call it here.
        onNewIntent(intent)
    }

    private fun setupTheme() {
        // Update the current accent and theme
        val settingsManager = SettingsManager.getInstance()
        AppCompatDelegate.setDefaultNightMode(settingsManager.theme)

        val newAccent = Accent.set(settingsManager.accent)

        // The black theme has a completely separate set of styles since style attributes cannot
        // be modified at runtime.
        if (isNight() && settingsManager.useBlackTheme) {
            setTheme(newAccent.blackTheme)
        } else {
            setTheme(newAccent.theme)
        }
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

    private fun setupEdgeToEdge(binding: ActivityMainBinding) {
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
        private const val KEY_INTENT_USED = BuildConfig.APPLICATION_ID + ".key.FILE_INTENT_USED"
    }
}
