package org.oxycblt.auxio.recycler

import android.graphics.PointF
import android.view.View
import android.view.animation.Interpolator
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.BuildConfig
import kotlin.math.exp

/**
 * A custom [RecyclerView.SmoothScroller] partially copied from [androidx.recyclerview.widget.LinearSmoothScroller] that has a scroll effect similar
 * to [androidx.core.widget.NestedScrollView].
 *
 * I don't know what half of this code does but it works and looks better than the default scroller so I use it
 */
class LinearCenterScroller(target: Int) : RecyclerView.SmoothScroller() {
    private val viscousInterpolator = ViscousFluidInterpolator()
    private var targetVec: PointF? = null

    // Temporary variables to keep track of the interim scroll target. These values do not
    // point to a real item position, rather point to an estimated location pixels.
    private var interimTargetDx = 0
    private var interimTargetDy = 0

    init {
        targetPosition = target
    }

    // Not used
    override fun onStart() {}

    override fun onTargetFound(targetView: View, state: RecyclerView.State, action: Action) {
        val dx = calcDxToMakeVisible(targetView)
        val dy = calcDyToMakeVisible(targetView)

        action.update(-dx, -dy, DEFAULT_TIME, viscousInterpolator)
    }

    override fun onSeekTargetStep(dx: Int, dy: Int, state: RecyclerView.State, action: Action) {
        if (childCount == 0) {
            stop()
            return
        }

        if (BuildConfig.DEBUG && targetVec != null && ((targetVec!!.x * dx < 0 || targetVec!!.y * dy < 0))) {
            error("Scroll happened in the opposite direction of the target. Some calculations are wrong")
        }

        interimTargetDx = clampApplyScroll(interimTargetDx, dx)
        interimTargetDy = clampApplyScroll(interimTargetDy, dy)

        if (interimTargetDx == 0 && interimTargetDy == 0) {
            updateActionForInterimTarget(action)
        }
    }

    override fun onStop() {
        interimTargetDx = 0
        interimTargetDy = 0
        targetVec = null
    }

    private fun calcDxToMakeVisible(view: View): Int {
        val manager = layoutManager ?: return 0

        if (!manager.canScrollHorizontally()) return 0

        val params = view.layoutParams as RecyclerView.LayoutParams
        val top = manager.getDecoratedTop(view) - params.topMargin
        val bottom = manager.getDecoratedBottom(view) + params.bottomMargin
        val start = manager.paddingTop
        val end = manager.height - manager.paddingBottom

        return calculateDeltaToFit(top, bottom, start, end)
    }

    private fun calcDyToMakeVisible(view: View): Int {
        val manager = layoutManager ?: return 0

        if (!manager.canScrollVertically()) return 0

        val params = view.layoutParams as RecyclerView.LayoutParams
        val top = manager.getDecoratedTop(view) - params.topMargin
        val bottom = manager.getDecoratedBottom(view) + params.bottomMargin
        val start = manager.paddingTop
        val end = manager.height - manager.paddingBottom

        return calculateDeltaToFit(top, bottom, start, end)
    }

    private fun calculateDeltaToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int): Int {
        // Center the view instead of making it sit at the top or bottom.
        return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
    }

    private fun clampApplyScroll(argTmpDt: Int, dt: Int): Int {
        var tmpDt = argTmpDt
        tmpDt -= dt
        if (argTmpDt * tmpDt <= 0) { // changed sign, reached 0 or was 0, reset
            return 0
        }
        return tmpDt
    }

    private fun updateActionForInterimTarget(action: Action) {
        val scrollVector = computeScrollVectorForPosition(targetPosition)
        if (scrollVector == null || (scrollVector.x == 0.0f && scrollVector.y == 0.0f)) {
            val target = targetPosition
            action.jumpTo(target)
            stop()
            return
        }
        normalize(scrollVector)

        targetVec = scrollVector

        interimTargetDx = (TARGET_SEEK_SCROLL_DIST * scrollVector.x).toInt()
        interimTargetDy = (TARGET_SEEK_SCROLL_DIST * scrollVector.y).toInt()
        // To avoid UI hiccups, trigger a smooth scroll to a distance little further than the
        // interim target. Since we track the distance travelled in onSeekTargetStep callback, it
        // won't actually scroll more than what we need.
        action.update(
            (interimTargetDx * TARGET_SEEK_EXTRA_SCROLL_RATIO).toInt(),
            (interimTargetDy * TARGET_SEEK_EXTRA_SCROLL_RATIO).toInt(),
            DEFAULT_TIME, viscousInterpolator
        )
    }

    /**
     * A nice-looking interpolator that is similar to the [androidx.core.widget.NestedScrollView] interpolator.
     */
    private inner class ViscousFluidInterpolator : Interpolator {
        private val viscousNormalize = 1.0f / viscousFluid(1.0f)
        private val viscousOffset = 1.0f - viscousNormalize * viscousFluid(1.0f)

        fun viscousFluid(argX: Float): Float {
            var x = argX

            x *= VISCOUS_FLUID_SCALE

            if (x < 1.0f) {
                x -= (1.0f - exp(-x))
            } else {
                val start = 0.36787944117f; // 1/e == exp(-1)
                x = 1.0f - exp(1.0f - x)
                x = start + x * (1.0f - start)
            }
            return x
        }

        override fun getInterpolation(input: Float): Float {
            val interpolated = viscousNormalize * viscousFluid(input)
            if (interpolated > 0) {
                return interpolated + viscousOffset
            }
            return interpolated
        }
    }

    companion object {
        private const val VISCOUS_FLUID_SCALE = 12.0f
        private const val TARGET_SEEK_SCROLL_DIST = 10000
        private const val TARGET_SEEK_EXTRA_SCROLL_RATIO = 1.2f
        private const val DEFAULT_TIME = 500
    }
}
