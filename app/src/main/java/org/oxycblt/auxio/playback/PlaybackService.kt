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
import android.view.KeyEvent
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Renderer
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import org.oxycblt.auxio.coil.getBitmap
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toURI
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.settings.SettingsManager

/**
 * A service that manages the system-side aspects of playback, such as:
 * - The single [SimpleExoPlayer] instance.
 * - The [MediaSessionCompat]
 * - The Media Notification
 * - Audio Focus
 * - Headset management
 *
 * This service relies on [PlaybackStateManager.Callback] and [SettingsManager.Callback],
 * so therefore there's no need to bind to it to deliver commands.
 * @author OxygenCobalt
 */
class PlaybackService : Service(), Player.EventListener, PlaybackStateManager.Callback, SettingsManager.Callback {
    private val player: SimpleExoPlayer by lazy { newPlayer() }

    private val playbackManager = PlaybackStateManager.getInstance()
    private val settingsManager = SettingsManager.getInstance()

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var systemReceiver: SystemEventReceiver
    private val playerAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .build()

    private lateinit var notificationManager: NotificationManager
    private lateinit var notification: NotificationCompat.Builder

    private lateinit var audioReactor: AudioReactor
    private var isForeground = false

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(
        serviceJob + Dispatchers.Main
    )

    // --- SERVICE OVERRIDES ---

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logD("Service is active.")

