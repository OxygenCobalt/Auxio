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
 
package org.oxycblt.auxio.list

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener
import androidx.core.view.isInvisible
import com.google.android.material.appbar.MaterialToolbar
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.logD

/**
 * A wrapper around a Toolbar that enables an overlaid toolbar showing information about an item
 * selection.
 * @author OxygenCobalt
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
        }

    private var fadeThroughAnimator: ValueAnimator? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        check(childCount == 1 && getChildAt(0) is MaterialToolbar) {
            "SelectionToolbarOverlay Must have only one MaterialToolbar child"
        }

        innerToolbar = getChildAt(0) as MaterialToolbar
        addView(selectionToolbar)
    }

    fun setOnSelectionCancelListener(listener: OnClickListener) {
        selectionToolbar.setNavigationOnClickListener(listener)
    }

    fun setOnMenuItemClickListener(listener: OnMenuItemClickListener) {
        selectionToolbar.setOnMenuItemClickListener(listener)
    }

    /**
     * Update the selection amount in the selection Toolbar. This will animate the selection Toolbar
     * into focus if there is now a selection to show.
     */
    fun updateSelectionAmount(amount: Int): Boolean {
        logD("Updating selection amount to $amount")
        return if (amount > 0) {
            selectionToolbar.title = context.getString(R.string.fmt_selected, amount)
            animateToolbarVisibility(true)
        } else {
            animateToolbarVisibility(false)
        }
    }

    private fun animateToolbarVisibility(selectionVisible: Boolean): Boolean {
        // TODO: Animate nicer Material Fade transitions using animators (Normal transitions
        //  don't work due to translation)
        val targetInnerAlpha: Float
        val targetSelectionAlpha: Float
        val targetDuration: Long

        if (selectionVisible) {
            targetInnerAlpha = 0f
            targetSelectionAlpha = 1f
            targetDuration =
                context.resources.getInteger(R.integer.anim_fade_enter_duration).toLong()
        } else {
            targetInnerAlpha = 1f
            targetSelectionAlpha = 0f
            targetDuration =
                context.resources.getInteger(R.integer.anim_fade_exit_duration).toLong()
        }

        if (innerToolbar.alpha == targetInnerAlpha &&
            selectionToolbar.alpha == targetSelectionAlpha) {
            return false
        }

        if (!isLaidOut) {
            // Not laid out, just change it immediately while are not shown to the user.
            // This is an initialization, so we return false despite changing.
            changeToolbarAlpha(targetInnerAlpha)
            return false
        }

        if (fadeThroughAnimator != null) {
            fadeThroughAnimator?.cancel()
            fadeThroughAnimator = null
        }

        fadeThroughAnimator =
            ValueAnimator.ofFloat(innerToolbar.alpha, targetInnerAlpha).apply {
                duration = targetDuration
                addUpdateListener { changeToolbarAlpha(it.animatedValue as Float) }
                start()
            }

        return true
    }

    private fun changeToolbarAlpha(innerAlpha: Float) {
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
