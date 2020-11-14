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
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.playback.state.PlaybackStateManager

object NotificationUtils {
    val DO_COMPAT_SUBTEXT = Build.VERSION.SDK_INT < Build.VERSION_CODES.O

    const val CHANNEL_ID = "CHANNEL_AUXIO_PLAYBACK"
    const val NOTIFICATION_ID = 0xA0A0
    const val REQUEST_CODE = 0xA0C0

    const val ACTION_LOOP = "ACTION_AUXIO_LOOP"
    const val ACTION_SKIP_PREV = "ACTION_AUXIO_SKIP_PREV"
    const val ACTION_PLAY_PAUSE = "ACTION_AUXIO_PLAY_PAUSE"
    const val ACTION_SKIP_NEXT = "ACTION_AUXIO_SKIP_NEXT"
    const val ACTION_EXIT = "ACTION_AUXIO_EXIT"
}

fun NotificationManager.createMediaNotification(
    context: Context,
    mediaSession: MediaSessionCompat
): NotificationCompat.Builder {
    // Create a notification channel if required
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            NotificationUtils.CHANNEL_ID,
            context.getString(R.string.label_channel),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        createNotificationChannel(channel)
    }

    val mainIntent = PendingIntent.getActivity(
        context, NotificationUtils.REQUEST_CODE,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // TODO: Things that probably aren't possible but would be nice
    //  - Playing intent takes you to PlaybackFragment instead of MainFragment
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
        .addAction(newAction(NotificationUtils.ACTION_LOOP, context))
        .addAction(newAction(NotificationUtils.ACTION_SKIP_PREV, context))
        .addAction(newAction(NotificationUtils.ACTION_PLAY_PAUSE, context))
        .addAction(newAction(NotificationUtils.ACTION_SKIP_NEXT, context))
        .addAction(newAction(NotificationUtils.ACTION_EXIT, context))
        .setNotificationSilent()
        .setContentIntent(mainIntent)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
}

fun NotificationCompat.Builder.setMetadata(song: Song, context: Context, onDone: () -> Unit) {
    setContentTitle(song.name)
    setContentText(
        song.album.artist.name,
    )

    // On older versions of android [API <26], show the song's album on the subtext instead of
    // the current mode, as that makes more sense for the old style of media notifications.
    if (NotificationUtils.DO_COMPAT_SUBTEXT) {
        setSubText(song.album.name)
    }

    // getBitmap() is concurrent, so only call back to the object calling this function when
    // the loading is over.
    getBitmap(song, context) {
        setLargeIcon(it)

        onDone()
    }
}

// I have no idea how to update actions on the fly so I have to use these restricted APIs.
@SuppressLint("RestrictedApi")
fun NotificationCompat.Builder.updatePlaying(context: Context) {
    mActions[2] = newAction(NotificationUtils.ACTION_PLAY_PAUSE, context)
}

@SuppressLint("RestrictedApi")
fun NotificationCompat.Builder.updateLoop(context: Context) {
    mActions[0] = newAction(NotificationUtils.ACTION_LOOP, context)
}

fun NotificationCompat.Builder.updateMode(context: Context) {
    if (!NotificationUtils.DO_COMPAT_SUBTEXT) {
        val playbackManager = PlaybackStateManager.getInstance()

        // If playing from all songs, set the subtext as that, otherwise the currently played parent.
        if (playbackManager.mode == PlaybackMode.ALL_SONGS) {
            setSubText(context.getString(R.string.title_all_songs))
        } else {
            setSubText(playbackManager.parent!!.name)
        }
    }
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

        NotificationUtils.ACTION_SKIP_PREV -> R.drawable.ic_skip_prev

        NotificationUtils.ACTION_PLAY_PAUSE -> {
            if (playbackManager.isPlaying) {
                R.drawable.ic_pause
            } else {
                R.drawable.ic_play
            }
        }

        NotificationUtils.ACTION_SKIP_NEXT -> R.drawable.ic_skip_next

        NotificationUtils.ACTION_EXIT -> R.drawable.ic_exit

        else -> R.drawable.ic_error
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
