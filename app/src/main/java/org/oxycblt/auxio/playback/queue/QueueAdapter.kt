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
 
package org.oxycblt.auxio.playback.queue

import android.annotation.SuppressLint
import android.graphics.drawable.LayerDrawable
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemQueueSongBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.recycler.IndicatorAdapter
import org.oxycblt.auxio.ui.recycler.SongViewHolder
import org.oxycblt.auxio.ui.recycler.SyncListDiffer
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getAttrColorCompat
import org.oxycblt.auxio.util.getDimen
import org.oxycblt.auxio.util.inflater

class QueueAdapter(private val listener: QueueItemListener) :
    RecyclerView.Adapter<QueueSongViewHolder>() {
    private var differ = SyncListDiffer(this, QueueSongViewHolder.DIFFER)
    private var currentIndex = 0
    private var isPlaying = false

    override fun getItemCount() = differ.currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        QueueSongViewHolder.new(parent)

    override fun onBindViewHolder(holder: QueueSongViewHolder, position: Int) =
        throw IllegalStateException()

    override fun onBindViewHolder(
        viewHolder: QueueSongViewHolder,
        position: Int,
        payload: List<Any>
    ) {
        if (payload.isEmpty()) {
            viewHolder.bind(differ.currentList[position], listener)
        }

        viewHolder.isEnabled = position > currentIndex
        viewHolder.updateIndicator(position == currentIndex, isPlaying)
    }

    fun submitList(newList: List<Song>) {
        differ.submitList(newList)
    }

    fun replaceList(newList: List<Song>) {
        differ.replaceList(newList)
    }

    fun updateIndicator(index: Int, isPlaying: Boolean) {
        var updatedIndex = false

        if (index != currentIndex) {
            when {
                index < currentIndex -> {
                    val lastIndex = currentIndex
                    currentIndex = index
                    notifyItemRangeChanged(0, lastIndex + 1, PAYLOAD_UPDATE_INDEX)
                }
                else -> {
                    currentIndex = index
                    notifyItemRangeChanged(0, currentIndex + 1, PAYLOAD_UPDATE_INDEX)
                }
            }

            updatedIndex = true
        }

        if (this.isPlaying != isPlaying) {
            this.isPlaying = isPlaying

            if (!updatedIndex) {
                notifyItemChanged(index, PAYLOAD_UPDATE_INDEX)
            }
        }
    }

    companion object {
        val PAYLOAD_UPDATE_INDEX = Any()
    }
}

interface QueueItemListener {
    fun onClick(viewHolder: RecyclerView.ViewHolder)
    fun onPickUp(viewHolder: RecyclerView.ViewHolder)
}

class QueueSongViewHolder private constructor(private val binding: ItemQueueSongBinding) :
    IndicatorAdapter.ViewHolder(binding.root) {
    val bodyView: View
        get() = binding.body
    val backgroundView: View
        get() = binding.background

    val backgroundDrawable =
        MaterialShapeDrawable.createWithElevationOverlay(binding.root.context).apply {
            fillColor = binding.context.getAttrColorCompat(R.attr.colorSurface)
            elevation = binding.context.getDimen(R.dimen.elevation_normal) * 5
            alpha = 0
        }

    init {
        binding.body.background =
            LayerDrawable(
                arrayOf(
                    MaterialShapeDrawable.createWithElevationOverlay(binding.context).apply {
                        fillColor = binding.context.getAttrColorCompat(R.attr.colorSurface)
                        elevation = binding.context.getDimen(R.dimen.elevation_normal)
                    },
                    backgroundDrawable
                )
            )
    }

    @SuppressLint("ClickableViewAccessibility")
    fun bind(item: Song, listener: QueueItemListener) {
        binding.songAlbumCover.bind(item)
        binding.songName.text = item.resolveName(binding.context)
        binding.songInfo.text = item.resolveArtistContents(binding.context)

        binding.background.isInvisible = true

        binding.body.setOnClickListener { listener.onClick(this) }

        // Roll our own drag handlers as the default ones suck
        binding.songDragHandle.setOnTouchListener { _, motionEvent ->
            binding.songDragHandle.performClick()
            if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                listener.onPickUp(this)
                true
            } else false
        }
    }

    var isEnabled: Boolean
        get() = binding.songAlbumCover.isEnabled
        set(value) {
            // Don't want to disable clicking, just indicate the body and handle is disabled
            binding.songAlbumCover.isEnabled = value
            binding.songName.isEnabled = value
            binding.songInfo.isEnabled = value
            binding.songDragHandle.isEnabled = value
        }

    override fun updateIndicator(isActive: Boolean, isPlaying: Boolean) {
        binding.interactBody.isActivated = isActive
        binding.songAlbumCover.isPlaying = isPlaying
    }

    companion object {
        fun new(parent: View) =
            QueueSongViewHolder(ItemQueueSongBinding.inflate(parent.context.inflater))

        val DIFFER = SongViewHolder.DIFFER
    }
}
