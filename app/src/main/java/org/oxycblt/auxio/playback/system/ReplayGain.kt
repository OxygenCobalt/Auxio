package org.oxycblt.auxio.playback.system

import androidx.core.math.MathUtils
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.flac.VorbisComment
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame
import kotlin.math.pow

const val RG_TRACK = "REPLAYGAIN_TRACK_GAIN"
const val RG_ALBUM = "REPLAYGAIN_ALBUM_GAIN"
const val R128_TRACK = "R128_TRACK_GAIN"
const val R128_ALBUM = "R128_ALBUM_GAIN"

val replayGainTags = arrayOf(
    RG_TRACK,
    RG_ALBUM,
    R128_ALBUM,
    R128_TRACK
)

data class Gain(val track: Float, val album: Float)

/**
 * Calculates the rough volume adjustment for [Metadata] with ReplayGain tags.
 * This is based off Vanilla Music's implementation.
 */
fun calculateReplayGain(metadata: Metadata): Float {
    val gain = parseReplayGain(metadata)

    // Currently we consider both the album and the track gain. One might want to add
    // configuration to handle more cases.
    var adjust = 0f

    if (gain != null) {
        adjust = if (gain.album != 0f) {
            gain.album
        } else {
            gain.track
        }
    }

    // Final adjustment along the volume curve.
    // Ensure this is clamped to 0 or 1 so that it can be used as a volume.
    return MathUtils.clamp((10f.pow((adjust / 20f))), 0f, 1f)
}

private fun parseReplayGain(metadata: Metadata): Gain? {
    data class GainTag(val key: String, val value: Float)

    var trackGain = 0f
    var albumGain = 0f
    var found = false

    val tags = mutableListOf<GainTag>()

    for (i in 0 until metadata.length()) {
        val entry = metadata.get(i)

        // Sometimes the ReplayGain keys will be lowercase, so make them uppercase.
        if (entry is TextInformationFrame && entry.description?.uppercase() in replayGainTags) {
            tags.add(GainTag(entry.description!!.uppercase(), parseReplayGainFloat(entry.value)))
            continue
        }

        if (entry is VorbisComment && entry.key.uppercase() in replayGainTags) {
            tags.add(GainTag(entry.key.uppercase(), parseReplayGainFloat(entry.value)))
        }
    }

    // Case 1: Normal ReplayGain, most commonly found on MPEG files.
    tags.findLast { tag -> tag.key == RG_TRACK }?.let { tag ->
        trackGain = tag.value
        found = true
    }

    tags.findLast { tag -> tag.key == RG_ALBUM }?.let { tag ->
        albumGain = tag.value
        found = true
    }

    // Case 2: R128 ReplayGain, most commonly found on FLAC files.
    // While technically there is the R128 base gain in Opus files, ExoPlayer doesn't
    // have metadata parsing functionality for those, so we just ignore it.
    tags.findLast { tag -> tag.key == R128_TRACK }?.let { tag ->
        trackGain += tag.value / 256f
        found = true
    }

    tags.findLast { tag -> tag.key == R128_ALBUM }?.let { tag ->
        albumGain += tag.value / 256f
        found = true
    }

    return if (found) {
        Gain(trackGain, albumGain)
    } else {
        null
    }
}

private fun parseReplayGainFloat(raw: String): Float {
    return try {
        raw.replace(Regex("[^0-9.-]"), "").toFloat()
    } catch (e: Exception) {
        0f
    }
}
