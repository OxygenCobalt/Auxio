/*
 * Copyright (c) 2023 Auxio Project
 * PreferenceHeaderItemDecoration.kt is part of Auxio.
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

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceGroupAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.BackportMaterialDividerItemDecoration
import org.oxycblt.auxio.R

/**
 * A [BackportMaterialDividerItemDecoration] that sets up the divider configuration to correctly
 * separate preference categories.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class PreferenceHeaderItemDecoration
@JvmOverloads
constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = R.attr.materialDividerStyle,
    orientation: Int = LinearLayoutManager.VERTICAL
) : BackportMaterialDividerItemDecoration(context, attributeSet, defStyleAttr, orientation) {
    @SuppressLint("RestrictedApi")
    override fun shouldDrawDivider(position: Int, adapter: RecyclerView.Adapter<*>?) =
        try {
            // Add a divider if the next item is a header (in this case a preference category
            // that corresponds to a header viewholder). This organizes the divider to separate
            // the ends of content rather than the beginning of content, alongside an added benefit
            // of preventing top headers from having a divider applied.
            (adapter as PreferenceGroupAdapter).getItem(position + 1) is PreferenceCategory
        } catch (e: ClassCastException) {
            false
        } catch (e: IndexOutOfBoundsException) {
            false
        }
}
