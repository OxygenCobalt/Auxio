/*
 * Copyright (c) 2026 Auxio Project
 * HeadUnitEntryPoints.kt is part of Auxio.
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

package org.oxycblt.auxio.headunit

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.MainActivity
import org.oxycblt.auxio.R

object HeadUnitEntryPoints {
    private const val ACTION_PREFIX = BuildConfig.APPLICATION_ID + ".action."

    const val ACTION_OPEN_NOW_PLAYING = ACTION_PREFIX + "OPEN_NOW_PLAYING"
    const val ACTION_SHUFFLE_ALL = ACTION_PREFIX + "SHUFFLE_ALL"
    const val ACTION_OPEN_QUEUE = ACTION_PREFIX + "OPEN_QUEUE"
    const val ACTION_OPEN_RECENTLY_ADDED = ACTION_PREFIX + "OPEN_RECENTLY_ADDED"
    const val ACTION_OPEN_GENRES = ACTION_PREFIX + "OPEN_GENRES"
    const val ACTION_OPEN_ARTISTS = ACTION_PREFIX + "OPEN_ARTISTS"
    const val ACTION_OPEN_ALBUMS = ACTION_PREFIX + "OPEN_ALBUMS"
    const val ACTION_OPEN_FAVOURITES = ACTION_PREFIX + "OPEN_FAVOURITES"
    const val ACTION_OPEN_HEAD_UNIT_SETTINGS = ACTION_PREFIX + "OPEN_HEAD_UNIT_SETTINGS"

    const val EXTRA_ENTRY_DESTINATION = ACTION_PREFIX + "ENTRY_DESTINATION"

    enum class EntryDestination {
        NOW_PLAYING,
        QUEUE,
        RECENTLY_ADDED,
        GENRES,
        ARTISTS,
        ALBUMS,
        FAVOURITES,
        HEAD_UNIT_SETTINGS,
    }

    fun destinationForAction(action: String?): EntryDestination? =
        HeadUnitRoutePolicy.routeForAction(action)?.let { HeadUnitRoutePolicy.entryDestinationForRoute(it) }

    internal fun publishedDynamicShortcutIds(maxShortcutCount: Int): List<String> =
        dynamicShortcutSpecs(maxShortcutCount).map { it.id }

    fun createDynamicShortcuts(context: Context): List<ShortcutInfoCompat> {
        val maxShortcutCount = ShortcutManagerCompat.getMaxShortcutCountPerActivity(context)
        return dynamicShortcutSpecs(maxShortcutCount).map { spec ->
            shortcut(
                context = context,
                id = spec.id,
                shortLabelRes = spec.shortLabelRes,
                longLabelRes = spec.longLabelRes,
                action = spec.action,
                icon = IconCompat.createWithResource(context, spec.iconRes),
            )
        }
    }

    private fun dynamicShortcutSpecs(maxShortcutCount: Int): List<ShortcutSpec> {
        val nonNegativeCount = maxShortcutCount.coerceAtLeast(0)
        return PRIORITIZED_DYNAMIC_SHORTCUTS.take(nonNegativeCount)
    }

    private fun shortcut(
        context: Context,
        id: String,
        shortLabelRes: Int,
        longLabelRes: Int,
        action: String,
        icon: IconCompat,
    ): ShortcutInfoCompat =
        ShortcutInfoCompat.Builder(context, id)
            .setShortLabel(context.getString(shortLabelRes))
            .setLongLabel(context.getString(longLabelRes))
            .setIcon(icon)
            .setIntent(Intent(context, MainActivity::class.java).setAction(action))
            .build()

    private data class ShortcutSpec(
        val id: String,
        val shortLabelRes: Int,
        val longLabelRes: Int,
        val action: String,
        val iconRes: Int,
    )

    private val PRIORITIZED_DYNAMIC_SHORTCUTS =
        listOf(
            ShortcutSpec(
                id = "shortcut_now_playing",
                shortLabelRes = R.string.lbl_playback,
                longLabelRes = R.string.lbl_playback,
                action = ACTION_OPEN_NOW_PLAYING,
                iconRes = R.drawable.ic_play_24,
            ),
            ShortcutSpec(
                id = "shortcut_shuffle",
                shortLabelRes = R.string.lbl_shuffle_shortcut_short,
                longLabelRes = R.string.lbl_shuffle_shortcut_long,
                action = ACTION_SHUFFLE_ALL,
                iconRes = R.drawable.ic_shortcut_shuffle_24,
            ),
            ShortcutSpec(
                id = "shortcut_queue",
                shortLabelRes = R.string.lbl_queue,
                longLabelRes = R.string.lbl_queue,
                action = ACTION_OPEN_QUEUE,
                iconRes = R.drawable.ic_queue_add_24,
            ),
            ShortcutSpec(
                id = "shortcut_recently_added",
                shortLabelRes = R.string.lbl_recently_added,
                longLabelRes = R.string.lbl_recently_added,
                action = ACTION_OPEN_RECENTLY_ADDED,
                iconRes = R.drawable.ic_time_24,
            ),
        )
}