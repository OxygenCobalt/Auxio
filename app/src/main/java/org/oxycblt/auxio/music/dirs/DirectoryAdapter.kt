/*
 * Copyright (c) 2021 Auxio Project
 * DirectoryAdapter.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.dirs

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemMusicDirBinding
import org.oxycblt.auxio.list.recycler.DialogRecyclerView
import org.oxycblt.auxio.music.fs.Path
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.logD

/**
 * [RecyclerView.Adapter] that manages a list of [Path] music directory instances.
 *
 * @param listener A [DirectoryAdapter.Listener] to bind interactions to.
 * @author Alexander Capehart (OxygenCobalt)
 */
class DirectoryAdapter(private val listener: Listener) :
    RecyclerView.Adapter<MusicDirViewHolder>() {
    private val _dirs = mutableListOf<Path>()
    /** The current list of [Path]s, may not line up with [MusicDirectories] due to removals. */
    val dirs: List<Path> = _dirs

    override fun getItemCount() = dirs.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MusicDirViewHolder.from(parent)

    override fun onBindViewHolder(holder: MusicDirViewHolder, position: Int) =
        holder.bind(dirs[position], listener)

    /**
     * Add a [Path] to the end of the list.
     *
     * @param path The [Path] to add.
     */
    fun add(path: Path) {
        if (_dirs.contains(path)) return
        logD("Adding $path")
        _dirs.add(path)
        notifyItemInserted(_dirs.lastIndex)
    }

    /**
     * Add a list of [Path] instances to the end of the list.
     *
     * @param path The [Path] instances to add.
     */
    fun addAll(path: List<Path>) {
        logD("Adding ${path.size} directories")
        val oldLastIndex = path.lastIndex
        _dirs.addAll(path)
        notifyItemRangeInserted(oldLastIndex, path.size)
    }

    /**
     * Remove a [Path] from the list.
     *
     * @param path The [Path] to remove. Must exist in the list.
     */
    fun remove(path: Path) {
        logD("Removing $path")
        val idx = _dirs.indexOf(path)
        _dirs.removeAt(idx)
        notifyItemRemoved(idx)
    }

    /** A Listener for [DirectoryAdapter] interactions. */
    interface Listener {
        /** Called when the delete button on a directory item is clicked. */
        fun onRemoveDirectory(dir: Path)
    }
}

/**
 * A [RecyclerView.Recycler] that displays a [Path]. Use [from] to create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class MusicDirViewHolder private constructor(private val binding: ItemMusicDirBinding) :
    DialogRecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param path The new [Path] to bind.
     * @param listener A [DirectoryAdapter.Listener] to bind interactions to.
     */
    fun bind(path: Path, listener: DirectoryAdapter.Listener) {
        binding.dirPath.text = path.resolve(binding.context)
        binding.dirDelete.setOnClickListener { listener.onRemoveDirectory(path) }
    }

    companion object {
        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            MusicDirViewHolder(ItemMusicDirBinding.inflate(parent.context.inflater))
    }
}
