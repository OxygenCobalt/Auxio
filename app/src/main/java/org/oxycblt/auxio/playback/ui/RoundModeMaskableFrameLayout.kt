package org.oxycblt.auxio.playback.ui

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.carousel.MaskableFrameLayout
import com.google.android.material.shape.ShapeAppearanceModel
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.ui.UISettings
import javax.inject.Inject

@AndroidEntryPoint
class RoundModeMaskableFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleRes: Int = -1
) : MaskableFrameLayout(context, attrs, defStyleRes) {
    @Inject
    lateinit var uiSettings: UISettings

    init {
        // The parent FrameLayout will have already fetched/applied a rounded shape appearance
        // so in non-round mode we just force it back to sharp
        if (!uiSettings.roundMode) {
            shapeAppearanceModel = ShapeAppearanceModel.builder().build()
        }
    }
}