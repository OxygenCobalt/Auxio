/*
 * Copyright (c) 2021 Auxio Project
 * ExcludedLocationAdapter.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.locations

import android.view.ViewGroup
import org.oxycblt.auxio.databinding.ItemMusicLocationBinding
import org.oxycblt.auxio.util.inflater
import org.oxycblt.musikr.fs.Location

/**
 * [LocationAdapter] that manages a list of [Location.Unopened] excluded directory instances.
 *
 * @param listener A [LocationAdapter.Listener] to bind interactions to.
 * @author Alexander Capehart (OxygenCobalt)
 */
class ExcludedLocationAdapter(listener: LocationAdapter.Listener<Location.Unopened>) :
    LocationAdapter<Location.Unopened>(listener) {
    override fun createViewHolder(parent: ViewGroup): LocationViewHolder<Location.Unopened> =
        ExcludedLocationViewHolder.from(parent)
}

/**
 * A [LocationViewHolder] that displays a [Location.Unopened]. Use [from] to create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class ExcludedLocationViewHolder private constructor(binding: ItemMusicLocationBinding) :
    LocationViewHolder<Location.Unopened>(binding) {

    companion object {
        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: ViewGroup) =
            ExcludedLocationViewHolder(
                ItemMusicLocationBinding.inflate(parent.context.inflater, parent, false))
    }
}
