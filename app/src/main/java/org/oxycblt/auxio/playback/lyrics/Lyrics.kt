/*
 * Copyright (c) 2026 Auxio Project
 * Lyrics.kt is part of Auxio.
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

package org.oxycblt.auxio.playback.lyrics

/**
 * A single lyrics line. [timestampMs] is null for unsynced (plain) lyrics.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
data class LyricsLine(val timestampMs: Long?, val text: String)

/**
 * Parsed lyrics for a song, as obtained from [Song.lyrics][org.oxycblt.musikr.Song.lyrics] via
 * [LrcParser].
 *
 * @param lines The individual lines to display, in order. Timestamps are present (and sorted)
 *   when the source could be synced, absent otherwise.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
data class Lyrics(val lines: List<LyricsLine>) {
    /** True when line-level synced timestamps are available. */
    val synced: Boolean
        get() = lines.isNotEmpty() && lines[0].timestampMs != null
}

/**
 * Parser for the raw lyrics text stored in [Song.lyrics][org.oxycblt.musikr.Song.lyrics].
 *
 * The source may be plain text (ID3v2 USLT, Xiph LYRICS/UNSYNCEDLYRICS, the MP4 lyrics atom) or
 * LRC-formatted text (a tagger that writes LRC markup directly into one of those fields, or a
 * binary ID3v2 SYLT frame that `JMetadataBuilder` already rendered as LRC on the native side) --
 * either way, this single parser handles it, since LRC-formatted text is just plain text with a
 * recognizable `[mm:ss.xx]` prefix on each synced line.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
object LrcParser {
    private val TIMESTAMP_PREFIX = Regex("""^\[(\d{1,3}):(\d{1,2})(?:[.:](\d{1,3}))?]""")
    private val WORD_TAG = Regex("""<\d{1,3}:\d{1,2}(?:[.:]\d{1,3})?>""")
    // Requires a "key:" before the value -- without it, this also matched plain bracketed
    // section headers some lyrics sources use (e.g. "[Chorus]", "[Verse 1]"), silently dropping
    // them as if they were an LRC metadata tag like "[ar:Some Artist]".
    private val METADATA_TAG = Regex("""^\[[a-zA-Z#][a-zA-Z0-9#]*:[^\[\]]*]\s*$""")

    /**
     * Parse [raw] lyrics text into a [Lyrics] instance.
     *
     * @param raw The raw lyrics text, as obtained from [Song.lyrics][org.oxycblt.musikr.Song.lyrics].
     * @return The parsed [Lyrics], or null if [raw] is null/blank.
     */
    fun parse(raw: String?): Lyrics? {
        if (raw.isNullOrBlank()) {
            return null
        }

        val syncedLines = mutableListOf<LyricsLine>()
        val plainLines = mutableListOf<String>()

        for (rawLine in raw.lineSequence()) {
            val line = rawLine.trim()
            if (line.isEmpty()) {
                continue
            }

            // Collect all leading timestamps (a line can carry more than one, e.g. a repeated
            // chorus: "[00:12.00][01:15.00]text").
            val timestamps = mutableListOf<Long>()
            var rest = line
            while (true) {
                val match = TIMESTAMP_PREFIX.find(rest) ?: break
                val (min, sec, frac) = match.destructured
                val fracMs =
                    when (frac.length) {
                        0 -> 0L
                        1 -> frac.toLong() * 100
                        2 -> frac.toLong() * 10
                        else -> frac.take(3).toLong()
                    }
                timestamps += min.toLong() * 60_000 + sec.toLong() * 1_000 + fracMs
                rest = rest.substring(match.value.length)
            }

            if (timestamps.isEmpty()) {
                // No leading timestamp: an LRC metadata tag (e.g. "[ar:Some Artist]") is
                // dropped, anything else is kept as plain fallback text.
                if (!METADATA_TAG.matches(line)) {
                    plainLines += line
                }
                continue
            }

            val text = WORD_TAG.replace(rest, "").trim()
            for (timestampMs in timestamps) {
                syncedLines += LyricsLine(timestampMs, text)
            }
        }

        return when {
            syncedLines.isNotEmpty() -> Lyrics(syncedLines.sortedBy { it.timestampMs })
            plainLines.isNotEmpty() -> Lyrics(plainLines.map { LyricsLine(null, it) })
            else -> null
        }
    }
}

/**
 * Returns the index of the line currently being sung at [positionMs]: the last line whose
 * timestamp is <= the position. Returns -1 before the first line, when [lines] is empty, or when
 * [lines] isn't synced.
 *
 * @param lines Must be sorted by timestamp, as produced by [LrcParser].
 */
fun currentLyricsLineIndex(lines: List<LyricsLine>, positionMs: Long): Int {
    var low = 0
    var high = lines.size - 1
    var result = -1
    while (low <= high) {
        val mid = (low + high) ushr 1
        val timestampMs = lines[mid].timestampMs
        if (timestampMs != null && timestampMs <= positionMs) {
            result = mid
            low = mid + 1
        } else {
            high = mid - 1
        }
    }
    return result
}
