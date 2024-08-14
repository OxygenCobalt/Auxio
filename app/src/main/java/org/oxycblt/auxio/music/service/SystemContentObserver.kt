/*
 * Copyright (c) 2024 Auxio Project
 * SystemContentObserver.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.service

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.music.fs.contentResolverSafe
import org.oxycblt.auxio.util.logD

/**
 * A [ContentObserver] that observes the [MediaStore] music database for changes, a behavior known
 * to the user as automatic rescanning. The active (and not passive) nature of observing the
 * database is what requires [IndexerServiceFragment] to stay foreground when this is enabled.
 */
class SystemContentObserver
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val musicRepository: MusicRepository,
    private val musicSettings: MusicSettings
) : ContentObserver(Handler(Looper.getMainLooper())), Runnable {
    private val handler = Handler(Looper.getMainLooper())

    fun attach() {
        context.contentResolverSafe.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, this)
    }

    /**
     * Release this instance, preventing it from further observing the database and cancelling any
     * pending update events.
     */
    fun release() {
        handler.removeCallbacks(this)
        context.contentResolverSafe.unregisterContentObserver(this)
    }

    override fun onChange(selfChange: Boolean) {
        // Batch rapid-fire updates to the library into a single call to run after 500ms
        handler.removeCallbacks(this)
        handler.postDelayed(this, REINDEX_DELAY_MS)
    }

    override fun run() {
        // Check here if we should even start a reindex. This is much less bug-prone than
        // registering and de-registering this component as this setting changes.
        if (musicSettings.shouldBeObserving) {
            logD("MediaStore changed, starting re-index")
            musicRepository.requestIndex(true)
        }
    }

    private companion object {
        const val REINDEX_DELAY_MS = 500L
    }
}
