package org.oxycblt.auxio.playback

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toURI
import org.oxycblt.auxio.playback.state.PlaybackStateCallback
import org.oxycblt.auxio.playback.state.PlaybackStateManager

class PlaybackService : Service(), Player.EventListener, PlaybackStateCallback {
    private val player: SimpleExoPlayer by lazy {
        val p = SimpleExoPlayer.Builder(applicationContext).build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            p.experimentalSetOffloadSchedulingEnabled(true)
        }
        p.addListener(this)
        p
    }

    private val mBinder = LocalBinder()
    private val playbackManager = PlaybackStateManager.getInstance()

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()

        playbackManager.addCallback(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        player.release()
        playbackManager.removeCallback(this)
    }

    override fun onSongUpdate(song: Song?) {
        song?.let {
            val item = MediaItem.fromUri(it.id.toURI())
            player.setMediaItem(item)
            player.prepare()
            player.play()
        }
    }

    inner class LocalBinder : Binder() {
        fun getService() = this@PlaybackService
    }
}
