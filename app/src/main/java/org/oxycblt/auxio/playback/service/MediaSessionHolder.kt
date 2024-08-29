/*
 * Copyright (c) 2021 Auxio Project
 * MediaSessionHolder.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.system

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.DrawableRes
import androidx.car.app.mediaextensions.MetadataExtras
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import javax.inject.Inject
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.ForegroundListener
import org.oxycblt.auxio.ForegroundServiceNotification
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.image.BitmapProvider
import org.oxycblt.auxio.image.ImageSettings
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.music.service.MediaSessionUID
import org.oxycblt.auxio.music.service.toMediaDescription
import org.oxycblt.auxio.music.service.toMediaItem
import org.oxycblt.auxio.playback.ActionMode
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.playback.service.MediaSessionInterface
import org.oxycblt.auxio.playback.service.PlaybackActions
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.Progression
import org.oxycblt.auxio.playback.state.QueueChange
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.newBroadcastPendingIntent
import org.oxycblt.auxio.util.newMainPendingIntent

/**
 * A component that mirrors the current playback state into the [MediaSessionCompat] and
 * [NotificationComponent].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class MediaSessionHolder
private constructor(
    private val context: Context,
    private val sessionInterface: MediaSessionInterface,
    private val playbackManager: PlaybackStateManager,
    private val playbackSettings: PlaybackSettings,
    private val bitmapProvider: BitmapProvider,
    private val imageSettings: ImageSettings
) :
    MediaSessionCompat.Callback(),
    PlaybackStateManager.Listener,
    ImageSettings.Listener,
    PlaybackSettings.Listener {

    class Factory
    @Inject
    constructor(
        private val sessionInterface: MediaSessionInterface,
        private val playbackManager: PlaybackStateManager,
        private val playbackSettings: PlaybackSettings,
        private val bitmapProvider: BitmapProvider,
        private val imageSettings: ImageSettings,
    ) {
        fun create(context: Context) =
            MediaSessionHolder(
                context,
                sessionInterface,
                playbackManager,
                playbackSettings,
                bitmapProvider,
                imageSettings)
    }

    private val mediaSession = MediaSessionCompat(context, context.packageName)
    val token: MediaSessionCompat.Token
        get() = mediaSession.sessionToken

    private val _notification = PlaybackNotification(context, mediaSession.sessionToken)
    val notification: ForegroundServiceNotification
        get() = _notification

    private var foregroundListener: ForegroundListener? = null

    fun attach(foregroundListener: ForegroundListener) {
        mediaSession.apply {
            isActive = true
            setQueueTitle(context.getString(R.string.lbl_queue))
            setCallback(sessionInterface)
            setFlags(MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS)
        }
        this.foregroundListener = foregroundListener
        playbackManager.addListener(this)
        playbackSettings.registerListener(this)
        imageSettings.registerListener(this)
        mediaSession.setCallback(this)
    }

    /**
     * Release this instance, closing the [MediaSessionCompat] and preventing any further updates to
     * the [NotificationComponent].
     */
    fun release() {
        foregroundListener = null
        bitmapProvider.release()
        playbackSettings.unregisterListener(this)
        imageSettings.unregisterListener(this)
        playbackManager.removeListener(this)
        mediaSession.apply {
            isActive = false
            release()
        }
    }

    // --- PLAYBACKSTATEMANAGER OVERRIDES ---

    override fun onIndexMoved(index: Int) {
        updateMediaMetadata(playbackManager.currentSong, playbackManager.parent)
        invalidateSessionState()
    }

    override fun onQueueChanged(queue: List<Song>, index: Int, change: QueueChange) {
        updateQueue(queue)
        when (change.type) {
            // Nothing special to do with mapping changes.
            QueueChange.Type.MAPPING -> {}
            // Index changed, ensure playback state's index changes.
            QueueChange.Type.INDEX -> invalidateSessionState()
            // Song changed, ensure metadata changes.
            QueueChange.Type.SONG ->
                updateMediaMetadata(playbackManager.currentSong, playbackManager.parent)
        }
    }

    override fun onQueueReordered(queue: List<Song>, index: Int, isShuffled: Boolean) {
        updateQueue(queue)
        invalidateSessionState()
        mediaSession.setShuffleMode(
            if (isShuffled) {
                PlaybackStateCompat.SHUFFLE_MODE_ALL
            } else {
                PlaybackStateCompat.SHUFFLE_MODE_NONE
            })
        invalidateSecondaryAction()
    }

    override fun onNewPlayback(
        parent: MusicParent?,
        queue: List<Song>,
        index: Int,
        isShuffled: Boolean
    ) {
        updateMediaMetadata(playbackManager.currentSong, parent)
        updateQueue(queue)
        invalidateSessionState()
    }

    override fun onProgressionChanged(progression: Progression) {
        invalidateSessionState()
        _notification.updatePlaying(playbackManager.progression.isPlaying)
        if (!bitmapProvider.isBusy) {
            foregroundListener?.updateForeground(ForegroundListener.Change.MEDIA_SESSION)
        }
    }

    override fun onRepeatModeChanged(repeatMode: RepeatMode) {
        mediaSession.setRepeatMode(
            when (repeatMode) {
                RepeatMode.NONE -> PlaybackStateCompat.REPEAT_MODE_NONE
                RepeatMode.TRACK -> PlaybackStateCompat.REPEAT_MODE_ONE
                RepeatMode.ALL -> PlaybackStateCompat.REPEAT_MODE_ALL
            })

        invalidateSecondaryAction()
    }

    // --- SETTINGS OVERRIDES ---

    override fun onImageSettingsChanged() {
        // Need to reload the metadata cover.
        updateMediaMetadata(playbackManager.currentSong, playbackManager.parent)
    }

    override fun onNotificationActionChanged() {
        // Need to re-load the action shown in the notification.
        invalidateSecondaryAction()
    }

    // --- MEDIASESSION OVERRIDES ---

    // --- INTERNAL ---

    /**
     * Upload a new [MediaMetadataCompat] based on the current playback state to the
     * [MediaSessionCompat] and [NotificationComponent].
     *
     * @param song The current [Song] to create the [MediaMetadataCompat] from, or null if no [Song]
     *   is currently playing.
     * @param parent The current [MusicParent] to create the [MediaMetadataCompat] from, or null if
     *   playback is currently occuring from all songs.
     */
    private fun updateMediaMetadata(song: Song?, parent: MusicParent?) {
        logD("Updating media metadata to $song with $parent")
        if (song == null) {
            // Nothing playing, reset the MediaSession and close the notification.
            logD("Nothing playing, resetting media session")
            mediaSession.setMetadata(emptyMetadata)
            return
        }

        // Populate MediaMetadataCompat. For efficiency, cache some fields that are re-used
        // several times.
        val title = song.name.resolve(context)
        val artist = song.artists.resolveNames(context)
        val album = song.album.name.resolve(context)
        val builder =
            MediaMetadataCompat.Builder()
                .putText(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putText(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                // Note: We would leave the artist field null if it didn't exist and let downstream
                // consumers handle it, but that would break the notification display.
                .putText(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putText(
                    MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST,
                    song.album.artists.resolveNames(context))
                .putText(MediaMetadataCompat.METADATA_KEY_AUTHOR, artist)
                .putText(MediaMetadataCompat.METADATA_KEY_COMPOSER, artist)
                .putText(MediaMetadataCompat.METADATA_KEY_WRITER, artist)
                .putText(MediaMetadataCompat.METADATA_KEY_GENRE, song.genres.resolveNames(context))
                .putText(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
                .putText(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, artist)
                .putText(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, album)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.durationMs)
                .putText(
                    PlaybackNotification.KEY_PARENT,
                    parent?.name?.resolve(context) ?: context.getString(R.string.lbl_all_songs))
                .putText(
                    MetadataExtras.KEY_SUBTITLE_LINK_MEDIA_ID,
                    MediaSessionUID.SingleItem(song.artists[0].uid).toString())
                .putText(
                    MetadataExtras.KEY_DESCRIPTION_LINK_MEDIA_ID,
                    MediaSessionUID.SingleItem(song.album.uid).toString())
        // These fields are nullable and so we must check first before adding them to the fields.
        song.track?.let {
            logD("Adding track information")
            builder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, it.toLong())
        }
        song.disc?.let {
            logD("Adding disc information")
            builder.putLong(MediaMetadataCompat.METADATA_KEY_DISC_NUMBER, it.number.toLong())
        }
        song.date?.let {
            logD("Adding date information")
            builder.putString(MediaMetadataCompat.METADATA_KEY_DATE, it.toString())
            builder.putString(MediaMetadataCompat.METADATA_KEY_YEAR, it.year.toString())
        }

        // We are normally supposed to use URIs for album art, but that removes some of the
        // nice things we can do like square cropping or high quality covers. Instead,
        // we load a full-size bitmap into the media session and take the performance hit.
        bitmapProvider.load(
            song,
            object : BitmapProvider.Target {
                override fun onCompleted(bitmap: Bitmap?) {
                    logD("Bitmap loaded, applying media session and posting notification")
                    if (bitmap != null) {
                        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, bitmap)
                        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
                    }
                    val metadata = builder.build()
                    mediaSession.setMetadata(metadata)
                    _notification.updateMetadata(metadata)
                    foregroundListener?.updateForeground(ForegroundListener.Change.MEDIA_SESSION)
                }
            })
    }

    /**
     * Upload a new queue to the [MediaSessionCompat].
     *
     * @param queue The current queue to upload.
     */
    private fun updateQueue(queue: List<Song>) {
        val queueItems =
            queue.mapIndexed { i, song ->
                val description = song.toMediaDescription(context, null, { putInt(MediaSessionInterface.KEY_QUEUE_POS, i) })
                // Store the item index so we can then use the analogous index in the
                // playback state.
                MediaSessionCompat.QueueItem(description, i.toLong())
            }
        logD("Uploading ${queueItems.size} songs to MediaSession queue")
        mediaSession.setQueue(queueItems)
    }

    /** Invalidate the current [MediaSessionCompat]'s [PlaybackStateCompat]. */
    private fun invalidateSessionState() {
        logD("Updating media session playback state")

        val state =
            // InternalPlayer.State handles position/state information.
            playbackManager.progression
                .intoPlaybackState(PlaybackStateCompat.Builder())
                .setActions(MediaSessionInterface.ACTIONS)
                // Active queue ID corresponds to the indices we populated prior, use them here.
                .setActiveQueueItemId(playbackManager.index.toLong())

        // Android 13+ relies on custom actions in the notification.

        // Add the secondary action (either repeat/shuffle depending on the configuration)
        val secondaryAction =
            when (playbackSettings.notificationAction) {
                ActionMode.SHUFFLE -> {
                    logD("Using shuffle MediaSession action")
                    PlaybackStateCompat.CustomAction.Builder(
                        PlaybackActions.ACTION_INVERT_SHUFFLE,
                        context.getString(R.string.desc_shuffle),
                        if (playbackManager.isShuffled) {
                            R.drawable.ic_shuffle_on_24
                        } else {
                            R.drawable.ic_shuffle_off_24
                        })
                }
                else -> {
                    logD("Using repeat mode MediaSession action")
                    PlaybackStateCompat.CustomAction.Builder(
                        PlaybackActions.ACTION_INC_REPEAT_MODE,
                        context.getString(R.string.desc_change_repeat),
                        playbackManager.repeatMode.icon)
                }
            }
        state.addCustomAction(secondaryAction.build())

        // Add the exit action so the service can be closed
        val exitAction =
            PlaybackStateCompat.CustomAction.Builder(
                    PlaybackActions.ACTION_EXIT,
                    context.getString(R.string.desc_exit),
                    R.drawable.ic_close_24)
                .build()
        state.addCustomAction(exitAction)

        mediaSession.setPlaybackState(state.build())
    }

    /** Invalidate the "secondary" action (i.e shuffle/repeat mode). */
    private fun invalidateSecondaryAction() {
        logD("Invalidating secondary action")
        invalidateSessionState()

        when (playbackSettings.notificationAction) {
            ActionMode.SHUFFLE -> {
                logD("Using shuffle notification action")
                _notification.updateShuffled(playbackManager.isShuffled)
            }
            else -> {
                logD("Using repeat mode notification action")
                _notification.updateRepeatMode(playbackManager.repeatMode)
            }
        }

        if (!bitmapProvider.isBusy) {
            logD("Not loading a bitmap, post the notification")
            foregroundListener?.updateForeground(ForegroundListener.Change.MEDIA_SESSION)
        }
    }

    companion object {
        private val emptyMetadata = MediaMetadataCompat.Builder().build()
    }
}

