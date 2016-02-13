package tv.spacedentist.android.view;

import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import tv.spacedentist.android.chromecast.SDChromecastManager;

public class SDButtonClickSender implements View.OnClickListener {
    private static final String TAG = SDButtonClickSender.class.getSimpleName();

    private static final String SDTV_MSG_KEY = "sdtv_msg";
    private static final String SDTV_REMOTE_CONTROL_KEY = "rc";
    private static final String SDTV_KEY_KEY = "key";

    private final SDChromecastManager mChromecastManager;

    public SDButtonClickSender(SDChromecastManager chromecastManager) {
        mChromecastManager = chromecastManager;
    }

    @Override
    public void onClick(View v) {
        try {
            SDButton button = SDButton.fromResId(v.getId());
            if (button != null) {
                Log.d(TAG, "Button was clicked: " + button.getKey());
                JSONObject message = new JSONObject();
                message.put(SDTV_MSG_KEY, SDTV_REMOTE_CONTROL_KEY);
                message.put(SDTV_KEY_KEY, button.getKey());
                mChromecastManager.sendChromecastMessage(message.toString());
            } else {
                Log.e(TAG, "Invalid button res id: " + v.getId());
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception while sending message", e);
        }
    }
}
