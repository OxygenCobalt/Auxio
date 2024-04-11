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
 
package org.oxycblt.auxio

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.MediaStore
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
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
import coil.ImageLoader
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.guava.asListenableFuture
import org.oxycblt.auxio.image.service.NeoBitmapLoader
import org.oxycblt.auxio.music.IndexingProgress
import org.oxycblt.auxio.music.IndexingState
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.fs.contentResolverSafe
import org.oxycblt.auxio.music.service.IndexingNotification
import org.oxycblt.auxio.music.service.MusicMediaItemBrowser
import org.oxycblt.auxio.music.service.ObservingNotification
import org.oxycblt.auxio.playback.ActionMode
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.playback.service.ExoPlaybackStateHolder
import org.oxycblt.auxio.playback.service.SystemPlaybackReceiver
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.Progression
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.util.getSystemServiceCompat
import org.oxycblt.auxio.util.logD

// TODO: Android Auto Hookup
// TODO: Custom notif

@AndroidEntryPoint
class AuxioService :
    MediaLibraryService(),
    MediaLibrarySession.Callback,
    MusicRepository.IndexingWorker,
    MusicRepository.IndexingListener,
    MusicRepository.UpdateListener,
    MusicSettings.Listener,
    PlaybackStateManager.Listener,
    PlaybackSettings.Listener {
    private val serviceJob = Job()
    private var inPlayback = false

    @Inject lateinit var musicRepository: MusicRepository
    @Inject lateinit var musicSettings: MusicSettings
    private lateinit var indexingNotification: IndexingNotification
    private lateinit var observingNotification: ObservingNotification
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var indexerContentObserver: SystemContentObserver
    private val indexScope = CoroutineScope(serviceJob + Dispatchers.IO)
    private var currentIndexJob: Job? = null

    @Inject lateinit var playbackManager: PlaybackStateManager
    @Inject lateinit var playbackSettings: PlaybackSettings
    @Inject lateinit var systemReceiver: SystemPlaybackReceiver
    @Inject lateinit var exoHolderFactory: ExoPlaybackStateHolder.Factory
    private lateinit var exoHolder: ExoPlaybackStateHolder

    @Inject lateinit var bitmapLoader: NeoBitmapLoader
    @Inject lateinit var imageLoader: ImageLoader

    @Inject lateinit var musicMediaItemBrowser: MusicMediaItemBrowser
    private val waitScope = CoroutineScope(serviceJob + Dispatchers.Default)
    private lateinit var mediaSession: MediaLibrarySession

    @SuppressLint("WrongConstant")
    override fun onCreate() {
        super.onCreate()

        indexingNotification = IndexingNotification(this)
        observingNotification = ObservingNotification(this)
        wakeLock =
            getSystemServiceCompat(PowerManager::class)
                .newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK, BuildConfig.APPLICATION_ID + ":IndexerService")

        exoHolder = exoHolderFactory.create()

        mediaSession =
            MediaLibrarySession.Builder(this, exoHolder.mediaSessionPlayer, this)
                .setBitmapLoader(bitmapLoader)
                .build()

        // Initialize any listener-dependent components last as we wouldn't want a listener race
        // condition to cause us to load music before we were fully initialize.
        indexerContentObserver = SystemContentObserver()

        setMediaNotificationProvider(
            DefaultMediaNotificationProvider.Builder(this)
                .setNotificationId(IntegerTable.PLAYBACK_NOTIFICATION_CODE)
                .setChannelId(BuildConfig.APPLICATION_ID + ".channel.PLAYBACK")
                .setChannelName(R.string.lbl_playback)
                .build()
                .also { it.setSmallIcon(R.drawable.ic_auxio_24) })
        addSession(mediaSession)
        updateCustomButtons()

        // Initialize any listener-dependent components last as we wouldn't want a listener race
        // condition to cause us to load music before we were fully initialize.
        exoHolder.attach()
        playbackManager.addListener(this)
        playbackSettings.registerListener(this)

        ContextCompat.registerReceiver(
            this, systemReceiver, systemReceiver.intentFilter, ContextCompat.RECEIVER_EXPORTED)

        musicMediaItemBrowser.attach()
        musicSettings.registerListener(this)
        musicRepository.addUpdateListener(this)
        musicRepository.addIndexingListener(this)
        musicRepository.registerWorker(this)
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
        serviceJob.cancel()
        wakeLock.releaseSafe()
        // Then cancel the listener-dependent components to ensure that stray reloading
        // events will not occur.
        indexerContentObserver.release()
        exoHolder.release()
        musicSettings.unregisterListener(this)
        musicRepository.removeUpdateListener(this)
        musicRepository.removeIndexingListener(this)
        musicRepository.unregisterWorker(this)

        // Pause just in case this destruction was unexpected.
        playbackManager.playing(false)
        playbackManager.unregisterStateHolder(exoHolder)
        playbackSettings.unregisterListener(this)

        removeSession(mediaSession)
        mediaSession.release()
        unregisterReceiver(systemReceiver)
        exoHolder.release()
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

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        val deviceLibrary = musicRepository.deviceLibrary ?: return
        logD("Music changed, updating shared objects")
        // Wipe possibly-invalidated outdated covers
        imageLoader.memoryCache?.clear()
        // Clear invalid models from PlaybackStateManager. This is not connected
        // to a listener as it is bad practice for a shared object to attach to
        // the listener system of another.
        playbackManager.toSavedState()?.let { savedState ->
            playbackManager.applySavedState(
                savedState.copy(
                    heap =
                        savedState.heap.map { song ->
                            song?.let { deviceLibrary.findSong(it.uid) }
                        }),
                true)
        }
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
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
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

    // --- SERVICE MANAGEMENT ---

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession =
        mediaSession

    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
        logD("Notification update requested")
        updateForeground(forMusic = false)
    }

    private fun postMediaNotification(notification: MediaNotification, session: MediaSession) {
        // Pulled from MediaNotificationManager: Need to specify MediaSession token manually
        // in notification
        val fwkToken = session.sessionCompatToken.token as android.media.session.MediaSession.Token
        notification.notification.extras.putParcelable(Notification.EXTRA_MEDIA_SESSION, fwkToken)
        startForeground(notification.notificationId, notification.notification)
    }

    // --- MEDIASESSION CALLBACKS ---

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): ConnectionResult {
        val sessionCommands =
            ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS.buildUpon()
                .add(SessionCommand(ACTION_INC_REPEAT_MODE, Bundle.EMPTY))
                .add(SessionCommand(ACTION_INVERT_SHUFFLE, Bundle.EMPTY))
                .add(SessionCommand(ACTION_EXIT, Bundle.EMPTY))
                .build()
        return ConnectionResult.AcceptedResultBuilder(session)
            .setAvailableSessionCommands(sessionCommands)
            .build()
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> =
        when (customCommand.customAction) {
            ACTION_INC_REPEAT_MODE -> {
                logD(playbackManager.repeatMode.increment())
                playbackManager.repeatMode(playbackManager.repeatMode.increment())
                Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
            }
            ACTION_INVERT_SHUFFLE -> {
                playbackManager.shuffled(!playbackManager.isShuffled)
                Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
            }
            ACTION_EXIT -> {
                endSession()
                Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
            }
            else -> super.onCustomCommand(session, controller, customCommand, args)
        }

    override fun onGetLibraryRoot(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: LibraryParams?
    ): ListenableFuture<LibraryResult<MediaItem>> =
        Futures.immediateFuture(LibraryResult.ofItem(musicMediaItemBrowser.root, params))

    override fun onGetItem(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String
    ): ListenableFuture<LibraryResult<MediaItem>> {
        val result =
            musicMediaItemBrowser.getItem(mediaId)?.let { LibraryResult.ofItem(it, null) }
                ?: LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
        return Futures.immediateFuture(result)
    }

    override fun onSetMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> =
        Futures.immediateFuture(
            MediaSession.MediaItemsWithStartPosition(mediaItems, startIndex, startPositionMs))

    override fun onGetChildren(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
        val children =
            musicMediaItemBrowser.getChildren(parentId, page, pageSize)?.let {
                LibraryResult.ofItemList(it, params)
            }
                ?: LibraryResult.ofError<ImmutableList<MediaItem>>(
                    LibraryResult.RESULT_ERROR_BAD_VALUE)
        return Futures.immediateFuture(children)
    }

    override fun onSearch(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        query: String,
        params: LibraryParams?
    ): ListenableFuture<LibraryResult<Void>> =
        waitScope
            .async {
                val count = musicMediaItemBrowser.prepareSearch(query)
                session.notifySearchResultChanged(browser, query, count, params)
                LibraryResult.ofVoid()
            }
            .asListenableFuture()

    override fun onGetSearchResult(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        query: String,
        page: Int,
        pageSize: Int,
        params: LibraryParams?
    ) =
        waitScope
            .async {
                musicMediaItemBrowser.getSearchResult(query, page, pageSize)?.let {
                    LibraryResult.ofItemList(it, params)
                }
                    ?: LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
            }
            .asListenableFuture()

    // --- BUTTON MANAGEMENT ---

    override fun onPauseOnRepeatChanged() {
        super.onPauseOnRepeatChanged()
        updateCustomButtons()
    }

    override fun onProgressionChanged(progression: Progression) {
        super.onProgressionChanged(progression)
        if (progression.isPlaying) {
            inPlayback = true
        }
    }

    override fun onRepeatModeChanged(repeatMode: RepeatMode) {
        super.onRepeatModeChanged(repeatMode)
        updateCustomButtons()
    }

    override fun onQueueReordered(queue: List<Song>, index: Int, isShuffled: Boolean) {
        super.onQueueReordered(queue, index, isShuffled)
        updateCustomButtons()
    }

    override fun onNotificationActionChanged() {
        super.onNotificationActionChanged()
        updateCustomButtons()
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
                            if (playbackManager.isShuffled) R.drawable.ic_shuffle_on_24
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

    private fun endSession() {
        // This session has ended, so we need to reset this flag for when the next
        // session starts.
        exoHolder.save {
            // User could feasibly start playing again if they were fast enough, so
            // we need to avoid stopping the foreground state if that's the case.
            if (playbackManager.progression.isPlaying) {
                playbackManager.playing(false)
            }
            inPlayback = false
            updateForeground(forMusic = false)
        }
    }

    companion object {
        const val WAKELOCK_TIMEOUT_MS = 60 * 1000L
        const val REINDEX_DELAY_MS = 500L
        const val ACTION_INC_REPEAT_MODE = BuildConfig.APPLICATION_ID + ".action.LOOP"
        const val ACTION_INVERT_SHUFFLE = BuildConfig.APPLICATION_ID + ".action.SHUFFLE"
        const val ACTION_SKIP_PREV = BuildConfig.APPLICATION_ID + ".action.PREV"
        const val ACTION_PLAY_PAUSE = BuildConfig.APPLICATION_ID + ".action.PLAY_PAUSE"
        const val ACTION_SKIP_NEXT = BuildConfig.APPLICATION_ID + ".action.NEXT"
        const val ACTION_EXIT = BuildConfig.APPLICATION_ID + ".action.EXIT"
    }
}
