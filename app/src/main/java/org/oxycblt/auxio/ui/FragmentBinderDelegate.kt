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
import java.lang.IllegalStateException
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A delegate that creates a binding that can be used as a member variable without nullability or
 * memory leaks.
 */
fun <T : ViewBinding> Fragment.memberBinding(
    viewBindingFactory: (LayoutInflater) -> T,
    disposeEvents: T.() -> Unit = {}
) = FragmentBinderDelegate(this, viewBindingFactory, disposeEvents)

/**
 * The delegate for the [binder] shortcut function.
 * Adapted from KAHelpers (https://github.com/FunkyMuse/KAHelpers/tree/master/viewbinding)
 */
class FragmentBinderDelegate<T : ViewBinding>(
    private val fragment: Fragment,
    private val binder: (LayoutInflater) -> T,
    private val disposeEvents: T.() -> Unit
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

                viewLifecycleOwnerLiveData.observe(this@observeOwnerThroughCreation) { owner ->
                    owner.viewOwner()
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

        return binder(LayoutInflater.from(thisRef.requireContext())).also { fragmentBinding = it }
    }

    @Suppress("UNUSED")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun dispose() {
        fragmentBinding?.disposeEvents()
        fragmentBinding = null
    }
}
