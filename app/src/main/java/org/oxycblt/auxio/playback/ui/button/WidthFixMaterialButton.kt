package org.oxycblt.auxio.playback.ui.button

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import com.google.android.material.R
import org.oxycblt.auxio.ui.RippleFixMaterialButton

/**
 * [org.oxycblt.auxio.ui.RippleFixMaterialButton] that works around another bug where switching the icon during a press
 * breaks width expansion animations.
 *
 * @author Alexander Capehart (OxygenCobalt)
 *
 * TODO: Should animate icon transitions to make this look less bad.
 */
open class WidthFixMaterialButton
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.materialButtonStyle,
) : RippleFixMaterialButton(context, attrs, defStyleAttr) {
    private var pendingIconRes: Int? = null
    private var appliedIconRes: Int? = null

    private val applyPendingIconRunnable =
        object : Runnable {
            override fun run() {
                val iconRes = pendingIconRes ?: return
                if (isPressed) {
                    postOnAnimation(this)
                    return
                }
                if (appliedIconRes != iconRes) {
                    super@WidthFixMaterialButton.setIconResource(iconRes)
                    appliedIconRes = iconRes
                }
                pendingIconRes = null
            }
        }

    override fun setIconResource(@DrawableRes iconRes: Int) {
        super.setIconResource(iconRes)
        pendingIconRes = iconRes
        postOnAnimation(applyPendingIconRunnable)
        removeCallbacks(applyPendingIconRunnable)
        postOnAnimation(applyPendingIconRunnable)
    }

    fun clearPendingIcon() {
        removeCallbacks(applyPendingIconRunnable)
        pendingIconRes = null
        appliedIconRes = null
    }
}