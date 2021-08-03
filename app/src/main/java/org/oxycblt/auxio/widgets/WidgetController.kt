package org.oxycblt.auxio.widgets

import android.content.Context
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.settings.SettingsManager

/**
 * A wrapper around each widget subclass that manages which updates to deliver from the
 * main process's [PlaybackStateManager] and [SettingsManager] instances to the widgets themselves.
 */
class WidgetController(private val context: Context) :
    PlaybackStateManager.Callback,
    SettingsManager.Callback {
    private val playbackManager = PlaybackStateManager.getInstance()
    private val settingsManager = SettingsManager.getInstance()
    private val minimal = MinimalWidgetProvider()

    init {
        playbackManager.addCallback(this)
        settingsManager.addCallback(this)
    }

    /*
     * Initialize a newly added widget. This usually comes from the WIDGET_UPDATE intent.
     */
    fun initWidget(type: Int) {
        logD("Updating new widget $type")

        when (type) {
            MinimalWidgetProvider.TYPE -> minimal.update(context, playbackManager)
        }
    }

    /*
     * Update every widget, regardless of whether it needs to or not.
     */
    fun update() {
        logD("Updating all widgets")

        minimal.update(context, playbackManager)
    }

    /*
     * Release this instance, removing the callbacks and resetting all widgets
     */
    fun release() {
        logD("Resetting widgets")

        minimal.reset(context)
        playbackManager.removeCallback(this)
        settingsManager.removeCallback(this)
    }

    // --- PLAYBACKSTATEMANAGER CALLBACKS ---

    override fun onSongUpdate(song: Song?) {
        minimal.update(context, playbackManager)
    }

    override fun onPlayingUpdate(isPlaying: Boolean) {
        minimal.update(context, playbackManager)
    }

    // --- SETTINGSMANAGER CALLBACKS ---

    override fun onShowCoverUpdate(showCovers: Boolean) {
        minimal.update(context, playbackManager)
    }

    override fun onQualityCoverUpdate(doQualityCovers: Boolean) {
        minimal.update(context, playbackManager)
    }
}
