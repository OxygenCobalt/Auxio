package org.oxycblt.auxio.coil

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.util.getColorSafe
import org.oxycblt.auxio.util.stateList

/**
 * An [AppCompatImageView] that applies the specified cornerRadius attribute if the user
 * has enabled the "Round album covers" option. We don't round album covers by default as
 * it desecrates album artwork, but if the user desires it we do have an option to enable it.
 */
class RoundableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    init {
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.RoundableImageView)
        val cornerRadius = styledAttrs.getDimension(R.styleable.RoundableImageView_cornerRadius, 0f)
        styledAttrs.recycle()

        background = MaterialShapeDrawable().apply {
            setCornerSize(cornerRadius)
            fillColor = context.getColorSafe(android.R.color.transparent).stateList
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // Use clipToOutline and a background drawable to crop images. While Coil's transformation
        // could theoretically be used to round corners, the corner radius is dependent on the
        // dimensions of the image, which will result in inconsistent corners across different
        // album covers unless we resize all covers to be the same size. clipToOutline is both
        // cheaper and more elegant.
        val settingsManager = SettingsManager.getInstance()
        clipToOutline = settingsManager.roundCovers
    }
}
