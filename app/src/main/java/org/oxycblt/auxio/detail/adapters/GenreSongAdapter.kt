package org.oxycblt.auxio.detail.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemGenreHeaderBinding
import org.oxycblt.auxio.databinding.ItemGenreSongBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.DiffCallback
import org.oxycblt.auxio.recycler.viewholders.BaseViewHolder

/**
 * An adapter for displaying the [Song]s of a genre.
 */
class GenreSongAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val detailModel: DetailViewModel,
    private val doOnClick: (data: Song) -> Unit,
    private val doOnLongClick: (data: Song, view: View) -> Unit
) : ListAdapter<BaseModel, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Genre -> GENRE_HEADER_ITEM_TYPE
            is Song -> GENRE_SONG_ITEM_TYPE

            else -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            GENRE_HEADER_ITEM_TYPE -> GenreHeaderViewHolder(
                ItemGenreHeaderBinding.inflate(LayoutInflater.from(parent.context))
            )

            GENRE_SONG_ITEM_TYPE -> GenreSongViewHolder(
                ItemGenreSongBinding.inflate(LayoutInflater.from(parent.context)),
            )

            else -> error("Bad viewholder item type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Genre -> (holder as GenreHeaderViewHolder).bind(item)
            is Song -> (holder as GenreSongViewHolder).bind(item)
        }
    }

    inner class GenreHeaderViewHolder(
        private val binding: ItemGenreHeaderBinding
    ) : BaseViewHolder<Genre>(binding, null, null) {
        override fun onBind(data: Genre) {
            binding.genre = data
            binding.detailModel = detailModel
            binding.lifecycleOwner = lifecycleOwner
        }
    }

    inner class GenreSongViewHolder(
        private val binding: ItemGenreSongBinding,
    ) : BaseViewHolder<Song>(binding, doOnClick, doOnLongClick) {

        override fun onBind(data: Song) {
            binding.song = data

            binding.songName.requestLayout()
            binding.songInfo.requestLayout()
        }
    }

    companion object {
        const val GENRE_HEADER_ITEM_TYPE = 0xA020
        const val GENRE_SONG_ITEM_TYPE = 0xA021
    }
}
