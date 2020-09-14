package org.oxycblt.auxio.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.ClickListener
import org.oxycblt.auxio.MainFragmentDirections
import org.oxycblt.auxio.databinding.FragmentLibraryBinding
import org.oxycblt.auxio.library.adapters.ArtistAdapter
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.models.Artist
import org.oxycblt.auxio.theme.applyDivider

class LibraryFragment : Fragment() {

    private val musicModel: MusicViewModel by activityViewModels()
    private val libraryModel: LibraryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLibraryBinding.inflate(inflater)

        binding.libraryRecycler.adapter = ArtistAdapter(
            musicModel.artists.value!!,
            ClickListener {
                navToArtist(it)
            }
        )

        binding.libraryRecycler.applyDivider()
        binding.libraryRecycler.setHasFixedSize(true)

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        libraryModel.isAlreadyNavigating = false
    }

    private fun navToArtist(artist: Artist) {
        if (!libraryModel.isAlreadyNavigating) {
            libraryModel.isAlreadyNavigating = true

            // When navigation, pass the artistImage of the item as a shared element to create
            // the image popup.
            findNavController().navigate(
                MainFragmentDirections.actionShowArtist(artist.id)
            )
        }
    }
}
