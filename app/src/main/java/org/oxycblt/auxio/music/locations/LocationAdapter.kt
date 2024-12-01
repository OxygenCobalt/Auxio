/*
 * Copyright (c) 2021 Auxio Project
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

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemMusicLocationBinding
import org.oxycblt.auxio.list.recycler.DialogRecyclerView
import org.oxycblt.auxio.musikr.fs.Path
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater
import timber.log.Timber as L

/**
 * [RecyclerView.Adapter] that manages a list of [MusicLocation] music directory instances.
 *
 * @param listener A [LocationAdapter.Listener] to bind interactions to.
 * @author Alexander Capehart (OxygenCobalt)
 */
class LocationAdapter(private val listener: Listener) : RecyclerView.Adapter<MusicDirViewHolder>() {
    private val _locations = mutableListOf<MusicLocation>()
    /**
     * The current list of [MusicLocation]s, may not line up with [MusicLocation]s due to removals.
     */
    val locations: List<MusicLocation> = _locations

    override fun getItemCount() = locations.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MusicDirViewHolder.from(parent)

    override fun onBindViewHolder(holder: MusicDirViewHolder, position: Int) =
        holder.bind(locations[position], listener)

    /**
     * Add a [MusicLocation] to the end of the list.
     *
     * @param location The [MusicLocation] to add.
     */
    fun add(location: MusicLocation) {
        if (_locations.contains(location)) return
        L.d("Adding $location")
        _locations.add(location)
        notifyItemInserted(_locations.lastIndex)
    }

    /**
     * Add a list of [MusicLocation] instances to the end of the list.
     *
     * @param locations The [MusicLocation] instances to add.
     */
    fun addAll(locations: List<MusicLocation>) {
        L.d("Adding ${locations.size} locations")
        val oldLastIndex = locations.lastIndex
        _locations.addAll(locations)
        notifyItemRangeInserted(oldLastIndex, locations.size)
    }

    /**
     * Remove a [MusicLocation] from the list.
     *
     * @param location The [MusicLocation] to remove. Must exist in the list.
     */
    fun remove(location: MusicLocation) {
        L.d("Removing $location")
        val idx = _locations.indexOf(location)
        _locations.removeAt(idx)
        notifyItemRemoved(idx)
    }

    /** A Listener for [LocationAdapter] interactions. */
    interface Listener {
        /** Called when the delete button on a directory item is clicked. */
        fun onRemoveLocation(location: MusicLocation)
    }
}

data class MusicLocation(val uri: Uri, val path: Path)

/**
 * A [RecyclerView.Recycler] that displays a [MusicLocation]. Use [from] to create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class MusicDirViewHolder private constructor(private val binding: ItemMusicLocationBinding) :
    DialogRecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param location The new [MusicLocation] to bind.
     * @param listener A [LocationAdapter.Listener] to bind interactions to.
     */
    fun bind(location: MusicLocation, listener: LocationAdapter.Listener) {
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
        fun from(parent: View) =
            MusicDirViewHolder(ItemMusicLocationBinding.inflate(parent.context.inflater))
    }
}
