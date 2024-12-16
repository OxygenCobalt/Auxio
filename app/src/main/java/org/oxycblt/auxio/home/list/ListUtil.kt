/*
 * Copyright (c) 2024 Auxio Project
 * ListUtil.kt is part of Auxio.
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
 
package org.oxycblt.auxio.home.list

import androidx.core.text.isDigitsOnly
import org.oxycblt.musikr.tag.Name

fun Name.thumb() =
    when (this) {
        is Name.Known ->
            tokens.firstOrNull()?.let {
                if (it.value.isDigitsOnly()) "#" else it.value
            }
        is Name.Unknown -> "?"
    }
