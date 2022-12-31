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

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.service.ForegroundServiceNotification
import org.oxycblt.auxio.util.newBroadcastPendingIntent
import org.oxycblt.auxio.util.newMainPendingIntent

/**
 * The playback notification component. Due to race conditions regarding notification updates, this
 * component is not self-sufficient. [MediaSessionComponent] should be used instead of manage it.
 * @author Alexander Capehart (OxygenCobalt)
 */
@SuppressLint("RestrictedApi")
class NotificationComponent(private val context: Context, sessionToken: MediaSessionCompat.Token) :
    ForegroundServiceNotification(context, CHANNEL_INFO) {
    init {
        setSmallIcon(R.drawable.ic_auxio_24)
        setCategory(NotificationCompat.CATEGORY_TRANSPORT)
        setShowWhen(false)
        setSilent(true)
        setContentIntent(context.newMainPendingIntent())
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        addAction(buildRepeatAction(context, RepeatMode.NONE))
        addAction(
            buildAction(context, PlaybackService.ACTION_SKIP_PREV, R.drawable.ic_skip_prev_24))
        addAction(buildPlayPauseAction(context, true))
        addAction(
            buildAction(context, PlaybackService.ACTION_SKIP_NEXT, R.drawable.ic_skip_next_24))
        addAction(buildAction(context, PlaybackService.ACTION_EXIT, R.drawable.ic_close_24))

        setStyle(MediaStyle().setMediaSession(sessionToken).setShowActionsInCompactView(1, 2, 3))
    }

    override val code: Int
        get() = IntegerTable.PLAYBACK_NOTIFICATION_CODE

    // --- STATE FUNCTIONS ---

    /**
     * Update the currently shown metadata in this notification.
     * @param metadata The [MediaMetadataCompat] to display in this notification.
     */
    fun updateMetadata(metadata: MediaMetadataCompat) {
        setLargeIcon(metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART))
        setContentTitle(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
        setContentText(metadata.getText(MediaMetadataCompat.METADATA_KEY_ARTIST))

        // Starting in API 24, the subtext field changed semantics from being below the
        // content text to being above the title. Use an appropriate field for both.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Display description -> Parent in which playback is occurring
            setSubText(metadata.getText(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION))
        } else {
            setSubText(metadata.getText(MediaMetadataCompat.METADATA_KEY_ALBUM))
        }
    }

    /**
     * Update the playing state shown in this notification.
     * @param isPlaying Whether playback should be indicated as ongoing or paused.
     */
    fun updatePlaying(isPlaying: Boolean) {
        mActions[2] = buildPlayPauseAction(context, isPlaying)
    }

    /**
     * Update the secondary action in this notification to show the current [RepeatMode].
     * @param repeatMode The current [RepeatMode].
     */
    fun updateRepeatMode(repeatMode: RepeatMode) {
        mActions[0] = buildRepeatAction(context, repeatMode)
    }

    /**
     * Update the secondary action in this notification to show the current shuffle state.
     * @param isShuffled Whether the queue is currently shuffled or not.
     */
    fun updateShuffled(isShuffled: Boolean) {
        mActions[0] = buildShuffleAction(context, isShuffled)
    }

    // --- NOTIFICATION ACTION BUILDERS ---

    private fun buildPlayPauseAction(
        context: Context,
        isPlaying: Boolean
    ): NotificationCompat.Action {
        val drawableRes =
            if (isPlaying) {
                R.drawable.ic_pause_24
            } else {
                R.drawable.ic_play_24
            }
        return buildAction(context, PlaybackService.ACTION_PLAY_PAUSE, drawableRes)
    }

    private fun buildRepeatAction(
        context: Context,
        repeatMode: RepeatMode
    ): NotificationCompat.Action {
        return buildAction(context, PlaybackService.ACTION_INC_REPEAT_MODE, repeatMode.icon)
    }

    private fun buildShuffleAction(
        context: Context,
        isShuffled: Boolean
    ): NotificationCompat.Action {
        val drawableRes =
            if (isShuffled) {
                R.drawable.ic_shuffle_on_24
            } else {
                R.drawable.ic_shuffle_off_24
            }
        return buildAction(context, PlaybackService.ACTION_INVERT_SHUFFLE, drawableRes)
    }

    private fun buildAction(context: Context, actionName: String, @DrawableRes iconRes: Int) =
        NotificationCompat.Action.Builder(
                iconRes, actionName, context.newBroadcastPendingIntent(actionName))
            .build()

    private companion object {
        /** Notification channel used by solely the playback notification. */
        val CHANNEL_INFO =
            ChannelInfo(
                id = BuildConfig.APPLICATION_ID + ".channel.PLAYBACK",
                nameRes = R.string.lbl_playback)
    }
}
