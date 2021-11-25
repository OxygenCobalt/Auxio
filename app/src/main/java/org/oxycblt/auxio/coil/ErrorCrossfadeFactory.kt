package org.oxycblt.auxio.coil

import coil.decode.DataSource
import coil.drawable.CrossfadeDrawable
import coil.request.ImageResult
import coil.request.SuccessResult
import coil.transition.CrossfadeTransition
import coil.transition.Transition
import coil.transition.TransitionTarget

/**
 * A copy of [CrossfadeTransition.Factory] that applies a transition to error results.
 * You know. Like they used to.
 * @author Coil Team
 */
class ErrorCrossfadeFactory : Transition.Factory {
    override fun create(target: TransitionTarget, result: ImageResult): Transition {
        // Don't animate if the request was fulfilled by the memory cache.
        if (result is SuccessResult && result.dataSource == DataSource.MEMORY_CACHE) {
            return Transition.Factory.NONE.create(target, result)
        }

        return CrossfadeTransition(target, result, CrossfadeDrawable.DEFAULT_DURATION, false)
    }
}
