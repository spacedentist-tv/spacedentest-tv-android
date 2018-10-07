package tv.spacedentist.android.view

import androidx.annotation.IdRes
import org.json.JSONException
import org.json.JSONObject
import tv.spacedentist.android.R

/**
 * Some data about the buttons so we can hook up click listeners that send the key when clicked.
 */
enum class SDButton(@param:IdRes @get:IdRes val resId: Int, val key: String) {
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
    BUTTON_TRUFAX(R.id.button_trufax, "cycle");

    val message: String
        @Throws(JSONException::class)
        get() {
            val message = JSONObject()
            message.put(SDTV_MSG_KEY, SDTV_REMOTE_CONTROL_KEY)
            message.put(SDTV_KEY_KEY, key)
            return message.toString()
        }

    companion object {
        private val SDTV_MSG_KEY = "sdtv_msg"
        private val SDTV_REMOTE_CONTROL_KEY = "rc"
        private val SDTV_KEY_KEY = "key"

        fun fromResId(@IdRes resId: Int): SDButton {
            return SDButton.values().first { it.resId == resId }
        }
    }
}
