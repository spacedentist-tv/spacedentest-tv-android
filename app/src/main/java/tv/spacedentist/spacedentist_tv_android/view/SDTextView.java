package tv.spacedentist.spacedentist_tv_android.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by coffey on 02/01/15.
 */
public class SDTextView extends TextView {
    private static final String TAG = SDTextView.class.getSimpleName();

    public SDTextView(Context context, AttributeSet attr) {
        super(context, attr);
        try {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/BebasNeue.ttf");
            setTypeface(tf);
        } catch (Exception e) {
            Log.e(TAG, "Couldn't load font", e);
        }
    }
}
