package org.oxycblt.auxio.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentSongsBinding
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.recycler.adapters.SongAdapter
import org.oxycblt.auxio.recycler.applyDivider
import org.oxycblt.auxio.recycler.viewholders.ClickListener

class SongsFragment : Fragment() {

    private val musicModel: MusicViewModel by activityViewModels {
        MusicViewModel.Factory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentSongsBinding>(
            inflater, R.layout.fragment_songs, container, false
        )

        binding.songRecycler.adapter = SongAdapter(
            musicModel.songs.value!!,
            ClickListener { song ->
                Log.d(this::class.simpleName, song.name)
            }
        )
        binding.songRecycler.applyDivider()
        binding.songRecycler.setHasFixedSize(true)

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }
}
