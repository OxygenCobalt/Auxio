package org.oxycblt.auxio.widgets

import android.content.Context
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackStateManager

class WidgetController(private val context: Context) : PlaybackStateManager.Callback {
    private val playbackManager = PlaybackStateManager.getInstance()
    private val minimal = MinimalWidgetProvider()

    init {
        playbackManager.addCallback(this)
    }

    fun initWidget(type: Int) {
        logD("Updating new widget $type")

        when (type) {
            MinimalWidgetProvider.TYPE -> minimal.update(context, playbackManager)
        }
    }

    fun update() {
        logD("Updating all widgets")

        minimal.update(context, playbackManager)
    }

    fun release() {
        logD("Resetting widgets")

        minimal.reset(context)
        playbackManager.removeCallback(this)
    }

    override fun onSongUpdate(song: Song?) {
        minimal.update(context, playbackManager)
    }

    override fun onPlayingUpdate(isPlaying: Boolean) {
        minimal.update(context, playbackManager)
    }
}
