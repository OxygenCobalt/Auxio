/*
 * Copyright (c) 2024 Auxio Project
 * AuxioService.kt is part of Auxio.
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
 
package org.oxycblt.auxio.service

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.MediaStore
import androidx.annotation.StringRes
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.BitmapLoader
import androidx.media3.decoder.ffmpeg.FfmpegAudioRenderer
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.audio.AudioCapabilities
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.image.BitmapProvider
import org.oxycblt.auxio.list.ListSettings
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.IndexingProgress
import org.oxycblt.auxio.music.IndexingState
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.device.DeviceLibrary
import org.oxycblt.auxio.music.fs.contentResolverSafe
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.music.user.UserLibrary
import org.oxycblt.auxio.playback.ActionMode
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.playback.persist.PersistenceRepository
import org.oxycblt.auxio.playback.replaygain.ReplayGainAudioProcessor
import org.oxycblt.auxio.playback.service.BetterShuffleOrder
import org.oxycblt.auxio.playback.state.DeferredPlayback
import org.oxycblt.auxio.playback.state.PlaybackCommand
import org.oxycblt.auxio.playback.state.PlaybackStateHolder
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.Progression
import org.oxycblt.auxio.playback.state.RawQueue
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.playback.state.ShuffleMode
import org.oxycblt.auxio.playback.state.StateAck
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.getSystemServiceCompat
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE
import org.oxycblt.auxio.widgets.WidgetComponent
import org.oxycblt.auxio.widgets.WidgetProvider

@AndroidEntryPoint
class AuxioService :
    MediaLibraryService(),
    MediaLibrarySession.Callback,
    MusicRepository.IndexingWorker,
    MusicRepository.IndexingListener,
    MusicRepository.UpdateListener,
    MusicSettings.Listener,
    PlaybackStateHolder,
    Player.Listener,
    PlaybackSettings.Listener {
    @Inject lateinit var musicRepository: MusicRepository
    @Inject lateinit var musicSettings: MusicSettings

    private lateinit var indexingNotification: IndexingNotification
    private lateinit var observingNotification: ObservingNotification
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var indexerContentObserver: SystemContentObserver
    private val serviceJob = Job()
    private val indexScope = CoroutineScope(serviceJob + Dispatchers.IO)
    private var currentIndexJob: Job? = null

    @Inject lateinit var playbackManager: PlaybackStateManager
    @Inject lateinit var commandFactory: PlaybackCommand.Factory
    @Inject lateinit var playbackSettings: PlaybackSettings
    @Inject lateinit var persistenceRepository: PersistenceRepository
    @Inject lateinit var mediaSourceFactory: MediaSource.Factory
    @Inject lateinit var replayGainProcessor: ReplayGainAudioProcessor
    private lateinit var player: NeoPlayer
    private lateinit var mediaSession: MediaLibrarySession
    private val systemReceiver = PlaybackReceiver()
    private val restoreScope = CoroutineScope(serviceJob + Dispatchers.IO)
    private val saveScope = CoroutineScope(serviceJob + Dispatchers.IO)
    private var currentSaveJob: Job? = null
    private var inPlayback = false
    private var openAudioEffectSession = false

    @Inject lateinit var listSettings: ListSettings
    @Inject lateinit var widgetComponent: WidgetComponent
    @Inject lateinit var bitmapLoader: NeoBitmapLoader

    override fun onCreate() {
        super.onCreate()

        indexingNotification = IndexingNotification(this)
        observingNotification = ObservingNotification(this)
        wakeLock =
            getSystemServiceCompat(PowerManager::class)
                .newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK, BuildConfig.APPLICATION_ID + ":IndexerService")
        // Initialize any listener-dependent components last as we wouldn't want a listener race
        // condition to cause us to load music before we were fully initialize.
        indexerContentObserver = SystemContentObserver()

        // Since Auxio is a music player, only specify an audio renderer to save
        // battery/apk size/cache size
        val audioRenderer = RenderersFactory { handler, _, audioListener, _, _ ->
            arrayOf(
                FfmpegAudioRenderer(handler, audioListener, replayGainProcessor),
                MediaCodecAudioRenderer(
                    this,
                    MediaCodecSelector.DEFAULT,
                    handler,
                    audioListener,
                    AudioCapabilities.DEFAULT_AUDIO_CAPABILITIES,
                    replayGainProcessor))
        }

        val exoPlayer =
            ExoPlayer.Builder(this, audioRenderer)
                .setMediaSourceFactory(mediaSourceFactory)
                // Enable automatic WakeLock support
                .setWakeMode(C.WAKE_MODE_LOCAL)
                .setAudioAttributes(
                    // Signal that we are a music player.
                    AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .build(),
                    true)
                .build()
                .also { it.addListener(this) }

        player = NeoPlayer(this, exoPlayer, musicRepository, playbackSettings)
        setMediaNotificationProvider(
            DefaultMediaNotificationProvider.Builder(this)
                .setNotificationId(IntegerTable.PLAYBACK_NOTIFICATION_CODE)
                .setChannelId(BuildConfig.APPLICATION_ID + ".channel.PLAYBACK")
                .setChannelName(R.string.lbl_playback)
                .build()
                .also { it.setSmallIcon(R.drawable.ic_auxio_24) })
        mediaSession =
            MediaLibrarySession.Builder(this, player, this).setBitmapLoader(bitmapLoader).build()
        addSession(mediaSession)
        updateCustomButtons()

        val intentFilter =
            IntentFilter().apply {
                addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
                addAction(AudioManager.ACTION_HEADSET_PLUG)
                addAction(ACTION_INC_REPEAT_MODE)
                addAction(ACTION_INVERT_SHUFFLE)
                addAction(ACTION_SKIP_PREV)
                addAction(ACTION_PLAY_PAUSE)
                addAction(ACTION_SKIP_NEXT)
                addAction(ACTION_EXIT)
                addAction(WidgetProvider.ACTION_WIDGET_UPDATE)
            }

        ContextCompat.registerReceiver(
            this, systemReceiver, intentFilter, ContextCompat.RECEIVER_EXPORTED)

        musicSettings.registerListener(this)
        musicRepository.addUpdateListener(this)
        musicRepository.addIndexingListener(this)
        musicRepository.registerWorker(this)

        // Initialize any listener-dependent components last as we wouldn't want a listener race
        // condition to cause us to load music before we were fully initialize.
        playbackManager.registerStateHolder(this)
        musicRepository.addUpdateListener(this)
        playbackSettings.registerListener(this)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (!playbackManager.progression.isPlaying) {
            // Stop the service if not playing, continue playing in the background
            // otherwise.
            endSession()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // De-initialize core service components first.
        wakeLock.releaseSafe()
        // Then cancel the listener-dependent components to ensure that stray reloading
        // events will not occur.
        indexerContentObserver.release()
        musicSettings.unregisterListener(this)
        musicRepository.removeUpdateListener(this)
        musicRepository.removeIndexingListener(this)
        musicRepository.unregisterWorker(this)
        // Then cancel any remaining music loading jobs.
        serviceJob.cancel()

        // Pause just in case this destruction was unexpected.
        playbackManager.playing(false)
        playbackManager.unregisterStateHolder(this)
        musicRepository.removeUpdateListener(this)
        playbackSettings.unregisterListener(this)

        serviceJob.cancel()

        replayGainProcessor.release()
        player.release()
        if (openAudioEffectSession) {
            // Make sure to close the audio session when we release the player.
            broadcastAudioEffectAction(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION)
            openAudioEffectSession = false
        }

        removeSession(mediaSession)
        mediaSession.release()
        player.release()
    }

    // --- INDEXER OVERRIDES ---

    override fun requestIndex(withCache: Boolean) {
        logD("Starting new indexing job (previous=${currentIndexJob?.hashCode()})")
        // Cancel the previous music loading job.
        currentIndexJob?.cancel()
        // Start a new music loading job on a co-routine.
        currentIndexJob = musicRepository.index(this, withCache)
    }

    override val workerContext: Context
        get() = this

    override val scope = indexScope

    override fun onIndexingStateChanged() {
        updateForeground(forMusic = true)
    }

    // --- INTERNAL ---

    private fun updateForeground(forMusic: Boolean) {
        if (inPlayback) {
            if (!forMusic) {
                val notification =
                    mediaNotificationProvider.createNotification(
                        mediaSession,
                        mediaSession.customLayout,
                        mediaNotificationManager.actionFactory) { notification ->
                            postMediaNotification(notification, mediaSession)
                        }
                postMediaNotification(notification, mediaSession)
            }
            return
        }

        val state = musicRepository.indexingState
        if (state is IndexingState.Indexing) {
            updateLoadingForeground(state.progress)
        } else {
            updateIdleForeground()
        }
    }

    private fun updateLoadingForeground(progress: IndexingProgress) {
        // When loading, we want to enter the foreground state so that android does
        // not shut off the loading process. Note that while we will always post the
        // notification when initially starting, we will not update the notification
        // unless it indicates that it has changed.
        val changed = indexingNotification.updateIndexingState(progress)
        if (changed) {
            logD("Notification changed, re-posting notification")
            startForeground(indexingNotification.code, indexingNotification.build())
        }
        // Make sure we can keep the CPU on while loading music
        wakeLock.acquireSafe()
    }

    private fun updateIdleForeground() {
        if (musicSettings.shouldBeObserving) {
            // There are a few reasons why we stay in the foreground with automatic rescanning:
            // 1. Newer versions of Android have become more and more restrictive regarding
            // how a foreground service starts. Thus, it's best to go foreground now so that
            // we can go foreground later.
            // 2. If a non-foreground service is killed, the app will probably still be alive,
            // and thus the music library will not be updated at all.
            // TODO: Assuming I unify this with PlaybackService, it's possible that I won't need
            //  this anymore, or at least I only have to use it when the app task is not removed.
            logD("Need to observe, staying in foreground")
            startForeground(observingNotification.code, observingNotification.build())
        } else {
            // Not observing and done loading, exit foreground.
            logD("Exiting foreground")
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_DETACH)
        }
        // Release our wake lock (if we were using it)
        wakeLock.releaseSafe()
    }

    /** Utility to safely acquire a [PowerManager.WakeLock] without crashes/inefficiency. */
    private fun PowerManager.WakeLock.acquireSafe() {
        // Avoid unnecessary acquire calls.
        if (!wakeLock.isHeld) {
            logD("Acquiring wake lock")
            // Time out after a minute, which is the average music loading time for a medium-sized
            // library. If this runs out, we will re-request the lock, and if music loading is
            // shorter than the timeout, it will be released early.
            acquire(WAKELOCK_TIMEOUT_MS)
        }
    }

    /** Utility to safely release a [PowerManager.WakeLock] without crashes/inefficiency. */
    private fun PowerManager.WakeLock.releaseSafe() {
        // Avoid unnecessary release calls.
        if (wakeLock.isHeld) {
            logD("Releasing wake lock")
            release()
        }
    }

    // --- SETTING CALLBACKS ---

    override fun onIndexingSettingChanged() {
        // Music loading configuration changed, need to reload music.
        requestIndex(true)
    }

    override fun onObservingChanged() {
        // Make sure we don't override the service state with the observing
        // notification if we were actively loading when the automatic rescanning
        // setting changed. In such a case, the state will still be updated when
        // the music loading process ends.
        if (currentIndexJob == null) {
            logD("Not loading, updating idle session")
            updateForeground(forMusic = false)
        }
    }

    /**
     * A [ContentObserver] that observes the [MediaStore] music database for changes, a behavior
     * known to the user as automatic rescanning. The active (and not passive) nature of observing
     * the database is what requires [IndexerService] to stay foreground when this is enabled.
     */
    private inner class SystemContentObserver :
        ContentObserver(Handler(Looper.getMainLooper())), Runnable {
        private val handler = Handler(Looper.getMainLooper())

        init {
            contentResolverSafe.registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, this)
        }

        /**
         * Release this instance, preventing it from further observing the database and cancelling
         * any pending update events.
         */
        fun release() {
            handler.removeCallbacks(this)
            contentResolverSafe.unregisterContentObserver(this)
        }

        override fun onChange(selfChange: Boolean) {
            // Batch rapid-fire updates to the library into a single call to run after 500ms
            handler.removeCallbacks(this)
            handler.postDelayed(this, REINDEX_DELAY_MS)
        }

        override fun run() {
            // Check here if we should even start a reindex. This is much less bug-prone than
            // registering and de-registering this component as this setting changes.
            if (musicSettings.shouldBeObserving) {
                logD("MediaStore changed, starting re-index")
                requestIndex(true)
            }
        }
    }

    // --- PLAYBACKSTATEHOLDER OVERRIDES ---

    override val progression: Progression
        get() =
            player.currentMediaItem?.let {
                Progression.from(
                    player.playWhenReady,
                    player.isPlaying,
                    // The position value can be below zero or past the expected duration, make
                    // sure we handle that.
                    player.currentPosition
                        .coerceAtLeast(0)
                        .coerceAtMost(player.durationMs ?: Long.MAX_VALUE))
            }
                ?: Progression.nil()

    override val repeatMode
        get() =
            when (val repeatMode = player.repeatMode) {
                Player.REPEAT_MODE_OFF -> RepeatMode.NONE
                Player.REPEAT_MODE_ONE -> RepeatMode.TRACK
                Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                else -> throw IllegalStateException("Unknown repeat mode: $repeatMode")
            }

    override val parent: MusicParent?
        get() = player.parent

    override val audioSessionId: Int
        get() = player.audioSessionId

    override fun resolveQueue() = player.resolveQueue()

    override fun newPlayback(
        queue: List<Song>,
        start: Song?,
        parent: MusicParent?,
        shuffled: Boolean
    ) {
        player.newPlayback(queue, start, parent, shuffled)
        updateCustomButtons()
        playbackManager.ack(this, StateAck.NewPlayback)
        deferSave()
    }

    override fun playing(playing: Boolean) {
        player.playWhenReady = playing
        // Dispatched later once all of the changes have been accumulated
        // Playing state is not persisted, do not need to save
    }

    override fun repeatMode(repeatMode: RepeatMode) {
        player.repeatMode(repeatMode)
        playbackManager.ack(this, StateAck.RepeatModeChanged)
        deferSave()
        updateCustomButtons()
    }

    override fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        // Dispatched later once all of the changes have been accumulated
        // Deferred save is handled on position discontinuity
    }

    override fun next() {
        player.seekToNext()
        // Deferred save is handled on position discontinuity
    }

    override fun prev() {
        player.seekToNext()
        // Deferred save is handled on position discontinuity
    }

    override fun goto(index: Int) {
        player.goto(index)
        // Deferred save is handled on position discontinuity
    }

    override fun shuffled(shuffled: Boolean) {
        logD("Reordering queue to $shuffled")
        player.shuffleModeEnabled = shuffled
        playbackManager.ack(this, StateAck.QueueReordered)
        deferSave()
        updateCustomButtons()
    }

    override fun playNext(songs: List<Song>, ack: StateAck.PlayNext) {
        player.playNext(songs)
        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun addToQueue(songs: List<Song>, ack: StateAck.AddToQueue) {
        player.addToQueue(songs)
        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun move(from: Int, to: Int, ack: StateAck.Move) {
        player.move(from, to)
        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun remove(at: Int, ack: StateAck.Remove) {
        playbackManager.ack(this, ack)
        deferSave()
    }

    override fun handleDeferred(action: DeferredPlayback): Boolean {
        val deviceLibrary =
            musicRepository.deviceLibrary
            // No library, cannot do anything.
            ?: return false

        when (action) {
            // Restore state -> Start a new restoreState job
            is DeferredPlayback.RestoreState -> {
                logD("Restoring playback state")
                restoreScope.launch {
                    persistenceRepository.readState()?.let {
                        // Apply the saved state on the main thread to prevent code expecting
                        // state updates on the main thread from crashing.
                        withContext(Dispatchers.Main) { playbackManager.applySavedState(it, false) }
                    }
                }
            }
            // Shuffle all -> Start new playback from all songs
            is DeferredPlayback.ShuffleAll -> {
                logD("Shuffling all tracks")
                playbackManager.play(
                    requireNotNull(commandFactory.all(ShuffleMode.ON)) {
                        "Invalid playback parameters"
                    })
            }
            // Open -> Try to find the Song for the given file and then play it from all songs
            is DeferredPlayback.Open -> {
                logD("Opening specified file")
                deviceLibrary.findSongForUri(workerContext, action.uri)?.let { song ->
                    playbackManager.play(
                        requireNotNull(commandFactory.song(song, ShuffleMode.IMPLICIT)) {
                            "Invalid playback parameters"
                        })
                }
            }
        }

        return true
    }

    override fun applySavedState(
        parent: MusicParent?,
        rawQueue: RawQueue,
        ack: StateAck.NewPlayback?
    ) {
        player.applySavedState(parent, rawQueue)
        ack?.let { playbackManager.ack(this, it) }
    }

    override fun reset(ack: StateAck.NewPlayback) {
        player.setMediaItems(emptyList())
        playbackManager.ack(this, ack)
    }

    // --- PLAYER OVERRIDES ---

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)

        if (player.playWhenReady) {
            // Mark that we have started playing so that the notification can now be posted.
            logD("Player has started playing")
            inPlayback = true
            if (!openAudioEffectSession) {
                // Convention to start an audioeffect session on play/pause rather than
                // start/stop
                logD("Opening audio effect session")
                broadcastAudioEffectAction(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION)
                openAudioEffectSession = true
            }
        } else if (openAudioEffectSession) {
            // Make sure to close the audio session when we stop playback.
            logD("Closing audio effect session")
            broadcastAudioEffectAction(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION)
            openAudioEffectSession = false
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)

        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO ||
            reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK) {
            playbackManager.ack(this, StateAck.IndexMoved)
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

        if (playbackState == Player.STATE_ENDED && player.repeatMode == Player.REPEAT_MODE_OFF) {
            goto(0)
            player.pause()
        }
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
        if (reason == Player.DISCONTINUITY_REASON_SEEK) {
            // TODO: Once position also naturally drifts by some threshold, save
            deferSave()
        }
    }

    override fun onEvents(player: Player, events: Player.Events) {
        super.onEvents(player, events)

        if (events.containsAny(
            Player.EVENT_PLAY_WHEN_READY_CHANGED,
            Player.EVENT_IS_PLAYING_CHANGED,
            Player.EVENT_POSITION_DISCONTINUITY)) {
            logD("Player state changed, must synchronize state")
            playbackManager.ack(this, StateAck.ProgressionChanged)
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        // TODO: Replace with no skipping and a notification instead
        // If there's any issue, just go to the next song.
        logE("Player error occured")
        logE(error.stackTraceToString())
        playbackManager.next()
    }

    // --- OTHER OVERRIDES ---

    override fun onNotificationActionChanged() {
        super.onNotificationActionChanged()
        updateCustomButtons()
    }

    override fun onPauseOnRepeatChanged() {
        player.updatePauseOnRepeat()
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        if (changes.deviceLibrary && musicRepository.deviceLibrary != null) {
            // We now have a library, see if we have anything we need to do.
            logD("Library obtained, requesting action")
            playbackManager.requestAction(this)
        }
    }

    // --- MEDIASESSION OVERRIDES ---

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession =
        mediaSession

    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
        updateForeground(forMusic = false)
    }

    private fun postMediaNotification(notification: MediaNotification, session: MediaSession) {
        // Pulled from MediaNotificationManager: Need to specify MediaSession token manually
        // in notification
        val fwkToken = session.sessionCompatToken.token as android.media.session.MediaSession.Token
        notification.notification.extras.putParcelable(Notification.EXTRA_MEDIA_SESSION, fwkToken)
        startForeground(notification.notificationId, notification.notification)
    }

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): ConnectionResult {
        logD(Exception().stackTraceToString())
        val sessionCommands =
            ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS.buildUpon()
                .add(SessionCommand(ACTION_INC_REPEAT_MODE, Bundle.EMPTY))
                .add(SessionCommand(ACTION_INVERT_SHUFFLE, Bundle.EMPTY))
                .add(SessionCommand(ACTION_EXIT, Bundle.EMPTY))
                .build()
        logD(controller)
        return ConnectionResult.AcceptedResultBuilder(session)
            .setAvailableSessionCommands(sessionCommands)
            .build()
    }

    override fun onGetLibraryRoot(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: LibraryParams?
    ): ListenableFuture<LibraryResult<MediaItem>> {
        val result =
            LibraryResult.ofItem(
                ExternalUID.Category.ROOT.toMediaItem(this), LibraryParams.Builder().build())
        return Futures.immediateFuture(result)
    }

    override fun onGetItem(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String
    ): ListenableFuture<LibraryResult<MediaItem>> {
        // TODO
        return super.onGetItem(session, browser, mediaId)
    }

    override fun onGetChildren(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
        val deviceLibrary = musicRepository.deviceLibrary
        val userLibrary = musicRepository.userLibrary
        if (deviceLibrary == null || userLibrary == null) {
            return Futures.immediateFuture(
                LibraryResult.ofItemList(emptyList(), LibraryParams.Builder().build()))
        }

        val items =
            getMediaItemList(parentId, deviceLibrary, userLibrary)
                ?: return Futures.immediateFuture(
                    LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE))
        val result = LibraryResult.ofItemList(items, LibraryParams.Builder().build())
        return Futures.immediateFuture(result)
    }

    private fun getMediaItemList(
        id: String,
        deviceLibrary: DeviceLibrary,
        userLibrary: UserLibrary
    ): List<MediaItem>? {
        return when (val externalUID = ExternalUID.fromString(id)) {
            is ExternalUID.Category -> {
                when (externalUID) {
                    ExternalUID.Category.ROOT ->
                        listOf(
                                ExternalUID.Category.SONGS,
                                ExternalUID.Category.ALBUMS,
                                ExternalUID.Category.ARTISTS,
                                ExternalUID.Category.GENRES,
                                ExternalUID.Category.PLAYLISTS)
                            .map { it.toMediaItem(this) }
                    ExternalUID.Category.SONGS ->
                        deviceLibrary.songs.map { it.toMediaItem(this, null) }
                    ExternalUID.Category.ALBUMS ->
                        deviceLibrary.albums.map { it.toMediaItem(this, null) }
                    ExternalUID.Category.ARTISTS ->
                        deviceLibrary.artists.map { it.toMediaItem(this, null) }
                    ExternalUID.Category.GENRES -> deviceLibrary.genres.map { it.toMediaItem(this) }
                    ExternalUID.Category.PLAYLISTS ->
                        userLibrary.playlists.map { it.toMediaItem(this) }
                }
            }
            is ExternalUID.Single -> {
                getChildMediaItems(externalUID.uid) ?: return null
            }
            is ExternalUID.Joined -> {
                getChildMediaItems(externalUID.childUid) ?: return null
            }
            null -> return null
        }
    }

    private fun getChildMediaItems(uid: Music.UID): List<MediaItem>? {
        return when (val item = musicRepository.find(uid)) {
            is Album -> {
                item.songs.map { it.toMediaItem(this, item) }
            }
            is Artist -> {
                (item.explicitAlbums + item.implicitAlbums).map { it.toMediaItem(this, item) } +
                    item.songs.map { it.toMediaItem(this, item) }
            }
            is Genre -> {
                item.songs.map { it.toMediaItem(this, item) }
            }
            is Playlist -> {
                item.songs.map { it.toMediaItem(this, item) }
            }
            is Song,
            null -> return null
        }
    }

    override fun onSetMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        val deviceLibrary =
            musicRepository.deviceLibrary
                ?: return Futures.immediateFailedFuture(Exception("Invalid state"))
        val result =
            if (mediaItems.size > 1) {
                playMediaItemSelection(mediaItems, startIndex, deviceLibrary)
            } else {
                playSingleMediaItem(mediaItems.first(), deviceLibrary)
            }
        return if (result) {
            // This will not actually do anything to the player, I patched that out
            Futures.immediateFuture(
                MediaSession.MediaItemsWithStartPosition(listOf(), C.INDEX_UNSET, C.TIME_UNSET))
        } else {
            Futures.immediateFailedFuture(Exception("Invalid state"))
        }
    }

    private fun playMediaItemSelection(
        mediaItems: List<MediaItem>,
        startIndex: Int,
        deviceLibrary: DeviceLibrary
    ): Boolean {
        val targetSong = mediaItems.getOrNull(startIndex)?.toSong(deviceLibrary)
        val songs = mediaItems.mapNotNull { it.toSong(deviceLibrary) }
        var index = startIndex
        if (targetSong != null) {
            while (songs.getOrNull(index)?.uid != targetSong.uid) {
                index--
            }
        }
        playbackManager.play(commandFactory.songs(songs, ShuffleMode.OFF) ?: return false)
        return true
    }

    private fun playSingleMediaItem(mediaItem: MediaItem, deviceLibrary: DeviceLibrary): Boolean {
        val uid = ExternalUID.fromString(mediaItem.mediaId) ?: return false
        val music: Music
        var parent: MusicParent? = null
        when (uid) {
            is ExternalUID.Single -> {
                music = musicRepository.find(uid.uid) ?: return false
            }
            is ExternalUID.Joined -> {
                music = musicRepository.find(uid.childUid) ?: return false
                parent = musicRepository.find(uid.parentUid) as? MusicParent ?: return false
            }
            else -> return false
        }

        val command =
            when (music) {
                is Song -> inferSongFromParentCommand(music, parent)
                is Album -> commandFactory.album(music, ShuffleMode.OFF)
                is Artist -> commandFactory.artist(music, ShuffleMode.OFF)
                is Genre -> commandFactory.genre(music, ShuffleMode.OFF)
                is Playlist -> commandFactory.playlist(music, ShuffleMode.OFF)
            }

        playbackManager.play(command ?: return false)

        return true
    }

    private fun inferSongFromParentCommand(music: Song, parent: MusicParent?) =
        when (parent) {
            is Album -> commandFactory.songFromAlbum(music, ShuffleMode.IMPLICIT)
            is Artist -> commandFactory.songFromArtist(music, parent, ShuffleMode.IMPLICIT)
                    ?: commandFactory.songFromArtist(music, music.artists[0], ShuffleMode.IMPLICIT)
            is Genre -> commandFactory.songFromGenre(music, parent, ShuffleMode.IMPLICIT)
                    ?: commandFactory.songFromGenre(music, music.genres[0], ShuffleMode.IMPLICIT)
            is Playlist -> commandFactory.songFromPlaylist(music, parent, ShuffleMode.IMPLICIT)
            null -> commandFactory.songFromAll(music, ShuffleMode.IMPLICIT)
        }

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        val deviceLibrary =
            musicRepository.deviceLibrary ?: return Futures.immediateFuture(mutableListOf())
        val songs = mediaItems.mapNotNull { it.toSong(deviceLibrary) }
        playbackManager.addToQueue(songs)
        // This will not actually do anything to the player, I patched that out
        return Futures.immediateFuture(mutableListOf())
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> =
        when (customCommand.customAction) {
            ACTION_INC_REPEAT_MODE -> {
                repeatMode(repeatMode.increment())
                Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
            }
            ACTION_INVERT_SHUFFLE -> {
                shuffled(!player.shuffleModeEnabled)
                Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
            }
            ACTION_EXIT -> {
                endSession()
                Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
            }
            else -> super.onCustomCommand(session, controller, customCommand, args)
        }

    private fun updateCustomButtons() {
        val actions = mutableListOf<CommandButton>()

        when (playbackSettings.notificationAction) {
            ActionMode.REPEAT -> {
                actions.add(
                    CommandButton.Builder()
                        .setIconResId(playbackManager.repeatMode.icon)
                        .setDisplayName(getString(R.string.desc_change_repeat))
                        .setSessionCommand(SessionCommand(ACTION_INC_REPEAT_MODE, Bundle()))
                        .build())
            }
            ActionMode.SHUFFLE -> {
                actions.add(
                    CommandButton.Builder()
                        .setIconResId(
                            if (player.shuffleModeEnabled) R.drawable.ic_shuffle_on_24
                            else R.drawable.ic_shuffle_off_24)
                        .setDisplayName(getString(R.string.lbl_shuffle))
                        .setSessionCommand(SessionCommand(ACTION_INVERT_SHUFFLE, Bundle()))
                        .build())
            }
            else -> {}
        }

        actions.add(
            CommandButton.Builder()
                .setIconResId(R.drawable.ic_close_24)
                .setDisplayName(getString(R.string.desc_exit))
                .setSessionCommand(SessionCommand(ACTION_EXIT, Bundle()))
                .build())

        mediaSession.setCustomLayout(actions)
    }

    private fun deferSave() {
        saveJob {
            logD("Waiting for save buffer")
            delay(SAVE_BUFFER)
            yield()
            logD("Committing saved state")
            persistenceRepository.saveState(playbackManager.toSavedState())
        }
    }

    private fun saveJob(block: suspend () -> Unit) {
        currentSaveJob?.let {
            logD("Discarding prior save job")
            it.cancel()
        }
        currentSaveJob = saveScope.launch { block() }
    }

    private fun broadcastAudioEffectAction(event: String) {
        logD("Broadcasting AudioEffect event: $event")
        sendBroadcast(
            Intent(event)
                .putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName)
                .putExtra(AudioEffect.EXTRA_AUDIO_SESSION, audioSessionId)
                .putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC))
    }

    private fun endSession() {
        // This session has ended, so we need to reset this flag for when the next
        // session starts.
        saveJob {
            logD("Committing saved state")
            persistenceRepository.saveState(playbackManager.toSavedState())
            withContext(Dispatchers.Main) {
                // User could feasibly start playing again if they were fast enough, so
                // we need to avoid stopping the foreground state if that's the case.
                if (player.isPlaying) {
                    playbackManager.playing(false)
                }
                inPlayback = false
                updateForeground(forMusic = false)
            }
        }
    }

    /**
     * A [BroadcastReceiver] for receiving playback-specific [Intent]s from the system that require
     * an active [IntentFilter] to be registered.
     */
    private inner class PlaybackReceiver : BroadcastReceiver() {
        private var initialHeadsetPlugEventHandled = false

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                // --- SYSTEM EVENTS ---

                // Android has three different ways of handling audio plug events for some reason:
                // 1. ACTION_HEADSET_PLUG, which only works with wired headsets
                // 2. ACTION_ACL_CONNECTED, which allows headset autoplay but also requires
                // granting the BLUETOOTH/BLUETOOTH_CONNECT permissions, which is more or less
                // a non-starter since both require me to display a permission prompt
                // 3. Some internal framework thing that also handles bluetooth headsets
                // Just use ACTION_HEADSET_PLUG.
                AudioManager.ACTION_HEADSET_PLUG -> {
                    logD("Received headset plug event")
                    when (intent.getIntExtra("state", -1)) {
                        0 -> pauseFromHeadsetPlug()
                        1 -> playFromHeadsetPlug()
                    }

                    initialHeadsetPlugEventHandled = true
                }
                AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                    logD("Received Headset noise event")
                    pauseFromHeadsetPlug()
                }

                // --- AUXIO EVENTS ---
                ACTION_PLAY_PAUSE -> {
                    logD("Received play event")
                    playbackManager.playing(!playbackManager.progression.isPlaying)
                }
                ACTION_INC_REPEAT_MODE -> {
                    logD("Received repeat mode event")
                    playbackManager.repeatMode(playbackManager.repeatMode.increment())
                }
                ACTION_INVERT_SHUFFLE -> {
                    logD("Received shuffle event")
                    playbackManager.shuffled(!playbackManager.isShuffled)
                }
                ACTION_SKIP_PREV -> {
                    logD("Received skip previous event")
                    playbackManager.prev()
                }
                ACTION_SKIP_NEXT -> {
                    logD("Received skip next event")
                    playbackManager.next()
                }
                ACTION_EXIT -> {
                    logD("Received exit event")
                    playbackManager.playing(false)
                    endSession()
                }
                WidgetProvider.ACTION_WIDGET_UPDATE -> {
                    logD("Received widget update event")
                    widgetComponent.update()
                }
            }
        }

        private fun playFromHeadsetPlug() {
            // ACTION_HEADSET_PLUG will fire when this BroadcastReciever is initially attached,
            // which would result in unexpected playback. Work around it by dropping the first
            // call to this function, which should come from that Intent.
            if (playbackSettings.headsetAutoplay &&
                playbackManager.currentSong != null &&
                initialHeadsetPlugEventHandled) {
                logD("Device connected, resuming")
                playbackManager.playing(true)
            }
        }

        private fun pauseFromHeadsetPlug() {
            if (playbackManager.currentSong != null) {
                logD("Device disconnected, pausing")
                playbackManager.playing(false)
            }
        }
    }

    companion object {
        const val WAKELOCK_TIMEOUT_MS = 60 * 1000L
        const val REINDEX_DELAY_MS = 500L
        const val SAVE_BUFFER = 5000L
        const val ACTION_INC_REPEAT_MODE = BuildConfig.APPLICATION_ID + ".action.LOOP"
        const val ACTION_INVERT_SHUFFLE = BuildConfig.APPLICATION_ID + ".action.SHUFFLE"
        const val ACTION_SKIP_PREV = BuildConfig.APPLICATION_ID + ".action.PREV"
        const val ACTION_PLAY_PAUSE = BuildConfig.APPLICATION_ID + ".action.PLAY_PAUSE"
        const val ACTION_SKIP_NEXT = BuildConfig.APPLICATION_ID + ".action.NEXT"
        const val ACTION_EXIT = BuildConfig.APPLICATION_ID + ".action.EXIT"
    }
}

