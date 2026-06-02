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
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
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
            // Null intent: system restarted the service after process death.
            // Re-establish the overlay if still enabled and permitted; otherwise stop cleanly.
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
                // Only reposition a live overlay. Position prefs are already updated by caller.
                if (isOverlayAttached) {
                    val (cx, cy) = clampPosition(prefs.positionX, prefs.positionY)
                    prefs.positionX = cx
                    prefs.positionY = cy
                    updateOverlayPosition(cx, cy)
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
            else -> {
                L.w("Unknown action: ${intent.action}, stopping idle service")
                stopSelfCleanly()
            }
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
        if (!promoteForeground()) {
            // Foreground promotion failed — stop cleanly.
            stopSelfCleanly()
            return
        }
        if (!isOverlayAttached) {
            showOverlayIfAllowed()
        }
    }

    private fun stopOverlayRuntime() {
        removeOverlay()
    }

    private fun showOverlayIfAllowed() {
        if (!Settings.canDrawOverlays(this)) {
            L.w("Cannot show overlay: permission revoked, stopping")
            removeOverlay()
            stopSelfCleanly()
            return
        }
        if (isOverlayAttached) return
        if (isAuxioForeground && prefs.hideWhileAuxioForeground) return

        val view = CarFloatingControlsView(this, this)
        view.applyOpacity(prefs.opacityPercent)

        val params = createLayoutParams()
        val (cx, cy) = clampPosition(prefs.positionX, prefs.positionY)
        params.x = cx
        params.y = cy
        // Persist clamped position in case old prefs were out-of-bounds.
        prefs.positionX = cx
        prefs.positionY = cy

        try {
            windowManager?.addView(view, params)
        } catch (e: Exception) {
            L.e(e, "Failed to add overlay view, stopping")
            stopSelfCleanly()
            return
        }
        overlayView = view
        isOverlayAttached = true
        L.d("Overlay attached at ($cx, $cy)")
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
            } catch (e: Exception) {
                L.w(e, "Unexpected error removing overlay view")
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
        } catch (e: Exception) {
            L.w(e, "Unexpected error updating overlay position")
        }
    }

    private fun createLayoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT,
            )
            .apply { gravity = Gravity.TOP or Gravity.START }
    }

    // --- Position clamping ---

    /**
     * Clamps overlay coordinates to the visible usable area, accounting for TS18-style system bars.
     * On Android 10 fallback, uses display size minus known TS18 bar insets.
     */
    private fun clampPosition(x: Int, y: Int): Pair<Int, Int> {
        val display = windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)
        val screenW = if (size.x > 0) size.x else DEFAULT_SCREEN_WIDTH
        val screenH = if (size.y > 0) size.y else DEFAULT_SCREEN_HEIGHT

        // Approximate usable area accounting for system bars.
        val minX = 0
        val minY = STATUS_BAR_INSET_PX
        val maxX = (screenW - NAV_BAR_INSET_PX - OVERLAY_ESTIMATED_WIDTH_PX).coerceAtLeast(minX)
        val maxY = (screenH - OVERLAY_ESTIMATED_HEIGHT_PX).coerceAtLeast(minY)

        return x.coerceIn(minX, maxX) to y.coerceIn(minY, maxY)
    }

    // --- Foreground notification ---

    /**
     * Promotes the service to foreground. Returns true on success, false if foreground promotion
     * failed (e.g., on Android 10 where specialUse is not supported but manifest declares it).
     */
    private fun promoteForeground(): Boolean {
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

        return try {
            val serviceType = foregroundServiceType()
            ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, serviceType)
            true
        } catch (e: Exception) {
            L.e(e, "Failed to promote to foreground")
            false
        }
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
        val (cx, cy) = clampPosition(params.x, params.y)
        params.x = cx
        params.y = cy
        try {
            windowManager?.updateViewLayout(view, params)
        } catch (_: Exception) {}
        prefs.positionX = cx
        prefs.positionY = cy
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
        sendBroadcast(Intent(action).apply { setPackage(packageName) })
    }

    companion object {
        private const val CHANNEL_ID = "auxio_car_overlay_channel"
        private const val NOTIFICATION_ID = 42

        // TS18-specific defaults for position clamping.
        private const val DEFAULT_SCREEN_WIDTH = 1280
        private const val DEFAULT_SCREEN_HEIGHT = 720
        private const val STATUS_BAR_INSET_PX = 55
        private const val NAV_BAR_INSET_PX = 55
        private const val OVERLAY_ESTIMATED_WIDTH_PX = 350
        private const val OVERLAY_ESTIMATED_HEIGHT_PX = 80

        const val ACTION_START = BuildConfig.APPLICATION_ID + ".car.overlay.START"
        const val ACTION_STOP = BuildConfig.APPLICATION_ID + ".car.overlay.STOP"
        const val ACTION_SHOW = BuildConfig.APPLICATION_ID + ".car.overlay.SHOW"
        const val ACTION_HIDE = BuildConfig.APPLICATION_ID + ".car.overlay.HIDE"
        const val ACTION_TOGGLE = BuildConfig.APPLICATION_ID + ".car.overlay.TOGGLE"
        const val ACTION_RESET_POSITION = BuildConfig.APPLICATION_ID + ".car.overlay.RESET_POSITION"
        const val ACTION_AUXIO_FOREGROUND_CHANGED =
            BuildConfig.APPLICATION_ID + ".car.overlay.AUXIO_FG_CHANGED"
        const val EXTRA_AUXIO_FOREGROUND = "extra_auxio_foreground"

        /**
         * Returns the appropriate foreground service type for the current API level.
         * `FOREGROUND_SERVICE_TYPE_SPECIAL_USE` is only valid on API 34+. On older APIs, use 0
         * (none) which is the legacy behaviour and is accepted by ServiceCompat.
         */
        fun foregroundServiceType(): Int {
            return if (Build.VERSION.SDK_INT >= 34) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            } else {
                0
            }
        }

        fun start(context: Context) {
            if (!Settings.canDrawOverlays(context)) return
            val intent = Intent(context, CarFloatingControlsService::class.java)
            intent.action = ACTION_START
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, CarFloatingControlsService::class.java))
        }

        /**
         * Signals foreground/background change to a running service. Does NOT cold-start the
         * service. If the service is not running, startService with a non-start action on a
         * non-running service is safe (it will trigger onCreate + onStartCommand, which will detect
         * the service is not in foreground mode and stop itself).
         */
        fun setAuxioForeground(context: Context, isForeground: Boolean) {
            val prefs = CarOverlayPrefs.from(context)
            if (!prefs.enabled) return
            if (!Settings.canDrawOverlays(context)) return
            val intent = Intent(context, CarFloatingControlsService::class.java)
            intent.action = ACTION_AUXIO_FOREGROUND_CHANGED
            intent.putExtra(EXTRA_AUXIO_FOREGROUND, isForeground)
            // Use startService (not startForegroundService) since the service should already be
            // in foreground. If it isn't running, this is a no-op on API 26+ when the app is in
            // background. That's acceptable — we only want to signal a running service.
            try {
                context.startService(intent)
            } catch (e: IllegalStateException) {
                // App is in background and service is not running — expected on API 26+.
                L.d("Cannot signal foreground change: service not running")
            }
        }

        /**
         * Sends a position-reset command to an already-running service. Does NOT cold-start the
         * service. Position prefs are already updated by the caller.
         */
        fun resetPositionIfRunning(context: Context) {
            val intent = Intent(context, CarFloatingControlsService::class.java)
            intent.action = ACTION_RESET_POSITION
            try {
                context.startService(intent)
            } catch (e: IllegalStateException) {
                L.d("Cannot reset position: service not running")
            }
        }
    }
}