        return START_NOT_STICKY
    }

    // No binding, service is headless. Deliver updates through PlaybackStateManager/SettingsManager instead.
    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        // --- PLAYER SETUP ---

        player.apply {
            addListener(this@PlaybackService)
            setAudioAttributes(playerAttributes, false)
        }

        audioReactor = AudioReactor(this, player)

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
            addAction(NotificationUtils.ACTION_SHUFFLE)
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

        playbackManager.resetHasPlayedStatus()
        playbackManager.addCallback(this)

        if (playbackManager.song != null || playbackManager.isRestored) {
            restorePlayer()
            restoreNotification()
        }

        // --- SETTINGSMANAGER SETUP ---

        settingsManager.addCallback(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        stopForegroundAndNotification()
        unregisterReceiver(systemReceiver)

        // Release everything that could cause a memory leak if left around
        player.release()
        mediaSession.release()
        audioReactor.destroy()
        playbackManager.removeCallback(this)
        settingsManager.removeCallback(this)

        // The service coroutines last job is to save the state to the DB, before terminating itself.
        serviceScope.launch {
            playbackManager.saveStateToDatabase(this@PlaybackService)
            serviceJob.cancel()
        }

        logD("Service destroyed.")
    }

    // --- PLAYER EVENT LISTENER OVERRIDES ---

    override fun onPlaybackStateChanged(state: Int) {
        if (state == Player.STATE_ENDED) {
            playbackManager.next()
        } else if (state == Player.STATE_READY) {
            startPollingPosition()
        }
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

            if (playbackManager.isPlaying) {
                player.play()
            }

            uploadMetadataToSession(it)

            notification.setMetadata(this, it, settingsManager.colorizeNotif) {
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
        if (isPlaying && !player.isPlaying) {
            player.play()
            notification.updatePlaying(this)
            audioReactor.requestFocus()
            startForegroundOrNotify()

            startPollingPosition()
        } else {
            player.pause()
            notification.updatePlaying(this)
            startForegroundOrNotify()
        }
    }

    override fun onLoopUpdate(mode: LoopMode) {
        when (mode) {
            LoopMode.NONE -> {
                player.repeatMode = Player.REPEAT_MODE_OFF
            }
            else -> {
                player.repeatMode = Player.REPEAT_MODE_ONE
            }
        }

        notification.updateExtraAction(this, settingsManager.useAltNotifAction)
        startForegroundOrNotify()
    }

    override fun onShuffleUpdate(isShuffling: Boolean) {
        if (settingsManager.useAltNotifAction) {
            notification.updateExtraAction(this, settingsManager.useAltNotifAction)

            startForegroundOrNotify()
        }
    }

    override fun onSeek(position: Long) {
        player.seekTo(position)
    }

    // --- SETTINGSMANAGER OVERRIDES ---

    override fun onColorizeNotifUpdate(doColorize: Boolean) {
        playbackManager.song?.let {
            notification.setMetadata(this, it, settingsManager.colorizeNotif) {
                startForegroundOrNotify()
            }
        }
    }

    override fun onNotifActionUpdate(useAltAction: Boolean) {
        notification.updateExtraAction(this, useAltAction)

        startForegroundOrNotify()
    }

    override fun onShowCoverUpdate(showCovers: Boolean) {
        playbackManager.song?.let {
            notification.setMetadata(this, it, settingsManager.colorizeNotif) {
                startForegroundOrNotify()
            }
        }
    }

    override fun onQualityCoverUpdate(doQualityCovers: Boolean) {
        playbackManager.song?.let { song ->
            notification.setMetadata(this, song, settingsManager.colorizeNotif) {
                startForegroundOrNotify()
            }
        }
    }

    // --- OTHER FUNCTIONS ---

    /**
     * Create the [SimpleExoPlayer] instance.
     */
    private fun newPlayer(): SimpleExoPlayer {
        // Since Auxio is a music player, only specify an audio renderer to save battery/apk size/cache size.
        val audioRenderer = RenderersFactory { handler, _, audioListener, _, _ ->
            arrayOf<Renderer>(
                MediaCodecAudioRenderer(this, MediaCodecSelector.DEFAULT, handler, audioListener)
            )
        }

        val extractorsFactory = DefaultExtractorsFactory().setConstantBitrateSeekingEnabled(true)

        return SimpleExoPlayer.Builder(this, audioRenderer)
            .setMediaSourceFactory(DefaultMediaSourceFactory(this, extractorsFactory))
            .build()
    }

    /**
     * Restore the [SimpleExoPlayer] state, if the service was destroyed while [PlaybackStateManager] persisted.
     */
    private fun restorePlayer() {
        playbackManager.song?.let {
            val item = MediaItem.fromUri(it.id.toURI())
            player.setMediaItem(item)
            player.prepare()
            player.seekTo(playbackManager.position)
        }

        when (playbackManager.loopMode) {
            LoopMode.NONE -> {
                player.repeatMode = Player.REPEAT_MODE_OFF
            }
            else -> {
                player.repeatMode = Player.REPEAT_MODE_ONE
            }
        }
    }

    /**
     * Restore the notification, if the service was destroyed while [PlaybackStateManager] persisted.
     */
    private fun restoreNotification() {
        notification.updateExtraAction(this, settingsManager.useAltNotifAction)
        notification.updateMode(this)
        notification.updatePlaying(this)

        playbackManager.song?.let {
            notification.setMetadata(this, it, settingsManager.colorizeNotif) {
                if (playbackManager.isPlaying) {
                    startForegroundOrNotify()
                } else {
                    stopForegroundAndNotification()
                }
            }
        }
    }

    /**
     * Upload the song metadata to the [MediaSessionCompat], so that things such as album art
     * show up on the lock screen.
     */
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

        getBitmap(this, song) {
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, it)
            mediaSession.setMetadata(builder.build())
        }
    }

    /**
     * Start polling the position on a co-routine.
     */
    private fun startPollingPosition() {
        fun pollCurrentPosition() = flow {
            while (player.isPlaying) {
                emit(player.currentPosition)
                delay(250)
            }
        }.conflate()

        serviceScope.launch {
            pollCurrentPosition().takeWhile { player.isPlaying }.collect {
                playbackManager.setPosition(it)
            }
        }
    }

    /**
     * Bring the service into the foreground and show the notification, or refresh the notification.
     */
    private fun startForegroundOrNotify() {
        // Don't start foreground if:
        //     - The playback hasnt even started
        //     - The playback hasnt been restored
        //     - There is nothing to play
        if (playbackManager.hasPlayed && playbackManager.isRestored && playbackManager.song != null) {
            logD("Starting foreground/notifying")

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
    }

    /**
     * Stop the foreground state and hide the notification
     */
    private fun stopForegroundAndNotification() {
        stopForeground(true)
        notificationManager.cancel(NotificationUtils.NOTIFICATION_ID)

        isForeground = false
    }

    /**
     * Handle a media button intent.
     */
    private fun handleMediaButtonEvent(event: Intent): Boolean {
        val item = event
            .getParcelableExtra<Parcelable>(Intent.EXTRA_KEY_EVENT) as KeyEvent

        if (item.action == KeyEvent.ACTION_DOWN) {
            return when (item.keyCode) {
                // Play/Pause if any of the keys are play/pause
                KeyEvent.KEYCODE_MEDIA_PAUSE, KeyEvent.KEYCODE_MEDIA_PLAY,
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_HEADSETHOOK -> {
                    playbackManager.setPlaying(!playbackManager.isPlaying)
                    true
                }

                // Go to the next song is the key is next
                KeyEvent.KEYCODE_MEDIA_NEXT -> {
                    playbackManager.next()
                    true
                }

                // Go to the previous song if the key is back
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                    playbackManager.prev()
                    true
                }

                // Rewind if the key is rewind
                KeyEvent.KEYCODE_MEDIA_REWIND -> {
                    playbackManager.rewind()
                    true
                }

                // Stop the service entirely if the key was stop/close
                KeyEvent.KEYCODE_MEDIA_STOP, KeyEvent.KEYCODE_MEDIA_CLOSE -> {
                    stopSelf()
                    true
                }

                else -> false
            }
        }

        return false
    }

    /**
     * A [BroadcastReceiver] for receiving system events from the media notification or the headset.
     */
    private inner class SystemEventReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            action?.let {
                when (it) {
                    NotificationUtils.ACTION_LOOP ->
                        playbackManager.setLoopMode(playbackManager.loopMode.increment())

                    NotificationUtils.ACTION_SHUFFLE ->
                        playbackManager.setShuffling(!playbackManager.isShuffling, keepSong = true)

                    NotificationUtils.ACTION_SKIP_PREV -> playbackManager.prev()

                    NotificationUtils.ACTION_PLAY_PAUSE ->
                        playbackManager.setPlaying(!playbackManager.isPlaying)

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

        /**
         * Resume, as long as its allowed.
         */
        private fun resume() {
            if (playbackManager.song != null && settingsManager.doPlugMgt) {
                logD("Device connected, resuming...")

                playbackManager.setPlaying(true)
            }
        }

        /**
         * Pause, as long as its allowed.
         */
        private fun pause() {
            if (playbackManager.song != null && settingsManager.doPlugMgt) {
                logD("Device disconnected, pausing...")

                playbackManager.setPlaying(false)
            }
        }

        /**
         * Stop if the X button was clicked from the notification
         */
        private fun stop() {
            playbackManager.setPlaying(false)
            stopForegroundAndNotification()
        }
    }

    companion object {
        private const val DISCONNECTED = 0
        private const val CONNECTED = 1
    }
}
