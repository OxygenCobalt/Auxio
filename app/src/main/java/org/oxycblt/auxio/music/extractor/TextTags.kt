/*
 * Copyright (c) 2023 Auxio Project
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
 
package org.oxycblt.auxio.music.extractor

import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.id3.InternalFrame
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame
import com.google.android.exoplayer2.metadata.vorbis.VorbisComment
import org.oxycblt.auxio.music.parsing.correctWhitespace

/**
 * Processing wrapper for [Metadata] that allows organized access to text-based audio tags.
 * @param metadata The [Metadata] to wrap.
 * @author Alexander Capehart (OxygenCobalt)
 */
class TextTags(metadata: Metadata) {
    private val _id3v2 = mutableMapOf<String, List<String>>()
    /** The ID3v2 text identification frames found in the file. Can have more than one value. */
    val id3v2: Map<String, List<String>>
        get() = _id3v2

    private val _vorbis = mutableMapOf<String, MutableList<String>>()
    /** The vorbis comments found in the file. Can have more than one value. */
    val vorbis: Map<String, List<String>>
        get() = _vorbis

    init {
        for (i in 0 until metadata.length()) {
            when (val tag = metadata[i]) {
                is TextInformationFrame -> {
                    // Map TXXX frames differently so we can specifically index by their
                    // descriptions.
                    val id =
                        tag.description?.let { "TXXX:${it.sanitize().lowercase()}" }
                            ?: tag.id.sanitize()
                    val values = tag.values.map { it.sanitize() }.correctWhitespace()
                    if (values.isNotEmpty()) {
                        _id3v2[id] = values
                    }
                }
                is InternalFrame -> {
                    // Most MP4 metadata atoms map to ID3v2 text frames, except for the ---- atom,
                    // which has it's own frame. Map this to TXXX, it's rough ID3v2 equivalent.
                    val id = "TXXX:${tag.description.sanitize().lowercase()}"
                    val value = tag.text
                    if (value.isNotEmpty()) {
                        _id3v2[id] = listOf(value)
                    }
                }
                is VorbisComment -> {
                    // Vorbis comment keys can be in any case, make them uppercase for simplicity.
                    val id = tag.key.sanitize().lowercase()
                    if (id == "metadata_block_picture") {
                        // Picture, we don't care about these
                        continue
                    }
                    val value = tag.value.sanitize().correctWhitespace()
                    if (value != null) {
                        _vorbis.getOrPut(id) { mutableListOf() }.add(value)
                    }
                }
            }
        }
    }

    /**
     * Copies and sanitizes a possibly invalid string outputted from ExoPlayer.
     * @return A new string allocated in a memory-safe manner with any UTF-8 errors replaced with
     * the Unicode replacement byte sequence.
     */
    private fun String.sanitize() = String(encodeToByteArray())
}
