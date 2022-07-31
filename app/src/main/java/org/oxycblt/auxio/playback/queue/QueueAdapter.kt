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
import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.view.MotionEvent
import android.view.View
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.MaterialShapeDrawable
import java.util.*
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemQueueSongBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.recycler.*
import org.oxycblt.auxio.util.*

class QueueAdapter(listener: QueueItemListener) :
    MonoAdapter<Song, QueueItemListener, QueueSongViewHolder>(listener) {
    private var currentIndex = 0

    override val data = SyncBackingData(this, QueueSongViewHolder.DIFFER)
    override val creator = QueueSongViewHolder.CREATOR

    override fun onBindViewHolder(
        viewHolder: QueueSongViewHolder,
        position: Int,
        payload: List<Any>
    ) {
        if (payload.isEmpty()) {
            super.onBindViewHolder(viewHolder, position, payload)
        }

        viewHolder.isEnabled = position > currentIndex
        viewHolder.isActivated = position == currentIndex
    }

    fun updateIndex(index: Int) {
        when {
            index < currentIndex -> {
                val lastIndex = currentIndex
                currentIndex = index
                notifyItemRangeChanged(0, lastIndex + 1, PAYLOAD_UPDATE_INDEX)
            }
            index > currentIndex -> {
                currentIndex = index
                notifyItemRangeChanged(0, currentIndex + 1, PAYLOAD_UPDATE_INDEX)
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

class QueueSongViewHolder
private constructor(
    private val binding: ItemQueueSongBinding,
) : BindingViewHolder<Song, QueueItemListener>(binding.root) {
    val bodyView: View
        get() = binding.body
    val backgroundView: View
        get() = binding.background

    val backgroundDrawable =
        MaterialShapeDrawable.createWithElevationOverlay(binding.root.context).apply {
            fillColor = binding.context.getAttrColorSafe(R.attr.colorSurface).stateList
            elevation = binding.context.getDimenSafe(R.dimen.elevation_normal) * 5
            alpha = 0
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

    var isActivated: Boolean
        get() = binding.interactBody.isActivated
        set(value) {
            // Activation does not affect clicking, make everything activated.
            binding.interactBody.setActivated(value)
        }

    init {
        binding.body.background =
            LayerDrawable(
                arrayOf(
                    MaterialShapeDrawable.createWithElevationOverlay(binding.context).apply {
                        fillColor = binding.context.getAttrColorSafe(R.attr.colorSurface).stateList
                        elevation = binding.context.getDimenSafe(R.dimen.elevation_normal)
                    },
                    backgroundDrawable))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun bind(item: Song, listener: QueueItemListener) {
        binding.songAlbumCover.bind(item)
        binding.songName.textSafe = item.resolveName(binding.context)
        binding.songInfo.textSafe = item.resolveIndividualArtistName(binding.context)

        binding.background.isInvisible = true

        binding.songName.requestLayout()
        binding.songInfo.requestLayout()

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

    companion object {
        val CREATOR =
            object : Creator<QueueSongViewHolder> {
                override val viewType: Int
                    get() = IntegerTable.ITEM_TYPE_QUEUE_SONG

                override fun create(context: Context): QueueSongViewHolder =
                    QueueSongViewHolder(ItemQueueSongBinding.inflate(context.inflater))
            }

        val DIFFER = SongViewHolder.DIFFER
    }
}