class NeoPlayer(
    val context: Context,
    val player: ExoPlayer,
    val musicRepository: MusicRepository,
    val playbackSettings: PlaybackSettings
) : ForwardingPlayer(player) {
    var parent: MusicParent? = null
        private set

    val audioSessionId: Int
        get() = player.audioSessionId

    val durationMs: Long?
        get() =
            musicRepository.deviceLibrary?.let {
                currentMediaItem?.mediaMetadata?.extras?.getLong("durationMs")
            }

    fun setShuffleOrder(order: BetterShuffleOrder) {
        player.setShuffleOrder(order)
    }

    override fun getAvailableCommands(): Player.Commands {
        return super.getAvailableCommands()
            .buildUpon()
            .addAll(Player.COMMAND_SEEK_TO_NEXT, Player.COMMAND_SEEK_TO_PREVIOUS)
            .build()
    }

    override fun isCommandAvailable(command: Int): Boolean {
        // We can always skip forward and backward (this is to retain parity with the old behavior)
        return super.isCommandAvailable(command) ||
            command in setOf(Player.COMMAND_SEEK_TO_NEXT, Player.COMMAND_SEEK_TO_PREVIOUS)
    }

    override fun seekToNext() {
        // Replicate the old pseudo-circular queue behavior when no repeat option is implemented.
        // Basically, you can't skip back and wrap around the queue, but you can skip forward and
        // wrap around the queue, albeit playback will be paused.
        if (repeatMode != REPEAT_MODE_OFF || hasNextMediaItem()) {
            super.seekToNext()
            if (!playbackSettings.rememberPause) {
                play()
            }
        } else {
            seekTo(currentTimeline.getFirstWindowIndex(shuffleModeEnabled), C.TIME_UNSET)
            // TODO: Dislike the UX implications of this, I feel should I bite the bullet
            //  and switch to dynamic skip enable/disable?
            if (!playbackSettings.rememberPause) {
                pause()
            }
        }
    }

    override fun seekToPrevious() {
        if (playbackSettings.rewindWithPrev) {
            super.seekToPrevious()
        } else {
            seekToPreviousMediaItem()
        }
        if (!playbackSettings.rememberPause) {
            play()
        }
    }

    override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {
        super.setShuffleModeEnabled(shuffleModeEnabled)
        if (shuffleModeEnabled) {
            // Have to manually refresh the shuffle seed and anchor it to the new current songs
            setShuffleOrder(BetterShuffleOrder(mediaItemCount, currentMediaItemIndex))
        }
    }

    fun newPlayback(queue: List<Song>, start: Song?, parent: MusicParent?, shuffled: Boolean) {
        this.parent = parent
        super.setShuffleModeEnabled(shuffleModeEnabled)
        setMediaItems(queue.map { it.toMediaItem(context, null) })
        val startIndex =
            start
                ?.let { queue.indexOf(start) }
                .also { check(it != -1) { "Start song not in queue" } }
        if (shuffled) {
            setShuffleOrder(BetterShuffleOrder(queue.size, startIndex ?: -1))
        }
        val target = startIndex ?: currentTimeline.getFirstWindowIndex(shuffleModeEnabled)
        seekTo(target, C.TIME_UNSET)
        prepare()
        play()
    }

    fun repeatMode(repeatMode: RepeatMode) {
        this.repeatMode =
            when (repeatMode) {
                RepeatMode.NONE -> REPEAT_MODE_OFF
                RepeatMode.ALL -> REPEAT_MODE_ALL
                RepeatMode.TRACK -> REPEAT_MODE_ONE
            }
        updatePauseOnRepeat()
    }

    fun goto(index: Int) {
        val indices = unscrambleQueueIndices()
        if (indices.isEmpty()) {
            return
        }

        val trueIndex = indices[index]
        seekTo(trueIndex, C.TIME_UNSET)
        if (!playbackSettings.rememberPause) {
            play()
        }
    }

    fun playNext(songs: List<Song>) {
        val currTimeline = currentTimeline
        val nextIndex =
            if (currTimeline.isEmpty) {
                C.INDEX_UNSET
            } else {
                currTimeline.getNextWindowIndex(
                    currentMediaItemIndex, REPEAT_MODE_OFF, shuffleModeEnabled)
            }

        if (nextIndex == C.INDEX_UNSET) {
            addMediaItems(songs.map { it.toMediaItem(context, null) })
        } else {
            addMediaItems(nextIndex, songs.map { it.toMediaItem(context, null) })
        }
    }

    fun addToQueue(songs: List<Song>) {
        addMediaItems(songs.map { it.toMediaItem(context, null) })
    }

    fun move(from: Int, to: Int) {
        val indices = unscrambleQueueIndices()
        if (indices.isEmpty()) {
            return
        }

        val trueFrom = indices[from]
        val trueTo = indices[to]

        when {
            trueFrom > trueTo -> {
                moveMediaItem(trueFrom, trueTo)
                moveMediaItem(trueTo + 1, trueFrom)
            }
            trueTo > trueFrom -> {
                moveMediaItem(trueFrom, trueTo)
                moveMediaItem(trueTo - 1, trueFrom)
            }
        }
    }

    fun remove(at: Int) {
        val indices = unscrambleQueueIndices()
        if (indices.isEmpty()) {
            return
        }

        val trueIndex = indices[at]
        val songWillChange = currentMediaItemIndex == trueIndex
        removeMediaItem(trueIndex)
        if (songWillChange && !playbackSettings.rememberPause) {
            play()
        }
    }

    fun applySavedState(parent: MusicParent?, rawQueue: RawQueue) {
        this.parent = parent
        player.setMediaItems(rawQueue.heap.map { it.toMediaItem(context, null) })
        if (rawQueue.isShuffled) {
            player.shuffleModeEnabled = true
            player.setShuffleOrder(BetterShuffleOrder(rawQueue.shuffledMapping.toIntArray()))
        } else {
            player.shuffleModeEnabled = false
        }
        player.seekTo(rawQueue.heapIndex, C.TIME_UNSET)
        player.prepare()
    }

    fun updatePauseOnRepeat() {
        player.pauseAtEndOfMediaItems =
            repeatMode == Player.REPEAT_MODE_ONE && playbackSettings.pauseOnRepeat
    }

    fun resolveQueue(): RawQueue {
        val deviceLibrary =
            musicRepository.deviceLibrary
            // No library, cannot do anything.
            ?: return RawQueue(emptyList(), emptyList(), 0)
        val heap = (0 until player.mediaItemCount).map { player.getMediaItemAt(it) }
        val shuffledMapping =
            if (shuffleModeEnabled) {
                unscrambleQueueIndices()
            } else {
                emptyList()
            }
        return RawQueue(
            heap.mapNotNull { it.toSong(deviceLibrary) },
            shuffledMapping,
            player.currentMediaItemIndex)
    }

    private fun unscrambleQueueIndices(): List<Int> {
        val timeline = currentTimeline
        if (timeline.isEmpty) {
            return emptyList()
        }
        val queue = mutableListOf<Int>()

        // Add the active queue item.
        val currentMediaItemIndex = currentMediaItemIndex
        queue.add(currentMediaItemIndex)

        // Fill queue alternating with next and/or previous queue items.
        var firstMediaItemIndex = currentMediaItemIndex
        var lastMediaItemIndex = currentMediaItemIndex
        val shuffleModeEnabled = shuffleModeEnabled
        while ((firstMediaItemIndex != C.INDEX_UNSET || lastMediaItemIndex != C.INDEX_UNSET)) {
            // Begin with next to have a longer tail than head if an even sized queue needs to be
            // trimmed.
            if (lastMediaItemIndex != C.INDEX_UNSET) {
                lastMediaItemIndex =
                    timeline.getNextWindowIndex(
                        lastMediaItemIndex, Player.REPEAT_MODE_OFF, shuffleModeEnabled)
                if (lastMediaItemIndex != C.INDEX_UNSET) {
                    queue.add(lastMediaItemIndex)
                }
            }
            if (firstMediaItemIndex != C.INDEX_UNSET) {
                firstMediaItemIndex =
                    timeline.getPreviousWindowIndex(
                        firstMediaItemIndex, Player.REPEAT_MODE_OFF, shuffleModeEnabled)
                if (firstMediaItemIndex != C.INDEX_UNSET) {
                    queue.add(0, firstMediaItemIndex)
                }
            }
        }

        return queue
    }
}

