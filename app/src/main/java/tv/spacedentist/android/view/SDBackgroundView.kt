package tv.spacedentist.android.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView

/**
 * Our own custom image scaling for the background image as Android doesn't support keeping aspect
 * ratio and cropping to the top left out of the box.
 */
class SDBackgroundView : AppCompatImageView {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        scaleType = ImageView.ScaleType.MATRIX
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        recomputeImageMatrix()
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        recomputeImageMatrix()
        return super.setFrame(l, t, r, b)
    }

    protected fun recomputeImageMatrix() {
        val matrix = imageMatrix

        val scale: Float
        val viewWidth = width - paddingLeft - paddingRight
        val viewHeight = height - paddingTop - paddingBottom
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight

        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            scale = viewHeight.toFloat() / drawableHeight.toFloat()
        } else {
            scale = viewWidth.toFloat() / drawableWidth.toFloat()
        }

        matrix.setScale(scale, scale)
        imageMatrix = matrix
    }
}
