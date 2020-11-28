package org.oxycblt.auxio

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.oxycblt.auxio.playback.PlaybackService
import org.oxycblt.auxio.prefs.PrefsManager
import org.oxycblt.auxio.ui.accent

// FIXME: Fix bug where fast navigation will break the animations and
//  lead to nothing being displayed [Possibly Un-fixable]
// TODO: Landscape UI layouts
// FIXME: Compat issue with Versions 5 that leads to progress bar looking off
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        val prefsManager = PrefsManager.init(this)

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
}
