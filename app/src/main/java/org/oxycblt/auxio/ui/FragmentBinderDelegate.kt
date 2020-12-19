package org.oxycblt.auxio.ui

import android.os.Looper
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A delegate that creates a binding that can be used as a member variable without nullability or
 * memory leaks.
 * @param bindingFactory The ViewBinding inflation method that should be used
 * @param onDestroy      Any code that should be run when the binding is destroyed.
 */
fun <T : ViewBinding> Fragment.memberBinding(
    bindingFactory: (LayoutInflater) -> T,
    onDestroy: T.() -> Unit = {}
) = FragmentBinderDelegate(this, bindingFactory, onDestroy)

/**
 * The delegate for the [memberBinding] shortcut function.
 * Adapted from KAHelpers (https://github.com/FunkyMuse/KAHelpers/tree/master/viewbinding)
 */
class FragmentBinderDelegate<T : ViewBinding>(
    private val fragment: Fragment,
    private val inflate: (LayoutInflater) -> T,
    private val onDestroy: T.() -> Unit
) : ReadOnlyProperty<Fragment, T>, LifecycleObserver {
    private var fragmentBinding: T? = null

    init {
        fragment.observeOwnerThroughCreation {
            lifecycle.addObserver(this@FragmentBinderDelegate)
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

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalThreadStateException("View can only be accessed on the main thread.")
        }

        val binding = fragmentBinding
        if (binding != null) {
            return binding
        }

        val lifecycle = fragment.viewLifecycleOwner.lifecycle

        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            throw IllegalStateException("Fragment views are destroyed.")
        }

        return inflate(LayoutInflater.from(thisRef.requireContext())).also { fragmentBinding = it }
    }

    @Suppress("UNUSED")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun dispose() {
        fragmentBinding?.onDestroy()
        fragmentBinding = null
    }
}
