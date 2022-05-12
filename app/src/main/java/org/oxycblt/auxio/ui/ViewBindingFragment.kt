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
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import org.oxycblt.auxio.util.logD

/** A fragment enabling ViewBinding inflation and usage across the fragment lifecycle. */
abstract class ViewBindingFragment<T : ViewBinding> : Fragment() {
    private var _binding: T? = null

    protected abstract fun onCreateBinding(inflater: LayoutInflater): T
    protected open fun onBindingCreated(binding: T, savedInstanceState: Bundle?) {}
    protected open fun onDestroyBinding(binding: T) {}

    protected val binding: T?
        get() = _binding

    protected fun requireBinding(): T {
        return requireNotNull(_binding) {
            "ViewBinding was not available, as the fragment was not in a valid state"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = onCreateBinding(inflater).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBindingCreated(requireBinding(), savedInstanceState)
        logD("Fragment created")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onDestroyBinding(requireBinding())
        _binding = null
    }
}
