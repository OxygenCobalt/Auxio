/*
 * Copyright (c) 2023 Auxio Project
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

import android.content.Context
import android.util.AttributeSet
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceGroupAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.BackportMaterialDividerItemDecoration
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.logD

class PreferenceHeaderItemDecoration
@JvmOverloads
constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = R.attr.materialDividerStyle,
    orientation: Int = LinearLayoutManager.VERTICAL
) : BackportMaterialDividerItemDecoration(context, attributeSet, defStyleAttr, orientation) {
    override fun shouldDrawDivider(position: Int, adapter: RecyclerView.Adapter<*>?) =
        try {
            logD(position)
            (adapter as PreferenceGroupAdapter).getItem(position + 1) is PreferenceCategory
        } catch (e: ClassCastException) {
            false
        } catch (e: IndexOutOfBoundsException) {
            false
        }
}
