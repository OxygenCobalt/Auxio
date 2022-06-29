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
 
package org.oxycblt.auxio.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A fragment enabling ViewBinding inflation and usage across the fragment lifecycle.
 * @author OxygenCobalt
 */
abstract class ViewBindingFragment<VB : ViewBinding> : Fragment() {
    private var _binding: VB? = null
    private var lifecycleObjects = mutableListOf<LifecycleObject<VB, *>>()

    /**
     * Inflate the binding from the given [inflater]. This should usually be done by the binding
     * implementation's inflate function.
     */
    protected abstract fun onCreateBinding(inflater: LayoutInflater): VB

    /**
     * Called during [onViewCreated] when the binding was successfully inflated and set as the view.
     * This is where view setup should occur.
     */
    protected open fun onBindingCreated(binding: VB, savedInstanceState: Bundle?) {}

    /**
     * Called during [onDestroyView] when the binding should be destroyed and all callbacks or
     * leaking elements be released.
     */
    protected open fun onDestroyBinding(binding: VB) {}

    /** Maybe get the binding. This will be null outside of the fragment view lifecycle. */
    protected val binding: VB?
        get() = _binding

    /**
     * Get the binding under the assumption that the fragment has a view at this state in the
     * lifecycle. This will throw an exception if the fragment is not in a valid lifecycle.
     */
    protected fun requireBinding(): VB {
        return requireNotNull(_binding) {
            "ViewBinding was available. Fragment should be a valid state " +
                "right now, but instead it was ${lifecycle.currentState}"
        }
    }

    /**
     * Shortcut to create a member bound to the lifecycle of this fragment. This is automatically
     * populated in onBindingCreated, and destroyed in onDestroyBinding.
     */
    fun <T> lifecycleObject(create: (VB) -> T): ReadOnlyProperty<Fragment, T> {
        lifecycleObjects.add(LifecycleObject(null, create))

        return object : ReadOnlyProperty<Fragment, T> {
            private val objIdx = lifecycleObjects.lastIndex

            @Suppress("UNCHECKED_CAST")
            override fun getValue(thisRef: Fragment, property: KProperty<*>) =
                requireNotNull(lifecycleObjects[objIdx].data) {
                    "Cannot access lifecycle object when view does not exist"
                }
                    as T
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = onCreateBinding(inflater).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = unlikelyToBeNull(_binding)
        lifecycleObjects.forEach { it.populate(binding) }
        onBindingCreated(binding, savedInstanceState)
        logD("Fragment created")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onDestroyBinding(unlikelyToBeNull(_binding))
        lifecycleObjects.forEach { it.clear() }
        _binding = null
        logD("Fragment destroyed")
    }

    private data class LifecycleObject<VB, T>(var data: T?, val create: (VB) -> T) {
        fun populate(binding: VB) {
            data = create(binding)
        }

        fun clear() {
            data = null
        }
    }
}
