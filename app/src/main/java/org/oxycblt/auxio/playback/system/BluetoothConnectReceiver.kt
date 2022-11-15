package org.oxycblt.auxio.playback.system

import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import org.oxycblt.auxio.playback.state.InternalPlayer
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.settings.Settings

class BluetoothConnectReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == android.bluetooth.BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED) {
            val newState = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, BluetoothProfile.STATE_DISCONNECTED)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                val settings = Settings(context)
                if (settings.bluetoothAutoplay) {
                    // make sure required services are up and running
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(Intent(context, PlaybackService::class.java))
                    } else {
                        context.startService(Intent(context, PlaybackService::class.java))
                    }
                    // start playback
                    val playbackManager = PlaybackStateManager.getInstance()
                    playbackManager.startAction(InternalPlayer.Action.RestoreState)
                    playbackManager.changePlaying(true)
                }
            }
        }
    }
}