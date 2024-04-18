/*
 * Copyright (c) 2023 Auxio Project
 * PlaylistDetailListAdapter.kt is part of Auxio.
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
 
package org.oxycblt.auxio.detail.list

import android.annotation.SuppressLint
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R as MR
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.databinding.ItemEditHeaderBinding
import org.oxycblt.auxio.databinding.ItemEditableSongBinding
import org.oxycblt.auxio.list.EditableListListener
import org.oxycblt.auxio.list.Header
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.adapter.PlayingIndicatorAdapter
import org.oxycblt.auxio.list.adapter.SelectionIndicatorAdapter
import org.oxycblt.auxio.list.adapter.SimpleDiffCallback
import org.oxycblt.auxio.list.recycler.MaterialDragCallback
import org.oxycblt.auxio.list.recycler.SongViewHolder
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getAttrColorCompat
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.logD

/**
 * A [DetailListAdapter] implementing the header, sub-items, and editing state for the [Playlist]
 * detail view.
 *
 * @param listener A [DetailListAdapter.Listener] to bind interactions to.
 * @author Alexander Capehart (OxygenCobalt)
 */
class PlaylistDetailListAdapter(private val listener: Listener) :
    DetailListAdapter(listener, DIFF_CALLBACK) {
    private var isEditing = false

    override fun getItemViewType(position: Int) =
        when (getItem(position)) {
            is EditHeader -> EditHeaderViewHolder.VIEW_TYPE
            is Song -> PlaylistSongViewHolder.VIEW_TYPE
            else -> super.getItemViewType(position)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            EditHeaderViewHolder.VIEW_TYPE -> EditHeaderViewHolder.from(parent)
            PlaylistSongViewHolder.VIEW_TYPE -> PlaylistSongViewHolder.from(parent)
            else -> super.onCreateViewHolder(parent, viewType)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)

        if (payloads.isEmpty()) {
            when (val item = getItem(position)) {
                is EditHeader -> (holder as EditHeaderViewHolder).bind(item, listener)
                is Song -> (holder as PlaylistSongViewHolder).bind(item, listener)
            }
        }

        if (holder is ViewHolder) {
            holder.updateEditing(isEditing)
        }
    }

    fun setEditing(editing: Boolean) {
        if (editing == isEditing) {
            // Nothing to do.
            return
        }
        logD("Updating editing state [old: $isEditing new: $editing]")
        this.isEditing = editing
        notifyItemRangeChanged(1, currentList.size - 1, PAYLOAD_EDITING_CHANGED)
    }

    /** An extended [DetailListAdapter.Listener] for [PlaylistDetailListAdapter]. */
    interface Listener : DetailListAdapter.Listener<Song>, EditableListListener {
        /** Called when the "edit" option is selected in the edit header. */
        fun onStartEdit()
    }

    /**
     * A [RecyclerView.ViewHolder] extension required to respond to changes in the editing state.
     */
    interface ViewHolder {
        /**
         * Called when the editing state changes. Implementations should update UI options as needed
         * to reflect the new state.
         *
         * @param editing Whether the data is currently being edited or not.
         */
        fun updateEditing(editing: Boolean)
    }

    private companion object {
        val PAYLOAD_EDITING_CHANGED = Any()

        val DIFF_CALLBACK =
            object : SimpleDiffCallback<Item>() {
                override fun areContentsTheSame(oldItem: Item, newItem: Item) =
                    when {
                        oldItem is Song && newItem is Song ->
                            PlaylistSongViewHolder.DIFF_CALLBACK.areContentsTheSame(
                                oldItem, newItem)
                        oldItem is EditHeader && newItem is EditHeader ->
                            EditHeaderViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        else -> DetailListAdapter.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                    }
            }
    }
}

/**
 * A [Header] variant that displays an edit button.
 *
 * @param titleRes The string resource to use as the header title
 * @author Alexander Capehart (OxygenCobalt)
 */
data class EditHeader(@StringRes override val titleRes: Int) : Header

