/*
 * Copyright (c) 2024 Auxio Project
 * MediaSessionServiceFragment.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.service

import android.app.Notification
import android.content.Context
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultActionFactory
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaNotification.ActionFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.guava.asListenableFuture
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.ForegroundListener
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.service.MediaItemBrowser
import org.oxycblt.auxio.playback.state.DeferredPlayback
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.newMainPendingIntent

class MediaSessionServiceFragment
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val playbackManager: PlaybackStateManager,
    private val actionHandler: PlaybackActionHandler,
    private val mediaItemBrowser: MediaItemBrowser,
    exoHolderFactory: ExoPlaybackStateHolder.Factory
) :
    MediaLibrarySession.Callback,
    PlaybackActionHandler.Callback,
    MediaItemBrowser.Invalidator,
    PlaybackStateManager.Listener {
    private val waitJob = Job()
    private val waitScope = CoroutineScope(waitJob + Dispatchers.Default)
    private val exoHolder = exoHolderFactory.create()

    private lateinit var actionFactory: ActionFactory
    private val mediaNotificationProvider =
        DefaultMediaNotificationProvider.Builder(context)
            .setNotificationId(IntegerTable.PLAYBACK_NOTIFICATION_CODE)
            .setChannelId(BuildConfig.APPLICATION_ID + ".channel.PLAYBACK")
            .setChannelName(R.string.lbl_playback)
            .setPlayDrawableResourceId(R.drawable.ic_play_24)
            .setPauseDrawableResourceId(R.drawable.ic_pause_24)
            .setSkipNextDrawableResourceId(R.drawable.ic_skip_next_24)
            .setSkipPrevDrawableResourceId(R.drawable.ic_skip_prev_24)
            .setContentIntent(context.newMainPendingIntent())
            .build()
            .also { it.setSmallIcon(R.drawable.ic_auxio_24) }
    private var foregroundListener: ForegroundListener? = null

    lateinit var mediaSession: MediaLibrarySession
        private set

    // --- MEDIASESSION CALLBACKS ---

    fun attach(service: MediaLibraryService, listener: ForegroundListener): MediaLibrarySession {
        foregroundListener = listener
        mediaSession = createSession(service)
        service.addSession(mediaSession)
        actionFactory = DefaultActionFactory(service)
        playbackManager.addListener(this)
        exoHolder.attach()
        actionHandler.attach(this)
        mediaItemBrowser.attach(this)
        return mediaSession
    }

    fun handleTaskRemoved() {
        if (!playbackManager.progression.isPlaying) {
            playbackManager.endSession()
        }
    }

    fun handleNonNativeStart() {
        // At minimum we want to ensure an active playback state.
        // TODO: Possibly also force to go foreground?
        logD("Handling non-native start.")
        playbackManager.playDeferred(DeferredPlayback.RestoreState)
    }

    fun hasNotification(): Boolean = exoHolder.sessionOngoing

    fun createNotification(post: (MediaNotification) -> Unit) {
        val notification =
            mediaNotificationProvider.createNotification(
                mediaSession, mediaSession.customLayout, actionFactory) { notification ->
                    post(wrapMediaNotification(notification))
                }
        post(wrapMediaNotification(notification))
    }

    fun release() {
        waitJob.cancel()
        mediaItemBrowser.release()
        actionHandler.release()
        exoHolder.release()
        playbackManager.removeListener(this)
        mediaSession.release()
        foregroundListener = null
    }

    private fun wrapMediaNotification(notification: MediaNotification): MediaNotification {
        // Pulled from MediaNotificationManager: Need to specify MediaSession token manually
        // in notification
        val fwkToken =
            mediaSession.sessionCompatToken.token as android.media.session.MediaSession.Token
        notification.notification.extras.putParcelable(Notification.EXTRA_MEDIA_SESSION, fwkToken)
        return notification
    }

    private fun createSession(service: MediaLibraryService) =
        MediaLibrarySession.Builder(service, exoHolder.mediaSessionPlayer, this).build()

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): ConnectionResult {
        val sessionCommands =
            actionHandler.withCommands(ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS)
        return ConnectionResult.AcceptedResultBuilder(session)
            .setAvailableSessionCommands(sessionCommands)
            .setCustomLayout(actionHandler.createCustomLayout())
            .build()
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> =
        if (actionHandler.handleCommand(customCommand)) {
            Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        } else {
            super.onCustomCommand(session, controller, customCommand, args)
        }

    override fun onGetLibraryRoot(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<MediaItem>> =
        Futures.immediateFuture(LibraryResult.ofItem(mediaItemBrowser.root, params))

    override fun onGetItem(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String
    ): ListenableFuture<LibraryResult<MediaItem>> {
        val result =
            mediaItemBrowser.getItem(mediaId)?.let { LibraryResult.ofItem(it, null) }
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
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
        val children =
            mediaItemBrowser.getChildren(parentId, page, pageSize)?.let {
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
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<Void>> =
        waitScope
            .async {
                mediaItemBrowser.prepareSearch(query, browser)
                // Invalidator will send the notify result
                LibraryResult.ofVoid()
            }
            .asListenableFuture()

    override fun onGetSearchResult(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        query: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ) =
        waitScope
            .async {
                mediaItemBrowser.getSearchResult(query, page, pageSize)?.let {
                    LibraryResult.ofItemList(it, params)
                }
                    ?: LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
            }
            .asListenableFuture()

    override fun onSessionEnded() {
        foregroundListener?.updateForeground(ForegroundListener.Change.MEDIA_SESSION)
    }

    override fun onCustomLayoutChanged(layout: List<CommandButton>) {
        mediaSession.setCustomLayout(layout)
    }

    override fun invalidate(ids: Map<String, Int>) {
        for (id in ids) {
            mediaSession.notifyChildrenChanged(id.key, id.value, null)
        }
    }

    override fun invalidate(
        controller: MediaSession.ControllerInfo,
        query: String,
        itemCount: Int
    ) {
        mediaSession.notifySearchResultChanged(controller, query, itemCount, null)
    }
}
