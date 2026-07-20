/*
 * Copyright (c) 2022 Auxio Project
 * BitmapProvider.kt is part of Auxio.
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
 
package org.oxycblt.auxio.image

import android.content.Context
import android.graphics.Bitmap
import coil3.ImageLoader
import coil3.request.Disposable
import coil3.request.ImageRequest
import coil3.size.Size
import coil3.toBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.oxycblt.musikr.Song

/**
 * A utility to provide bitmaps in a race-less manner.
 *
 * When it comes to components that load images manually as [Bitmap] instances, queued
 * [ImageRequest]s may cause a race condition that results in the incorrect image being drawn. This
 * utility resolves this by keeping track of the current request, and disposing it as soon as a new
 * request is queued or if another, competing request is newer.
 *
 * @param context [Context] required to load images.
 * @author Alexander Capehart (OxygenCobalt)
 */
class BitmapProvider
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
) {
    /**
     * An extension of [Disposable] with an additional [Target] to deliver the final [Bitmap] to.
     */
    private data class Request(val disposable: Disposable, val callback: Target)

    /** The target that will receive the requested [Bitmap]. */
    interface Target {
        /**
         * Configure the [ImageRequest.Builder] to enable [Target]-specific configuration.
         *
         * @param builder The [ImageRequest.Builder] that will be used to request the desired
         *   [Bitmap].
         * @return The same [ImageRequest.Builder] in order to easily chain configuration methods.
         */
        fun onConfigRequest(builder: ImageRequest.Builder): ImageRequest.Builder = builder

        /**
         * Called when the loading process is completed.
         *
         * @param bitmap The loaded bitmap, or null if the bitmap could not be loaded.
         */
        fun onCompleted(bitmap: Bitmap?)
    }

    private var currentRequest: Request? = null
    private var currentHandle = 0L

    /** If this provider is currently attempting to load something. */
    val isBusy: Boolean
        get() = currentRequest?.run { !disposable.isDisposed } ?: false

    /**
     * Load the Album cover [Bitmap] from a [Song].
     *
     * @param song The song to load a [Bitmap] of it's album cover from.
     * @param target The [Target] to deliver the [Bitmap] to asynchronously.
     */
    @Synchronized
    fun load(song: Song, target: Target) {
        // Increment the handle, indicating a newer request has been created
        val handle = ++currentHandle
        currentRequest?.run { disposable.dispose() }
        currentRequest = null

        val imageRequest =
            target
                .onConfigRequest(
                    ImageRequest.Builder(context)
                        .data(song.cover)
                        // dot166: No longer using Size.ORIGINAL because on Android 17
                        // (Cinnamon Bun) Size.ORIGINAL causes issues #1380, #1382 and #1385
                        // set to 640x640 as it was larger artwork that broke the notification,
                        // tested with ワールズエンド・ダンスホール (World's End Dancehall) by wowaka
                        // this patch does not affect widgets, as widgets already force ORIGINAL
                        // anyway, was originally 512x512, but @programmerlexi suggested it be
                        // 640x640 as that is the most common resolution (according to them)
                        .size(Size(width = 640, height = 640))
                )
                .target(
                    onSuccess = {
                        synchronized(this) {
                            if (currentHandle == handle) {
                                target.onCompleted(it.toBitmap())
                            }
                        }
                    },
                    onError = {
                        synchronized(this) {
                            if (currentHandle == handle) {
                                target.onCompleted(null)
                            }
                        }
                    },
                )
        currentRequest = Request(imageLoader.enqueue(imageRequest.build()), target)
    }

    /** Release this instance, cancelling any currently running operations. */
    @Synchronized
    fun release() {
        ++currentHandle
        currentRequest?.run { disposable.dispose() }
        currentRequest = null
    }
}
