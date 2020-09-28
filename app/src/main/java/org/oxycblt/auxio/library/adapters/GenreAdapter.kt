package org.oxycblt.auxio.library.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.oxycblt.auxio.databinding.ItemGenreBinding
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.recycler.BaseViewHolder
import org.oxycblt.auxio.recycler.ClickListener
import org.oxycblt.auxio.recycler.DiffCallback

class GenreAdapter(
    private val listener: ClickListener<Genre>
) : ListAdapter<Genre, GenreAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemGenreBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemGenreBinding
    ) : BaseViewHolder<Genre>(binding, listener) {

        override fun onBind(model: Genre) {
            binding.genre = model
            binding.genreName.requestLayout()
        }
    }
}
