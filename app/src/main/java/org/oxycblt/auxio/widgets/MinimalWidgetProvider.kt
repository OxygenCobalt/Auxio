package org.oxycblt.auxio.widgets

import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import org.oxycblt.auxio.R
import org.oxycblt.auxio.coil.loadBitmap
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.system.PlaybackService
import org.oxycblt.auxio.ui.newBroadcastIntent

/**
 * The minimal widget, which shows the primary song controls and basic playback information.
 */
class MinimalWidgetProvider : BaseWidget() {
    override val type: Int get() = TYPE

    override fun createViews(context: Context, layout: Int): RemoteViews {
        val views = super.createViews(context, layout)

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

        return views
    }

    override fun updateViews(
        context: Context,
        playbackManager: PlaybackStateManager,
        onDone: (RemoteViews) -> Unit
    ) {
        val song = playbackManager.song

        if (song != null) {
            logD("updating view to ${song.name}")

            val views = createViews(context, R.layout.widget_minimal)

            // Update the metadata
            views.setTextViewText(R.id.widget_song, song.name)
            views.setTextViewText(R.id.widget_artist, song.album.artist.name)

            views.setInt(
                R.id.widget_play_pause,
                "setImageResource",
                if (playbackManager.isPlaying) {
                    R.drawable.ic_pause
                } else {
                    R.drawable.ic_play
                }
            )

            // loadBitmap is async, hence the need for onDone
            loadBitmap(context, song) { bitmap ->
                if (bitmap != null) {
                    views.setBitmap(R.id.widget_cover, "setImageBitmap", bitmap)
                    views.setCharSequence(
                        R.id.widget_cover, "setContentDescription",
                        context.getString(R.string.description_album_cover, song.album.name)
                    )
                } else {
                    views.setInt(R.id.widget_cover, "setImageResource", R.drawable.ic_song)
                    views.setCharSequence(
                        R.id.widget_cover, "setContentDescription",
                        context.getString(R.string.description_placeholder_cover)
                    )
                }

                onDone(views)
            }
        } else {
            onDone(defaultViews(context))
        }
    }

    companion object {
        const val TYPE = 0xA0D0

        fun new(): MinimalWidgetProvider? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MinimalWidgetProvider()
            } else {
                null
            }
        }
    }
}
