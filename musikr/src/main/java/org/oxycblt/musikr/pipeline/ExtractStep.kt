/*
 * Copyright (c) 2024 Auxio Project
 * ExtractStep.kt is part of Auxio.
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
 
package org.oxycblt.musikr.pipeline

import android.content.Context
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.oxycblt.musikr.Storage
import org.oxycblt.musikr.cache.Cache
import org.oxycblt.musikr.cache.CacheResult
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.cover.MutableCovers
import org.oxycblt.musikr.fs.DeviceFile
import org.oxycblt.musikr.metadata.Metadata
import org.oxycblt.musikr.metadata.MetadataExtractor
import org.oxycblt.musikr.metadata.Properties
import org.oxycblt.musikr.playlist.PlaylistFile
import org.oxycblt.musikr.tag.parse.ParsedTags
import org.oxycblt.musikr.tag.parse.TagParser

internal interface ExtractStep {
    suspend fun extract(
        explored: ReceiveChannel<ExploreNode>,
        extracted: SendChannel<ExtractedMusic>
    ): Deferred<Result<Unit>>

    companion object {
        fun from(context: Context, storage: Storage): ExtractStep =
            ExtractStepImpl(
                context,
                MetadataExtractor.new(),
                TagParser.new(),
                storage.cache,
                storage.storedCovers)
    }
}

private class ExtractStepImpl(
    private val context: Context,
    private val metadataExtractor: MetadataExtractor,
    private val tagParser: TagParser,
    private val cacheFactory: Cache.Factory,
    private val storedCovers: MutableCovers
) : ExtractStep {
    override suspend fun extract(
        explored: ReceiveChannel<ExploreNode>,
        extracted: SendChannel<ExtractedMusic>
    ) = coroutineScope {
        async {
            try {
                val cache = cacheFactory.open()
                val addingMs = System.currentTimeMillis()

                val read = Channel<DeviceFile>(Channel.UNLIMITED)
                val open = Channel<DeviceFile>(Channel.UNLIMITED)
                val extract = Channel<FileWith<ParcelFileDescriptor>>(Channel.UNLIMITED)
                // Covers are pretty big, so cap the amount of parsed metadata in-memory to at most
                // 8 to minimize GCs.
                val parse = Channel<FileWith<Metadata>>(8)
                val write = Channel<RawSong>(Channel.UNLIMITED)

                val exploreAssortTask = async {
                    try {
                        for (node in explored) {
                            when (node) {
                                is ExploreNode.Audio -> read.send(node.file)
                                is ExploreNode.Playlist ->
                                    extracted.send(ExtractedMusic.Valid.Playlist(node.file))
                            }
                        }
                        read.close()
                        Result.success(Unit)
                    } catch (e: Exception) {
                        read.close(e)
                        Result.failure(e)
                    }
                }

                val readTasks =
                    List(8) {
                        async {
                            try {
                                for (file in read) {
                                    when (val result = cache.read(file, storedCovers)) {
                                        is CacheResult.Hit ->
                                            extracted.send(ExtractedMusic.Valid.Song(result.song))
                                        is CacheResult.Miss -> open.send(result.file)
                                    }
                                }
                                Result.success(Unit)
                            } catch (e: Exception) {
                                Result.failure(e)
                            }
                        }
                    }

                val readTask = async {
                    try {
                        readTasks.awaitAll().forEach { it.getOrThrow() }
                        open.close()
                        Result.success(Unit)
                    } catch (e: Exception) {
                        open.close(e)
                        Result.failure(e)
                    }
                }

                val openTask = async {
                    try {
                        for (file in open) {
                            withContext(Dispatchers.IO) {
                                val fd = context.contentResolver.openFileDescriptor(file.uri, "r")
                                if (fd != null) {
                                    extract.send(FileWith(file, fd))
                                } else {
                                    extracted.send(ExtractedMusic.Invalid)
                                }
                            }
                        }
                        extract.close()
                        Result.success(Unit)
                    } catch (e: Exception) {
                        extract.close(e)
                        Result.failure(e)
                    }
                }

                val extractTask = async {
                    try {
                        for (fileWith in extract) {
                            val metadata = metadataExtractor.extract(fileWith.file, fileWith.with)
                            if (metadata != null) {
                                parse.send(FileWith(fileWith.file, metadata))
                            } else {
                                extracted.send(ExtractedMusic.Invalid)
                            }
                            fileWith.with.close()
                        }
                        parse.close()
                        Result.success(Unit)
                    } catch (e: Exception) {
                        parse.close(e)
                        Result.failure(e)
                    }
                }

                val parseTask = async {
                    try {
                        for (fileWith in parse) {
                            val tags = tagParser.parse(fileWith.file, fileWith.with)
                            val cover = fileWith.with.cover?.let { storedCovers.write(it) }
                            write.send(
                                RawSong(
                                    fileWith.file, fileWith.with.properties, tags, cover, addingMs))
                        }
                        write.close()
                        Result.success(Unit)
                    } catch (e: Exception) {
                        write.close(e)
                        Result.failure(e)
                    }
                }

                val writeTasks =
                    List(8) {
                        async {
                            try {
                                for (song in write) {
                                    cache.write(song)
                                    extracted.send(ExtractedMusic.Valid.Song(song))
                                }
                                Result.success(Unit)
                            } catch (e: Exception) {
                                Result.failure(e)
                            }
                        }
                    }

                exploreAssortTask.await().getOrThrow()
                readTask.await().getOrThrow()
                openTask.await().getOrThrow()
                extractTask.await().getOrThrow()
                parseTask.await().getOrThrow()
                writeTasks.awaitAll().forEach { it.getOrThrow() }
                cache.finalize()

                extracted.close()
                Result.success(Unit)
            } catch (e: Exception) {
                extracted.close(e)
                Result.failure(e)
            }
        }
    }

    private data class FileWith<T>(val file: DeviceFile, val with: T)
}

internal data class RawSong(
    val file: DeviceFile,
    val properties: Properties,
    val tags: ParsedTags,
    val cover: Cover?,
    val addedMs: Long
)

internal sealed interface ExtractedMusic {
    sealed interface Valid : ExtractedMusic {
        data class Song(val song: RawSong) : Valid

        data class Playlist(val file: PlaylistFile) : Valid
    }

    data object Invalid : ExtractedMusic
}