/**
 * The playback notification component. Due to race conditions regarding notification updates, this
 * component is not self-sufficient. [MediaSessionHolder] should be used instead of manage it.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@SuppressLint("RestrictedApi")
private class PlaybackNotification(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token
) : ForegroundServiceNotification(context, CHANNEL_INFO) {
    init {
        setSmallIcon(R.drawable.ic_auxio_24)
        setCategory(NotificationCompat.CATEGORY_TRANSPORT)
        setShowWhen(false)
        setSilent(true)
        setContentIntent(context.newMainPendingIntent())
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        addAction(buildRepeatAction(context, RepeatMode.NONE))
        addAction(
            buildAction(context, PlaybackActions.ACTION_SKIP_PREV, R.drawable.ic_skip_prev_24))
        addAction(buildPlayPauseAction(context, true))
        addAction(
            buildAction(context, PlaybackActions.ACTION_SKIP_NEXT, R.drawable.ic_skip_next_24))
        addAction(buildAction(context, PlaybackActions.ACTION_EXIT, R.drawable.ic_close_24))

        setStyle(
            MediaStyle(this).setMediaSession(sessionToken).setShowActionsInCompactView(1, 2, 3))
    }

    override val code: Int
        get() = IntegerTable.PLAYBACK_NOTIFICATION_CODE

    // --- STATE FUNCTIONS ---

    /**
     * Update the currently shown metadata in this notification.
     *
     * @param metadata The [MediaMetadataCompat] to display in this notification.
     */
    fun updateMetadata(metadata: MediaMetadataCompat) {
        logD("Updating shown metadata")
        setLargeIcon(metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART))
        setContentTitle(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
        setContentText(metadata.getText(MediaMetadataCompat.METADATA_KEY_ARTIST))
        setSubText(metadata.getText(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION))
    }

    /**
     * Update the playing state shown in this notification.
     *
     * @param isPlaying Whether playback should be indicated as ongoing or paused.
     */
    fun updatePlaying(isPlaying: Boolean) {
        logD("Updating playing state: $isPlaying")
        mActions[2] = buildPlayPauseAction(context, isPlaying)
    }

    /**
     * Update the secondary action in this notification to show the current [RepeatMode].
     *
     * @param repeatMode The current [RepeatMode].
     */
    fun updateRepeatMode(repeatMode: RepeatMode) {
        logD("Applying repeat mode action: $repeatMode")
        mActions[0] = buildRepeatAction(context, repeatMode)
    }

    /**
     * Update the secondary action in this notification to show the current shuffle state.
     *
     * @param isShuffled Whether the queue is currently shuffled or not.
     */
    fun updateShuffled(isShuffled: Boolean) {
        logD("Applying shuffle action: $isShuffled")
        mActions[0] = buildShuffleAction(context, isShuffled)
    }

    // --- NOTIFICATION ACTION BUILDERS ---

    private fun buildPlayPauseAction(
        context: Context,
        isPlaying: Boolean
    ): NotificationCompat.Action {
        val drawableRes =
            if (isPlaying) {
                R.drawable.ic_pause_24
            } else {
                R.drawable.ic_play_24
            }
        return buildAction(context, PlaybackActions.ACTION_PLAY_PAUSE, drawableRes)
    }

    private fun buildRepeatAction(
        context: Context,
        repeatMode: RepeatMode
    ): NotificationCompat.Action {
        return buildAction(context, PlaybackActions.ACTION_INC_REPEAT_MODE, repeatMode.icon)
    }

    private fun buildShuffleAction(
        context: Context,
        isShuffled: Boolean
    ): NotificationCompat.Action {
        val drawableRes =
            if (isShuffled) {
                R.drawable.ic_shuffle_on_24
            } else {
                R.drawable.ic_shuffle_off_24
            }
        return buildAction(context, PlaybackActions.ACTION_INVERT_SHUFFLE, drawableRes)
    }

    private fun buildAction(context: Context, actionName: String, @DrawableRes iconRes: Int) =
        NotificationCompat.Action.Builder(
                iconRes, actionName, context.newBroadcastPendingIntent(actionName))
            .build()

    companion object {
        const val KEY_PARENT = BuildConfig.APPLICATION_ID + ".metadata.PARENT"

        /** Notification channel used by solely the playback notification. */
        private val CHANNEL_INFO =
            ChannelInfo(
                id = BuildConfig.APPLICATION_ID + ".channel.PLAYBACK",
                nameRes = R.string.lbl_playback)
    }
}
