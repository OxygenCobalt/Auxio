package org.oxycblt.auxio.car.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class CarFloatingControlsService : Service(), CarFloatingControlsView.Callbacks {
    private lateinit var prefs: CarOverlayPrefs
    private lateinit var playback: CarOverlayPlaybackBridge
    private lateinit var windowManager: WindowManager
    private var overlayView: CarFloatingControlsView? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var auxioForeground: Boolean = false

    override fun onCreate() {
        super.onCreate()
        prefs = CarOverlayPrefs(this)
        playback = CarOverlayPlaybackBridge.create(this)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action ?: CarOverlayActions.ACTION_START) {
            CarOverlayActions.ACTION_START -> startOverlayRuntime()
            CarOverlayActions.ACTION_STOP -> stopOverlayRuntime()
            CarOverlayActions.ACTION_SHOW -> showOverlayIfAllowed()
            CarOverlayActions.ACTION_HIDE -> removeOverlay()
            CarOverlayActions.ACTION_TOGGLE -> if (overlayView == null) showOverlayIfAllowed() else removeOverlay()
            CarOverlayActions.ACTION_RESET_POSITION -> {
                prefs.resetPosition()
                updatePositionFromPrefs()
            }
            CarOverlayActions.ACTION_PREVIOUS -> onPrevious()
            CarOverlayActions.ACTION_PLAY_PAUSE -> onPlayPause()
            CarOverlayActions.ACTION_NEXT -> onNext()
            CarOverlayActions.ACTION_OPEN_AUXIO -> onOpenAuxio()
            CarOverlayActions.ACTION_AUXIO_FOREGROUND_CHANGED -> {
                auxioForeground = intent.getBooleanExtra(CarOverlayActions.EXTRA_AUXIO_FOREGROUND, false)
                if (auxioForeground && prefs.hideWhileAuxioForeground) {
                    removeOverlay()
                } else if (prefs.enabled) {
                    showOverlayIfAllowed()
                }
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        removeOverlay()
        super.onDestroy()
    }

    private fun startOverlayRuntime() {
        if (!prefs.enabled) prefs.enabled = true
        startForeground(NOTIFICATION_ID, buildNotification())
        showOverlayIfAllowed()
    }

    private fun stopOverlayRuntime() {
        prefs.enabled = false
        removeOverlay()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun showOverlayIfAllowed() {
        if (!prefs.enabled) return
        if (prefs.hideWhileAuxioForeground && auxioForeground) return
        if (!canDrawOverlays()) {
            removeOverlay()
            return
        }
        if (overlayView != null) return

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = prefs.x
            y = prefs.y
        }

        val view = CarFloatingControlsView(this, prefs, this)
        runCatching {
            windowManager.addView(view, params)
            overlayView = view
            layoutParams = params
        }.onFailure {
            overlayView = null
            layoutParams = null
        }
    }

    private fun removeOverlay() {
        val view = overlayView ?: return
        runCatching { windowManager.removeView(view) }
        overlayView = null
        layoutParams = null
    }

    private fun updatePositionFromPrefs() {
        val params = layoutParams ?: return
        params.x = prefs.x
        params.y = prefs.y
        overlayView?.let { runCatching { windowManager.updateViewLayout(it, params) } }
    }

    override fun onDrag(dx: Int, dy: Int) {
        val params = layoutParams ?: return
        params.x += dx
        params.y += dy
        overlayView?.let { runCatching { windowManager.updateViewLayout(it, params) } }
    }

    override fun onDragFinished() {
        layoutParams?.let {
            prefs.x = it.x
            prefs.y = it.y
        }
    }

    override fun onPrevious() = playback.previous()
    override fun onPlayPause() = playback.playPause()
    override fun onNext() = playback.next()
    override fun onOpenAuxio() = playback.openAuxio()

    override fun onStopRequested() {
        // Triple-tap on the drag handle stops the overlay as a recovery affordance.
        stopOverlayRuntime()
    }

    private fun canDrawOverlays(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)
    }

    private fun overlayType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
    }

    private fun buildNotification(): Notification {
        val stopIntent = Intent(this, CarFloatingControlsService::class.java).apply {
            action = CarOverlayActions.ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            pendingIntentFlags(),
        )
        val openIntent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val openPendingIntent = openIntent?.let {
            PendingIntent.getActivity(this, 2, it, pendingIntentFlags())
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(getStringResource("Auxio floating controls"))
            .setContentText(getStringResource("Media controls are available over other apps."))
            .setOngoing(true)
            .setContentIntent(openPendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, getStringResource("Stop"), stopPendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Auxio floating controls",
            NotificationManager.IMPORTANCE_LOW,
        )
        manager.createNotificationChannel(channel)
    }

    private fun pendingIntentFlags(): Int {
        return PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
    }

    private fun getStringResource(fallback: String): String = fallback

    companion object {
        private const val CHANNEL_ID = "auxio_car_floating_controls"
        private const val NOTIFICATION_ID = 32818

        fun start(context: Context) {
            val intent = Intent(context, CarFloatingControlsService::class.java).apply {
                action = CarOverlayActions.ACTION_START
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, CarFloatingControlsService::class.java).apply {
                action = CarOverlayActions.ACTION_STOP
            }
            context.startService(intent)
        }

        fun setAuxioForeground(context: Context, foreground: Boolean) {
            val intent = Intent(context, CarFloatingControlsService::class.java).apply {
                action = CarOverlayActions.ACTION_AUXIO_FOREGROUND_CHANGED
                putExtra(CarOverlayActions.EXTRA_AUXIO_FOREGROUND, foreground)
            }
            context.startService(intent)
        }

        fun permissionIntent(context: Context): Intent {
            return Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}"),
            )
        }
    }
}
