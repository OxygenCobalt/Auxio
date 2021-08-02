package org.oxycblt.auxio.widgets

import android.content.Context
import android.os.Build
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
        return getRemoteViews(context, R.layout.widget_default)
    }

    override fun updateViews(
        context: Context,
        playbackManager: PlaybackStateManager,
        onDone: (RemoteViews) -> Unit
    ) {
        val song = playbackManager.song

        if (song != null) {
            logD("updating view to ${song.name}")

            val views = getRemoteViews(context, R.layout.widget_minimal)

            // Update the metadata
            views.setTextViewText(R.id.widget_song, song.name)
            views.setTextViewText(R.id.widget_artist, song.album.artist.name)

            // loadBitmap is async, hence the need for onDone
            loadBitmap(context, song) { bitmap ->
                if (bitmap != null) {
                    views.setBitmap(R.id.widget_cover, "setImageBitmap", bitmap)
                } else {
                    views.setInt(R.id.widget_cover, "setImageResource", R.drawable.ic_song)
                }

                onDone(views)
            }
        } else {
            onDone(getDefaultViews(context))
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
