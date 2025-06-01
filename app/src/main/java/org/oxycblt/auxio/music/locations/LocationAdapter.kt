/*
 * Copyright (c) 2024 Auxio Project
 * LocationAdapter.kt is part of Auxio.
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
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemMusicLocationBinding
import org.oxycblt.auxio.list.recycler.DialogRecyclerView
import org.oxycblt.auxio.util.context
import org.oxycblt.musikr.fs.Location
import timber.log.Timber as L

abstract class LocationAdapter<T : Location>(private val listener: Listener<T>) :
    RecyclerView.Adapter<LocationViewHolder<T>>() {
    private val _locations = mutableListOf<T>()
    val locations: List<T> = _locations

    override fun getItemCount() = locations.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = createViewHolder(parent)

    override fun onBindViewHolder(holder: LocationViewHolder<T>, position: Int) =
        holder.bind(locations[position], listener)

    fun add(location: T) {
        if (_locations.contains(location)) return
        L.d("Adding $location")
        _locations.add(location)
        notifyItemInserted(_locations.lastIndex)
    }

    fun addAll(locations: List<T>) {
        L.d("Adding ${locations.size} locations")
        val oldSize = _locations.size
        _locations.addAll(locations)
        notifyItemRangeInserted(oldSize, locations.size)
    }

    fun remove(location: T) {
        L.d("Removing $location")
        val idx = _locations.indexOf(location)
        _locations.removeAt(idx)
        notifyItemRemoved(idx)
    }

    protected abstract fun createViewHolder(parent: ViewGroup): LocationViewHolder<T>

    interface Listener<T : Location> {
        fun onRemoveLocation(location: T)
    }
}

abstract class LocationViewHolder<T : Location>(protected val binding: ItemMusicLocationBinding) :
    DialogRecyclerView.ViewHolder(binding.root) {

    fun bind(location: T, listener: LocationAdapter.Listener<T>) {
        binding.locationPath.text = location.path.resolve(binding.context)
        binding.locationDelete.setOnClickListener { listener.onRemoveLocation(location) }
    }
}
