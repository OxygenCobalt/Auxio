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
 
package org.oxycblt.auxio.music.dirs

import android.content.Context
import org.oxycblt.auxio.databinding.ItemMusicDirBinding
import org.oxycblt.auxio.music.Dir
import org.oxycblt.auxio.ui.BackingData
import org.oxycblt.auxio.ui.BindingViewHolder
import org.oxycblt.auxio.ui.MonoAdapter
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.textSafe

/**
 * Adapter that shows the excluded directories and their "Clear" button.
 * @author OxygenCobalt
 */
class MusicDirAdapter(listener: Listener) :
    MonoAdapter<Dir.Relative, MusicDirAdapter.Listener, MusicDirViewHolder>(listener) {
    override val data = ExcludedBackingData(this)
    override val creator = MusicDirViewHolder.CREATOR

    interface Listener {
        fun onRemoveDirectory(dir: Dir.Relative)
    }

    class ExcludedBackingData(private val adapter: MusicDirAdapter) : BackingData<Dir.Relative>() {
        private val _currentList = mutableListOf<Dir.Relative>()
        val currentList: List<Dir.Relative> = _currentList

        override fun getItemCount(): Int = _currentList.size
        override fun getItem(position: Int): Dir.Relative = _currentList[position]

        fun add(dir: Dir.Relative) {
            if (_currentList.contains(dir)) {
                return
            }

            _currentList.add(dir)
            adapter.notifyItemInserted(_currentList.lastIndex)
        }

        fun addAll(dirs: List<Dir.Relative>) {
            val oldLastIndex = dirs.lastIndex
            _currentList.addAll(dirs)
            adapter.notifyItemRangeInserted(oldLastIndex, dirs.size)
        }

        fun remove(dir: Dir.Relative) {
            val idx = _currentList.indexOf(dir)
            _currentList.removeAt(idx)
            adapter.notifyItemRemoved(idx)
        }
    }
}

/** The viewholder for [MusicDirAdapter]. Not intended for use in other adapters. */
class MusicDirViewHolder private constructor(private val binding: ItemMusicDirBinding) :
    BindingViewHolder<Dir.Relative, MusicDirAdapter.Listener>(binding.root) {
    override fun bind(item: Dir.Relative, listener: MusicDirAdapter.Listener) {
        binding.dirPath.textSafe = item.resolveName(binding.context)
        binding.dirDelete.setOnClickListener { listener.onRemoveDirectory(item) }
    }

    companion object {
        val CREATOR =
            object : Creator<MusicDirViewHolder> {
                override val viewType: Int
                    get() = throw UnsupportedOperationException()

                override fun create(context: Context) =
                    MusicDirViewHolder(ItemMusicDirBinding.inflate(context.inflater))
            }
    }
}
