package org.oxycblt.auxio.playback.ui.swiper

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemCoverBinding
import org.oxycblt.auxio.list.adapter.FlexibleListAdapter
import org.oxycblt.auxio.list.adapter.SimpleDiffCallback
import org.oxycblt.auxio.playback.ui.stepper.PlayerFastSeekOverlay
import org.oxycblt.auxio.util.inflater
import org.oxycblt.musikr.Song

/**
 * A [FlexibleListAdapter] that hosts [CoverViewHolder]s containing a [Song]'s cover and step
 * gesture overlays.
 *
 * @param listener The [PlayerFastSeekOverlay.PerformListener] that step gesture events will be
 *  forwarded to
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class CoverPagerAdapter(
    private val listener: PlayerFastSeekOverlay.PerformListener
) : FlexibleListAdapter<Song, CoverViewHolder>(CoverViewHolder.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int) = CoverViewHolder.from(parent)

    override fun onBindViewHolder(viewHolder: CoverViewHolder, pos: Int) {
        viewHolder.bind(currentList[pos], listener)
    }
}

/**
 * A [RecyclerView.ViewHolder] that displays a [Song]'s cover and step gesture overlays.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class CoverViewHolder private constructor(private val binding: ItemCoverBinding) :
    RecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param song The new [Song] to bind.
     * @param listener An [PlayerFastSeekOverlay.PerformListener] to bind fast seek interactions to.
     */
    fun bind(song: Song, listener: PlayerFastSeekOverlay.PerformListener) {
        binding.cover.bind(song)
        binding.coverFastSeekOverlay.performListener = listener
    }

    companion object {
        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: ViewGroup) = CoverViewHolder(ItemCoverBinding.inflate(parent.context.inflater, parent, false))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<Song>() {
                override fun areContentsTheSame(oldItem: Song, newItem: Song) =
                    oldItem.cover == newItem.cover
            }
    }
}
