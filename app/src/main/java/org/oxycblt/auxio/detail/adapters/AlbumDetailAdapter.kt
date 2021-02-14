package org.oxycblt.auxio.detail.adapters

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
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.recycler.DiffCallback
import org.oxycblt.auxio.recycler.viewholders.BaseViewHolder
import org.oxycblt.auxio.recycler.viewholders.Highlightable
import org.oxycblt.auxio.ui.Accent
import org.oxycblt.auxio.ui.applyAccents
import org.oxycblt.auxio.ui.disable
import org.oxycblt.auxio.ui.inflater
import org.oxycblt.auxio.ui.setTextColorResource

/**
 * An adapter for displaying the details and [Song]s of an [Album]
 */
class AlbumDetailAdapter(
    private val detailModel: DetailViewModel,
    private val playbackModel: PlaybackViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val doOnClick: (data: Song) -> Unit,
    private val doOnLongClick: (view: View, data: Song) -> Unit
) : ListAdapter<BaseModel, RecyclerView.ViewHolder>(DiffCallback()) {

    private var currentSong: Song? = null
    private var lastHolder: Highlightable? = null

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
                ItemAlbumHeaderBinding.inflate(parent.context.inflater)
            )
            ALBUM_SONG_ITEM_TYPE -> AlbumSongViewHolder(
                ItemAlbumSongBinding.inflate(parent.context.inflater)
            )

            else -> error("Invalid ViewHolder item type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Album -> (holder as AlbumHeaderViewHolder).bind(item)
            is Song -> (holder as AlbumSongViewHolder).bind(item)
        }

        if (currentSong != null && position > 0) {
            if (getItem(position).id == currentSong?.id) {
                // Reset the last ViewHolder before assigning the new, correct one to be highlighted
                lastHolder?.setHighlighted(false)
                lastHolder = (holder as Highlightable)
                holder.setHighlighted(true)
            } else {
                (holder as Highlightable).setHighlighted(false)
            }
        }
    }

    /**
     * Update the current song that this adapter should be watching for to highlight.
     * @param song The [Song] to highlight if found, null to clear any highlighted ViewHolders
     */
    fun highlightSong(song: Song?, recycler: RecyclerView) {
        // Clear out the last ViewHolder as a song update usually signifies that this current
        // ViewHolder is likely invalid.
        lastHolder?.setHighlighted(false)
        lastHolder = null

        currentSong = song

        if (song != null) {
            // Use existing data instead of having to re-sort it.
            val pos = currentList.indexOfFirst {
                it.name == song.name && it is Song
            }

            // Check if the ViewHolder for this song is visible, if it is then highlight it.
            // If the ViewHolder is not visible, then the adapter should take care of it if
            // it does become visible.
            recycler.layoutManager?.findViewByPosition(pos)?.let { child ->
                recycler.getChildViewHolder(child)?.let {
                    lastHolder = it as Highlightable

                    lastHolder?.setHighlighted(true)
                }
            }
        }
    }

    inner class AlbumHeaderViewHolder(
        private val binding: ItemAlbumHeaderBinding
    ) : BaseViewHolder<Album>(binding) {

        override fun onBind(data: Album) {
            binding.album = data
            binding.detailModel = detailModel
            binding.playbackModel = playbackModel
            binding.lifecycleOwner = lifecycleOwner

            binding.albumShuffleButton.applyAccents(true)
            binding.albumPlayButton.applyAccents(false)

            if (data.songs.size < 2) {
                binding.albumSortButton.disable()
            }
        }
    }

    inner class AlbumSongViewHolder(
        private val binding: ItemAlbumSongBinding,
    ) : BaseViewHolder<Song>(binding, doOnClick, doOnLongClick), Highlightable {
        private val normalTextColor = binding.songName.currentTextColor
        private val inactiveTextColor = binding.songTrack.currentTextColor

        override fun onBind(data: Song) {
            binding.song = data

            binding.songName.requestLayout()
        }

        override fun setHighlighted(isHighlighted: Boolean) {
            if (isHighlighted) {
                val accent = Accent.get()

                binding.songName.setTextColorResource(accent.color)
                binding.songTrack.setTextColorResource(accent.color)
            } else {
                binding.songName.setTextColor(normalTextColor)
                binding.songTrack.setTextColor(inactiveTextColor)
            }
        }
    }

    companion object {
        const val ALBUM_HEADER_ITEM_TYPE = 0xA007
        const val ALBUM_SONG_ITEM_TYPE = 0xA008
    }
}
