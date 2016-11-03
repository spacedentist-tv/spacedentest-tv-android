package tv.spacedentist.android.view;

import android.support.annotation.IdRes;

import org.json.JSONException;
import org.json.JSONObject;

import tv.spacedentist.android.R;

/**
 * Some data about the buttons so we can hook up click listeners that send the key when clicked.
 */
public enum SDButton {
    BUTTON_ONE(R.id.button_one, "1"),
    BUTTON_TWO(R.id.button_two, "2"),
    BUTTON_THREE(R.id.button_three, "3"),
    BUTTON_FOUR(R.id.button_four, "4"),
    BUTTON_FIVE(R.id.button_five, "5"),
    BUTTON_SIX(R.id.button_six, "6"),
    BUTTON_SEVEN(R.id.button_seven, "7"),
    BUTTON_EIGHT(R.id.button_eight, "8"),
    BUTTON_NINE(R.id.button_nine, "9"),
    BUTTON_ZERO(R.id.button_zero, "0"),
    BUTTON_TEXT(R.id.button_text, "cycle");

    private static final String SDTV_MSG_KEY = "sdtv_msg";
    private static final String SDTV_REMOTE_CONTROL_KEY = "rc";
    private static final String SDTV_KEY_KEY = "key";

    private final int mResId;
    private final String mKey;

    SDButton(@IdRes int resId, String key) {
        mResId = resId;
        mKey = key;
    }

    public @IdRes int getResId() {
        return mResId;
    }

    public String getKey() {
        return mKey;
    }

    public static SDButton fromResId(@IdRes int resId) {
        for (SDButton button : SDButton.values()) {
            if (button.getResId() == resId) {
                return button;
            }
        }

        return null;
    }

    public String getMessage() throws JSONException {
        final JSONObject message = new JSONObject();
        message.put(SDTV_MSG_KEY, SDTV_REMOTE_CONTROL_KEY);
        message.put(SDTV_KEY_KEY, getKey());
        return message.toString();
    }
}