private fun ExternalUID.Category.toMediaItem(context: Context): MediaItem {
    val metadata =
        MediaMetadata.Builder()
            .setTitle(context.getString(nameRes))
            .setIsPlayable(false)
            .setIsBrowsable(true)
            .setMediaType(mediaType)
            .build()
    return MediaItem.Builder().setMediaId(toString()).setMediaMetadata(metadata).build()
}

private fun Song.toMediaItem(context: Context, parent: MusicParent?): MediaItem {
    val externalUID =
        if (parent == null) {
            ExternalUID.Single(uid)
        } else {
            ExternalUID.Joined(parent.uid, uid)
        }
    val metadata =
        MediaMetadata.Builder()
            .setTitle(name.resolve(context))
            .setArtist(artists.resolveNames(context))
            .setAlbumTitle(album.name.resolve(context))
            .setAlbumArtist(album.artists.resolveNames(context))
            .setTrackNumber(track)
            .setDiscNumber(disc?.number)
            .setGenre(genres.resolveNames(context))
            .setDisplayTitle(name.resolve(context))
            .setSubtitle(artists.resolveNames(context))
            .setRecordingYear(album.dates?.min?.year)
            .setRecordingMonth(album.dates?.min?.month)
            .setRecordingDay(album.dates?.min?.day)
            .setReleaseYear(album.dates?.min?.year)
            .setReleaseMonth(album.dates?.min?.month)
            .setReleaseDay(album.dates?.min?.day)
            .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
            .setIsPlayable(true)
            .setIsBrowsable(false)
            .setArtworkUri(album.coverUri.mediaStore)
            .setExtras(
                Bundle().apply {
                    putString("uid", externalUID.toString())
                    putLong("durationMs", durationMs)
                })
            .build()
    return MediaItem.Builder()
        .setUri(uri)
        .setMediaId(externalUID.toString())
        .setMediaMetadata(metadata)
        .build()
}

