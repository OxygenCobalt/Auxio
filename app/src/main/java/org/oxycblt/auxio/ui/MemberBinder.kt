package org.oxycblt.auxio.ui

import android.os.Looper
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
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
) : ReadOnlyProperty<Fragment, T>, LifecycleObserver {
    private var fragmentBinding: T? = null

    init {
        fragment.observeOwnerThroughCreation {
            lifecycle.addObserver(this@MemberBinder)
        }
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        check(Looper.myLooper() == Looper.getMainLooper()) {
            "View can only be accessed on the main thread."
        }

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

    @Suppress("UNUSED")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        fragmentBinding?.onDestroy()
        fragmentBinding = null
    }
}
