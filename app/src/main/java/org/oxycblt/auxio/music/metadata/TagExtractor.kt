/*
 * Copyright (c) 2023 Auxio Project
 * TagExtractor.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.metadata

import com.google.android.exoplayer2.MetadataRetriever
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.yield
import org.oxycblt.auxio.music.device.RawSong

/**
 * The extractor that leverages ExoPlayer's [MetadataRetriever] API to parse metadata. This is the
 * last step in the music extraction process and is mostly responsible for papering over the bad
 * metadata that other extractors produce.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface TagExtractor {
    /**
     * Extract the metadata of songs from [incompleteSongs] and send them to [completeSongs]. Will
     * terminate as soon as [incompleteSongs] is closed.
     *
     * @param incompleteSongs A [Channel] of incomplete songs to process.
     * @param completeSongs A [Channel] to send completed songs to.
     */
    suspend fun consume(incompleteSongs: Channel<RawSong>, completeSongs: Channel<RawSong>)
}

class TagExtractorImpl @Inject constructor(private val tagWorkerFactory: TagWorker.Factory) :
    TagExtractor {
    override suspend fun consume(
        incompleteSongs: Channel<RawSong>,
        completeSongs: Channel<RawSong>
    ) {
        // We can parallelize MetadataRetriever Futures to work around it's speed issues,
        // producing similar throughput's to other kinds of manual metadata extraction.
        val tagWorkerPool: Array<TagWorker?> = arrayOfNulls(TASK_CAPACITY)

        for (incompleteRawSong in incompleteSongs) {
            spin@ while (true) {
                for (i in tagWorkerPool.indices) {
                    val worker = tagWorkerPool[i]
                    if (worker != null) {
                        val completeRawSong = worker.poll()
                        if (completeRawSong != null) {
                            completeSongs.send(completeRawSong)
                            yield()
                        } else {
                            continue
                        }
                    }
                    tagWorkerPool[i] = tagWorkerFactory.create(incompleteRawSong)
                    break@spin
                }
            }
        }

        do {
            var ongoingTasks = false
            for (i in tagWorkerPool.indices) {
                val task = tagWorkerPool[i]
                if (task != null) {
                    val completeRawSong = task.poll()
                    if (completeRawSong != null) {
                        completeSongs.send(completeRawSong)
                        tagWorkerPool[i] = null
                        yield()
                    } else {
                        ongoingTasks = true
                    }
                }
            }
        } while (ongoingTasks)

        completeSongs.close()
    }

    private companion object {
        const val TASK_CAPACITY = 8
    }
}
