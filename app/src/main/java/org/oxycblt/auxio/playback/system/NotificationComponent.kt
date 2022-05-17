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
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.coil.BitmapProvider
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.util.getSystemServiceSafe
import org.oxycblt.auxio.util.newBroadcastIntent
import org.oxycblt.auxio.util.newMainIntent

/**
 * The unified notification for [PlaybackService]. Due to the nature of how this notification is
 * used, it is *not self-sufficient*. Updates have to be delivered manually, as to prevent state
 * inconsistency when the foreground state is started.
 * @author OxygenCobalt
 */
@SuppressLint("RestrictedApi")
class NotificationComponent(
    private val context: Context,
    private val callback: Callback,
    sessionToken: MediaSessionCompat.Token
) : NotificationCompat.Builder(context, CHANNEL_ID) {
    private val notificationManager = context.getSystemServiceSafe(NotificationManager::class)
    private val provider = BitmapProvider(context)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.info_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT)

            notificationManager.createNotificationChannel(channel)
        }

        setSmallIcon(R.drawable.ic_auxio)
        setCategory(NotificationCompat.CATEGORY_SERVICE)
        setShowWhen(false)
        setSilent(true)
        setContentIntent(context.newMainIntent())
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        addAction(buildRepeatAction(context, RepeatMode.NONE))
        addAction(buildAction(context, PlaybackService.ACTION_SKIP_PREV, R.drawable.ic_skip_prev))
        addAction(buildPlayPauseAction(context, true))
        addAction(buildAction(context, PlaybackService.ACTION_SKIP_NEXT, R.drawable.ic_skip_next))
        addAction(buildAction(context, PlaybackService.ACTION_EXIT, R.drawable.ic_exit))

        setStyle(MediaStyle().setMediaSession(sessionToken).setShowActionsInCompactView(1, 2, 3))
    }

    fun renotify() {
        notificationManager.notify(IntegerTable.NOTIFICATION_CODE, build())
    }

    fun release() {
        provider.release()
    }

    // --- STATE FUNCTIONS ---

    /** Set the metadata of the notification using [song]. */
    fun updateMetadata(song: Song, parent: MusicParent?) {
        setContentTitle(song.resolveName(context))
        setContentText(song.resolveIndividualArtistName(context))

        // Starting in API 24, the subtext field changed semantics from being below the content
        // text to being above the title.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setSubText(parent?.resolveName(context) ?: context.getString(R.string.lbl_all_songs))
        } else {
            setSubText(song.resolveName(context))
        }

        provider.load(
            song,
            object : BitmapProvider.Target {
                override fun onCompleted(bitmap: Bitmap?) {
                    setLargeIcon(bitmap)
                    callback.onNotificationChanged(this@NotificationComponent)
                }
            })
    }

    /** Set the playing icon on the notification */
    fun updatePlaying(isPlaying: Boolean) {
        mActions[2] = buildPlayPauseAction(context, isPlaying)

        if (!provider.isBusy) {
            callback.onNotificationChanged(this)
        }
    }

    /** Update the first action to reflect the [repeatMode] given. */
    fun updateRepeatMode(repeatMode: RepeatMode) {
        mActions[0] = buildRepeatAction(context, repeatMode)

        if (!provider.isBusy) {
            callback.onNotificationChanged(this)
        }
    }

    /** Update the first action to reflect whether the queue is shuffled or not */
    fun updateShuffled(isShuffled: Boolean) {
        mActions[0] = buildShuffleAction(context, isShuffled)

        if (!provider.isBusy) {
            callback.onNotificationChanged(this)
        }
    }

    // --- NOTIFICATION ACTION BUILDERS ---

    private fun buildPlayPauseAction(
        context: Context,
        isPlaying: Boolean
    ): NotificationCompat.Action {
        val drawableRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play

        return buildAction(context, PlaybackService.ACTION_PLAY_PAUSE, drawableRes)
    }

    private fun buildRepeatAction(
        context: Context,
        repeatMode: RepeatMode
    ): NotificationCompat.Action {
        val drawableRes =
            when (repeatMode) {
                RepeatMode.NONE -> R.drawable.ic_remote_repeat_off
                RepeatMode.ALL -> R.drawable.ic_repeat
                RepeatMode.TRACK -> R.drawable.ic_repeat_one
            }

        return buildAction(context, PlaybackService.ACTION_INC_REPEAT_MODE, drawableRes)
    }

    private fun buildShuffleAction(
        context: Context,
        isShuffled: Boolean
    ): NotificationCompat.Action {
        val drawableRes =
            if (isShuffled) R.drawable.ic_shuffle else R.drawable.ic_remote_shuffle_off

        return buildAction(context, PlaybackService.ACTION_INVERT_SHUFFLE, drawableRes)
    }

    private fun buildAction(
        context: Context,
        actionName: String,
        @DrawableRes iconRes: Int
    ): NotificationCompat.Action {
        val action =
            NotificationCompat.Action.Builder(
                iconRes, actionName, context.newBroadcastIntent(actionName))

        return action.build()
    }

    interface Callback {
        fun onNotificationChanged(component: NotificationComponent)
    }

    companion object {
        const val CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel.PLAYBACK"
    }
}
