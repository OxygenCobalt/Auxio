/*
 * Copyright (c) 2024 Auxio Project
 * RateLimitedContentResolver.kt is part of Auxio.
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
 
package org.oxycblt.musikr.fs.device

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import kotlin.math.min
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit

/**
 * A rate-limited wrapper around ContentResolver that uses a token bucket algorithm to limit the
 * rate of queries.
 *
 * @param contentResolver The underlying ContentResolver to wrap
 * @param maxTokens The maximum number of tokens in the bucket (burst capacity)
 * @param refillRate The rate at which tokens are refilled per second
 * @param maxConcurrency The maximum number of concurrent queries allowed
 */
internal class RateLimitedContentResolver(
    private val contentResolver: ContentResolver,
    private val maxTokens: Int = 1000,
    private val refillRate: Double = 1000.0, // tokens per second
    private val maxConcurrency: Int = 10
) {
    private val tokenBucket = TokenBucket(maxTokens, refillRate)
    private val concurrencySemaphore = Semaphore(maxConcurrency)

    /**
     * A rate-limited version of ContentResolver.query This is a suspend function that should be
     * called from a coroutine
     */
    suspend fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? =
        concurrencySemaphore.withPermit {
            tokenBucket.acquire()
            contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        }

    /**
     * A shortcut for querying the database with automatic resource management. Rate-limited version
     * of the useQuery extension function. This is a suspend function that should be called from a
     * coroutine
     */
    suspend inline fun <reified R> useQuery(
        uri: Uri,
        projection: Array<out String>,
        selector: String? = null,
        args: Array<String>? = null,
        sortOrder: String? = null,
        crossinline block: suspend (Cursor) -> R
    ): R =
        concurrencySemaphore.withPermit {
            tokenBucket.acquire()
            contentResolver.useQuery(uri, projection, selector, args, sortOrder) { cursor ->
                block(cursor)
            }
        }

    /** Token bucket implementation for rate limiting */
    internal class TokenBucket(
        private val maxTokens: Int,
        private val refillRate: Double // tokens per second
    ) {
        private var tokens: Double = maxTokens.toDouble()
        private var lastRefillTime: Long = System.currentTimeMillis()
        private val mutex = Mutex()
        private val tokenWaitTime = (1000.0 / refillRate).toLong() // milliseconds per token

        suspend fun acquire() {
            mutex.withLock {
                refill()

                // Wait until a token is available
                while (tokens < 1.0) {
                    val waitTime = ((1.0 - tokens) * tokenWaitTime).toLong()
                    delay(waitTime)
                    refill()
                }

                tokens -= 1.0
            }
        }

        private fun refill() {
            val now = System.currentTimeMillis()
            val timePassed = now - lastRefillTime
            val tokensToAdd = (timePassed / 1000.0) * refillRate

            if (tokensToAdd > 0) {
                tokens = min(tokens + tokensToAdd, maxTokens.toDouble())
                lastRefillTime = now
            }
        }
    }
}
