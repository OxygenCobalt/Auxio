/*
 * Copyright (c) 2021 Auxio Project
 * MediaSessionComponent.kt is part of Auxio.
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

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.session.MediaButtonReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.image.BitmapProvider
import org.oxycblt.auxio.image.ImageSettings
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.playback.ActionMode
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.playback.queue.Queue
import org.oxycblt.auxio.playback.state.InternalPlayer
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.util.logD

/**
 * A component that mirrors the current playback state into the [MediaSessionCompat] and
 * [NotificationComponent].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class MediaSessionComponent
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val bitmapProvider: BitmapProvider,
    private val playbackManager: PlaybackStateManager,
    private val playbackSettings: PlaybackSettings,
) :
    MediaSessionCompat.Callback(),
    PlaybackStateManager.Listener,
    ImageSettings.Listener,
    PlaybackSettings.Listener {
    private val mediaSession =
        MediaSessionCompat(context, context.packageName).apply {
            isActive = true
            setQueueTitle(context.getString(R.string.lbl_queue))
        }

    private val notification = NotificationComponent(context, mediaSession.sessionToken)

    private var listener: Listener? = null

    init {
        playbackManager.addListener(this)
        playbackSettings.registerListener(this)
        mediaSession.setCallback(this)
    }

    /**
     * Forward a system media button [Intent] to the [MediaSessionCompat].
     *
     * @param intent The [Intent.ACTION_MEDIA_BUTTON] [Intent] to forward.
     */
    fun handleMediaButtonIntent(intent: Intent) {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
    }

    /**
     * Register a [Listener] for notification updates to this service.
     *
     * @param listener The [Listener] to register.
     */
    fun registerListener(listener: Listener) {
        this.listener = listener
    }

    /**
     * Release this instance, closing the [MediaSessionCompat] and preventing any further updates to
     * the [NotificationComponent].
     */
    fun release() {
        listener = null
        bitmapProvider.release()
        playbackSettings.unregisterListener(this)
        playbackManager.removeListener(this)
        mediaSession.apply {
            isActive = false
            release()
        }
    }

    // --- PLAYBACKSTATEMANAGER OVERRIDES ---

    override fun onIndexMoved(queue: Queue) {
        updateMediaMetadata(queue.currentSong, playbackManager.parent)
        invalidateSessionState()
    }

    override fun onQueueChanged(queue: Queue, change: Queue.Change) {
        updateQueue(queue)
        when (change.type) {
            // Nothing special to do with mapping changes.
            Queue.Change.Type.MAPPING -> {}
            // Index changed, ensure playback state's index changes.
            Queue.Change.Type.INDEX -> invalidateSessionState()
            // Song changed, ensure metadata changes.
            Queue.Change.Type.SONG -> updateMediaMetadata(queue.currentSong, playbackManager.parent)
        }
    }

    override fun onQueueReordered(queue: Queue) {
        updateQueue(queue)
        invalidateSessionState()
        mediaSession.setShuffleMode(
            if (queue.isShuffled) {
                PlaybackStateCompat.SHUFFLE_MODE_ALL
            } else {
                PlaybackStateCompat.SHUFFLE_MODE_NONE
            })
        invalidateSecondaryAction()
    }

    override fun onNewPlayback(queue: Queue, parent: MusicParent?) {
        updateMediaMetadata(queue.currentSong, parent)
        updateQueue(queue)
        invalidateSessionState()
    }

    override fun onStateChanged(state: InternalPlayer.State) {
        invalidateSessionState()
        notification.updatePlaying(playbackManager.playerState.isPlaying)
        if (!bitmapProvider.isBusy) {
            listener?.onPostNotification(notification)
        }
    }

    override fun onRepeatChanged(repeatMode: RepeatMode) {
        mediaSession.setRepeatMode(
            when (repeatMode) {
                RepeatMode.NONE -> PlaybackStateCompat.REPEAT_MODE_NONE
                RepeatMode.TRACK -> PlaybackStateCompat.REPEAT_MODE_ONE
                RepeatMode.ALL -> PlaybackStateCompat.REPEAT_MODE_ALL
            })

        invalidateSecondaryAction()
    }

    // --- SETTINGS OVERRIDES ---

    override fun onCoverModeChanged() {
        // Need to reload the metadata cover.
        updateMediaMetadata(playbackManager.queue.currentSong, playbackManager.parent)
    }

    override fun onNotificationActionChanged() {
        // Need to re-load the action shown in the notification.
        invalidateSecondaryAction()
    }

    // --- MEDIASESSION OVERRIDES ---

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        super.onPlayFromMediaId(mediaId, extras)
        // STUB: Unimplemented, no media browser
    }

    override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
        super.onPlayFromUri(uri, extras)
        // STUB: Unimplemented, no media browser
    }

    override fun onPlayFromSearch(query: String?, extras: Bundle?) {
        super.onPlayFromSearch(query, extras)
        // STUB: Unimplemented, no media browser
    }

    override fun onAddQueueItem(description: MediaDescriptionCompat?) {
        super.onAddQueueItem(description)
        // STUB: Unimplemented
    }

    override fun onRemoveQueueItem(description: MediaDescriptionCompat?) {
        super.onRemoveQueueItem(description)
        // STUB: Unimplemented
    }

    override fun onPlay() {
        playbackManager.setPlaying(true)
    }

    override fun onPause() {
        playbackManager.setPlaying(false)
    }

    override fun onSkipToNext() {
        playbackManager.next()
    }

    override fun onSkipToPrevious() {
        playbackManager.prev()
    }

    override fun onSeekTo(position: Long) {
        playbackManager.seekTo(position)
    }

    override fun onFastForward() {
        playbackManager.next()
    }

    override fun onRewind() {
        playbackManager.rewind()
        playbackManager.setPlaying(true)
    }

    override fun onSetRepeatMode(repeatMode: Int) {
        playbackManager.repeatMode =
            when (repeatMode) {
                PlaybackStateCompat.REPEAT_MODE_ALL -> RepeatMode.ALL
                PlaybackStateCompat.REPEAT_MODE_GROUP -> RepeatMode.ALL
                PlaybackStateCompat.REPEAT_MODE_ONE -> RepeatMode.TRACK
                else -> RepeatMode.NONE
            }
    }

    override fun onSetShuffleMode(shuffleMode: Int) {
        playbackManager.reorder(
            shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL ||
                shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_GROUP)
    }

    override fun onSkipToQueueItem(id: Long) {
        playbackManager.goto(id.toInt())
    }

    override fun onCustomAction(action: String?, extras: Bundle?) {
        super.onCustomAction(action, extras)

        // Service already handles intents from the old notification actions, easier to
        // plug into that system.
        context.sendBroadcast(Intent(action))
    }

    override fun onStop() {
        // Get the service to shut down with the ACTION_EXIT intent
        context.sendBroadcast(Intent(PlaybackService.ACTION_EXIT))
    }

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
        if (song == null) {
            // Nothing playing, reset the MediaSession and close the notification.
            mediaSession.setMetadata(emptyMetadata)
            return
        }

        // Populate MediaMetadataCompat. For efficiency, cache some fields that are re-used
        // several times.
        val title = song.resolveName(context)
        val artist = song.artists.resolveNames(context)
        val builder =
            MediaMetadataCompat.Builder()
                .putText(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putText(MediaMetadataCompat.METADATA_KEY_ALBUM, song.album.resolveName(context))
                // Note: We would leave the artist field null if it didn't exist and let downstream
                // consumers handle it, but that would break the notification display.
                .putText(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putText(
                    MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST,
                    song.album.artists.resolveNames(context))
                .putText(MediaMetadataCompat.METADATA_KEY_AUTHOR, artist)
                .putText(MediaMetadataCompat.METADATA_KEY_COMPOSER, artist)
                .putText(MediaMetadataCompat.METADATA_KEY_WRITER, artist)
                .putText(
                    METADATA_KEY_PARENT,
                    parent?.resolveName(context) ?: context.getString(R.string.lbl_all_songs))
                .putText(MediaMetadataCompat.METADATA_KEY_GENRE, song.genres.resolveNames(context))
                .putText(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
                .putText(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, artist)
                .putText(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION,
                    parent?.resolveName(context) ?: context.getString(R.string.lbl_all_songs))
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.durationMs)
        // These fields are nullable and so we must check first before adding them to the fields.
        song.track?.let {
            builder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, it.toLong())
        }
        song.disc?.let {
            builder.putLong(MediaMetadataCompat.METADATA_KEY_DISC_NUMBER, it.number.toLong())
        }
        song.date?.let { builder.putString(MediaMetadataCompat.METADATA_KEY_DATE, it.toString()) }

        // We are normally supposed to use URIs for album art, but that removes some of the
        // nice things we can do like square cropping or high quality covers. Instead,
        // we load a full-size bitmap into the media session and take the performance hit.
        bitmapProvider.load(
            song,
            object : BitmapProvider.Target {
                override fun onCompleted(bitmap: Bitmap?) {
                    builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, bitmap)
                    builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
                    val metadata = builder.build()
                    mediaSession.setMetadata(metadata)
                    notification.updateMetadata(metadata)
                    listener?.onPostNotification(notification)
                }
            })
    }

    /**
     * Upload a new queue to the [MediaSessionCompat].
     *
     * @param queue The current queue to upload.
     */
    private fun updateQueue(queue: Queue) {
        val queueItems =
            queue.resolve().mapIndexed { i, song ->
                val description =
                    MediaDescriptionCompat.Builder()
                        // Media ID should not be the item index but rather the UID,
                        // as it's used to request a song to be played from the queue.
                        .setMediaId(song.uid.toString())
                        .setTitle(song.resolveName(context))
                        .setSubtitle(song.artists.resolveNames(context))
                        // Since we usually have to load many songs into the queue, use the
                        // MediaStore URI instead of loading a bitmap.
                        .setIconUri(song.album.coverUri)
                        .setMediaUri(song.uri)
                        .build()
                // Store the item index so we can then use the analogous index in the
                // playback state.
                MediaSessionCompat.QueueItem(description, i.toLong())
            }
        mediaSession.setQueue(queueItems)
    }

    /** Invalidate the current [MediaSessionCompat]'s [PlaybackStateCompat]. */
    private fun invalidateSessionState() {
        logD("Updating media session playback state")

        val state =
            // InternalPlayer.State handles position/state information.
            playbackManager.playerState
                .intoPlaybackState(PlaybackStateCompat.Builder())
                .setActions(ACTIONS)
                // Active queue ID corresponds to the indices we populated prior, use them here.
                .setActiveQueueItemId(playbackManager.queue.index.toLong())

        // Android 13+ relies on custom actions in the notification.

        // Add the secondary action (either repeat/shuffle depending on the configuration)
        val secondaryAction =
            when (playbackSettings.notificationAction) {
                ActionMode.SHUFFLE ->
                    PlaybackStateCompat.CustomAction.Builder(
                        PlaybackService.ACTION_INVERT_SHUFFLE,
                        context.getString(R.string.desc_shuffle),
                        if (playbackManager.queue.isShuffled) {
                            R.drawable.ic_shuffle_on_24
                        } else {
                            R.drawable.ic_shuffle_off_24
                        })
                else ->
                    PlaybackStateCompat.CustomAction.Builder(
                        PlaybackService.ACTION_INC_REPEAT_MODE,
                        context.getString(R.string.desc_change_repeat),
                        playbackManager.repeatMode.icon)
            }
        state.addCustomAction(secondaryAction.build())

        // Add the exit action so the service can be closed
        val exitAction =
            PlaybackStateCompat.CustomAction.Builder(
                    PlaybackService.ACTION_EXIT,
                    context.getString(R.string.desc_exit),
                    R.drawable.ic_close_24)
                .build()
        state.addCustomAction(exitAction)

        mediaSession.setPlaybackState(state.build())
    }

    /** Invalidate the "secondary" action (i.e shuffle/repeat mode). */
    private fun invalidateSecondaryAction() {
        invalidateSessionState()

        when (playbackSettings.notificationAction) {
            ActionMode.SHUFFLE -> notification.updateShuffled(playbackManager.queue.isShuffled)
            else -> notification.updateRepeatMode(playbackManager.repeatMode)
        }

        if (!bitmapProvider.isBusy) {
            listener?.onPostNotification(notification)
        }
    }

    /** An interface for handling changes in the notification configuration. */
    interface Listener {
        /**
         * Called when the [NotificationComponent] changes, requiring it to be re-posed.
         *
         * @param notification The new [NotificationComponent].
         */
        fun onPostNotification(notification: NotificationComponent)
    }

    companion object {
        /**
         * An extended metadata key that stores the resolved name of the [MusicParent] that is
         * currently being played from.
         */
        const val METADATA_KEY_PARENT = BuildConfig.APPLICATION_ID + ".metadata.PARENT"
        private val emptyMetadata = MediaMetadataCompat.Builder().build()
        private const val ACTIONS =
            PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_SET_REPEAT_MODE or
                PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM or
                PlaybackStateCompat.ACTION_SEEK_TO or
                PlaybackStateCompat.ACTION_STOP
    }
}
