/*
 * Copyright (c) 2021 Auxio Project
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
 
package org.oxycblt.auxio.music.storage

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemMusicDirBinding
import org.oxycblt.auxio.list.recycler.DialogRecyclerView
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater

/**
 * [RecyclerView.Adapter] that manages a list of [Directory] instances.
 * @param listener A [DirectoryAdapter.Listener] to bind interactions to.
 * @author Alexander Capehart (OxygenCobalt)
 */
class DirectoryAdapter(private val listener: Listener) :
    RecyclerView.Adapter<MusicDirViewHolder>() {
    private val _dirs = mutableListOf<Directory>()
    /**
     * The current list of [Directory]s, may not line up with [MusicDirectories] due to removals.
     */
    val dirs: List<Directory> = _dirs

    override fun getItemCount() = dirs.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MusicDirViewHolder.from(parent)

    override fun onBindViewHolder(holder: MusicDirViewHolder, position: Int) =
        holder.bind(dirs[position], listener)

    /**
     * Add a [Directory] to the end of the list.
     * @param dir The [Directory] to add.
     */
    fun add(dir: Directory) {
        if (_dirs.contains(dir)) {
            return
        }

        _dirs.add(dir)
        notifyItemInserted(_dirs.lastIndex)
    }

    /**
     * Add a list of [Directory] instances to the end of the list.
     * @param dirs The [Directory instances to add.
     */
    fun addAll(dirs: List<Directory>) {
        val oldLastIndex = dirs.lastIndex
        _dirs.addAll(dirs)
        notifyItemRangeInserted(oldLastIndex, dirs.size)
    }

    /**
     * Remove a [Directory] from the list.
     * @param dir The [Directory] to remove. Must exist in the list.
     */
    fun remove(dir: Directory) {
        val idx = _dirs.indexOf(dir)
        _dirs.removeAt(idx)
        notifyItemRemoved(idx)
    }

    /** A Listener for [DirectoryAdapter] interactions. */
    interface Listener {
        fun onRemoveDirectory(dir: Directory)
    }
}

/**
 * A [RecyclerView.Recycler] that displays a [Directory]. Use [from] to create an instance.
 * @author Alexander Capehart (OxygenCobalt)
 */
class MusicDirViewHolder private constructor(private val binding: ItemMusicDirBinding) :
    DialogRecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     * @param dir The new [Directory] to bind.
     * @param listener A [DirectoryAdapter.Listener] to bind interactions to.
     */
    fun bind(dir: Directory, listener: DirectoryAdapter.Listener) {
        binding.dirPath.text = dir.resolveName(binding.context)
        binding.dirDelete.setOnClickListener { listener.onRemoveDirectory(dir) }
    }

    companion object {
        /**
         * Create a new instance.
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            MusicDirViewHolder(ItemMusicDirBinding.inflate(parent.context.inflater))
    }
}
