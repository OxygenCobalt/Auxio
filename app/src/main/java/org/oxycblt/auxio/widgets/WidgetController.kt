package org.oxycblt.auxio.widgets

import android.content.Context
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.settings.SettingsManager

/**
 * A wrapper around each [WidgetProvider] that plugs into the main Auxio process and updates the
 * widget state based off of that. This cannot be rolled into [WidgetProvider] directly.
 */
class WidgetController(private val context: Context) :
    PlaybackStateManager.Callback,
    SettingsManager.Callback {
    private val playbackManager = PlaybackStateManager.getInstance()
    private val settingsManager = SettingsManager.getInstance()
    private val widget = WidgetProvider()

    init {
        playbackManager.addCallback(this)
        settingsManager.addCallback(this)
    }

    fun update() {
        widget.update(context, playbackManager)
    }

    /*
     * Release this instance, removing the callbacks and resetting all widgets
     */
    fun release() {
        widget.reset(context)
        playbackManager.removeCallback(this)
        settingsManager.removeCallback(this)
    }

    // --- PLAYBACKSTATEMANAGER CALLBACKS ---

    override fun onSongUpdate(song: Song?) {
        widget.update(context, playbackManager)
    }

    override fun onPlayingUpdate(isPlaying: Boolean) {
        widget.update(context, playbackManager)
    }

    override fun onShuffleUpdate(isShuffling: Boolean) {
        widget.update(context, playbackManager)
    }

    override fun onLoopUpdate(loopMode: LoopMode) {
        widget.update(context, playbackManager)
    }

    // --- SETTINGSMANAGER CALLBACKS ---

    override fun onShowCoverUpdate(showCovers: Boolean) {
        widget.update(context, playbackManager)
    }

    override fun onQualityCoverUpdate(doQualityCovers: Boolean) {
        widget.update(context, playbackManager)
    }
}
