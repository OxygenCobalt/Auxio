package org.oxycblt.auxio.music.locations

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemNewMusicLocationBinding
import org.oxycblt.auxio.databinding.ItemNewPlaylistChoiceBinding
import org.oxycblt.auxio.list.recycler.DialogRecyclerView
import org.oxycblt.auxio.music.decision.AddToPlaylistDialog
import org.oxycblt.auxio.util.inflater

/**
 * A purely-visual [RecyclerView.Adapter] that acts as a footer providing a "New Playlist" choice in
 * [AddToPlaylistDialog].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class NewLocationFooterAdapter(private val listener: Listener) :
    RecyclerView.Adapter<NewLocationFooterViewHolder>() {
    override fun getItemCount() = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NewLocationFooterViewHolder.from(parent)

    override fun onBindViewHolder(holder: NewLocationFooterViewHolder, position: Int) {
        holder.bind(listener)
    }

    /** A listener for [NewLocationFooterAdapter] interactions. */
    interface Listener {
        /**
         * Called when the footer has been pressed, requesting to create a new location.
         */
        fun onNewLocation()
    }
}

/**
 * A [RecyclerView.ViewHolder] that displays a "New Playlist" choice in [NewPlaylistFooterAdapter].
 * Use [from] to create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class NewLocationFooterViewHolder
private constructor(private val binding: ItemNewMusicLocationBinding) :
    DialogRecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param listener A [NewLocationFooterAdapter.Listener] to bind interactions to.
     */
    fun bind(listener: NewLocationFooterAdapter.Listener) {
        binding.root.setOnClickListener { listener.onNewLocation() }
    }

    companion object {
        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            NewLocationFooterViewHolder(
                ItemNewMusicLocationBinding.inflate(parent.context.inflater))
    }
}
