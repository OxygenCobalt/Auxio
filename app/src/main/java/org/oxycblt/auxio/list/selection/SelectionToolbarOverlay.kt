/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.list.selection

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener
import androidx.core.view.isInvisible
import com.google.android.material.appbar.MaterialToolbar
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getInteger
import org.oxycblt.auxio.util.logD

/**
 * A wrapper around a [MaterialToolbar] that adds an additional [MaterialToolbar] showing the
 * current selection state.
 * @author Alexander Capehart (OxygenCobalt)
 */
class SelectionToolbarOverlay
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    private lateinit var innerToolbar: MaterialToolbar
    private val selectionToolbar =
        MaterialToolbar(context).apply {
            setNavigationIcon(R.drawable.ic_close_24)
            inflateMenu(R.menu.menu_selection_actions)

            if (isInEditMode) {
                isInvisible = true
            }
        }
    private var fadeThroughAnimator: ValueAnimator? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        // Sanity check: Avoid incorrect views from being included in this layout.
        check(childCount == 1 && getChildAt(0) is MaterialToolbar) {
            "SelectionToolbarOverlay Must have only one MaterialToolbar child"
        }
        // The inner toolbar should be the first child.
        innerToolbar = getChildAt(0) as MaterialToolbar
        // Selection toolbar should appear on top of the inner toolbar.
        addView(selectionToolbar)
    }

    /**
     * Set an OnClickListener for when the "cancel" button in the selection [MaterialToolbar] is
     * pressed.
     * @param listener The OnClickListener to respond to this interaction.
     * @see MaterialToolbar.setNavigationOnClickListener
     */
    fun setOnSelectionCancelListener(listener: OnClickListener) {
        selectionToolbar.setNavigationOnClickListener(listener)
    }

    /**
     * Set an [OnMenuItemClickListener] for when a MenuItem is selected from the selection
     * [MaterialToolbar].
     * @param listener The [OnMenuItemClickListener] to respond to this interaction.
     * @see MaterialToolbar.setOnMenuItemClickListener
     */
    fun setOnMenuItemClickListener(listener: OnMenuItemClickListener?) {
        selectionToolbar.setOnMenuItemClickListener(listener)
    }

    /**
     * Update the selection [MaterialToolbar] to reflect the current selection amount.
     * @param amount The amount of items that are currently selected.
     * @return true if the selection [MaterialToolbar] changes, false otherwise.
     */
    fun updateSelectionAmount(amount: Int): Boolean {
        logD("Updating selection amount to $amount")
        return if (amount > 0) {
            // Only update the selected amount when it's non-zero to prevent a strange
            // title text.
            selectionToolbar.title = context.getString(R.string.fmt_selected, amount)
            animateToolbarsVisibility(true)
        } else {
            animateToolbarsVisibility(false)
        }
    }

    /**
     * Animate the visibility of the inner and selection [MaterialToolbar]s to the given state.
     * @param selectionVisible Whether the selection [MaterialToolbar] should be visible or not.
     * @return true if the toolbars have changed, false otherwise.
     */
    private fun animateToolbarsVisibility(selectionVisible: Boolean): Boolean {
        // TODO: Animate nicer Material Fade transitions using animators (Normal transitions
        //  don't work due to translation)
        // Set up the target transitions for both the inner and selection toolbars.
        val targetInnerAlpha: Float
        val targetSelectionAlpha: Float
        val targetDuration: Long

        if (selectionVisible) {
            targetInnerAlpha = 0f
            targetSelectionAlpha = 1f
            targetDuration = context.getInteger(R.integer.anim_fade_enter_duration).toLong()
        } else {
            targetInnerAlpha = 1f
            targetSelectionAlpha = 0f
            targetDuration = context.getInteger(R.integer.anim_fade_exit_duration).toLong()
        }

        if (innerToolbar.alpha == targetInnerAlpha &&
            selectionToolbar.alpha == targetSelectionAlpha) {
            // Nothing to do.
            return false
        }

        if (!isLaidOut) {
            // Not laid out, just change it immediately while are not shown to the user.
            // This is an initialization, so we return false despite changing.
            setToolbarsAlpha(targetInnerAlpha)
            return false
        }

        if (fadeThroughAnimator != null) {
            fadeThroughAnimator?.cancel()
            fadeThroughAnimator = null
        }

        fadeThroughAnimator =
            ValueAnimator.ofFloat(innerToolbar.alpha, targetInnerAlpha).apply {
                duration = targetDuration
                addUpdateListener { setToolbarsAlpha(it.animatedValue as Float) }
                start()
            }

        return true
    }

    /**
     * Update the alpha of the inner and selection [MaterialToolbar]s.
     * @param innerAlpha The opacity of the inner [MaterialToolbar]. This will map to the inverse
     * opacity of the selection [MaterialToolbar].
     */
    private fun setToolbarsAlpha(innerAlpha: Float) {
        innerToolbar.apply {
            alpha = innerAlpha
            isInvisible = innerAlpha == 0f
        }

        selectionToolbar.apply {
            alpha = 1 - innerAlpha
            isInvisible = innerAlpha == 1f
        }
    }
}
