/*
 * Copyright (c) 2025 Auxio Project
 * Logger.kt is part of Auxio.
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
 
package org.oxycblt.musikr.log

import android.util.Log

interface Logger {
    fun v(vararg msgs: Any)

    fun d(vararg msgs: Any)

    fun w(vararg msgs: Any)

    fun e(vararg msgs: Any)

    fun primary(tag: String): Logger

    fun secondary(tag: String): Logger

    companion object {
        fun root(): Logger = LoggerImpl("mskr", null)
    }
}

private class LoggerImpl(private val primaryTag: String, private val secondaryTag: String?) :
    Logger {
    override fun v(vararg msgs: Any) {
        Log.v(primaryTag, "[$secondaryTag] ${msgs.joinToString(" ")}")
    }

    override fun d(vararg msgs: Any) {
        Log.d(primaryTag, "[$secondaryTag] ${msgs.joinToString(" ")}")
    }

    override fun w(vararg msgs: Any) {
        Log.w(primaryTag, "[$secondaryTag] ${msgs.joinToString(" ")}")
    }

    override fun e(vararg msgs: Any) {
        Log.e(primaryTag, "[$secondaryTag] ${msgs.joinToString(" ")}")
    }

    override fun primary(tag: String) = LoggerImpl("${primaryTag}.${tag}", secondaryTag)

    override fun secondary(tag: String) =
        LoggerImpl(primaryTag, secondaryTag?.let { "$it.$tag" } ?: tag)
}
