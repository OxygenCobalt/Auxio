package org.oxycblt.auxio.library.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemGenreBinding
import org.oxycblt.auxio.music.models.Genre
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

    // Generic ViewHolder for an artist
    inner class ViewHolder(
        private val binding: ItemGenreBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Force the viewholder to *actually* be the screen width so ellipsizing can work.
            binding.root.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
            )
        }

        // Bind the view w/new data
        fun bind(genre: Genre) {
            binding.genre = genre

            binding.root.setOnClickListener { listener.onClick(genre) }

            // Force-update the layout so ellipsizing works.
            binding.artistName.requestLayout()
            binding.executePendingBindings()
        }
    }
}
