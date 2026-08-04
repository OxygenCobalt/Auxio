/*
 * Copyright (c) 2026 Auxio Project
 * DislikedSongsFragment.kt is part of Auxio.
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

package org.oxycblt.auxio.smartshuffle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentDislikedSongsBinding
import org.oxycblt.auxio.databinding.ItemDislikedSongBinding
import org.oxycblt.auxio.list.adapter.FlexibleListAdapter
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.resolve
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.ui.ViewBindingFragment
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.navigateSafe
import org.oxycblt.auxio.util.showToast
import org.oxycblt.auxio.util.systemBarInsetsCompat
import org.oxycblt.musikr.Song

/**
 * Lists songs Smart Shuffle marked as undesirable so they can be forgiven or deleted.
 */
@AndroidEntryPoint
class DislikedSongsFragment : ViewBindingFragment<FragmentDislikedSongsBinding>() {
    private val dislikedModel: DislikedSongsViewModel by viewModels()
    private val musicModel: MusicViewModel by activityViewModels()
    private val adapter = DislikedSongAdapter(::onSongClick, ::onForgive, ::onDelete)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateBinding(inflater: LayoutInflater) =
        FragmentDislikedSongsBinding.inflate(inflater)

    override fun onBindingCreated(
        binding: FragmentDislikedSongsBinding,
        savedInstanceState: Bundle?,
    ) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.dislikedToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.dislikedRecycler.apply {
            adapter = this@DislikedSongsFragment.adapter
            setOnApplyWindowInsetsListener { view, insets ->
                view.updatePadding(bottom = insets.systemBarInsetsCompat.bottom)
                insets
            }
        }

        dislikedModel.refresh()
        collectImmediately(dislikedModel.songs, ::updateSongs)
    }

    override fun onDestroyBinding(binding: FragmentDislikedSongsBinding) {
        super.onDestroyBinding(binding)
        binding.dislikedRecycler.adapter = null
    }

    private fun updateSongs(songs: List<DislikedSongItem>) {
        val binding = requireBinding()
        adapter.update(songs, UpdateInstructions.Diff)
        binding.dislikedEmpty.isVisible = songs.isEmpty()
        binding.dislikedRecycler.isVisible = songs.isNotEmpty()
    }

    private fun onSongClick(song: Song) {
        findNavController()
            .navigateSafe(DislikedSongsFragmentDirections.showSong(song.uid))
    }

    private fun onForgive(song: Song) {
        dislikedModel.forgive(song)
    }

    private fun onDelete(song: Song) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.lbl_delete)
            .setMessage(
                getString(R.string.fmt_deletion_info, song.name.resolve(requireContext()))
            )
            .setPositiveButton(R.string.lbl_delete) { _, _ ->
                if (dislikedModel.delete(song)) {
                    musicModel.rescan()
                } else {
                    requireContext().showToast(R.string.err_delete_song)
                }
            }
            .setNegativeButton(R.string.lbl_cancel, null)
            .show()
    }

    private class DislikedSongAdapter(
        private val onClick: (Song) -> Unit,
        private val onForgive: (Song) -> Unit,
        private val onDelete: (Song) -> Unit,
    ) :
        FlexibleListAdapter<DislikedSongItem, DislikedSongViewHolder>(DIFF_CALLBACK) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            DislikedSongViewHolder.from(parent)

        override fun onBindViewHolder(holder: DislikedSongViewHolder, position: Int) {
            holder.bind(getItem(position), onClick, onForgive, onDelete)
        }

        private companion object {
            val DIFF_CALLBACK =
                object : DiffUtil.ItemCallback<DislikedSongItem>() {
                    override fun areItemsTheSame(
                        oldItem: DislikedSongItem,
                        newItem: DislikedSongItem,
                    ) = oldItem.song.uid == newItem.song.uid

                    override fun areContentsTheSame(
                        oldItem: DislikedSongItem,
                        newItem: DislikedSongItem,
                    ) = oldItem == newItem
                }
        }
    }

    private class DislikedSongViewHolder(private val binding: ItemDislikedSongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: DislikedSongItem,
            onClick: (Song) -> Unit,
            onForgive: (Song) -> Unit,
            onDelete: (Song) -> Unit,
        ) {
            val song = item.song
            binding.dislikedCover.bind(song)
            binding.dislikedName.text = song.name.resolve(binding.context)
            binding.dislikedInfo.text =
                binding.context.getString(
                    R.string.fmt_disliked_song_info,
                    song.artists.resolveNames(binding.context),
                    item.stats.skips,
                    item.stats.listens,
                )
            binding.dislikedPath.text = song.path.resolve(binding.context)
            binding.root.setOnClickListener { onClick(song) }
            binding.dislikedForgive.setOnClickListener { onForgive(song) }
            binding.dislikedDelete.setOnClickListener { onDelete(song) }
        }

        companion object {
            fun from(parent: ViewGroup) =
                DislikedSongViewHolder(
                    ItemDislikedSongBinding.inflate(parent.context.inflater, parent, false)
                )
        }
    }
}
