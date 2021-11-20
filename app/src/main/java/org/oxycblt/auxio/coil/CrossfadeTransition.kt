package org.oxycblt.auxio.coil

import android.widget.ImageView
import coil.decode.DataSource
import coil.drawable.CrossfadeDrawable
import coil.request.ErrorResult
import coil.request.ImageResult
import coil.request.SuccessResult
import coil.size.Scale
import coil.transition.Transition
import coil.transition.TransitionTarget

/**
 * A modified variant of coil's CrossfadeTransition that actually animates error results.
 * You know. Like it used to.
 *
 * @author Coil Team
 */
class CrossfadeTransition @JvmOverloads constructor(
    private val target: TransitionTarget,
    private val result: ImageResult,
    private val durationMillis: Int = CrossfadeDrawable.DEFAULT_DURATION,
    private val preferExactIntrinsicSize: Boolean = false
) : Transition {

    init {
        require(durationMillis > 0) { "durationMillis must be > 0." }
    }

    override fun transition() {
        val drawable = CrossfadeDrawable(
            start = target.drawable,
            end = result.drawable,
            scale = (target.view as? ImageView)?.scale ?: Scale.FIT,
            durationMillis = durationMillis,
            fadeStart = !(result is SuccessResult && result.isPlaceholderCached),
            preferExactIntrinsicSize = preferExactIntrinsicSize
        )

        when (result) {
            is SuccessResult -> target.onSuccess(drawable)
            is ErrorResult -> target.onError(drawable)
        }
    }

    val ImageView.scale: Scale
        get() = when (scaleType) {
            ImageView.ScaleType.FIT_START, ImageView.ScaleType.FIT_CENTER,
            ImageView.ScaleType.FIT_END, ImageView.ScaleType.CENTER_INSIDE -> Scale.FIT
            else -> Scale.FILL
        }

    class Factory @JvmOverloads constructor(
        val durationMillis: Int = CrossfadeDrawable.DEFAULT_DURATION,
        val preferExactIntrinsicSize: Boolean = false
    ) : Transition.Factory {

        init {
            require(durationMillis > 0) { "durationMillis must be > 0." }
        }

        override fun create(target: TransitionTarget, result: ImageResult): Transition {
            // !!!!!!!!!!!!!! MODIFICATION !!!!!!!!!!!!!!
            // Remove the error check for this transition. Usually when something errors in
            // Auxio it will stay erroring, so not crossfading on an error looks weird.

            // Don't animate if the request was fulfilled by the memory cache.
            if (result is SuccessResult && result.dataSource == DataSource.MEMORY_CACHE) {
                return Transition.Factory.NONE.create(target, result)
            }

            return CrossfadeTransition(target, result, durationMillis, preferExactIntrinsicSize)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Factory &&
                durationMillis == other.durationMillis &&
                preferExactIntrinsicSize == other.preferExactIntrinsicSize
        }

        override fun hashCode(): Int {
            var result = durationMillis
            result = 31 * result + preferExactIntrinsicSize.hashCode()
            return result
        }
    }
}
