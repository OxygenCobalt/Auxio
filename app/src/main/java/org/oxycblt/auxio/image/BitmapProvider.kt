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
 
package org.oxycblt.auxio.image

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import coil.size.Size
import org.oxycblt.auxio.image.extractor.SquareFrameTransform
import org.oxycblt.auxio.music.Song

/**
 * A utility to provide bitmaps in a race-less manner.
 *
 * When it comes to components that load images manually as [Bitmap] instances, queued
 * [ImageRequest]s may cause a race condition that results in the incorrect image being
 * drawn. This utility resolves this by keeping track of the current request, and disposing
 * it as soon as a new request is queued or if another, competing request is newer.
 *
 * @param context [Context] required to load images.
 * @author Alexander Capehart (OxygenCobalt)
 */
class BitmapProvider(private val context: Context) {
    /**
     * An extension of [Disposable] with an additional [Target] to deliver the final [Bitmap] to.
     */
    private data class Request(val disposable: Disposable, val callback: Target)

    /**
     * The target that will recieve the requested [Bitmap].
     */
    interface Target {
        /**
         * Configure the [ImageRequest.Builder] to enable [Target]-specific configuration.
         * @param builder The [ImageRequest.Builder] that will be used to request the
         * desired [Bitmap].
         * @return The same [ImageRequest.Builder] in order to easily chain configuration
         * methods.
         */
        fun onConfigRequest(builder: ImageRequest.Builder): ImageRequest.Builder = builder

        /**
         * Called when the loading process is completed.
         * @param bitmap The loaded bitmap, or null if the bitmap could not be loaded.
         */
        fun onCompleted(bitmap: Bitmap?)
    }

    private var currentRequest: Request? = null
    // Keeps track of the current image request we are on. If the stored handle in an
    // ImageRequest is still equal to this, it means that the request has not been
    // superceded by a new one.
    private var currentHandle = 0L
    private var handleLock = Any()

    /** If this provider is currently attempting to load something. */
    val isBusy: Boolean
        get() = currentRequest?.run { !disposable.isDisposed } ?: false

    /**
     * Load the Album cover [Bitmap] from a [Song].
     * @param song The song to load a [Bitmap] of it's album cover from.
     * @param target The [Target] to deliver the [Bitmap] to asynchronously.
     */
    @Synchronized
    fun load(song: Song, target: Target) {
        // Increment the handle, indicating a newer request being created.
        val handle = synchronized(handleLock) { ++currentHandle }
        // Be even safer and cancel the previous request.
        currentRequest?.run { disposable.dispose() }
        currentRequest = null

        val request =
            target.onConfigRequest(
                ImageRequest.Builder(context)
                    .data(song)
                    // Use ORIGINAL sizing, as we are not loading into any View-like component.
                    .size(Size.ORIGINAL)
                    .transformations(SquareFrameTransform.INSTANCE))
                    // Override the target in order to deliver the bitmap to the given
                    // callback.
                    .target(
                        onSuccess = {
                            synchronized(handleLock) {
                                if (currentHandle == handle) {
                                    // Still the active request, deliver it to the target.
                                    target.onCompleted(it.toBitmap())
                                }
                            }
                        },
                        onError = {
                            synchronized(handleLock) {
                                if (currentHandle == handle) {
                                    // Still the active request, deliver it to the target.
                                    target.onCompleted(null)
                                }
                            }
                        })

        currentRequest = Request(context.imageLoader.enqueue(request.build()), target)
    }

    /**
     * Release this instance. Run this when the object is no longer used to prevent
     * stray loading callbacks.
     */
    @Synchronized
    fun release() {
        synchronized(handleLock) { ++currentHandle }
        currentRequest?.run { disposable.dispose() }
        currentRequest = null
    }
}
