/*
 * Copyright (c) 2023 Auxio Project
 * MenuDialogFragment.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list.menu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.children
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.DialogMenuBinding
import org.oxycblt.auxio.list.ClickableListListener
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.ui.ViewBindingBottomSheetDialogFragment
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.logD

/**
 * A [ViewBindingBottomSheetDialogFragment] that displays basic music information and a series of
 * options.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
abstract class MenuDialogFragment<T : Music> :
    ViewBindingBottomSheetDialogFragment<DialogMenuBinding>(), ClickableListListener<MenuItem> {
    protected abstract val menuModel: MenuViewModel
    private val menuAdapter = MenuOptionAdapter(@Suppress("LeakingThis") this)

    abstract val menuRes: Int
    abstract val uid: Music.UID
    abstract fun updateMusic(binding: DialogMenuBinding, music: T)
    abstract fun onClick(music: T, @IdRes optionId: Int)

    override fun onCreateBinding(inflater: LayoutInflater) = DialogMenuBinding.inflate(inflater)

    override fun onBindingCreated(binding: DialogMenuBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        // --- UI SETUP ---
        binding.menuOptionRecycler.apply {
            adapter = menuAdapter
            itemAnimator = null
        }

        // Avoid having to use a dummy view and rely on what AndroidX Toolbar uses.
        @SuppressLint("RestrictedApi") val builder = MenuBuilder(requireContext())
        MenuInflater(requireContext()).inflate(menuRes, builder)
        menuAdapter.update(builder.children.toList(), UpdateInstructions.Diff)

        // --- VIEWMODEL SETUP ---
        menuModel.setMusic(uid)
        collectImmediately(menuModel.currentMusic, this::updateMusic)
    }

    override fun onDestroyBinding(binding: DialogMenuBinding) {
        super.onDestroyBinding(binding)
        binding.menuOptionRecycler.adapter = null
    }

    private fun updateMusic(music: Music?) {
        if (music == null) {
            logD("No music to show, navigating away")
            findNavController().navigateUp()
        }
        @Suppress("UNCHECKED_CAST") updateMusic(requireBinding(), music as T)
    }

    final override fun onClick(item: MenuItem, viewHolder: RecyclerView.ViewHolder) {
        @Suppress("UNCHECKED_CAST") onClick(menuModel.currentMusic.value as T, item.itemId)
    }
}
