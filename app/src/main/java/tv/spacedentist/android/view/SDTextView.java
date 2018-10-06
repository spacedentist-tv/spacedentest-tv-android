package tv.spacedentist.android.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * A custom text view so that we can set the font
 */
public class SDTextView extends AppCompatTextView {
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
