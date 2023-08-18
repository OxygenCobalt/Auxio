/*
 * Copyright (c) 2022 Auxio Project
 * ViewBindingBottomSheetDialogFragment.kt is part of Auxio.
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
 
package org.oxycblt.auxio.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BackportBottomSheetBehavior
import com.google.android.material.bottomsheet.BackportBottomSheetDialog
import com.google.android.material.bottomsheet.BackportBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.oxycblt.auxio.util.getDimenPixels
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A lifecycle-aware [DialogFragment] that automatically manages the [ViewBinding] lifecycle as a
 * [BottomSheetDialogFragment].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
abstract class ViewBindingBottomSheetDialogFragment<VB : ViewBinding> :
    BackportBottomSheetDialogFragment() {
    private var _binding: VB? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): BackportBottomSheetDialog =
        TweakedBottomSheetDialog(requireContext(), theme)

    /**
     * Inflate the [ViewBinding] during [onCreateView].
     *
     * @param inflater The [LayoutInflater] to inflate the [ViewBinding] with.
     * @return A new [ViewBinding] instance.
     * @see onCreateView
     */
    protected abstract fun onCreateBinding(inflater: LayoutInflater): VB

    /**
     * Configure the newly-inflated [ViewBinding] during [onViewCreated].
     *
     * @param binding The [ViewBinding] to configure.
     * @param savedInstanceState The previously saved state of the UI.
     * @see onViewCreated
     */
    protected open fun onBindingCreated(binding: VB, savedInstanceState: Bundle?) {}

    /**
     * Free memory held by the [ViewBinding] during [onDestroyView]
     *
     * @param binding The [ViewBinding] to release.
     * @see onDestroyView
     */
    protected open fun onDestroyBinding(binding: VB) {}

    /** The [ViewBinding], or null if it has not been inflated yet. */
    protected val binding: VB?
        get() = _binding

    /**
     * Get the [ViewBinding] under the assumption that it has been inflated.
     *
     * @return The currently-inflated [ViewBinding].
     * @throws IllegalStateException if the [ViewBinding] is not inflated.
     */
    protected fun requireBinding() =
        requireNotNull(_binding) {
            "ViewBinding was available. Fragment should be a valid state " +
                "right now, but instead it was ${lifecycle.currentState}"
        }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = onCreateBinding(inflater).also { _binding = it }.root

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBindingCreated(requireBinding(), savedInstanceState)
        logD("Fragment created")
    }

    final override fun onDestroyView() {
        super.onDestroyView()
        onDestroyBinding(unlikelyToBeNull(_binding))
        // Clear binding
        _binding = null
        logD("Fragment destroyed")
    }

    private inner class TweakedBottomSheetDialog
    @JvmOverloads
    constructor(context: Context, @StyleRes theme: Int = 0) :
        BackportBottomSheetDialog(context, theme) {
        private var avoidUnusableCollapsedState = false

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            // Automatic peek height calculations are bugged in phone landscape mode and show only
            // 10% of the dialog. Just disable it in that case and go directly from expanded ->
            // hidden.
            val metrics = context.resources.displayMetrics
            avoidUnusableCollapsedState =
                metrics.heightPixels - metrics.widthPixels <
                    context.getDimenPixels(
                        com.google.android.material.R.dimen.design_bottom_sheet_peek_height_min)
            behavior.skipCollapsed = avoidUnusableCollapsedState
        }

        override fun onStart() {
            super.onStart()
            if (avoidUnusableCollapsedState) {
                // skipCollapsed isn't enough, also need to immediately snap to expanded state.
                behavior.state = BackportBottomSheetBehavior.STATE_EXPANDED
            }
        }
    }
}
