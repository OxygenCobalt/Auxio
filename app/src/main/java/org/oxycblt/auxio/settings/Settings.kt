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
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Build
import android.os.storage.StorageManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.home.tabs.Tab
import org.oxycblt.auxio.image.CoverMode
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.music.Sort
import org.oxycblt.auxio.music.filesystem.Directory
import org.oxycblt.auxio.music.filesystem.MusicDirectories
import org.oxycblt.auxio.playback.ActionMode
import org.oxycblt.auxio.playback.replaygain.ReplayGainMode
import org.oxycblt.auxio.playback.replaygain.ReplayGainPreAmp
import org.oxycblt.auxio.ui.accent.Accent
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A [SharedPreferences] wrapper providing type-safe interfaces to all of the app's settings. Member
 * mutability is dependent on how they are used in app. Immutable members are often only modified by
 * the preferences view, while mutable members are modified elsewhere.
 * @author Alexander Capehart (OxygenCobalt)
 */
class Settings(private val context: Context) {
    private val inner = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    /**
     * Migrate any settings from an old version into their modern counterparts. This can cause data
     * loss depending on the feasibility of a migration.
     */
    fun migrate() {
        if (inner.contains(OldKeys.KEY_ACCENT3)) {
            logD("Migrating ${OldKeys.KEY_ACCENT3}")

            var accent = inner.getInt(OldKeys.KEY_ACCENT3, 5)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Accents were previously frozen as soon as the OS was updated to android twelve,
                // as dynamic colors were enabled by default. This is no longer the case, so we need
                // to re-update the setting to dynamic colors here.
                accent = 16
            }

            inner.edit {
                putInt(context.getString(R.string.set_key_accent), accent)
                remove(OldKeys.KEY_ACCENT3)
                apply()
            }
        }

        if (inner.contains(OldKeys.KEY_SHOW_COVERS) || inner.contains(OldKeys.KEY_QUALITY_COVERS)) {
            logD("Migrating cover settings")

            val mode =
                when {
                    !inner.getBoolean(OldKeys.KEY_SHOW_COVERS, true) -> CoverMode.OFF
                    !inner.getBoolean(OldKeys.KEY_QUALITY_COVERS, true) -> CoverMode.MEDIA_STORE
                    else -> CoverMode.QUALITY
                }

            inner.edit {
                putInt(context.getString(R.string.set_key_cover_mode), mode.intCode)
                remove(OldKeys.KEY_SHOW_COVERS)
                remove(OldKeys.KEY_QUALITY_COVERS)
            }
        }

        if (inner.contains(OldKeys.KEY_ALT_NOTIF_ACTION)) {
            logD("Migrating ${OldKeys.KEY_ALT_NOTIF_ACTION}")

            val mode =
                if (inner.getBoolean(OldKeys.KEY_ALT_NOTIF_ACTION, false)) {
                    ActionMode.SHUFFLE
                } else {
                    ActionMode.REPEAT
                }

            inner.edit {
                putInt(context.getString(R.string.set_key_notif_action), mode.intCode)
                remove(OldKeys.KEY_ALT_NOTIF_ACTION)
                apply()
            }
        }

        fun Int.migratePlaybackMode() =
            when (this) {
                // Convert PlaybackMode into MusicMode
                IntegerTable.PLAYBACK_MODE_ALL_SONGS -> MusicMode.SONGS
                IntegerTable.PLAYBACK_MODE_IN_ARTIST -> MusicMode.ARTISTS
                IntegerTable.PLAYBACK_MODE_IN_ALBUM -> MusicMode.ALBUMS
                IntegerTable.PLAYBACK_MODE_IN_GENRE -> MusicMode.GENRES
                else -> null
            }

        if (inner.contains(OldKeys.KEY_LIB_PLAYBACK_MODE)) {
            logD("Migrating ${OldKeys.KEY_LIB_PLAYBACK_MODE}")

            val mode =
                inner
                    .getInt(OldKeys.KEY_LIB_PLAYBACK_MODE, IntegerTable.PLAYBACK_MODE_ALL_SONGS)
                    .migratePlaybackMode()
                    ?: MusicMode.SONGS

            inner.edit {
                putInt(context.getString(R.string.set_key_library_song_playback_mode), mode.intCode)
                remove(OldKeys.KEY_LIB_PLAYBACK_MODE)
                apply()
            }
        }

