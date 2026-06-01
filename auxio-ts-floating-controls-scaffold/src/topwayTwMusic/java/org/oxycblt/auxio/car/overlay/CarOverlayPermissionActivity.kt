package org.oxycblt.auxio.car.overlay

import android.app.Activity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class CarOverlayPermissionActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        val message = TextView(this).apply {
            text = "Auxio needs Display over other apps permission to show car floating controls over launcher, navigation, and radio screens."
            textSize = 18f
        }
        val button = Button(this).apply {
            text = "Open overlay permission"
            setOnClickListener { startActivity(CarFloatingControlsService.permissionIntent(this@CarOverlayPermissionActivity)) }
        }
        val done = Button(this).apply {
            text = "Done"
            setOnClickListener {
                if (Settings.canDrawOverlays(this@CarOverlayPermissionActivity)) {
                    CarFloatingControlsService.start(this@CarOverlayPermissionActivity)
                }
                finish()
            }
        }
        layout.addView(message)
        layout.addView(button)
        layout.addView(done)
        setContentView(layout)
    }
}
