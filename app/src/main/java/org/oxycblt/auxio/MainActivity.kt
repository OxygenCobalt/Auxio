package org.oxycblt.auxio

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import org.oxycblt.auxio.playback.PlaybackService
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.accent

// FIXME: Fix bug where fast navigation will break the animations and
//  lead to nothing being displayed [Possibly Un-fixable]
// TODO: Landscape UI layouts
// FIXME: Compat issue with Versions 5 that leads to progress bar looking off
class MainActivity : AppCompatActivity(R.layout.activity_main), SettingsManager.Callback {
    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        val settingsManager = SettingsManager.init(applicationContext)

        AppCompatDelegate.setDefaultNightMode(
            settingsManager.getTheme()
        )

        // Apply the theme
        setTheme(accent.second)

        return super.onCreateView(name, context, attrs)
    }

    override fun onStart() {
        super.onStart()

        Intent(this, PlaybackService::class.java).also {
            startService(it)
        }
    }

    override fun onResume() {
        super.onResume()

        // Perform callback additions/removals in onPause/onResume so that they are always
        // ran when the activity is recreated.
        SettingsManager.getInstance().addCallback(this)
    }

    override fun onPause() {
        super.onPause()

        SettingsManager.getInstance().removeCallback(this)
    }

    override fun onThemeUpdate(value: Int) {
        AppCompatDelegate.setDefaultNightMode(value)
    }
}
