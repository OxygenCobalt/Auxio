package org.oxycblt.auxio.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentLibraryListBinding
import org.oxycblt.auxio.library.adapters.ArtistAdapter
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.recycler.ClickListener
import org.oxycblt.auxio.recycler.applyDivider

class LibraryListFragment : Fragment() {

    private val musicModel: MusicViewModel by activityViewModels {
        MusicViewModel.Factory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentLibraryListBinding>(
            inflater, R.layout.fragment_library_list, container, false
        )

        binding.libraryRecycler.adapter = ArtistAdapter(
            musicModel.artists.value!!,
            ClickListener { artist ->
                findNavController().navigate(
                    LibraryListFragmentDirections.actionShowArtist(artist.id)
                )
            }
        )
        binding.libraryRecycler.applyDivider()
        binding.libraryRecycler.setHasFixedSize(true)

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }
}
