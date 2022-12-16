package org.oxycblt.auxio.ui

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isInvisible
import androidx.transition.TransitionManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.transition.MaterialFadeThrough
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.logD
import kotlin.math.max
import kotlin.math.min

/**
 * A wrapper around a Toolbar that enables an overlaid toolbar showing information about
 * an item selection.
 * @author OxygenCobalt
 */
class SelectionToolbarOverlay
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr){
    private lateinit var innerToolbar: MaterialToolbar
    private val selectionToolbar = MaterialToolbar(context).apply { 
        inflateMenu(R.menu.menu_selection_actions)
        setNavigationIcon(R.drawable.ic_close_24)
    }

    private val selectionMenu = selectionToolbar.menu

    private var fadeThroughAnimator: ValueAnimator? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        check(childCount == 1 && getChildAt(0) is MaterialToolbar) {
            "SelectionToolbarOverlay Must have only one MaterialToolbar child"
        }

        innerToolbar = getChildAt(0) as MaterialToolbar
        addView(selectionToolbar)
    }

    /**
     * Add listeners for the selection toolbar.
     */
    fun registerListeners(onExit: OnClickListener,
                          onMenuItemClick: Toolbar.OnMenuItemClickListener) {
        selectionToolbar.apply {
            setNavigationOnClickListener(onExit)
            setOnMenuItemClickListener(onMenuItemClick)
        }
    }

    /**
     * Unregister listeners for this instance.
     */
    fun unregisterListeners() {
        selectionToolbar.apply {
            setNavigationOnClickListener(null)
            setOnMenuItemClickListener(null)
        }
    }

    /**
     * Update the selection amount in the selection Toolbar. This will animate the selection
     * Toolbar into focus if there is now a selection to show.
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
            targetDuration = context.resources.getInteger(R.integer.anim_fade_enter_duration).toLong()
        } else {
            targetInnerAlpha = 1f
            targetSelectionAlpha = 0f
            targetDuration = context.resources.getInteger(R.integer.anim_fade_exit_duration).toLong()
        }

        if (innerToolbar.alpha == targetInnerAlpha &&
            selectionToolbar.alpha == targetSelectionAlpha) {
            return false
        }

        if (!isLaidOut) {
            changeToolbarAlpha(targetInnerAlpha)
            return true
        }

        if (fadeThroughAnimator != null) {
            fadeThroughAnimator?.cancel()
            fadeThroughAnimator = null
        }

        fadeThroughAnimator = ValueAnimator.ofFloat(innerToolbar.alpha, targetInnerAlpha).apply {
            duration = targetDuration
            addUpdateListener {
                changeToolbarAlpha(it.animatedValue as Float)
            }
            start()
        }

        return true
    }

    private fun changeToolbarAlpha(innerAlpha: Float ) {
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