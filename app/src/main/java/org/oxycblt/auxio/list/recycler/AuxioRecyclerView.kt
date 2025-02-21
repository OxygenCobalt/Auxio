/*
 * Copyright (c) 2021 Auxio Project
 * AuxioRecyclerView.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list.recycler

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.WindowInsets
import androidx.annotation.AttrRes
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.util.systemBarInsetsCompat

/**
 * A [RecyclerView] with a few QoL extensions, such as:
 * - Automatic edge-to-edge support
 * - Automatic [setHasFixedSize] setup
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
open class AuxioRecyclerView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    RecyclerView(context, attrs, defStyleAttr) {
    private val initialPaddingBottom = paddingBottom
    private var savedState: Parcelable? = null

    init {
        // Prevent children from being clipped by window insets
        clipToPadding = false
        // Auxio's non-dialog RecyclerViews never change their size based on adapter contents,
        // so we can enable fixed-size optimizations.
        setHasFixedSize(true)
    }

    final override fun setHasFixedSize(hasFixedSize: Boolean) {
        // Prevent a this leak by marking setHasFixedSize as final.
        super.setHasFixedSize(hasFixedSize)
    }

    final override fun addItemDecoration(decor: ItemDecoration) {
        super.addItemDecoration(decor)
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        // Update the RecyclerView's padding such that the bottom insets are applied
        // while still preserving bottom padding.
        updatePadding(bottom = initialPaddingBottom + insets.systemBarInsetsCompat.bottom)
        if (savedState != null) {
            // State restore happens before we get insets, so there will be scroll drift unless
            // we restore the state after the insets are applied.
            // We must only do this once, otherwise we'll get jumpy behavior.
            super.onRestoreInstanceState(savedState)
            savedState = null
        }
        return insets
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        savedState = state
    }
}
