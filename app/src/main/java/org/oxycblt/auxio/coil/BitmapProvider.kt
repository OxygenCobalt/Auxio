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
 
package org.oxycblt.auxio.coil

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import coil.size.Size
import org.oxycblt.auxio.music.Song

/**
 * A utility to provide bitmaps in a manner less prone to race conditions.
 *
 * Pretty much each service component needs to load bitmaps of some kind, but doing a blind image
 * request with some target callbacks could result in overlapping requests causing unrelated
 * updates. This class (to an extent) resolves this by keeping track of the current request and
 * disposing of it every time a new request is created. This greatly reduces the surface for race
 * conditions save the case of instruction-by-instruction data races, which are effectively
 * impossible to solve.
 *
 * @author OxygenCobalt
 */
class BitmapProvider(private val context: Context) {
    private var currentRequest: Request? = null

    val isBusy: Boolean
        get() = currentRequest?.run { !disposable.isDisposed } ?: false

    /**
     * Load a bitmap from [song]. [target] should be a new object, not a reference to an existing
     * callback.
     */
    fun load(song: Song, target: Target) {
        currentRequest?.run { disposable.dispose() }
        currentRequest = null

        val request =
            target.setupRequest(
                ImageRequest.Builder(context)
                    .data(song)
                    .size(Size.ORIGINAL)
                    .target(
                        onSuccess = { target.onCompleted(it.toBitmap()) },
                        onError = { target.onCompleted(null) })
                    .transformations(SquareFrameTransform.INSTANCE))

        currentRequest = Request(context.imageLoader.enqueue(request.build()), target)
    }

    /**
     * Release this instance, canceling all image load jobs. This should be ran when the object is
     * no longer used.
     */
    fun release() {
        currentRequest?.run { disposable.dispose() }
        currentRequest = null
    }

    private data class Request(val disposable: Disposable, val callback: Target)

    interface Target {
        fun setupRequest(builder: ImageRequest.Builder): ImageRequest.Builder = builder
        fun onCompleted(bitmap: Bitmap?)
    }
}
