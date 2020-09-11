package org.oxycblt.auxio.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentArtistDetailBinding
import org.oxycblt.auxio.music.MusicViewModel

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

        binding.artist = musicModel.artists.value?.find { it.id == artistId }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }
}
