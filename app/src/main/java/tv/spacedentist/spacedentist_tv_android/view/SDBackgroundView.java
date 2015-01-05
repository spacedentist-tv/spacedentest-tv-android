package tv.spacedentist.spacedentist_tv_android.view;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by coffey on 05/01/15.
 */
public class SDBackgroundView extends ImageView {

    public SDBackgroundView(Context context) {
        super(context);
        setScaleType(ScaleType.MATRIX);
    }

    public SDBackgroundView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
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

        return super.setFrame(l, t, r, b);
    }

}
