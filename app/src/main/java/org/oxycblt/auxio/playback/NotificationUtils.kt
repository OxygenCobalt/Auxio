package org.oxycblt.auxio.playback

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import org.oxycblt.auxio.MainActivity
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.coil.getBitmap
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.playback.state.PlaybackStateManager

object NotificationUtils {
    const val CHANNEL_ID = "CHANNEL_AUXIO_PLAYBACK"
    const val NOTIFICATION_ID = 0xA0A0
    const val REQUEST_CODE = 0xA0C0

    const val ACTION_LOOP = "ACTION_AUXIO_LOOP"
    const val ACTION_SKIP_PREV = "ACTION_AUXIO_SKIP_PREV"
    const val ACTION_PLAY_PAUSE = "ACTION_AUXIO_PLAY_PAUSE"
    const val ACTION_SKIP_NEXT = "ACTION_AUXIO_SKIP_NEXT"
    const val ACTION_SHUFFLE = "ACTION_AUXIO_SHUFFLE"
}

fun NotificationManager.createMediaNotification(
    context: Context,
    mediaSession: MediaSessionCompat
): NotificationCompat.Builder {

    // Create a notification channel if required
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            NotificationUtils.CHANNEL_ID,
            context.getString(R.string.label_notification_playback),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        createNotificationChannel(channel)
    }

    val mainIntent = PendingIntent.getActivity(
        context, NotificationUtils.REQUEST_CODE,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // TODO: It would be cool if the notification intent took you to the now playing screen.
    return NotificationCompat.Builder(context, NotificationUtils.CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_song)
        .setStyle(
            MediaStyle()
                .setMediaSession(mediaSession.sessionToken)
                .setShowActionsInCompactView(1, 2, 3)
        )
        .setCategory(NotificationCompat.CATEGORY_SERVICE)
        .setChannelId(NotificationUtils.CHANNEL_ID)
        .setShowWhen(false)
        .setTicker(context.getString(R.string.title_playback))
        .addAction(newAction(NotificationUtils.ACTION_LOOP, context))
        .addAction(newAction(NotificationUtils.ACTION_SKIP_PREV, context))
        .addAction(newAction(NotificationUtils.ACTION_PLAY_PAUSE, context))
        .addAction(newAction(NotificationUtils.ACTION_SKIP_NEXT, context))
        .addAction(newAction(NotificationUtils.ACTION_SHUFFLE, context))
        .setSubText(context.getString(R.string.title_playback))
        .setContentIntent(mainIntent)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
}

fun NotificationCompat.Builder.setMetadata(song: Song, context: Context, onDone: () -> Unit) {
    setContentTitle(song.name)
    setContentText(
        song.album.artist.name,
    )

    getBitmap(song, context) {
        setLargeIcon(it)

        onDone()
    }
}

@SuppressLint("RestrictedApi")
fun NotificationCompat.Builder.updateLoop(context: Context) {
    mActions[0] = newAction(NotificationUtils.ACTION_LOOP, context)
}

@SuppressLint("RestrictedApi")
fun NotificationCompat.Builder.updatePlaying(context: Context) {
    mActions[2] = newAction(NotificationUtils.ACTION_PLAY_PAUSE, context)
}

@SuppressLint("RestrictedApi")
fun NotificationCompat.Builder.updateShuffle(context: Context) {
    mActions[4] = newAction(NotificationUtils.ACTION_SHUFFLE, context)
}

private fun newAction(action: String, context: Context): NotificationCompat.Action {
    val playbackManager = PlaybackStateManager.getInstance()

    val drawable = when (action) {
        NotificationUtils.ACTION_LOOP -> {
            when (playbackManager.loopMode) {
                LoopMode.NONE -> R.drawable.ic_loop_disabled
                LoopMode.ONCE -> R.drawable.ic_loop_one
                LoopMode.INFINITE -> R.drawable.ic_loop
            }
        }

        NotificationUtils.ACTION_SKIP_PREV -> {
            R.drawable.ic_skip_prev
        }

        NotificationUtils.ACTION_PLAY_PAUSE -> {
            if (playbackManager.isPlaying) {
                R.drawable.ic_pause
            } else {
                R.drawable.ic_play
            }
        }

        NotificationUtils.ACTION_SKIP_NEXT -> {
            R.drawable.ic_skip_next
        }

        NotificationUtils.ACTION_SHUFFLE -> {
            if (playbackManager.isShuffling) {
                R.drawable.ic_shuffle
            } else {
                R.drawable.ic_shuffle_disabled
            }
        }

        else -> R.drawable.ic_play
    }

    return NotificationCompat.Action.Builder(
        drawable, action, newPlaybackIntent(action, context)
    ).build()
}

private fun newPlaybackIntent(action: String, context: Context): PendingIntent {
    return PendingIntent.getBroadcast(
        context, NotificationUtils.REQUEST_CODE, Intent(action), PendingIntent.FLAG_UPDATE_CURRENT
    )
}
