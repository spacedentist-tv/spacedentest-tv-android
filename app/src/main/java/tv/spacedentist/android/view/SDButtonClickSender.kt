package tv.spacedentist.android.view

import android.util.Log
import android.view.View

import org.json.JSONException

import tv.spacedentist.android.chromecast.SDCastManager

class SDButtonClickSender(private val mCastManager: SDCastManager) : View.OnClickListener {

    override fun onClick(v: View) {
        try {
            val button = SDButton.fromResId(v.id)
            Log.d(TAG, "Button was clicked: " + button.key)
            mCastManager.sendCastMessage(button.message)
        } catch (e: JSONException) {
            Log.e(TAG, "Exception while sending message", e)
        }

    }

    companion object {
        private const val TAG = "SDButtonClickSender"
    }
}
