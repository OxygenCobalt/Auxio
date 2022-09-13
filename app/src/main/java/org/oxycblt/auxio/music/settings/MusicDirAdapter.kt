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
 
package org.oxycblt.auxio.music.settings

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemMusicDirBinding
import org.oxycblt.auxio.music.Directory
import org.oxycblt.auxio.ui.recycler.DialogViewHolder
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater

/**
 * Adapter that shows the list of music folder and their "Clear" button.
 * @author OxygenCobalt
 */
class MusicDirAdapter(private val listener: Listener) : RecyclerView.Adapter<MusicDirViewHolder>() {
    private val _dirs = mutableListOf<Directory>()
    val dirs: List<Directory> = _dirs

    override fun getItemCount() = dirs.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MusicDirViewHolder.new(parent)

    override fun onBindViewHolder(holder: MusicDirViewHolder, position: Int) =
        holder.bind(dirs[position], listener)

    fun add(dir: Directory) {
        if (_dirs.contains(dir)) {
            return
        }

        _dirs.add(dir)
        notifyItemInserted(_dirs.lastIndex)
    }

    fun addAll(dirs: List<Directory>) {
        val oldLastIndex = dirs.lastIndex
        _dirs.addAll(dirs)
        notifyItemRangeInserted(oldLastIndex, dirs.size)
    }

    fun remove(dir: Directory) {
        val idx = _dirs.indexOf(dir)
        _dirs.removeAt(idx)
        notifyItemRemoved(idx)
    }

    interface Listener {
        fun onRemoveDirectory(dir: Directory)
    }
}

/** The viewholder for [MusicDirAdapter]. Not intended for use in other adapters. */
class MusicDirViewHolder private constructor(private val binding: ItemMusicDirBinding) :
    DialogViewHolder(binding.root) {
    fun bind(item: Directory, listener: MusicDirAdapter.Listener) {
        binding.dirPath.text = item.resolveName(binding.context)
        binding.dirDelete.setOnClickListener { listener.onRemoveDirectory(item) }
    }

    companion object {
        fun new(parent: View) =
            MusicDirViewHolder(ItemMusicDirBinding.inflate(parent.context.inflater))
    }
}
