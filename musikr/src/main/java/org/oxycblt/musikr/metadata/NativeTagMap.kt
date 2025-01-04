package org.oxycblt.musikr.metadata

import org.oxycblt.musikr.util.correctWhitespace

class NativeTagMap {
    private val map = mutableMapOf<String, List<String>>()

    fun addID(id: String, value: String) {
        addID(id, listOf(value))
    }

    fun addID(id: String, values: List<String>) {
        map[id] = values.mapNotNull { it.correctWhitespace() }
    }

    fun addCustom(description: String, value: String) {
        addCustom(description, listOf(value))
    }

    fun addCustom(description: String, values: List<String>) {
        map[description.uppercase()] = values.mapNotNull { it.correctWhitespace() }
    }

    fun addCombined(id: String, description: String, value: String) {
        addCombined(id, description, listOf(value))
    }

    fun addCombined(id: String, description: String, values: List<String>) {
        map["$id:${description.uppercase()}"] = values.mapNotNull { it.correctWhitespace() }
    }

    fun getObject(): Map<String, List<String>> {
        return map
    }
}