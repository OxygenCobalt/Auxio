/*
 * Copyright (c) 2024 Auxio Project
 * DetailFragment.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package org.oxycblt.auxio.detail

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.transition.MaterialSharedAxis
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentDetailBinding
import org.oxycblt.auxio.detail.list.DetailListAdapter
import org.oxycblt.auxio.list.Divider
import org.oxycblt.auxio.list.Header
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.ListViewModel
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.util.getDimenPixels
import org.oxycblt.auxio.util.overrideOnOverflowMenuClick
import org.oxycblt.auxio.util.setFullWidthLookup

abstract class DetailFragment<P : MusicParent, C : Music> :
    ListFragment<C, FragmentDetailBinding>(),
    DetailListAdapter.Listener<C>,
    AppBarLayout.OnOffsetChangedListener {
    protected val detailModel: DetailViewModel by activityViewModels()
    override val listModel: ListViewModel by activityViewModels()
    override val musicModel: MusicViewModel by activityViewModels()
    override val playbackModel: PlaybackViewModel by activityViewModels()

    private var spacingSmall = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Detail transitions are always on the X axis. Shared element transitions are more
        // semantically correct, but are also too buggy to be sensible.
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentDetailBinding.inflate(inflater)

    abstract fun getDetailListAdapter(): DetailListAdapter

    override fun getSelectionToolbar(binding: FragmentDetailBinding) =
        binding.detailSelectionToolbar

    override fun onBindingCreated(binding: FragmentDetailBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        // --- UI SETUP ---
        binding.detailAppbar.addOnOffsetChangedListener(this)

        binding.detailNormalToolbar.apply {
            setNavigationOnClickListener { findNavController().navigateUp() }
            setOnMenuItemClickListener(this@DetailFragment)
            overrideOnOverflowMenuClick { onOpenParentMenu() }
        }

        binding.detailRecycler.apply {
            adapter = getDetailListAdapter()
            (layoutManager as GridLayoutManager).setFullWidthLookup {
                if (it != 0) {
                    val item =
                        detailModel.artistSongList.value.getOrElse(it - 1) {
                            return@setFullWidthLookup false
                        }
                    item is Divider || item is Header
                } else {
                    true
                }
            }
        }

        spacingSmall = requireContext().getDimenPixels(R.dimen.spacing_small)
    }

    override fun onDestroyBinding(binding: FragmentDetailBinding) {
        super.onDestroyBinding(binding)
        binding.detailAppbar.removeOnOffsetChangedListener(this)
        binding.detailNormalToolbar.setOnMenuItemClickListener(null)
        binding.detailRecycler.adapter = null
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val binding = requireBinding()
        val range = appBarLayout.totalScrollRange
        val ratio = abs(verticalOffset.toFloat()) / range.toFloat()

        val outRatio = min(ratio * 2, 1f)
        val detailHeader = binding.detailHeader
        detailHeader.scaleX = 1 - 0.05f * outRatio
        detailHeader.scaleY = 1 - 0.05f * outRatio
        detailHeader.alpha = 1 - outRatio

        val inRatio = max(ratio - 0.5f, 0f) * 2
        val detailContent = binding.detailToolbarContent
        detailContent.alpha = inRatio
        detailContent.translationY = spacingSmall * (1 - inRatio)
    }

    abstract fun onOpenParentMenu()
}
