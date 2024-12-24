/*
 * Copyright (c) 2021 Auxio Project
 * Keyers.kt is part of Auxio.
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
 
package org.oxycblt.auxio.image.coil

import coil3.key.Keyer
import coil3.request.Options
import javax.inject.Inject
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.cover.CoverCollection

class CoverKeyer @Inject constructor() : Keyer<Cover> {
    override fun key(data: Cover, options: Options) = "${data.id}&${options.size}"
}

class CoverCollectionKeyer @Inject constructor() : Keyer<CoverCollection> {
    override fun key(data: CoverCollection, options: Options) =
        "multi:${data.hashCode()}&${options.size}"
}
