package org.oxycblt.auxio.playback

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.KeyEvent
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toURI
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.playback.state.PlaybackStateCallback
import org.oxycblt.auxio.playback.state.PlaybackStateManager

private const val CHANNEL_ID = "CHANNEL_AUXIO_PLAYBACK"
private const val NOTIF_ID = 0xA0A0
private const val CONNECTED = 1
private const val DISCONNECTED = 0

// A Service that manages the single ExoPlayer instance and manages the system-side
// aspects of playback.
class PlaybackService : Service(), Player.EventListener, PlaybackStateCallback {
    // TODO: Use the ExoPlayer queue functionality [To an extent]? Could make things faster.
    private val player: SimpleExoPlayer by lazy {
        val p = SimpleExoPlayer.Builder(applicationContext).build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            p.experimentalSetOffloadSchedulingEnabled(true)
        }
        p.addListener(this)
        p
    }

    private val playbackManager = PlaybackStateManager.getInstance()
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var systemReceiver: SystemEventReceiver
    private var changeIsFromSystem = false

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(
        serviceJob + Dispatchers.Main
    )

    private lateinit var notification: Notification

    // --- SERVICE OVERRIDES ---

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(this::class.simpleName, "Service is active.")

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        // Set up the media button callbacks
        mediaSession = MediaSessionCompat(this, packageName).apply {
            isActive = true
        }

        val connector = MediaSessionConnector(mediaSession)
        connector.setPlayer(player)
        connector.setMediaButtonEventHandler { _, _, mediaButtonEvent ->
            handleMediaButtonEvent(mediaButtonEvent)
        }

        // Set up AudioFocus/AudioAttributes
        player.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build(),
            true
        )

        notification = createNotification()

        // Set up callback for system events
        systemReceiver = SystemEventReceiver()
        IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            addAction(Intent.ACTION_HEADSET_PLUG)

            registerReceiver(systemReceiver, this)
        }

        playbackManager.addCallback(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        stopForeground(true)
        unregisterReceiver(systemReceiver)

        // Release everything that could cause a memory leak if left around
        player.release()
        mediaSession.release()
        serviceJob.cancel()
        playbackManager.removeCallback(this)

        Log.d(this::class.simpleName, "Service destroyed.")
    }

    // --- PLAYER EVENT LISTENER OVERRIDES ---

    override fun onPlaybackStateChanged(state: Int) {
        if (state == Player.STATE_ENDED) {
            playbackManager.next()
        } else if (state == Player.STATE_READY) {
            startPollingPosition()
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        // If the change to playing occurred from the system instead of PlaybackStateManager, then
        // sync the playing value to PlaybackStateManager to keep it up ton date.
        if (isPlaying != playbackManager.isPlaying && changeIsFromSystem) {
            playbackManager.setPlayingStatus(isPlaying)
        }

        changeIsFromSystem = true
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        // If the song loops while in the LOOP_ONCE mode, then stop looping after that.
        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT &&
            playbackManager.loopMode == LoopMode.ONCE
        ) {
            playbackManager.setLoopMode(LoopMode.NONE)
        }
    }

    // --- PLAYBACK STATE CALLBACK OVERRIDES ---

    override fun onSongUpdate(song: Song?) {
        changeIsFromSystem = false

        song?.let {
            val item = MediaItem.fromUri(it.id.toURI())
            player.setMediaItem(item)
            player.prepare()
            player.play()

            return
        }

        player.stop()
    }

    override fun onPlayingUpdate(isPlaying: Boolean) {
        changeIsFromSystem = false

        if (isPlaying && !player.isPlaying) {
            player.play()

            startForeground(NOTIF_ID, notification)
            startPollingPosition()
        } else {
            player.pause()

            // Be a polite service and stop being foreground if nothing is playing.
            stopForeground(false)
        }
    }

    override fun onLoopUpdate(mode: LoopMode) {
        changeIsFromSystem = false

        when (mode) {
            LoopMode.NONE -> {
                player.repeatMode = Player.REPEAT_MODE_OFF
            }
            else -> {
                player.repeatMode = Player.REPEAT_MODE_ONE
            }
        }
    }

    override fun onSeekConfirm(position: Long) {
        changeIsFromSystem = false

        player.seekTo(position * 1000)
    }

    // --- OTHER FUNCTIONS ---

    // Awful Hack to get position polling to work, as exoplayer does not provide any
    // onPositionChanged callback for some inane reason.
    // FIXME: There has to be a better way of polling positions.
    private fun pollCurrentPosition() = flow {
        while (player.isPlaying) {
            emit(player.currentPosition)
            delay(250)
        }
    }.conflate()

    private fun startPollingPosition() {
        serviceScope.launch {
            pollCurrentPosition().takeWhile { player.isPlaying }.collect {
                playbackManager.setPosition(it / 1000)
            }
        }
    }

    // Handle a media button event.
    private fun handleMediaButtonEvent(event: Intent): Boolean {
        val item = event
            .getParcelableExtra<Parcelable>(Intent.EXTRA_KEY_EVENT) as KeyEvent

        if (item.action == KeyEvent.ACTION_DOWN) {
            return when (item.keyCode) {
                KeyEvent.KEYCODE_MEDIA_PAUSE, KeyEvent.KEYCODE_MEDIA_PLAY,
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_HEADSETHOOK -> {
                    playbackManager.setPlayingStatus(!playbackManager.isPlaying)
                    true
                }

                KeyEvent.KEYCODE_MEDIA_NEXT -> {
                    playbackManager.next()
                    true
                }

                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                    playbackManager.prev()
                    true
                }

                KeyEvent.KEYCODE_MEDIA_REWIND -> {
                    player.seekTo(0)
                    true
                }

                KeyEvent.KEYCODE_MEDIA_STOP, KeyEvent.KEYCODE_MEDIA_CLOSE -> {
                    stopSelf()
                    true
                }

                else -> false
            }
        }

        return false
    }

    // Create a notification
    // TODO: Spin this off into its own object!
    private fun createNotification(): Notification {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.label_notif_playback),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        // TODO: Placeholder, implement proper media controls.
        val notif = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_song)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.label_is_playing))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setChannelId(CHANNEL_ID)
            .build()

        return notif
    }

    // BroadcastReceiver for receiving system events [E.G Headphones connected/disconnected]
    private inner class SystemEventReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            action?.let {
                when (it) {
                    BluetoothDevice.ACTION_ACL_CONNECTED -> resume()
                    BluetoothDevice.ACTION_ACL_DISCONNECTED -> pause()

                    AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED -> {
                        when (intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1)) {
                            AudioManager.SCO_AUDIO_STATE_CONNECTED -> resume()
                            AudioManager.SCO_AUDIO_STATE_DISCONNECTED -> pause()
                        }
                    }

                    AudioManager.ACTION_AUDIO_BECOMING_NOISY -> pause()

                    Intent.ACTION_HEADSET_PLUG -> {
                        when (intent.getIntExtra("state", -1)) {
                            CONNECTED -> resume()
                            DISCONNECTED -> pause()
                        }
                    }
                }
            }
        }

        private fun resume() {
            if (playbackManager.song != null) {
                Log.d(this::class.simpleName, "Device connected, resuming...")

                playbackManager.setPlayingStatus(true)
            }
        }

        private fun pause() {
            if (playbackManager.song != null) {
                Log.d(this::class.simpleName, "Device disconnected, pausing...")

                playbackManager.setPlayingStatus(false)
            }
        }
    }
}
