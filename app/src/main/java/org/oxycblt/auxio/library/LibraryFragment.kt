package org.oxycblt.auxio.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentLibraryBinding>(
            inflater, R.layout.fragment_library, container, false
        )

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }
}
