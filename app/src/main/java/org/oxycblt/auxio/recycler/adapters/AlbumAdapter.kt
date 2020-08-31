package org.oxycblt.auxio.recycler.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.AlbumItemBinding
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.recycler.viewholders.AlbumViewHolder

class AlbumAdapter(private val data: List<Album>) : RecyclerView.Adapter<AlbumViewHolder>() {

    override fun getItemCount(): Int = data.size

    /*
    private var time = 0
    private var inflationCount = 0
     */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        // val then = System.currentTimeMillis()

        val binding = AlbumItemBinding.inflate(LayoutInflater.from(parent.context))

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

        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = data[position]

        holder.bind(album)
    }
}
