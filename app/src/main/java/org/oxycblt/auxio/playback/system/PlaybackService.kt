package org.oxycblt.auxio.playback.system

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
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
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
import org.oxycblt.auxio.coil.loadBitmap
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.Parent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toURI
import org.oxycblt.auxio.playback.state.LoopMode
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
    private val player: SimpleExoPlayer by lazy(::newPlayer)

    private val playbackManager = PlaybackStateManager.getInstance()
    private val settingsManager = SettingsManager.getInstance()

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var notificationManager: NotificationManager
    private lateinit var notification: PlaybackNotification

    private lateinit var audioReactor: AudioReactor
    private lateinit var systemReceiver: SystemEventReceiver

    private var isForeground = false

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(serviceJob + Dispatchers.Main)

    // --- SERVICE OVERRIDES ---

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logD("Service is active.")

        return START_NOT_STICKY
    }

    // No binding, service is headless.
    // Deliver updates through PlaybackStateManager/SettingsManager instead.
    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        // --- PLAYER SETUP ---

        player.addListener(this@PlaybackService)
        player.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build(),
            false
        )

        audioReactor = AudioReactor(this, player)

        // --- SYSTEM RECEIVER SETUP ---

        systemReceiver = SystemEventReceiver()

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

        IntentFilter().apply {
            addAction(PlaybackNotification.ACTION_LOOP)
            addAction(PlaybackNotification.ACTION_SHUFFLE)
            addAction(PlaybackNotification.ACTION_SKIP_PREV)
            addAction(PlaybackNotification.ACTION_PLAY_PAUSE)
            addAction(PlaybackNotification.ACTION_SKIP_NEXT)
            addAction(PlaybackNotification.ACTION_EXIT)

            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            addAction(Intent.ACTION_HEADSET_PLUG)

            registerReceiver(systemReceiver, this)
        }

        // --- NOTIFICATION SETUP ---

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notification = PlaybackNotification.from(this, mediaSession)

        // --- PLAYBACKSTATEMANAGER SETUP ---

        playbackManager.resetHasPlayedStatus()
        playbackManager.addCallback(this)

        if (playbackManager.song != null || playbackManager.isRestored) {
            restore()
        }

        // --- SETTINGSMANAGER SETUP ---

        settingsManager.addCallback(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        stopForegroundAndNotification()
        unregisterReceiver(systemReceiver)

        player.release()
        mediaSession.release()
        audioReactor.release()

        playbackManager.removeCallback(this)
        settingsManager.removeCallback(this)

        // The service coroutines last job is to save the state to the DB, before terminating itself
        serviceScope.launch {
            playbackManager.saveStateToDatabase(this@PlaybackService)
            serviceJob.cancel()
        }

        logD("Service destroyed.")
    }

    // --- PLAYER EVENT LISTENER OVERRIDES ---

    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_READY -> startPollingPosition()
            Player.STATE_ENDED -> playbackManager.next()
            else -> {}
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        // Reset the loop mode from LOOP_ONE (if it is LOOP_ONE) on each repeat
        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT) {
            playbackManager.clearLoopMode()
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
        if (song != null) {
            player.setMediaItem(MediaItem.fromUri(song.id.toURI()))
            player.prepare()

            pushMetadataToSession(song)

            notification.setMetadata(
                this, song, settingsManager.colorizeNotif, { startForegroundOrNotify() }
            )

            return
        }

        // Stop playing/the notification if there's nothing to play.
        player.stop()
        stopForegroundAndNotification()
    }

    override fun onParentUpdate(parent: Parent?) {
        notification.setParent(this, parent)

        startForegroundOrNotify()
    }

    override fun onPlayingUpdate(isPlaying: Boolean) {
        if (isPlaying && !player.isPlaying) {
            player.play()
            audioReactor.requestFocus()
            startPollingPosition()
        } else {
            player.pause()
        }

        notification.setPlaying(this, isPlaying)
        startForegroundOrNotify()
    }

    override fun onLoopUpdate(loopMode: LoopMode) {
        player.setLoopMode(loopMode)

        if (!settingsManager.useAltNotifAction) {
            notification.setLoop(this, loopMode)

            startForegroundOrNotify()
        }
    }

    override fun onShuffleUpdate(isShuffling: Boolean) {
        if (settingsManager.useAltNotifAction) {
            notification.setShuffle(this, isShuffling)

            startForegroundOrNotify()
        }
    }

    override fun onSeek(position: Long) {
        player.seekTo(position)
    }

    // --- SETTINGSMANAGER OVERRIDES ---

    override fun onColorizeNotifUpdate(doColorize: Boolean) {
        playbackManager.song?.let { song ->
            notification.setMetadata(
                this, song, settingsManager.colorizeNotif, {}
            )
        }
    }

    override fun onNotifActionUpdate(useAltAction: Boolean) {
        if (useAltAction) {
            notification.setShuffle(this, playbackManager.isShuffling)
        } else {
            notification.setLoop(this, playbackManager.loopMode)
        }

        startForegroundOrNotify()
    }

    override fun onShowCoverUpdate(showCovers: Boolean) {
        playbackManager.song?.let { song ->
            notification.setMetadata(
                this, song, settingsManager.colorizeNotif, { startForegroundOrNotify() }
            )
        }
    }

    override fun onQualityCoverUpdate(doQualityCovers: Boolean) {
        playbackManager.song?.let { song ->
            notification.setMetadata(
                this, song, settingsManager.colorizeNotif, { startForegroundOrNotify() }
            )
        }
    }

    // --- OTHER FUNCTIONS ---

    /**
     * Create the [SimpleExoPlayer] instance.
     */
    private fun newPlayer(): SimpleExoPlayer {
        // Since Auxio is a music player, only specify an audio renderer to save
        // battery/apk size/cache size
        val audioRenderer = RenderersFactory { handler, _, audioListener, _, _ ->
            arrayOf(
                MediaCodecAudioRenderer(this, MediaCodecSelector.DEFAULT, handler, audioListener)
            )
        }

        // Enable constant bitrate seeking so that certain MP3s/AACs are seekable
        val extractorsFactory = DefaultExtractorsFactory().setConstantBitrateSeekingEnabled(true)

        return SimpleExoPlayer.Builder(this, audioRenderer)
            .setMediaSourceFactory(DefaultMediaSourceFactory(this, extractorsFactory))
            .build()
    }

    /**
     * Fully restore the notification and playback state
     */
    private fun restore() {
        playbackManager.song?.let { song ->
            notification.setMetadata(this, song, settingsManager.colorizeNotif) {}

            player.setMediaItem(MediaItem.fromUri(song.id.toURI()))
            player.seekTo(playbackManager.position)
            player.prepare()
        }

        notification.setParent(this, playbackManager.parent)
        notification.setPlaying(this, playbackManager.isPlaying)

        if (settingsManager.useAltNotifAction) {
            notification.setShuffle(this, playbackManager.isShuffling)
        } else {
            notification.setLoop(this, playbackManager.loopMode)
        }

        player.setLoopMode(playbackManager.loopMode)
    }

    /**
     * Upload the song metadata to the [MediaSessionCompat], so that things such as album art
     * show up on the lock screen.
     */
    private fun pushMetadataToSession(song: Song) {
        val builder = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.name)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, song.name)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.album.artist.name)
            .putString(MediaMetadataCompat.METADATA_KEY_AUTHOR, song.album.artist.name)
            .putString(MediaMetadataCompat.METADATA_KEY_COMPOSER, song.album.artist.name)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, song.album.artist.name)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.album.name)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration)

        loadBitmap(this, song) {
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, it)
            mediaSession.setMetadata(builder.build())
        }
    }

    /**
     * Start polling the position on a coroutine.
     */
    private fun startPollingPosition() {
        val pollFlow = flow {
            while (true) {
                emit(player.currentPosition)
                delay(500)
            }
        }.conflate()

        serviceScope.launch {
            pollFlow.takeWhile { player.isPlaying }.collect {
                playbackManager.setPosition(it)
            }
        }
    }

    /**
     * Shortcut to transform a [LoopMode] into a player repeat mode
     */
    private fun Player.setLoopMode(mode: LoopMode) {
        repeatMode = if (mode == LoopMode.NONE) {
            Player.REPEAT_MODE_OFF
        } else {
            Player.REPEAT_MODE_ALL
        }
    }

    /**
     * Bring the service into the foreground and show the notification, or refresh the notification.
     */
    private fun startForegroundOrNotify() {
        if (playbackManager.hasPlayed && playbackManager.song != null) {
            logD("Starting foreground/notifying")

            if (!isForeground) {
                // Specify that this is a media service, if supported.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(
                        PlaybackNotification.NOTIFICATION_ID, notification.build(),
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                    )
                } else {
                    startForeground(
                        PlaybackNotification.NOTIFICATION_ID, notification.build()
                    )
                }
            } else {
                // If we are already in foreground just update the notification
                notificationManager.notify(
                    PlaybackNotification.NOTIFICATION_ID, notification.build()
                )
            }
        }
    }

    /**
     * Stop the foreground state and hide the notification
     */
    private fun stopForegroundAndNotification() {
        stopForeground(true)
        notificationManager.cancel(PlaybackNotification.NOTIFICATION_ID)

        isForeground = false
    }

    /**
     * Handle a media button intent.
     */
    private fun handleMediaButtonEvent(event: Intent): Boolean {
        val item = event.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)

        if (item != null && item.action == KeyEvent.ACTION_DOWN) {
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
                    // --- NOTIFICATION CASES ---

                    PlaybackNotification.ACTION_PLAY_PAUSE -> playbackManager.setPlaying(
                        !playbackManager.isPlaying
                    )

                    PlaybackNotification.ACTION_LOOP -> playbackManager.setLoopMode(
                        playbackManager.loopMode.increment()
                    )

                    PlaybackNotification.ACTION_SHUFFLE -> playbackManager.setShuffling(
                        !playbackManager.isShuffling, keepSong = true
                    )

                    PlaybackNotification.ACTION_SKIP_PREV -> playbackManager.prev()
                    PlaybackNotification.ACTION_SKIP_NEXT -> playbackManager.next()

                    PlaybackNotification.ACTION_EXIT -> {
                        playbackManager.setPlaying(false)
                        stopForegroundAndNotification()
                    }

                    // --- HEADSET CASES ---

                    BluetoothDevice.ACTION_ACL_CONNECTED -> resumeFromPlug()
                    BluetoothDevice.ACTION_ACL_DISCONNECTED -> pauseFromPlug()

                    AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED -> {
                        when (intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1)) {
                            AudioManager.SCO_AUDIO_STATE_CONNECTED -> resumeFromPlug()
                            AudioManager.SCO_AUDIO_STATE_DISCONNECTED -> pauseFromPlug()
                        }
                    }

                    AudioManager.ACTION_AUDIO_BECOMING_NOISY -> pauseFromPlug()

                    Intent.ACTION_HEADSET_PLUG -> {
                        when (intent.getIntExtra("state", -1)) {
                            CONNECTED -> resumeFromPlug()
                            DISCONNECTED -> pauseFromPlug()
                        }
                    }
                }
            }
        }

        /**
         * Resume from a headset plug event, as long as its allowed.
         */
        private fun resumeFromPlug() {
            if (playbackManager.song != null && settingsManager.doPlugMgt) {
                logD("Device connected, resuming...")

                playbackManager.setPlaying(true)
            }
        }

        /**
         * Pause from a headset plug, as long as its allowed.
         */
        private fun pauseFromPlug() {
            if (playbackManager.song != null && settingsManager.doPlugMgt) {
                logD("Device disconnected, pausing...")

                playbackManager.setPlaying(false)
            }
        }
    }

    companion object {
        private const val DISCONNECTED = 0
        private const val CONNECTED = 1
    }
}
