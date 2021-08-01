package org.oxycblt.auxio.widgets

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import org.oxycblt.auxio.R
import org.oxycblt.auxio.coil.loadBitmap
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.playback.state.PlaybackStateManager

/**
 * The minimal widget. This widget only shows the album, song name, and artist without any
 * controls. Because you know. Minimalism.
 */
class MinimalWidgetProvider : BaseWidget() {
    override val type: Int get() = TYPE

    override fun getDefaultViews(context: Context): RemoteViews {
        val views = getRemoteViews(context, LAYOUT)

        views.setInt(R.id.widget_cover, "setImageResource", R.drawable.ic_song_clear)
        views.setInt(R.id.widget_cover, "setVisibility", View.VISIBLE)
        views.setInt(R.id.widget_placeholder_msg, "setVisibility", View.VISIBLE)
        views.setInt(R.id.widget_meta, "setVisibility", View.GONE)

        return views
    }

    override fun updateViews(
        context: Context,
        playbackManager: PlaybackStateManager,
        onDone: (RemoteViews) -> Unit
    ) {
        val views = getRemoteViews(context, LAYOUT)
        val song = playbackManager.song

        if (song != null) {
            logD("updating view to ${song.name}")

            // Show the proper widget views
            views.setInt(R.id.widget_placeholder_msg, "setVisibility", View.GONE)
            views.setInt(R.id.widget_meta, "setVisibility", View.VISIBLE)

            // Update the metadata
            views.setTextViewText(R.id.widget_song, song.name)
            views.setTextViewText(R.id.widget_artist, song.album.artist.name)

            // loadBitmap is async, hence the need for onDone
            loadBitmap(context, song) { bitmap ->
                if (bitmap != null) {
                    views.setBitmap(R.id.widget_cover, "setImageBitmap", bitmap)
                } else {
                    views.setInt(R.id.widget_cover, "setImageResource", R.drawable.ic_song_clear)
                }

                onDone(views)
            }
        } else {
            views.setInt(R.id.widget_cover, "setImageResource", R.drawable.ic_song_clear)
            onDone(views)
        }
    }

    companion object {
        const val TYPE = 0xA0D0

        // Workaround to make studio shut up about perfectly valid layouts somehow
        // being invalid for remote views.
        const val LAYOUT = R.layout.widget_minimal

        fun new(): MinimalWidgetProvider? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MinimalWidgetProvider()
            } else {
                null
            }
        }
    }
}
