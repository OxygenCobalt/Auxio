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

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.oxycblt.auxio.util.logD

abstract class ViewBindingDialogFragment<T : ViewBinding> : DialogFragment() {
    private var mBinding: T? = null

    protected abstract fun onCreateBinding(inflater: LayoutInflater): T
    protected open fun onBindingCreated(binding: T, savedInstanceState: Bundle?) {}
    protected open fun onDestroyBinding(binding: T) {}
    protected open fun onConfigDialog(builder: AlertDialog.Builder) {}

    protected val binding: T?
        get() = mBinding

    protected fun requireBinding(): T {
        return requireNotNull(mBinding) {
            "ViewBinding was not available, as the fragment was not in a valid state"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = onCreateBinding(inflater).also { mBinding = it }.root

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireActivity(), theme).run {
            onConfigDialog(this)
            create()
        }
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
        mBinding = null
    }
}
