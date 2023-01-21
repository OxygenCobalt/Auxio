/*
 * Copyright (c) 2021 Auxio Project
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
 
package org.oxycblt.auxio

import android.app.Application
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import org.oxycblt.auxio.image.ImageSettings
import org.oxycblt.auxio.image.extractor.AlbumCoverFetcher
import org.oxycblt.auxio.image.extractor.ArtistImageFetcher
import org.oxycblt.auxio.image.extractor.ErrorCrossfadeTransitionFactory
import org.oxycblt.auxio.image.extractor.GenreImageFetcher
import org.oxycblt.auxio.image.extractor.MusicKeyer
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.ui.UISettings

/**
 * A simple, rational music player for android.
 * @author Alexander Capehart (OxygenCobalt)
 */
class Auxio : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        // Migrate any settings that may have changed in an app update.
        ImageSettings.from(this).migrate()
        PlaybackSettings.from(this).migrate()
        UISettings.from(this).migrate()
        // Adding static shortcuts in a dynamic manner is better than declaring them
        // manually, as it will properly handle the difference between debug and release
        // Auxio instances.
        ShortcutManagerCompat.addDynamicShortcuts(
            this,
            listOf(
                ShortcutInfoCompat.Builder(this, SHORTCUT_SHUFFLE_ID)
                    .setShortLabel(getString(R.string.lbl_shuffle_shortcut_short))
                    .setLongLabel(getString(R.string.lbl_shuffle_shortcut_long))
                    .setIcon(IconCompat.createWithResource(this, R.drawable.ic_shortcut_shuffle_24))
                    .setIntent(
                        Intent(this, MainActivity::class.java)
                            .setAction(INTENT_KEY_SHORTCUT_SHUFFLE))
                    .build()))
    }

    override fun newImageLoader() =
        ImageLoader.Builder(applicationContext)
            .components {
                // Add fetchers for Music components to make them usable with ImageRequest
                add(MusicKeyer())
                add(AlbumCoverFetcher.SongFactory())
                add(AlbumCoverFetcher.AlbumFactory())
                add(ArtistImageFetcher.Factory())
                add(GenreImageFetcher.Factory())
            }
            // Use our own crossfade with error drawable support
            .transitionFactory(ErrorCrossfadeTransitionFactory())
            // Not downloading anything, so no disk-caching
            .diskCachePolicy(CachePolicy.DISABLED)
            .build()

    companion object {
        /** The [Intent] name for the "Shuffle All" shortcut. */
        const val INTENT_KEY_SHORTCUT_SHUFFLE = BuildConfig.APPLICATION_ID + ".action.SHUFFLE_ALL"
        /** The ID of the "Shuffle All" shortcut. */
        private const val SHORTCUT_SHUFFLE_ID = "shortcut_shuffle"
    }
}
