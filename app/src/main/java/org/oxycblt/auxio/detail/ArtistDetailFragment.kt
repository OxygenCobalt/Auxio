package org.oxycblt.auxio.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.oxycblt.auxio.ClickListener
import org.oxycblt.auxio.databinding.FragmentArtistDetailBinding
import org.oxycblt.auxio.detail.adapters.DetailAlbumAdapter
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.theme.applyDivider

class ArtistDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentArtistDetailBinding.inflate(inflater)

        // I honestly don't want to turn of the any data classes into a parcelables due to how
        // many lists they store, so just pick up the artist id and find it from musicModel.
        val musicModel: MusicViewModel by activityViewModels()
        val artistId = ArtistDetailFragmentArgs.fromBundle(requireArguments()).artistId

        val artist = musicModel.artists.value?.find { it.id == artistId }!!

        binding.lifecycleOwner = this
        binding.artist = artist

        binding.albumRecycler.adapter = DetailAlbumAdapter(
            artist.albums,
            ClickListener {
                Log.d(this::class.simpleName, it.name)
            }
        )
        binding.albumRecycler.applyDivider()
        binding.albumRecycler.setHasFixedSize(true)

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }
}
