package org.oxycblt.auxio.recycler.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.SongItemBinding
import org.oxycblt.auxio.music.models.Song
import org.oxycblt.auxio.recycler.viewholders.SongViewHolder

class SongAdapter(private val data: List<Song>) : RecyclerView.Adapter<SongViewHolder>() {

    override fun getItemCount(): Int = data.size

    /*
    private var time = 0
    private var inflationCount = 0
     */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val then = System.currentTimeMillis()

        val binding = SongItemBinding.inflate(LayoutInflater.from(parent.context))

        // Force the item to *actually* be the screen width so ellipsizing can work.
        binding.root.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
        )

        /*
        time += (System.currentTimeMillis() - then).toInt()
        inflationCount++

        if (inflationCount == 10) {
            Log.d(
                this::class.simpleName,
                "Initial inflation took ${time}ms"
            )
        }
         */

        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = data[position]

        holder.bind(song)
    }
}