/**
 * Displays an [EditHeader] and it's actions. Use [from] to create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
private class EditHeaderViewHolder private constructor(private val binding: ItemEditHeaderBinding) :
    RecyclerView.ViewHolder(binding.root), PlaylistDetailListAdapter.ViewHolder {
    /**
     * Bind new data to this instance.
     *
     * @param editHeader The new [EditHeader] to bind.
     * @param listener An [PlaylistDetailListAdapter.Listener] to bind interactions to.
     */
    fun bind(editHeader: EditHeader, listener: PlaylistDetailListAdapter.Listener) {
        binding.headerTitle.text = binding.context.getString(editHeader.titleRes)
        // Add a Tooltip based on the content description so that the purpose of this
        // button can be clear.
        binding.headerEdit.apply {
            TooltipCompat.setTooltipText(this, contentDescription)
            setOnClickListener { listener.onStartEdit() }
        }
        binding.headerSort.apply {
            TooltipCompat.setTooltipText(this, contentDescription)
            setOnClickListener { listener.onOpenSortMenu() }
        }
    }

    override fun updateEditing(editing: Boolean) {
        binding.headerEdit.apply {
            isVisible = !editing
            isClickable = !editing
            isFocusable = !editing
            jumpDrawablesToCurrentState()
        }
        binding.headerSort.apply {
            isVisible = editing
            isClickable = editing
            isFocusable = editing
            jumpDrawablesToCurrentState()
        }
    }

    companion object {
        /** A unique ID for this [RecyclerView.ViewHolder] type. */
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_EDIT_HEADER

        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            EditHeaderViewHolder(ItemEditHeaderBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<EditHeader>() {
                override fun areContentsTheSame(oldItem: EditHeader, newItem: EditHeader) =
                    oldItem.titleRes == newItem.titleRes
            }
    }
}

/**
 * A [PlayingIndicatorAdapter.ViewHolder] that displays a queue [Song] which can be re-ordered and
 * removed. Use [from] to create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
private class PlaylistSongViewHolder
private constructor(private val binding: ItemEditableSongBinding) :
    SelectionIndicatorAdapter.ViewHolder(binding.root),
    MaterialDragCallback.ViewHolder,
    PlaylistDetailListAdapter.ViewHolder {
    override val enabled: Boolean
        get() = binding.songDragHandle.isVisible

    override val root = binding.root
    override val body = binding.body
    override val delete = binding.background
    override val background =
        MaterialShapeDrawable.createWithElevationOverlay(binding.root.context).apply {
            fillColor = binding.context.getAttrColorCompat(MR.attr.colorSurfaceContainerHigh)
            alpha = 0
        }

    init {
        binding.body.background =
            LayerDrawable(
                arrayOf(
                    MaterialShapeDrawable.createWithElevationOverlay(binding.context).apply {
                        fillColor = binding.context.getAttrColorCompat(MR.attr.colorSurface)
                    },
                    background))
    }

    /**
     * Bind new data to this instance.
     *
     * @param song The new [Song] to bind.
     * @param listener A [PlaylistDetailListAdapter.Listener] to bind interactions to.
     */
    @SuppressLint("ClickableViewAccessibility")
    fun bind(song: Song, listener: PlaylistDetailListAdapter.Listener) {
        listener.bind(song, this, binding.interactBody, menuButton = binding.songMenu)
        listener.bind(this, binding.songDragHandle)
        binding.songAlbumCover.bind(song)
        binding.songName.text = song.name.resolve(binding.context)
        binding.songInfo.text = song.artists.resolveNames(binding.context)
        // Not swiping this ViewHolder if it's being re-bound, ensure that the background is
        // not visible. See MaterialDragCallback for why this is done.
        binding.background.isInvisible = true
    }

    override fun updateSelectionIndicator(isSelected: Boolean) {
        binding.interactBody.isActivated = isSelected
        binding.songAlbumCover.isActivated = isSelected
    }

    override fun updatePlayingIndicator(isActive: Boolean, isPlaying: Boolean) {
        binding.interactBody.isSelected = isActive
        binding.songAlbumCover.setPlaying(isPlaying)
    }

    override fun updateEditing(editing: Boolean) {
        binding.songDragHandle.isInvisible = !editing
        binding.songMenu.isInvisible = editing
        binding.interactBody.isEnabled = !editing
    }

    companion object {
        /** A unique ID for this [RecyclerView.ViewHolder] type. */
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_PLAYLIST_SONG

        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            PlaylistSongViewHolder(ItemEditableSongBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK = SongViewHolder.DIFF_CALLBACK
    }
}
