package org.oxycblt.auxio.playback

import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.KeyEvent
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
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
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.coil.getBitmap
import org.oxycblt.auxio.music.toURI
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.playback.state.PlaybackStateManager

// A Service that manages the single ExoPlayer instance and manages the system-side
// aspects of playback.
class PlaybackService : Service(), Player.EventListener, PlaybackStateManager.Callback {
    private val player: SimpleExoPlayer by lazy {
        SimpleExoPlayer.Builder(applicationContext).build()
    }

    private val playbackManager = PlaybackStateManager.getInstance()
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var systemReceiver: SystemEventReceiver

    private lateinit var notificationManager: NotificationManager
    private lateinit var notification: NotificationCompat.Builder

    private var changeIsFromAudioFocus = true
    private var isForeground = false

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(
        serviceJob + Dispatchers.Main
    )

    // --- SERVICE OVERRIDES ---

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(this::class.simpleName, "Service is active.")

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        // --- PLAYER SETUP ---

        player.addListener(this)

        // Set up AudioFocus/AudioAttributes
        player.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build(),
            true
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            player.experimentalSetOffloadSchedulingEnabled(true)
        }

        // --- SYSTEM RECEIVER SETUP ---

        // Set up the media button callbacks
        mediaSession = MediaSessionCompat(this, packageName).apply {
            isActive = true

            MediaSessionConnector(this).apply {
                setPlayer(player)
                setMediaButtonEventHandler { _, _, mediaButtonEvent ->
                    handleMediaButtonEvent(mediaButtonEvent)
                }
            }
        }

        // Set up callback for system events
        systemReceiver = SystemEventReceiver()
        IntentFilter().apply {
            addAction(NotificationUtils.ACTION_LOOP)
            addAction(NotificationUtils.ACTION_SKIP_PREV)
            addAction(NotificationUtils.ACTION_PLAY_PAUSE)
            addAction(NotificationUtils.ACTION_SKIP_NEXT)
            addAction(NotificationUtils.ACTION_EXIT)

            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            addAction(Intent.ACTION_HEADSET_PLUG)

            registerReceiver(systemReceiver, this)
        }

        // --- NOTIFICATION SETUP ---

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notification = notificationManager.createMediaNotification(this, mediaSession)

        // --- PLAYBACKSTATEMANAGER SETUP ---

        playbackManager.addCallback(this)

        if (playbackManager.song != null) {
            restorePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        stopForegroundAndNotification()
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
        changeIsFromAudioFocus = false

        if (state == Player.STATE_ENDED) {
            playbackManager.next()
        } else if (state == Player.STATE_READY) {
            startPollingPosition()
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        // Only sync the playing status with PlaybackStateManager if the change occurred
        // from an Audio Focus change. Nowhere else.
        if (isPlaying != playbackManager.isPlaying && changeIsFromAudioFocus) {
            playbackManager.setPlayingStatus(isPlaying)
        }

        changeIsFromAudioFocus = true
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        // If the song loops while in the LOOP_ONCE mode, then stop looping after that.
        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT &&
            playbackManager.loopMode == LoopMode.ONCE
        ) {
            playbackManager.setLoopMode(LoopMode.NONE)
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        // If there's any issue, just go to the next song.
        playbackManager.next()
    }

    override fun onPositionDiscontinuity(reason: Int) {
        if (reason == Player.DISCONTINUITY_REASON_SEEK) {
            playbackManager.setPosition(player.currentPosition)
        }
    }

    // --- PLAYBACK STATE CALLBACK OVERRIDES ---

    override fun onSongUpdate(song: Song?) {
        song?.let {
            val item = MediaItem.fromUri(it.id.toURI())

            player.setMediaItem(item)
            player.prepare()
            player.play()

            uploadMetadataToSession(it)
            notification.setMetadata(playbackManager.song!!, this) {
                startForegroundOrNotify()
            }

            return
        }

        // Stop playing/the notification if there's nothing to play.
        player.stop()
        stopForegroundAndNotification()
    }

    override fun onModeUpdate(mode: PlaybackMode) {
        notification.updateMode(this)

        startForegroundOrNotify()
    }

    override fun onPlayingUpdate(isPlaying: Boolean) {
        changeIsFromAudioFocus = false

        if (isPlaying && !player.isPlaying) {
            player.play()
            notification.updatePlaying(this)
            startForegroundOrNotify()

            startPollingPosition()
        } else {
            player.pause()
            notification.updatePlaying(this)
            startForegroundOrNotify()
        }
    }

    override fun onLoopUpdate(mode: LoopMode) {
        changeIsFromAudioFocus = false

        when (mode) {
            LoopMode.NONE -> {
                player.repeatMode = Player.REPEAT_MODE_OFF
            }
            else -> {
                player.repeatMode = Player.REPEAT_MODE_ONE
            }
        }

        notification.updateLoop(this)
        startForegroundOrNotify()
    }

    override fun onSeekConfirm(position: Long) {
        changeIsFromAudioFocus = false

        player.seekTo(position)
    }

    // --- OTHER FUNCTIONS ---

    private fun restorePlayer() {
        playbackManager.song?.let {
            val item = MediaItem.fromUri(it.id.toURI())
            player.setMediaItem(item)
            player.prepare()
            player.seekTo(playbackManager.position)

            notification.setMetadata(it, this) {
                startForegroundOrNotify()
            }
        }
    }

    private fun uploadMetadataToSession(song: Song) {
        val builder = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.name)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, song.name)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.album.artist.name)
            .putString(MediaMetadataCompat.METADATA_KEY_AUTHOR, song.album.artist.name)
            .putString(MediaMetadataCompat.METADATA_KEY_COMPOSER, song.album.artist.name)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, song.album.artist.name)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.album.name)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration)

        getBitmap(song, this) {
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, it)
            mediaSession.setMetadata(builder.build())
        }
    }

    private fun pollCurrentPosition() = flow {
        while (player.isPlaying) {
            emit(player.currentPosition)
            delay(250)
        }
    }.conflate()

    private fun startPollingPosition() {
        serviceScope.launch {
            pollCurrentPosition().takeWhile { player.isPlaying }.collect {
                playbackManager.setPosition(it)
            }
        }
    }

    private fun startForegroundOrNotify() {
        // Start the service in the foreground if haven't already.
        if (!isForeground) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NotificationUtils.NOTIFICATION_ID, notification.build(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )
            } else {
                startForeground(NotificationUtils.NOTIFICATION_ID, notification.build())
            }
        } else {
            notificationManager.notify(NotificationUtils.NOTIFICATION_ID, notification.build())
        }
    }

    private fun stopForegroundAndNotification() {
        stopForeground(true)
        notificationManager.cancel(NotificationUtils.NOTIFICATION_ID)

        isForeground = false
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

    // BroadcastReceiver for receiving system events [E.G Headphones connected/disconnected]
    private inner class SystemEventReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            action?.let {
                when (it) {
                    NotificationUtils.ACTION_LOOP ->
                        playbackManager.setLoopMode(playbackManager.loopMode.increment())
                    NotificationUtils.ACTION_SKIP_PREV -> playbackManager.prev()
                    NotificationUtils.ACTION_PLAY_PAUSE ->
                        playbackManager.setPlayingStatus(!playbackManager.isPlaying)
                    NotificationUtils.ACTION_SKIP_NEXT -> playbackManager.next()
                    NotificationUtils.ACTION_EXIT -> stop()

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

        private fun stop() {
            playbackManager.setPlayingStatus(false)
            stopForegroundAndNotification()
        }
    }

    companion object {
        private const val DISCONNECTED = 0
        private const val CONNECTED = 1
    }
}
