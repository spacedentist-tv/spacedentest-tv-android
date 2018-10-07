package tv.spacedentist.android.view

import android.util.Log
import android.view.View

import org.json.JSONException

import tv.spacedentist.android.chromecast.SDChromecastManager

class SDButtonClickSender(private val mChromecastManager: SDChromecastManager) : View.OnClickListener {

    override fun onClick(v: View) {
        try {
            val button = SDButton.fromResId(v.id)
            Log.d(TAG, "Button was clicked: " + button.key)
            mChromecastManager.sendChromecastMessage(button.message)
        } catch (e: JSONException) {
            Log.e(TAG, "Exception while sending message", e)
        }

    }

    companion object {
        private val TAG = "SDButtonClickSender"
    }
}
