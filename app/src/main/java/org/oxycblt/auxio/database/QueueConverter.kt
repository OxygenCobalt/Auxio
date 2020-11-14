package org.oxycblt.auxio.database

import org.json.JSONArray
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song

object QueueConverter {
    fun fromString(arrayString: String): MutableList<Song> {
        val jsonArray = JSONArray(arrayString)
        val queue = mutableListOf<Song>()
        val musicStore = MusicStore.getInstance()

        for (i in 0 until jsonArray.length()) {
            val id = jsonArray.getLong(i)
            musicStore.songs.find { it.id == id }?.let {
                queue.add(it)
            }
        }

        return queue
    }

    fun fromQueue(queueIds: List<Long>): String {
        val jsonArray = JSONArray()
        queueIds.forEach {
            jsonArray.put(it)
        }
        return jsonArray.toString(0)
    }
}
