package org.oxycblt.auxio.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

/** A square [FrameLayout] that clips its contents to a Material shape. */
class SquareMaskableFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private val shapeDrawable = MaterialShapeDrawable()
    private val maskPath = Path()
    private val maskRect = RectF()

    init {
        val shapeAppearanceModel =
            ShapeAppearanceModel.builder(context, attrs, defStyleAttr, 0).build()
        shapeDrawable.shapeAppearanceModel = shapeAppearanceModel
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = minOf(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        maskRect.set(0f, 0f, w.toFloat(), h.toFloat())
        shapeDrawable.setBounds(0, 0, w, h)
        maskPath.reset()
        shapeDrawable.getPathForSize(w, h, maskPath)
    }

    override fun dispatchDraw(canvas: Canvas) {
        val save = canvas.save()
        canvas.clipPath(maskPath)
        super.dispatchDraw(canvas)
        canvas.restoreToCount(save)
    }
}
