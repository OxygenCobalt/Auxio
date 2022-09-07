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
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.session.MediaButtonReceiver
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.image.BitmapProvider
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.InternalPlayer
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.logD

/**
 * The component managing the [MediaSessionCompat] instance, alongside the [NotificationComponent]
 *
 * MediaSession is easily one of the most poorly thought out APIs in Android. It tries to hard to be
 * hElpfUl and implement so many fundamental behaviors into a one-size-fits-all package that it only
 * ends up causing bugs and frustration. The queue system is horribly designed, the playback state
 * system has unending coherency issues, and the overstretched abstractions result in god-awful
 * performance bottlenecks and insane state bugs.
 *
 * The sheer absurdity of the hoops we have jump through to get this working in an okay manner is
 * the reason why Auxio only mirrors a saner playback state to the media session instead of relying
 * on it. I thought that Android 13 would at least try to make the state more coherent, but NOPE.
 * You still have to do a delicate dance of posting notifications and updating the session state
 * while also keeping in mind the absurd rate limiting system in place just to have a sort-of
 * coherent state. And even then it will break if you skip too much.
 *
 * @author OxygenCobalt
 */
class MediaSessionComponent(private val context: Context, private val callback: Callback) :
    MediaSessionCompat.Callback(), PlaybackStateManager.Callback, Settings.Callback {
    interface Callback {
        fun onPostNotification(notification: NotificationComponent?, reason: PostingReason)
    }

    enum class PostingReason {
        METADATA,
        ACTIONS
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
        playbackManager.addCallback(this)
        mediaSession.setCallback(this)
    }

    fun handleMediaButtonIntent(intent: Intent) {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
    }

    fun release() {
        provider.release()
        settings.release()
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
            callback.onPostNotification(null, PostingReason.METADATA)
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
                .putText(
                    MediaMetadataCompat.METADATA_KEY_GENRE,
                    song.genres.joinToString { it.resolveName(context) })
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
                    callback.onPostNotification(notification, PostingReason.METADATA)
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
                        .setMediaId(song.uid.toString())
                        .setTitle(song.resolveName(context))
                        .setSubtitle(song.resolveIndividualArtistName(context))
                        .setIconUri(song.album.coverUri)
                        .setMediaUri(song.uri)
                        .build()

                MediaSessionCompat.QueueItem(description, i.toLong())
            }

        mediaSession.setQueue(queueItems)
    }

    override fun onStateChanged(state: InternalPlayer.State) {
        invalidateSessionState()
        notification.updatePlaying(playbackManager.playerState.isPlaying)
        if (!provider.isBusy) {
            callback.onPostNotification(notification, PostingReason.ACTIONS)
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

    override fun onShuffledChanged(isShuffled: Boolean) {
        mediaSession.setShuffleMode(
            if (isShuffled) {
                PlaybackStateCompat.SHUFFLE_MODE_ALL
            } else {
                PlaybackStateCompat.SHUFFLE_MODE_NONE
            })

        invalidateSecondaryAction()
    }

    // --- SETTINGSMANAGER CALLBACKS ---

    override fun onSettingChanged(key: String) {
        when (key) {
            context.getString(R.string.set_key_show_covers),
            context.getString(R.string.set_key_quality_covers) ->
                updateMediaMetadata(playbackManager.song, playbackManager.parent)
            context.getString(R.string.set_key_alt_notif_action) -> invalidateSecondaryAction()
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
        playbackManager.changePlaying(true)
    }

    override fun onPause() {
        playbackManager.changePlaying(false)
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
        playbackManager.changePlaying(true)
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

    // --- MISC ---

    private fun invalidateSessionState() {
        logD("Updating media session playback state")

        // Note: Due to metadata updates being delayed but playback remaining ongoing, the position
        // will be wonky until we can upload a duration. Again, this ties back to how I must
        // aggressively batch notification updates to prevent rate-limiting.
        val state =
            PlaybackStateCompat.Builder()
                .setActions(ACTIONS)
                .setActiveQueueItemId(playbackManager.index.toLong())

        playbackManager.playerState.intoPlaybackState(state)

        // Android 13+ leverages custom actions in the notification.

        val extraAction =
            if (settings.useAltNotifAction) {
                PlaybackStateCompat.CustomAction.Builder(
                    PlaybackService.ACTION_INVERT_SHUFFLE,
                    context.getString(R.string.desc_shuffle),
                    if (playbackManager.isShuffled) {
                        R.drawable.ic_shuffle_on_24
                    } else {
                        R.drawable.ic_shuffle_off_24
                    })
            } else {
                PlaybackStateCompat.CustomAction.Builder(
                    PlaybackService.ACTION_INC_REPEAT_MODE,
                    context.getString(R.string.desc_change_repeat),
                    playbackManager.repeatMode.icon)
            }

        val exitAction =
            PlaybackStateCompat.CustomAction.Builder(
                    PlaybackService.ACTION_EXIT,
                    context.getString(R.string.desc_exit),
                    R.drawable.ic_close_24)
                .build()

        state.addCustomAction(extraAction.build())
        state.addCustomAction(exitAction)

        mediaSession.setPlaybackState(state.build())
    }

    private fun invalidateSecondaryAction() {
        invalidateSessionState()

        if (settings.useAltNotifAction) {
            notification.updateShuffled(playbackManager.isShuffled)
        } else {
            notification.updateRepeatMode(playbackManager.repeatMode)
        }

        if (!provider.isBusy) {
            callback.onPostNotification(notification, PostingReason.ACTIONS)
        }
    }

    companion object {
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
