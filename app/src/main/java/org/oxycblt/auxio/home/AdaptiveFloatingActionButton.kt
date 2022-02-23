package org.oxycblt.auxio.home

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.oxycblt.auxio.util.getDimenSizeSafe
import org.oxycblt.auxio.util.logD
import com.google.android.material.R as MaterialR

/**
 * A FloatingActionButton that automatically switches to a normal or large FAB depending on the
 * screen size.
 */
@Suppress("PrivateResource")
class AdaptiveFloatingActionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = MaterialR.style.Widget_Material3_FloatingActionButton_Primary
) : FloatingActionButton(context, attrs, defStyleAttr) {

    init {
        size = SIZE_NORMAL

        // Use a large FAB on large screens, as it makes it easier to touch.
        if (resources.configuration.smallestScreenWidthDp >= 640) {
            logD("Using large FAB configuration")

            val largeFabSize = context.getDimenSizeSafe(
                MaterialR.dimen.m3_large_fab_size
            )

            val largeImageSize = context.getDimenSizeSafe(
                MaterialR.dimen.m3_large_fab_max_image_size
            )

            customSize = largeFabSize
            setMaxImageSize(largeImageSize)
        }
    }
}
