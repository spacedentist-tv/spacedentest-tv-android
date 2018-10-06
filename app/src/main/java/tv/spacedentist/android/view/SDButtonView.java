package tv.spacedentist.android.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatButton;

/**
 * A custom Button so we can set the font
 */
public class SDButtonView extends AppCompatButton {
    private static final String TAG = SDButtonView.class.getSimpleName();

    public SDButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/BebasNeue.ttf");
            setTypeface(tf);
        } catch (Exception e) {
            Log.e(TAG, "Couldn't load font", e);
        }
    }
}
