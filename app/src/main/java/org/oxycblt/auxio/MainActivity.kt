package org.oxycblt.auxio

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import org.oxycblt.auxio.playback.PlaybackService
import org.oxycblt.auxio.ui.accent

// FIXME: Fix bug where fast navigation will break the animations and
//  lead to nothing being displayed [Possibly Un-fixable]
// FIXME: Compat issues with Versions 5/6 that cause recyclerview
//  dividers not to show and for progress bars to look wonky
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
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
