package org.oxycblt.auxio.playback

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toURI
import org.oxycblt.auxio.playback.state.PlaybackStateCallback
import org.oxycblt.auxio.playback.state.PlaybackStateManager

// A Service that manages the single ExoPlayer instance and [attempts] to keep
// persistence if the app closes.
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

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(
        serviceJob + Dispatchers.Main
    )

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
        serviceJob.cancel()
        playbackManager.removeCallback(this)
    }

    override fun onPlaybackStateChanged(state: Int) {
        if (state == Player.STATE_ENDED) {
            playbackManager.next()
        } else if (state == Player.STATE_READY) {
            startPollingPosition()
        }
    }

    override fun onSongUpdate(song: Song?) {
        song?.let { song ->
            val item = MediaItem.fromUri(song.id.toURI())
            player.setMediaItem(item)
            player.prepare()
            player.play()
        }
    }

    override fun onPlayingUpdate(isPlaying: Boolean) {
        if (isPlaying) {
            player.play()

            startPollingPosition()
        } else {
            player.pause()
        }
    }

    fun doSeek(position: Long) {
        player.seekTo(position * 1000)
    }

    // Awful Hack to get position polling to work, as exoplayer does not provide any
    // onPositionChanged callback for some inane reason.
    // FIXME: Consider using exoplayer UI elements here, don't be surprised if this causes problems.

    private fun pollCurrentPosition() = flow {
        while (player.currentPosition <= player.duration) {
            emit(player.currentPosition)
            delay(500)
        }
    }.conflate()

    private fun startPollingPosition() {
        serviceScope.launch {
            pollCurrentPosition().takeWhile { true }.collect {
                playbackManager.setPosition(it / 1000)
            }
        }
    }

    inner class LocalBinder : Binder() {
        fun getService() = this@PlaybackService
    }
}
