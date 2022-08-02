/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.storage.StorageManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import org.oxycblt.auxio.R
import org.oxycblt.auxio.home.tabs.Tab
import org.oxycblt.auxio.music.Directory
import org.oxycblt.auxio.music.dirs.MusicDirs
import org.oxycblt.auxio.playback.replaygain.ReplayGainMode
import org.oxycblt.auxio.playback.replaygain.ReplayGainPreAmp
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.ui.accent.Accent
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * Auxio's settings.
 *
 * This object wraps [SharedPreferences] in a type-safe manner, allowing access to all of the major
 * settings that Auxio uses. Mutability is determined by use, as some values are written by
 * PreferenceManager and others are written by Auxio's code.
 *
 * @author OxygenCobalt
 */
class Settings(private val context: Context, private val callback: Callback? = null) :
    SharedPreferences.OnSharedPreferenceChangeListener {
    private val inner = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    init {
        if (callback != null) {
            inner.registerOnSharedPreferenceChangeListener(this)
        }
    }

    fun release() {
        inner.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        unlikelyToBeNull(callback).onSettingChanged(key)
    }

    /**
     * An interface for receiving some preference updates. Use/Extend this instead of
     * [SharedPreferences.OnSharedPreferenceChangeListener] if possible, as it doesn't require a
     * context.
     */
    interface Callback {
        fun onSettingChanged(key: String)
    }

    // --- VALUES ---

    /** The current theme */
    val theme: Int
        get() =
            inner.getInt(
                context.getString(R.string.set_key_theme),
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

    /** Whether the dark theme should be black or not */
    val useBlackTheme: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_black_theme), false)

    /** The current accent. */
    var accent: Accent
        get() = handleAccentCompat(context, inner)
        set(value) {
            inner.edit {
                putInt(context.getString(R.string.set_key_accent), value.index)
                apply()
            }
        }

    /**
     * Whether to display the RepeatMode or the shuffle status on the notification. False if repeat,
     * true if shuffle.
     */
    val useAltNotifAction: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_alt_notif_action), false)

    /** The current library tabs preferred by the user. */
    var libTabs: Array<Tab>
        get() =
            Tab.fromSequence(
                inner.getInt(context.getString(R.string.set_key_lib_tabs), Tab.SEQUENCE_DEFAULT))
                ?: unlikelyToBeNull(Tab.fromSequence(Tab.SEQUENCE_DEFAULT))
        set(value) {
            inner.edit {
                putInt(context.getString(R.string.set_key_lib_tabs), Tab.toSequence(value))
                apply()
            }
        }

    /** Whether to load embedded covers */
    val showCovers: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_show_covers), true)

    /** Whether to ignore MediaStore covers */
    val useQualityCovers: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_quality_covers), false)

    /** Whether to round additional UI elements (including album covers) */
    val roundMode: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_round_mode), false)

    /** Whether to resume playback when a headset is connected (may not work well in all cases) */
    val headsetAutoplay: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_headset_autoplay), false)

    /** The current ReplayGain configuration */
    val replayGainMode: ReplayGainMode
        get() =
            ReplayGainMode.fromIntCode(
                inner.getInt(context.getString(R.string.set_key_replay_gain), Int.MIN_VALUE))
                ?: ReplayGainMode.OFF

    /** The current ReplayGain pre-amp configuration */
    var replayGainPreAmp: ReplayGainPreAmp
        get() =
            ReplayGainPreAmp(
                inner.getFloat(context.getString(R.string.set_key_pre_amp_with), 0f),
                inner.getFloat(context.getString(R.string.set_key_pre_amp_without), 0f))
        set(value) {
            inner.edit {
                putFloat(context.getString(R.string.set_key_pre_amp_with), value.with)
                putFloat(context.getString(R.string.set_key_pre_amp_without), value.without)
                apply()
            }
        }

    /** What queue to create when a song is selected from the library or search */
    val libPlaybackMode: PlaybackMode
        get() =
            PlaybackMode.fromInt(
                inner.getInt(
                    context.getString(R.string.set_key_library_song_playback_mode), Int.MIN_VALUE))
                ?: PlaybackMode.ALL_SONGS

    /**
     * What queue t create when a song is selected from an album/artist/genre. Null means to default
     * to the currently shown item.
     */
    val detailPlaybackMode: PlaybackMode?
        get() =
            PlaybackMode.fromInt(
                inner.getInt(
                    context.getString(R.string.set_key_detail_song_playback_mode), Int.MIN_VALUE))

    /** Whether shuffle should stay on when a new song is selected. */
    val keepShuffle: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_keep_shuffle), true)

    /** Whether to rewind when the back button is pressed. */
    val rewindWithPrev: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_rewind_prev), true)

    /**
     * Whether [org.oxycblt.auxio.playback.state.RepeatMode.TRACK] should pause when the track
     * repeats
     */
    val pauseOnRepeat: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_repeat_pause), false)

    /** Whether to parse metadata directly with ExoPlayer. */
    val useQualityTags: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_quality_tags), false)

    /** Whether to be actively watching for changes in the music library. */
    val shouldBeObserving: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_observing), false)

    /** Get the list of directories that music should be hidden/loaded from. */
    fun getMusicDirs(storageManager: StorageManager): MusicDirs {
        val dirs =
            (inner.getStringSet(context.getString(R.string.set_key_music_dirs), null) ?: emptySet())
                .mapNotNull { Directory.fromDocumentUri(storageManager, it) }

        return MusicDirs(
            dirs, inner.getBoolean(context.getString(R.string.set_key_music_dirs_include), false))
    }

    /** Set the list of directories that music should be hidden/loaded from. */
    fun setMusicDirs(musicDirs: MusicDirs) {
        inner.edit {
            putStringSet(
                context.getString(R.string.set_key_music_dirs),
                musicDirs.dirs.map(Directory::toDocumentUri).toSet())
            putBoolean(
                context.getString(R.string.set_key_music_dirs_include), musicDirs.shouldInclude)
            apply()
        }
    }

    /** The current filter mode of the search tab */
    var searchFilterMode: DisplayMode?
        get() =
            DisplayMode.fromInt(
                inner.getInt(context.getString(R.string.set_key_search_filter), Int.MIN_VALUE))
        set(value) {
            logD(value)
            inner.edit {
                putInt(
                    context.getString(R.string.set_key_search_filter),
                    value?.intCode ?: Int.MIN_VALUE)
                apply()
            }
        }

    /** The song sort mode on HomeFragment */
    var libSongSort: Sort
        get() =
            Sort.fromIntCode(
                inner.getInt(context.getString(R.string.set_key_lib_songs_sort), Int.MIN_VALUE))
                ?: Sort(Sort.Mode.ByName, true)
        set(value) {
            inner.edit {
                putInt(context.getString(R.string.set_key_lib_songs_sort), value.intCode)
                apply()
            }
        }

    /** The album sort mode on HomeFragment */
    var libAlbumSort: Sort
        get() =
            Sort.fromIntCode(
                inner.getInt(context.getString(R.string.set_key_lib_albums_sort), Int.MIN_VALUE))
                ?: Sort(Sort.Mode.ByName, true)
        set(value) {
            inner.edit {
                putInt(context.getString(R.string.set_key_lib_albums_sort), value.intCode)
                apply()
            }
        }

    /** The artist sort mode on HomeFragment */
    var libArtistSort: Sort
        get() =
            Sort.fromIntCode(
                inner.getInt(context.getString(R.string.set_key_lib_artists_sort), Int.MIN_VALUE))
                ?: Sort(Sort.Mode.ByName, true)
        set(value) {
            inner.edit {
                putInt(context.getString(R.string.set_key_lib_artists_sort), value.intCode)
                apply()
            }
        }

    /** The genre sort mode on HomeFragment */
    var libGenreSort: Sort
        get() =
            Sort.fromIntCode(
                inner.getInt(context.getString(R.string.set_key_lib_genres_sort), Int.MIN_VALUE))
                ?: Sort(Sort.Mode.ByName, true)
        set(value) {
            inner.edit {
                putInt(context.getString(R.string.set_key_lib_genres_sort), value.intCode)
                apply()
            }
        }

    /** The detail album sort mode */
    var detailAlbumSort: Sort
        get() {
            var sort =
                Sort.fromIntCode(
                    inner.getInt(
                        context.getString(R.string.set_key_detail_album_sort), Int.MIN_VALUE))
                    ?: Sort(Sort.Mode.ByDisc, true)

            // Correct legacy album sort modes to Disc
            if (sort.mode is Sort.Mode.ByName) {
                sort = sort.withMode(Sort.Mode.ByDisc)
            }

            return sort
        }
        set(value) {
            inner.edit {
                putInt(context.getString(R.string.set_key_detail_album_sort), value.intCode)
                apply()
            }
        }

    /** The detail artist sort mode */
    var detailArtistSort: Sort
        get() =
            Sort.fromIntCode(
                inner.getInt(context.getString(R.string.set_key_detail_artist_sort), Int.MIN_VALUE))
                ?: Sort(Sort.Mode.ByYear, false)
        set(value) {
            inner.edit {
                putInt(context.getString(R.string.set_key_detail_artist_sort), value.intCode)
                apply()
            }
        }

    /** The detail genre sort mode */
    var detailGenreSort: Sort
        get() =
            Sort.fromIntCode(
                inner.getInt(context.getString(R.string.set_key_detail_genre_sort), Int.MIN_VALUE))
                ?: Sort(Sort.Mode.ByName, true)
        set(value) {
            inner.edit {
                putInt(context.getString(R.string.set_key_detail_genre_sort), value.intCode)
                apply()
            }
        }
}
