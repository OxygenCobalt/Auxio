/*
 * Copyright (c) 2026 Auxio Project
 * LyricsAdapter.kt is part of Auxio.
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

package org.oxycblt.auxio.playback.lyrics

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemLyricsLineBinding
import org.oxycblt.auxio.list.adapter.FlexibleListAdapter
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater

/**
 * A [FlexibleListAdapter] that displays a list of [LyricsLine]s, highlighting the line at
 * [currentLine] when the lyrics are synced.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class LyricsAdapter :
    FlexibleListAdapter<LyricsLine, LyricsLineViewHolder>(LyricsLineViewHolder.DIFF_CALLBACK) {
    /** The index of the line currently being sung, or -1 if none/unsynced. */
    var currentLine: Int = -1
        private set
    /**
     * Whether the current list is synced. When false, [currentLine] is never highlighted, as
     * there is no "current" line for plain lyrics -- every line simply shows at full opacity.
     */
    var synced: Boolean = false
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LyricsLineViewHolder.from(parent)

    override fun onBindViewHolder(holder: LyricsLineViewHolder, position: Int) {
        holder.bind(getItem(position), isCurrent(position), synced)
    }

    override fun onBindViewHolder(
        holder: LyricsLineViewHolder,
        position: Int,
        payload: List<Any>,
    ) {
        if (payload.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            holder.setCurrent(isCurrent(position), synced)
        }
    }

    private fun isCurrent(position: Int) = synced && position == currentLine

    /**
     * Replace the displayed lyrics lines.
     *
     * @param lines The new lines to display.
     * @param synced Whether [lines] carry synced timestamps.
     * @param onSubmitted Called once [lines] has actually been diffed in and applied -- a caller
     *   about to immediately call [setCurrentLine] for the new content (e.g. right after a song
     *   change) should wait for this rather than calling it synchronously right after this
     *   function returns: the diff behind [UpdateInstructions.Diff] runs asynchronously, so a
     *   synchronous [setCurrentLine] call could otherwise highlight the right index against the
     *   still-old, not-yet-replaced list for one frame.
     */
    fun submitLines(lines: List<LyricsLine>, synced: Boolean, onSubmitted: (() -> Unit)? = null) {
        this.synced = synced
        currentLine = -1
        update(lines, UpdateInstructions.Diff, onSubmitted)
    }

    /**
     * Update the currently-sung line, re-binding only the old and new highlighted rows. No-op
     * when [synced] is false.
     *
     * @param line The new current line index, or -1 for none.
     */
    fun setCurrentLine(line: Int) {
        if (!synced || line == currentLine) return
        val oldLine = currentLine
        currentLine = line
        if (oldLine in currentList.indices) {
            notifyItemChanged(oldLine, PAYLOAD_CURRENT_CHANGED)
        }
        if (line in currentList.indices) {
            notifyItemChanged(line, PAYLOAD_CURRENT_CHANGED)
        }
    }

    private companion object {
        val PAYLOAD_CURRENT_CHANGED = Any()
    }
}

/**
 * A [RecyclerView.ViewHolder] that displays a [LyricsLine].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class LyricsLineViewHolder private constructor(private val binding: ItemLyricsLineBinding) :
    RecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param line The new [LyricsLine] to bind.
     * @param current Whether this line is currently being sung. Always false when [synced] is
     *   false.
     * @param synced Whether the lyrics as a whole are synced. When false, every line shows at
     *   full opacity, since there is no "current" line to contrast against.
     */
    fun bind(line: LyricsLine, current: Boolean, synced: Boolean) {
        binding.lyricsLine.text = line.text
        setCurrent(current, synced)
    }

    /**
     * Update only the highlighted state of this instance.
     *
     * @param current Whether this line is currently being sung.
     * @param synced Whether the lyrics as a whole are synced.
     * @see bind
     */
    fun setCurrent(current: Boolean, synced: Boolean) {
        binding.lyricsLine.alpha = if (!synced || current) 1f else 0.5f
    }

    companion object {
        fun from(parent: ViewGroup) =
            LyricsLineViewHolder(
                ItemLyricsLineBinding.inflate(parent.context.inflater, parent, false)
            )

        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<LyricsLine>() {
                override fun areItemsTheSame(oldItem: LyricsLine, newItem: LyricsLine) =
                    oldItem == newItem

                override fun areContentsTheSame(oldItem: LyricsLine, newItem: LyricsLine) =
                    oldItem == newItem
            }
    }
}
