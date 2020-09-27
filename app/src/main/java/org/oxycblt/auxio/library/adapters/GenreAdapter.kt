package org.oxycblt.auxio.library.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemGenreBinding
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.recycler.BaseViewHolder
import org.oxycblt.auxio.recycler.ClickListener

class GenreAdapter(
    private val data: List<Genre>,
    private val listener: ClickListener<Genre>
) : RecyclerView.Adapter<GenreAdapter.ViewHolder>() {

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemGenreBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
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
