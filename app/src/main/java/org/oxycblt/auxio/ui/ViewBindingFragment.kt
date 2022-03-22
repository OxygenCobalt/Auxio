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

/** A fragment enabling ViewBinding inflation and usage across the fragment lifecycle. */
abstract class ViewBindingFragment<T : ViewBinding> : Fragment() {
    private var mBinding: T? = null

    abstract fun onCreateBinding(inflater: LayoutInflater): T
    abstract fun onBindingCreated(binding: T, savedInstanceState: Bundle?)
    abstract fun onDestroyBinding(binding: T)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = onCreateBinding(inflater).also { mBinding = it }
        onBindingCreated(binding, savedInstanceState)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onDestroyBinding(requireBinding())
        mBinding = null
    }

    protected val binding: T?
        get() = mBinding

    protected fun requireBinding(): T {
        return requireNotNull(mBinding) {
            "ViewBinding was not available, as the fragment is not in a valid state"
        }
    }
}
