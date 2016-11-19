package tv.spacedentist.android.view;

import android.util.Log;
import android.view.View;

import org.json.JSONException;

import tv.spacedentist.android.chromecast.SDChromecastManager;

public class SDButtonClickSender implements View.OnClickListener {

    private static final String TAG = "SDButtonClickSender";

    private final SDChromecastManager mChromecastManager;

    public SDButtonClickSender(SDChromecastManager chromecastManager) {
        mChromecastManager = chromecastManager;
    }

    @Override
    public void onClick(View v) {
        try {
            SDButton button = SDButton.fromResId(v.getId());
            Log.d(TAG, "Button was clicked: " + button.getKey());
            mChromecastManager.sendChromecastMessage(button.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "Exception while sending message", e);
        }
    }
}
