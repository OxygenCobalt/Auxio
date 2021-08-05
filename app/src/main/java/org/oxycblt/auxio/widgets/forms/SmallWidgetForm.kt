package org.oxycblt.auxio.widgets.forms

import android.content.Context
import android.widget.RemoteViews
import org.oxycblt.auxio.R
import org.oxycblt.auxio.playback.system.PlaybackService
import org.oxycblt.auxio.ui.newBroadcastIntent
import org.oxycblt.auxio.widgets.WidgetState

class SmallWidgetForm : WidgetForm(R.layout.widget_small) {
    override fun createViews(context: Context, state: WidgetState): RemoteViews {
        val views = super.createViews(context, state)

        views.setOnClickPendingIntent(
            R.id.widget_skip_prev,
            context.newBroadcastIntent(
                PlaybackService.ACTION_SKIP_PREV
            )
        )

        views.setOnClickPendingIntent(
            R.id.widget_play_pause,
            context.newBroadcastIntent(
                PlaybackService.ACTION_PLAY_PAUSE
            )
        )

        views.setOnClickPendingIntent(
            R.id.widget_skip_next,
            context.newBroadcastIntent(
                PlaybackService.ACTION_SKIP_NEXT
            )
        )

        views.setTextViewText(R.id.widget_song, state.song.name)
        views.setTextViewText(R.id.widget_artist, state.song.album.artist.name)

        views.setImageViewResource(
            R.id.widget_play_pause,
            if (state.isPlaying) {
                R.drawable.ic_pause
            } else {
                R.drawable.ic_play
            }
        )

        if (state.albumArt != null) {
            views.setImageViewBitmap(R.id.widget_cover, state.albumArt)
            views.setCharSequence(
                R.id.widget_cover, "setContentDescription",
                context.getString(R.string.description_album_cover, state.song.album.name)
            )
        } else {
            views.setImageViewResource(R.id.widget_cover, R.drawable.ic_song)
            views.setCharSequence(
                R.id.widget_cover,
                "setContentDescription",
                context.getString(R.string.description_no_cover)
            )
        }

        return views
    }
}
