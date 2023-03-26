/*
 * Copyright (c) 2023 Auxio Project
 * PickerDialogFragment.kt is part of Auxio.
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
 
package org.oxycblt.auxio.picker

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogMusicPickerBinding
import org.oxycblt.auxio.list.ClickableListListener
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.ui.ViewBindingDialogFragment
import org.oxycblt.auxio.util.collectImmediately

/**
 * A [ViewBindingDialogFragment] that acts as the base for a "picker" UI, shown when a given choice
 * is ambiguous.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
abstract class PickerDialogFragment<T : Music> :
    ViewBindingDialogFragment<DialogMusicPickerBinding>(), ClickableListListener<T> {
    // Okay to leak this since the Listener will not be called until after initialization.
    private val choiceAdapter = ChoiceAdapter(@Suppress("LeakingThis") this)

    /** The string resource to use in the dialog title. */
    abstract val titleRes: Int
    /** The [StateFlow] of choices to show in the picker. */
    abstract val pickerChoices: StateFlow<PickerChoices<T>?>
    /** Called when the choice list should be initialized from the stored arguments. */
    abstract fun initChoices()

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogMusicPickerBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder.setTitle(titleRes).setNegativeButton(R.string.lbl_cancel, null)
    }

    override fun onBindingCreated(binding: DialogMusicPickerBinding, savedInstanceState: Bundle?) {
        binding.pickerRecycler.apply {
            itemAnimator = null
            adapter = choiceAdapter
        }

        initChoices()
        collectImmediately(pickerChoices) { item ->
            if (item != null) {
                // Make sure the choices align with any changes in the music library.
                choiceAdapter.update(item.choices, UpdateInstructions.Diff)
            } else {
                // Not showing any choices, navigate up.
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyBinding(binding: DialogMusicPickerBinding) {
        binding.pickerRecycler.adapter = null
    }

    override fun onClick(item: T, viewHolder: RecyclerView.ViewHolder) {
        findNavController().navigateUp()
    }
}
