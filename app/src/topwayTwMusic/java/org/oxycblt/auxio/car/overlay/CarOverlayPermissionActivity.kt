/*
 * Copyright (c) 2024 Auxio Project
 * CarOverlayPermissionActivity.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.oxycblt.auxio.car.overlay

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.oxycblt.auxio.R

/**
 * Simple activity to guide users through the overlay permission setup. Checks permission on resume
 * and starts the overlay service when granted.
 */
class CarOverlayPermissionActivity : AppCompatActivity() {

    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout =
            LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                val pad = (24 * resources.displayMetrics.density).toInt()
                setPadding(pad, pad, pad, pad)
            }

        val title =
            TextView(this).apply {
                text = getString(R.string.car_overlay_permission_title)
                textSize = 20f
            }
        layout.addView(title)

        statusText =
            TextView(this).apply {
                textSize = 16f
                val topMargin = (16 * resources.displayMetrics.density).toInt()
                layoutParams =
                    LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                        )
                        .apply { setMargins(0, topMargin, 0, topMargin) }
            }
        layout.addView(statusText)

        val grantButton =
            Button(this).apply {
                text = getString(R.string.car_overlay_permission_grant_button)
                setOnClickListener { openOverlaySettings() }
            }
        layout.addView(grantButton)

        val doneButton =
            Button(this).apply {
                text = getString(R.string.car_overlay_permission_done_button)
                setOnClickListener { finish() }
            }
        layout.addView(doneButton)

        setContentView(layout)
    }

    override fun onResume() {
        super.onResume()
        if (Settings.canDrawOverlays(this)) {
            statusText.text = getString(R.string.car_overlay_permission_granted)
            val prefs = CarOverlayPrefs.from(this)
            if (prefs.enabled) {
                CarFloatingControlsService.start(this)
            }
        } else {
            statusText.text = getString(R.string.car_overlay_permission_not_granted)
        }
    }

    private fun openOverlaySettings() {
        val intent =
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        startActivity(intent)
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, CarOverlayPermissionActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}