private fun Album.toMediaItem(context: Context, parent: Artist?): MediaItem {
    val externalUID =
        if (parent == null) {
            ExternalUID.Single(uid)
        } else {
            ExternalUID.Joined(parent.uid, uid)
        }
    val metadata =
        MediaMetadata.Builder()
            .setTitle(name.resolve(context))
            .setArtist(artists.resolveNames(context))
            .setAlbumTitle(name.resolve(context))
            .setAlbumArtist(artists.resolveNames(context))
            .setRecordingYear(dates?.min?.year)
            .setRecordingMonth(dates?.min?.month)
            .setRecordingDay(dates?.min?.day)
            .setReleaseYear(dates?.min?.year)
            .setReleaseMonth(dates?.min?.month)
            .setReleaseDay(dates?.min?.day)
            .setMediaType(MediaMetadata.MEDIA_TYPE_ALBUM)
            .setIsPlayable(true)
            .setIsBrowsable(true)
            .setArtworkUri(coverUri.mediaStore)
            .setExtras(Bundle().apply { putString("uid", externalUID.toString()) })
            .build()
    return MediaItem.Builder().setMediaId(externalUID.toString()).setMediaMetadata(metadata).build()
}

private fun Artist.toMediaItem(context: Context, parent: Genre?): MediaItem {
    val externalUID =
        if (parent == null) {
            ExternalUID.Single(uid)
        } else {
            ExternalUID.Joined(parent.uid, uid)
        }
    val metadata =
        MediaMetadata.Builder()
            .setTitle(name.resolve(context))
            .setSubtitle(
                context.getString(
                    R.string.fmt_two,
                    if (explicitAlbums.isNotEmpty()) {
                        context.getPlural(R.plurals.fmt_album_count, explicitAlbums.size)
                    } else {
                        context.getString(R.string.def_album_count)
                    },
                    if (songs.isNotEmpty()) {
                        context.getPlural(R.plurals.fmt_song_count, songs.size)
                    } else {
                        context.getString(R.string.def_song_count)
                    }))
            .setMediaType(MediaMetadata.MEDIA_TYPE_ARTIST)
            .setIsPlayable(true)
            .setIsBrowsable(true)
            .setGenre(genres.resolveNames(context))
            .setArtworkUri(songs.firstOrNull()?.album?.coverUri?.mediaStore)
            .setExtras(Bundle().apply { putString("uid", externalUID.toString()) })
            .build()
    return MediaItem.Builder().setMediaId(externalUID.toString()).setMediaMetadata(metadata).build()
}

