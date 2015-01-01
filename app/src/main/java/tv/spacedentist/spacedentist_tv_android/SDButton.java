package tv.spacedentist.spacedentist_tv_android;

/**
 * Created by coffey on 01/01/15.
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

    private final int mResId;
    private final String mKey;

    SDButton(int resId, String key) {
        mResId = resId;
        mKey = key;
    }

    public int getResId() {
        return mResId;
    }

    public String getKey() {
        return mKey;
    }

    public static SDButton fromResId(int resId) {
        for (SDButton button : SDButton.values()) {
            if (button.getResId() == resId) {
                return button;
            }
        }

        return null;
    }
}
