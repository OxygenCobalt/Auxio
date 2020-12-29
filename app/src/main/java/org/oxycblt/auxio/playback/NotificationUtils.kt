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
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.MainActivity
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.coil.getBitmap
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.playback.state.PlaybackStateManager

object NotificationUtils {
    const val CHANNEL_ID = "CHANNEL_AUXIO_PLAYBACK"
    const val NOTIFICATION_ID = 0xA0A0
    const val REQUEST_CODE = 0xA0C0

    // The build type is applied to each action so that broadcasts will not conflict with debug/release builds.
    const val ACTION_LOOP = "ACTION_AUXIO_LOOP_" + BuildConfig.BUILD_TYPE
    const val ACTION_SHUFFLE = "ACTION_AUXIO_SHUFFLE_" + BuildConfig.BUILD_TYPE
    const val ACTION_SKIP_PREV = "ACTION_AUXIO_SKIP_PREV_" + BuildConfig.BUILD_TYPE
    const val ACTION_PLAY_PAUSE = "ACTION_AUXIO_PLAY_PAUSE_" + BuildConfig.BUILD_TYPE
    const val ACTION_SKIP_NEXT = "ACTION_AUXIO_SKIP_NEXT_" + BuildConfig.BUILD_TYPE
    const val ACTION_EXIT = "ACTION_AUXIO_EXIT_" + BuildConfig.BUILD_TYPE
}

/**
 * Create the standard media notification used by Auxio.
 * @param context [Context] required to create the notification
 * @param mediaSession [MediaSessionCompat] required for the [MediaStyle] notification
 * @author OxygenCobalt
 */
fun NotificationManager.createMediaNotification(
    context: Context,
    mediaSession: MediaSessionCompat
): NotificationCompat.Builder {
    // Create a notification channel if requireds
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

/**
 * Set the current metadata of a media notification.
 * @param context The [Context] needed to load the cover bitmap
 * @param song The [Song] that the notification should reflect
 * @param colorize Whether to load the album art and colorize the notification based off it
 * @param onDone A callback for when the process is finished
 * @author OxygenCobalt
 */
fun NotificationCompat.Builder.setMetadata(
    context: Context,
    song: Song,
    colorize: Boolean,
    onDone: () -> Unit
) {
    setContentTitle(song.name)
    setContentText(
        song.album.artist.name,
    )

    // On older versions of android [API <24], show the song's album on the subtext instead of
    // the current mode, as that makes more sense for the old style of media notifications.
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        setSubText(song.album.name)
    }

    if (colorize) {
        // getBitmap() is concurrent, so only call back to the object calling this function when
        // the loading is over.
        getBitmap(context, song) {
            setLargeIcon(it)

            onDone()
        }
    } else {
        setLargeIcon(null)

        onDone()
    }
}

/**
 * Update the playing button on the media notification.
 * @param context The context required to refresh the action
 */
@SuppressLint("RestrictedApi")
fun NotificationCompat.Builder.updatePlaying(context: Context) {
    mActions[2] = newAction(NotificationUtils.ACTION_PLAY_PAUSE, context)
}

/**
 * Update the extra action on the media notification [E.G the Loop/Shuffle button]
 * @param context The context required to refresh the action
 * @param useAltAction Whether to use the shuffle action or not, true if yes, false if no
 */
@SuppressLint("RestrictedApi")
fun NotificationCompat.Builder.updateExtraAction(context: Context, useAltAction: Boolean) {
    mActions[0] = if (useAltAction) {
        newAction(NotificationUtils.ACTION_SHUFFLE, context)
    } else {
        newAction(NotificationUtils.ACTION_LOOP, context)
    }
}

/**
 * Update the subtext of the media notification to reflect the current mode.
 * @param context The context required to get the strings required to show certain modes
 */
fun NotificationCompat.Builder.updateMode(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val playbackManager = PlaybackStateManager.getInstance()

        // If playing from all songs, set the subtext as that, otherwise the currently played parent.
        if (playbackManager.mode == PlaybackMode.ALL_SONGS) {
            setSubText(context.getString(R.string.label_all_songs))
        } else {
            setSubText(playbackManager.parent!!.name)
        }
    }
}

/**
 * Create a new [NotificationCompat.Action].
 * @param action The action that the notification action should represent
 * @param context The [Context] needed to create the action
 */
private fun newAction(action: String, context: Context): NotificationCompat.Action {
    val playbackManager = PlaybackStateManager.getInstance()

    // Get the icon depending on the action & current state.
    val drawable = when (action) {
        NotificationUtils.ACTION_LOOP -> {
            when (playbackManager.loopMode) {
                LoopMode.NONE -> R.drawable.ic_loop_inactive
                LoopMode.ONCE -> R.drawable.ic_loop_one
                LoopMode.INFINITE -> R.drawable.ic_loop
            }
        }

        NotificationUtils.ACTION_SHUFFLE -> {
            if (playbackManager.isShuffling) {
                R.drawable.ic_shuffle
            } else {
                R.drawable.ic_shuffle_inactive
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
        drawable, action,
        PendingIntent.getBroadcast(
            context, NotificationUtils.REQUEST_CODE,
            Intent(action), PendingIntent.FLAG_UPDATE_CURRENT
        )
    ).build()
}
