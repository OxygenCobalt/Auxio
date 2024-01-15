/*
 * Copyright (c) 2023 Auxio Project
 * PlaybackSettings.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.playback.replaygain.ReplayGainMode
import org.oxycblt.auxio.playback.replaygain.ReplayGainPreAmp
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.logD

/**
 * User configuration specific to the playback system.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface PlaybackSettings : Settings<PlaybackSettings.Listener> {
    /** The action to display on the playback bar. */
    val barAction: ActionMode
    /** The action to display in the playback notification. */
    val notificationAction: ActionMode
    /** Whether to start playback when a headset is plugged in. */
    val headsetAutoplay: Boolean
    /** The current ReplayGain configuration. */
    val replayGainMode: ReplayGainMode
    /** The current ReplayGain pre-amp configuration. */
    var replayGainPreAmp: ReplayGainPreAmp
    /** How to play a song from a general list of songs, specified by [PlaySong] */
    val playInListWith: PlaySong
    /**
     * How to play a song from a parent item, specified by [PlaySong]. Null if to delegate to the UI
     * context.
     */
    val inParentPlaybackMode: PlaySong?
    /** Whether to keep shuffle on when playing a new Song. */
    val keepShuffle: Boolean
    /** Whether to rewind when the skip previous button is pressed before skipping back. */
    val rewindWithPrev: Boolean
    /** Whether a song should pause after every repeat. */
    val pauseOnRepeat: Boolean
    /** Whether to maintain the play/pause state when skipping or editing the queue */
    val rememberPause: Boolean

    interface Listener {
        /** Called when one of the ReplayGain configurations have changed. */
        fun onReplayGainSettingsChanged() {}
        /** Called when [notificationAction] has changed. */
        fun onNotificationActionChanged() {}
        /** Called when [barAction] has changed. */
        fun onBarActionChanged() {}
        /** Called when [pauseOnRepeat] has changed. */
        fun onPauseOnRepeatChanged() {}
    }
}

class PlaybackSettingsImpl @Inject constructor(@ApplicationContext context: Context) :
    Settings.Impl<PlaybackSettings.Listener>(context), PlaybackSettings {
    override val playInListWith: PlaySong
        get() =
            PlaySong.fromIntCode(
                sharedPreferences.getInt(
                    getString(R.string.set_key_play_in_list_with), Int.MIN_VALUE))
                ?: PlaySong.FromAll

    override val inParentPlaybackMode: PlaySong?
        get() =
            PlaySong.fromIntCode(
                sharedPreferences.getInt(
                    getString(R.string.set_key_play_in_parent_with), Int.MIN_VALUE))

    override val barAction: ActionMode
        get() =
            ActionMode.fromIntCode(
                sharedPreferences.getInt(getString(R.string.set_key_bar_action), Int.MIN_VALUE))
                ?: ActionMode.NEXT

    override val notificationAction: ActionMode
        get() =
            ActionMode.fromIntCode(
                sharedPreferences.getInt(getString(R.string.set_key_notif_action), Int.MIN_VALUE))
                ?: ActionMode.REPEAT

    override val headsetAutoplay: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_headset_autoplay), false)

    override val replayGainMode: ReplayGainMode
        get() =
            ReplayGainMode.fromIntCode(
                sharedPreferences.getInt(getString(R.string.set_key_replay_gain), Int.MIN_VALUE))
                ?: ReplayGainMode.DYNAMIC

    override var replayGainPreAmp: ReplayGainPreAmp
        get() =
            ReplayGainPreAmp(
                sharedPreferences.getFloat(getString(R.string.set_key_pre_amp_with), 0f),
                sharedPreferences.getFloat(getString(R.string.set_key_pre_amp_without), 0f))
        set(value) {
            sharedPreferences.edit {
                putFloat(getString(R.string.set_key_pre_amp_with), value.with)
                putFloat(getString(R.string.set_key_pre_amp_without), value.without)
                apply()
            }
        }

    override val keepShuffle: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_keep_shuffle), true)

    override val rewindWithPrev: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_rewind_prev), true)

    override val pauseOnRepeat: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_repeat_pause), false)

    override val rememberPause: Boolean
        get() = sharedPreferences.getBoolean(getString(R.string.set_key_remember_pause), false)

    override fun migrate() {
        // MusicMode was converted to PlaySong in 3.2.0
        fun Int.migrateMusicMode() =
            when (this) {
                IntegerTable.MUSIC_MODE_SONGS -> PlaySong.FromAll
                IntegerTable.MUSIC_MODE_ALBUMS -> PlaySong.FromAlbum
                IntegerTable.MUSIC_MODE_ARTISTS -> PlaySong.FromArtist(null)
                IntegerTable.MUSIC_MODE_GENRES -> PlaySong.FromGenre(null)
                else -> null
            }

        if (sharedPreferences.contains(OLD_KEY_LIB_MUSIC_PLAYBACK_MODE)) {
            logD("Migrating $OLD_KEY_LIB_MUSIC_PLAYBACK_MODE")

            val mode =
                sharedPreferences
                    .getInt(OLD_KEY_LIB_MUSIC_PLAYBACK_MODE, Int.MIN_VALUE)
                    .migrateMusicMode()

            sharedPreferences.edit {
                putInt(
                    getString(R.string.set_key_play_in_list_with), mode?.intCode ?: Int.MIN_VALUE)
                remove(OLD_KEY_LIB_MUSIC_PLAYBACK_MODE)
                apply()
            }
        }

        if (sharedPreferences.contains(OLD_KEY_DETAIL_MUSIC_PLAYBACK_MODE)) {
            logD("Migrating $OLD_KEY_DETAIL_MUSIC_PLAYBACK_MODE")

            val mode =
                sharedPreferences
                    .getInt(OLD_KEY_DETAIL_MUSIC_PLAYBACK_MODE, Int.MIN_VALUE)
                    .migrateMusicMode()

            sharedPreferences.edit {
                putInt(
                    getString(R.string.set_key_play_in_parent_with), mode?.intCode ?: Int.MIN_VALUE)
                remove(OLD_KEY_DETAIL_MUSIC_PLAYBACK_MODE)
                apply()
            }
        }
    }

    override fun onSettingChanged(key: String, listener: PlaybackSettings.Listener) {
        when (key) {
            getString(R.string.set_key_replay_gain),
            getString(R.string.set_key_pre_amp_with),
            getString(R.string.set_key_pre_amp_without) -> {
                logD("Dispatching ReplayGain setting change")
                listener.onReplayGainSettingsChanged()
            }
            getString(R.string.set_key_notif_action) -> {
                logD("Dispatching notification setting change")
                listener.onNotificationActionChanged()
            }
            getString(R.string.set_key_bar_action) -> {
                logD("Dispatching bar action change")
                listener.onBarActionChanged()
            }
            getString(R.string.set_key_repeat_pause) -> {
                logD("Dispatching pause on repeat change")
                listener.onPauseOnRepeatChanged()
            }
        }
    }

    private companion object {
        const val OLD_KEY_LIB_MUSIC_PLAYBACK_MODE = "auxio_library_playback_mode"
        const val OLD_KEY_DETAIL_MUSIC_PLAYBACK_MODE = "auxio_detail_playback_mode"
    }
}
