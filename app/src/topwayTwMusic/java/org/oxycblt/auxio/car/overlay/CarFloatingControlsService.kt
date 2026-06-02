/*
 * Copyright (c) 2024 Auxio Project
 * CarFloatingControlsService.kt is part of Auxio.
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

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.playback.service.PlaybackActions
import timber.log.Timber as L

/**
 * Foreground service managing the car floating controls overlay window. Handles overlay lifecycle,
 * drag persistence, and visibility toggling based on Auxio foreground state.
 *
 * Playback commands are dispatched via Auxio's own broadcast actions ([PlaybackActions]), which
 * route deterministically to [org.oxycblt.auxio.playback.service.SystemPlaybackReceiver] and then
 * to [org.oxycblt.auxio.playback.state.PlaybackStateManager]. This is the same path used by
 * notifications and widgets -- not generic media key dispatch.
 */
class CarFloatingControlsService : Service(), CarFloatingControlsView.Callbacks {

    private lateinit var prefs: CarOverlayPrefs
    private var overlayView: CarFloatingControlsView? = null
    private var windowManager: WindowManager? = null
    private var isOverlayAttached = false
    private var isAuxioForeground = false

    override fun onCreate() {
        super.onCreate()
        prefs = CarOverlayPrefs.from(this)
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        L.d("CarFloatingControlsService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            // Null intent means system restarted a sticky service. Check if still enabled.
            if (!prefs.enabled || !Settings.canDrawOverlays(this)) {
                stopSelfCleanly()
                return START_NOT_STICKY
            }
            startOverlayRuntime()
            return START_NOT_STICKY
        }

        when (intent.action) {
            ACTION_START -> {
                if (!Settings.canDrawOverlays(this)) {
                    L.w("Overlay permission not granted, stopping")
                    stopSelfCleanly()
                    return START_NOT_STICKY
                }
                startOverlayRuntime()
            }
            ACTION_STOP -> {
                stopOverlayRuntime()
                stopSelfCleanly()
            }
            ACTION_SHOW -> showOverlayIfAllowed()
            ACTION_HIDE -> hideOverlay()
            ACTION_TOGGLE -> {
                if (isOverlayAttached) hideOverlay() else showOverlayIfAllowed()
            }
            ACTION_RESET_POSITION -> {
                prefs.resetPosition()
                if (isOverlayAttached) {
                    updateOverlayPosition(prefs.positionX, prefs.positionY)
                }
                // Don't keep the service running just for a position reset
                if (!prefs.enabled) {
                    stopSelfCleanly()
                }
            }
            ACTION_AUXIO_FOREGROUND_CHANGED -> {
                isAuxioForeground = intent.getBooleanExtra(EXTRA_AUXIO_FOREGROUND, false)
                if (isAuxioForeground && prefs.hideWhileAuxioForeground) {
                    hideOverlay()
                } else if (!isAuxioForeground && prefs.enabled) {
                    showOverlayIfAllowed()
                }
            }
            else -> L.w("Unknown action: ${intent.action}")
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        removeOverlay()
        L.d("CarFloatingControlsService destroyed")
        super.onDestroy()
    }

    // --- Overlay lifecycle ---

    private fun startOverlayRuntime() {
        promoteForeground()
        if (!isOverlayAttached) {
            showOverlayIfAllowed()
        }
    }

    private fun stopOverlayRuntime() {
        removeOverlay()
    }

    private fun showOverlayIfAllowed() {
        if (!Settings.canDrawOverlays(this)) {
            L.w("Cannot show overlay: permission revoked")
            return
        }
        if (isOverlayAttached) return
        if (isAuxioForeground && prefs.hideWhileAuxioForeground) return

        val view = CarFloatingControlsView(this, this)
        view.applyOpacity(prefs.opacityPercent)

        val params = createLayoutParams()
        params.x = prefs.positionX
        params.y = prefs.positionY

        windowManager?.addView(view, params)
        overlayView = view
        isOverlayAttached = true
        L.d("Overlay attached at (${params.x}, ${params.y})")
    }

    private fun hideOverlay() {
        if (!isOverlayAttached) return
        removeOverlay()
    }

    private fun removeOverlay() {
        overlayView?.let { view ->
            try {
                windowManager?.removeView(view)
            } catch (e: IllegalArgumentException) {
                L.w("View already removed from window")
            }
        }
        overlayView = null
        isOverlayAttached = false
    }

    private fun updateOverlayPosition(x: Int, y: Int) {
        val view = overlayView ?: return
        val params = view.layoutParams as? WindowManager.LayoutParams ?: return
        params.x = x
        params.y = y
        try {
            windowManager?.updateViewLayout(view, params)
        } catch (e: IllegalArgumentException) {
            L.w("Cannot update layout: view not attached")
        }
    }

    private fun createLayoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }
    }

    // --- Foreground notification ---

    private fun promoteForeground() {
        val nm = NotificationManagerCompat.from(this)
        val channel =
            NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW)
                .setName(getString(R.string.car_overlay_notification_channel))
                .setShowBadge(false)
                .setLightsEnabled(false)
                .setVibrationEnabled(false)
                .build()
        nm.createNotificationChannel(channel)

        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_auxio_24)
                .setContentTitle(getString(R.string.car_overlay_notification_title))
                .setContentText(getString(R.string.car_overlay_notification_text))
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            0 // No specific foreground service type needed for overlay-only service
        )
    }

    private fun stopSelfCleanly() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    // --- CarFloatingControlsView.Callbacks ---

    override fun onDrag(deltaX: Int, deltaY: Int) {
        val view = overlayView ?: return
        val params = view.layoutParams as? WindowManager.LayoutParams ?: return
        params.x += deltaX
        params.y += deltaY
        try {
            windowManager?.updateViewLayout(view, params)
        } catch (e: IllegalArgumentException) {
            L.w("Cannot update layout during drag")
        }
    }

    override fun onDragFinished(x: Int, y: Int) {
        val view = overlayView ?: return
        val params = view.layoutParams as? WindowManager.LayoutParams ?: return
        prefs.positionX = params.x
        prefs.positionY = params.y
    }

    override fun onPrevious() {
        sendPlaybackBroadcast(PlaybackActions.ACTION_SKIP_PREV)
    }

    override fun onPlayPause() {
        sendPlaybackBroadcast(PlaybackActions.ACTION_PLAY_PAUSE)
    }

    override fun onNext() {
        sendPlaybackBroadcast(PlaybackActions.ACTION_SKIP_NEXT)
    }

    override fun onOpenAuxio() {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(launchIntent)
        }
    }

    override fun onStopRequested() {
        L.d("Stop requested via triple-tap")
        stopOverlayRuntime()
        stopSelfCleanly()
    }

    /**
     * Dispatches a playback command via Auxio's own broadcast actions. These route to
     * [org.oxycblt.auxio.playback.service.SystemPlaybackReceiver] which is registered with an
     * exported intent filter and dispatches directly to PlaybackStateManager.
     */
    private fun sendPlaybackBroadcast(action: String) {
        val intent = Intent(action)
        intent.setPackage(packageName)
        sendBroadcast(intent)
    }

    companion object {
        private const val CHANNEL_ID = "auxio_car_overlay_channel"
        private const val NOTIFICATION_ID = 42

        const val ACTION_START = BuildConfig.APPLICATION_ID + ".car.overlay.START"
        const val ACTION_STOP = BuildConfig.APPLICATION_ID + ".car.overlay.STOP"
        const val ACTION_SHOW = BuildConfig.APPLICATION_ID + ".car.overlay.SHOW"
        const val ACTION_HIDE = BuildConfig.APPLICATION_ID + ".car.overlay.HIDE"
        const val ACTION_TOGGLE = BuildConfig.APPLICATION_ID + ".car.overlay.TOGGLE"
        const val ACTION_RESET_POSITION =
            BuildConfig.APPLICATION_ID + ".car.overlay.RESET_POSITION"
        const val ACTION_AUXIO_FOREGROUND_CHANGED =
            BuildConfig.APPLICATION_ID + ".car.overlay.AUXIO_FG_CHANGED"
        const val EXTRA_AUXIO_FOREGROUND = "extra_auxio_foreground"

        fun start(context: Context) {
            if (!Settings.canDrawOverlays(context)) return
            val intent = Intent(context, CarFloatingControlsService::class.java)
            intent.action = ACTION_START
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, CarFloatingControlsService::class.java))
        }

        fun setAuxioForeground(context: Context, isForeground: Boolean) {
            val prefs = CarOverlayPrefs.from(context)
            if (!prefs.enabled) return
            val intent = Intent(context, CarFloatingControlsService::class.java)
            intent.action = ACTION_AUXIO_FOREGROUND_CHANGED
            intent.putExtra(EXTRA_AUXIO_FOREGROUND, isForeground)
            context.startService(intent)
        }

        fun resetPosition(context: Context) {
            val intent = Intent(context, CarFloatingControlsService::class.java)
            intent.action = ACTION_RESET_POSITION
            context.startService(intent)
        }
    }
}
