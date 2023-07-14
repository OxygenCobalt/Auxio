/*
 * Copyright (c) 2023 Auxio Project
 * PlaybackPagerAdapter.kt is part of Auxio.
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

package org.oxycblt.auxio.playback.pager

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlin.jvm.internal.Intrinsics
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemPlaybackSongBinding
import org.oxycblt.auxio.list.adapter.FlexibleListAdapter
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.util.inflater

class PlaybackPagerAdapter(
    private val listener: PlaybackPageListener,
    private val lifecycleOwner: LifecycleOwner
) : FlexibleListAdapter<Song, CoverViewHolder>(CoverViewHolder.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoverViewHolder {
        return CoverViewHolder.from(parent, listener).also {
            lifecycleOwner.lifecycle.addObserver(it)
        }
    }

    override fun onBindViewHolder(holder: CoverViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: CoverViewHolder) {
        holder.recycle()
        super.onViewRecycled(holder)
    }
}

class CoverViewHolder
private constructor(
    private val binding: ItemPlaybackSongBinding,
    private val listener: PlaybackPageListener
) : RecyclerView.ViewHolder(binding.root), DefaultLifecycleObserver, View.OnClickListener {

    init {
        binding.playbackSong.setOnClickListener(this)
        binding.playbackArtist.setOnClickListener(this)
        binding.playbackAlbum.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.playback_album -> listener.navigateToCurrentAlbum()
            R.id.playback_artist -> listener.navigateToCurrentArtist()
            R.id.playback_song -> listener.navigateToCurrentSong()
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        setSelected(true)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        setSelected(false)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        owner.lifecycle.removeObserver(this)
    }

    /**
     * Bind new data to this instance.
     *
     * @param item The new [Song] to bind.
     */
    fun bind(item: Song) {
        binding.playbackCover.bind(item)
        val context = binding.root.context
        binding.playbackSong.text = item.name.resolve(context)
        binding.playbackArtist.text = item.artists.resolveNames(context)
        binding.playbackAlbum.text = item.album.name.resolve(context)
        setSelected(true)
    }

    fun recycle() {
        // Marquee elements leak if they are not disabled when the views are destroyed.
        setSelected(false)
    }

    private fun setSelected(value: Boolean) {
        binding.playbackSong.isSelected = value
        binding.playbackArtist.isSelected = value
        binding.playbackAlbum.isSelected = value
    }

    companion object {
        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: ViewGroup, listener: PlaybackPageListener) =
            CoverViewHolder(
                ItemPlaybackSongBinding.inflate(parent.context.inflater, parent, false),
                listener
            )

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Song>() {
                override fun areItemsTheSame(oldItem: Song, newItem: Song) =
                    oldItem.uid == newItem.uid

                override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
                    return Intrinsics.areEqual(oldItem, newItem)
                }
            }
    }
}
