package org.oxycblt.auxio.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentLibraryBinding
import org.oxycblt.auxio.recycler.adapters.ArtistAdapter
import org.oxycblt.auxio.recycler.applyDivider
import org.oxycblt.auxio.recycler.viewholders.ClickListener

class LibraryFragment : Fragment() {

    private val libraryModel: LibraryViewModel by lazy {
        ViewModelProvider(this).get(LibraryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentLibraryBinding>(
            inflater, R.layout.fragment_library, container, false
        )

        binding.libraryRecycler.adapter = ArtistAdapter(
            libraryModel.artists.value!!,
            ClickListener { artist ->
                Log.d(this::class.simpleName, artist.name)
            }
        )
        binding.libraryRecycler.applyDivider()
        binding.libraryRecycler.setHasFixedSize(true)

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }
}
