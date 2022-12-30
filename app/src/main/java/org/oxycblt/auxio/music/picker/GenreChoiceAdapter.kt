package org.oxycblt.auxio.music.picker

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemPickerChoiceBinding
import org.oxycblt.auxio.list.ClickableListListener
import org.oxycblt.auxio.list.recycler.DialogRecyclerView
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater

/**
 * An [RecyclerView.Adapter] that displays a list of [Genre] choices.
 * @param listener A [ClickableListListener] to bind interactions to.
 * @author OxygenCobalt.
 */
class GenreChoiceAdapter(private val listener: ClickableListListener) :
    RecyclerView.Adapter<GenreChoiceViewHolder>() {
    private var genres = listOf<Genre>()

    override fun getItemCount() = genres.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        GenreChoiceViewHolder.new(parent)

    override fun onBindViewHolder(holder: GenreChoiceViewHolder, position: Int) =
        holder.bind(genres[position], listener)

    /**
     * Immediately update the [Genre] choices.
     * @param newGenres The new [Genre]s to show.
     */
    fun submitList(newGenres: List<Genre>) {
        if (newGenres != genres) {
            genres = newGenres
            @Suppress("NotifyDataSetChanged") notifyDataSetChanged()
        }
    }
}

/**
 * A [DialogRecyclerView.ViewHolder] that displays a smaller variant of a typical [Genre] item, for
 * use with [GenreChoiceAdapter]. Use [new] to create an instance.
 */
class GenreChoiceViewHolder(private val binding: ItemPickerChoiceBinding) :
    DialogRecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     * @param genre The new [Genre] to bind.
     * @param listener A [ClickableListListener] to bind interactions to.
     */
    fun bind(genre: Genre, listener: ClickableListListener) {
        binding.root.setOnClickListener { listener.onClick(genre) }
        binding.pickerImage.bind(genre)
        binding.pickerName.text = genre.resolveName(binding.context)
    }

    companion object {
        /**
         * Create a new instance.
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun new(parent: View) =
            GenreChoiceViewHolder(ItemPickerChoiceBinding.inflate(parent.context.inflater))
    }
}
