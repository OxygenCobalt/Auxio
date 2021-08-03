package org.oxycblt.auxio.widgets

import android.content.Context
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackStateManager

class WidgetController(private val context: Context) : PlaybackStateManager.Callback {
    private val manager = PlaybackStateManager.getInstance()
    private val minimal = MinimalWidgetProvider()

    init {
        manager.addCallback(this)
    }

    fun initWidget(type: Int) {
        when (type) {
            MinimalWidgetProvider.TYPE -> minimal.update(context, manager)
        }
    }

    fun release() {
        minimal.stop(context)
        manager.removeCallback(this)
    }

    override fun onSongUpdate(song: Song?) {
        minimal.update(context, manager)
    }

    override fun onPlayingUpdate(isPlaying: Boolean) {
        minimal.update(context, manager)
    }
}
