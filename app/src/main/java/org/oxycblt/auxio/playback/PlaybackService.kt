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

class PlaybackService : Service(), Player.EventListener {
    private val player: SimpleExoPlayer by lazy {
        val p = SimpleExoPlayer.Builder(applicationContext).build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            p.experimentalSetOffloadSchedulingEnabled(true)
        }
        p.addListener(this)
        p
    }

    private val mBinder = LocalBinder()

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onDestroy() {
        super.onDestroy()

        player.release()
    }

    fun playSong(song: Song) {
        val item = MediaItem.fromUri(song.id.toURI())

        player.setMediaItem(item)
        player.prepare()
        player.play()
    }

    inner class LocalBinder : Binder() {
        fun getService() = this@PlaybackService
    }
}
