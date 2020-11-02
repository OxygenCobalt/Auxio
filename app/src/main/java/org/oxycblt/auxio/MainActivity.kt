package org.oxycblt.auxio

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import org.oxycblt.auxio.playback.PlaybackService
import org.oxycblt.auxio.theme.accent

// FIXME: Fix bug where fast navigation will break the animations and
//  lead to nothing being displayed [Possibly Un-fixable]
// TODO: Test for compatibility
//  API 30    - No Issues
//  API 29    - No Issues [Primary Testing Version]
//  API 28-23 - Not tested yet
//  API 22    - ProgressBar/SeekBar look wonky, RecyclerView dividers don't show
//  API 21    - Not tested yet
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
