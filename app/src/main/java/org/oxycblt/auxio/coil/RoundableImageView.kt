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

class RoundableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    init {
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.RoundableImageView)
        val cornerRadius = styledAttrs.getDimension(R.styleable.RoundableImageView_cornerRadius, 0.0f)
        styledAttrs.recycle()

        background = MaterialShapeDrawable().apply {
            setCornerSize(cornerRadius)
            fillColor = context.getColorSafe(android.R.color.transparent).stateList
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // We don't round album covers by default as it desecrates album artwork, but we do
        // provide an option if one wants it.
        // As for why we use clipToOutline instead of coils RoundedCornersTransformation, the radii
        // of an image's corners is dependent on the actual dimensions of the image, which would
        // force us to resize all images to a fixed size. clipToOutline is pretty much always
        // cheaper as long as we have a perfectly-square image.
        val settingsManager = SettingsManager.getInstance()
        clipToOutline = settingsManager.roundCovers
    }
}