private fun Genre.toMediaItem(context: Context): MediaItem {
    val externalUID = ExternalUID.Single(uid)
    val metadata =
        MediaMetadata.Builder()
            .setTitle(name.resolve(context))
            .setSubtitle(
                if (songs.isNotEmpty()) {
                    context.getPlural(R.plurals.fmt_song_count, songs.size)
                } else {
                    context.getString(R.string.def_song_count)
                })
            .setMediaType(MediaMetadata.MEDIA_TYPE_GENRE)
            .setIsPlayable(true)
            .setIsBrowsable(true)
            .setArtworkUri(songs.firstOrNull()?.album?.coverUri?.mediaStore)
            .setExtras(Bundle().apply { putString("uid", externalUID.toString()) })
            .build()
    return MediaItem.Builder().setMediaId(externalUID.toString()).setMediaMetadata(metadata).build()
}

private fun Playlist.toMediaItem(context: Context): MediaItem {
    val externalUID = ExternalUID.Single(uid)
    val metadata =
        MediaMetadata.Builder()
            .setTitle(name.resolve(context))
            .setSubtitle(
                if (songs.isNotEmpty()) {
                    context.getPlural(R.plurals.fmt_song_count, songs.size)
                } else {
                    context.getString(R.string.def_song_count)
                })
            .setMediaType(MediaMetadata.MEDIA_TYPE_PLAYLIST)
            .setIsPlayable(true)
            .setIsBrowsable(true)
            .setArtworkUri(songs.firstOrNull()?.album?.coverUri?.mediaStore)
            .setExtras(Bundle().apply { putString("uid", externalUID.toString()) })
            .build()
    return MediaItem.Builder().setMediaId(externalUID.toString()).setMediaMetadata(metadata).build()
}

