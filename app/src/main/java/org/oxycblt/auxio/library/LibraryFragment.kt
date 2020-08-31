package org.oxycblt.auxio.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentLibraryBinding
import org.oxycblt.auxio.recycler.adapters.AlbumAdapter
import org.oxycblt.auxio.recycler.applyDivider

class LibraryFragment : Fragment() {

    private val libraryModel: LibraryViewModel by lazy {
        ViewModelProvider(this).get(LibraryViewModel::class.java)
    }

    private val inflateJob = Job()
    private val mainScope = CoroutineScope(
        inflateJob + Dispatchers.Main
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentLibraryBinding>(
            inflater, R.layout.fragment_library, container, false
        )

        // Offload the initial layout creation to a coroutine so that it doesn't hold up
        // the UI thread. Hacky but it results in a smoother experience.
        mainScope.launch {
            binding.libraryRecycler.adapter = AlbumAdapter(libraryModel.albums.value!!)
            binding.libraryRecycler.visibility = View.VISIBLE
        }

        // binding.libraryRecycler.adapter = adapter
        binding.libraryRecycler.applyDivider()
        binding.libraryRecycler.setHasFixedSize(true)

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        inflateJob.cancel()
    }
}
