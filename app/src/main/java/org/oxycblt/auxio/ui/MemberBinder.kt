/*
 * Copyright (c) 2021 Auxio Project
 * MemberBinder.kt is part of Auxio.
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

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.oxycblt.auxio.util.assertMainThread
import org.oxycblt.auxio.util.inflater
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A delegate that creates a binding that can be used as a member variable without nullability or
 * memory leaks.
 * @param inflate The ViewBinding inflation method that should be used
 * @param onDestroy What to do when the binding is destroyed
 */
fun <T : ViewDataBinding> Fragment.memberBinding(
    inflate: (LayoutInflater) -> T,
    onDestroy: T.() -> Unit = {}
) = MemberBinder(this, inflate, onDestroy)

/**
 * The delegate for the [memberBinding] shortcut function.
 * Adapted from KAHelpers (https://github.com/FunkyMuse/KAHelpers/tree/master/viewbinding)
 * @author OxygenCobalt
 */
class MemberBinder<T : ViewDataBinding>(
    private val fragment: Fragment,
    private val inflate: (LayoutInflater) -> T,
    private val onDestroy: T.() -> Unit
) : ReadOnlyProperty<Fragment, T>, LifecycleObserver, LifecycleEventObserver {
    private var fragmentBinding: T? = null

    init {
        fragment.observeOwnerThroughCreation {
            lifecycle.addObserver(this@MemberBinder)
        }
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        assertMainThread()

        val binding = fragmentBinding

        // If the fragment is already initialized, then just return that.
        if (binding != null) {
            return binding
        }

        val lifecycle = fragment.viewLifecycleOwner.lifecycle

        check(lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            "Fragment views are destroyed."
        }

        // Otherwise create the binding and return that.
        return inflate(thisRef.requireContext().inflater).also {
            fragmentBinding = it
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            fragmentBinding?.onDestroy()
            fragmentBinding = null
        }
    }

    private inline fun Fragment.observeOwnerThroughCreation(
        crossinline viewOwner: LifecycleOwner.() -> Unit
    ) {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)

                viewLifecycleOwnerLiveData.observe(this@observeOwnerThroughCreation) {
                    it.viewOwner()
                }
            }
        })
    }
}
