package org.oxycblt.auxio.detail.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemAlbumHeaderBinding
import org.oxycblt.auxio.databinding.ItemAlbumSongBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.DiffCallback
import org.oxycblt.auxio.recycler.viewholders.BaseViewHolder
import org.oxycblt.auxio.ui.disable

/**
 * An adapter for displaying the details and [Song]s of an [Album]
 */
class AlbumDetailAdapter(
    private val detailModel: DetailViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val doOnClick: (data: Song) -> Unit,
    private val doOnLongClick: (data: Song, view: View) -> Unit
) : ListAdapter<BaseModel, RecyclerView.ViewHolder>(DiffCallback()) {
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Album -> ALBUM_HEADER_ITEM_TYPE
            is Song -> ALBUM_SONG_ITEM_TYPE

            else -> -1
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ALBUM_HEADER_ITEM_TYPE -> AlbumHeaderViewHolder(
                ItemAlbumHeaderBinding.inflate(LayoutInflater.from(parent.context))
            )
            ALBUM_SONG_ITEM_TYPE -> AlbumSongViewHolder(
                ItemAlbumSongBinding.inflate(LayoutInflater.from(parent.context))
            )

            else -> error("Invalid ViewHolder item type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Album -> (holder as AlbumHeaderViewHolder).bind(item)
            is Song -> (holder as AlbumSongViewHolder).bind(item)
        }
    }

    inner class AlbumHeaderViewHolder(
        private val binding: ItemAlbumHeaderBinding
    ) : BaseViewHolder<Album>(binding, null, null) {

        override fun onBind(data: Album) {
            binding.album = data
            binding.detailModel = detailModel
            binding.lifecycleOwner = lifecycleOwner

            if (data.songs.size < 2) {
                binding.albumSortButton.disable()
            }
        }
    }

    inner class AlbumSongViewHolder(
        private val binding: ItemAlbumSongBinding,
    ) : BaseViewHolder<Song>(binding, doOnClick, doOnLongClick) {
        override fun onBind(data: Song) {
            binding.song = data

            binding.songName.requestLayout()
        }
    }

    companion object {
        const val ALBUM_HEADER_ITEM_TYPE = 0xA024
        const val ALBUM_SONG_ITEM_TYPE = 0xA025
    }
}
