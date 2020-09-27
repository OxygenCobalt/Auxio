package org.oxycblt.auxio.library.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemAlbumBinding
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.bindAlbumSongs
import org.oxycblt.auxio.recycler.BaseViewHolder
import org.oxycblt.auxio.recycler.ClickListener

class AlbumAdapter(
    private val data: List<Album>,
    private val listener: ClickListener<Album>
) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAlbumBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ViewHolder(
        private val binding: ItemAlbumBinding
    ) : BaseViewHolder<Album>(binding, listener) {

        override fun onBind(model: Album) {
            binding.album = model
            binding.albumName.requestLayout()
        }
    }
}
