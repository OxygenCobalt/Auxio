package org.oxycblt.auxio.settings.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import org.oxycblt.auxio.R

/**
 * A wrapper around [DialogFragment] that allows the usage of the standard Auxio lifecycle
 * override [onCreateView] and [onDestroyView], but with a proper dialog being created.
 */
abstract class LifecycleDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity(), R.style.Theme_CustomDialog)

        onConfigDialog(builder)

        return builder.create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireDialog() as AlertDialog).setView(view)
    }

    protected open fun onConfigDialog(builder: AlertDialog.Builder) {}
}
