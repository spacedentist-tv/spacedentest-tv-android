package tv.spacedentist.android.view;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * Our own custom image scaling for the background image as Android doesn't support keeping aspect
 * ratio and cropping to the top left out of the box.
 */
public class SDBackgroundView extends AppCompatImageView {

    public SDBackgroundView(Context context) {
        super(context);
        init();
    }

    public SDBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SDBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        recomputeImageMatrix();
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        recomputeImageMatrix();
        return super.setFrame(l, t, r, b);
    }

    protected void recomputeImageMatrix() {
        final Matrix matrix = getImageMatrix();

        float scale;
        final int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int drawableWidth = getDrawable().getIntrinsicWidth();
        final int drawableHeight = getDrawable().getIntrinsicHeight();

        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            scale = (float) viewHeight / (float) drawableHeight;
        } else {
            scale = (float) viewWidth / (float) drawableWidth;
        }

        matrix.setScale(scale, scale);
        setImageMatrix(matrix);
    }
}
