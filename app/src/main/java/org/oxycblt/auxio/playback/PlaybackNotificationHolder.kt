package org.oxycblt.auxio.playback

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.coil.getBitmap

// Manager for the playback notification
// TODO: Implement some ability
internal class PlaybackNotificationHolder {
    private lateinit var mNotification: Notification

    private lateinit var notificationManager: NotificationManager
    private lateinit var baseNotification: NotificationCompat.Builder

    private var isForeground = false

    fun init(context: Context, session: MediaSessionCompat) {
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
                .setStyle(MediaStyle().setMediaSession(session.sessionToken))
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setChannelId(CHANNEL_ID)
                .setShowWhen(false)
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
            mNotification = baseNotification.build()

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
    }

    fun stop(playbackService: PlaybackService) {
        playbackService.stopForeground(true)
        notificationManager.cancel(NOTIFICATION_ID)

        isForeground = false
    }

    companion object {
        const val CHANNEL_ID = "CHANNEL_AUXIO_PLAYBACK"
        const val NOTIFICATION_ID = 0xA0A0
    }
}
