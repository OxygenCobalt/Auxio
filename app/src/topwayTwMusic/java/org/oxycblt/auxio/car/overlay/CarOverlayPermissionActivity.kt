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
 * and, if a pending-enable flag is set, enables and starts the overlay service when granted.
 */
class CarOverlayPermissionActivity : AppCompatActivity() {

    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val density = resources.displayMetrics.density
        val padPx = (PADDING_DP * density).toInt()
        val marginPx = (MARGIN_DP * density).toInt()

        val layout =
            LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(padPx, padPx, padPx, padPx)
            }

        val title =
            TextView(this).apply {
                text = getString(R.string.car_overlay_permission_title)
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, TITLE_TEXT_SP)
            }
        layout.addView(title)

        statusText =
            TextView(this).apply {
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, BODY_TEXT_SP)
                layoutParams =
                    LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                        )
                        .apply { setMargins(0, marginPx, 0, marginPx) }
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
        val prefs = CarOverlayPrefs.from(this)
        if (Settings.canDrawOverlays(this)) {
            statusText.text = getString(R.string.car_overlay_permission_granted)
            // Complete the pending-enable flow if permission was just granted.
            if (prefs.pendingEnable) {
                prefs.pendingEnable = false
                prefs.enabled = true
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

    private companion object {
        const val PADDING_DP = 24f
        const val MARGIN_DP = 16f
        const val TITLE_TEXT_SP = 20f
        const val BODY_TEXT_SP = 16f
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, CarOverlayPermissionActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}
