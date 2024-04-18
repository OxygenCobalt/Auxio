/*
 * Copyright (c) 2021 Auxio Project
 * QueueAdapter.kt is part of Auxio.
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
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R as MR
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.databinding.ItemEditableSongBinding
import org.oxycblt.auxio.list.EditClickListListener
import org.oxycblt.auxio.list.adapter.FlexibleListAdapter
import org.oxycblt.auxio.list.adapter.PlayingIndicatorAdapter
import org.oxycblt.auxio.list.recycler.MaterialDragCallback
import org.oxycblt.auxio.list.recycler.SongViewHolder
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getAttrColorCompat
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.logD

/**
 * A [RecyclerView.Adapter] that shows an editable list of queue items.
 *
 * @param listener A [EditClickListListener] to bind interactions to.
 * @author Alexander Capehart (OxygenCobalt)
 */
class QueueAdapter(private val listener: EditClickListListener<Song>) :
    FlexibleListAdapter<Song, QueueSongViewHolder>(QueueSongViewHolder.DIFF_CALLBACK) {
    // Since PlayingIndicator adapter relies on an item value, we cannot use it for this
    // adapter, as one item can appear at several points in the UI. Use a similar implementation
    // with an index value instead.
    private var currentIndex = 0
    private var isPlaying = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        QueueSongViewHolder.from(parent)

    override fun onBindViewHolder(holder: QueueSongViewHolder, position: Int) =
        throw IllegalStateException()

    override fun onBindViewHolder(
        viewHolder: QueueSongViewHolder,
        position: Int,
        payload: List<Any>
    ) {
        if (payload.isEmpty()) {
            viewHolder.bind(getItem(position), listener)
        }

        viewHolder.isFuture = position > currentIndex
        viewHolder.updatePlayingIndicator(position == currentIndex, isPlaying)
    }

    /**
     * Set the position of the currently playing item in the queue. This will mark the item as
     * playing and any previous items as played.
     *
     * @param index The position of the currently playing item in the queue.
     * @param isPlaying Whether playback is ongoing or paused.
     */
    fun setPosition(index: Int, isPlaying: Boolean) {
        logD("Updating index")
        val lastIndex = currentIndex
        currentIndex = index

        // Have to update not only the currently playing item, but also all items marked
        // as playing.
        // TODO: Optimize this by only updating the range between old and new indices?
        // TODO: Don't update when the index has not moved.
        if (currentIndex < lastIndex) {
            logD("Moved backwards, must update items above last index")
            notifyItemRangeChanged(0, lastIndex + 1, PAYLOAD_UPDATE_POSITION)
        } else {
            logD("Moved forwards, update items after index")
            notifyItemRangeChanged(0, currentIndex + 1, PAYLOAD_UPDATE_POSITION)
        }

        this.isPlaying = isPlaying
    }

    private companion object {
        val PAYLOAD_UPDATE_POSITION = Any()
    }
}

/**
 * A [PlayingIndicatorAdapter.ViewHolder] that displays an queue [Song] which can be re-ordered and
 * removed. Use [from] to create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class QueueSongViewHolder private constructor(private val binding: ItemEditableSongBinding) :
    PlayingIndicatorAdapter.ViewHolder(binding.root), MaterialDragCallback.ViewHolder {
    override val enabled = true
    override val root = binding.root
    override val body = binding.body
    override val delete = binding.background
    override val background =
        MaterialShapeDrawable.createWithElevationOverlay(binding.root.context).apply {
            fillColor = binding.context.getAttrColorCompat(MR.attr.colorSurfaceContainerHighest)
            alpha = 0
        }

    /**
     * Whether this ViewHolder should be full-opacity to represent a future item, or greyed out to
     * represent a past item. True if former, false if latter.
     */
    var isFuture: Boolean
        get() = binding.songAlbumCover.isEnabled
        set(value) {
            binding.songAlbumCover.isEnabled = value
            binding.songName.isEnabled = value
            binding.songInfo.isEnabled = value
        }

    init {
        binding.body.background =
            LayerDrawable(
                arrayOf(
                    MaterialShapeDrawable.createWithElevationOverlay(binding.context).apply {
                        fillColor =
                            binding.context.getAttrColorCompat(MR.attr.colorSurfaceContainerHigh)
                    },
                    background))
    }

    /**
     * Bind new data to this instance.
     *
     * @param song The new [Song] to bind.
     * @param listener A [EditClickListListener] to bind interactions to.
     */
    @SuppressLint("ClickableViewAccessibility")
    fun bind(song: Song, listener: EditClickListListener<Song>) {
        listener.bind(song, this, body, binding.songDragHandle)
        binding.songAlbumCover.bind(song)
        binding.songName.text = song.name.resolve(binding.context)
        binding.songInfo.text = song.artists.resolveNames(binding.context)
        // Not swiping this ViewHolder if it's being re-bound, ensure that the background is
        // not visible. See QueueDragCallback for why this is done.
        binding.background.isInvisible = true
    }

    override fun updatePlayingIndicator(isActive: Boolean, isPlaying: Boolean) {
        binding.interactBody.isSelected = isActive
        binding.songAlbumCover.setPlaying(isPlaying)
    }

    companion object {
        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            QueueSongViewHolder(ItemEditableSongBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK = SongViewHolder.DIFF_CALLBACK
    }
}
