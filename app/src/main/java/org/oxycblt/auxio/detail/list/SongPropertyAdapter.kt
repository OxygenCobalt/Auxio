/*
 * Copyright (c) 2023 Auxio Project
 * SongPropertyAdapter.kt is part of Auxio.
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

import android.text.Editable
import android.text.format.Formatter
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemSongPropertyBinding
import org.oxycblt.auxio.list.adapter.FlexibleListAdapter
import org.oxycblt.auxio.list.adapter.SimpleDiffCallback
import org.oxycblt.auxio.list.recycler.DialogRecyclerView
import org.oxycblt.auxio.music.resolve
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.playback.replaygain.formatDb
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.fs.Format
import org.oxycblt.musikr.fs.Path
import org.oxycblt.musikr.tag.Date
import org.oxycblt.musikr.tag.Name

/**
 * An adapter for [SongProperty] instances.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class SongPropertyAdapter :
    FlexibleListAdapter<SongProperty, SongPropertyViewHolder>(
        SongPropertyViewHolder.DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SongPropertyViewHolder.from(parent)

    override fun onBindViewHolder(holder: SongPropertyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

/**
 * A property entry for use in [SongPropertyAdapter].
 *
 * @param name The contextual title to use for the property.
 * @param value The value of the property.
 * @author Alexander Capehart (OxygenCobalt)
 */
data class SongProperty(@StringRes val name: Int, val value: Value) {
    sealed interface Value {
        data class MusicName(val music: Music) : Value
        data class MusicNames(val name: List<Music>) : Value
        data class Number(val value: Int, val subtitle: String?) : Value
        data class ItemDate(val date: Date) : Value
        data class ItemPath(val path: Path) : Value
        data class Size(val sizeBytes: Long) : Value
        data class Duration(val durationMs: Long) : Value
        data class ItemFormat(val format: Format) : Value
        data class Bitrate(val kbps: Int) : Value
        data class SampleRate(val hz: Int) : Value
        data class Decibels(val value: Float) : Value
    }
}

/**
 * A [RecyclerView.ViewHolder] that displays a [SongProperty]. Use [from] to create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class SongPropertyViewHolder private constructor(private val binding: ItemSongPropertyBinding) :
    DialogRecyclerView.ViewHolder(binding.root) {
    fun bind(property: SongProperty) {
        val context = binding.context
        binding.propertyName.hint = context.getString(property.name)
        when (property.value) {
            is SongProperty.Value.MusicName -> {
                val music = property.value.music
                binding.propertyValue.setText(music.name.resolve(context))
            }
            is SongProperty.Value.MusicNames -> {
                val names = property.value.name.resolveNames(context)
                binding.propertyValue.setText(names)
            }
            is SongProperty.Value.Number -> {
                val value = context.getString(R.string.fmt_number, property.value.value)
                val subtitle = property.value.subtitle
                binding.propertyValue.setText(if (subtitle != null) {
                    context.getString(R.string.fmt_zipped_names, value, subtitle)
                } else {
                    value
                })
            }
            is SongProperty.Value.ItemDate -> {
                val date = property.value.date
                binding.propertyValue.setText(date.resolve(context))
            }
            is SongProperty.Value.ItemPath -> {
                val path = property.value.path
                binding.propertyValue.setText(path.resolve(context))
            }
            is SongProperty.Value.Size -> {
                val size = property.value.sizeBytes
                binding.propertyValue.setText(Formatter.formatFileSize(context, size))
            }
            is SongProperty.Value.Duration -> {
                val duration = property.value.durationMs
                binding.propertyValue.setText(duration.formatDurationMs(true))
            }
            is SongProperty.Value.ItemFormat -> {
                val format = property.value.format
                binding.propertyValue.setText(format.resolve(context))
            }
            is SongProperty.Value.Bitrate -> {
                val kbps = property.value.kbps
                binding.propertyValue.setText(context.getString(R.string.fmt_bitrate, kbps))
            }
            is SongProperty.Value.SampleRate -> {
                val hz = property.value.hz
                binding.propertyValue.setText(context.getString(R.string.fmt_sample_rate, hz))
            }
            is SongProperty.Value.Decibels -> {
                val value = property.value.value
                binding.propertyValue.setText(value.formatDb(context))
            }
        }
    }

    companion object {
        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            SongPropertyViewHolder(ItemSongPropertyBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<SongProperty>() {
                override fun areContentsTheSame(oldItem: SongProperty, newItem: SongProperty) =
                    oldItem.name == newItem.name && oldItem.value == newItem.value
            }
    }
}