        if (inner.contains(OldKeys.KEY_DETAIL_PLAYBACK_MODE)) {
            logD("Migrating ${OldKeys.KEY_DETAIL_PLAYBACK_MODE}")

            val mode =
                inner.getInt(OldKeys.KEY_DETAIL_PLAYBACK_MODE, Int.MIN_VALUE).migratePlaybackMode()

            inner.edit {
                putInt(
                    context.getString(R.string.set_key_detail_song_playback_mode),
                    mode?.intCode ?: Int.MIN_VALUE)
                remove(OldKeys.KEY_DETAIL_PLAYBACK_MODE)
                apply()
            }
        }
    }

    /**
     * Add a [SharedPreferences.OnSharedPreferenceChangeListener] to monitor for settings updates.
     * @param listener The [SharedPreferences.OnSharedPreferenceChangeListener] to add.
     */
    fun addListener(listener: OnSharedPreferenceChangeListener) {
        inner.registerOnSharedPreferenceChangeListener(listener)
    }

    /**
     * Unregister a [SharedPreferences.OnSharedPreferenceChangeListener], preventing any further
     * settings updates from being sent to ti.t
     */
    fun removeListener(listener: OnSharedPreferenceChangeListener) {
        inner.unregisterOnSharedPreferenceChangeListener(listener)
    }

    // --- VALUES ---

    /** The current theme. Represented by the [AppCompatDelegate] constants. */
    val theme: Int
        get() =
            inner.getInt(
                context.getString(R.string.set_key_theme),
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

    /** Whether to use a black background when a dark theme is currently used. */
    val useBlackTheme: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_black_theme), false)

    /** The current [Accent] (Color Scheme). */
    var accent: Accent
        get() =
            Accent.from(inner.getInt(context.getString(R.string.set_key_accent), Accent.DEFAULT))
        set(value) {
            inner.edit {
                putInt(context.getString(R.string.set_key_accent), value.index)
                apply()
            }
        }

    /** The tabs to show in the home UI. */
    var libTabs: Array<Tab>
        get() =
            Tab.fromIntCode(
                inner.getInt(context.getString(R.string.set_key_lib_tabs), Tab.SEQUENCE_DEFAULT))
                ?: unlikelyToBeNull(Tab.fromIntCode(Tab.SEQUENCE_DEFAULT))
        set(value) {
            inner.edit {
                putInt(context.getString(R.string.set_key_lib_tabs), Tab.toIntCode(value))
                apply()
            }
        }

    /** Whether to hide artists considered "collaborators" from the home UI. */
    val shouldHideCollaborators: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_hide_collaborators), false)

    /** Whether to round additional UI elements that require album covers to be rounded. */
    val roundMode: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_round_mode), false)

    /** The action to display on the playback bar. */
    val playbackBarAction: ActionMode
        get() =
            ActionMode.fromIntCode(
                inner.getInt(context.getString(R.string.set_key_bar_action), Int.MIN_VALUE))
                ?: ActionMode.NEXT

    /** The action to display in the playback notification. */
    val playbackNotificationAction: ActionMode
        get() =
            ActionMode.fromIntCode(
                inner.getInt(context.getString(R.string.set_key_notif_action), Int.MIN_VALUE))
                ?: ActionMode.REPEAT

    /** Whether to start playback when a headset is plugged in. */
    val headsetAutoplay: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_headset_autoplay), false)

    /** The current ReplayGain configuration. */
    val replayGainMode: ReplayGainMode
        get() =
            ReplayGainMode.fromIntCode(
                inner.getInt(context.getString(R.string.set_key_replay_gain), Int.MIN_VALUE))
                ?: ReplayGainMode.DYNAMIC

    /** The current ReplayGain pre-amp configuration. */
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

    /** What MusicParent item to play from when a Song is played from the home view. */
    val libPlaybackMode: MusicMode
        get() =
            MusicMode.fromIntCode(
                inner.getInt(
                    context.getString(R.string.set_key_library_song_playback_mode), Int.MIN_VALUE))
                ?: MusicMode.SONGS

    /**
     * What MusicParent item to play from when a Song is played from the detail view. Will be null
     * if configured to play from the currently shown item.
     */
    val detailPlaybackMode: MusicMode?
        get() =
            MusicMode.fromIntCode(
                inner.getInt(
                    context.getString(R.string.set_key_detail_song_playback_mode), Int.MIN_VALUE))

    /** Whether to keep shuffle on when playing a new Song. */
    val keepShuffle: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_keep_shuffle), true)

    /** Whether to rewind when the skip previous button is pressed before skipping back. */
    val rewindWithPrev: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_rewind_prev), true)

    /** Whether a song should pause after every repeat. */
    val pauseOnRepeat: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_repeat_pause), false)

    /** Whether to be actively watching for changes in the music library. */
    val shouldBeObserving: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_observing), false)

    /** The strategy used when loading album covers. */
    val coverMode: CoverMode
        get() =
            CoverMode.fromIntCode(
                inner.getInt(context.getString(R.string.set_key_cover_mode), Int.MIN_VALUE))
                ?: CoverMode.MEDIA_STORE

    /** Whether to exclude non-music audio files from the music library. */
    val excludeNonMusic: Boolean
        get() = inner.getBoolean(context.getString(R.string.set_key_exclude_non_music), true)

    /**
     * Set the configuration on how to handle particular directories in the music library.
     * @param storageManager [StorageManager] required to parse directories.
     * @return The [MusicDirectories] configuration.
     */
    fun getMusicDirs(storageManager: StorageManager): MusicDirectories {
        val dirs =
            (inner.getStringSet(context.getString(R.string.set_key_music_dirs), null) ?: emptySet())
                .mapNotNull { Directory.fromDocumentTreeUri(storageManager, it) }
        return MusicDirectories(
            dirs, inner.getBoolean(context.getString(R.string.set_key_music_dirs_include), false))
    }

    /**
     * Set the configuration on how to handle particular directories in the music library.
     * @param musicDirs The new [MusicDirectories] configuration.
     */
    fun setMusicDirs(musicDirs: MusicDirectories) {
        inner.edit {
            putStringSet(
                context.getString(R.string.set_key_music_dirs),
                musicDirs.dirs.map(Directory::toDocumentTreeUri).toSet())
            putBoolean(
                context.getString(R.string.set_key_music_dirs_include), musicDirs.shouldInclude)
            apply()
        }
    }

    /**
     * A string of characters representing the desired separator characters to denote multi-value
     * tags.
     */
    var musicSeparators: String?
        // Differ from convention and store a string of separator characters instead of an int
        // code. This makes it easier to use in Regexes and makes it more extendable.
        get() =
            inner.getString(context.getString(R.string.set_key_separators), null)?.ifEmpty { null }
        set(value) {
            inner.edit {
                putString(context.getString(R.string.set_key_separators), value?.ifEmpty { null })
                apply()
            }
        }

    /** The type of Music the search view is currently filtering to. */
    var searchFilterMode: MusicMode?
        get() =
            MusicMode.fromIntCode(
                inner.getInt(context.getString(R.string.set_key_search_filter), Int.MIN_VALUE))
        set(value) {
            inner.edit {
                putInt(
                    context.getString(R.string.set_key_search_filter),
                    value?.intCode ?: Int.MIN_VALUE)
                apply()
            }
        }

    /** The Song [Sort] mode used in the Home UI. */
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

    /** The Album [Sort] mode used in the Home UI. */
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

    /** The Artist [Sort] mode used in the Home UI. */
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

    /** The Genre [Sort] mode used in the Home UI. */
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

    /** The [Sort] mode used in the Album Detail UI. */
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

    /** The [Sort] mode used in the Artist Detail UI. */
    var detailArtistSort: Sort
        get() =
            Sort.fromIntCode(
                inner.getInt(context.getString(R.string.set_key_detail_artist_sort), Int.MIN_VALUE))
                ?: Sort(Sort.Mode.ByDate, false)
        set(value) {
            inner.edit {
                putInt(context.getString(R.string.set_key_detail_artist_sort), value.intCode)
                apply()
            }
        }

    /** The [Sort] mode used in the Genre Detail UI. */
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

    /** Legacy keys that are no longer used, but still have to be migrated. */
    private object OldKeys {
        const val KEY_ACCENT3 = "auxio_accent"
        const val KEY_ALT_NOTIF_ACTION = "KEY_ALT_NOTIF_ACTION"
        const val KEY_SHOW_COVERS = "KEY_SHOW_COVERS"
        const val KEY_QUALITY_COVERS = "KEY_QUALITY_COVERS"
        const val KEY_LIB_PLAYBACK_MODE = "KEY_SONG_PLAY_MODE2"
        const val KEY_DETAIL_PLAYBACK_MODE = "auxio_detail_song_play_mode"
    }
}
