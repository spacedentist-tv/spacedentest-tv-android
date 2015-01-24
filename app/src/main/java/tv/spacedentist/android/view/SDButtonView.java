package tv.spacedentist.android.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

/**
 * Created by coffey on 01/01/15.
 */
public class SDButtonView extends Button {
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
