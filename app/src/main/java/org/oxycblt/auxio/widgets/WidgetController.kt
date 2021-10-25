/*
 * Copyright (c) 2021 Auxio Project
 * WidgetController.kt is part of Auxio.
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

package org.oxycblt.auxio.widgets

import android.content.Context
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.settings.SettingsManager

/**
 * A wrapper around each [WidgetProvider] that plugs into the main Auxio process and updates the
 * widget state based off of that. This cannot be rolled into [WidgetProvider] directly, as it may
 * result in memory leaks if [PlaybackStateManager]/[SettingsManager] gets created and bound
 * to without being released.
 */
class WidgetController(private val context: Context) :
    PlaybackStateManager.Callback,
    SettingsManager.Callback {
    private val playbackManager = PlaybackStateManager.maybeGetInstance()
    private val settingsManager = SettingsManager.getInstance()
    private val widget = WidgetProvider()

    init {
        playbackManager.addCallback(this)
        settingsManager.addCallback(this)
    }

    /*
     * Force-update the widget.
     */
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
