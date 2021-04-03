package org.oxycblt.auxio.settings.ui

import androidx.appcompat.app.AlertDialog

class IntListPrefDialog(private val pref: IntListPreference) : LifecycleDialog() {
    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder.setTitle(pref.title)

        builder.setSingleChoiceItems(pref.entries, pref.getValueIndex()) { _, index ->
            pref.setValueIndex(index)

            dismiss()
        }

        builder.setNegativeButton(android.R.string.cancel, null)
    }

    companion object {
        const val TAG = "TAG_INT_PREF_DIALOG"
    }
}
