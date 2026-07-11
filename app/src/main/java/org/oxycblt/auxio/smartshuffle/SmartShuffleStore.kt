/*
 * Copyright (c) 2026 Auxio Project
 * SmartShuffleStore.kt is part of Auxio.
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

package org.oxycblt.auxio.smartshuffle

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import org.json.JSONObject
import timber.log.Timber as L

@Singleton
class SmartShuffleStore @Inject constructor(@ApplicationContext context: Context) {
    private val file = File(context.filesDir, FILE_NAME)

    @Synchronized
    fun load(): SmartShuffleModel {
        if (!file.exists()) {
            return SmartShuffleModel()
        }

        return try {
            val root = JSONObject(file.readText())
            val featureScores = mutableMapOf<String, Int>()
            val featureObject = root.optJSONObject("features") ?: JSONObject()
            for (key in featureObject.keys()) {
                featureScores[key] = featureObject.optInt(key)
            }

            val songStats = mutableMapOf<String, SongStats>()
            val songsObject = root.optJSONObject("songs") ?: JSONObject()
            for (key in songsObject.keys()) {
                val statsObject = songsObject.optJSONObject(key) ?: continue
                songStats[key] =
                    SongStats(
                        seen = statsObject.optInt("seen"),
                        engagement = statsObject.optInt("engagement"),
                        skips = statsObject.optInt("skips"),
                        listens = statsObject.optInt("listens"),
                        skipStreak = statsObject.optInt("skipStreak"),
                        lastSkipMs = statsObject.optLong("lastSkipMs"),
                        lastListenMs = statsObject.optLong("lastListenMs"),
                        lastSeenMs = statsObject.optLong("lastSeenMs"),
                        undesirable = statsObject.optBoolean("undesirable"),
                        forgiven = statsObject.optBoolean("forgiven"),
                    )
            }

            SmartShuffleModel(
                featureScores = featureScores,
                songStats = songStats,
                songsSinceLastStrongLike = root.optInt("songsSinceLastStrongLike"),
            )
        } catch (e: Exception) {
            L.w(e, "Unable to load smart shuffle model")
            SmartShuffleModel()
        }
    }

    @Synchronized
    fun save(model: SmartShuffleModel) {
        val root = JSONObject()
        val featureObject = JSONObject()
        for ((key, value) in model.featureScores) {
            featureObject.put(key, value)
        }
        root.put("features", featureObject)
        root.put("songsSinceLastStrongLike", model.songsSinceLastStrongLike)

        val songsObject = JSONObject()
        for ((key, stats) in model.songStats) {
            songsObject.put(
                key,
                JSONObject()
                    .put("seen", stats.seen)
                    .put("engagement", stats.engagement)
                    .put("skips", stats.skips)
                    .put("listens", stats.listens)
                    .put("skipStreak", stats.skipStreak)
                    .put("lastSkipMs", stats.lastSkipMs)
                    .put("lastListenMs", stats.lastListenMs)
                    .put("lastSeenMs", stats.lastSeenMs)
                    .put("undesirable", stats.undesirable)
                    .put("forgiven", stats.forgiven),
            )
        }
        root.put("songs", songsObject)

        // Atomic replace avoids a truncated file if the process dies mid-write.
        val tmp = File(file.parentFile, "$FILE_NAME.tmp")
        tmp.writeText(root.toString())
        if (!tmp.renameTo(file)) {
            tmp.copyTo(file, overwrite = true)
            tmp.delete()
        }
    }

    private companion object {
        const val FILE_NAME = "smart_shuffle_model.json"
    }
}
