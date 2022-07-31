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
import org.oxycblt.auxio.ui.recycler.*
import org.oxycblt.auxio.util.*

class QueueAdapter(listener: QueueItemListener) :
    MonoAdapter<QueueViewModel.QueueSong, QueueItemListener, QueueSongViewHolder>(listener) {
    override val data = SyncBackingData(this, QueueSongViewHolder.DIFFER)
    override val creator = QueueSongViewHolder.CREATOR
}

interface QueueItemListener {
    fun onClick(viewHolder: RecyclerView.ViewHolder)
    fun onPickUp(viewHolder: RecyclerView.ViewHolder)
}

class QueueSongViewHolder
private constructor(
    private val binding: ItemQueueSongBinding,
) : BindingViewHolder<QueueViewModel.QueueSong, QueueItemListener>(binding.root) {
    val bodyView: View
        get() = binding.body
    val backgroundView: View
        get() = binding.background

    val backgroundDrawable =
        MaterialShapeDrawable.createWithElevationOverlay(binding.root.context).apply {
            fillColor = binding.context.getAttrColorSafe(R.attr.colorSurface).stateList
            elevation = binding.context.getDimenSafe(R.dimen.elevation_normal) * 5
        }

    val isPrevious: Boolean
        get() = binding.songDragHandle.alpha == 0.5f

    init {
        binding.body.background =
            LayerDrawable(
                arrayOf(
                    MaterialShapeDrawable.createWithElevationOverlay(binding.context).apply {
                        fillColor = binding.context.getAttrColorSafe(R.attr.colorSurface).stateList
                        elevation = binding.context.getDimenSafe(R.dimen.elevation_normal)
                    },
                    backgroundDrawable))

        backgroundDrawable.alpha = 0
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun bind(item: QueueViewModel.QueueSong, listener: QueueItemListener) {
        binding.songAlbumCover.bind(item.song)
        binding.songName.textSafe = item.song.resolveName(binding.context)
        binding.songInfo.textSafe = item.song.resolveIndividualArtistName(binding.context)

        binding.background.isInvisible = true

        binding.songName.requestLayout()
        binding.songInfo.requestLayout()

        binding.body.setOnClickListener { listener.onClick(this) }

        val alpha = if (item.previous) 0.5f else 1f
        binding.songAlbumCover.alpha = alpha
        binding.songName.alpha = alpha
        binding.songInfo.alpha = alpha
        binding.songDragHandle.alpha = alpha

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

        val DIFFER =
            object : SimpleItemCallback<QueueViewModel.QueueSong>() {
                override fun areContentsTheSame(
                    oldItem: QueueViewModel.QueueSong,
                    newItem: QueueViewModel.QueueSong
                ) =
                    super.areContentsTheSame(oldItem, newItem) &&
                        oldItem.previous == newItem.previous

                override fun areItemsTheSame(
                    oldItem: QueueViewModel.QueueSong,
                    newItem: QueueViewModel.QueueSong
                ) = oldItem.song == newItem.song && oldItem.previous == newItem.previous
            }
    }
}
