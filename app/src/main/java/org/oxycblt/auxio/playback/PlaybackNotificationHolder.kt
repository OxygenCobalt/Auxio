package org.oxycblt.auxio.playback

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.coil.getBitmap
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.playback.state.PlaybackStateManager

// Holder for the playback notification, should only be used by PlaybackService.
// TODO: You really need to rewrite this class. Christ.
// TODO: Disable skip prev/next buttons when you cant do those actions
// TODO: Implement a way to exit the notification
class PlaybackNotificationHolder {
    private lateinit var mNotification: Notification

    private lateinit var notificationManager: NotificationManager
    private lateinit var baseNotification: NotificationCompat.Builder

    private val playbackManager = PlaybackStateManager.getInstance()

    private var isForeground = false

    fun init(context: Context, session: MediaSessionCompat, playbackService: PlaybackService) {
        // Never run if the notification has already been created
        if (!::mNotification.isInitialized) {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Create a notification channel if required
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.label_notification_playback),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }

            baseNotification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_song)
                .setStyle(
                    MediaStyle()
                        .setMediaSession(session.sessionToken)
                        .setShowActionsInCompactView(1, 2, 3)
                )
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setChannelId(CHANNEL_ID)
                .setShowWhen(false)
                .setTicker(playbackService.getString(R.string.title_playback))
                .addAction(createAction(ACTION_LOOP, playbackService))
                .addAction(createAction(ACTION_SKIP_PREV, playbackService))
                .addAction(createAction(ACTION_PLAY_PAUSE, playbackService))
                .addAction(createAction(ACTION_SKIP_NEXT, playbackService))
                .addAction(createAction(ACTION_SHUFFLE, playbackService))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            mNotification = baseNotification.build()
        }
    }

    fun setMetadata(song: Song, playbackService: PlaybackService) {
        // Set the basic metadata since MediaStyle wont do it yourself.
        // Fun Fact: The documentation still says that MediaStyle will handle metadata changes
        // from MediaSession, even though it doesn't. Its been 6 years. Fun.
        baseNotification
            .setContentTitle(song.name)
            .setContentText(
                playbackService.getString(
                    R.string.format_info,
                    song.album.artist.name,
                    song.album.name
                )
            )

        getBitmap(song, playbackService) {
            baseNotification.setLargeIcon(it)

            startForegroundOrNotify(playbackService)
        }
    }

    @SuppressLint("RestrictedApi")
    fun updatePlaying(playbackService: PlaybackService) {
        baseNotification.mActions[2] = createAction(ACTION_PLAY_PAUSE, playbackService)

        Log.d(this::class.simpleName, baseNotification.mActions[1].iconCompat?.resId.toString())

        startForegroundOrNotify(playbackService)
    }

    @SuppressLint("RestrictedApi")
    fun updateLoop(playbackService: PlaybackService) {
        baseNotification.mActions[0] = createAction(ACTION_LOOP, playbackService)

        startForegroundOrNotify(playbackService)
    }

    @SuppressLint("RestrictedApi")
    fun updateShuffle(playbackService: PlaybackService) {
        baseNotification.mActions[4] = createAction(ACTION_SHUFFLE, playbackService)

        startForegroundOrNotify(playbackService)
    }

    fun stop(playbackService: PlaybackService) {
        playbackService.stopForeground(true)
        notificationManager.cancel(NOTIFICATION_ID)

        isForeground = false
    }

    private fun createAction(action: String, playbackService: PlaybackService): NotificationCompat.Action {
        val drawable = when (action) {
            ACTION_LOOP -> {
                when (playbackManager.loopMode) {
                    LoopMode.NONE -> R.drawable.ic_loop_disabled
                    LoopMode.ONCE -> R.drawable.ic_loop_one
                    LoopMode.INFINITE -> R.drawable.ic_loop
                }
            }

            ACTION_SKIP_PREV -> {
                R.drawable.ic_skip_prev
            }

            ACTION_PLAY_PAUSE -> {
                if (playbackManager.isPlaying) {
                    R.drawable.ic_pause
                } else {
                    R.drawable.ic_play
                }
            }

            ACTION_SKIP_NEXT -> {
                R.drawable.ic_skip_next
            }

            ACTION_SHUFFLE -> {
                if (playbackManager.isShuffling) {
                    R.drawable.ic_shuffle
                } else {
                    R.drawable.ic_shuffle_disabled
                }
            }

            else -> R.drawable.ic_play
        }

        return NotificationCompat.Action.Builder(
            drawable, action, createPlaybackAction(action, playbackService)
        ).build()
    }

    private fun createPlaybackAction(action: String, playbackService: PlaybackService): PendingIntent {
        val intent = Intent()
        intent.action = action

        return PendingIntent.getBroadcast(
            playbackService,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun startForegroundOrNotify(playbackService: PlaybackService) {
        mNotification = baseNotification.build()

        // Start the service in the foreground if haven't already.
        if (!isForeground) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                playbackService.startForeground(
                    NOTIFICATION_ID, mNotification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )
            } else {
                playbackService.startForeground(NOTIFICATION_ID, mNotification)
            }
        } else {
            notificationManager.notify(NOTIFICATION_ID, mNotification)
        }
    }

    companion object {
        const val CHANNEL_ID = "CHANNEL_AUXIO_PLAYBACK"
        const val NOTIFICATION_ID = 0xA0A0
        const val REQUEST_CODE = 0xA0C0

        const val ACTION_LOOP = "ACTION_AUXIO_LOOP"
        const val ACTION_SKIP_PREV = "ACTION_AUXIO_SKIP_PREV"
        const val ACTION_PLAY_PAUSE = "ACTION_AUXIO_PLAY_PAUSE"
        const val ACTION_SKIP_NEXT = "ACTION_AUXIO_SKIP_NEXT"
        const val ACTION_SHUFFLE = "ACTION_AUXIO_SHUFFLE"
    }
}
