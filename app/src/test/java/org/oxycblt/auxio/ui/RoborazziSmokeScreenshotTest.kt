package org.oxycblt.auxio.ui

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.test.core.app.ApplicationProvider
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import org.junit.runner.RunWith
import org.oxycblt.auxio.R
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class RoborazziSmokeScreenshotTest {
    @Test
    fun capturePlaybackBarLayout() {
        val base = ApplicationProvider.getApplicationContext<android.content.Context>()
        val themed = ContextThemeWrapper(base, R.style.Theme_Auxio)
        val view = LayoutInflater.from(themed).inflate(R.layout.fragment_playback_bar, null, false)
        view.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(
                1280,
                android.view.View.MeasureSpec.EXACTLY,
            ),
            android.view.View.MeasureSpec.makeMeasureSpec(
                720,
                android.view.View.MeasureSpec.AT_MOST,
            ),
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.captureRoboImage("playback-bar-landscape")
    }
}
