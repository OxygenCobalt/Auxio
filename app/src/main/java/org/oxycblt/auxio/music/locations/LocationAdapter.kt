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
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemMusicLocationBinding
import org.oxycblt.auxio.databinding.ItemNoLocationsBinding
import org.oxycblt.auxio.list.recycler.DialogRecyclerView
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater
import org.oxycblt.musikr.fs.Location
import timber.log.Timber as L

class LocationAdapter<T : Location>(private val listener: Listener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val _locations = mutableListOf<T>()
    val locations: List<T> = _locations

    override fun getItemCount() = if (locations.isEmpty()) 1 else locations.size

    override fun getItemViewType(position: Int): Int {
        return if (locations.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_LOCATION
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_EMPTY -> EmptyLocationViewHolder.from(parent)
            VIEW_TYPE_LOCATION -> LocationViewHolder.from<T>(parent)
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LocationViewHolder<*> -> {
                @Suppress("UNCHECKED_CAST")
                (holder as LocationViewHolder<T>).bind(locations[position], listener)
            }
            is EmptyLocationViewHolder -> holder.bind()
        }
    }

    fun add(location: T) {
        if (_locations.contains(location)) return
        L.d("Adding $location")
        val wasEmpty = _locations.isEmpty()
        _locations.add(location)
        if (wasEmpty) {
            notifyItemChanged(0)
        } else {
            notifyItemInserted(_locations.lastIndex)
        }
    }

    fun addAll(locations: List<T>) {
        L.d("Adding ${locations.size} locations")
        val wasEmpty = _locations.isEmpty()
        val oldSize = _locations.size
        _locations.addAll(locations)
        if (wasEmpty && locations.isNotEmpty()) {
            notifyItemChanged(0)
        } else if (locations.isNotEmpty()) {
            notifyItemRangeInserted(oldSize, locations.size)
        }
    }

    fun remove(location: T) {
        L.d("Removing $location")
        val idx = _locations.indexOf(location)
        _locations.removeAt(idx)
        if (_locations.isEmpty()) {
            notifyItemChanged(0)
        } else {
            notifyItemRemoved(idx)
        }
    }

    interface Listener {
        fun onRemoveLocation(location: Location)
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_LOCATION = 1
    }
}

class LocationViewHolder<T : Location>(private val binding: ItemMusicLocationBinding) :
    DialogRecyclerView.ViewHolder(binding.root) {

    fun bind(location: T, listener: LocationAdapter.Listener) {
        binding.locationPath.text = location.path.resolve(binding.context)
        binding.locationDelete.setOnClickListener { listener.onRemoveLocation(location) }
    }

    companion object {
        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun <T : Location> from(parent: ViewGroup) =
            LocationViewHolder<T>(
                ItemMusicLocationBinding.inflate(parent.context.inflater, parent, false))
    }
}

class EmptyLocationViewHolder(private val binding: ItemNoLocationsBinding) :
    DialogRecyclerView.ViewHolder(binding.root) {

    fun bind() {
        binding.locationPath.text = binding.context.getString(R.string.lbl_no_folders)
    }

    companion object {
        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: ViewGroup) =
            EmptyLocationViewHolder(
                ItemNoLocationsBinding.inflate(parent.context.inflater, parent, false))
    }
}
