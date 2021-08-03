package org.oxycblt.auxio.playback.system

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import org.oxycblt.auxio.R
import org.oxycblt.auxio.coil.loadBitmap
import org.oxycblt.auxio.music.Parent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.ui.newBroadcastIntent
import org.oxycblt.auxio.ui.newMainIntent

/**
 * The unified notification for [PlaybackService]. This is not self-sufficient, updates have
 * to be delivered manually.
 * @author OxygenCobalt
 */
@SuppressLint("RestrictedApi")
class PlaybackNotification private constructor(
    context: Context,
    mediaToken: MediaSessionCompat.Token
) : NotificationCompat.Builder(context, CHANNEL_ID), PlaybackStateManager.Callback {
    init {
        setSmallIcon(R.drawable.ic_song)
        setCategory(NotificationCompat.CATEGORY_SERVICE)
        setShowWhen(false)
        setSilent(true)
        setContentIntent(context.newMainIntent())
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        addAction(buildLoopAction(context, LoopMode.NONE))
        addAction(buildAction(context, PlaybackService.ACTION_SKIP_PREV, R.drawable.ic_skip_prev))
        addAction(buildPlayPauseAction(context, true))
        addAction(buildAction(context, PlaybackService.ACTION_SKIP_NEXT, R.drawable.ic_skip_next))
        addAction(buildAction(context, PlaybackService.ACTION_EXIT, R.drawable.ic_exit))

        setStyle(
            MediaStyle()
                .setMediaSession(mediaToken)
                .setShowActionsInCompactView(1, 2, 3)
        )
    }

    // --- STATE FUNCTIONS ---

    /**
     * Set the metadata of the notification using [song].
     * @param colorize Whether to show the album art of [song] on the notification
     * @param onDone What to do when the loading of the album art is finished
     */
    fun setMetadata(context: Context, song: Song, colorize: Boolean, onDone: () -> Unit) {
        setContentTitle(song.name)
        setContentText(song.album.artist.name)

        // On older versions of android [API <24], show the song's album on the subtext instead of
        // the current mode, as that makes more sense for the old style of media notifications.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            setSubText(song.album.name)
        }

        if (colorize) {
            // loadBitmap() is concurrent, so only call back to the object calling this function when
            // the loading is over.
            loadBitmap(context, song) { bitmap ->
                setLargeIcon(bitmap)
                onDone()
            }
        } else {
            setLargeIcon(null)
            onDone()
        }
    }

    /**
     * Set the playing icon on the notification
     */
    fun setPlaying(context: Context, isPlaying: Boolean) {
        mActions[2] = buildPlayPauseAction(context, isPlaying)
    }

    /**
     * Update the first action to reflect the [loopMode] given.
     */
    fun setLoop(context: Context, loopMode: LoopMode) {
        mActions[0] = buildLoopAction(context, loopMode)
    }

    /**
     * Update the first action to reflect whether the queue is shuffled or not
     */
    fun setShuffle(context: Context, isShuffling: Boolean) {
        mActions[0] = buildShuffleAction(context, isShuffling)
    }

    /**
     * Apply the current [parent] to the header of the notification.
     */
    fun setParent(context: Context, parent: Parent?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return

        // A blank parent always means that the mode is ALL_SONGS
        setSubText(parent?.displayName ?: context.getString(R.string.label_all_songs))
    }

    // --- NOTIFICATION ACTION BUILDERS ---

    private fun buildPlayPauseAction(
        context: Context,
        isPlaying: Boolean
    ): NotificationCompat.Action {
        val drawableRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play

        return buildAction(context, PlaybackService.ACTION_PLAY_PAUSE, drawableRes)
    }

    private fun buildLoopAction(
        context: Context,
        loopMode: LoopMode
    ): NotificationCompat.Action {
        val drawableRes = when (loopMode) {
            LoopMode.NONE -> R.drawable.ic_loop_inactive
            LoopMode.ALL -> R.drawable.ic_loop
            LoopMode.TRACK -> R.drawable.ic_loop_one
        }

        return buildAction(context, PlaybackService.ACTION_LOOP, drawableRes)
    }

    private fun buildShuffleAction(
        context: Context,
        isShuffled: Boolean
    ): NotificationCompat.Action {
        val drawableRes = if (isShuffled) R.drawable.ic_shuffle else R.drawable.ic_shuffle_inactive

        return buildAction(context, PlaybackService.ACTION_SHUFFLE, drawableRes)
    }

    private fun buildAction(
        context: Context,
        actionName: String,
        @DrawableRes iconRes: Int
    ): NotificationCompat.Action {
        val action = NotificationCompat.Action.Builder(
            iconRes, actionName,
            context.newBroadcastIntent(actionName)
        )

        return action.build()
    }

    companion object {
        const val CHANNEL_ID = "CHANNEL_AUXIO_PLAYBACK"
        const val NOTIFICATION_ID = 0xA0A0

        /**
         * Build a new instance of [PlaybackNotification].
         */
        fun from(
            context: Context,
            notificationManager: NotificationManager,
            mediaSession: MediaSessionCompat
        ): PlaybackNotification {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID, context.getString(R.string.info_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                )

                notificationManager.createNotificationChannel(channel)
            }

            return PlaybackNotification(context, mediaSession.sessionToken)
        }
    }
}
