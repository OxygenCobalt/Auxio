/*
 * Copyright (c) 2021 Auxio Project
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
import android.os.SystemClock
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.session.MediaButtonReceiver
import com.google.android.exoplayer2.Player
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.image.BitmapProvider
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.logD

/**
 * The component managing the [MediaSessionCompat] instance.
 *
 * Media3 is a joke. It tries so hard to be "hElpfUl" and implement so many fundamental behaviors
 * into a one-size-fits-all package that it only ends up causing unending bugs and frustration. The
 * queue system is horribly designed, the notification code is outdated, and the overstretched
 * abstractions result in terrible performance bottlenecks and insane state bugs..
 *
 * Show me a way to adapt my internal queue into the new system and I will change my mind, but
 * otherwise, I will stick with my normal system that works correctly.
 *
 * @author OxygenCobalt
 *
 * TODO: Remove the player callback once smooth seeking is implemented
 */
class MediaSessionComponent(
    private val context: Context,
    private val player: Player,
    private val callback: Callback
) :
    Player.Listener,
    MediaSessionCompat.Callback(),
    PlaybackStateManager.Callback,
    Settings.Callback {
    interface Callback {
        fun onPostNotification(notification: NotificationComponent?, reason: String)
    }

    private val mediaSession =
        MediaSessionCompat(context, context.packageName).apply {
            isActive = true
            setQueueTitle(context.getString(R.string.lbl_queue))
        }

    private val playbackManager = PlaybackStateManager.getInstance()
    private val settings = Settings(context, this)

    private val notification = NotificationComponent(context, mediaSession.sessionToken)
    private val provider = BitmapProvider(context)

    init {
        player.addListener(this)
        playbackManager.addCallback(this)
        mediaSession.setCallback(this)
    }

    fun handleMediaButtonIntent(intent: Intent) {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
    }

    fun release() {
        provider.release()
        settings.release()
        player.removeListener(this)
        playbackManager.removeCallback(this)

        mediaSession.apply {
            isActive = false
            release()
        }
    }

    // --- PLAYBACKSTATEMANAGER CALLBACKS ---

    override fun onIndexMoved(index: Int) {
        updateMediaMetadata(playbackManager.song, playbackManager.parent)
        invalidateSessionState()
    }

    override fun onQueueChanged(queue: List<Song>) {
        updateQueue(queue)
    }

    override fun onQueueReworked(index: Int, queue: List<Song>) {
        updateQueue(queue)
        invalidateSessionState()
    }

    override fun onNewPlayback(index: Int, queue: List<Song>, parent: MusicParent?) {
        updateMediaMetadata(playbackManager.song, parent)
        updateQueue(queue)
        invalidateSessionState()
    }

    private fun updateMediaMetadata(song: Song?, parent: MusicParent?) {
        if (song == null) {
            mediaSession.setMetadata(emptyMetadata)
            callback.onPostNotification(null, "song update")
            return
        }

        // Note: We would leave the artist field null if it didn't exist and let downstream
        // consumers handle it, but that would break the notification display.
        val title = song.resolveName(context)
        val artist = song.resolveIndividualArtistName(context)
        val builder =
            MediaMetadataCompat.Builder()
                .putText(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putText(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
                .putText(MediaMetadataCompat.METADATA_KEY_ALBUM, song.album.resolveName(context))
                .putText(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putText(
                    MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST,
                    song.album.artist.resolveName(context))
                .putText(MediaMetadataCompat.METADATA_KEY_AUTHOR, artist)
                .putText(MediaMetadataCompat.METADATA_KEY_COMPOSER, artist)
                .putText(MediaMetadataCompat.METADATA_KEY_WRITER, artist)
                .putText(MediaMetadataCompat.METADATA_KEY_GENRE, song.genre.resolveName(context))
                .putText(
                    METADATA_KEY_PARENT,
                    parent?.resolveName(context) ?: context.getString(R.string.lbl_all_songs))
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.durationMs)

        song.track?.let {
            builder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, it.toLong())
        }

        song.disc?.let {
            builder.putLong(MediaMetadataCompat.METADATA_KEY_DISC_NUMBER, it.toLong())
        }

        song.album.date?.let {
            builder.putString(MediaMetadataCompat.METADATA_KEY_DATE, it.toString())
        }

        // Cover loading is a mess. Android expects you to provide a clean, easy URI for it to
        // leverage, but Auxio cannot do that as quality-of-life features like scaling or
        // 1:1 cropping could not be used.
        //
        // Thus, we have two options to handle album art:
        // 1. Load the bitmap, then post the notification
        // 2. Post the notification with text metadata, then post it with the bitmap when it's
        // loaded.
        //
        // Neither of these are good, but 1 is the only one that will work on all versions
        // without the notification being eaten by rate-limiting.
        provider.load(
            song,
            object : BitmapProvider.Target {
                override fun onCompleted(bitmap: Bitmap?) {
                    builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, bitmap)
                    builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
                    val metadata = builder.build()
                    mediaSession.setMetadata(metadata)
                    notification.updateMetadata(metadata)
                    callback.onPostNotification(notification, "song update")
                }
            })
    }

    private fun updateQueue(queue: List<Song>) {
        val queueItems =
            queue.mapIndexed { i, song ->
                // Since we usually have to load many songs into the queue, use the MediaStore URI
                // instead of loading a bitmap.
                val description =
                    MediaDescriptionCompat.Builder()
                        .setMediaId("Song:${song.id}")
                        .setTitle(song.resolveName(context))
                        .setSubtitle(song.resolveIndividualArtistName(context))
                        .setIconUri(song.album.coverUri)
                        .setMediaUri(song.uri)
                        .build()

                MediaSessionCompat.QueueItem(description, i.toLong())
            }

        mediaSession.setQueue(queueItems)
    }

    override fun onPlayingChanged(isPlaying: Boolean) {
        invalidateSessionState()
        invalidateNotificationActions()
    }

    override fun onRepeatChanged(repeatMode: RepeatMode) {
        mediaSession.setRepeatMode(
            when (repeatMode) {
                RepeatMode.NONE -> PlaybackStateCompat.REPEAT_MODE_NONE
                RepeatMode.TRACK -> PlaybackStateCompat.REPEAT_MODE_ONE
                RepeatMode.ALL -> PlaybackStateCompat.REPEAT_MODE_ALL
            })

        invalidateNotificationActions()
    }

    override fun onShuffledChanged(isShuffled: Boolean) {
        mediaSession.setShuffleMode(
            if (isShuffled) {
                PlaybackStateCompat.SHUFFLE_MODE_ALL
            } else {
                PlaybackStateCompat.SHUFFLE_MODE_NONE
            })

        invalidateNotificationActions()
    }

    // --- SETTINGSMANAGER CALLBACKS ---

    override fun onSettingChanged(key: String) {
        when (key) {
            context.getString(R.string.set_key_show_covers),
            context.getString(R.string.set_key_quality_covers) ->
                updateMediaMetadata(playbackManager.song, playbackManager.parent)
            context.getString(R.string.set_key_alt_notif_action) -> invalidateNotificationActions()
        }
    }

    // --- EXOPLAYER CALLBACKS ---

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        invalidateSessionState()

        if (!playbackManager.isPlaying) {
            // Hack around issue where the position won't update after a seek when paused.
            // Apparently this can be fixed by re-posting the notification, but not always
            // when we invalidate the state (that will cause us to be rate-limited), and also not
            // always when we seek (that will also cause us to be rate-limited). Someone looked at
            // this system and said it was well-designed.
            callback.onPostNotification(notification, "position discontinuity")
        }
    }

    // --- MEDIASESSION CALLBACKS ---

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
        // STUB: Unimplemented
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
        playbackManager.isPlaying = true
    }

    override fun onPause() {
        playbackManager.isPlaying = false
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
        playbackManager.isPlaying = true
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
        playbackManager.reshuffle(
            shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL ||
                shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_GROUP,
            settings)
    }

    override fun onSkipToQueueItem(id: Long) {
        if (id in playbackManager.queue.indices) {
            playbackManager.goto(id.toInt())
        }
    }

    override fun onStop() {
        // Get the service to shut down with the ACTION_EXIT intent
        context.sendBroadcast(Intent(PlaybackService.ACTION_EXIT))
    }

    // --- MISC ---

    private fun invalidateSessionState() {
        logD("Updating media session playback state")

        // Note: Due to metadata updates being delayed but playback remaining ongoing, the position
        // will be wonky until we can upload a duration. Again, this ties back to how I must
        // aggressively batch notification updates to prevent rate-limiting.
        // Android 13 seems to resolve these, but I'm still stuck with these issues below that
        // version.
        // TODO: Add the custom actions for Android 13
        val state =
            PlaybackStateCompat.Builder()
                .setActions(ACTIONS)
                .setBufferedPosition(player.bufferedPosition)
                .setActiveQueueItemId(playbackManager.index.toLong())

        val playerState =
            if (playbackManager.isPlaying) {
                PlaybackStateCompat.STATE_PLAYING
            } else {
                PlaybackStateCompat.STATE_PAUSED
            }

        state.setState(playerState, player.currentPosition, 1.0f, SystemClock.elapsedRealtime())

        mediaSession.setPlaybackState(state.build())
    }

    private fun invalidateNotificationActions() {
        notification.updatePlaying(playbackManager.isPlaying)

        if (settings.useAltNotifAction) {
            notification.updateShuffled(playbackManager.isShuffled)
        } else {
            notification.updateRepeatMode(playbackManager.repeatMode)
        }

        if (!provider.isBusy) {
            callback.onPostNotification(notification, "new notification actions")
        }
    }

    companion object {
        private val emptyMetadata = MediaMetadataCompat.Builder().build()

        const val METADATA_KEY_PARENT = BuildConfig.APPLICATION_ID + ".metadata.PARENT"

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
