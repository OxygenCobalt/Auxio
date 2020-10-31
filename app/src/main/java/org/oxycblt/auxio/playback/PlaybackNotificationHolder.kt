package org.oxycblt.auxio.playback

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.coil.getBitmap

internal class PlaybackNotificationHolder {
    private lateinit var mNotification: Notification

    private lateinit var notificationManager: NotificationManager
    private lateinit var baseNotification: NotificationCompat.Builder

    fun init(context: Context, session: MediaSessionCompat) {
        // Never run if the notification has already been created
        if (!::mNotification.isInitialized) {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Create a notification channel if required
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.label_notif_playback),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }

            baseNotification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_song)
                .setStyle(MediaStyle().setMediaSession(session.sessionToken))
                .setChannelId(CHANNEL_ID)
                .setShowWhen(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            mNotification = baseNotification.build()
        }
    }

    fun setMetadata(song: Song, playbackService: PlaybackService) {
        // Set the basic metadata since MediaStyle wont do it yourself.
        // Fun Fact: The documentation still says that MediaStyle will handle metadata changes
        // from MediaSession, even though it doesn't. After 6 years.
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
            playbackService.startForeground(NOTIFICATION_ID, mNotification)
        }
    }

    companion object {
        const val CHANNEL_ID = "CHANNEL_AUXIO_PLAYBACK"
        const val NOTIFICATION_ID = 0xA0A0
    }
}