private fun MediaItem.toSong(deviceLibrary: DeviceLibrary): Song? {
    val uid = ExternalUID.fromString(mediaId) ?: return null
    return when (uid) {
        is ExternalUID.Single -> {
            deviceLibrary.findSong(uid.uid)
        }
        is ExternalUID.Joined -> {
            deviceLibrary.findSong(uid.childUid)
        }
        is ExternalUID.Category -> null
    }
}

private fun MediaItem.toParent(deviceLibrary: DeviceLibrary): MusicParent? {
    val uid = ExternalUID.fromString(mediaId) ?: return null
    return when (uid) {
        is ExternalUID.Joined -> {
            deviceLibrary.findArtist(uid.parentUid)
        }
        is ExternalUID.Single -> null
        is ExternalUID.Category -> null
    }
}

class NeoBitmapLoader
@Inject
constructor(
    private val musicRepository: MusicRepository,
    private val bitmapProvider: BitmapProvider
) : BitmapLoader {
    override fun decodeBitmap(data: ByteArray): ListenableFuture<Bitmap> {
        TODO("Not yet implemented")
    }

    override fun loadBitmap(uri: Uri, options: BitmapFactory.Options?): ListenableFuture<Bitmap> {
        TODO("Not yet implemented")
    }

    override fun loadBitmapFromMetadata(metadata: MediaMetadata): ListenableFuture<Bitmap>? {
        val deviceLibrary = musicRepository.deviceLibrary ?: return null
        val future = SettableFuture.create<Bitmap>()
        val song =
            when (val uid = metadata.extras?.getString("uid")?.let { ExternalUID.fromString(it) }) {
                is ExternalUID.Single -> deviceLibrary.findSong(uid.uid)
                is ExternalUID.Joined -> deviceLibrary.findSong(uid.childUid)
                else -> return null
            }
                ?: return null
        bitmapProvider.load(
            song,
            object : BitmapProvider.Target {
                override fun onCompleted(bitmap: Bitmap?) {
                    if (bitmap == null) {
                        future.setException(IllegalStateException("Bitmap is null"))
                    } else {
                        future.set(bitmap)
                    }
                }
            })
        return future
    }
}

