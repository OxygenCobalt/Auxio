package org.oxycblt.auxio.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import org.oxycblt.auxio.databinding.FragmentAlbumDetailBinding
import org.oxycblt.auxio.music.MusicViewModel

class AlbumDetailFragment : Fragment() {

    private val args: AlbumDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAlbumDetailBinding.inflate(inflater)

        // I honestly don't want to turn of the any data classes into parcelables due to how
        // many lists they store, so just pick up the artist id and find it from musicModel.
        val musicModel: MusicViewModel by activityViewModels()
        val album = musicModel.albums.value?.find { it.id == args.albumId }!!

        binding.lifecycleOwner = this
        binding.album = album

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }
}
