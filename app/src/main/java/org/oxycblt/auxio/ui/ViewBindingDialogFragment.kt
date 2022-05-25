/*
 * Copyright (c) 2022 Auxio Project
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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.oxycblt.auxio.util.logD

/**
 * A dialog fragment enabling ViewBinding inflation and usage across the dialog fragment lifecycle.
 * @author OxygenCobalt
 */
abstract class ViewBindingDialogFragment<T : ViewBinding> : DialogFragment() {
    private var _binding: T? = null

    /**
     * Inflate the binding from the given [inflater]. This should usually be done by the binding
     * implementation's inflate function.
     */
    protected abstract fun onCreateBinding(inflater: LayoutInflater): T

    /** Called during [onCreateDialog]. Dialog elements should be configured here. */
    protected open fun onConfigDialog(builder: AlertDialog.Builder) {}

    /**
     * Called during [onViewCreated] when the binding was successfully inflated and set as the view.
     * This is where view setup should occur.
     */
    protected open fun onBindingCreated(binding: T, savedInstanceState: Bundle?) {}

    /**
     * Called during [onDestroyView] when the binding should be destroyed and all callbacks or
     * leaking elements be released.
     */
    protected open fun onDestroyBinding(binding: T) {}

    /** Maybe get the binding. This will be null outside of the fragment view lifecycle. */
    protected val binding: T?
        get() = _binding

    /**
     * Get the binding under the assumption that the fragment has a view at this state in the
     * lifecycle. This will throw an exception if the fragment is not in a valid lifecycle.
     */
    protected fun requireBinding(): T {
        return requireNotNull(_binding) {
            "ViewBinding was available. Fragment should be a valid state " +
                "right now, but instead it was ${lifecycle.currentState}"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = onCreateBinding(inflater).also { _binding = it }.root

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        MaterialAlertDialogBuilder(requireActivity(), theme).run {
            onConfigDialog(this)
            create()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBindingCreated(requireBinding(), savedInstanceState)
        (requireDialog() as AlertDialog).setView(view)
        logD("Fragment created")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onDestroyBinding(requireBinding())
        _binding = null
        logD("Fragment destroyed")
    }
}