sealed interface ExternalUID {
    enum class Category(val id: String, @StringRes val nameRes: Int, val mediaType: Int?) :
        ExternalUID {
        ROOT("root", R.string.info_app_name, null),
        SONGS("songs", R.string.lbl_songs, MediaMetadata.MEDIA_TYPE_MUSIC),
        ALBUMS("albums", R.string.lbl_albums, MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS),
        ARTISTS("artists", R.string.lbl_artists, MediaMetadata.MEDIA_TYPE_FOLDER_ARTISTS),
        GENRES("genres", R.string.lbl_genres, MediaMetadata.MEDIA_TYPE_FOLDER_GENRES),
        PLAYLISTS("playlists", R.string.lbl_playlists, MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS);

        override fun toString() = "$ID_CATEGORY:$id"
    }

    data class Single(val uid: Music.UID) : ExternalUID {
        override fun toString() = "$ID_ITEM:$uid"
    }

    data class Joined(val parentUid: Music.UID, val childUid: Music.UID) : ExternalUID {
        override fun toString() = "$ID_ITEM:$parentUid>$childUid"
    }

    companion object {
        const val ID_CATEGORY = BuildConfig.APPLICATION_ID + ".category"
        const val ID_ITEM = BuildConfig.APPLICATION_ID + ".item"

        fun fromString(str: String): ExternalUID? {
            val parts = str.split(":", limit = 2)
            if (parts.size != 2) {
                return null
            }
            return when (parts[0]) {
                ID_CATEGORY ->
                    when (parts[1]) {
                        Category.ROOT.id -> Category.ROOT
                        Category.SONGS.id -> Category.SONGS
                        Category.ALBUMS.id -> Category.ALBUMS
                        Category.ARTISTS.id -> Category.ARTISTS
                        Category.GENRES.id -> Category.GENRES
                        Category.PLAYLISTS.id -> Category.PLAYLISTS
                        else -> null
                    }
                ID_ITEM -> {
                    val uids = parts[1].split(">", limit = 2)
                    if (uids.size == 1) {
                        Music.UID.fromString(uids[0])?.let { Single(it) }
                    } else {
                        Music.UID.fromString(uids[0])?.let { parent ->
                            Music.UID.fromString(uids[1])?.let { child -> Joined(parent, child) }
                        }
                    }
                }
                else -> return null
            }
        }
    }
}
