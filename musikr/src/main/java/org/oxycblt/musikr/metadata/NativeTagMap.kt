/*
 * Copyright (c) 2025 Auxio Project
 * NativeTagMap.kt is part of Auxio.
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
 
package org.oxycblt.musikr.metadata

import org.oxycblt.musikr.util.correctWhitespace

internal class NativeTagMap {
    private val map = mutableMapOf<String, List<String>>()

    fun addID(id: String, value: String) {
        addID(id, listOf(value))
    }

    fun addID(id: String, values: List<String>) {
        if (values.isEmpty()) {
            return
        }
        val correctedValues = values.mapNotNull { it.correctWhitespace() }
        if (correctedValues.isEmpty()) {
            return
        }
        map[id] = correctedValues
    }

    fun addCustom(description: String, value: String) {
        addCustom(description, listOf(value))
    }

    fun addCustom(description: String, values: List<String>) {
        if (values.isEmpty()) {
            return
        }
        val correctedValues = values.mapNotNull { it.correctWhitespace() }
        if (correctedValues.isEmpty()) {
            return
        }
        map[description.uppercase()] = correctedValues
    }

    fun addCombined(id: String, description: String, value: String) {
        addCombined(id, description, listOf(value))
    }

    fun addCombined(id: String, description: String, values: List<String>) {
        if (values.isEmpty()) {
            return
        }
        val correctedValues = values.mapNotNull { it.correctWhitespace() }
        if (correctedValues.isEmpty()) {
            return
        }
        map["$id:${description.uppercase()}"] = correctedValues
    }

    fun getObject(): Map<String, List<String>> {
        return map
    }
}
