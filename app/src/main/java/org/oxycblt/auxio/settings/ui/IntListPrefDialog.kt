/*
 * Copyright (c) 2021 Auxio Project
 * IntListPrefDialog.kt is part of Auxio.
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
